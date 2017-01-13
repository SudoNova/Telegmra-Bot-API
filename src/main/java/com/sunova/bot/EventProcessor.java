package com.sunova.bot;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import com.mongodb.MongoException;
import com.sunova.botframework.Logger;
import com.sunova.botframework.UserInterface;
import com.sunova.prebuilt.Keyboards;
import com.sunova.prebuilt.Messages;
import com.sunova.prebuilt.States;
import org.bson.Document;
import org.telegram.objects.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by HellScre4m on 5/9/2016.
 */
public class EventProcessor extends UserInterface
{
	static final int referralReward = 20;
	private MongoDBDriver dbDriver;
	private ViewEntity viewEntity;
	
	public EventProcessor ()
	{
		dbDriver = new MongoDBDriver();
		viewEntity = new ViewEntity(dbDriver);
	}
	
	public void onMessage (Message message) throws SuspendExecution
	{
		User from = message.getFrom();
		try
		{
			Document doc = getUser(message, from);
			switch (doc.getInteger("state"))
			{
				case States.START:
					welCome(message, from);
					break;
				case States.WAITING_FOR_PHONE_NUMBER:
					getPhoneNumber(message, from, doc);
					break;
				case States.MAIN_MENU:
					processMainMenu(message, from, doc);
					break;
				case States.CONFIRM_VIEW_POST:
					viewEntity.confirmViewPost(message, from, doc, this);
					break;
				case States.WAITING_FOR_POST:
					viewEntity.getPost(message, from, doc, bot, this);
					break;
				case States.WAITING_FOR_AMOUNT:
					getAmount(message, from, doc);
					break;
				case States.CONFIRM_VIEW_ORDER:
					confirmOrder(message, from, doc);
					break;
//				default:
//					if (message.hasText() && message.getText().contains("/start"))
//					{
//						sendStateMessage(States.MAIN_MENU, doc, from);
//						goToState(from, States.MAIN_MENU);
//					}
			}
		}
		catch (Result result)
		{
			Logger.ERROR(result);
			Logger.DEBUG(Arrays.toString(Strand.currentStrand().getStackTrace()));
			Logger.TRACE(from, message);
			try
			{
				Logger.dumpAndSend(bot);
			}
			catch (IOException | Result e)
			{
				e.printStackTrace();
			}
		}
		catch (Throwable throwable)
		{
			throwable.printStackTrace();
		}
	}
	
	public Document getUser (Message message, User from) throws SuspendExecution, MongoException
	{
		Document doc = dbDriver.getUser(from.getId());
		if (doc == null)
		{
			String text = message.getText();
			if (message.hasText() && text.startsWith("/start"))
			{
				Scanner scanner = new Scanner(text);
				scanner.next();
				if (scanner.hasNextInt())
				{
					int referrerID = scanner.nextInt();
					doc = dbDriver.insertUser(from, referrerID);
				}
				else
				{
					doc = dbDriver.insertUser(from);
				}
			}
			else
			{
				doc = dbDriver.insertUser(from);
			}
		}
		return doc;
	}
	
	public void getAmount (Message message, User from, Document doc) throws SuspendExecution, Result
	{
		if (!message.hasText())
		{
			return;
		}
		if (message.getText().equals(Messages.RETURN_TO_MAIN))
		{
			sendStateMessage(States.MAIN_MENU, doc, from);
			goToState(from, States.MAIN_MENU);
		}
		ArrayList<Integer> list = new ArrayList<>();
		Scanner scanner = new Scanner(message.getText());
		while (scanner.hasNextInt())
		{
			list.add(scanner.nextInt());
		}
		if (!list.isEmpty())
		{
			int coins = doc.getInteger("coins");
			int previousState = doc.getInteger("previous_state");
			if (previousState == States.WAITING_FOR_POST)
			{
				viewEntity.getAmount(message, from, list, coins, botInterface);
			}
//						else if (previousState == States.)
			//TODO add channel
		}
	}
	
	public void welCome (Message message, User from) throws SuspendExecution, Result
	{
		String welcome = Messages.WELCOME;
		if (from.hasFirst_name())
		{
			welcome = welcome.replace("{first}", from.getFirst_name());
		}
		else
		{
			welcome = welcome.replace("{first}", "");
		}
		if (from.hasLast_name())
		{
			welcome = welcome.replace("{last}", from.getLast_name());
		}
		else
		{
			welcome = welcome.replace("{last}", "");
		}
		message.setText(welcome);
		message.setReply_markup(Keyboards.GET_PHONE);
		botInterface.sendMessage(message);
		dbDriver.updateUser(from, new Document("$set", new Document("state", States
				.WAITING_FOR_PHONE_NUMBER)));
	}
	
	public void confirmOrder (Message message, User from, Document doc) throws SuspendExecution, Result
	{
		String text = message.getText();
		if (text != null)
		{
			if (text.equals(Messages.YES))
			{
				int previous_state = doc.getInteger("previous_state");
				if (previous_state == States.WAITING_FOR_POST)
				{
					viewEntity.confirmOrder(message, from, doc, botInterface, this);
				}
				else if (previous_state == States.WAITING_FOR_CHANNEL)
				{
					//TODO add channel
				}
			}
			else if (text.equals((Messages.NO)))
			{
				sendStateMessage(States.MAIN_MENU, doc, from);
				goToState(from, States.MAIN_MENU);
			}
		}
	}
	
