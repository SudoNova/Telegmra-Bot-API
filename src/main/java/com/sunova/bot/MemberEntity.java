package com.sunova.bot;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import com.ibm.icu.util.Calendar;
import com.mongodb.MongoException;
import com.sunova.botframework.BotInterface;
import com.sunova.prebuilt.Keyboards;
import com.sunova.prebuilt.Messages;
import com.sunova.prebuilt.States;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.telegram.objects.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HellScre4m on 1/24/2017.
 */
public class MemberEntity
{
	private MongoDBDriver dbDriver;
	private EventProcessor processor;
	private boolean shutDown;
	static final int memberFactor = 2;
	
	public MemberEntity (MongoDBDriver dbDriver, EventProcessor processor)
	{
		this.dbDriver = dbDriver;
		this.processor = processor;
		Daemon daemon = new Daemon();
		daemon.start();
	}
	
	public void enterChannel (Message message, Document doc) throws SuspendExecution, Result
	{
		User from = message.getFrom();
		if (!message.hasText())
		{
			return;
		}
		if (message.getText().equals(Messages.RETURN_TO_MAIN) || message.getText().startsWith("/start"))
		{
			processor.sendStateMessage(States.MAIN_MENU, doc, from.getId());
			processor.goToState(from.getId(), States.MAIN_MENU);
			return;
		}
		BotInterface botInterface = processor.getBotInterface();
		String text = message.getText();
		String channelName = null;
		
		if (text.matches("(.)*(/joinchat/)(.)+"))
		{
			message.setText(Messages.CHANNEL_PRIVATE);
			botInterface.sendMessage(message);
			return;
		}
		Pattern pattern = Pattern.compile("((((http)s?(://))?((telegram|t)\\.me/))|@)+(?<group>(?!^joinchat/.*)" +
				                                  "[a-zA-Z]\\w{4,})/?");
		Matcher matcher = pattern.matcher(text);
		if (matcher.find())
		{
			channelName = matcher.group("group");
		}
		
		if (channelName != null)
		{
			try
			{
				botInterface.getChatAdministrators("@" + channelName);
			}
			catch (Result r)
			{
				if (r.getMessage().contains("bot is not a member of the channel chat") ||
						r.getMessage().contains("channel members are unavailable"))
				{
					message.setText(Messages.CHANNEL_NOT_ADMIN);
					botInterface.sendMessage(message);
				}
				else if (r.getMessage().contains("chat not found"))
				{
					message.setText(Messages.CHANNEL_NOT_EXIST);
					botInterface.sendMessage(message);
				}
				else
				{
					r.printStackTrace();
					message.setText(Messages.CHANNEL_INVALID_LINK);
					botInterface.sendMessage(message);
				}
				return;
			}
			message.setText(Messages.CHANNEL_ENTER_AMOUNT);
			message.setReply_markup(Keyboards.ENTER_INPUT);
			botInterface.sendMessage(message);
			processor.goToState(from.getId(), States.WAITING_FOR_AMOUNT,
			                    new Document("temp", channelName).append("previous_state", States.CHANNEL_ENTER), null
			                   );
		}
		else
		{
			message.setText(Messages.CHANNEL_INVALID_LINK);
			botInterface.sendMessage(message);
		}
	}
	
