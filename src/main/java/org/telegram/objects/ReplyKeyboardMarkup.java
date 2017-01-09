package org.telegram.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by HellScre4m on 12/5/2016.
 */
public class ReplyKeyboardMarkup implements ReplyMarkup
{
	private KeyboardButton[][] keyboard;
	private boolean resize_keyboard;
	private boolean one_time_keyboard;
	private boolean selective;
	
	@JsonGetter ("keyboard")
	public KeyboardButton[][] getKeyboard ()
	{
		return keyboard;
	}
	
	@JsonSetter ("keyboard")
	public ReplyKeyboardMarkup setKeyboard (KeyboardButton[][] keyboard)
	{
		this.keyboard = keyboard;
		return this;
	}
	
	@JsonGetter ("resize_keyboard")
	public boolean isResize_keyboard ()
	{
		return resize_keyboard;
	}
	
	@JsonSetter ("resize_keyboard")
	public ReplyKeyboardMarkup setResize_keyboard (boolean resize_keyboard)
	{
		this.resize_keyboard = resize_keyboard;
		return this;
	}
	
	@JsonGetter ("one_time_keyboard")
	public boolean isOne_time_keyboard ()
	{
		return one_time_keyboard;
	}
	
	@JsonSetter ("one_time_keyboard")
	public ReplyKeyboardMarkup setOne_time_keyboard (boolean one_time_keyboard)
	{
		this.one_time_keyboard = one_time_keyboard;
		return this;
	}
	
	@JsonGetter ("selective")
	public boolean isSelective ()
	{
		return selective;
	}
	
	@JsonSetter ("selective")
	public ReplyKeyboardMarkup setSelective (boolean selective)
	{
		this.selective = selective;
		return this;
	}
}
