package com.sunova.bot;

import co.paralleluniverse.fibers.FiberAsync;
import co.paralleluniverse.fibers.SuspendExecution;
import com.mongodb.async.AsyncBatchCursor;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.*;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.telegram.objects.User;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
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
	private HashMap<Integer, AbstractMap.SimpleEntry<AsyncBatchCursor<Document>, Long>> bachCursorMap;
	
	public MongoDBDriver ()
	{
		dbClient = MongoClients.create();
		MongoDatabase db = dbClient.getDatabase("tgAdmins");
		users = db.getCollection("users");
		channels = db.getCollection("channels");
		posts = db.getCollection("posts");
		misc = db.getCollection("misc");
		bachCursorMap = new HashMap<>();
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
									new Document("userID", 1),
									new IndexOptions().background(true).unique(true), (r1,
									                                                   t1) ->
									{
										posts.createIndex(
												new Document("chatID", 1).append("messageID", 1),
												new IndexOptions().background(true).unique(true)
												, (r2,
												   t2) ->
												{
													channels.createIndex(
															new Document("chatID", 1),
															new IndexOptions().background(true)
																	.unique(true), (r3,
															                        t3) ->
															{
																posts.createIndex(
																		new Document
																				("orders.postReqID", 1
																				),
																		new IndexOptions()
																				.background(
																						true)
																				.unique(true),
																		(r4,
																		 t4) ->
																		{
																			channels.createIndex(
																					new
																							Document(
																							"orders.channelReqID",
																							1
																					),
																					new IndexOptions()
																							.background(
																									true)
																							.unique(true)
																					,
																					(r5,
																					 t5) ->
																					{
																						posts.createIndex(
																								new
																										Document(
																										"orders.ownerID",
																										1
																								),
																								new
																										IndexOptions()
																										.background(
																												true),
																								(r6, t6) ->
																								{
																									posts.createIndex(
																											new Document(
																													"orders.visitList.userID",
																													1
																											),
																											new IndexOptions()
																													.background(
																															true),
																											(r7, t7) ->
																											{
																												asyncCompleted(
																														r1);
																												//TODO add channel request indexes
																											}
																									                 );
																								}
																						                 );
																						
																					}
																			                    );
																		}
																                 );
															}
													
													                    );
												}
										                 );
										
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
	
	public Document getUser (int userID) throws SuspendExecution, Throwable
	{
		return new FiberAsync<Document, Throwable>()
		{
			@Override
			protected void requestAsync ()
			{
				users.find(eq("userID", userID))
						.first((Document v, Throwable t) ->
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
	
	public boolean updateUser (User user, Document doc) throws SuspendExecution, Throwable
	{
		final UpdateResult[] updateResult = new UpdateResult[1];
		new FiberAsync<UpdateResult, Throwable>()
		{
			@Override
			protected void requestAsync ()
			{
				users.updateOne(eq("userID", user.getId()), doc, new
						SingleResultCallback<UpdateResult>()
						{
							@Override
							public void onResult (UpdateResult result, Throwable t)
							{
								if (t != null)
								{
									asyncFailed(t);
								}
								else
								{
									updateResult[0] = result;
									asyncCompleted(result);
								}
							}
						});
			}
		}.run();
		return !(updateResult[0] == null || updateResult[0].wasAcknowledged());
	}
	
	public Document insertUser (User user) throws SuspendExecution, Throwable
	{
		
		Document doc = new Document();
		doc.append("userID", user.getId());
//		doc.append("firstName", user.getFirst_name() == null ? "" : user.getFirst_name());
//		doc.append("lastName", user.getLast_name() == null ? "" : user.getLast_name());
		doc.append("type", User.FREE_USER);
//		if (user.getUsername() != null)
//		{
//			doc.append("userName", user.getUsername());
//		}
		doc.append("coins", 1000);
		doc.append("state", 1);
		doc.append("previous_state", 1).append("registrationDate", System.currentTimeMillis());
//		doc.append("registeredChannels", new ArrayList<>(Arrays.asList(test)));
		
		new FiberAsync<UpdateResult, Throwable>()
		{
			@Override
			protected void requestAsync ()
			{
				users.updateOne(
						eq("userID", user.getId()), new Document("$set", doc),
						new UpdateOptions()
								.upsert
										(true),
						(r,
						 t) ->
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
		return doc;
	}
	
	public void shutDown ()
	{
		dbClient.close();
	}
	
	public void insertNewPostViewOrder (User user, long chatID, int messageID, int amount)
			throws SuspendExecution, Throwable
	{
		Document result = new FiberAsync<Document, Throwable>()
		{
			@Override
			protected void requestAsync ()
			{
				misc.findOneAndUpdate(
						new Document(),
						new Document(
								"$inc", new Document("postReqID", 1)),
						new FindOneAndUpdateOptions().upsert(true)
								.returnDocument(ReturnDocument.AFTER),
						new SingleResultCallback<Document>()
						{
							@Override
							public void onResult (Document result, Throwable t)
							{
								int postReqId = result.getInteger("postReqID");
								long time = System.currentTimeMillis();
								Document newDoc = new Document("postReqID", postReqId)
										.append("ownerID", user.getId()).append("amount", amount)
										.append("visits", 0)
										.append("time", time);
								posts.updateOne(
										and(eq("chatID", chatID), eq("messageID", messageID)), new
												Document("$push", new Document("orders", newDoc)), new
												UpdateOptions().upsert
												(true), (rr,
										                 tt) ->
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
							
						}
				                     );
			}
		}.run();
		updateUser(user, new Document("$inc", new Document("coins", -amount * EventProcessor.visitFactor)));
		
		
	}
	
	public Document nextPost (int userID) throws SuspendExecution, Throwable
	{
		AbstractMap.SimpleEntry<AsyncBatchCursor<Document>, Long> entry = bachCursorMap.get(userID);
		if (entry == null ||
				System.currentTimeMillis() - entry.getValue() > TimeUnit.MINUTES.toMillis(10) ||
				entry.getKey().isClosed())
		
		{
			entry = new FiberAsync<AbstractMap.SimpleEntry<AsyncBatchCursor<Document>, Long>,
					Throwable>()
			{
				
				@Override
				protected void requestAsync ()
				{
					FindIterable<Document> it = posts.find(
							or(not(exists("orders.visitList")),
							   elemMatch(
									   "orders.visitList",
									   or(ne("userID", userID),
									      and(
											      gt("amount", 0),
											      lt("time", System
													      .currentTimeMillis() - TimeUnit.DAYS
													      .toMillis(1))
									         )
									     )
							            )
							  )
					                                      );
					
					it.batchCursor(
							(r, t) ->
							{
								if (t != null)
								{
									asyncFailed(t);
								}
								else
								{
									asyncCompleted(new AbstractMap
											.SimpleEntry<>(r, System.currentTimeMillis()
									));
								}
							});
					
				}
			}.run();
			bachCursorMap.put(userID, entry);
		}
		entry = bachCursorMap.get(userID);
		final AsyncBatchCursor<Document> cursor = (AsyncBatchCursor<Document>) entry.getKey();
		cursor.setBatchSize(1);
		List<Document> list = new FiberAsync<List<Document>, Throwable>()
		{
			@Override
			protected void requestAsync ()
			{
				cursor.next(
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
						});
				
			}
		}.run();
		if (list == null)
		{
			return null;
		}
		Document doc = list.get(0);
		return doc;
	}
	
	public Document confirmVisit (User from, int postReqID) throws SuspendExecution, Throwable
	{
		return new FiberAsync<Document, Throwable>()
		{
			
			@Override
			protected void requestAsync ()
			{
				posts.updateOne(
						eq("orders.postReqID", postReqID),
						new Document("$inc", new Document("orders.$.visits", 1).append("orders.$.amount", -1))
								.append("$push", new Document
										("orders.$.visitList", new Document("userID", from.getId())
												.append("time", System.currentTimeMillis()))),
						(r, t) ->
						{
							users.findOneAndUpdate(
									eq("userID", from.getId()),
									new Document("$inc", new Document("coins", 1)), new FindOneAndUpdateOptions()
											.returnDocument(ReturnDocument.AFTER),
									(r2, t2) ->
									{
										if (t != null)
										{
											asyncFailed(t);
										}
										else if (t2 != null)
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
		}.run();
		
	}
}
