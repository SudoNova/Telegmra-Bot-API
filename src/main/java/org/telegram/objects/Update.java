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
	
	private Message edited_message;
	private Message channel_post;
	private Message edited_channel_post;
	
	//TODO this class is incomplete
	@JsonGetter ("edited_message")
	public Message getEdited_message ()
	{
		return edited_message;
	}
	
	@JsonSetter ("edited_message")
	public Update setEdited_message (Message edited_message)
	{
		this.edited_message = edited_message;
		return this;
	}
	
	@JsonGetter ("channel_post")
	public Message getChannel_post ()
	{
		return channel_post;
	}
	
	@JsonSetter ("channel_post")
	public Update setChannel_post (Message channel_post)
	{
		this.channel_post = channel_post;
		return this;
	}
	
	@JsonGetter ("edited_channel_post")
	public Message getEdited_channel_post ()
	{
		return edited_channel_post;
	}
	
	@JsonSetter ("edited_channel_post")
	public Update setEdited_channel_post (Message edited_channel_post)
	{
		this.edited_channel_post = edited_channel_post;
		return this;
	}
	
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
