package org.telegram.objects;

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
	
	public String getTitle ()
	{
		return title;
	}
	
	public void setTitle (String title)
	{
		this.title = title;
	}
	
	public String getDescription ()
	{
		return description;
	}
	
	public void setDescription (String description)
	{
		this.description = description;
	}
	
	public String getText ()
	{
		return text;
	}
	
	public void setText (String text)
	{
		this.text = text;
	}
	
	public PhotoSize[] getPhoto ()
	{
		return photo;
	}
	
	public void setPhoto (PhotoSize[] photo)
	{
		this.photo = photo;
	}
	
	public MessageEntity[] getText_entities ()
	{
		return text_entities;
	}
	
	public void setText_entities (MessageEntity[] text_entities)
	{
		this.text_entities = text_entities;
	}
	
	public Animation getAnimation ()
	{
		return animation;
	}
	
	public void setAnimation (Animation animation)
	{
		this.animation = animation;
	}
}