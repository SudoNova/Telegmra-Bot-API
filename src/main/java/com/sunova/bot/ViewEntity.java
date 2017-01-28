package com.sunova.bot;

import co.paralleluniverse.fibers.SuspendExecution;
import com.mongodb.MongoException;
import com.sunova.botframework.Bot;
import com.sunova.botframework.BotInterface;
import com.sunova.botframework.Logger;
import com.sunova.prebuilt.Keyboards;
import com.sunova.prebuilt.Messages;
import com.sunova.prebuilt.States;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.telegram.objects.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HellScre4m on 1/12/2017.
 */
public class ViewEntity
{
	static final int visitFactor = 2;
	private static final long botChannel = -1001066076268L;
	private MongoDBDriver dbDriver;
	
	public ViewEntity (MongoDBDriver dbDriver)
	{
		this.dbDriver = dbDriver;
	}
	
	
	public void confirmViewPost (Message message, Document user, EventProcessor processor)
			throws SuspendExecution, MongoException, Result
	{
		int userID = user.getInteger("userID");
		String choice = message.getText();
		if (choice != null)
		{
			Document temp = user.get("temp", Document.class);
			int postReqID = temp.getInteger("postReqID");
			user = dbDriver.confirmVisit(userID, postReqID);
			if (choice.contains(Messages.POST_VIEW_AGAIN.substring(0, 14)))
			{
				goToNextPost(user, processor);
			}
			else if (choice.equals(Messages.POST_VIEW_CONFIRMED) || choice.startsWith("/start"))
			{
				dbDriver.closeCursor(userID);
				processor.sendStateMessage(States.MAIN_MENU, user, userID);
				processor.goToState(userID, States.MAIN_MENU, null, new Document("temp", ""));
			}
		}
	}
	
	void goToNextPost (Document user, EventProcessor processor)
			throws SuspendExecution, MongoException, Result
	{
		int userID = user.getInteger("userID");
		BotInterface botInterface = processor.getBotInterface();
		Message message = new Message().setChat(new Chat().setId(userID));
		Document newDoc = dbDriver.nextPost(userID);
		if (newDoc != null)
		{
			ReplyKeyboardMarkup keyboard = Keyboards.POST_CONFIRM_ORDER;
			int coins = user.getInteger("coins");
			keyboard.getKeyboard()[0][0].setText(Messages.POST_VIEW_AGAIN.replace
					("{coins}", coins + ""));
			message.setText(Messages.POST_VIEW_NOTE);
			message.setReply_markup(keyboard);
			botInterface.sendMessage(message);
			Chat chat = new Chat();
			long chatID = newDoc.getLong("chatID");
			int messageID = newDoc.getInteger("messageID");
			chat.setId(chatID);
			message.setForward_from_chat(chat);
			message.setForward_from_message_id(messageID);
			Document orders = (Document) newDoc.get(
					"orders", List.class).get(0);
			int postReqID = orders.getInteger("postReqID");
			try
			{
				botInterface.forwardMessage(message);
			}
			catch (Result result)
			{
				result.fillInStackTrace();
				result.printStackTrace();
				dbDriver.errorSendingPost(chatID, messageID);
				goToNextPost(user, processor);
				return;
			}
			Document updateDoc = new Document(new Document("temp", new Document("postReqID", postReqID)
			));
			processor.goToState(userID, States.CONFIRM_VIEW_POST, updateDoc, null);
		}
		else
		{
			message.setText(Messages.POST_NO_POSTS);
			botInterface.sendMessage(message);
			dbDriver.closeCursor(userID);
			processor.sendStateMessage(States.MAIN_MENU, user, userID);
			processor.goToState(userID, States.MAIN_MENU, null, new Document("temp", ""));
		}
	}
	
