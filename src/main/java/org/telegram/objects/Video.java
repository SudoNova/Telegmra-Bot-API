package org.telegram.objects;

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
	
	public String getFile_id ()
	{
		return file_id;
	}
	
	public void setFile_id (String file_id)
	{
		this.file_id = file_id;
	}
	
	public String getMime_type ()
	{
		return mime_type;
	}
	
	public void setMime_type (String mime_type)
	{
		this.mime_type = mime_type;
	}
	
	public int getWidth ()
	{
		return width;
	}
	
	public void setWidth (int width)
	{
		this.width = width;
	}
	
	public int getHeight ()
	{
		return height;
	}
	
	public void setHeight (int height)
	{
		this.height = height;
	}
	
	public int getDuration ()
	{
		return duration;
	}
	
	public void setDuration (int duration)
	{
		this.duration = duration;
	}
	
	public int getFile_size ()
	{
		return file_size;
	}
	
	public void setFile_size (int file_size)
	{
		this.file_size = file_size;
	}
	
	public PhotoSize getThumb ()
	{
		return thumb;
	}
	
	public void setThumb (PhotoSize thumb)
	{
		this.thumb = thumb;
	}
}
