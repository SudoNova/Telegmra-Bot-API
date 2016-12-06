package org.telegram.objects;

/**
 * Created by HellScre4m on 12/5/2016.
 */
public class ReplyKeyboardMarkup implements ReplyMarkup
{
	private KeyboardButton[][] keyboard;
	private boolean resize_keyboard;
	private boolean one_time_keyboard;
	private boolean selective;
	
	public KeyboardButton[][] getKeyboard ()
	{
		return keyboard;
	}
	
	public void setKeyboard (KeyboardButton[][] keyboard)
	{
		this.keyboard = keyboard;
	}
	
	public boolean isResize_keyboard ()
	{
		return resize_keyboard;
	}
	
	public void setResize_keyboard (boolean resize_keyboard)
	{
		this.resize_keyboard = resize_keyboard;
	}
	
	public boolean isOne_time_keyboard ()
	{
		return one_time_keyboard;
	}
	
	public void setOne_time_keyboard (boolean one_time_keyboard)
	{
		this.one_time_keyboard = one_time_keyboard;
	}
	
	public boolean isSelective ()
	{
		return selective;
	}
	
	public void setSelective (boolean selective)
	{
		this.selective = selective;
	}
}