	void trackRequests (User from, Message message, Document doc, BotInterface botInterface, EventProcessor processor,
	                    boolean init) throws SuspendExecution, MongoException, Result
	{
		if (!message.hasText())
		{
			return;
		}
		Message newMessage = new Message().setChat(new Chat().setId(from.getId()));
		String text = message.getText();
		init = init && (doc.get("temp") == null || doc.getInteger("temp") == 0);
		if (init)
		{
			List<Document> list = dbDriver.nextViewOrderList(from.getId(), 0);
			if (checkForZeroOrders(from, doc, botInterface, processor, newMessage, list))
			{
				return;
			}
			int listSize = list.size();
			ReplyKeyboardMarkup keyboard = getTrackKeyboard(true, listSize);
			newMessage.setReply_markup(keyboard);
			String newText = getTrackString(list);
			newMessage.setText(newText);
			botInterface.sendMessage(newMessage);
			processor.goToState(from.getId(), States.TRACK_POSTS, new Document("temp", 0), null);
		}
		else if (text.equals(Messages.NEXT))
		{
			int skip = doc.getInteger("temp") - 10;
			if (skip < 0)
			{
				return;
			}
			List<Document> list = dbDriver.nextViewOrderList(from.getId(), skip);
			int listSize = list.size();
			ReplyKeyboardMarkup keyboard = getTrackKeyboard(skip == 0, listSize);
			newMessage.setReply_markup(keyboard);
			String newText = getTrackString(list);
			newMessage.setText(newText);
			botInterface.sendMessage(newMessage);
			dbDriver.updateUser(from.getId(), new Document("$set", new Document("temp", skip)));
		}
		else if (text.equals(Messages.PREVIOUS))
		{
			int skip = doc.getInteger("temp") + 10;
			List<Document> list = dbDriver.nextViewOrderList(from.getId(), skip);
			if (list == null || list.isEmpty())
			{
				return;
			}
			int listSize = list.size();
			ReplyKeyboardMarkup keyboard = getTrackKeyboard(false, listSize);
			newMessage.setReply_markup(keyboard);
			String newText = getTrackString(list);
			newMessage.setText(newText);
			botInterface.sendMessage(newMessage);
			dbDriver.updateUser(from.getId(), new Document("$set", new Document("temp", skip)));
		}
		else if (text.equals(Messages.RETURN_TO_MAIN) || text.startsWith("/start"))
		{
			processor.sendStateMessage(States.MAIN_MENU, doc, from.getId());
			processor.goToState(from.getId(), States.MAIN_MENU, null, new Document("temp", ""));
		}
		else
		{
			try
			{
				int choice = Integer.parseInt(text);
				if (choice < 0 || choice > 10)
				{
					return;
				}
				int skip = doc.getInteger("temp");
				List<Document> list = dbDriver.nextViewOrderList(from.getId(), skip);
				if (list == null || list.size() < choice)
				{
					return;
				}
				Document order = list.get(choice - 1);
				long chatID = order.getLong("chatID");
				int messageID = order.getInteger("messageID");
				newMessage.setForward_from_chat(new Chat().setId(chatID));
				newMessage.setForward_from_message_id(messageID);
				try
				{
					botInterface.forwardMessage(newMessage);
				}
				catch (Result result)
				{
					if (result.getError_code() == 400)
					{
						newMessage.setText(Messages.POST_EXISTS_NO_MORE);
						botInterface.sendMessage(newMessage);
					}
				}
			}
			catch (NumberFormatException e)
			{
				//Do nothing
			}
		}
	}
	
	@NotNull
	private String getTrackString (List<Document> list)
	{
		int listSize = list.size();
		StringBuilder builder = new StringBuilder();
		listSize = listSize == 11 ? 10 : listSize;
		builder.append("برای دیدن پست مربوط به هر سفارش، روی دکمه مربوط به شماره آن کلیک کنید");
		for (int i = 0; i < listSize; i++)
		{
			Document doc = (Document) list.get(i).get("orders");
			builder.append("\n-------\n");
			builder.append(i + 1).append(".").append("\n");
			builder.append(
					Messages.TRACK_POST_TEMPLATE.replace("{id}", doc.getInteger("postReqID") + "")
							.replace("{date}", new Date(doc.getLong("startDate")).toString())
							.replace("{amount}", doc.getInteger("amount") + "")
							.replace("{viewCount}", doc.getInteger("viewCount") + "")
							.replace("{remaining}", doc.getInteger("remaining") + "")
			              );
			int remaining = doc.getInteger("remaining");
			if (remaining == 0)
			{
				builder.append("زمان پایان سفارش : ").append(new Date(doc.getLong("endDate")).toString());
			}
		}
		return builder.toString();
	}
	
