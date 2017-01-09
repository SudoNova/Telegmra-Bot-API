package org.telegram.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by HellScre4m on 12/9/2016.
 */
public class Voice implements TObject
{
	private String file_id;
	private String mime_type;
	private int duration;
	private int file_size;
	
	@JsonGetter ("file_id")
	public String getFile_id ()
	{
		return file_id;
	}
	
	@JsonSetter ("file_id")
	public Voice setFile_id (String file_id)
	{
		this.file_id = file_id;
		return this;
	}
	
	@JsonGetter ("mime_type")
	public String getMime_type ()
	{
		return mime_type;
	}
	
	@JsonSetter ("mime_type")
	public Voice setMime_type (String mime_type)
	{
		this.mime_type = mime_type;
		return this;
	}
	
	@JsonGetter ("duration")
	public int getDuration ()
	{
		return duration;
	}
	
	@JsonSetter ("duration")
	public Voice setDuration (int duration)
	{
		this.duration = duration;
		return this;
	}
	
	@JsonGetter ("file_size")
	public int getFile_size ()
	{
		return file_size;
	}
	
	@JsonSetter ("file_size")
	public Voice setFile_size (int file_size)
	{
		this.file_size = file_size;
		return this;
	}
}
