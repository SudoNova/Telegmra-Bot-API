package com.sunova.bot;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import org.bson.Document;
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
		String newMessage = "echo\n" + message.getText();
		try
		{
			Document doc = dbDriver.getUser(from.getId());
			if (doc == null)
			{
				dbDriver.insertUser(from);
			}
			else
			{
				System.out.println(doc.toJson());
			}
		}
		
		catch (Throwable throwable)
		{
			if (!(throwable instanceof NullPointerException))
			{
				throwable.printStackTrace();
			}
		}
		
		int chat_id = message.getChat().getId();
		botInterface.sendMesssage(updateID, chat_id, newMessage);
	}
	
	
}
