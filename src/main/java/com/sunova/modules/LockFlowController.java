package com.sunova.modules;

import co.paralleluniverse.fibers.SuspendExecution;
import com.sunova.botframework.EventHandler;
import org.telegram.objects.Message;

import java.util.HashMap;

/**
 * Created by HellScre4m on 1/25/2017.
 */
public class LockFlowController implements EventHandler
{
	private HashMap<Long, Boolean> map;
	
	public LockFlowController ()
	{
		map = new HashMap<>();
	}
	
	@Override
	public Message onMessage (Message message) throws SuspendExecution
	{
		long id = message.getFrom().getId();
		Boolean locked = map.get(id);
		if (locked == null)
		{
			unlock(id);
		}
		else if (locked)
		{
			return null;
		}
		return message;
	}
	
	public void lock (long id)
	{
		map.put(id, true);
	}
	
	public void unlock (long id)
	{
		map.put(id, false);
	}
}
