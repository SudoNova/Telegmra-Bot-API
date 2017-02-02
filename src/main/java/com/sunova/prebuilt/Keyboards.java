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
	public static final ReplyKeyboardMarkup ENTER_ORDER;
	public static final ReplyKeyboardMarkup ENTER_INPUT;
	public static final ReplyKeyboardMarkup CONFIRM;
	public static final ReplyKeyboardMarkup POST_CONFIRM_ORDER;
	public static final ReplyKeyboardMarkup TRACK_CHOOSE;
//	public static final ReplyKeyboardMarkup TRACK_NEXT;
	
	static
	{
		GET_PHONE = new ReplyKeyboardMarkup();
		KeyboardButton[][] buttons = new KeyboardButton[1][1];
		buttons[0][0] = new KeyboardButton().setText("ارسال شماره تلفن").setRequest_contact(true);
		GET_PHONE.setResize_keyboard(true);
		GET_PHONE.setKeyboard(buttons);
		
		MAIN_MENU = new ReplyKeyboardMarkup();
		buttons = new KeyboardButton[2][4];
		buttons[0][3] = new KeyboardButton().setText(Messages.CHANNEL_REGISTER);
		buttons[0][2] = new KeyboardButton().setText(Messages.POST_VIEW);
		buttons[0][0] = new KeyboardButton().setText(Messages.POST_ORDER);
		buttons[0][1] = new KeyboardButton().setText(Messages.CHANNEL_ORDER);
		buttons[1][3] = new KeyboardButton().setText(Messages.TRACK);
		buttons[1][2] = new KeyboardButton().setText(Messages.REFERRAL_LINK);
		buttons[1][1] = new KeyboardButton().setText(Messages.CONTACT_US);
		buttons[1][0] = new KeyboardButton().setText(Messages.OUR_CHANNEL);
		MAIN_MENU.setKeyboard(buttons);
		MAIN_MENU.setResize_keyboard(true);
		
		ENTER_ORDER = new ReplyKeyboardMarkup();
		buttons = new KeyboardButton[1][1];
		buttons[0][0] = new KeyboardButton().setText(Messages.RETURN_TO_MAIN);
		ENTER_ORDER.setKeyboard(buttons);
		ENTER_ORDER.setResize_keyboard(true);
		
		ENTER_INPUT = new ReplyKeyboardMarkup();
		buttons = new KeyboardButton[1][1];
		buttons[0][0] = new KeyboardButton().setText(Messages.RETURN_TO_MAIN);
		ENTER_INPUT.setKeyboard(buttons);
		ENTER_INPUT.setResize_keyboard(true);
		
		CONFIRM = new ReplyKeyboardMarkup();
		buttons = new KeyboardButton[2][1];
		buttons[0][0] = new KeyboardButton().setText(Messages.YES);
		buttons[1][0] = new KeyboardButton().setText(Messages.NO);
		CONFIRM.setKeyboard(buttons);
		CONFIRM.setResize_keyboard(true);
		
		POST_CONFIRM_ORDER = new ReplyKeyboardMarkup();
		buttons = new KeyboardButton[2][1];
		buttons[0][0] = new KeyboardButton().setText(Messages.POST_VIEW_AGAIN);
		buttons[1][0] = new KeyboardButton().setText(Messages.POST_VIEW_CONFIRMED);
		POST_CONFIRM_ORDER.setKeyboard(buttons);
		POST_CONFIRM_ORDER.setResize_keyboard(true);
		//		GET_PHONE.
		TRACK_CHOOSE = new ReplyKeyboardMarkup().setResize_keyboard(true);
		buttons = new KeyboardButton[3][1];
		buttons[2][0] = new KeyboardButton().setText(Messages.RETURN_TO_MAIN);
		buttons[1][0] = new KeyboardButton().setText(Messages.TRACK_MEMBER_REQUESTS);
		buttons[0][0] = new KeyboardButton().setText(Messages.TRACK_POST_REQUESTS);
		TRACK_CHOOSE.setKeyboard(buttons);
		
//		TRACK_NEXT = new ReplyKeyboardMarkup().setResize_keyboard(true);
////		buttons = new KeyboardButton[2][1];
////		buttons[0][0] = new KeyboardButton().setText(Messages.NEXT);
////		buttons[1][0] = new KeyboardButton().setText(Messages.RETURN_TO_MAIN);
//		TRACK_NEXT.setKeyboard(buttons);
	}
}
