package org.telegram.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by HellScre4m on 12/9/2016.
 */
public class Game implements TObject
{
	private String title;
	private String description;
	private String text;
	private PhotoSize[] photo;
	private MessageEntity[] text_entities;
	private Animation animation;
	
	@JsonGetter ("title")
	public String getTitle ()
	{
		return title;
	}
	
	@JsonSetter ("title")
	public Game setTitle (String title)
	{
		this.title = title;
		return this;
	}
	
	@JsonGetter ("description")
	public String getDescription ()
	{
		return description;
	}
	
	@JsonSetter ("description")
	public Game setDescription (String description)
	{
		this.description = description;
		return this;
	}
	
	@JsonGetter ("text")
	public String getText ()
	{
		return text;
	}
	
	@JsonSetter ("text")
	public Game setText (String text)
	{
		this.text = text;
		return this;
	}
	
	@JsonGetter ("photo")
	public PhotoSize[] getPhoto ()
	{
		return photo;
	}
	
	@JsonSetter ("photo")
	public Game setPhoto (PhotoSize[] photo)
	{
		this.photo = photo;
		return this;
	}
	
	@JsonGetter ("text_entities")
	public MessageEntity[] getText_entities ()
	{
		return text_entities;
	}
	
	@JsonSetter ("text_entities")
	public Game setText_entities (MessageEntity[] text_entities)
	{
		this.text_entities = text_entities;
		return this;
	}
	
	@JsonGetter ("animation")
	public Animation getAnimation ()
	{
		return animation;
	}
	
	@JsonSetter ("animation")
	public Game setAnimation (Animation animation)
	{
		this.animation = animation;
		return this;
	}
}
