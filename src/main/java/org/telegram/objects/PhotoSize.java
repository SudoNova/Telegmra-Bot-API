package org.telegram.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by HellScre4m on 12/9/2016.
 */
public class PhotoSize implements TObject
{
	private String file_id;
	private String file_path;
	private int width;
	private int height;
	private int file_size;
	
	@JsonGetter ("file_path")
	public String getFile_path ()
	{
		return file_path;
	}
	
	@JsonSetter ("file_path")
	public PhotoSize setFile_path (String file_path)
	{
		this.file_path = file_path;
		return this;
	}
	
	@JsonGetter ("file_id")
	public String getFile_id ()
	{
		return file_id;
	}
	
	@JsonSetter ("file_id")
	public PhotoSize setFile_id (String file_id)
	{
		this.file_id = file_id;
		return this;
	}
	
	@JsonGetter ("width")
	public int getWidth ()
	{
		return width;
	}
	
	@JsonSetter ("width")
	public PhotoSize setWidth (int width)
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
	public PhotoSize setHeight (int height)
	{
		this.height = height;
		return this;
	}
	
	@JsonGetter ("file_size")
	public int getFile_size ()
	{
		return file_size;
	}
	
	@JsonSetter ("file_size")
	public PhotoSize setFile_size (int file_size)
	{
		this.file_size = file_size;
		return this;
	}
}
