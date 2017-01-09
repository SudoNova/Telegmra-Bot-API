package org.telegram.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by HellScre4m on 12/9/2016.
 */
public class Sticker implements TObject
{
	private String file_id;
	private String emoji;
	private int width;
	private int height;
	private int file_size;
	private PhotoSize thumb;
	
	@JsonGetter ("file_id")
	public String getFile_id ()
	{
		return file_id;
	}
	
	@JsonSetter ("file_id")
	public Sticker setFile_id (String file_id)
	{
		this.file_id = file_id;
		return this;
	}
	
	@JsonGetter ("emoji")
	public String getEmoji ()
	{
		return emoji;
	}
	
	@JsonSetter ("emoji")
	public Sticker setEmoji (String emoji)
	{
		this.emoji = emoji;
		return this;
	}
	
	@JsonGetter ("width")
	public int getWidth ()
	{
		return width;
	}
	
	@JsonSetter ("width")
	public Sticker setWidth (int width)
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
	public Sticker setHeight (int height)
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
	public Sticker setFile_size (int file_size)
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
	public Sticker setThumb (PhotoSize thumb)
	{
		this.thumb = thumb;
		return this;
	}
}
