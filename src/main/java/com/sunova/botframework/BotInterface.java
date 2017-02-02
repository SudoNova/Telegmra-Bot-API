package com.sunova.botframework;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.FiberFileChannel;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.telegram.objects.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by HellScre4m on 5/11/2016.
 */
public class BotInterface
{
	private static ArrayList<BotInterface> repos;
	private boolean shutDown;
	private UserInterface processor;
	private ArrayList<EventHandler> handlers;
	
	static
	{
		repos = new ArrayList<>(5);
	}
	
	Transceiver transceiver;
	
	private BotInterface (Bot bot)
	{
		transceiver = Transceiver.getInstance(bot);
		processor = bot.userInterface;
		handlers = new ArrayList<>();
		handlers.add(processor);
	}
	
	static BotInterface getInstance (Bot bot)
	{
		int serial = bot.serialNumber;
		if (repos.size() <= serial || repos.get(serial) == null)
		{
			repos.add(serial, new BotInterface(bot));
		}
		return repos.get(serial);
	}
	
	public Chat getChat (String userName) throws SuspendExecution, Result
	{
		List<NameValuePair> list = new ArrayList<>(3);
		list.add(new BasicNameValuePair("chat_id", userName));
		try
		{
			HttpPost post = new HttpPost();
			post.setURI(new URI(Transceiver.getPath() + "getChat"));
			post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
			Result result = sendRequest(post);
			if (!result.isOk())
			{
				throw result;
			}
			else
			{
				return (Chat) result.getResult()[0];
			}
		}
		catch (IOException | URISyntaxException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public Chat getChat (int chatID) throws SuspendExecution, Result
	{
		return getChat(chatID + "");
	}
	
	public Message sendMessage (Message message) throws SuspendExecution, Result
	{
		
		return sendMessage(message, false, false);
	}
	
	public Message sendMessage (Message message, boolean disableNotification, boolean disablePreview) throws
			SuspendExecution, Result
	{
		try
		{
			boolean sendFile = false;
			HttpEntity entity;
			String method = "";
			List<AbstractMap.SimpleEntry<String, Object>> list = new ArrayList<>();
			list.add(new AbstractMap.SimpleEntry<>("chat_id", message.getChat().getId() + ""));
			
			ReplyMarkup markup = message.getReply_markup();
			if (markup != null)
			{
				if (markup instanceof ReplyKeyboardMarkup)
				{
					String serializedJson = JsonParser.getInstance().serialize(markup);
					list.add(new AbstractMap.SimpleEntry<>("reply_markup", serializedJson));
				}
			}
			if (disableNotification)
			{
				list.add(new AbstractMap.SimpleEntry<>("disable_notification", "true"));
			}
			if (disablePreview)
			{
				list.add(new AbstractMap.SimpleEntry<>("disable_web_page_preview", "true"));
			}
			Message replyTo = message.getReply_to_message();
			if (replyTo != null)
			{
				list.add(new AbstractMap.SimpleEntry<>("reply_to_message_id", replyTo.getMessage_id() + ""
				));
			}
			String text = message.getText();
			if (text != null)
			{
				method = "sendMessage";
				list.add(new AbstractMap.SimpleEntry<>("text", message.getText()));
			}
			else
			{
				Document doc = message.getDocument();
				if (doc != null)
				{
					method = "sendDocument";
					File file = doc.getFile();
					if (file != null)
					{
						sendFile = true;
						list.add(new AbstractMap.SimpleEntry<>("document", file));
					}
					else if (doc.getFile_id() != null)
					{
						list.add(new AbstractMap.SimpleEntry<>("document", doc.getFile_id()));
					}
					else if (doc.getFile_path() != null)
					{
						list.add(new AbstractMap.SimpleEntry<>("document", doc.getFile_path()));
					}
					if (message.getCaption() != null)
					{
						list.add(new AbstractMap.SimpleEntry<>("caption", message.getCaption()));
					}
				}
			}
//						list.add(new BasicNameValuePair("method", "application/x-www-form-urlencoded"));
			HttpPost post = new HttpPost();
			post.setURI(new URI(Transceiver.getPath() + method));
			if (sendFile)
			{
				MultipartEntityBuilder builder = MultipartEntityBuilder.create().setLaxMode();
				for (AbstractMap.SimpleEntry<String, Object> i : list)
				{
					String key = i.getKey();
					Object value = i.getValue();
					if (value instanceof String)
					{
						builder.addTextBody(key, (String) value, ContentType.create("text/plain", Consts.UTF_8));
					}
					else
					{
						File file = (File) value;
						Path path = FileSystems.getDefault().getPath(file.getPath());
						try
						{
							FiberFileChannel channel = FiberFileChannel.open(path, StandardOpenOption.READ);
							ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
							
							channel.read(
									buffer);
							builder.addBinaryBody(key, buffer.array(), ContentType.APPLICATION_OCTET_STREAM,
							                      file.getName()
							                     );
//							builder.addBinaryBody(key, file);
						}
						catch (Throwable throwable)
						{
							if (throwable instanceof IOException)
							{
								throw (IOException) throwable;
							}
							throwable.printStackTrace();
						}
					}
				}
				entity = builder.setCharset(Charset.forName("UTF-8")).build();
			}
			else
			{
				ArrayList<BasicNameValuePair> newList = new ArrayList<>(list.size());
				for (AbstractMap.SimpleEntry<String, Object> i : list)
				{
					newList.add(new BasicNameValuePair(i.getKey(), (String) i.getValue()));
				}
				entity = new UrlEncodedFormEntity(newList, "UTF-8");
			}
			post.setEntity(entity);
			return (Message) sendRequest(post).getResult()[0];
		}
		catch (IOException | URISyntaxException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private Result sendRequest (HttpUriRequest req) throws SuspendExecution, Result
	{
		while (true)
		{
			Result result = transceiver.execute(req);
			if (result.isOk())
			{
				return result;
			}
			if (result.getError_code() == 429)
			{
				System.err.println(result.getDescription());
//				System.err.println("Your bot is hitting limits. Slow down!");
				//TODO add method
				try
				{
					Fiber.sleep(25);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				continue;
			}
			throw result;
		}
		//TODO handle API framework side errors
	}
	
	protected void processUpdate (Update update)
	{
		new Fiber<Void>()
		{
			@Override
			protected Void run () throws InterruptedException, SuspendExecution
			{
				int updateID = update.getUpdate_id();
				int index = transceiver.updateIndex.get();
				int currentIndex = Math.max(index, updateID + 1);
				transceiver.updateIndex.compareAndSet(index, currentIndex);
				if (update.containsMessage())
				{
					Message message = update.getMessage();
					for (EventHandler i : handlers)
					{
						message = i.onMessage(message);
						if (message == null)
						{
							break;
						}
					}
				}
				return null;
			}
		}.start();
		
	}
	
	public void registerHandler (EventHandler handler)
	{
		handlers.add(0, handler);
	}
	
	public void removeHandler (EventHandler handler)
	{
		handlers.remove(handler);
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
		if (!resultSet.isOk())
		{
			System.err.println(resultSet.getDescription());
			return;
		}
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
		}
	}
	
	void shutDown () throws SuspendExecution, InterruptedException
	{
		processor.shutDown();
	}
	
	public Message forwardMessage (Message message) throws SuspendExecution, Result
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
//					String serializedJson = JsonParser.getInstance().serializeTObject(markup);
//					list.add(new BasicNameValuePair("reply_markup", serializedJson));
//				}
//			}
//						list.add(new BasicNameValuePair("method", "application/x-www-form-urlencoded"));
			HttpPost post = new HttpPost();
			post.setURI(new URI(Transceiver.getPath() + "forwardMessage"));
//			post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
			return (Message) sendRequest(post).getResult()[0];
		}
		catch (IOException | URISyntaxException e)
		{
			e.printStackTrace();
		}
		return null; //TODO add status code
	}
	
	public User getMe () throws SuspendExecution, Result
	{
		try
		{
			HttpPost post = new HttpPost();
			post.setURI(new URI(Transceiver.getPath() + "getMe"));
			return (User) sendRequest(post).getResult()[0];
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public ChatMember[] getChatAdministrators (long chatID) throws SuspendExecution, Result
	{
		return getChatAdministrators(chatID + "");
	}
	
	public ChatMember[] getChatAdministrators (String chatID) throws SuspendExecution, Result
	{
		List<NameValuePair> list = new ArrayList<>(3);
		list.add(new BasicNameValuePair("chat_id", chatID));
		try
		{
			HttpPost post = new HttpPost();
			post.setURI(new URI(Transceiver.getPath() + "getChatAdministrators"));
			post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
			Result result = sendRequest(post);
			if (!result.isOk())
			{
				throw result;
			}
			else
			{
				TObject[] results = result.getResult();
				return Arrays.copyOf(results, results.length, ChatMember[].class);
			}
		}
		catch (IOException | URISyntaxException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}