	public void processMainMenu (Message message, User from, Document doc)
			throws MongoException, SuspendExecution, Result
	{
		if (!message.hasText())
		{
			return;
		}
		String choice = message.getText();
		if (choice.equals(Messages.REGISTER_POST))
		{
			int coins = doc.getInteger("coins");
			if (coins < 2)
			{
				message.setText(Messages.LOW_CREDITS);
				botInterface.sendMessage(message);
				sendStateMessage(States.MAIN_MENU, doc, from);
				goToState(from, States.MAIN_MENU);
			}
			else
			{
				sendStateMessage(States.WAITING_FOR_POST, doc, from);
				dbDriver.updateUser(from, new Document("$set",
				                                       new Document("state", States.WAITING_FOR_POST)
				                    )
				                   );
			}
		}
		else if (choice.equals(Messages.VIEW_POSTS))
		{
			goToNextPost(from, doc);
		}
		//TODO other choices
	}
	
	void goToNextPost (User from, Document user)
			throws SuspendExecution, MongoException, Result
	{
		Message message = new Message().setChat(new Chat().setId(from.getId()));
		Document newDoc = dbDriver.nextPost(from.getId());
		if (newDoc != null)
		{
			ReplyKeyboardMarkup keyboard = Keyboards.CONFIRM_VIEW;
			int coins = user.getInteger("coins");
			keyboard.getKeyboard()[0][0].setText(Messages.VIEW_AGAIN.replace
					("{coins}", coins + ""));
			message.setText(Messages.VIEW_NOTE);
			message.setReply_markup(keyboard);
			botInterface.sendMessage(message);
			Chat chat = new Chat();
			long chatID = newDoc.getLong("chatID");
			int messageID = newDoc.getInteger("messageID");
			chat.setId(chatID);
			message.setForward_from_chat(chat);
			message.setForward_from_message_id(messageID);
			List<Document> visits = newDoc.get("visits", List.class);
			Document orders = (Document) newDoc.get(
					"orders", List.class).get(0);
			int postReqID = orders.getInteger("postReqID");
			boolean upsert = true;
			if (visits != null)
			{
				for (Document i : visits)
				{
					if (i.getInteger("userID") == from.getId())
					{
						upsert = false;
						break;
					}
				}
			}
			try
			{
				botInterface.forwardMessage(message);
			}
			catch (Result result)
			{
				if (result.getError_code() == 403)
				{
					Logger.ERROR(result);
					Logger.DEBUG("Error forwarding post to user");
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
				dbDriver.errorSendingPost(chatID, messageID);
				goToNextPost(from, user);
			}
			Document updateDoc = new Document("temp", new Document("upsert", upsert)
					.append("chatID", chatID).append("messageID", messageID).append("postReqID", postReqID));
			goToState(from, States.CONFIRM_VIEW_POST, updateDoc);
		}
		else
		{
			message.setText(Messages.NO_POSTS_NOW);
			botInterface.sendMessage(message);
			sendStateMessage(States.MAIN_MENU, user, from);
			goToState(from, States.MAIN_MENU);
		}
	}
	
	protected void sendStateMessage (int state, Document doc, User from) throws SuspendExecution, Result
	{
		Message message = new Message().setChat(new Chat().setId(from.getId()));
		switch (state)
		{
			case States.MAIN_MENU:
				message.setText(Messages.CHOOSE_MAIN_MENU.replace("{coins}", doc.getInteger("coins") + ""));
				message.setReply_markup(Keyboards.MAIN_MENU);
				botInterface.sendMessage(message);
				break;
			case States.WAITING_FOR_POST:
				message.setText(Messages.SEND_POST);
				message.setReply_markup(Keyboards.SEND_POST);
				botInterface.sendMessage(message);
				break;
		}
	}
	
	void goToState (User from, int state) throws SuspendExecution, MongoException
	{
		goToState(from, state, null);
	}
	
	private void goToState (User from, int state, Document doc) throws SuspendExecution, MongoException
	{
		if (doc == null)
		{
			doc = new Document("state", state);
		}
		else
		{
			doc.append("state", state);
		}
		Document newDoc = new Document("$set", doc);
		dbDriver.updateUser(from, newDoc);
	}
	
	private void getPhoneNumber (Message message, User from, Document doc)
			throws SuspendExecution, MongoException, Result
	{
		if ((message.hasContact() && message.getContact().getUser_id() == from.getId()))
		{
			Contact contact = message.getContact();
			boolean exists = dbDriver.updatePhoneNumber(from, Long.parseLong(contact.getPhone_number()));
			if (exists)
			{
				message.setText(Messages.WELCOME_BACK);
			}
			else
			{
				message.setText(Messages.PHONE_NUMBER_CONFIRMED);
			}
			Integer referrerID = dbDriver.checkForReferrer(from, referralReward);
			if (referrerID != null)
			{
				Message newMessage = new Message().setChat(new Chat().setId(referrerID))
						.setText(Messages.NEW_REFERRED_USER.replace("{coins}", referralReward + ""));
				try
				{
					botInterface.sendMessage(newMessage);
				}
				catch (Result r)
				{
					Logger.WARNING(r);
				}
			}
			botInterface.sendMessage(message);
			sendStateMessage(States.MAIN_MENU, doc, from);
		}
		else
		{
			message.setText(Messages.RESEND_PHONE_NUMBER);
			botInterface.sendMessage(message);
		}
	}
	
	protected void shutDown ()
	{
		dbDriver.shutDown();
	}
}