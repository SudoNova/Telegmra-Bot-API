package com.sunova.prebuilt;

import org.telegram.objects.KeyboardButton;
import org.telegram.objects.ReplyKeyboardMarkup;

/**
 * Created by HellScre4m on 12/5/2016.
 */
public final class Keyboards
{
	public static final ReplyKeyboardMarkup GET_PHONE;
	public static final ReplyKeyboardMarkup MAIN_MENU;
	
	static
	{
		GET_PHONE = new ReplyKeyboardMarkup();
		KeyboardButton[][] buttons = new KeyboardButton[1][1];
		KeyboardButton button = buttons[0][0] = new KeyboardButton();
		button.setText("ارسال شماره تلفن");
		button.setRequest_contact(true);
		GET_PHONE.setResize_keyboard(true);
		GET_PHONE.setKeyboard(buttons);
		
		MAIN_MENU = new ReplyKeyboardMarkup();
		buttons = new KeyboardButton[2][2];
		button = buttons[0][0] = new KeyboardButton();
		button.setText(Messages.VIEW_POSTS);
		button = buttons[0][1] = new KeyboardButton();
		button.setText(Messages.REGISTER_POST);
		button = buttons[1][0] = new KeyboardButton();
		button.setText(Messages.CONTACT_US);
		button = buttons[1][1] = new KeyboardButton();
		button.setText("به زودی");
		MAIN_MENU.setKeyboard(buttons);
		MAIN_MENU.setResize_keyboard(true);
//		GET_PHONE.
	}
}
