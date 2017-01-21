package com.sunova.botframework;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by HellScre4m on 4/26/2016.
 */
public class Bot
{
	static InputStream in;
	//	protected static String domainAddress = "188.211.199.38";
	private static AtomicInteger serialNumberTracker;
	private static ArrayList<Bot> botRepos;
	
	static
	{
		in = System.in;
		serialNumberTracker = new AtomicInteger(0);
		botRepos = new ArrayList<>(5);
	}
	
	String domainAddress;
	String token;
	int serialNumber;
	boolean shutDown;
	X509Certificate cert;
	PrivateKey privateKey;
	String webhookURL;
	String resourcesPath;
	UserInterface userInterface;
	private BotInterface botInterface;
	private WebHook webHook;
	private Transceiver transceiver;
	private ServantFiber requestHandler;
	
	private Bot ()
	{
		super();
	}
	
	public static Bot getInstance (int ID)
	{
		return botRepos.get(ID);
	}
	
	public static Bot createInstance ()
	{
		Bot bot = new Bot();
		bot.serialNumber = serialNumberTracker.getAndIncrement();
		botRepos.add(bot.serialNumber, bot);
		return bot;
	}
	
	static X509Certificate[] getBotsCertificates ()
	{
		int size = serialNumberTracker.get();
		X509Certificate[] result = new X509Certificate[size];
		for (int i = 0; i < size; i++)
		{
			result[i] = botRepos.get(i).cert;
		}
		return result;
		
	}
	
	@Suspendable
	public Bot build ()
	{
		if (domainAddress == null)
		{
			System.err.println("Specify domain address via setDomainAddress()");
			return this;
		}
		if (token == null)
		{
			System.err.println("Specify bot token to via setBotToken()");
			return this;
		}
		if (userInterface == null)
		{
			System.err.println("Please set an EventProcessor");
			return this;
		}
		init();
		return this;
	}
	
	public Bot setUserInterface (UserInterface userInterface)
	{
		this.userInterface = userInterface;
		return this;
	}
	
	
	public Bot setBotToken (String token)
	{
		this.token = token;
		return this;
	}
	
	public Bot setDomainAddress (String domainAddress)
	{
		this.domainAddress = domainAddress;
		return this;
	}
	
	private void init ()
	{
		userInterface.init(this);
		resourcesPath = System.getProperty("user.dir") + "\\resources\\" + token.replace(":", "_") + "\\";
		File temp = new File(resourcesPath);
		if (!temp.exists())
		{
			temp.mkdirs();
		}
		System.setProperty("co.paralleluniverse.fibers.detectRunawayFibers", "false");
		//May be added to a static constructor
		try
		{
			File certFile = new File(resourcesPath + "cert\\" + "cert.pem");
			if (!certFile.exists())
			{
				generateCert();
			}
			else
			{
				Scanner reader = new Scanner(certFile);
				StringBuilder bd = new StringBuilder();
//				bd.append("-----BEGIN CERTIFICATE-----\n");
				while (reader.hasNextLine())
				{
					String line = reader.nextLine();
					if (!(line.contains("BEGIN") || line.contains("END")))
					{
						bd.append(line.concat("\n"));
					}
				}
				reader.close();
//				bd.append("-----END CERTIFICATE-----");
				byte[] buffer = bd.toString().getBytes();
				X509CertificateHolder holder = new X509CertificateHolder(org.bouncycastle.util.encoders.Base64.decode
						(buffer));
				cert = new JcaX509CertificateConverter().getCertificate(holder);
				Date date = new Date();
				if (date.after(cert.getNotAfter()))
				{
					generateCert();
				}
				else
				{
					File privateKeyFile = new File(resourcesPath + "cert\\" + "privateKey.pem");
					reader = new Scanner(privateKeyFile);
					bd = new StringBuilder();
					while (reader.hasNextLine())
					{
						String line = reader.nextLine();
						bd.append(line.concat("\n"));
					}
					buffer = bd.toString().getBytes();
					reader.close();
					PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(
							org.bouncycastle.util.encoders.Base64.decode(buffer));
					KeyFactory kf = KeyFactory.getInstance("RSA");
					privateKey = kf.generatePrivate(spec);
					System.out.println("Certificate expiration date: " + holder.getNotAfter());
				}
			}
		}
		catch (
				Exception e)
		{
			e.printStackTrace();
		}
		
		webhookURL = "https://" + domainAddress + ":" + WebHook.serverPort +
				"/" +
				token +
				"/";
		transceiver = Transceiver.getInstance(this);
		botInterface = BotInterface.getInstance(this);
		userInterface.init(this);
		webHook = WebHook.getInstance(this);
		requestHandler = new
				
				ServantFiber();
		
		requestHandler.option = requestHandler.INIT;
		requestHandler.start();
		
		maintenance();
	}
	
