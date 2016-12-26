package com.sunova.bot;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import com.sunova.prebuilt.Keyboards;
import com.sunova.prebuilt.Messages;
import com.sunova.prebuilt.States;
import org.bson.Document;
import org.telegram.objects.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HellScre4m on 5/9/2016.
 */
public class EventProcessor extends Fiber<Void>
{
	static final int visitFactor = 2;
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
				{
					message.setText(
							Messages.WELCOME.replace("{first}", from.getFirst_name())
									.replace("{last}", from.getLast_name()));
					message.setReply_markup(Keyboards.GET_PHONE);
					botInterface.sendMessage(message);
					dbDriver.updateUser(from, new Document("$set", new Document("state", States
							.WAITING_FOR_PHONE_NUMBER)));
				}
				break;
				case States.WAITING_FOR_PHONE_NUMBER:
				{
					Contact contact = message.getContact();
					if (contact == null || contact.getUser_id() != from.getId())
					{
						message.setText(Messages.RESEND_PHONE_NUMBER);
						botInterface.sendMessage(message);
					}
					else
					{
						//TODO check for previous account
						message.setText(Messages.PHONE_NUMBER_CONFIRMED);
						botInterface.sendMessage(message);
						sendStateMessage(States.MAIN_MENU, message, from);
						dbDriver.updateUser(from, new Document("$set", new Document("phoneNumber", Long.parseLong
								(contact
										 .getPhone_number
												 ())).append("state", States.MAIN_MENU)));
					}
				}
				break;
				case States.MAIN_MENU:
				{
					String choice = message.getText();
					if (choice.equals(Messages.REGISTER_POST))
					{
						int coins = doc.getInteger("coins");
						if (coins < 2)
						{
							message.setText(Messages.LOW_CREDITS);
							botInterface.sendMessage(message);
							sendStateMessage(States.MAIN_MENU, message, from);
							goToState(from, States.MAIN_MENU);
						}
						else
						{
							sendStateMessage(States.WAITING_FOR_POST, message, from);
							dbDriver.updateUser(from,
							                    new Document("$set", new Document("state", States.WAITING_FOR_POST)
							                    )
							                   );
						}
					}
					else if (choice.equals(Messages.VIEW_POSTS))
					{
						goToNextPost(message, from, doc);
					}
					//TODO other choices
				}
				break;
				case States.CONFIRM_VIEW_POST:
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
							goToNextPost(message, from, doc);
						}
						else if (choice.equals(Messages.VIEW_CONFIRMED))
						{
							dbDriver
									.confirmVisit(from, chatID, messageID, postReqID, upsert);
							goToState(from, States.MAIN_MENU);
						}
					}
				}
				break;
				case States.WAITING_FOR_POST:
				{
					Long chatID = null;
					Integer messageID = null;
					if (message.getText() != null && message.getText().equals(Messages.RETURN_TO_MAIN))
					{
						sendStateMessage(States.MAIN_MENU, message, from);
						goToState(from, States.MAIN_MENU);
					}
					else if (message.getForward_from_chat() != null)
					{
						chatID = message.getForward_from_chat().getId();
						messageID = message.getForward_from_message_id();
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
							}
							
						}
					}
					if (!(chatID == null || messageID == null))
					{
						Chat forwardChat = new Chat();
						forwardChat.setId(chatID);
						message.setForward_from_chat(forwardChat);
						message.setForward_from_message_id(messageID);
						Result result = botInterface.forwardMessage(message);
						if (result.isOk())
						{
							message.setText(
									Messages.ENTER_AMOUNT_VISIT.replace("{coins}", doc.getInteger("coins") + ""));
							message.setReply_markup(Keyboards.ENTER_INPUT);
							botInterface.sendMessage(message);
							Document newDoc = new Document("$set",
							                               new Document("state", States.WAITING_FOR_AMOUNT).append
									                               ("previous_state", States.WAITING_FOR_POST)
									                               .append("temp", Arrays
											                               .asList(chatID, messageID))
							);
							dbDriver.updateUser(from, newDoc);
						}
						else
						{
							message.setText(Messages.INVALID_POST);
							botInterface.sendMessage(message);
						}
					}
				}
				break;
				case States.WAITING_FOR_AMOUNT:
				{
					if (message.getText() != null && message.getText().equals(Messages.RETURN_TO_MAIN))
					{
						sendStateMessage(States.MAIN_MENU, message, from);
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

//						else if (previousState == States.)
						//TODO add channel
					}
				}
				break;
				case States.CONFIRM_VIEW_ORDER:
				{
					String text = message.getText();
					if (text != null)
					{
						if (text.equals(Messages.YES))
						{
							int previous_state = doc.getInteger("previous_state");
							
							if (previous_state == States.WAITING_FOR_POST)
							{
								ArrayList temp = (ArrayList) (doc.get("temp"));
								ArrayList order_amounts = (ArrayList) (doc.get("order_amounts"));
								dbDriver.insertNewPostViewOrder(from, (long) temp.get(0), (int) temp.get(1), (int)
										order_amounts.get(0));
								message.setText(Messages.REQUEST_DONE);
								botInterface.sendMessage(message);
								sendStateMessage(States.MAIN_MENU, message, from);
								goToState(from, States.MAIN_MENU);
							}
							else if (previous_state == States.WAITING_FOR_CHANNEL)
							{
								//TODO add channel
							}
							
						}
						else if (text.equals((Messages.NO)))
						{
							sendStateMessage(States.MAIN_MENU, message, from);
							goToState(from, States.MAIN_MENU);
						}
					}
				}
			}
		}
		catch (Throwable throwable)
		{
			throwable.printStackTrace();
		}
	}
	
	private void goToNextPost (Message message, User from, Document user) throws SuspendExecution,
			Throwable
	{
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
			Result result = botInterface.forwardMessage(message);
			if (result.isOk())
			{
				Document updateDoc = new Document("temp", new Document("upsert", upsert)
						.append("chatID", chatID)
						.append("messageID", messageID)
						.append("postReqID", postReqID));
				goToState(from, States.CONFIRM_VIEW_POST, updateDoc);
			}
			else
			{
				dbDriver.errorSendingPost(chatID, messageID);
				goToNextPost(message, from, user);
				//TODO finalize order
			}
		}
		else
		{
			message.setText(Messages.NO_POSTS_NOW);
			botInterface.sendMessage(message);
			sendStateMessage(States.MAIN_MENU, message, from);
			goToState(from, States.MAIN_MENU);
		}
	}
	
	private void sendStateMessage (int state, Message message, User from) throws SuspendExecution
	{
		switch (state)
		{
			case States.MAIN_MENU:
				message.setText(Messages.CHOOSE_MAIN_MENU);
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
	
	private void goToState (User from, int state) throws SuspendExecution, Throwable
	{
		goToState(from, state, null);
	}
	
	private void goToState (User from, int state, Document doc) throws SuspendExecution, Throwable
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
	
	void shutDown ()
	{
		dbDriver.shutDown();
	}
}
