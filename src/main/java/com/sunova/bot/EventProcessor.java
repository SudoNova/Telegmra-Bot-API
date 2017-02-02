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
import java.util.Scanner;

/**
 * Created by HellScre4m on 5/9/2016.
 */
public class EventProcessor extends UserInterface
{
	static final int referralReward = 40;
	User botUser;
	private MongoDBDriver dbDriver;
	private ViewEntity viewEntity;
	private MemberEntity memberEntity;
	
	public EventProcessor ()
	{
		dbDriver = new MongoDBDriver();
		viewEntity = new ViewEntity(dbDriver);
		memberEntity = new MemberEntity(dbDriver);
	}
	
	public Message onMessage (Message message) throws SuspendExecution
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
					viewEntity.confirmViewPost(message, doc, this);
					break;
				case States.POST_ENTER:
					viewEntity.getPost(message, from, doc, bot, this);
					break;
				case States.CHANNEL_ENTER:
					memberEntity.enterChannel(message, doc, this);
					break;
				case States.WAITING_FOR_AMOUNT:
					getAmount(message, from, doc);
					break;
				case States.CONFIRM_VIEW_ORDER:
					confirmOrder(message, from, doc);
					break;
				case States.TRACK_CHOOSE:
					track(message, from, doc);
					break;
				case States.TRACK_POSTS:
					viewEntity.trackRequests(message, doc, this);
					break;
				case States.CHANNEL_DESCRIBE:
					memberEntity.getDescription(message, doc, this);
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
		return null;
	}
	
	private void track (Message message, User from, Document doc) throws SuspendExecution, Result
	{
		if (!message.hasText())
		{
			return;
		}
		String choice = message.getText();
		if (choice.equals(Messages.TRACK_POST_REQUESTS))
		{
			viewEntity.trackRequests(message, doc, this);
		}
		else if (choice.equals(Messages.TRACK_MEMBER_REQUESTS))
		{
//						memberEntity.trackRequests(from, message, doc, botInterface, this, true);
		}
		else if (choice.equals(Messages.RETURN_TO_MAIN) || choice.startsWith("/start"))
		{
			sendStateMessage(States.MAIN_MENU, doc, from.getId());
			goToState(from.getId(), States.MAIN_MENU);
		}
		else
		{
			sendStateMessage(States.TRACK_CHOOSE, doc, from.getId());
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
		if (message.getText().equals(Messages.RETURN_TO_MAIN) || message.getText().startsWith("/start"))
		{
			sendStateMessage(States.MAIN_MENU, doc, from.getId());
			goToState(from.getId(), States.MAIN_MENU, null, new Document("temp", "").append("previous_state", ""));
			return;
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
			if (previousState == States.POST_ENTER)
			{
				viewEntity.getAmount(from, list, coins, botInterface);
			}
			else if (previousState == States.CHANNEL_ENTER)
			{
				memberEntity.getAmount(from, list, coins, botInterface);
			}
//						else if (previousState == States.)
			//TODO add channel
		}
		else
		{
			sendStateMessage(States.WAITING_FOR_AMOUNT, doc, from.getId());
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
		goToState(from.getId(), States.WAITING_FOR_PHONE_NUMBER);
	}
	
	public void confirmOrder (Message message, User from, Document doc) throws SuspendExecution, Result
	{
		String text = message.getText();
		if (text != null)
		{
			if (text.equals(Messages.YES))
			{
				int previous_state = doc.getInteger("previous_state");
				if (previous_state == States.POST_ENTER)
				{
					viewEntity.confirmOrder(from, doc, botInterface, this);
				}
				else if (previous_state == States.CHANNEL_ENTER)
				{
					//TODO add channel
				}
			}
			else if (text.equals((Messages.NO)) || text.startsWith("/start"))
			{
				sendStateMessage(States.MAIN_MENU, doc, from.getId());
				goToState(from.getId(), States.MAIN_MENU, null, new Document("order_amounts", "").append("temp", "")
						.append("previous_state", ""));
				
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
		if (choice.equals(Messages.POST_ORDER))
		{
			int coins = doc.getInteger("coins");
			if (coins < 2)
			{
				message.setText(Messages.LOW_CREDITS);
				botInterface.sendMessage(message);
				sendStateMessage(States.MAIN_MENU, doc, from.getId());
				goToState(from.getId(), States.MAIN_MENU);
			}
			else
			{
				sendStateMessage(States.POST_ENTER, doc, from.getId());
				goToState(from.getId(), States.POST_ENTER);
			}
		}
		else if (choice.equals(Messages.CHANNEL_ORDER))
		{
//			message.setText("در حال آماده سازی. لطفا چند روز دیگر صبر کنید");
//			botInterface.sendMessage(message);
			int coins = doc.getInteger("coins");
			if (coins < 2)
			{
				message.setText(Messages.LOW_CREDITS);
				botInterface.sendMessage(message);
				sendStateMessage(States.MAIN_MENU, doc, from.getId());
				goToState(from.getId(), States.MAIN_MENU);
			}
			sendStateMessage(States.CHANNEL_ENTER, doc, from.getId());
			goToState(from.getId(), States.CHANNEL_ENTER);
		}
		else if (choice.equals(Messages.POST_VIEW))
		{
			viewEntity.goToNextPost(doc, this);
		}
		else if (choice.equals(Messages.TRACK))
		{
			sendStateMessage(States.TRACK_CHOOSE, doc, from.getId());
			goToState(from.getId(), States.TRACK_CHOOSE);
		}
		else if (choice.startsWith("/start"))
		{
			sendStateMessage(States.MAIN_MENU, doc, from.getId());
//			goToState(from, States.MAIN_MENU);
		}
		else if (choice.equals(Messages.REFERRAL_LINK))
		{
			if (botUser == null)
			{
				botUser = botInterface.getMe();
			}
			String text = Messages.REFERRAL_NOTE.replace("{coins}", referralReward + "")
					+ "https://telegram.me/" + botUser.getUsername() + "?start=" + doc.getInteger("userID");
			message.setText(text);
			botInterface.sendMessage(message, false, true);
		}
		else if (choice.equals(Messages.CONTACT_US))
		{
			message.setText("برای ارتباط با مدیر روبات به @ViewMemberSupport پیام دهید.");
			botInterface.sendMessage(message);
		}
		else
		{
			sendStateMessage(States.MAIN_MENU, doc, from.getId());
		}
		//TODO other choices
	}
	
	protected void sendStateMessage (int state, Document doc, int userID) throws SuspendExecution, Result
	{
		Message message = new Message().setChat(new Chat().setId(userID));
		switch (state)
		{
			case States.MAIN_MENU:
				message.setText(Messages.MAIN_MENU.replace("{coins}", doc.getInteger("coins") + ""));
				message.setReply_markup(Keyboards.MAIN_MENU);
				botInterface.sendMessage(message);
				break;
			case States.POST_ENTER:
				message.setText(Messages.POST_SEND);
				message.setReply_markup(Keyboards.ENTER_ORDER);
				botInterface.sendMessage(message);
				break;
			case States.TRACK_CHOOSE:
				message.setText(Messages.TRACK_CHOOSE);
				message.setReply_markup(Keyboards.TRACK_CHOOSE);
				botInterface.sendMessage(message);
				break;
			case States.CHANNEL_ENTER:
				message.setText(Messages.CHANNEL_ENTER);
				message.setReply_markup(Keyboards.ENTER_ORDER);
				botInterface.sendMessage(message);
				break;
			case States.WAITING_FOR_AMOUNT:
				message.setText(
						Messages.POST_ENTER_AMOUNT.replace("{coins}", doc.getInteger("coins") + ""));
				message.setReply_markup(Keyboards.ENTER_INPUT);
				botInterface.sendMessage(message);
				break;
		}
	}
	
	void goToState (int userID, int state) throws SuspendExecution, MongoException
	{
		goToState(userID, state, null, null);
	}
	
	void goToState (int userID, int state, Document set, Document unset) throws SuspendExecution, MongoException
	{
		if (set == null)
		{
			set = new Document("state", state);
		}
		else
		{
			set.append("state", state);
		}
		Document newDoc = new Document("$set", set);
		if (unset != null)
		{
			newDoc.append("$unset", unset);
		}
		
		dbDriver.updateUser(userID, newDoc);
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
						.setText(Messages.REFERRAL_NEW.replace("{coins}", referralReward + ""));
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
			sendStateMessage(States.MAIN_MENU, doc, from.getId());
		}
		else
		{
			message.setText(Messages.RESEND_PHONE_NUMBER).setReply_markup(Keyboards.GET_PHONE);
			botInterface.sendMessage(message);
		}
	}
	
	protected void shutDown ()
	{
		dbDriver.shutDown();
	}
}