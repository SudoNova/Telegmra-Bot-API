package com.sunova.bot;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberAsync;
import co.paralleluniverse.fibers.SuspendExecution;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
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
	//	private MongoClient dbClient;
	private MongoCollection<Document> users;
	private MongoCollection<Document> channels;
	
	protected EventProcessor (Interface botInterface)
	{
		this.botInterface = botInterface;
		MongoClient dbClient = MongoClients.create();
		MongoDatabase db = dbClient.getDatabase("tgAdmins");
		users = db.getCollection("users");
		
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
	
	void processUpdate (Update update) throws SuspendExecution
	{
		System.out.println("Processor processing update");
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
			Document doc = new FiberAsync<Document, Throwable>()
			{
				@Override
				protected void requestAsync ()
				{
					users.find(Filters.eq("userID", from.getId())).first((v, t) ->
					                                                     {
						                                                     if (t != null)
						                                                     {
							                                                     asyncFailed(t);
						                                                     }
						                                                     else
						                                                     {
							                                                     asyncCompleted(v);
						                                                     }
					                                                     }
					
					);
					
				}
			}.run();
			System.out.println(doc == null ? "Null" : doc.toJson());
		}
		catch (Throwable throwable)
		{
			throwable.printStackTrace();
		}
		
		int chat_id = message.getChat().getId();
		botInterface.sendMesssage(updateID, chat_id, newMessage);
	}
	
	
}
