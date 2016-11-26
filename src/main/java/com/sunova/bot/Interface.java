package com.sunova.bot;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.concurrent.ReentrantReadWriteLock;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.telegram.objects.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by HellScre4m on 5/11/2016.
 */
public class Interface
{
	/**
	 * Repository for incoming updates
	 */
	private HashMap<Integer, Update> updateRepos;
	/**
	 * Repository for outgoing requests
	 */
	private HashMap<Integer, HttpUriRequest> requestRepos;
	/**
	 * Keeps track of pending request timeouts
	 */
	private HashMap<Integer, Long> requestWaiting;
	private boolean shutDown;
	private boolean usingWebHook;
	private ReentrantReadWriteLock requestWaitingLock;
	private ReentrantReadWriteLock updateReposLock;
	private ReentrantReadWriteLock requestReposLock;
	private AtomicInteger nextReqID;
	private Fiber<Void> confirmedCleaner;
	private Fiber<Void> clientResponseChecker;
	private Fiber<Void> serverResponseChecker;
	private Fiber<Void> pollingUpdateChecker;
	private EventProcessor processor;
	private AtomicInteger updateIndex;
	private User bot;
	private Transceiver transceiver;
	
	protected Interface (Transceiver transceiver, User bot, boolean isUsingWebhook)
	{
		this.transceiver = transceiver;
		this.bot = bot;
		
		updateRepos = new HashMap<>();
		requestRepos = new HashMap<>();
		requestWaiting = new HashMap<>();
		
		updateReposLock = new ReentrantReadWriteLock();
		requestReposLock = new ReentrantReadWriteLock();
		requestWaitingLock = new ReentrantReadWriteLock();
		
		nextReqID = new AtomicInteger(0);
		updateIndex = new AtomicInteger(0);
		processor = new EventProcessor(this);
		serverResponseChecker = new Fiber<Void>()
		{
			@Override
			protected Void run () throws InterruptedException, SuspendExecution
			{
				while (!shutDown)
				{
					sleep(1000);
					requestWaitingLock.readLock().lock();
//					System.out.println("lock");
					Iterator<Map.Entry<Integer, Long>> it = requestWaiting.entrySet().iterator();
					while (it.hasNext())
					{
						Map.Entry<Integer, Long> entry = it.next();
						int id = entry.getKey();
						if (System.currentTimeMillis() - entry.getValue() > 1000)
						{
//							System.out.println(requestReposLock.getReadLockCount());
							requestReposLock.readLock().lock();
							HttpUriRequest request = requestRepos.get(id);
							requestReposLock.readLock().unlock();
							Fiber<Void> exec = new Fiber<Void>()
							{
								@Override
								protected Void run () throws InterruptedException, SuspendExecution
								{
									transceiver.execute(id, request);
									return null;
								}
							}.start();
							if (!requestWaitingLock.isWriteLockedByCurrentStrand())
							{
								//TODO come back here
								requestWaitingLock.readLock().unlock();
//								System.out.println(requestWaitingLock.getReadHoldCount());
//								System.out.println(requestWaitingLock.getReadLockCount());
//								System.out.println(requestWaitingLock.isWriteLocked());
								requestWaitingLock.writeLock().lock();
//								System.out.println("done");
								requestWaitingLock.readLock().lock();
							}
							requestWaiting.put(id, System.currentTimeMillis());
						}
						if (requestWaitingLock.isWriteLockedByCurrentStrand())
						{
							requestWaitingLock.writeLock().unlock();
						}
					}
					requestWaitingLock.readLock().unlock();
//					System.out.println("unlock");
				}
				return null;
			}
		}.start();
		pollingUpdateChecker = new Fiber<Void>()
		{
			@Override
			protected Void run () throws InterruptedException, SuspendExecution
			{
				do
				{
					int updateIndex = Interface.this.updateIndex.get();
					transceiver.getUpdates(updateIndex);
					sleep(500);
				}
				while (!shutDown);
				
				return null;
			}
		};
		if (!isUsingWebhook)
		{
			pollingUpdateChecker.start();
		}
		setUsingWebhook(isUsingWebhook);
	}
	
	public boolean isUsingWebHook ()
	{
		return usingWebHook;
	}
	
	protected void sendMesssage (int updateId, int chatID, String text) throws SuspendExecution
	{
		updateReposLock.writeLock().lock();
		updateRepos.remove(updateId);
		updateReposLock.writeLock().unlock();
		sendMessage(chatID, text);
	}
	
	protected void sendMessage (int chatID, String text) throws SuspendExecution
	{
		try
		{
			List<NameValuePair> list = new ArrayList<>(3);
			list.add(new BasicNameValuePair("chat_id", chatID + ""));
			list.add(new BasicNameValuePair("text", text));
//						list.add(new BasicNameValuePair("method", "application/x-www-form-urlencoded"));
			HttpPost post = new HttpPost();
			post.setURI(new URI(Transceiver.getPath() + "sendMessage"));
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
			int reqID = nextReqID.getAndIncrement();
			System.out.println(requestWaitingLock.getReadLockCount());
			requestWaitingLock.writeLock().lock();
			requestWaiting.put(reqID, System.currentTimeMillis());
			requestWaitingLock.writeLock().unlock();
			requestReposLock.writeLock().lock();
			requestRepos.put(reqID, post);
			requestReposLock.writeLock().unlock();
			sendRequest(reqID, post);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
	}
	
	protected void sendRequest (int requestID, HttpUriRequest req) throws SuspendExecution
	{
		System.out.println("Interface sending request to execute");
		transceiver.execute(requestID, req);
	}
	
	protected void processUpdate (Update update) throws SuspendExecution
	{
		System.out.println("Interface processing update");
		int updateID = update.getUpdate_id();
//		System.out.println(updateWaitingLock.getReadLockCount());
		updateReposLock.writeLock().lock();
//		System.out.println("Update repos write lock acquired");
		updateRepos.put(updateID, update);
		updateReposLock.writeLock().unlock();
		processor.processUpdate(update);
		updateIndex.compareAndSet(updateID, updateID + 1);
		int index = updateIndex.get();
		int currentIndex = Math.max(index, updateID + 1);
		updateIndex.compareAndSet(index, currentIndex);
		
	}
	
	protected void receiveResult (int requestID, Result resultSet) throws SuspendExecution
	{
		for (TObject i : resultSet.getResult())
		{
			TObject object = i;
			if (object instanceof Update)
			{
				Update update = (Update) object;
				processUpdate(update);
			}
			else if (object instanceof Message)
			{
				Message message = (Message) object;
				if (message.getFrom().getId() == bot.getId())
				{
					requestWaitingLock.writeLock().lock();
					requestWaiting.remove(requestID);
					requestWaitingLock.writeLock().unlock();
					requestReposLock.writeLock().lock();
					requestRepos.remove(requestID);
					requestReposLock.writeLock().unlock();
				}
			}
		}
	}
	
	protected void setUsingWebhook (boolean isUsingWebhook)
	{
		if (!isUsingWebhook)
		{
			pollingUpdateChecker.start();
		}
		else
		{
			pollingUpdateChecker.interrupt();
		}
		usingWebHook = isUsingWebhook;
	}
	
}


