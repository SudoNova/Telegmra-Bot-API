package org.telegram.objects;

/**
 * Created by HellScre4m on 5/4/2016.
 */
public class Result
{
	private TObject[] result;
	private boolean ok;
	
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
