package com.sunova.bot;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import com.sunova.botframework.BotInterface;
import com.sunova.prebuilt.Messages;
import org.telegram.objects.Message;
import org.telegram.objects.Result;
import org.telegram.objects.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HellScre4m on 1/24/2017.
 */
public class MemberEntity
{
	private MongoDBDriver driver;
	private boolean shutDown;
	
	public MemberEntity (MongoDBDriver driver)
	{
		this.driver = driver;
	}
	
	public void enterChannel (Message message, EventProcessor processor) throws SuspendExecution, Result
	{
		if (!message.hasText())
		{
			return;
		}
		BotInterface botInterface = processor.getBotInterface();
		User from = message.getFrom();
		String text = message.getText();
		if (text.startsWith("@"))
		{
			text = text.substring(1);
		}
		else if (text.startsWith("http"))
		{
			if (text.matches("(.)*(/joinchat/)(.)+"))
			{
				message.setText(Messages.CHANNEL_PRIVATE);
				botInterface.sendMessage(message);
				return;
			}
			Pattern pattern = Pattern.compile("((http)s?(://))?((telegram|t)\\.me+/)(?<group>\\w+)/*");
			Matcher matcher = pattern.matcher(text);
			if (matcher.find())
			{
				text = matcher.group("group");
			}
		}
		System.out.println(text);
	}
	
	private class Daemon extends Fiber<Void>
	{
		@Override
		protected Void run () throws SuspendExecution, InterruptedException
		{
			while (!shutDown)
			{
//				List<Document> documents = driver.getPendingChannels();
			}
			return null;
		}
	}
	
}