	void goToNextChannel (Document user)
			throws SuspendExecution, MongoException, Result
	{
		int userID = user.getInteger("userID");
		BotInterface botInterface = processor.getBotInterface();
		Message message = new Message().setChat(new Chat().setId(userID));
		Document newDoc = dbDriver.nextChannel(userID);
		if (newDoc != null)
		{
			Document order = (Document) newDoc.get(
					"orders", List.class).get(0);
			int channelReqID = order.getInteger("channelReqID");
			long chatID = newDoc.getLong("chatID");
			
			String description = order.getString("description");
			int days = order.getInteger("days");
			String userName = newDoc.getString("userName");
			Chat chat;
			try
			{
				chat = botInterface.getChat(chatID);
			}
			catch (Result r)
			{
				try
				{
					chat = botInterface.getChat("@" + userName);
				}
				catch (Result result)
				{
					chat = null;
				}
			}
			if (chat == null || !chat.hasUserName())
			{
				dbDriver.errorResolvingChannel(chatID);
				goToNextChannel(user);
				return;
			}
			String temp = chat.getUsername();
			if (!temp.equals(newDoc.getString("userName")))
			{
				dbDriver.updateChannel(chatID, new Document("$set", new Document("userName", temp)
						.append("errorCount", 0).append("warned", false)));
			}
			try
			{
				ChatMember member = botInterface.getChatMember(chatID, userID);
				String status = member.getStatus();
				if (status.equals("member") || status.equals("creator") || status.equals("administrator"))
				{
					goToNextChannel(user);
					return;
				}
			}
			catch (Result r)
			{
				
			}
			String text = Messages.CHANNEL_TEMPLATE
					.replace("{days}", days + "").replace("{description}", description)
					.replace("{name}", chat.getTitle()).replace("{username}", "@" + chat.getUsername());
			ReplyKeyboardMarkup keyboard = Keyboards.CHANNEL_CONFIRM_JOIN;
			message.setText(text).setReply_markup(keyboard);
			botInterface.sendMessage(message);
			Document updateDoc = new Document(
					new Document("temp",
					             new Document("channelReqID", channelReqID).append("chatID", chatID)
					));
			processor.goToState(userID, States.CHANNEL_CONFIRM_JOIN, updateDoc, null);
		}
		else
		{
			message.setText(Messages.CHANNEL_NO_CHANNEL);
			botInterface.sendMessage(message);
			dbDriver.closeCursor(userID);
			processor.sendStateMessage(States.MAIN_MENU, user, userID);
			processor.goToState(userID, States.MAIN_MENU, null, new Document("temp", ""));
		}
	}
	
	public void confirmJoin (Message message, Document doc)
			throws SuspendExecution, MongoException, Result
	{
		BotInterface botInterface = processor.getBotInterface();
		if (!message.hasText())
		{
			return;
		}
		String text = message.getText();
		Document temp = doc.get("temp", Document.class);
		User from = message.getFrom();
		int userID = from.getId();
		if (text.equals(Messages.CHANNEL_JOIN))
		{
			boolean isJoined = true;
			try
			{
				ChatMember member = botInterface.getChatMember(temp.getLong("chatID"), userID);
				String status = member.getStatus();
				if (status.equals("left") || status.equals("kicked"))
				{
					isJoined = false;
				}
			}
			catch (Result r)
			{
				
			}
			if (isJoined)
			{
				dbDriver.confirmJoin(userID, temp.getInteger("channelReqID"));
				message.setText(Messages.CHANNEL_JOIN_CONFIRMED);
				botInterface.sendMessage(message);
				goToNextChannel(doc);
//				processor.sendStateMessage(States.MAIN_MENU, doc, userID);
//				processor.goToState(userID, States.MAIN_MENU);
			}
			else
			{
				message.setText(Messages.CHANNEL_JOIN_DENIED);
				botInterface.sendMessage(message);
			}
//			dbDriver.
		}
		else if (text.equals(Messages.CHANNEL_NEXT))
		{
			goToNextChannel(doc);
		}
		else if (text.equals(Messages.RETURN_TO_MAIN) || text.startsWith("/start"))
		{
			dbDriver.closeCursor(userID);
			processor.sendStateMessage(States.MAIN_MENU, doc, userID);
			processor.goToState(userID, States.MAIN_MENU, null, new Document("temp", ""));
		}
//		else
//		{
//			message.setText("")
//		}
	}
	
