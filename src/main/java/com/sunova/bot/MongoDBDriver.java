package com.sunova.bot;

import co.paralleluniverse.fibers.FiberAsync;
import co.paralleluniverse.fibers.SuspendExecution;
import com.mongodb.MongoException;
import com.mongodb.async.AsyncBatchCursor;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.sunova.prebuilt.States;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.telegram.objects.User;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.*;

/**
 * Created by HellScre4m on 11/30/2016.
 */
public class MongoDBDriver
{
	MongoClient dbClient;
	private MongoCollection<Document> users;
	private MongoCollection<Document> channels;
	private MongoCollection<Document> posts;
	private MongoCollection<Document> misc;
	private HashMap<Long, AsyncBatchCursor<Document>> postListMap;
	
	public MongoDBDriver ()
	{
		dbClient = MongoClients.create();
		MongoDatabase db = dbClient.getDatabase("tgAdmins");
		users = db.getCollection("users");
		channels = db.getCollection("channels");
		posts = db.getCollection("posts");
		misc = db.getCollection("misc");
		postListMap = new HashMap<>();
		new co.paralleluniverse.fibers.Fiber<Void>()
		{
			protected Void run () throws InterruptedException, SuspendExecution
			{
				try
				{
					new FiberAsync<String, Throwable>()
					{
						@Override
						protected void requestAsync ()
						{
							users.createIndex(
									new Document("phoneNumber", 1).append("userID", 1),
									new IndexOptions().background(true).unique(true),
									(r1, t1) ->
									{
										if (t1 != null)
										{
											asyncFailed(t1);
										}
									}
							                 );
							posts.createIndex(
									new Document("chatID", 1).append("messageID", 1),
									new IndexOptions().background(true).unique(true),
									(r2, t2) ->
									{
										if (t2 != null)
										{
											asyncFailed(t2);
										}
									}
							                 );
							channels.createIndex(
									new Document("chatID", 1),
									new IndexOptions().background(true)
											.unique(true),
									(r3, t3) ->
									{
										if (t3 != null)
										{
											asyncFailed(t3);
										}
									}
							                    );
							posts.createIndex(
									new Document("orders.postReqID", 1),
									new IndexOptions().background(true).unique(true),
									(r4, t4) ->
									{
										if (t4 != null)
										{
											asyncFailed(t4);
										}
									}
							                 );
							channels.createIndex(
									new Document("orders.channelReqID", 1),
									new IndexOptions().background(true).unique(true),
									(r5, t5) ->
									{
										if (t5 != null)
										{
											asyncFailed(t5);
										}
									}
							                    );
							posts.createIndex(
									new Document("orders.ownerID", 1).append("orders.remaining", 1)
											.append("errorCount", 1),
									new IndexOptions().background(true),
									(r6, t6) ->
									{
										if (t6 != null)
										{
											asyncFailed(t6);
										}
									}
							                 );
							posts.createIndex(
									new Document("visits.userID", 1).append("visits.time", 1),
									new IndexOptions().background(true),
									(r7, t7) ->
									{
										if (t7 != null)
										{
											asyncFailed(t7);
										}
										asyncCompleted(r7);
									}
							                 );
						}
					}.run();
				}
				catch (Throwable throwable)
				{
					throwable.printStackTrace();
				}
				return null;
			}
		}.start();
	}
	
