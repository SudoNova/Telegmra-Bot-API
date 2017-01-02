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
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.telegram.objects.Result;
import org.telegram.objects.Update;
import org.telegram.objects.User;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by HellScre4m on 4/20/2016.
 */
public class Transceiver
{
	private static String path = "https://api.telegram.org/bot<token>/";
	static CloseableHttpClient client;
	private static ArrayList<Transceiver> repos;
	private ConcurrentLinkedQueue<Fiber> failureQueue;
	private boolean isUsingWebhook;
	private boolean isFailure;
	
	static
	{
		repos = new ArrayList<>(5);
		TrustManager trustManager = new X509TrustManager()
		{
			TrustManager[] trustManagers;
			
			{
				try
				{
					TrustManagerFactory factory = TrustManagerFactory
							.getInstance(TrustManagerFactory.getDefaultAlgorithm
									());
					factory.init((KeyStore) null);
					trustManagers = factory.getTrustManagers();
				}
				catch (NoSuchAlgorithmException | KeyStoreException e)
				{
					e.printStackTrace();
				}
			}
			
			@Override
			public void checkClientTrusted (X509Certificate[] x509Certificates, String s) throws CertificateException
			{
				for (TrustManager i : trustManagers)
				{
					if (i instanceof X509TrustManager)
					{
						X509TrustManager j = (X509TrustManager) i;
						j.checkClientTrusted(x509Certificates, s);
					}
				}
			}
			
			@Override
			public void checkServerTrusted (X509Certificate[] x509Certificates, String s) throws CertificateException
			{
				X509Certificate[] certs = Launcher.getBotsCertificates();
				for (X509Certificate i : certs)
				{
					for (X509Certificate j : x509Certificates)
					{
						if (i.equals(j))
						{
							return;
						}
					}
				}
				for (TrustManager i : trustManagers)
				{
					if (i instanceof X509TrustManager)
					{
						X509TrustManager j = (X509TrustManager) i;
						j.checkServerTrusted(x509Certificates, s);
					}
				}
			}
			
			@Override
			public X509Certificate[] getAcceptedIssuers ()
			{
				return new X509Certificate[0];
			}
		};
		SSLContextBuilder builder = new SSLContextBuilder();
		try
		{
			SSLContext context = builder.build();
			context.init(null, new TrustManager[]{trustManager}, null);
			client = FiberHttpClientBuilder.create().setSSLContext(context).build();
		}
		catch (NoSuchAlgorithmException | KeyManagementException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
//		client = FiberHttpClientBuilder.create().setHostnameVerifier(verifier).build();
		
	}
	
	AtomicInteger updateIndex;
	Interface botInterface;
	private boolean shutDown;
	private UpdatePuller updatePuller;
	private Launcher launcher;
	private boolean shutdown = false;
	private JsonParser parser;
	private KeepAliveDaemon daemon;
	
	private Transceiver (Launcher launcher)
	{
		this.launcher = launcher;
		path = path.replace("<token>", launcher.token);
		parser = JsonParser.getInstance();
		
		updatePuller = new UpdatePuller();
		updateIndex = new AtomicInteger(0);
		failureQueue = new ConcurrentLinkedQueue<>();
		daemon = new KeepAliveDaemon();
		if (Strand.isCurrentFiber())
		{
			try
			{
				init();
			}
			catch (SuspendExecution suspendExecution)
			{
				suspendExecution.printStackTrace();
			}
		}
		else
		{
			Fiber<Void> t = new Fiber<Void>()
			{
				@Override
				protected Void run () throws SuspendExecution, InterruptedException
				{
					init();
					return null;
				}
			}.start();
			try
			{
				Strand.join(t);
			}
			catch (ExecutionException | InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	static Transceiver getInstance (Launcher launcher)
	{
		int serial = launcher.serialNumber;
		if (repos.size() <= serial || repos.get(serial) == null)
		{
			Transceiver transceiver = new Transceiver(launcher);
			repos.add(serial, transceiver);
			transceiver.botInterface = Interface.getInstance(launcher);
			return transceiver;
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
			Object obj = parser.parseResult(byteValue).getResult();
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.err.println("Unable to start bot. Network problems found.");
			System.exit(-1);
		}
		enableWebhook(launcher.webhookURL);
		daemon.start();
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
		try
		{
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
		int tryCount = 3;
		while (true)
		{
			try
			{
				HttpResponse response = client.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode / 100 != 5)
				{
					notifySuccess();
					Result result = getResult(response);
					botInterface.receiveResult(result);
					return result;
				}
				tryCount--;
			}
//		catch (IOException e)
			catch (IOException e)
			{
				e.printStackTrace();
			}
			if (tryCount == 0)
			{
				tryCount = 3;
				notifyFailure();
			}
			try
			{
				Strand.sleep(250);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void notifySuccess ()
	{
		isFailure = false;
		for (int i = 0; i < failureQueue.size(); i++)
		{
			Fiber fiber = failureQueue.poll();
			if (fiber != null)
			{
				fiber.unpark();
			}
		}
	}
	
	private void notifyFailure () throws SuspendExecution
	{
		isFailure = true;
		Fiber thisFiber = Fiber.currentFiber();
		failureQueue.add(thisFiber);
		Fiber.park();
		//TODO add code here
	}
	
	boolean disableWebhook () throws SuspendExecution, InterruptedException
	{
		return disableWebhook(1);
	}
	
	boolean disableWebhook (int tryCount) throws SuspendExecution, InterruptedException
	{
		return enableWebhook(null, tryCount);
	}
	
	boolean enableWebhook (String webhookURL) throws SuspendExecution
	{
		return enableWebhook(webhookURL, 1);
	}
	
	boolean enableWebhook (String webhookURL, int tryCount) throws SuspendExecution
	{
		boolean isDisableOperation = false;
		boolean success = false;
		CloseableHttpResponse response;
		HttpPost webHookInitRequest = null;
		webHookInitRequest = new HttpPost(path + "setWebhook");
		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
		if (webhookURL == null)
		{
			webhookURL = "";
			isDisableOperation = true;
		}
		else
		{
			if (!testWebhook(webhookURL + "test"))
			{
				System.err.println("Webhook is not responding");
				return false;
			}
			FileBody fb = new FileBody(new File(launcher.certPath + "cert.pem"));
			entityBuilder.addPart("certificate", fb);
		}
		StringBody sb = new StringBody(webhookURL,
		                               ContentType
				                               .TEXT_PLAIN
		);
		entityBuilder.addPart("url", sb);
		HttpEntity entity = entityBuilder.build();
		webHookInitRequest.setEntity(entity);
		while (tryCount-- > 0)
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
				if (isDisableOperation)
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
			catch (IOException | InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		isUsingWebhook = success && !isDisableOperation;
		return success;
	}
	
	void shutDown () throws SuspendExecution, InterruptedException
	{
		disableWebhook();
		shutDown = true;
		botInterface.shutDown();
		//TODO add dump option
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
	
	public boolean testWebhook () throws SuspendExecution
	{
		return testWebhook(launcher.webhookURL + "test");
	}
	
	public boolean testWebhook (String url) throws SuspendExecution
	{
		HttpPost post = new HttpPost();
		try
		{
			URI uri = new URI(url);
			post.setURI(uri);
			HttpResponse response = client.execute(post);
			return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK
					&& EntityUtils.toString(response.getEntity()).equals("OK");
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;//false;
	}
	
	private class KeepAliveDaemon extends Fiber<Void>
	{
		@Override
		protected Void run () throws SuspendExecution, InterruptedException
		{
			sleep(20000);
			while (!shutDown)
			{
				sleep(5000);
				boolean answer = testWebhook();
				if (answer)
				{
					if (!isUsingWebhook)
					{
						enableWebhook(launcher.webhookURL);
					}
					notifySuccess();
				}
				else
				{
					if (isFailure)
					{
						disableWebhook();
					}
					else
					{
						isFailure = true;
					}
				}
			}
			return null;
		}
	}
	
	public User getUesr () throws SuspendExecution, UnsupportedHttpVersionException
	{
		throw new UnsupportedHttpVersionException();
	}
}

