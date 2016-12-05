package com.sunova.bot;

import co.paralleluniverse.fibers.Suspendable;
import org.apache.http.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnectionFactory;
import org.apache.http.protocol.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.util.encoders.Base64;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Scanner;

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
	
	private Launcher launcher;
	private ServerSocket serverSocket;
	private Thread connectionAcceptor;
	private boolean shutDown;
	private Transceiver transceiver;
	
	private WebHook (Launcher launcher)
	{
		try
		{
			this.launcher = launcher;
			transceiver = Transceiver.getInstance(launcher);
			SSLContext context = SSLContext.getInstance("TLSv1.2");
			Scanner reader = new Scanner(launcher.getCertificate());
			StringBuilder bd = new StringBuilder();
			while (reader.hasNextLine())
			{
				String line = reader.nextLine();
				if (!(line.contains("BEGIN") || line.contains("END")))
				{
					bd.append(line.concat("\n"));
				}
			}
			byte[] buffer = bd.toString().getBytes();
			reader.close();
			X509CertificateHolder holder = new X509CertificateHolder(Base64.decode(buffer));
			X509Certificate cert = new JcaX509CertificateConverter().getCertificate(holder);
			
			FileInputStream stream = new FileInputStream(launcher.getPrivateKey());
			buffer = new byte[(int) launcher.getPrivateKey().length()];
			stream.read(buffer);
			stream.close();
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.decode(buffer));
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = kf.generatePrivate(spec);
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
			registry.register("*" + launcher.token + "/", new HttpRequestHandler()
			{
				@Override
				@Suspendable
				public void handle (HttpRequest request, HttpResponse response, HttpContext context)
						throws HttpException, IOException
				{
					if (request.getRequestLine().getMethod().toUpperCase().equals("POST"))
					{
						System.out.println("New update. Sending to trancseiver");
						transceiver.receiveUpdate(request);
						response.setStatusCode(HttpStatus.SC_OK);
						StringEntity entity = new StringEntity("{}");
						response.setEntity(entity);
					}
					else
					{
						System.out.println("Error recieving webhook");
						response.setStatusCode(HttpStatus.SC_UNAUTHORIZED);
					}
				}
			});
			HttpService httpService = new HttpService(processor, registry);
			HttpConnectionFactory<DefaultBHttpServerConnection> connectionFactory =
					DefaultBHttpServerConnectionFactory.INSTANCE;
			connectionAcceptor = new Thread()
			{
				@Override
				public void run ()
				{
					while (!shutDown)
					{
						try
						{
							Socket s = serverSocket.accept();
							s.setKeepAlive(true);
							DefaultBHttpServerConnection connection = connectionFactory.createConnection(s);
							HttpContext context = new BasicHttpContext();
							Thread executor = new Thread()
							{
								@Override
								public void run ()
								{
									while (connection.isOpen() && !shutDown)
									{
										try
										{
											httpService.handleRequest(connection, context);
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
				}
				
			};
			connectionAcceptor.start();
		}
		
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	static WebHook getInstance (Launcher launcher)
	{
		int serial = launcher.serialNumber;
		if (repos.size() <= serial || repos.get(serial) == null)
		{
			repos.add(launcher.serialNumber, new WebHook(launcher));
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

