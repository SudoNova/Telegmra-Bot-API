package com.sunova.bot;

import co.paralleluniverse.fibers.FiberAsync;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.telegram.objects.User;

import java.util.Collections;

/**
 * Created by HellScre4m on 11/30/2016.
 */
public class MongoDBDriver
{
	MongoClient dbClient;
	private MongoCollection<Document> users;
	private MongoCollection<Document> channels;
	public MongoDBDriver ()
	{
		dbClient = MongoClients.create();
		MongoDatabase db = dbClient.getDatabase("tgAdmins");
		users = db.getCollection("users");
	}
	
	public Document getUser (int userID) throws Throwable
	{
		Document doc = new FiberAsync<Document, Throwable>()
		{
			@Override
			protected void requestAsync ()
			{
				users.find(Filters.eq("userID", userID)).first((Document v, Throwable t) ->
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
		return doc;
		
	}
	
	public Document insertUser (User user) throws Throwable
	{
		Document doc = new Document();
		doc.append("userID", user.getId());
		doc.append("firstName", user.getFirst_name() == null ? "" : user.getFirst_name());
		doc.append("lastName", user.getLast_name() == null ? "" : user.getLast_name());
		doc.append("type", User.FREE_USER);
		if (user.getUsername() != null)
		{
			doc.append("userName", user.getUsername());
		}
		doc.append("state", 1);
		doc.append("previous_state", 1);
		doc.put("registeredChannels", Collections.EMPTY_LIST);
		doc.put("visitedPosts", Collections.EMPTY_LIST);
//		doc.append("registeredChannels", new ArrayList<>(Arrays.asList(test)));
		
		new FiberAsync<Void, Throwable>()
		{
			@Override
			protected void requestAsync ()
			{
				users.insertOne(doc, (r, t) ->
				{
					if (t != null)
					{
						asyncFailed(t);
					}
					else
					{
						asyncCompleted(r);
					}
				});
			}
			
		}.run();
		System.out.println("insert done");
		return null;
	}
	
	public void shutDown ()
	{
		dbClient.close();
	}
}