	private void maintenance ()
	{
		Scanner in = new Scanner(System.in);
		while (!shutDown && in.hasNextLine())
		{
			String choice = in.nextLine().toLowerCase();
			if (choice.matches("disable\\s+webhook"))
			{
				requestHandler.option = requestHandler.DISABLE_WEBHOOK;
			}
			else if (choice.matches("enable\\s+webhook"))
			{
				requestHandler.option = requestHandler.SET_WEBHOOK;
			}
			else if (choice.matches("shut\\s+down"))
			{
				requestHandler.option = requestHandler.SHUTDOWN;
			}
			else
			{
				System.out.println(":(");
				continue;
			}
			requestHandler.unpark();
		}
	}
	
	private void generateCert ()
	{
		System.out.println("Generating Certificate");
		String certPath = resourcesPath + "cert\\";
		File temp = new File(certPath);
		temp.mkdirs();
		Date startDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);
		cal.add(Calendar.MONTH, 1);
		Date endDate = new Date(cal.getTime().getTime());
		int serialNumber = ("https://api.telegram.org/bot" + token + "/").hashCode();
		KeyPair pair;
		PublicKey publicKey;
		X500NameBuilder nameBuilder = new X500NameBuilder();
		nameBuilder.addRDN(BCStyle.CN, domainAddress);
		nameBuilder.addRDN(BCStyle.O, "SuNova LLP");
		nameBuilder.addRDN(BCStyle.C, "Iran");
		nameBuilder.addRDN(BCStyle.SERIALNUMBER, serialNumber + "");
		nameBuilder.addRDN(BCStyle.SN, "SuNova");
		X500Name subjectDN = nameBuilder.build();
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		try
		{
			KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA", "BC");
			gen.initialize(2048);
			pair = gen.generateKeyPair();
			privateKey = pair.getPrivate();
			System.out.println(privateKey.getFormat());
			publicKey = pair.getPublic();
			SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
			X509v3CertificateBuilder builder = new X509v3CertificateBuilder(subjectDN,
			                                                                new BigInteger(serialNumber + ""),
			                                                                startDate, endDate, subjectDN,
			                                                                publicKeyInfo
			);
			ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(privateKey);
			cert = new JcaX509CertificateConverter().getCertificate(builder.build(signer));
			
			Base64 encoder = new Base64(64);
			File file = new File(certPath + "privateKey.pem");
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
//			writer.write("-----BEGIN PRIVATE KEY-----\n");
			writer.write(new String(encoder.encode(privateKey.getEncoded())));
//			writer.write("-----END PRIVATE KEY-----");
			writer.close();
			
			file = new File(certPath + "publicKey.pem");
			file.createNewFile();
			writer = new FileWriter(file);
//			writer.write("-----BEGIN PUBLIC KEY-----\n");
			writer.write(new String(encoder.encode(publicKey.getEncoded())));
//			writer.write("-----END PUBLIC KEY-----");
			writer.close();
			
			file = new File(certPath + "cert.pem");
			file.createNewFile();
			writer = new FileWriter(file);
			writer.write("-----BEGIN CERTIFICATE-----\n");
			writer.write(new String(encoder.encode(cert.getEncoded())));
			writer.write("-----END CERTIFICATE-----");
			writer.close();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public BotInterface getInterface ()
	{
		return botInterface;
	}
	
	@Deprecated
	class ServantFiber extends Fiber<Void>
	{
		public final byte INIT = 1;
		public final byte SET_WEBHOOK = 2;
		public final byte DISABLE_WEBHOOK = 3;
		public final byte SHUTDOWN = 4;
		public byte option;
		
		protected Void run () throws SuspendExecution, InterruptedException
		{
			while (!shutDown)
			{
				handle();
				Fiber.park();
			}
			return null;
		}
		
		//TODO remove handlerFiber and make called methods thread callable
		@Deprecated
		private void handle () throws SuspendExecution, InterruptedException
		{
			switch (option)
			{
				case INIT:
					break;
				case SET_WEBHOOK:
					transceiver.enableWebhook(webhookURL);
					break;
				case DISABLE_WEBHOOK:
					transceiver.disableWebhook();
					break;
				case SHUTDOWN:
					webHook.shutDown();
					transceiver.shutDown();
					shutDown = true;
					System.exit(0);
			}
		}
		
	}
}
