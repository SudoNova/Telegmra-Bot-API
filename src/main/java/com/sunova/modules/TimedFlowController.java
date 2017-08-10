package com.sunova.modules;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.concurrent.ReentrantReadWriteLock;
import com.sunova.botframework.EventHandler;
import org.telegram.objects.Message;

import java.util.HashMap;

/**
 * Created by HellScre4m on 1/25/2017.
 */
public class TimedFlowController implements EventHandler
{
	private HashMap<Long, Long> map;
	private ReentrantReadWriteLock lock;
	
	public TimedFlowController ()
	{
		map = new HashMap<>();
		lock = new ReentrantReadWriteLock();
	}
	
	@Override
	public Message onMessage (Message message) throws SuspendExecution
	{
		long chatID = message.getChat().getId();
		lock.writeLock().lock();
		Long lastAccessTime = map.get(chatID);
		long currentTime = System.currentTimeMillis();
		if (lastAccessTime != null && currentTime - lastAccessTime < 1000)
		{
			message = null;
		}
		map.put(chatID, currentTime);
		lock.writeLock().unlock();
		return message;
	}
}
