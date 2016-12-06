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

/**
 * Created by HellScre4m on 5/9/2016.
 */
public class EventProcessor extends Fiber<Void>
{
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
					dbDriver.updateUser(from, new Document("state", States.WAITING_FOR_PHONE_NUMBER));
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
						dbDriver.updateUser(from, new Document("phoneNumber", Long.parseLong(contact.getPhone_number
								())).append("state", States.PHONE_NUMBER_CONFRIMED));
						//TODO check for previous account
						message.setText(Messages.PHONE_NUMBER_CONFIRMED);
						botInterface.sendMessage(updateID, message);
						message.setText(Messages.CHOOSE_MAIN_MENU);
						message.setReply_markup(Keyboards.MAIN_MENU);
						botInterface.sendMessage(message);
					}
			}
//			else
//			{
//				System.out.println(doc.toJson());
//			}
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
