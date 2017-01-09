package org.telegram.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by HellScre4m on 12/9/2016.
 */
public class Animation implements TObject
{
	private int file_size;
	private String file_id;
	private String file_name;
	private String mime_type;
	private PhotoSize thumb;
	
	@JsonGetter ("file_size")
	public int getFile_size ()
	{
		return file_size;
	}
	
	@JsonSetter ("file_size")
	public Animation setFile_size (int file_size)
	{
		this.file_size = file_size;
		return this;
	}
	
	@JsonGetter ("file_id")
	public String getFile_id ()
	{
		return file_id;
	}
	
	@JsonSetter ("file_id")
	public Animation setFile_id (String file_id)
	{
		this.file_id = file_id;
		return this;
	}
	
	@JsonGetter ("file_name")
	public String getFile_name ()
	{
		return file_name;
	}
	
	@JsonSetter ("file_name")
	public Animation setFile_name (String file_name)
	{
		this.file_name = file_name;
		return this;
	}
	
	@JsonGetter ("mime_type")
	public String getMime_type ()
	{
		return mime_type;
	}
	
	@JsonSetter ("mime_type")
	public Animation setMime_type (String mime_type)
	{
		this.mime_type = mime_type;
		return this;
	}
	
	@JsonGetter ("thumb")
	public PhotoSize getThumb ()
	{
		return thumb;
	}
	
	@JsonSetter ("thumb")
	public Animation setThumb (PhotoSize thumb)
	{
		this.thumb = thumb;
		return this;
	}
}
