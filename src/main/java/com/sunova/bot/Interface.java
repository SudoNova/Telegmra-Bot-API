package com.sunova.bot;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.TrueThreadLocal;
import co.paralleluniverse.strands.Strand;
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
	private static final ThreadLocal<Chat> td = new TrueThreadLocal<>();
	private static ArrayList<Interface> repos;
	
	static
	{
		repos = new ArrayList<>(5);
	}

	User bot;
	Transceiver transceiver;
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
	private ReentrantReadWriteLock requestWaitingLock;
	private ReentrantReadWriteLock updateReposLock;
	private ReentrantReadWriteLock requestReposLock;
	private AtomicInteger nextReqID;
	private Fiber<Void> serverResponseChecker;
	private EventProcessor processor;
	
	
	private Interface ()
	{
		updateRepos = new HashMap<>();
		requestRepos = new HashMap<>();
		requestWaiting = new HashMap<>();
		
		updateReposLock = new ReentrantReadWriteLock();
		requestReposLock = new ReentrantReadWriteLock();
		requestWaitingLock = new ReentrantReadWriteLock();
		
		nextReqID = new AtomicInteger(0);
		processor = new EventProcessor(this);
		//TODO fix null transceiver
		serverResponseChecker = new ResponseChecker().start();
	}
	
	static Interface getInstance (Launcher launcher)
	{
		int serial = launcher.serialNumber;
		if (repos.size() <= serial || repos.get(serial) == null)
		{
			repos.add(serial, new Interface());
		}
		return repos.get(serial);
	}
	
	protected void sendMessage (int updateID, Message message) throws SuspendExecution
	{
		sendMessage(message);
		confirmUpdate(updateID);
	}
	
	public Chat getChatID (String userName) throws SuspendExecution
	{
		List<NameValuePair> list = new ArrayList<>(3);
		list.add(new BasicNameValuePair("chat_id", userName));
		try
		{
			HttpPost post = new HttpPost();
			post.setURI(new URI(Transceiver.getPath() + "getChat"));
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
			int reqID = nextReqID.getAndIncrement();
			requestWaitingLock.writeLock().lock();
			requestWaiting.put(reqID, System.currentTimeMillis());
			requestWaitingLock.writeLock().unlock();
			requestReposLock.writeLock().lock();
			requestRepos.put(reqID, post);
			requestReposLock.writeLock().unlock();
			sendRequest(reqID, post);
			return td.get();
		}
		catch (IOException | URISyntaxException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public Chat getChatUserName (int chatID) throws SuspendExecution
	{
		return getChatID(chatID + "");
	}
	
	protected void sendMessage (Message message) throws SuspendExecution
	{
		try
		{
			List<NameValuePair> list = new ArrayList<>(3);
			list.add(new BasicNameValuePair("chat_id", message.getChat().getId() + ""));
			list.add(new BasicNameValuePair("text", message.getText()));
			ReplyMarkup markup = message.getReply_markup();
			if (markup != null)
			{
				if (markup instanceof ReplyKeyboardMarkup)
				{
					String serializedJson = JsonParser.getInstance().deserializeTObject(markup);
					list.add(new BasicNameValuePair("reply_markup", serializedJson));
				}
			}
//						list.add(new BasicNameValuePair("method", "application/x-www-form-urlencoded"));
			HttpPost post = new HttpPost();
			post.setURI(new URI(Transceiver.getPath() + "sendMessage"));
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
			int reqID = nextReqID.getAndIncrement();
			requestWaitingLock.writeLock().lock();
			requestWaiting.put(reqID, System.currentTimeMillis());
			requestWaitingLock.writeLock().unlock();
			requestReposLock.writeLock().lock();
			requestRepos.put(reqID, post);
			requestReposLock.writeLock().unlock();
			sendRequest(reqID, post);
		}
		catch (IOException | URISyntaxException e)
		{
			e.printStackTrace();
		}
	}
	
	private void sendRequest (int requestID, HttpUriRequest req) throws SuspendExecution
	{
//		System.out.println("Interface sending request to execute");
		transceiver.execute(requestID, req);
	}
	
	protected void processUpdate (Update update)
	{
		int updateID = update.getUpdate_id();
		int index = transceiver.updateIndex.get();
		int currentIndex = Math.max(index, updateID + 1);
		transceiver.updateIndex.compareAndSet(index, currentIndex);
		new Fiber<Void>()
		{
			@Override
			protected Void run () throws InterruptedException, SuspendExecution
			{
				updateReposLock.writeLock().lock();
				updateRepos.put(updateID, update);
				updateReposLock.writeLock().unlock();
				processor.processUpdate(update);
				return null;
			}
		}.start();
		
	}
	
	protected void processUpdates (Result result)
	{
		TObject[] results = result.getResult();
		for (TObject i : results)
		{
			processUpdate((Update) i);
		}
	}
	
	void receiveResult (int requestID, Result resultSet)
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
			else if (object instanceof Chat)
			{
				td.set((Chat) object);
				requestWaitingLock.writeLock().lock();
				requestWaiting.remove(requestID);
				requestWaitingLock.writeLock().unlock();
				requestReposLock.writeLock().lock();
				requestRepos.remove(requestID);
				requestReposLock.writeLock().unlock();
			}
		}
	}
	
	void shutDown () throws SuspendExecution, InterruptedException
	{
		while (!shutDown)
		{
			if (updateRepos.isEmpty() && requestRepos.isEmpty() && requestRepos.isEmpty())
			{
				shutDown = true;
			}
			Strand.sleep(250);
		}
		processor.shutDown();
	}
	
	public void confirmUpdate (int updateID)
	{
		updateReposLock.writeLock().lock();
		updateRepos.remove(updateID);
		updateReposLock.writeLock().unlock();
	}

	private class ResponseChecker extends Fiber<Void>
	{
		@Override
		protected Void run () throws InterruptedException, SuspendExecution
		{
			while (!shutDown)
			{
				sleep(1000);
				requestWaitingLock.readLock().lock();
				Iterator<Map.Entry<Integer, Long>> it = requestWaiting.entrySet().iterator();
				while (it.hasNext())
				{
					Map.Entry<Integer, Long> entry = it.next();
					int id = entry.getKey();
					if (System.currentTimeMillis() - entry.getValue() > 1000)
					{
						requestReposLock.readLock().lock();
						HttpUriRequest request = requestRepos.get(id);
						requestReposLock.readLock().unlock();
						new Fiber<Void>()
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
							requestWaitingLock.readLock().unlock();
							requestWaitingLock.writeLock().lock();
							requestWaiting.put(id, System.currentTimeMillis());
							requestWaitingLock.writeLock().unlock();
							requestWaitingLock.readLock().lock();
						}
					}
					
				}
				requestWaitingLock.readLock().unlock();
			}
			return null;
		}
	}
	
}


