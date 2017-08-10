package org.telegram.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by HellScre4m on 5/4/2016.
 */
public class Result extends Exception implements TObject
{
	private TObject[] result;
	private boolean ok;
	private int error_code;
	private String description;
	
	@JsonGetter ("error_code")
	public int getError_code ()
	{
		return error_code;
	}
	
	@JsonSetter ("error_code")
	public Result setError_code (int error_code)
	{
		this.error_code = error_code;
		return this;
	}
	
	@JsonGetter ("description")
	public String getDescription ()
	{
		return description;
	}
	
	@JsonSetter ("description")
	public Result setDescription (String description)
	{
		this.description = description;
		return this;
	}
	
	@JsonGetter ("result")
	public TObject[] getResult ()
	{
		return result;
	}
	
	@JsonSetter ("result")
	public Result setResult (TObject... result)
	{
		this.result = result;
		return this;
	}
	
	@JsonGetter ("ok")
	public boolean isOk ()
	{
		return ok;
	}
	
	@JsonSetter ("ok")
	public Result setOk (boolean ok)
	{
		this.ok = ok;
		return this;
	}
	
	@Override
	public String getMessage ()
	{
		return description;
	}
}
