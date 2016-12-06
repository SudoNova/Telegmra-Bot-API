package org.telegram.objects;

/**
 * Created by HellScre4m on 12/5/2016.
 */
public class KeyboardButton
{
	private String text;
	private boolean request_contact;
	private boolean request_location;
	
	
	public String getText ()
	{
		return text;
	}
	
	public void setText (String text)
	{
		this.text = text;
	}
	
	public boolean isRequest_contact ()
	{
		return request_contact;
	}
	
	public void setRequest_contact (boolean request_contact)
	{
		this.request_contact = request_contact;
	}
	
	public boolean isRequest_location ()
	{
		return request_location;
	}
	
	public void setRequest_location (boolean request_location)
	{
		this.request_location = request_location;
	}
	
}
