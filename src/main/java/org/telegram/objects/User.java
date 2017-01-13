package org.telegram.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by HellScre4m on 4/25/2016.
 */
public class User implements TObject
{
	
	//These fields are not official fields are used for personal use
	public static final byte ADMIN = 1;
	public static final byte FREE_USER = 2;
	public static final byte PREMIUM_USER = 3;
	private int id;
	private String first_name;
	private String last_name;
	private String username;
	
	@JsonGetter ("id")
	public int getId ()
	{
		return id;
	}
	
	@JsonSetter ("id")
	public User setId (int id)
	{
		this.id = id;
		return this;
	}
	
	@JsonGetter ("first_name")
	public String getFirst_name ()
	{
		return first_name;
	}
	
	@JsonSetter ("first_name")
	public User setFirst_name (String first_name)
	{
		this.first_name = first_name;
		return this;
	}
	
	public boolean hasFirst_name ()
	{
		return first_name != null;
	}
	
	@JsonGetter ("last_name")
	public String getLast_name ()
	{
		return last_name;
	}
	
	@JsonSetter ("last_name")
	public User setLast_name (String last_name)
	{
		this.last_name = last_name;
		return this;
	}
	
	public boolean hasLast_name ()
	{
		return last_name != null;
	}
	
	@JsonGetter ("username")
	public String getUsername ()
	{
		return username;
	}
	
	@JsonSetter ("username")
	public User setUsername (String username)
	{
		this.username = username;
		return this;
	}
	
}
