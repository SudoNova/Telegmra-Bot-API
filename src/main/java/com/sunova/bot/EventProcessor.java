package com.sunova.bot;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import com.sunova.prebuilt.Keyboards;
import com.sunova.prebuilt.Messages;
import com.sunova.prebuilt.States;
import org.bson.Document;
import org.telegram.objects.Contact;
import org.telegram.objects.Message;
import org.telegram.objects.Update;
import org.telegram.objects.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HellScre4m on 5/9/2016.
 */
public class EventProcessor extends Fiber<Void>
{
	private static final int visitFactor = 2;
	private Interface botInterface;
	private MongoDBDriver dbDriver;
	
	protected EventProcessor (Interface botInterface)
	{
		this.botInterface = botInterface;
		dbDriver = new MongoDBDriver();
		
	}
	
	void processUpdate (Update update) throws SuspendExecution
	{
//		System.out.println("Processor processing update");
		if (update.containsMessage())
		{
			Message message = update.getMessage();
			processMessage(update.getUpdate_id(), message);
			
		}
		
	}
	
	void processMessage (int updateID, Message message) throws SuspendExecution
	{
		User from = message.getFrom();
		
		try
		{
			Document doc = dbDriver.getUser(from.getId());
			if (doc == null)
			{
				doc = dbDriver.insertUser(from);
			}
			switch (doc.getInteger("state"))
			{
				case States.START:
					message.setText(
							Messages.WELCOME.replace("{first}", from.getFirst_name())
									.replace("{last}", from.getLast_name()));
					message.setReply_markup(Keyboards.GET_PHONE);
					botInterface.sendMessage(updateID, message);
					dbDriver.updateUser(from, new Document("$set", new Document("state", States
							.WAITING_FOR_PHONE_NUMBER)));
					break;
				case States.WAITING_FOR_PHONE_NUMBER:
					Contact contact = message.getContact();
					if (contact == null || contact.getUser_id() != from.getId())
					{
						message.setText(Messages.RESEND_PHONE_NUMBER);
						botInterface.sendMessage(updateID, message);
					}
					else
					{
						//TODO check for previous account
						
						message.setText(Messages.PHONE_NUMBER_CONFIRMED);
						botInterface.sendMessage(updateID, message);
						message.setText(Messages.CHOOSE_MAIN_MENU);
						message.setReply_markup(Keyboards.MAIN_MENU);
						botInterface.sendMessage(message);
						dbDriver.updateUser(from, new Document("$set", new Document("phoneNumber", Long.parseLong
								(contact
										 .getPhone_number
												 ())).append("state", States.MAIN_MENU)));
					}
					break;
				case States.MAIN_MENU:
					String choice = message.getText();
					if (choice.equals(Messages.REGISTER_POST))
					{
						message.setText(Messages.SEND_POST);
						message.setReply_markup(Keyboards.SEND_POST);
						botInterface.sendMessage(updateID, message);
						dbDriver.updateUser(from, new Document("$set", new Document("state", States.WAITING_FOR_POST)));
					}
					//TODO other choices
					break;
				case States.WAITING_FOR_POST:
					Long chatID = null;
					Integer messageID = null;
					if (message.getText() != null && message.getText().equals(Messages.RETURN_TO_MAIN))
					{
						message.setText(Messages.CHOOSE_MAIN_MENU);
						message.setReply_markup(Keyboards.MAIN_MENU);
						botInterface.sendMessage(updateID, message);
						Document newDoc = new Document("set", new Document("state", States.MAIN_MENU));
						dbDriver.updateUser(from, newDoc);
					}
					else if (message.getForward_from_chat() != null)
					{
						chatID = message.getForward_from_chat().getId();
						messageID = message.getForward_from_message_id();
						System.out.println(chatID + "   " + messageID);
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
							chatID = botInterface.getChatID("@" + channelName).getId();
							pattern = Pattern.compile("/\\d+");
							matcher = pattern.matcher(messageBody);
							if (matcher.find())
							{
								messageID = Integer.parseInt(matcher.group().substring(1));
								System.out.println(chatID + " " + messageID);
							}
							
						}
					}
					if (!(chatID == null || messageID == null))
					{
						message.setText(Messages.ENTER_AMOUNT_VISIT.replace("{coins}", doc.getInteger("coins") + ""));
						message.setReply_markup(Keyboards.ENTER_INPUT);
						botInterface.sendMessage(message);
						Document newDoc = new Document("$set", new Document("state", States.WAITING_FOR_AMOUNT).append
								("previous_state", States.WAITING_FOR_POST).append("temp", Arrays
								.asList(chatID, messageID)));
						dbDriver.updateUser(from, newDoc);
						
					}
					break;
				case States.WAITING_FOR_AMOUNT:
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
							if (list.get(0) * visitFactor > coins)
							{
								
								message.setText(Messages.AMOUNT_EXCEEDS);
								botInterface.sendMessage(message);
							}
							else
							{
								Document newDoc = new Document("$addToSet", new Document("order_amounts", list));
								newDoc.append("$set", new Document("state", States.CONFIRM));
								message.setText(Messages.CONFIRM_VIEW);
								message.setReply_markup(Keyboards.CONFIRM);
								botInterface.sendMessage(message);
								dbDriver.updateUser(from, newDoc);
							}
						}

//						else if (previousState == States.)
						//TODO add channel
					}
					break;
				case States.CONFIRM:
					String text = message.getText();
					if (text != null)
					{
						if (text.equals(Messages.YES))
						{
							int previous_state = doc.getInteger("previous_state");
							if (previous_state == States.WAITING_FOR_POST)
							{
								
							}
							else if (previous_state == States.WAITING_FOR_CHANNEL)
							{
								//TODO add channel
							}
							
						}
						else if (text.equals((Messages.NO)))
						{
							
						}
					}
			}
			
		}
		catch (Throwable throwable)
		{
			throwable.printStackTrace();
		}
	}
	
	void shutDown ()
	{
		dbDriver.shutDown();
	}
}
