package com.sunova.bot;


import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.httpclient.FiberHttpClientBuilder;
import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.telegram.objects.Result;
import org.telegram.objects.Update;
import org.telegram.objects.User;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by HellScre4m on 4/20/2016.
 */
public class Transceiver
{
	private static String path = "https://api.telegram.org/bot<token>/";
	private static CloseableHttpClient client;
	private static ArrayList<Transceiver> repos;
	
	static
	{
		repos = new ArrayList<>(5);
	}
	
	Interface botInterface;
	User bot;
	private Launcher launcher;
	private boolean shutdown = false;
	private JsonParser parser;
	
	private Transceiver (Launcher launcher)
	{
		this.launcher = launcher;
		path = path.replace("<token>", launcher.token);
		parser = JsonParser.getInstance();
		if (client == null)
		{
			client = FiberHttpClientBuilder.create().build();
		}
		
	}
	
	static Transceiver getInstance (Launcher launcher)
	{
		int serial = launcher.serialNumber;
		if (repos.size() <= serial || repos.get(serial) == null)
		{
			repos.add(serial, new Transceiver(launcher));
		}
		
		return repos.get(serial);
	}
	
	public static String getPath ()
	{
		return path;
	}
	
	void init () throws SuspendExecution
	{
		String getIDQuery = path + "getMe";
		final HttpGet req = new HttpGet(getIDQuery);
		
		try
		{
			HttpResponse response = client.execute(req);
			byte[] byteValue = getResponseByteArray(response);
			bot = (User) parser.parseResult(byteValue).getResult()[0];
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private byte[] getResponseByteArray (HttpResponse response)
	{
		try
		{
			return EntityUtils.toByteArray(response.getEntity());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	protected Result getResult (int requestID, HttpResponse response) throws SuspendExecution
	{
		Result result = null;
		try
		{
			byte[] byteValue = EntityUtils.toByteArray(response.getEntity());
//			System.out.println(new String(byteValue));
			result = parser.parseResult(byteValue);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	private Update parseUpdate (HttpMessage message)
	{
		Update result = null;
		try
		{
			byte[] byteValue = null;
			if (message instanceof HttpRequest)
			{
				byteValue = EntityUtils.toByteArray(((HttpEntityEnclosingRequest)
						(message))
						                                    .getEntity());
			}
			else
			{
				byteValue = EntityUtils.toByteArray(((HttpResponse) message).getEntity());
			}
			result = parser.parseUpdate(byteValue);
			return result;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	protected void receiveUpdate (HttpRequest request)
	{
		
		Update result = parseUpdate(request);
		Fiber<Void> fiber = new Fiber<Void>()
		{
			protected Void run () throws InterruptedException, SuspendExecution
			{
//				System.out.println("Transceiver sendig update to Interface");
				botInterface.processUpdate(result);
				return null;
			}
			
		}.start();
		
	}
	
	protected void getUpdates (int updateIndex) throws SuspendExecution
	{
		HttpGet req = new HttpGet(Transceiver.getPath() + "getUpdates" + (updateIndex == 0 ? "" : "?offset=" +
				updateIndex));
		try
		{
			Update update = parseUpdate(client.execute(req));
			botInterface.processUpdate(update);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	protected void execute (int requestID, HttpUriRequest request) throws SuspendExecution
	{
//		System.out.println("Transceiver executing request");
		try
		{
			HttpResponse response = client.execute(request);
			Result result = getResult(requestID, response);
			botInterface.receiveResult(requestID, result);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	boolean disableWebhook () throws SuspendExecution, InterruptedException
	{
		return disableWebhook(1);
	}
	
	boolean disableWebhook (int tryCount) throws SuspendExecution, InterruptedException
	{
		boolean success = enableWebhook(null, tryCount);
		if (success)
		{
			botInterface.setUsingWebhook(false);
		}
		return success;
	}
	
	boolean enableWebhook (String webhookURL) throws SuspendExecution, InterruptedException
	{
		return enableWebhook(webhookURL, 1);
	}
	
	boolean enableWebhook (String webhookURL, int tryCount) throws SuspendExecution, InterruptedException
	{
		boolean success = false;
		int i = 3;
		CloseableHttpResponse response;
		HttpPost webHookInitRequest = null;
		webHookInitRequest = new HttpPost(path + "setWebhook");
		StringBody sb = new StringBody(webhookURL,
		                               ContentType
				                               .TEXT_PLAIN);
		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create().addPart("url", sb);
		if (webhookURL != null)
		{
			FileBody fb = new FileBody(launcher.getCertificate());
			entityBuilder.addPart("certificate", fb);
		}
		HttpEntity entity = entityBuilder.build();
		webHookInitRequest.setEntity(entity);
		while (i-- > 0)
		{
			try
			{
				response = client.execute(webHookInitRequest);
				if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				{
					System.err.println(EntityUtils.toString(response.getEntity()));
					response.close();
					Fiber.sleep(1000);
					continue;
				}
				System.out.println(EntityUtils.toString(response.getEntity()));
				botInterface
						.setUsingWebhook(success = true);
				response.close();
				break;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return success;
	}
}

