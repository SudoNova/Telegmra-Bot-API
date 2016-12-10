package org.telegram.objects;

/**
 * Created by HellScre4m on 5/4/2016.
 */
public class Result
{
	private TObject[] result;
	private boolean ok;
	private int error_code;
	private String description;
	
	public int getError_code ()
	{
		return error_code;
	}
	
	public void setError_code (int error_code)
	{
		this.error_code = error_code;
	}
	
	public String getDescription ()
	{
		return description;
	}
	
	public void setDescription (String description)
	{
		this.description = description;
	}
	
	public TObject[] getResult ()
	{
		return result;
	}
	
	public void setResult (TObject... result)
	{
		this.result = result;
	}
	
	public boolean getOk ()
	{
		return ok;
	}
	
	public void setOk (boolean ok)
	{
		this.ok = ok;
	}
	
}
