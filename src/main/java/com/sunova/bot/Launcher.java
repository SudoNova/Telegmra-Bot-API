package com.sunova.bot;

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
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by HellScre4m on 4/26/2016.
 */
public class Launcher
{
	//	protected static String domainAddress = "188.211.199.38";
	private static AtomicInteger serialNumberTracker;
	private static ArrayList<Launcher> launcherRepos;
	
	static
	{
		serialNumberTracker = new AtomicInteger(0);
		launcherRepos = new ArrayList<>(5);
	}
	
	String domainAddress;
	String token;
	int serialNumber;
	private String certPath;
	private File publicKey;
	private File privateKey;
	private File certificate;
	private String webhookURL;
	private Interface botInterface;
	private WebHook webHook;
	private Transceiver transceiver;
	
	private Launcher ()
	{
		super();
	}
	
	public static Launcher getInstance (int ID)
	{
		return launcherRepos.get(ID);
	}
	
	public static Launcher createInstance ()
	{
		Launcher launcher = new Launcher();
		launcher.serialNumber = serialNumberTracker.getAndIncrement();
		launcherRepos.add(launcher.serialNumber, launcher);
		return launcher;
	}
	
	public static void main (String[] args) throws SuspendExecution
	{
		Launcher instance = createInstance();
		String certPath = System.getProperty("user.dir") + "\\Cert\\";
		String token = "211948704:AAGkZOzNDrzhNUApb8i8n8c6gx73F2R1prc";
		String domainAddress = "sunova.dynu.com";
		instance.setBotToken(token).setCertPath(certPath).setDomainAddress(domainAddress).build();
		
	}
	
	@Suspendable
	public Launcher build ()
	{
		// TODO check vars
		if (certPath == null)
		{
			System.err.println("Specify path to certificate folder via setCertPath()");
		}
		if (domainAddress == null)
		{
			System.err.println("Specify domain address via setDomainAddress()");
		}
		if (token == null)
		{
			System.err.println("Specify bot token to via setBotToken()");
		}
		try
		{
			init();
		}
		catch (SuspendExecution e)
		{
			
		}
		return this;
	}
	
	public Launcher setCertPath (String certPath)
	{
		this.certPath = certPath;
		return this;
	}
	
	public Launcher setBotToken (String token)
	{
		this.token = token;
		return this;
	}
	
	public Launcher setDomainAddress (String domainAddress)
	{
		this.domainAddress = domainAddress;
		return this;
	}
	
	private void init () throws SuspendExecution
	{
		//		System.setProperty("co.paralleluniverse.fibers.detectRunawayFibers", "false");
		File certFile = new File(certPath + "cert.pem");
		try
		{
			if (!certFile.exists())
			{
				generateCert();
			}
			else
			{
				Scanner reader = new Scanner(certFile);
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
				X509CertificateHolder cert = new X509CertificateHolder(org.bouncycastle.util.encoders.Base64.decode
						(buffer));
				Date date = new Date();
				if (date.after(cert.getNotAfter()))
				{
					generateCert();
				}
				else
				{
					System.out.println(cert.getNotAfter());
				}
				publicKey = new File(certPath + "publicKey.pem");
				privateKey = new File(certPath + "privateKey.pem");
				certificate = certFile;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		transceiver = Transceiver.getInstance(this);
		webHook = WebHook.getInstance(this);
		botInterface = Interface.getInstance(this);
		Fiber fiber = new Fiber(() ->
		                        {
			                        //@formatter:off
			                        transceiver.botInterface = botInterface;
			                        transceiver.init();
			                        botInterface.transceiver = transceiver;
			                        botInterface.bot = transceiver.bot;
			                        webhookURL = "https://" + domainAddress + ":" + WebHook.serverPort +
					                        "/" +
					                        token +
					                        "/";
			                        try
			                        {
				                        transceiver.enableWebhook(webhookURL);
				                        botInterface.start();
				                        System.out.println("haha");
			                        }
			                        catch (SuspendExecution | InterruptedException e)
			                        {
				                        e.printStackTrace();
			                        }
			
		                        }
		).start();
//		try
//		{
//			Strand.join(fiber);
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
	}
	
	File getPublicKey ()
	{
		return publicKey;
	}
	
	File getCertificate ()
	{
		return certificate;
	}
	
	File getPrivateKey ()
	{
		return privateKey;
	}
	
	private void generateCert ()
	{
		System.out.println("generating");
		String certPath = System.getProperty("user.dir") + "\\Cert\\";
		File temp = new File(certPath);
		temp.mkdir();
		Date startDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);
		cal.add(Calendar.MONTH, 1);
		Date endDate = new Date(cal.getTime().getTime());
		int serialNumber = ("https://api.telegram.org/bot" + token + "/").hashCode();
		KeyPair pair;
		PrivateKey privateKey;
		PublicKey publicKey;
		X509Certificate cert;
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
			                                                                publicKeyInfo);
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
			writer.write("-----BEGIN PUBLIC KEY-----\n");
			writer.write(new String(encoder.encode(publicKey.getEncoded())));
			writer.write("-----END PUBLIC KEY-----");
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
}