	public Document getUser (int userID) throws SuspendExecution, MongoException
	{
		try
		{
			return new FiberAsync<Document, MongoException>()
			{
				@Override
				protected void requestAsync ()
				{
					users.find(
							eq("userID", userID))
							.first((v, t) ->
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
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean updatePhoneNumber (User from, long phoneNumber) throws MongoException, SuspendExecution
	{
		try
		{
			Document newDoc = new FiberAsync<Document, MongoException>()
			{
				@Override
				protected void requestAsync ()
				{
					users.find(
							eq("phoneNumber", phoneNumber))
							.first((r, t) ->
							       {
								       if (t != null)
								       {
									       asyncFailed(t);
								       }
								       else
								       {
									       asyncCompleted(r);
								       }
							       }
							      );
				}
			}.run();
			if (newDoc != null)
			{
				ObjectId prevID = (ObjectId) newDoc.get("_id");
				newDoc = new FiberAsync<Document, MongoException>()
				{
					@Override
					protected void requestAsync ()
					{
						users.findOneAndDelete(
								eq("userID", from.getId()), (r, t) ->
								{
									if (t != null)
									{
										asyncFailed(t);
									}
									else if (r == null)
									{
										asyncFailed(new MongoException("Unable to delete previous ID"));
									}
								}
						                      );
						users.findOneAndUpdate(
								eq("_id", prevID),
								new Document("$set", new Document("userID", from.getId())
										.append("state", States.MAIN_MENU)),
								new FindOneAndUpdateOptions().returnDocument(ReturnDocument.BEFORE),
								(r, t) ->
								{
									if (t != null)
									{
										asyncFailed(t);
									}
									else
									{
										asyncCompleted(r);
									}
									
								}
						                      );
					}
					
				}.run();
				if (newDoc != null)
				{
					return true;
				}
				else
				{
					throw new MongoException("Unable to update previous ID");
				}
			}
			updateUser(from, new Document("$set", new Document("phoneNumber", phoneNumber)
					           .append("state", States.MAIN_MENU))
			          );
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	Integer checkForReferrer (User from, int referralReward) throws SuspendExecution, MongoException
	{
		try
		{
			Document result = new FiberAsync<Document, MongoException>()
			{
				@Override
				protected void requestAsync ()
				{
					users.findOneAndUpdate(
							eq("userID", from.getId()),
							new Document("$unset", new Document("temp_referrerID", "")),
							new FindOneAndUpdateOptions().returnDocument(ReturnDocument.BEFORE),
							(r, t) ->
							{
								if (t != null)
								{
									asyncFailed(t);
								}
								else
								{
									Integer referrerID = r.getInteger("referrerID");
									if (referrerID != null)
									{
										asyncCompleted(null);
										return;
									}
									try
									{
										referrerID = r.getInteger("temp_referrerID");
										users.findOneAndUpdate(
												eq("userID", referrerID),
												new Document("$inc",
												             new Document("coins", referralReward)
												),
												(r1, t1) ->
												{
													if (t1 != null)
													{
														asyncFailed(t1);
													}
													else
													{
														asyncCompleted(r1);
													}
												}
										                      );
									}
									catch (NullPointerException | ClassCastException e)
									{
										asyncCompleted(null);
									}
								}
							}
					                      );
				}
			}.run();
			if (result == null)
			{
				return null;
			}
			else
			{
				return result.getInteger("userID");
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public void updateUser (User user, Document doc) throws SuspendExecution, MongoException
	{
		try
		{
			new FiberAsync<UpdateResult, MongoException>()
			{
				@Override
				protected void requestAsync ()
				{
					users.updateOne(
							eq("userID", user.getId()), doc,
							(result, t) ->
							{
								if (t != null)
								{
									asyncFailed(t);
								}
								else
								{
									asyncCompleted(result);
								}
							}
					               );
				}
			}.run();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public Document insertUser (User user) throws SuspendExecution, MongoException
	{
		return insertUser(user, null);
	}
	
	public Document insertUser (User user, Integer referrerID) throws SuspendExecution, MongoException
	{
		Document doc = new Document();
		doc.append("userID", user.getId()).append("type", User.FREE_USER).append("coins", 1000).append("state", 1);
		doc.append("previous_state", 1).append("registrationDate", System.currentTimeMillis());
		if (referrerID != null)
		{
			doc.append("temp_referrerID", referrerID);
		}
		try
		{
			new FiberAsync<UpdateResult, MongoException>()
			{
				@Override
				protected void requestAsync ()
				{
					users.updateOne(
							eq("userID", user.getId()), new Document("$set", doc),
							new UpdateOptions().upsert(true),
							(r, t) ->
							{
								if (t != null)
								{
									asyncFailed(t);
								}
								else
								{
									asyncCompleted(r);
								}
							}
					               );
				}
				
			}.run();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return doc;
	}
	
	public void shutDown ()
	{
		dbClient.close();
	}
	
	//TODO fix
	public void errorSendingPost (long chatID, int messageID) throws SuspendExecution, MongoException
	{
		try
		{
			new FiberAsync<Document, MongoException>()
			{
				
				@Override
				protected void requestAsync ()
				{
					posts.findOneAndUpdate(
							and(eq("chatID", chatID),
							    eq("messageID", messageID)
							   ),
							new Document("$inc", new Document("errorCount", 1)),
							new FindOneAndUpdateOptions().upsert(true),
							(r, t) ->
							{
								if (t != null)
								{
									asyncFailed(t);
								}
								else
								{
									asyncCompleted(r);
								}
							}
					                      );
				}
			}.run();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void insertNewPostViewOrder (User user, long chatID, int messageID, int amount)
			throws SuspendExecution, MongoException
	{
		try
		{
			new FiberAsync<Document, MongoException>()
			{
				@Override
				protected void requestAsync ()
				{
					misc.findOneAndUpdate(
							new Document(),
							new Document("$inc", new Document("postReqID", 1)),
							new FindOneAndUpdateOptions().upsert(true)
									.returnDocument(ReturnDocument.AFTER),
							(result, t) ->
							{
								int postReqId = result.getInteger("postReqID");
								long time = System.currentTimeMillis();
								Document newDoc = new Document("postReqID", postReqId)
										.append("ownerID", user.getId()).append("time", time)
										.append("amount", amount).append("remaining", amount)
										.append("viewCount", 0);
								posts.updateOne(
										and(eq("chatID", chatID), eq("messageID", messageID)),
										new Document("$push", new Document("orders", newDoc))
												.append("$set", new Document("errorCount", 0)),
										new UpdateOptions().upsert(true), (rr, tt) ->
										{
											if (t != null)
											{
												asyncFailed(t);
											}
											else
											{
												asyncCompleted(result);
											}
										}
								               );
								
							}
					                     );
				}
			}.run();
			updateUser(user, new Document("$inc", new Document("coins", -amount * ViewEntity.visitFactor)));
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public Document nextPost (long userID) throws SuspendExecution, MongoException
	{
		AsyncBatchCursor<Document> cursor = postListMap.get(userID);
		if (cursor == null || cursor.isClosed())
		{
			try
			{
				cursor = new FiberAsync<AsyncBatchCursor<Document>, MongoException>()
				{
					@Override
					protected void requestAsync ()
					{
						posts.find(and(
								elemMatch("orders", and(
										gt("remaining", 0),
										ne("ownerID", userID)
								                       )),
								or(nin("visits.userID", userID),
								   elemMatch("visits",
								             and(eq("userID", userID),
								                 lt("time",
								                    System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)
								                   )
								
								                )
								            )
								  )
						              )).batchSize(1).batchCursor(
								(r, t) ->
								{
									if (t != null)
									{
										asyncFailed(t);
									}
									else
									{
										asyncCompleted(r);
									}
								});//.projection(Projections.elemMatch("orders"));
						
						
					}
				}.run();
				postListMap.put(userID, cursor);
				cursor = postListMap.get(userID);
				final AsyncBatchCursor<Document> temp = cursor;
				Document result = new FiberAsync<Document, MongoException>()
				{
					@Override
					protected void requestAsync ()
					{
						temp.next(
								(r, t) ->
								{
									if (t != null)
									{
										asyncFailed(t);
									}
									else
									{
										try
										{
											asyncCompleted(r.get(0));
										}
										catch (NullPointerException e)
										{
											asyncCompleted(null);
										}
									}
								});
					}
				}.run();
				return result;
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public Document confirmVisit (User from, long chatID, int messageID, int postReqID, boolean upsert) throws
			SuspendExecution, MongoException
	{
		try
		{
			return new FiberAsync<Document, MongoException>()
			{
				
				@Override
				protected void requestAsync ()
				{
					Document doc = new Document("$inc", new Document("orders.$.viewCount", 1)
							.append("orders.$.remaining", -1));
					if (upsert)
					{
						doc.append("$push", new Document
								("visits", new Document("userID", from.getId())
										.append("time", System.currentTimeMillis())));
					}
					else
					{
						doc.append("$set", new Document("visits.$.time", System.currentTimeMillis()));
					}
					
					Document filter = new Document("chatID", chatID)
							.append("messageID", messageID).append("orders.postReqID", postReqID);
					if (!upsert)
					{
						filter.append("visits.userID", from.getId());
					}
					//				filter = new Document("$and", Collections.singletonList(filter));
					posts.updateOne(
							filter, doc, (r, t) ->
							{
								if (t != null)
								{
									asyncFailed(t);
								}
								users.findOneAndUpdate(
										eq("userID", from.getId()),
										new Document("$inc", new Document("coins", 1)),
										new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER),
										(r2, t2) ->
										{
											
											if (t2 != null)
											{
												asyncFailed(t2);
											}
											else
											{
												asyncCompleted(r2);
											}
										}
								                      );
								
							}
					               );
				}
			}.
					
					run();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
}
