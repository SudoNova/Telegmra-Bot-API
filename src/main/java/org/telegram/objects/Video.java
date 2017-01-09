package org.telegram.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by HellScre4m on 12/9/2016.
 */
public class Video implements TObject
{
	private String file_id;
	private String mime_type;
	private int width;
	private int height;
	private int duration;
	private int file_size;
	private PhotoSize thumb;
	
	@JsonGetter ("file_id")
	public String getFile_id ()
	{
		return file_id;
	}
	
	@JsonSetter ("file_id")
	public Video setFile_id (String file_id)
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
	public Video setMime_type (String mime_type)
	{
		this.mime_type = mime_type;
		return this;
	}
	
	@JsonGetter ("width")
	public int getWidth ()
	{
		return width;
	}
	
	@JsonSetter ("width")
	public Video setWidth (int width)
	{
		this.width = width;
		return this;
	}
	
	@JsonGetter ("height")
	public int getHeight ()
	{
		return height;
	}
	
	@JsonSetter ("height")
	public Video setHeight (int height)
	{
		this.height = height;
		return this;
	}
	
	@JsonGetter ("duration")
	public int getDuration ()
	{
		return duration;
	}
	
	@JsonSetter ("duration")
	public Video setDuration (int duration)
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
	public Video setFile_size (int file_size)
	{
		this.file_size = file_size;
		return this;
	}
	
	@JsonGetter ("thumb")
	public PhotoSize getThumb ()
	{
		return thumb;
	}
	
	@JsonSetter ("thumb")
	public Video setThumb (PhotoSize thumb)
	{
		this.thumb = thumb;
		return this;
	}
}
