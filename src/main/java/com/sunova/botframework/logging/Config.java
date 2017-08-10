package com.sunova.botframework.logging;

/**
 * Created by HellScre4m on 2/26/2017.
 */
public class Config
{
	public static void test ()
	{
		System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
		System.setProperty("log4j2.garbagefree.threadContextMap", "true");
//		Appender

//		LoggerConfig
	}
}
