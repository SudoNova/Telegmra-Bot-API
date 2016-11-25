package com.sunova.bot;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import org.telegram.objects.Message;
import org.telegram.objects.Update;
import org.telegram.objects.User;

/**
 * Created by HellScre4m on 5/9/2016.
 */
public class EventProcessor extends Fiber<Void>
{
	private Fiber<Void> messageHandler;
	private Interface botInterface;
	
	protected EventProcessor (Interface botInterface)
	{
		this.botInterface = botInterface;
	}

//	protected void processTObject (TObject object) throws SuspendExecution
//	{
//		if (object instanceof Update)
//		{
//			Update update = (Update) object;
//			processUpdate(update);
//		}
//		else if (object instanceof Message)
//		{
//			System.out.println("test");
//		}
//	}
	
	protected void processUpdate (Update update) throws SuspendExecution
	{
		System.out.println("Processor processing update");
		if (update.containsMessage())
		{
			Message message = update.getMessage();
			User from = message.getFrom();
			String newMessage = "echo\n" + message.getText();
			int chat_id = message.getChat().getId();
			botInterface.sendMesssage(update.getUpdate_id(), chat_id, newMessage);
		}
		
	}
	
	private void sendMessage () throws SuspendExecution
	{
		//// TODO: 5/11/2016  Method body
	}
	
}
