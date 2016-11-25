package org.telegram.objects;

/**
 * Created by HellScre4m on 4/25/2016.
 */
public class Chat implements TObject
{
	public static final byte PRIVATE = 1;
	public static final byte GROUP = 2;
	public static final byte SUPERGROUP = 3;
	public static final byte CHANNEL = 4;
	private int id;
	private byte operationType;
	private String title;
	private String username;
	private String first_name;
	private String last_name;
	private String type;
	
	public int getId ()
	{
		return id;
	}
	
	public void setId (int id)
	{
		this.id = id;
	}
	
	public byte getOperationType ()
	{
		return operationType;
	}
	
	public String getTitle ()
	{
		return title;
	}
	
	public void setTitle (String title)
	{
		this.title = title;
	}
	
	public String getUsername ()
	{
		return username;
	}
	
	public void setUsername (String username)
	{
		this.username = username;
	}
	
	public String getFirst_name ()
	{
		return first_name;
	}
	
	public void setFirst_name (String first_name)
	{
		this.first_name = first_name;
	}
	
	public String getLast_name ()
	{
		return last_name;
	}
	
	public void setLast_name (String last_name)
	{
		this.last_name = last_name;
	}
	
	public String getType ()
	{
		return type;
	}
	
	public void setType (String type)
	{
		this.type = type;
		switch (type)
		{
			case "private":
				operationType = PRIVATE;
				break;
			case "group":
				operationType = GROUP;
				break;
			case "supergroup":
				operationType = SUPERGROUP;
				break;
			case "channel":
				operationType = CHANNEL;
		}
	}
}
