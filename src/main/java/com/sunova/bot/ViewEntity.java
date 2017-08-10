package com.sunova.bot;

import co.paralleluniverse.fibers.SuspendExecution;
import com.ibm.icu.util.Calendar;
import com.mongodb.MongoException;
import com.sunova.botframework.Bot;
import com.sunova.botframework.BotInterface;
import com.sunova.prebuilt.Keyboards;
import com.sunova.prebuilt.Messages;
import com.sunova.prebuilt.States;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.telegram.objects.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HellScre4m on 1/12/2017.
 */
public class ViewEntity
{
	static final int visitFactor = 2;
	private static final long botChannelID = -1001066076268L;
	private EventProcessor processor;
	private MongoDBDriver dbDriver;
	
	public ViewEntity (MongoDBDriver dbDriver, EventProcessor processor)
	{
		this.dbDriver = dbDriver;
		this.processor = processor;
	}
	
	
	public void confirmViewPost (Message message, Document user)
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
				goToNextPost(user, true);
			}
			else if (choice.equals(Messages.POST_VIEW_CONFIRMED) || choice.startsWith("/start"))
			{
				dbDriver.closeCursor(userID);
				processor.sendStateMessage(States.MAIN_MENU, user, userID);
				processor.goToState(userID, States.MAIN_MENU, null, new Document("temp", ""));
			}
		}
	}
	
	void goToNextPost (Document user, boolean refresh)
			throws SuspendExecution, MongoException, Result
	{
		int userID = user.getInteger("userID");
		BotInterface botInterface = processor.getBotInterface();
		Message message = new Message().setChat(new Chat().setId(userID));
		Document newDoc = dbDriver.nextPost(userID);
		if (newDoc != null)
		{
			
			if (refresh)
			{
				int coins = user.getInteger("coins");
				ReplyKeyboardMarkup keyboard = Keyboards.POST_CONFIRM_ORDER;
				keyboard.getKeyboard()[0][0].setText(Messages.POST_VIEW_AGAIN.replace
						("{coins}", coins + ""));
				message.setText(Messages.POST_VIEW_NOTE);
				message.setReply_markup(keyboard);
				botInterface.sendMessage(message);
			}
			Chat chat = new Chat();
			int messageID = newDoc.getInteger("messageID");
			chat.setId(botChannelID);
			message.setForward_from_chat(chat);
			message.setForward_from_message_id(messageID);
			Document order = (Document) newDoc.get("orders", List.class).get(0);
			int postReqID = order.getInteger("postReqID");
			try
			{
				botInterface.forwardMessage(message);
			}
			catch (Result result)
			{
				result.fillInStackTrace();
				result.printStackTrace();
				dbDriver.errorSendingPost(messageID);
				goToNextPost(user, false);
				return;
			}
			Document updateDoc = new Document(new Document("temp", new Document("postReqID", postReqID)
			));
			processor.goToState(userID, States.POST_CONFIRM_VIEW, updateDoc, null);
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
	
	void trackRequests (Message message, Document doc)
			throws SuspendExecution, MongoException, Result
	{
		User from = message.getFrom();
		BotInterface botInterface = processor.getBotInterface();
		if (!message.hasText())
		{
			return;
		}
		Message newMessage = new Message().setChat(new Chat().setId(from.getId()));
		String text = message.getText();
		boolean init = doc.get("temp") == null ||
				(doc.getInteger("temp") == 0 && doc.getInteger("state") == States.TRACK_CHOOSE);
		if (init)
		{
			List<Document> list = dbDriver.nextViewOrderList(from.getId(), 0);
			if (checkForZeroOrders(from, doc, botInterface, newMessage, list))
			{
				return;
			}
			int listSize = list.size();
			ReplyKeyboardMarkup keyboard = getTrackKeyboard(true, listSize);
			newMessage.setReply_markup(keyboard);
			String newText = getTrackString(list, 0);
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
			String newText = getTrackString(list, skip);
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
			String newText = getTrackString(list, skip);
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
				int messageID = order.getInteger("messageID");
				newMessage.setForward_from_chat(new Chat().setId(botChannelID));
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
					else
					{
						result.printStackTrace();
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
	private String getTrackString (List<Document> list, int skip)
	{
		int listSize = list.size();
		StringBuilder builder = new StringBuilder();
		listSize = listSize == 11 ? 10 : listSize;
		builder.append("برای دیدن پست مربوط به هر سفارش، روی دکمه مربوط به شماره آن کلیک کنید\n");
		for (int i = 0; i < listSize; i++)
		{
			Document doc = (Document) list.get(i).get("orders");
			builder.append("------------\n");
			builder.append(i + 1 + skip).append(".").append("\t\t");
			Calendar cal = (Calendar) EventProcessor.cal.clone();
			cal.setTimeInMillis(doc.getLong("startDate"));
			String date = EventProcessor.df.format(cal);
			builder.append(
					Messages.TRACK_POST_TEMPLATE.replace("{id}", doc.getInteger("postReqID") + "")
							.replace("{date}", date)
							.replace("{amount}", doc.getInteger("amount") + "")
							.replace("{viewCount}", doc.getInteger("viewCount") + "")
							.replace("{remaining}", doc.getInteger("remaining") + "")
			              );
			int remaining = doc.getInteger("remaining");
			if (remaining == 0)
			{
				cal.setTimeInMillis(doc.getLong("endDate"));
				date = EventProcessor.df.format(cal);
				builder.append("زمان پایان سفارش: ").append(date).append("\n");
			}
		}
		return builder.toString();
	}
	
	private boolean checkForZeroOrders (User from, Document doc, BotInterface botInterface,
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
		int k = 1;
		if (listSize == 11)
		{
			k++;
		}
		if (!init)
		{
			k++;
		}
		buttons[j] = new KeyboardButton[k];
		k = 0;
		if (listSize == 11)
		{
			buttons[j][k++] = new KeyboardButton().setText(Messages.PREVIOUS);
		}
		buttons[j][k] = new KeyboardButton().setText(Messages.RETURN_TO_MAIN);
		if (!init)
		{
			buttons[j][++k] = new KeyboardButton().setText(Messages.NEXT);
		}
		keyboard.setKeyboard(buttons);
		return keyboard;
	}
	
	public void getPost (Message message, User from, Document doc, Bot bot)
			throws MongoException, SuspendExecution, Result
	{
		BotInterface botInterface = bot.getInterface();
		Long refChatID = null;
		Integer refMessageID = null;
		boolean found = false;
		if (message.hasText())
		{
			String text = message.getText();
			if (text.equals(Messages.RETURN_TO_MAIN) || text.startsWith("/start"))
			{
				processor.sendStateMessage(States.MAIN_MENU, doc, from.getId());
				processor.goToState(from.getId(), States.MAIN_MENU);
				return;
			}
			else
			{
				Pattern pattern = Pattern.compile("(((http)s?(://))?((telegram|t)\\.me/))+(?<g1>[a-zA-Z]\\w{4,})" +
						                                  "/(?<g2>\\d+)/?");
				Matcher matcher = pattern.matcher(text);
				String channelName;
				if (matcher.matches())
				{
					channelName = matcher.group("g1");
					refChatID = botInterface.getChat("@" + channelName).getId();
					refMessageID = Integer.parseInt(matcher.group("g2"));
					found = true;
				}
			}
		}
		if (!found)
		{
			if (message.isForwardedFromChannel())
			{
				refChatID = message.getForward_from_chat().getId();
				refMessageID = message.getForward_from_message_id();
				found = true;
			}
			else if (message.isForwardedFromUser())
			{
				refChatID = message.getChat().getId();
				refMessageID = message.getMessage_id();
				found = true;
			}
		}
		if (found)
		{
			Document ref = dbDriver.findByRef(refChatID, refMessageID);
			int messageID;
			Chat botChannel = new Chat().setId(botChannelID);
			if (ref != null)
			{
				messageID = ref.getInteger("messageID");
			}
			else
			{
				try
				{
					Message newMessage = new Message().setChat(botChannel);
					if (message.isForwardedFromChannel() && !message.getForward_from_chat().hasUserName())
					{
						newMessage.setForward_from_chat(message.getChat())
								.setForward_from_message_id(message.getMessage_id());
					}
					else
					{
						newMessage.setForward_from_chat(new Chat().setId(refChatID))
								.setForward_from_message_id(refMessageID);
					}
					newMessage = botInterface.forwardMessage(newMessage);
					messageID = newMessage.getMessage_id();
				}
				catch (Result result)
				{
					message.setText(Messages.POST_INVALID_POST);
					botInterface.sendMessage(message);
					return;
				}
			}
			message.setForward_from_chat(botChannel);
			message.setForward_from_message_id(messageID);
			try
			{
				botInterface.forwardMessage(message);
				message.setText(Messages.POST_ENTER_AMOUNT.replace("{coins}", doc.getInteger("coins") + ""));
				message.setReply_markup(Keyboards.ENTER_INPUT);
				botInterface.sendMessage(message);
				Document newDoc = new Document(
						"$set", new Document("state", States.WAITING_FOR_AMOUNT)
						.append("previous_state", States.POST_ENTER)
						.append("temp", Arrays.asList(messageID, refChatID, refMessageID))
				);
				dbDriver.updateUser(from.getId(), newDoc);
			}
			catch (Result result)
			{
				result.printStackTrace();
			}
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
					.append("state", States.POST_CONFIRM_ORDER));
			message.setText(
					Messages.POST_CONFIRM_ORDER.replace("{amount}", list.get(0) + "").replace
							("{value}", list.get(0) * visitFactor + ""));
			message.setReply_markup(Keyboards.CONFIRM);
			botInterface.sendMessage(message);
			dbDriver.updateUser(from.getId(), newDoc);
		}
	}
	
	public void confirmOrder (User from, Document doc, BotInterface botInterface) throws SuspendExecution, Result
	{
		int userID = from.getId();
		Message message = new Message().setChat(new Chat().setId(doc.getInteger("userID")));
		ArrayList temp = (ArrayList) (doc.get("temp"));
		ArrayList order_amounts = (ArrayList) (doc.get("order_amounts"));
		doc = dbDriver.registerPost(userID, (int) temp.get(0), (long) temp.get(1), (int) temp.get(2), (int)
				order_amounts.get(0));
		message.setText(Messages.REQUEST_DONE);
		botInterface.sendMessage(message);
		processor.sendStateMessage(States.MAIN_MENU, doc, userID);
		processor.goToState(userID, States.MAIN_MENU, null, new Document("temp", "").append
				("order_amounts", "").append("previous_state", ""));
	}
}
