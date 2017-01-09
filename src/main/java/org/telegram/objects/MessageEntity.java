package org.telegram.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by HellScre4m on 5/2/2016.
 */
public class MessageEntity implements TObject
{
	private String type;
	private int offset;
	private int length;
	private String url;
	
	@JsonGetter ("type")
	public String getType ()
	{
		return type;
	}
	
	@JsonSetter ("type")
	public MessageEntity setType (String type)
	{
		this.type = type;
		return this;
	}
	
	@JsonGetter ("length")
	public int getLength ()
	{
		return length;
	}
	
	@JsonSetter ("length")
	public MessageEntity setLength (int length)
	{
		this.length = length;
		return this;
	}
	
	@JsonGetter ("offset")
	public int getOffset ()
	{
		return offset;
	}
	
	@JsonSetter ("offset")
	public MessageEntity setOffset (int offset)
	{
		this.offset = offset;
		return this;
	}
	
	@JsonGetter ("url")
	public String getUrl ()
	{
		return url;
	}
	
	@JsonSetter ("url")
	public MessageEntity setUrl (String url)
	{
		this.url = url;
		return this;
	}
	
}
