package org.telegram.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.File;

/**
 * Created by HellScre4m on 12/9/2016.
 */
public class Document implements TObject
{
	private String file_id;
	private String file_name;
	private String mime_type;
	private int file_size;
	private PhotoSize thumb;
	private File file;
	private String file_path;
	
	@JsonGetter ("file")
	public File getFile ()
	{
		return file;
	}
	
	@JsonSetter ("file")
	public Document setFile (File file)
	{
		this.file = file;
		return this;
	}
	
	@JsonGetter ("file_id")
	public String getFile_id ()
	{
		return file_id;
	}
	
	@JsonSetter ("file_id")
	public Document setFile_id (String file_id)
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
	public Document setFile_name (String file_name)
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
	public Document setMime_type (String mime_type)
	{
		this.mime_type = mime_type;
		return this;
	}
	
	@JsonGetter ("file_size")
	public int getFile_size ()
	{
		return file_size;
	}
	
	@JsonSetter ("file_size")
	public Document setFile_size (int file_size)
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
	public Document setThumb (PhotoSize thumb)
	{
		this.thumb = thumb;
		return this;
	}
	
	@JsonGetter ("file_path")
	public String getFile_path ()
	{
		return file_path;
	}
	
	@JsonSetter ("file_path")
	public Document setFile_path (String file_path)
	{
		this.file_path = file_path;
		return this;
	}
}
