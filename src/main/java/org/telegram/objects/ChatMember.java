package org.telegram.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by HellScre4m on 1/28/2017.
 */
public class ChatMember implements TObject
{
	private User user;
	private String status;
	
	@JsonGetter ("user")
	public User getUser ()
	{
		return user;
	}
	
	@JsonSetter ("user")
	public ChatMember setUser (User user)
	{
		this.user = user;
		return this;
	}
	
	@JsonGetter ("status")
	public String getStatus ()
	{
		return status;
	}
	
	@JsonSetter ("status")
	public ChatMember setStatus (String status)
	{
		this.status = status;
		return this;
	}
}
