package com.sunova.botframework;

import co.paralleluniverse.fibers.SuspendExecution;
import org.telegram.objects.Message;

/**
 * Created by HellScre4m on 1/10/2017.
 */
public abstract class UserInterface
{
	protected BotInterface botInterface;
	protected Bot bot;
	
	void init (Bot bot)
	{
		this.bot = bot;
		this.botInterface = bot.getInterface();
	}
	
	public BotInterface getBotInterface ()
	{
		return botInterface;
	}
	
	public Bot getBot ()
	{
		return bot;
	}
	
	public abstract void onMessage (Message message) throws SuspendExecution;
	
	protected abstract void shutDown () throws SuspendExecution;
	
}
