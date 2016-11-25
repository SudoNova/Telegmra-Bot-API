package org.telegram.objects;

/**
 * Created by HellScre4m on 5/2/2016.
 */
public class MessageEntity implements TObject
{
	String ObjectType = "message_entity";
	private String type;
	private int offset;
	private int length;
	private String url;
	
	public String getType ()
	{
		return type;
	}
	
	public void setType (String type)
	{
		this.type = type;
	}
	
	public int getLength ()
	{
		
		return length;
	}
	
	public void setLength (int length)
	{
		this.length = length;
	}
	
	public int getOffset ()
	{
		return offset;
	}
	
	public void setOffset (int offset)
	{
		this.offset = offset;
	}
	
	public String getOperation ()
	{
		return type;
	}
	
	public String getUrl ()
	{
		return url;
	}
	
	public void setUrl (String url)
	{
		this.url = url;
	}
	
}
