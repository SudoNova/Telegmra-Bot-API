package org.telegram.objects;

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
	
	public String getFile_path ()
	{
		return file_path;
	}
	
	public void setFile_path (String file_path)
	{
		this.file_path = file_path;
	}
	
	public String getFile_id ()
	{
		return file_id;
	}
	
	public void setFile_id (String file_id)
	{
		this.file_id = file_id;
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
	
	public int getFile_size ()
	{
		return file_size;
	}
	
	public void setFile_size (int file_size)
	{
		this.file_size = file_size;
	}
}
