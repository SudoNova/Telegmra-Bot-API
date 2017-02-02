package com.sunova.bot;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import com.mongodb.MongoException;
import com.sunova.botframework.BotInterface;
import com.sunova.prebuilt.Keyboards;
import com.sunova.prebuilt.Messages;
import com.sunova.prebuilt.States;
import org.bson.Document;
import org.telegram.objects.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HellScre4m on 1/24/2017.
 */
public class MemberEntity
{
	private MongoDBDriver dbDriver;
	private boolean shutDown;
	static final int memberFactor = 2;
	
	public MemberEntity (MongoDBDriver dbDriver)
	{
		this.dbDriver = dbDriver;
	}
	
	public void enterChannel (Message message, Document doc, EventProcessor processor) throws SuspendExecution, Result
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
						r.getMessage().contains("Channel members are unavailable"))
				{
					message.setText(Messages.CHANNEL_NOT_ADMIN);
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
	/*void goToNextChannel (Document user, EventProcessor processor)
			throws SuspendExecution, MongoException, Result
	{
		int userID = user.getInteger("userID");
		BotInterface botInterface = processor.getBotInterface();
		Message message = new Message().setChat(new Chat().setId(userID));
		Document newDoc = dbDriver.nextChannel(userID);
		if (newDoc != null)
		{
			ReplyKeyboardMarkup keyboard = Keyboards.CHANNEL_CONFIRM_ORDER;
			int coins = user.getInteger("coins");
			keyboard.getKeyboard()[0][0].setText(Messages.POST_VIEW_AGAIN.replace
					("{coins}", coins + ""));
			message.setText(Messages.CHANNEL_ENTER_NOTE);
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
	}*/
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
	
	void getDescription (Message message, Document doc, EventProcessor processor)
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
	
	private class Daemon extends Fiber<Void>
	{
		@Override
		protected Void run () throws SuspendExecution, InterruptedException
		{
			while (!shutDown)
			{
//				List<Document> documents = dbDriver.getPendingChannels();
			}
			return null;
		}
	}
	
}
