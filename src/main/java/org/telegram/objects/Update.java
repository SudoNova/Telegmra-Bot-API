package org.telegram.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by HellScre4m on 5/2/2016.
 */
public class Update implements TObject
{
	private int update_id;
	private Message message;
	@JsonIgnore (true)
	private Message edited_message;
	@JsonIgnore (true)
	private Message channel_post;
	@JsonIgnore (true)
	private Message edited_channel_post;
	
	//TODO this class is incomplete
	
	@JsonGetter ("update_id")
	public int getUpdate_id ()
	{
		return update_id;
	}
	
	@JsonSetter ("update_id")
	public Update setUpdate_id (int update_id)
	{
		this.update_id = update_id;
		return this;
	}
	
	@JsonGetter ("message")
	public Message getMessage ()
	{
		return message;
	}
	
	@JsonSetter ("message")
	public Update setMessage (Message message)
	{
		this.message = message;
		return this;
	}
	
	@JsonIgnore
	public boolean containsMessage ()
	{
		return message != null;
	}
	
}
