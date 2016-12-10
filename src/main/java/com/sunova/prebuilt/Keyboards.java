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
	public static final ReplyKeyboardMarkup SEND_POST;
	public static final ReplyKeyboardMarkup ENTER_INPUT;
	public static final ReplyKeyboardMarkup CONFIRM;
	
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
		
		SEND_POST = new ReplyKeyboardMarkup();
		buttons = new KeyboardButton[1][1];
		button = buttons[0][0] = new KeyboardButton();
		button.setText(Messages.RETURN_TO_MAIN);
		SEND_POST.setKeyboard(buttons);
		SEND_POST.setResize_keyboard(true);
		
		ENTER_INPUT = new ReplyKeyboardMarkup();
		buttons = new KeyboardButton[1][1];
		button = buttons[0][0] = new KeyboardButton();
		button.setText("انصراف و بازگشت به منوی قبلی");
		ENTER_INPUT.setKeyboard(buttons);
		ENTER_INPUT.setResize_keyboard(true);
		
		CONFIRM = new ReplyKeyboardMarkup();
		buttons = new KeyboardButton[2][1];
		button = buttons[0][0] = new KeyboardButton();
		button.setText(Messages.YES);
		button = buttons[1][0] = new KeyboardButton();
		button.setText(Messages.NO);
		CONFIRM.setKeyboard(buttons);
		CONFIRM.setResize_keyboard(true);
		//		GET_PHONE.
	}
}