	private boolean checkForZeroOrders (User from, Document doc, BotInterface botInterface, EventProcessor processor,
	                                    Message newMessage, List<Document> list) throws SuspendExecution, Result
	{
		if (list == null || list.size() == 0)
		{
			newMessage.setText(Messages.TRACK_NO_ORDERS);
			try
			{
				botInterface.sendMessage(newMessage);
			}
			catch (Result result)
			{
				Logger.ERROR(result);
			}
			processor.sendStateMessage(States.MAIN_MENU, doc, from.getId());
			processor.goToState(from.getId(), States.MAIN_MENU);
			return true;
		}
		return false;
	}
	
	private ReplyKeyboardMarkup getTrackKeyboard (boolean init, int listSize)
	{
		ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup().setResize_keyboard(true);
		int keyboardSize = listSize / 6 + 2;
		if (listSize == 11)
		{
			keyboardSize++;
		}
		if (!init)
		{
			keyboardSize++;
		}
		KeyboardButton[][] buttons = new KeyboardButton[keyboardSize][];
		int j = 0;
		buttons[j] = new KeyboardButton[listSize >= 5 ? 5 : listSize];
		for (int i = 0; i < buttons[j].length; i++)
		{
			buttons[j][i] = new KeyboardButton().setText((i + 1) + "");
		}
		j++;
		if (listSize > 5)
		{
			buttons[j] = new KeyboardButton[listSize >= 10 ? 5 : listSize % 5];
			for (int i = 0; i < buttons[j].length; i++)
			{
				buttons[j][i] = new KeyboardButton().setText((i + 6) + "");
			}
			j++;
		}
		if (listSize == 11)
		{
			buttons[j] = new KeyboardButton[1];
			buttons[j++][0] = new KeyboardButton().setText(Messages.PREVIOUS);
		}
		if (!init)
		{
			buttons[j] = new KeyboardButton[1];
			buttons[j++][0] = new KeyboardButton().setText(Messages.NEXT);
		}
		buttons[j] = new KeyboardButton[1];
		buttons[j][0] = new KeyboardButton().setText(Messages.RETURN_TO_MAIN);
		keyboard.setKeyboard(buttons);
		return keyboard;
	}
	
