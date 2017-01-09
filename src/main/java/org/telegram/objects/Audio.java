package org.telegram.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by HellScre4m on 12/9/2016.
 */
public class Audio implements TObject
{
	private String file_id;
	private int duration;
	private int file_size;
	private String performer;
	private String title;
	private String mime_type;
	
	@JsonGetter ("file_id")
	public String getFile_id ()
	{
		return file_id;
	}
	
	@JsonSetter ("file_id")
	public Audio setFile_id (String file_id)
	{
		this.file_id = file_id;
		return this;
	}
	
	@JsonGetter ("duration")
	public int getDuration ()
	{
		return duration;
	}
	
	@JsonSetter ("duration")
	public Audio setDuration (int duration)
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
	public Audio setFile_size (int file_size)
	{
		this.file_size = file_size;
		return this;
	}
	
	@JsonGetter ("performer")
	public String getPerformer ()
	{
		return performer;
	}
	
	@JsonSetter ("performer")
	public Audio setPerformer (String performer)
	{
		this.performer = performer;
		return this;
	}
	
	@JsonGetter ("title")
	public String getTitle ()
	{
		return title;
	}
	
	@JsonSetter ("title")
	public Audio setTitle (String title)
	{
		this.title = title;
		return this;
	}
	
	@JsonGetter ("mime_type")
	public String getMime_type ()
	{
		return mime_type;
	}
	
	@JsonSetter ("mime_type")
	public Audio setMime_type (String mime_type)
	{
		this.mime_type = mime_type;
		return this;
	}
}
