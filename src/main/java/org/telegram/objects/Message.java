package org.telegram.objects;

/**
 * Created by HellScre4m on 4/26/2016.
 */
public class Message implements TObject
{
	private int message_id;
	private User from;
	private int date;
	private Chat chat;
	private User forward_from;
	private int forward_date;
	private Message reply_to_message;
	private String text;
	private MessageEntity[] entities;
	private ReplyKeyboardMarkup reply_markup;
	private Contact contact;
	
	public Contact getContact ()
	{
		return contact;
	}
	
	public void setContact (Contact contact)
	{
		this.contact = contact;
	}
	
	public ReplyKeyboardMarkup getReply_markup ()
	{
		return reply_markup;
	}
	
	public void setReply_markup (ReplyKeyboardMarkup reply_markup)
	{
		this.reply_markup = reply_markup;
	}
	
	public MessageEntity[] getEntities ()
	{
		return entities;
	}
	
	public void setEntities (MessageEntity[] entities)
	{
		this.entities = entities;
	}

//	private
// TODO Add other fields.
	//TODO this class needs to be completed.
	
	public Chat getChat ()
	{
		return chat;
	}
	
	public void setChat (Chat chat)
	{
		this.chat = chat;
	}
	
	public String getText ()
	{
		return text;
	}
	
	public void setText (String text)
	{
		this.text = text;
	}
	
	public Message getReply_to_message ()
	{
		return reply_to_message;
	}
	
	public void setReply_to_message (Message reply_to_message)
	{
		this.reply_to_message = reply_to_message;
	}
	
	public int getMessage_id ()
	{
		return message_id;
	}
	
	public void setMessage_id (int message_id)
	{
		this.message_id = message_id;
	}
	
	public User getFrom ()
	{
		return from;
	}
	
	public void setFrom (User from)
	{
		this.from = from;
	}
	
	public User getForward_from ()
	{
		return forward_from;
	}
	
	public void setForward_from (User forward_from)
	{
		this.forward_from = forward_from;
	}
	
	public int getForward_date ()
	{
		return forward_date;
	}
	
	public void setForward_date (int forward_date)
	{
		this.forward_date = forward_date;
	}
	
	public int getDate ()
	{
		return date;
	}
	
	public void setDate (int date)
	{
		this.date = date;
	}
	
}
