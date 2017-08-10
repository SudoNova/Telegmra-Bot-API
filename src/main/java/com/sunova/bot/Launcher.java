package com.sunova.bot;

import co.paralleluniverse.fibers.SuspendExecution;
import com.sunova.botframework.Bot;
import com.sunova.modules.LockFlowController;
import com.sunova.modules.TimedFlowController;

import java.io.IOException;

/**
 * Created by HellScre4m on 1/10/2017.
 */
public class Launcher
{
	public static void main (String[] args) throws SuspendExecution, IOException
	{
//		System.setProperty("Dco.paralleluniverse.fibers.verifyInstrumentation", "true");
//		System.setProperty("Dhttps.protocols", "TLSv1.1,TLSv1.2");
		Bot instance = Bot.createInstance();
		String token = "309177874:AAFdSl9F6P9jZZNoMdhuAprCOXbx_pRepXs";
		String domainAddress = "sunova.dynu.com";
		EventProcessor processor = new EventProcessor();
		LockFlowController lockFlowController = new LockFlowController();
		processor.flowController = lockFlowController;
		instance.setBotToken(token).setDomainAddress(domainAddress)
				.setUserInterface(processor).build().getInterface()
				.registerHandler(new TimedFlowController()).registerHandler(lockFlowController);
		
	}
}
