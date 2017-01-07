package org.telegram.objects;

/**
 * Created by HellScre4m on 4/25/2016.
 */
public class Chat implements TObject
{
	
	private boolean all_members_are_administrators;
	private long id;
	private String title;
	private String username;
	private String first_name;
	private String last_name;
	private String type;
	
	public boolean isAll_members_are_administrators ()
	{
		return all_members_are_administrators;
	}
	
	public void setAll_members_are_administrators (boolean all_members_are_administrators)
	{
		this.all_members_are_administrators = all_members_are_administrators;
	}
	
	public long getId ()
	{
		return id;
	}
	
	public Chat setId (long id)
	{
		this.id = id;
		return this;
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
	}
	
}