	public void getAmount (User from, ArrayList<Integer> list, int coins, BotInterface botInterface)
			throws MongoException, Result, SuspendExecution
	{
		Message message = new Message().setChat(new Chat().setId(from.getId()));
		if (list.size() < 2)
		{
			message.setText(Messages.AMOUNT_ONE);
			botInterface.sendMessage(message);
			
		}
		else if (list.get(0) * list.get(1) * memberFactor > coins)
		{
			message.setText(Messages.AMOUNT_EXCEEDS);
			botInterface.sendMessage(message);
		}
		else if (list.get(0) * list.get(1) < 1)
		{
			message.setText(Messages.AMOUNT_ZERO);
			botInterface.sendMessage(message);
		}
		else
		{
			Document newDoc = new Document("$set", new Document("order_amounts", list)
					.append("state", States.CHANNEL_DESCRIBE))
					.append("$unset", new Document("previous_state", ""));
			message.setText(Messages.CHANNEL_ENTER_DESCRIPTION
					                .replace("{days}", list.get(0) + "").replace("{persons}", list.get(1) + "")
					                .replace("{amount}", list.get(0) * list.get(1) * memberFactor + ""));
			message.setReply_markup(Keyboards.ENTER_INPUT);
			botInterface.sendMessage(message);
			dbDriver.updateUser(from.getId(), newDoc);
		}
	}
	
	void getDescription (Message message, Document doc)
			throws SuspendExecution, Result, MongoException
	{
		User from = message.getFrom();
		BotInterface botInterface = processor.getBotInterface();
		if (!message.hasText())
		{
			return;
		}
		String text = message.getText();
		if (message.getText().equals(Messages.RETURN_TO_MAIN) || message.getText().startsWith("/start"))
		{
			processor.sendStateMessage(States.MAIN_MENU, doc, from.getId());
			processor.goToState(from.getId(), States.MAIN_MENU, null, new Document("order_amounts", "")
					.append("temp", ""));
			return;
		}
		if (text.length() > 250)
		{
			message.setText(Messages.LENGTH_EXCEEDED);
			botInterface.sendMessage(message);
			return;
		}
		List<Integer> orderAmounts = doc.get("order_amounts", List.class);
		String channelName = doc.getString("temp");
		try
		{
			long channelID = botInterface.getChat("@" + channelName).getId();
			doc = dbDriver.registerChannel(orderAmounts.get(0), orderAmounts.get(1), channelName, channelID, text,
			                               from.getId()
			                              );
			message.setText(Messages.REQUEST_DONE);
			botInterface.sendMessage(message);
		}
		catch (Result r)
		{
			if (r.getMessage().contains("bot is not a member of the channel chat") ||
					r.getMessage().contains("Channel members are unavailable"))
			{
				message.setText(Messages.CHANNEL_NOT_ADMIN);
				botInterface.sendMessage(message);
			}
			else
			{
				r.printStackTrace();
			}
		}
		processor.sendStateMessage(States.MAIN_MENU, doc, from.getId());
		processor.goToState(from.getId(), States.MAIN_MENU, null, new Document("order_amounts", "")
				.append("temp", ""));
	}
	
	void trackJoins (Message message)
			throws SuspendExecution, MongoException, Result
	{
		int userID = message.getFrom().getId();
		List<Document> list = dbDriver.getUserJoinedChannels(userID);
		StringBuilder builder = new StringBuilder("لیست کانال‌هایی که به واسطه ربات در آن‌ها عضو هستید:\n");
		for (Document i : list)
		{
			builder.append("@" + i.getString("userName") + "\n");
		}
		message.setText(builder.toString());
		processor.getBotInterface().sendMessage(message);
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
			List<Document> list = dbDriver.nextChannelOrderList(from.getId(), 0);
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
			List<Document> list = dbDriver.nextChannelOrderList(from.getId(), skip);
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
			List<Document> list = dbDriver.nextChannelOrderList(from.getId(), skip);
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
	}
	
