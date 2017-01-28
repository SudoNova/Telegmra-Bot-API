package com.sunova.botframework;

import co.paralleluniverse.fibers.SuspendExecution;
import org.telegram.objects.Message;

/**
 * Created by HellScre4m on 1/25/2017.
 */
public interface EventHandler
{
	 Message onMessage (Message message) throws SuspendExecution;
}
