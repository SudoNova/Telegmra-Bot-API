package com.sunova.bot;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.httpclient.FiberHttpClientBuilder;
import co.paralleluniverse.strands.Strand;
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

/**
 * Created by HellScre4m on 4/20/2016.
 */
public class Transceiver
{
	private static String path = "https://api.telegram.org/bot<token>/";
	private CloseableHttpClient client;
	private boolean shutdown = false;
	private JsonParser parser;
	private Interface botInterface;
	private WebHook webHook;
	private boolean isUsingWebhook;
	private User bot;
	
	public Transceiver (String token) throws SuspendExecution
	{
		path = path.replace("<token>", token);
		parser = new JsonParser();
		client = FiberHttpClientBuilder.create().build();
		String getIDQuery = path + "getMe";
		final HttpGet req = new HttpGet(getIDQuery);
		webHook = new WebHook(token, Transceiver.this);
		Fiber<Void> init = new Fiber<Void>()
		{
			protected Void run () throws SuspendExecution, InterruptedException
			{
				
				int i = 3;
				CloseableHttpResponse response;
				HttpPost webHookInitRequest = null;
				FileBody fb = new FileBody(Launcher.getCertificate());
				try
				{
					response = client.execute(req);
					byte[] byteValue = getResponseByteArray(response);
					bot = (User) parser.parseResult(byteValue).getResult()[0];
					webHookInitRequest = new HttpPost(path + "setWebhook");
					StringBody sb = new StringBody("https://" + Launcher.IPAddress + ":" + WebHook.serverPort + "/" + token +
							                               "/",
					                               ContentType
							                               .TEXT_PLAIN);
					HttpEntity entity = MultipartEntityBuilder.create().addPart("certificate", fb).addPart("url", sb).build();
					webHookInitRequest.setEntity(entity);
				}
				catch (IOException e)
				{
					e.printStackTrace();
					System.exit(-1);
				}
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
						isUsingWebhook = true;
						response.close();
						break;
					}
					catch (IOException e)
					{
						e.printStackTrace();
						continue;
					}
				}
				return null;
			}
		}.start();
		try
		{
			Strand.join(init);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		botInterface = new Interface(this, bot, isUsingWebhook);
	}
	
	public static String getPath ()
	{
		return path;
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
				System.out.println("Transceiver sendig update to Interface");
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
		System.out.println("Transceiver executing request");
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
	
}

