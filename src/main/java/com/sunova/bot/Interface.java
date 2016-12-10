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
	private HashMap<Integer, AbstractMap.SimpleEntry<Update, Long>> updateRepos;
	/**
	 * Repository for outgoing requests
	 */
	private HashMap<HttpUriRequest, Long> requestRepos;
	/**
	 * Keeps track of pending request timeouts
	 */
	private boolean shutDown;
	private ReentrantReadWriteLock updateReposLock;
	private ReentrantReadWriteLock requestReposLock;
	private EventProcessor processor;
	
	
	private Interface ()
	{
		updateRepos = new HashMap<>();
		requestRepos = new HashMap<>();
		
		updateReposLock = new ReentrantReadWriteLock();
		requestReposLock = new ReentrantReadWriteLock();
		
		processor = new EventProcessor(this);
		new Servant().start();
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
			sendRequest(post);
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
	
	protected Result sendMessage (Message message) throws SuspendExecution
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
			return sendRequest(post);
		}
		catch (IOException | URISyntaxException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private Result sendRequest (HttpUriRequest req) throws SuspendExecution
	{
		requestReposLock.writeLock().lock();
		requestRepos.put(req, System.currentTimeMillis());
		requestReposLock.writeLock().unlock();
		Result result = transceiver.execute(req);
		return result;
		//TODO decide
	}
	
	protected void processUpdate (Update update)
	{
		
		new Fiber<Void>()
		{
			@Override
			protected Void run () throws InterruptedException, SuspendExecution
			{
				int updateID = update.getUpdate_id();
				updateReposLock.writeLock().lock();
				if (!updateRepos.containsKey(updateID))
				{
					updateRepos.put(updateID, new AbstractMap.SimpleEntry<>(update, System.currentTimeMillis()));
					updateReposLock.writeLock().unlock();
					int index = transceiver.updateIndex.get();
					int currentIndex = Math.max(index, updateID + 1);
					transceiver.updateIndex.compareAndSet(index, currentIndex);
					processor.processUpdate(update);
				}
				else
				{
					updateReposLock.writeLock().unlock();
				}
				
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
	
	void receiveResult (Result resultSet)
	{
		for (TObject i : resultSet.getResult())
		{
			TObject object = i;
			if (object instanceof Update)
			{
				Update update = (Update) object;
				processUpdate(update);
			}

//			else if (object instanceof Message)
//			{
//				Message message = (Message) object;
//				if (message.getFrom().getId() == bot.getId())
//				{
//
//				}
//			}
			else if (object instanceof Chat)
			{
				td.set((Chat) object);
			}
		}
	}
	
	void shutDown () throws SuspendExecution, InterruptedException
	{
		while (!shutDown)
		{
			if (requestRepos.isEmpty())
			{
				shutDown = true;
			}
			Strand.sleep(250);
		}
		processor.shutDown();
	}
	
	public int forwardMessage (Message message) throws SuspendExecution
	{
		try
		{
			List<NameValuePair> list = new ArrayList<>(3);
			list.add(new BasicNameValuePair("chat_id", message.getChat().getId() + ""));
			list.add(new BasicNameValuePair("from_chat_id", message.getForward_from_chat().getId() + ""));
			list.add(new BasicNameValuePair("message_id", message.getForward_from_message_id() + ""));
//			ReplyMarkup markup = message.getReply_markup();
//			if (markup != null)
//			{
//				if (markup instanceof ReplyKeyboardMarkup)
//				{
//					String serializedJson = JsonParser.getInstance().deserializeTObject(markup);
//					list.add(new BasicNameValuePair("reply_markup", serializedJson));
//				}
//			}
//						list.add(new BasicNameValuePair("method", "application/x-www-form-urlencoded"));
			HttpPost post = new HttpPost();
			post.setURI(new URI(Transceiver.getPath() + "forwardMessage"));
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
			sendRequest(post);
		}
		catch (IOException | URISyntaxException e)
		{
			e.printStackTrace();
		}
		return 0; //TODO add status code
	}
	
	private class Servant extends Fiber<Void>
	{
		@Override
		protected Void run () throws SuspendExecution, InterruptedException
		{
			int waitFactor = 1;
			while (!shutDown)
			{
				sleep(20000 / waitFactor);
				if (updateReposLock.writeLock().tryLock())
				{
					Iterator<Map.Entry<Integer, AbstractMap.SimpleEntry<Update, Long>>> it = updateRepos.entrySet()
							.iterator();
					while (it.hasNext())
					{
						Map.Entry<Integer, AbstractMap.SimpleEntry<Update, Long>> i = it.next();
						AbstractMap.SimpleEntry<Update, Long> j = i.getValue();
						long lastAccessTime = j.getValue();
						long currentTime = System.currentTimeMillis();
						if (currentTime - lastAccessTime > 120000)
						{
							it.remove();
						}
					}
					updateReposLock.writeLock().unlock();
					waitFactor = 1;
				}
				else
				{
					waitFactor++;
				}
			}
			return null;
		}
	}
}


