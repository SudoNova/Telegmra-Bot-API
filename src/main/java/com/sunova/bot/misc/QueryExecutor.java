package com.sunova.bot.misc;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

/**
 * Created by HellScre4m on 2/1/2017.
 */
public class QueryExecutor
{
	public static void main (String[] args) throws InterruptedException
	{
		MongoClient client = MongoClients.create();
		Thread.sleep(3000);
		MongoDatabase db = client.getDatabase("tgAdmins");
		MongoCollection users = db.getCollection("users");
		MongoCollection posts = db.getCollection("posts");
		posts.deleteMany(new Document(), (r, t) ->
		{
			if (t != null)
			{
				t.printStackTrace();
			}
			System.out.println("done1");
		});
		Thread.sleep(3000);
		users.updateMany(Filters.gte("state", 3)
				, new Document("$inc", new Document("coins", 200))
				                 .append("$set", new Document("state", 3))
				                 .append("$unset", new Document("temp", "").append("previous_state", "")
						                 .append("order_amounts", "")),
				         (r, t) ->
		                 {
			                 if (t != null)
			                 {
				                 t.printStackTrace();
			                 }
			                 System.out.println("done2");
		                 }
		                );
		
		Thread.sleep(3000);
		
	}
}
