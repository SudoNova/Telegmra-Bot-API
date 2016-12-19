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
import java.util.concurrent.atomic.AtomicInteger;

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
	
	AtomicInteger updateIndex;
	Interface botInterface;
	User bot;
	private boolean shutDown;
	private UpdatePuller updatePuller;
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
		updatePuller = new UpdatePuller();
		updateIndex = new AtomicInteger(0);
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
	
	protected Result getResult (HttpResponse response)
	{
		Result result = null;
		byte[] byteValue = getResponseByteArray(response);
		result = parser.parseResult(byteValue);
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
		botInterface.processUpdate(result);
		
	}
	
	protected void getUpdates (int updateIndex) throws SuspendExecution
	{
		HttpGet req = new HttpGet(Transceiver.getPath() + "getUpdates" + (updateIndex == 0 ? "" : "?offset=" +
				updateIndex));
		try
		{
			Result result = getResult(client.execute(req));
			botInterface.processUpdates(result);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	Result execute (HttpUriRequest request) throws SuspendExecution
	{
//		System.out.println("Transceiver executing request");
		try
		{
			HttpResponse response = client.execute(request);
			Result result = getResult(response);
			botInterface.receiveResult(result);
			return result;
		}
//		catch (IOException e)
		catch (Exception e)
		{
			e.printStackTrace();
			Result result = new Result();
			result.setOk(false);
			result.setError_code(-1);
			return result;
		}
	}
	
	boolean disableWebhook () throws SuspendExecution, InterruptedException
	{
		return disableWebhook(1);
	}
	
	boolean disableWebhook (int tryCount) throws SuspendExecution, InterruptedException
	{
		return enableWebhook(null, tryCount);
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
		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
		if (webhookURL != null)
		{
			FileBody fb = new FileBody(launcher.getCertificate());
			entityBuilder.addPart("certificate", fb);
			
		}
		else
		{
			webhookURL = "";
		}
		StringBody sb = new StringBody(webhookURL,
		                               ContentType
				                               .TEXT_PLAIN
		);
		entityBuilder.addPart("url", sb);
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
				success = true;
				if (webhookURL.equals(""))
				{
					if (updatePuller.isInterrupted())
					{
						updatePuller.unpark();
					}
					else if (!updatePuller.isAlive())
					{
						updatePuller.start();
					}
				}
				else if (!updatePuller.isInterrupted() && updatePuller.isAlive())
				{
					updatePuller.interrupt();
				}
				
				
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
	
	void shutDown () throws SuspendExecution, InterruptedException
	{
		disableWebhook();
		shutDown = true;
		botInterface.shutDown();
		try
		{
			client.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private class UpdatePuller extends Fiber<Void>
	{
		@Override
		protected Void run () throws InterruptedException, SuspendExecution
		{
			while (!shutDown)
			{
				sleep(750);
				int updateIndex = Transceiver.this.updateIndex.get();
				getUpdates(updateIndex);
			}
			return null;
		}
	}
}

