package com.sunova.bot;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberAsync;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import co.paralleluniverse.strands.concurrent.ReentrantReadWriteLock;
import com.mongodb.MongoCursorNotFoundException;
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
import com.sunova.botframework.Logger;
import com.sunova.prebuilt.States;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.telegram.objects.User;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Aggregates.*;
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
	private HashMap<Integer, AsyncBatchCursor<Document>> batchMap;
	private ReentrantReadWriteLock batchMapLock;
	private boolean shutDown;
	
	public MongoDBDriver ()
	{
		dbClient = MongoClients.create();
		MongoDatabase db = dbClient.getDatabase("tgAdmins");
		users = db.getCollection("users");
		channels = db.getCollection("channels");
		posts = db.getCollection("posts");
		misc = db.getCollection("misc");
		batchMap = new HashMap<>();
		batchMapLock = new ReentrantReadWriteLock();
		ensureIndexes();
		new Cleaner().start();
	}
	
	private void ensureIndexes ()
	{
		new Fiber<Void>()
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
									new IndexOptions().unique(true),
									(r1, t1) ->
									{
										if (t1 != null)
										{
											asyncFailed(t1);
										}
									}
							                 );
							posts.createIndex(
									new Document("refChatID", 1).append("refMessageID", 1),
									new IndexOptions().unique(true).background(true),
									(r2, t2) ->
									{
										if (t2 != null)
										{
											asyncFailed(t2);
										}
									}
							                 );
							posts.createIndex(new Document("messageID", 1),
							                  new IndexOptions().unique(true).background(true),
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
									new IndexOptions().unique(true),
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
									new IndexOptions().unique(true),
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
									new IndexOptions().unique(true),
									(r5, t5) ->
									{
										if (t5 != null)
										{
											asyncFailed(t5);
										}
									}
							                    );
							posts.createIndex(
									new Document("orders.ownerID", 1).append("orders.remaining", -1)
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
									new Document("visits.userID", 1).append("visits.date", 1),
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
					}.
							
							run();
				}
				catch (Throwable throwable)
				{
					throwable.printStackTrace();
				}
				return null;
			}
		}.
				
				start();
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
		int userID = from.getId();
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
				ObjectId prev_id = (ObjectId) newDoc.get("_id");
				int prevUserID = newDoc.getInteger("userID");
				newDoc = new FiberAsync<Document, MongoException>()
				{
					@Override
					protected void requestAsync ()
					{
						users.findOneAndDelete(
								eq("userID", userID), (r, t) ->
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
								eq("_id", prev_id),
								new Document("$set", new Document("userID", userID)
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
					Document doc;
					try
					{
						do
						{
							doc = new FiberAsync<Document, MongoException>()
							{
								@Override
								protected void requestAsync ()
								{
									posts.findOneAndUpdate(
											new Document("orders.ownerID", prevUserID),
											new Document("$set", new Document("orders.$.ownerID", userID)),
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
						while (doc != null);
					}
					catch (MongoException e)
					{
						Logger.ERROR(e);
						Logger.DEBUG(Arrays.toString(Strand.currentStrand().getStackTrace()));
						Logger.TRACE(from);
						e.printStackTrace();
						Logger.makeDumpable();
					}
					return true;
				}
				else
				{
					throw new MongoException("Unable to update previous ID");
				}
			}
			updateUser(from.getId(), new Document("$set", new Document("phoneNumber", phoneNumber)
					.append("state", States.MAIN_MENU)).append("$unset", new Document("previous_state", "")));
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
												)
														.append("$addToSet", new Document("referrals", from.getId())),
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
	
	public Document updateUser (int userID, Document doc) throws SuspendExecution, MongoException
	{
		try
		{
			return new FiberAsync<Document, MongoException>()
			{
				@Override
				protected void requestAsync ()
				{
					users.findOneAndUpdate(
							eq("userID", userID), doc,
							new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER),
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
			return null;
		}
	}
	
	public Document insertUser (User user) throws SuspendExecution, MongoException
	{
		return insertUser(user, null);
	}
	
	public Document insertUser (User user, Integer referrerID) throws SuspendExecution, MongoException
	{
		Document doc = new Document();
		doc.append("userID", user.getId()).append("type", User.FREE_USER).append("coins", 20).append("state", 1);
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
	public void errorSendingPost (int messageID) throws SuspendExecution, MongoException
	{
		try
		{
			new FiberAsync<Document, MongoException>()
			{
				
				@Override
				protected void requestAsync ()
				{
					posts.findOneAndUpdate(
							new Document("messageID", messageID),
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
	
	public Document findByRef (long refChatID, int refMessageID) throws SuspendExecution, MongoException
	{
		try
		{
			return new FiberAsync<Document, MongoException>()
			{
				
				@Override
				protected void requestAsync ()
				{
					posts.find(new Document("refMessageID", refMessageID).append("refChatID", refChatID))
							.first((r, t)
									       ->
							       {
								       if (t != null)
								       {
									       asyncFailed(t);
								       }
								       asyncCompleted(r);
							       });
				}
			}.run();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public Document registerPost (int userID, int messageID, Long refChatID, Integer refMessageID, int amount)
			throws SuspendExecution, MongoException
	{
		try
		{
			new FiberAsync<Void, MongoException>()
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
								Document push = new Document("postReqID", postReqId)
										.append("ownerID", userID).append("startDate", time)
										.append("amount", amount).append("remaining", amount)
										.append("viewCount", 0).append("endDate", time);
								Document set = new Document("errorCount", 0);
								posts.updateOne(
										and(new Document("refChatID", refChatID),
										    new Document("refMessageID", refMessageID)
										   ),
										new Document("$push", new Document("orders", push))
												.append("$set", set), (r2, t2) ->
										{
											if (t2 != null)
											{
												asyncFailed(t2);
											}
											else
											{
												if (r2.getModifiedCount() == 0)
												{
													set.append("refChatID", refChatID)
															.append("refMessageID", refMessageID);
													posts.updateOne(
															new Document("messageID", messageID),
															new Document("$push", new Document("orders", push))
																	.append("$set", set),
															new UpdateOptions().upsert(true),
															(r3, t3) ->
															{
																if (t3 != null)
																{
																	asyncFailed(t3);
																}
																asyncCompleted(null);
															}
													               );
												}
												else
												{
													asyncCompleted(null);
												}
											}
										}
								               );
								
							}
					                     );
				}
			}.run();
			return updateUser(userID, new Document("$inc", new Document("coins", -amount * ViewEntity.visitFactor)));
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	void closeCursor (int userID) throws SuspendExecution
	{
		batchMapLock.writeLock().lock();
		try
		{
			batchMap.remove(userID).close();
		}
		catch (NullPointerException e)
		{
			
		}
		batchMapLock.writeLock().unlock();
	}
	
	public Document nextPost (int userID) throws SuspendExecution, MongoException
	{
		batchMapLock.readLock().lock();
		AsyncBatchCursor<Document> cursor = batchMap.get(userID);
		batchMapLock.readLock().unlock();
		try
		{
			if (cursor == null || cursor.isClosed())
			{
				cursor = new FiberAsync<AsyncBatchCursor<Document>, MongoException>()
				{
					@Override
					protected void requestAsync ()
					{
						posts.find(and(
								lt("errorCount", 10),
								elemMatch("orders", and(
										gt("remaining", 0),
										ne("ownerID", userID)
								                       )),
								or(nin("visits.userID", userID),
								   elemMatch("visits",
								             and(eq("userID", userID),
								                 lt("date",
								                    System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)
								                   )
								
								                )
								            )
								  )
						              ))
								.projection(
										new Document("orders.$", 1).append("messageID", 1)).batchSize(1).batchCursor(
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
				batchMapLock.writeLock().lock();
				batchMap.put(userID, cursor);
				batchMapLock.writeLock().unlock();
			}
			return getNextCursorItem(userID, cursor);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public Document nextChannel (int userID) throws SuspendExecution, MongoException
	{
		batchMapLock.readLock().lock();
		AsyncBatchCursor<Document> cursor = batchMap.get(userID);
		batchMapLock.readLock().unlock();
		try
		{
			if (cursor == null || cursor.isClosed())
			{
				cursor = new FiberAsync<AsyncBatchCursor<Document>, MongoException>()
				{
					@Override
					protected void requestAsync ()
					{
						channels.find(and(
								elemMatch("orders", and(
										gt("remaining", 0),
										ne("ownerID", userID)
								                       )),
								or(nin("members.userID", userID),
								   elemMatch("members",
								             and(eq("userID", userID),
								                 or(lte("remaining", 0)
										                 , eq("exited", true))
								                )
								            )
								  )
						                 )).batchSize(1).batchCursor( //FIXME this is incorrect requires projection
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
				batchMapLock.writeLock().lock();
				batchMap.put(userID, cursor);
				batchMapLock.writeLock().unlock();
			}
			return getNextCursorItem(userID, cursor);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private Document getNextCursorItem (int userID, final AsyncBatchCursor<Document> cursor)
			throws SuspendExecution, InterruptedException
	{
		try
		{
			Document result = new FiberAsync<Document, MongoException>()
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
		catch (MongoCursorNotFoundException e)
		{
			batchMapLock.writeLock().lock();
			batchMap.remove(userID);
			batchMapLock.writeLock().unlock();
			return nextPost(userID);
		}
	}
	
	public List<Document> nextViewOrderList (int userID, int skip) throws SuspendExecution, MongoException
	{
		
		try
		{
			return new FiberAsync<List<Document>, MongoException>()
			{
				@Override
				protected void requestAsync ()
				{
					
					ArrayList<Bson> list = new ArrayList<>(6);
					list.add(match(eq("orders.ownerID", userID)));
					list.add(project(new Document("_id", 0).append("orders", 1)
							                 .append("messageID", 1)));
					list.add(unwind("$orders"));
					list.add(sort(new Document("orders.startDate", -1)));
					if (skip != 0)
					{
						list.add(new Document("$skip", skip));
					}
					list.add(limit(11));
					posts.aggregate(list)
							.batchCursor(
									(r, t) ->
									{
										if (t != null)
										{
											asyncFailed(t);
										}
										else
										{
											r.next((r1, t1) ->
											       {
												       if (t1 != null)
												       {
													       asyncFailed(t1);
												       }
												       else
												       {
													       asyncCompleted(r1);
												       }
											       });
										}
									});
				}
			}.run();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public Document confirmVisit (int userID, int postReqID)
			throws SuspendExecution, MongoException
	{
		try
		{
			return new FiberAsync<Document, MongoException>()
			{
				@Override
				protected void requestAsync ()
				{
					long currentTime = System.currentTimeMillis();
					posts.findOneAndUpdate(
							new Document("orders.postReqID", postReqID),
							new Document("$inc", new Document("orders.$.viewCount", 1).append("orders.$.remaining", -1)
							).append("$set", new Document("orders.$.endDate", currentTime)),
							(r, t) ->
							{
								if (t != null)
								{
									t.fillInStackTrace();
									t.printStackTrace();
									System.err.println(new Date(currentTime) + ": " + userID + " " + postReqID);
								}
								else
								{
									ObjectId id = r.getObjectId("_id");
									posts.findOneAndUpdate(
											new Document("_id", id).append("visits.userID", userID),
											new Document("$set", new Document("visits.$.date", currentTime)),
											(r2, t2) ->
											{
												if (t2 != null)
												{
													t2.fillInStackTrace();
													t2.printStackTrace();
													System.err.println(
															new Date(currentTime) + ": " + userID + " " + postReqID);
												}
												else if (r2 == null)
												{
													posts.findOneAndUpdate(
															new Document("_id", id),
															new Document(
																	"$push", new Document(
																	"visits", new Document("userID", userID)
																	.append("date", currentTime)
															)),
															(r3, t3) ->
															{
																if (t3 != null)
																{
																	t3.fillInStackTrace();
																	t3.printStackTrace();
																	System.err.println(
																			new Date(currentTime) + ": " +
																					userID + " " + postReqID);
																}
															}
													                      );
												}
											}
									                      );
								}
								users.findOneAndUpdate(
										eq("userID", userID),
										new Document("$inc", new Document("coins", 1)),
										new FindOneAndUpdateOptions()
												.returnDocument(ReturnDocument.AFTER),
										(r4, t4) ->
										{
											
											if (t4 != null)
											{
												asyncFailed(t4);
											}
											else
											{
												asyncCompleted(r4);
											}
										}
								                      );
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
	
	public Document registerChannel (int days, int persons, String userName, long channelID, String description,
	                                 int ownerID) throws SuspendExecution, MongoException
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
							new Document("$inc", new Document("channelReqID", 1)),
							new FindOneAndUpdateOptions().upsert(true)
									.returnDocument(ReturnDocument.AFTER),
							(result, t) ->
							{
								int channelReqID = result.getInteger("channelReqID");
								long time = System.currentTimeMillis();
								Document newDoc = new Document("channelReqID", channelReqID)
										.append("ownerID", ownerID).append("startDate", time)
										.append("persons", persons).append("days", days)
										.append("remaining", days * persons)
										.append("description", description).append("entered", 0)
										.append("left", 0).append("endDate", time);
								channels.updateOne(
										new Document("chatID", channelID),
										new Document("$push", new Document("orders", newDoc))
												.append("$set", new Document("warned", false)
														.append("userName", userName)),
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
			return updateUser(
					ownerID, new Document("$inc", new Document("coins", -days * persons * MemberEntity
							.memberFactor)));
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private class Cleaner extends Fiber<Void>
	{
		
		@Override
		protected Void run () throws SuspendExecution, InterruptedException
		{
			while (!shutDown)
			{
				sleep(100000);
				batchMapLock.writeLock().lock();
				Iterator<Integer> it = batchMap.keySet().iterator();
				while (it.hasNext())
				{
					int i = it.next();
					AsyncBatchCursor<Document> v = batchMap.get(i);
					if (v == null || v.isClosed())
					{
						it.remove();
					}
				}
				batchMapLock.writeLock().unlock();
			}
			return null;
		}
	}
	
}