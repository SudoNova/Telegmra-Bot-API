package org.telegram.objects;

/**
 * Created by HellScre4m on 5/2/2016.
 */
public class Update implements TObject

{
	private int update_id;
	private Message message;
	//TODO add other fields later - optional fields
	
	public int getUpdate_id ()
	{
		return update_id;
	}
	
	public void setUpdate_id (int update_id)
	{
		this.update_id = update_id;
	}
	
	public Message getMessage ()
	{
		return message;
	}
	
	public void setMessage (Message message)
	{
		this.message = message;
	}
	
	public boolean containsMessage ()
	{
		return message != null;
	}
	
}
