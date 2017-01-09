package com.sunova.bot;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.TrueThreadLocal;
import co.paralleluniverse.fibers.io.FiberFileChannel;
import co.paralleluniverse.strands.Strand;
import co.paralleluniverse.strands.concurrent.ReentrantReadWriteLock;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.telegram.objects.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by HellScre4m on 12/26/2016.
 */
public class Logger
{
	private static final ThreadLocal<Long> logID;
	private static final HashMap<Long, LinkedList<char[]>> loggerMap;
	private static final ReentrantReadWriteLock mapLock;
	private final static ThreadLocal<StringBuilder> buffer;
	private final static String INFO = "INFO";
	private final static String TRACE = "TRACE";
	private final static String DEBUG = "DEBUG";
	private final static String WARNING = "WARNING";
	private final static String ERROR = "ERROR";
	private static boolean shutDown;
	
	private enum LEVEL
	{
		INFO, TRACE, DEBUG, WARNING, ERROR
	}
	
	static
	{
		logID = new TrueThreadLocal<>();
		buffer = new ThreadLocal<>();
		mapLock = new ReentrantReadWriteLock();
		loggerMap = new HashMap<>();
	}
	
	private static void getInstance ()
	{
		setAncestor(null);
	}
	
	public static void setAncestor (Long ancestorID)
	{
		Long ID = Strand.currentStrand().getId();
		Logger.logID.set(ID);
		buffer.set(new StringBuilder());
		mapLock.writeLock().lock();
		LinkedList<char[]> list;
		if (ancestorID != null)
		{
			list = loggerMap.get(ancestorID);
			list = new LinkedList<>(list);
		}
		else
		{
			list = new LinkedList<>();
		}
		loggerMap.put(ID, list);
		mapLock.writeLock().unlock();
	}
	
	public static void INFO (Object... args)
	{
		log(LEVEL.INFO, args);
	}
	
	public static void WARNING (Object... args)
	{
		log(LEVEL.WARNING, args);
	}
	
	public static void ERROR (Object... args)
	{
		log(LEVEL.ERROR, args);
	}
	
	public static void TRACE (Object... args)
	{
		log(LEVEL.TRACE, args);
	}
	
	public static void DEBUG (Object... args)
	{
		log(LEVEL.DEBUG, args);
	}
	
	private static void log (LEVEL level, Object... obj)
	{
		if (logID.get() == null)
		{
			setAncestor(null);
		}
		StringBuilder builder = buffer.get();
		builder.setLength(0);
		switch (level)
		{
			case INFO:
				builder.append(INFO + ": ");
				break;
			case DEBUG:
				builder.append(DEBUG + ": ");
				break;
			case WARNING:
				builder.append(WARNING + ": ");
				break;
			case ERROR:
				builder.append(ERROR + ": ");
				break;
			case TRACE:
				builder.append(TRACE + ": ");
		}
		
		for (Object i : obj)
		{
			if (i instanceof String)
			{
				builder.append(((String) i).toCharArray());
			}
			else if (i instanceof TObject)
			{
				try
				{
					JsonParser.getInstance().serialize(i);
				}
				catch (JsonProcessingException e)
				{
					try
					{
						builder.append((String) (i.getClass().getDeclaredMethod(
								"toString", String.class).invoke(
								i, (Object) null)));
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
					}
				}
			}
			else
			{
				try
				{
					builder.append((String) (i.getClass().getDeclaredMethod(
							"toString", String.class).invoke(
							i, (Object) null)));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		mapLock.readLock().lock();
		LinkedList<char[]> list = loggerMap.get(logID.get());
		mapLock.readLock().unlock();
		list.add(builder.toString().toCharArray());
	}
	
	public File dumpAndSend (Long adminUserID, Bot bot) throws SuspendExecution, IOException, Result
	{
		File file = dump(bot);
		Document doc = new Document().setFile(file);
		Message message = new Message().setChat(new Chat().setId(adminUserID)).setDocument(doc);
		message.setCaption(file.getName());
		bot.getInterface().sendMessage(message);
		return file;
	}
	
	public File dump (Bot bot) throws SuspendExecution, IOException
	{
		StringBuilder builder = buffer.get();
		builder.setLength(0);
		long ID = logID.get();
		mapLock.writeLock().lock();
		LinkedList<char[]> list = loggerMap.remove(ID);
		mapLock.writeLock().unlock();
		for (char[] i : list)
		{
			builder.append(i);
		}
		byte[] log = builder.toString().getBytes("UTF-8");
		Path path = FileSystems
				.getDefault()
				.getPath(bot.resourcesPath + "logs\\" + "log-" + System.currentTimeMillis() + ".txt");
		
		FiberFileChannel channel = FiberFileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		ByteBuffer buffer = ByteBuffer.wrap(log);
		try
		{
			channel.write(buffer);
		}
		catch (Throwable throwable)
		{
			if (throwable instanceof IOException)
			{
				throw (IOException) throwable;
			}
			else
			{
				throwable.printStackTrace();
			}
		}
		return path.toFile();
//		return file;
	}
	
	
}
