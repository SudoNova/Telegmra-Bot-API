package org.telegram.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

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
	
	@JsonGetter ("all_members_are_administrators")
	public boolean isAll_members_are_administrators ()
	{
		return all_members_are_administrators;
	}
	
	@JsonSetter ("all_members_are_administrators")
	public Chat setAll_members_are_administrators (boolean all_members_are_administrators)
	{
		this.all_members_are_administrators = all_members_are_administrators;
		return this;
	}
	
	@JsonGetter ("id")
	public long getId ()
	{
		return id;
	}
	
	@JsonSetter ("id")
	public Chat setId (long id)
	{
		this.id = id;
		return this;
	}
	
	@JsonGetter ("title")
	public String getTitle ()
	{
		return title;
	}
	
	@JsonSetter ("title")
	public Chat setTitle (String title)
	{
		this.title = title;
		return this;
	}
	
	@JsonGetter ("username")
	public String getUsername ()
	{
		return username;
	}
	
	@JsonSetter ("username")
	public Chat setUsername (String username)
	{
		this.username = username;
		return this;
	}
	
	@JsonGetter ("first_name")
	public String getFirst_name ()
	{
		return first_name;
	}
	
	@JsonSetter ("first_name")
	public Chat setFirst_name (String first_name)
	{
		this.first_name = first_name;
		return this;
	}
	
	@JsonGetter ("last_name")
	public String getLast_name ()
	{
		return last_name;
	}
	
	@JsonSetter ("last_name")
	public Chat setLast_name (String last_name)
	{
		this.last_name = last_name;
		return this;
	}
	
	@JsonGetter ("type")
	public String getType ()
	{
		return type;
	}
	
	@JsonSetter ("type")
	public Chat setType (String type)
	{
		this.type = type;
		return this;
	}
	
}
