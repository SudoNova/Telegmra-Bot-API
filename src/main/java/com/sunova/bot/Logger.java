package com.sunova.bot;

import co.paralleluniverse.fibers.TrueThreadLocal;
import co.paralleluniverse.strands.concurrent.ReentrantReadWriteLock;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by HellScre4m on 12/26/2016.
 */
public class Logger
{
	private static final Logger instance;
	private HashMap<Integer, LinkedList<char[]>> loggerMap;
	private static final ThreadLocal<Integer> logID;
	private static final ReentrantReadWriteLock mapLock;
	private final static ThreadLocal<StringBuilder> buffer;
	private final static char[] INFO = "INFO".toCharArray();
	private final static char[] TRACE = "TRACE".toCharArray();
	private final static char[] DEBUG = "DEBUG".toCharArray();
	private final static char[] WARNING = "WARNING".toCharArray();
	private final static char[] ERROR = "ERROR".toCharArray();
	
	private enum LEVEL
	{
		INFO, TRACE, DEBUG, WARNING, ERROR
	}
	
	static
	{
		instance = new Logger();
		logID = new TrueThreadLocal<>();
		buffer = new ThreadLocal<>();
		mapLock = new ReentrantReadWriteLock();
	}
	
	private Logger ()
	{
		loggerMap = new HashMap<>();
	}
	
	public Logger getInstance (int logID)
	{
		Logger.logID.set(logID);
		buffer.set(new StringBuilder());
		return instance;
	}
	
	public void INFO (Object... args)
	{
		log(LEVEL.INFO, args);
	}
	
	public void WARNING (Object... args)
	{
		log(LEVEL.WARNING, args);
	}
	
	public void ERROR (Object... args)
	{
		log(LEVEL.ERROR, args);
	}
	
	public void TRACE (Object... args)
	{
		log(LEVEL.TRACE, args);
	}
	
	public void DEBUG (Object... args)
	{
		log(LEVEL.DEBUG, args);
	}
	
	private void log (LEVEL level, Object... obj)
	{
		StringBuilder builder = buffer.get();
		builder.setLength(0);
		switch (level)
		{
			case INFO:
				builder.append(INFO);
				break;
			case DEBUG:
				builder.append(DEBUG);
				break;
			case WARNING:
				builder.append(WARNING);
				break;
			case ERROR:
				builder.append(ERROR);
				break;
			case TRACE:
				builder.append(TRACE);
		}
		
		for (Object i : obj)
		{
			if (i instanceof String)
			{
				builder.append(((String) i).toCharArray());
			}
			else
			{
				try
				{
					builder.append((String) (i.getClass().getDeclaredMethod(
							"toString", String.class).invoke(
							i, null)));
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
		if (list != null)
		{
			list.add(builder.toString().toCharArray());
		}
		else
		{
			list = new LinkedList<>();
			list.add(builder.toString().toCharArray());
			mapLock.writeLock().lock();
			loggerMap.put(logID.get(), list);
			mapLock.writeLock().lock();
		}
		
	}
	
	public File dump (Integer adminUserID)
	{
		//TODO add body
		throw new UnsupportedOperationException();
	}
	
	public File dump ()
	{
		dump(null);
		throw new UnsupportedOperationException();
	}
}