	@NotNull
	private String getTrackString (List<Document> list, int skip)
	{
		int listSize = list.size();
		StringBuilder builder = new StringBuilder();
		listSize = listSize == 11 ? 10 : listSize;
		for (int i = 0; i < listSize; i++)
		{
			Document doc = (Document) list.get(i).get("orders");
			builder.append("------------\n");
			builder.append(i + 1 + skip).append(".").append("\t\t");
			Calendar cal = (Calendar) EventProcessor.cal.clone();
			cal.setTimeInMillis(doc.getLong("startDate"));
			String date = EventProcessor.df.format(cal);
			builder.append(
					Messages.TRACK_CHANNEL_TEMPLATE.replace("{id}", doc.getInteger("channelReqID") + "")
							.replace("{userName}", list.get(i).getString("userName"))
							.replace("{date}", date)
							.replace("{persons}", doc.getInteger("persons") + "")
							.replace("{days}", doc.getInteger("days") + "")
							.replace("{entered}", doc.getInteger("entered") + "")
							.replace("{left}", doc.getInteger("left") + "")
							.replace("{returned}", doc.getInteger("returned") + "")
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
		if (list == null || list.isEmpty())
		{
			newMessage.setText(Messages.TRACK_NO_ORDERS);
			try
			{
				botInterface.sendMessage(newMessage);
			}
			catch (Result result)
			{
//				Logger.ERROR(result);
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
		KeyboardButton[][] buttons = new KeyboardButton[1][];
		int k = 1;
		if (listSize == 11)
		{
			k++;
		}
		if (!init)
		{
			k++;
		}
		buttons[0] = new KeyboardButton[k];
		k = 0;
		if (listSize == 11)
		{
			buttons[0][k++] = new KeyboardButton().setText(Messages.PREVIOUS);
		}
		buttons[0][k] = new KeyboardButton().setText(Messages.RETURN_TO_MAIN);
		if (!init)
		{
			buttons[0][++k] = new KeyboardButton().setText(Messages.NEXT);
		}
		keyboard.setKeyboard(buttons);
		return keyboard;
	}
	
	class Daemon extends Fiber<Void>
	{
		@Override
		protected Void run () throws SuspendExecution, InterruptedException
		{
			BotInterface botInterface;
			do
			{
				botInterface = processor.getBotInterface();
				sleep(1000);
			}
			while (botInterface == null);
			long limit = TimeUnit.MINUTES.toMillis(5);
			while (!shutDown)
			{
				long start = System.currentTimeMillis();
				List<Document> documents = dbDriver.getPendingChannelJoins();
				for (Document i : documents)
				{
					long chatID = i.getLong("chatID");
					Document joins = i.get("joins", Document.class);
					int userID = joins.getInteger("userID");
					int channelReqID = joins.getInteger("channelReqID");
					boolean left = false;
					boolean warned = i.getBoolean("warned");
					try
					{
						ChatMember member = botInterface.getChatMember(chatID, userID);
						String status = member.getStatus();
						if (status.equals("left"))
						{
							left = true;
						}
						if (warned)
						{
							dbDriver.warnChannelOwner(channelReqID, false);
						}
					}
					catch (Result r)
					{
						if (!warned)
						{
							int ownerID = dbDriver.warnChannelOwner(channelReqID, true);
							Message message = new Message().setChat(new Chat().setId(ownerID));
							message.setText(Messages.CHANNEL_ADMIN_REVOKED.replace("{name}", i.getString
									("userName")));
							try
							{
								botInterface.sendMessage(message);
							}
							catch (Result result)
							{
								result.printStackTrace();
							}
						}
						
					}
					if (left)
					{
						warned = joins.getBoolean("warned");
						long lastWarn = joins.getLong("lastWarn");
						if (warned)
						{
							if (System.currentTimeMillis() - lastWarn > TimeUnit.MINUTES.toMillis(20))
							{
								dbDriver.userLeftChannel(userID, channelReqID);
							}
						}
						else
						{
							Message message = new Message().setChat(new Chat().setId(userID));
							message.setText(Messages.CHANNEL_USER_LEFT_CHANNEL.replace("{name}", i.getString("userName")
							                                                          ));
							try
							{
								botInterface.sendMessage(message);
							}
							catch (Result result)
							{
								result.printStackTrace();
							}
							dbDriver.warnJoinedUser(userID, channelReqID);
						}
					}
					else
					{
						long lastAckDate = joins.getLong("lastAckDate");
						long currentTime = System.currentTimeMillis();
						if (currentTime - lastAckDate >= TimeUnit.DAYS.toMillis(1))
						{
							dbDriver.ackJoin(userID, channelReqID, joins.getLong("lastAckDate"));
						}
					}
				}
				long diff = System.currentTimeMillis() - start;
				if (diff < limit)
				{
					sleep(limit - diff);
				}
				System.out.println(new Date() + " :Course done");
			}
			return null;
		}
	}
	
}
