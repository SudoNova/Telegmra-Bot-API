package com.sunova.bot;

import co.paralleluniverse.fibers.FiberAsync;
import co.paralleluniverse.fibers.SuspendExecution;
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
import java.util.LinkedList;
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
	private HashMap<Integer, AbstractMap.SimpleEntry<List<Document>, Long>> postListMap;
	
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
									new Document("userID", 1),
									new IndexOptions().background(true).unique(true), (r1,
									                                                   t1) ->
									{
										if (t1 != null)
										{
											asyncFailed(t1);
										}
										posts.createIndex(
												new Document("chatID", 1).append("messageID", 1),
												new IndexOptions().background(true).unique(true)
												, (r2,
												   t2) ->
												{
													if (t2 != null)
													{
														asyncFailed(t2);
													}
													channels.createIndex(
															new Document("chatID", 1),
															new IndexOptions().background(true)
																	.unique(true), (r3,
															                        t3) ->
															{
																if (t3 != null)
																{
																	asyncFailed(t3);
																}
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
																			if (t4 != null)
																			{
																				asyncFailed(t4);
																			}
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
																						if (t5 != null)
																						{
																							asyncFailed(t5);
																						}
																						posts.createIndex(
																								new Document(
																										"orders.ownerID",
																										1
																								).append
																										(
																												"orders.remaining",
																												1
																										)
																										.append(
																												"errorCount",
																												1
																										       )
																								,
																								new
																										IndexOptions()
																										.background(
																												true),
																								(r6, t6) ->
																								{
																									if (t6 != null)
																									{
																										asyncFailed(t6);
																									}
																									else
																									{
																										posts.createIndex(
																												new Document
																														(
																																"visits.userID",
																																1
																														)
																														.append(
																																"visits.time",
																																1
																														       ),
																												new
																														IndexOptions()
																														.background(
																																true),
																												(r7, t7) ->
																												{
																													if (t7 !=
																															null)
																													{
																														asyncFailed(
																																t7);
																													}
																													asyncCompleted(
																															r7);
																												}
																										                 );
																										
																									}
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
	
	//TODO fix
	public void errorSendingPost (long chatID, int messageID) throws SuspendExecution, Throwable
	{
		new FiberAsync<Document, Throwable>()
		{
			
			@Override
			protected void requestAsync ()
			{
				posts.findOneAndUpdate(
						and(eq("chatID", chatID), eq("messageID", messageID)), new Document("$inc", new Document
								("errorCount", 1)), new FindOneAndUpdateOptions().upsert(true),
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
		};
		
	}
	
	public void insertNewPostViewOrder (User user, long chatID, int messageID, int amount)
			throws SuspendExecution, Throwable
	{
		new FiberAsync<Document, Throwable>()
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
						(result, t) ->
						{
							int postReqId = result.getInteger("postReqID");
							long time = System.currentTimeMillis();
							Document newDoc = new Document("postReqID", postReqId)
									.append("ownerID", user.getId())
									.append("time", time)
									.append("amount", amount)
									.append("remaining", amount)
									.append("viewCount", 0);
							posts.updateOne(
									and(eq("chatID", chatID), eq("messageID", messageID)),
									new Document("$push", new Document("orders", newDoc))
											.append("$set", new Document("errorCount", 0)),
									new UpdateOptions().upsert
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
				                     );
			}
		}.run();
		updateUser(user, new Document("$inc", new Document("coins", -amount * EventProcessor.visitFactor)));
	}
	
	public Document nextPost (int userID) throws SuspendExecution, Throwable
	{
		AbstractMap.SimpleEntry<List<Document>, Long> entry = postListMap.get(userID);
		if (entry == null ||
				System.currentTimeMillis() - entry.getValue() > TimeUnit.MINUTES.toMillis(10)
				|| entry.getKey().isEmpty())
		
		{
			entry = new FiberAsync<AbstractMap.SimpleEntry<List<Document>, Long>,
					Throwable>()
			{
				
				@Override
				protected void requestAsync ()
				{
					FindIterable<Document> it = posts.find(
							and(
									elemMatch("orders", gt("remaining", 0)),
									or(
											nin("visits.userID", userID),
											elemMatch("visits",
											          and(
													          eq("userID", userID),
													          lt("time",
													             System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)
													            )
											
											             )
											         )
									  )
							
							
							   ));//.projection(Projections.elemMatch("orders"));
					
					LinkedList<Document> list = new LinkedList<>();
					it.into(list,
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
					        }
					       );
				}
			}.run();
			postListMap.put(userID, entry);
		}
		entry = postListMap.get(userID);
		LinkedList<Document> list = (LinkedList<Document>) entry.getKey();
		if (list == null || list.isEmpty())
		{
			return null;
		}
		return list.pollFirst();
	}
	
	public Document confirmVisit (User from, long chatID, int messageID, int postReqID, boolean upsert) throws
			SuspendExecution,
			Throwable
	{
		return new FiberAsync<Document, Throwable>()
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
						filter,
						doc,
						(r, t) ->
						{
							if (t != null)
							{
								asyncFailed(t);
							}
							users.findOneAndUpdate(
									eq("userID", from.getId()),
									new Document("$inc", new Document("coins", 1)),
									new FindOneAndUpdateOptions()
											.returnDocument(ReturnDocument.AFTER),
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
}
