package org.telegram.objects;

/**
 * Created by HellScre4m on 12/9/2016.
 */
public class Document implements TObject
{
	private String field_id;
	private String file_name;
	private String mime_type;
	private int file_size;
	private PhotoSize thumb;
	
	public String getField_id ()
	{
		return field_id;
	}
	
	public void setField_id (String field_id)
	{
		this.field_id = field_id;
	}
	
	public String getFile_name ()
	{
		return file_name;
	}
	
	public void setFile_name (String file_name)
	{
		this.file_name = file_name;
	}
	
	public String getMime_type ()
	{
		return mime_type;
	}
	
	public void setMime_type (String mime_type)
	{
		this.mime_type = mime_type;
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
