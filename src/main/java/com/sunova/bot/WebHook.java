package com.sunova.bot;

import org.apache.http.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnectionFactory;
import org.apache.http.protocol.*;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
//TODO make it asynchronous and more flexible and use singleton pattern
/**
 * Created by HellScre4m on 6/1/2016.
 */
public class WebHook
{
	protected static final int serverPort = 8443;
	private static ArrayList<WebHook> repos;
	
	static
	{
		repos = new ArrayList<>(5);
	}
	
	private Bot bot;
	private ServerSocket serverSocket;
	private Thread connectionAcceptor;
	private boolean shutDown;
	private Transceiver transceiver;
	
	private WebHook (Bot bot)
	{
		try
		{
			this.bot = bot;
			transceiver = Transceiver.getInstance(bot);
			SSLContext context = SSLContext.getInstance("TLSv1.2");
			
			X509Certificate cert = bot.cert;
			PrivateKey privateKey = bot.privateKey;
			
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(null);
			ks.setCertificateEntry("cert-alias", cert);
			ks.setKeyEntry("key-alias", privateKey, "missile@supervisor".toCharArray(), new Certificate[]{cert});
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, "missile@supervisor".toCharArray());
			KeyManager[] km = kmf.getKeyManagers();
			context.init(km, null, null);
			serverSocket = context.getServerSocketFactory().createServerSocket(serverPort);
			ResponseContent responseContent = new ResponseContent();
			ResponseConnControl responseConnControl = new ResponseConnControl();
			ResponseServer responseServer = new ResponseServer("SuNova/1.1");
			HttpProcessor processor = HttpProcessorBuilder.create().add(responseConnControl).add(responseContent).add
					(responseServer).build();
			UriHttpRequestHandlerMapper registry = new UriHttpRequestHandlerMapper();
			HttpRequestHandler handler = new HttpRequestHandler()
			{
				StringEntity entity1 = new StringEntity("{}");
				StringEntity entity2 = new StringEntity("OK");
				
				@Override
				public void handle (HttpRequest request, HttpResponse response, HttpContext context)
						throws HttpException, IOException
				{
					if (request.getRequestLine().getMethod().toUpperCase().equals("POST"))
					{
						String uri = request.getRequestLine().getUri();
//						System.out.println(uri);
//						System.out.println("New update. Sending to trancseiver");
						response.setStatusCode(HttpStatus.SC_OK);
						if (uri.endsWith("test"))
						{
							response.setEntity(entity2);
						}
						else
						{
							response.setEntity(entity1);
							transceiver.receiveUpdate(request);
						}
					}
					else
					{
						System.out.println("Error receiving webhook");
						response.setStatusCode(HttpStatus.SC_UNAUTHORIZED);
					}
				}
			};
			registry.register("/" + bot.token + "/*", handler);
			HttpService httpService = new HttpService(processor, registry);
			HttpConnectionFactory<DefaultBHttpServerConnection> connectionFactory =
					DefaultBHttpServerConnectionFactory.INSTANCE;
			connectionAcceptor =
					new Thread(
							() ->
							{
								while (!shutDown)
								{
									try
									{
										Socket s = serverSocket.accept();
//							System.out.println("Connection accepted");
										s.setKeepAlive(true);
										DefaultBHttpServerConnection connection = connectionFactory
												.createConnection(s);
										HttpContext context1 = new BasicHttpContext();
										Thread executor = new Thread()
										{
											@Override
											public void run ()
											{
												while (connection.isOpen() && !shutDown)
												{
													try
													{
//											System.out.println("handling");
														httpService.handleRequest(connection, context1);
													}
													catch (ConnectionClosedException e)
													{
														// Do nothing
														break;
													}
													catch (IOException | HttpException e)
													{
														e.printStackTrace();
														break;
													}
													
												}
												try
												{
													connection.close();
												}
												catch (Exception e)
												{
													e.printStackTrace();
												}
											}
											
										};
										executor.start();
									}
									
									catch (Exception e)
									{
										e.printStackTrace();
									}
								}
								try
								{
									serverSocket.close();
								}
								catch (Exception e)
								{
									e.printStackTrace();
								}
							});
			connectionAcceptor.start();
		}
		
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	static WebHook getInstance (Bot bot)
	{
		int serial = bot.serialNumber;
		if (repos.size() <= serial || repos.get(serial) == null)
		{
			repos.add(bot.serialNumber, new WebHook(bot));
		}
		return repos.get(serial);
	}
	
	void shutDown ()
	{
		shutDown = true;
	}
//
//	private class Handler implements HttpRequestHandler
//	{
//
//		@Override
//		public void handle (HttpRequest request, HttpResponse response, HttpContext context)
//				throws HttpException, IOException
//		{
//
//		}
//	}
}