	public void getPost (Message message, User from, Document doc, Bot bot, EventProcessor processor)
			throws MongoException, SuspendExecution, Result
	{
		BotInterface botInterface = bot.getInterface();
		Long chatID = null;
		Integer messageID = null;
		if (message.isForwardedFromChannel())
		{
			if (message.getForward_from_chat().hasUserName())
			{
				chatID = message.getForward_from_chat().getId();
				messageID = message.getForward_from_message_id();
			}
			else
			{
				chatID = message.getChat().getId();
				messageID = message.getMessage_id();
			}
		}
		else if (message.isForwardedFromUser())
		{
			chatID = message.getChat().getId();
			messageID = message.getMessage_id();
			Message newMessage = new Message().setChat(new Chat().setId(botChannel))
					.setForward_from_chat(new Chat().setId(chatID)).setForward_from_message_id(messageID);
			try
			{
				newMessage = botInterface.forwardMessage(newMessage);
				chatID = newMessage.getChat().getId();
				messageID = newMessage.getMessage_id();
			}
			catch (Result result)
			{
				if (result.getError_code() == 403)
				{
					Logger.ERROR(result);
					Logger.DEBUG("Error sending user message to bot channel");
					Logger.TRACE(from);
					try
					{
						Logger.dumpAndSend(bot);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				return;
			}
		}
		else if (!message.hasText())
		{
			return;
		}
		else if (message.getText().equals(Messages.RETURN_TO_MAIN) || message.getText().startsWith("/start"))
		{
			processor.sendStateMessage(States.MAIN_MENU, doc, from.getId());
			processor.goToState(from.getId(), States.MAIN_MENU);
			return;
		}
		else
		{
			Pattern pattern = Pattern.compile("/\\w+/");
			String messageBody = message.getText();
			Matcher matcher = pattern.matcher(messageBody);
			String channelName;
			if (matcher.find())
			{
				channelName = matcher.group();
				channelName = channelName.substring(1, channelName.length() - 1);
				chatID = botInterface.getChat("@" + channelName).getId();
				pattern = Pattern.compile("/\\d+");
				matcher = pattern.matcher(messageBody);
				if (matcher.find())
				{
					messageID = Integer.parseInt(matcher.group().substring(1));
				}
			}
		}
		if (!(chatID == null || messageID == null))
		{
			Chat forwardChat = new Chat().setId(chatID);
			Message newMessage = new Message().setForward_from_chat(forwardChat);
			newMessage.setForward_from_message_id(messageID).setChat(message.getChat());
			try
			{
				botInterface.forwardMessage(newMessage);
			}
			catch (Result result)
			{
//				if (result.getError_code() == 403)
//				{
//					message.setText("متاسفانه کانال‌های پرایویت پشتیبانی نمی‌شوند.");
//					botInterface.sendMessage(message);
//					return;
//				}
				message.setText(Messages.POST_INVALID_POST);
				botInterface.sendMessage(message);
				return;
			}
//			}
			message.setText(
					Messages.POST_ENTER_AMOUNT.replace("{coins}", doc.getInteger("coins") + ""));
			message.setReply_markup(Keyboards.ENTER_INPUT);
			botInterface.sendMessage(message);
			Document newDoc = new Document(
					"$set", new Document("state", States.WAITING_FOR_AMOUNT)
					.append("previous_state", States.POST_ENTER)
					.append("temp", Arrays.asList(chatID, messageID))
			);
			dbDriver.updateUser(from.getId(), newDoc);
		}
		else
		{
			botInterface.sendMessage(message.setText(Messages.POST_INVALID_POST));
		}
	}
	
	public void getAmount (User from, ArrayList<Integer> list, int coins, BotInterface botInterface)
			throws MongoException, Result, SuspendExecution
	{
		Message message = new Message().setChat(new Chat().setId(from.getId()));
		if (list.get(0) * visitFactor > coins)
		{
			message.setText(Messages.AMOUNT_EXCEEDS);
			botInterface.sendMessage(message);
		}
		else if (list.get(0) < 1)
		{
			message.setText(Messages.AMOUNT_ZERO);
			botInterface.sendMessage(message);
		}
		else
		{
			Document newDoc = new Document("$set", new Document("order_amounts", list)
					.append("state", States.CONFIRM_VIEW_ORDER));
			message.setText(
					Messages.POST_CONFIRM_ORDER.replace("{amount}", list.get(0) + "").replace
							("{value}", list.get(0) * visitFactor + ""));
			message.setReply_markup(Keyboards.CONFIRM);
			botInterface.sendMessage(message);
			dbDriver.updateUser(from.getId(), newDoc);
		}
	}
	
	public void confirmOrder (User from, Document doc, BotInterface botInterface,
	                          EventProcessor processor) throws SuspendExecution, Result
	{
		int userID = from.getId();
		Message message = new Message().setChat(new Chat().setId(doc.getInteger("userID")));
		ArrayList temp = (ArrayList) (doc.get("temp"));
		ArrayList order_amounts = (ArrayList) (doc.get("order_amounts"));
		doc = dbDriver.insertNewPostViewOrder(userID, (long) temp.get(0), (int) temp.get(1), (int)
				order_amounts.get(0));
		message.setText(Messages.REQUEST_DONE);
		botInterface.sendMessage(message);
		processor.sendStateMessage(States.MAIN_MENU, doc, userID);
		processor.goToState(userID, States.MAIN_MENU, null, new Document("temp", "").append
				("order_amounts", "").append("previous_state", ""));
	}
}
