package com.sunova.bot;

import co.paralleluniverse.fibers.SuspendExecution;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by HellScre4m on 4/26/2016.
 */
public class Launcher
{
	protected static String IPAddress = "sunova.dynu.com";
	private static Launcher ourInstance;
	private static File publicKey;
	private static File privateKey;
	private static File certificate;
	private static String webhookURL;
	private String token = "211948704:AAGkZOzNDrzhNUApb8i8n8c6gx73F2R1prc";
	private Transceiver transceiver;
//	protected static String IPAddress = "188.211.199.38";
	
	
	private Launcher () throws SuspendExecution
	{
		System.setProperty("co.paralleluniverse.fibers.detectRunawayFibers", "false");
		String certPath = System.getProperty("user.dir") + "\\Cert\\";
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
		transceiver = new Transceiver(token);
	}
	
	public static Launcher getInstance () throws SuspendExecution
	{
		if (ourInstance == null)
		{
			ourInstance = new Launcher();
		}
		return ourInstance;
	}
	
	public static void main (String[] args) throws SuspendExecution
	{
		getInstance();
	}
	
	protected static File getPublicKey ()
	{
		return publicKey;
	}
	
	protected static File getCertificate ()
	{
		return certificate;
	}
	
	protected static File getPrivateKey ()
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
		nameBuilder.addRDN(BCStyle.CN, IPAddress);
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
			X509v3CertificateBuilder builder = new X509v3CertificateBuilder(subjectDN, new BigInteger(serialNumber + ""),
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
