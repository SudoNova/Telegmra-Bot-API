package org.telegram.objects;

/**
 * Created by HellScre4m on 4/25/2016.
 */
public class User implements TObject
{
	String objectType = "user";
	private int id;
	private String first_name;
	private String last_name;
	private String username;
	
	public int getId ()
	{
		
		return id;
	}
	
	public void setId (int id)
	{
		this.id = id;
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
	
	public String getUsername ()
	{
		return username;
	}
	
	public void setUsername (String username)
	{
		this.username = username;
	}
	
}
