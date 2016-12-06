package com.sunova.prebuilt;

import org.telegram.objects.KeyboardButton;
import org.telegram.objects.ReplyKeyboardMarkup;

/**
 * Created by HellScre4m on 12/5/2016.
 */
public final class Keyboards
{
	public static final ReplyKeyboardMarkup GET_PHONE;
	
	static
	{
		GET_PHONE = new ReplyKeyboardMarkup();
		KeyboardButton[][] buttons = new KeyboardButton[1][1];
		buttons[0][0].setText("ارسال شماره تلفن");
		buttons[0][0].setRequest_contact(true);
		GET_PHONE.setKeyboard(buttons);

//		GET_PHONE.
	}
}
