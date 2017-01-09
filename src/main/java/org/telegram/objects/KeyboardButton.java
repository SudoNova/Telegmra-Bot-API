package org.telegram.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by HellScre4m on 12/5/2016.
 */
public class KeyboardButton implements TObject
{
	private String text;
	private boolean request_contact;
	private boolean request_location;
	
	@JsonGetter ("text")
	public String getText ()
	{
		return text;
	}
	
	@JsonSetter ("text")
	public KeyboardButton setText (String text)
	{
		this.text = text;
		return this;
	}
	
	@JsonGetter ("request_contact")
	public boolean isRequest_contact ()
	{
		return request_contact;
	}
	
	@JsonSetter ("request_contact")
	public KeyboardButton setRequest_contact (boolean request_contact)
	{
		this.request_contact = request_contact;
		return this;
	}
	
	@JsonGetter ("request_location")
	public boolean isRequest_location ()
	{
		return request_location;
	}
	
	@JsonSetter ("request_location")
	public KeyboardButton setRequest_location (boolean request_location)
	{
		this.request_location = request_location;
		return this;
	}
	
}
