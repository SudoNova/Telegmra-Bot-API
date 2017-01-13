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
import org.telegram.objects.Chat;
import org.telegram.objects.Message;
import org.telegram.objects.Result;
import org.telegram.objects.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	
	public void confirmViewPost (Message message, User from, Document doc, EventProcessor processor)
			throws SuspendExecution, MongoException, Result
	{
		String choice = message.getText();
		if (choice != null)
		{
			Document temp = doc.get("temp", Document.class);
			boolean upsert = temp.getBoolean("upsert");
			long chatID = temp.getLong("chatID");
			int messageID = temp.getInteger("messageID");
			int postReqID = temp.getInteger("postReqID");
			doc = dbDriver.confirmVisit(from, chatID, messageID, postReqID, upsert);
			if (choice.contains(Messages.VIEW_AGAIN.substring(0, 14)))
			{
				processor.goToNextPost(from, doc);
			}
			else if (choice.equals(Messages.VIEW_CONFIRMED))
			{
				dbDriver.confirmVisit(from, chatID, messageID, postReqID, upsert);
				processor.sendStateMessage(States.MAIN_MENU, doc, from);
				processor.goToState(from, States.MAIN_MENU);
			}
		}
	}
	
	public void getPost (Message message, User from, Document doc, Bot bot, EventProcessor processor)
			throws MongoException, SuspendExecution, Result
	{
		BotInterface botInterface = bot.getInterface();
		Long chatID = null;
		Integer messageID = null;
		if (message.isForwardedFromChannel())
		{
			chatID = message.getForward_from_chat().getId();
			messageID = message.getForward_from_message_id();
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
		else if (message.getText().equals(Messages.RETURN_TO_MAIN))
		{
			processor.sendStateMessage(States.MAIN_MENU, doc, from);
			processor.goToState(from, States.MAIN_MENU);
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
//			if (!message.isForwarded())
//			{
			Chat forwardChat = new Chat().setId(chatID);
			Message newMessage = new Message().setForward_from_chat(forwardChat);
			newMessage.setForward_from_message_id(messageID).setChat(message.getChat());
			try
			{
				botInterface.forwardMessage(newMessage);
			}
			catch (Result result)
			{
				if (result.getError_code() == 403)
				{
					Logger.ERROR(result);
					Logger.DEBUG("Error returning post to user");
					Logger.TRACE(from);
					try
					{
						Logger.dumpAndSend(bot);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					return;
				}
				message.setText(Messages.INVALID_POST);
				botInterface.sendMessage(message);
				return;
			}
//			}
			message.setText(
					Messages.ENTER_AMOUNT_VISIT.replace("{coins}", doc.getInteger("coins") + ""));
			message.setReply_markup(Keyboards.ENTER_INPUT);
			botInterface.sendMessage(message);
			Document newDoc = new Document(
					"$set", new Document("state", States.WAITING_FOR_AMOUNT)
					.append("previous_state", States.WAITING_FOR_POST)
					.append("temp", Arrays.asList(chatID, messageID))
			);
			dbDriver.updateUser(from, newDoc);
		}
		else
		{
			botInterface.sendMessage(message.setText(Messages.INVALID_POST));
		}
	}
	
	public void getAmount (Message message, User from, ArrayList<Integer> list, int coins, BotInterface botInterface)
			throws MongoException, Result, SuspendExecution
	{
		if (list.get(0) * visitFactor > coins)
		{
			message.setText(Messages.AMOUNT_EXCEEDS);
			botInterface.sendMessage(message);
		}
		else
		{
			Document newDoc = new Document("$set", new Document("order_amounts", list)
					.append("state", States.CONFIRM_VIEW_ORDER));
			message.setText(
					Messages.CONFIRM_VIEW_ORDER.replace("{amount}", list.get(0) + "").replace
							("{value}", list.get(0) * visitFactor + ""));
			message.setReply_markup(Keyboards.CONFIRM);
			botInterface.sendMessage(message);
			dbDriver.updateUser(from, newDoc);
		}
	}
	
	public void confirmOrder (Message message, User from, Document doc, BotInterface botInterface,
	                          EventProcessor processor) throws SuspendExecution, Result
	{
		ArrayList temp = (ArrayList) (doc.get("temp"));
		ArrayList order_amounts = (ArrayList) (doc.get("order_amounts"));
		dbDriver.insertNewPostViewOrder(from, (long) temp.get(0), (int) temp.get(1), (int)
				order_amounts.get(0));
		message.setText(Messages.REQUEST_DONE);
		botInterface.sendMessage(message);
		processor.sendStateMessage(States.MAIN_MENU, doc, from);
		processor.goToState(from, States.MAIN_MENU);
	}
}
