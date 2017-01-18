package com.sunova.prebuilt;


/**
 * Created by HellScre4m on 12/7/2016.
 */
public final class Messages
{
	public static final String WELCOME;
	public static final String RESEND_PHONE_NUMBER;
	public static final String PHONE_NUMBER_CONFIRMED;
	public static final String POST_VIEW;
	public static final String POST_ORDER;
	public static final String MEMBER_ORDER;
	public static final String CONTACT_US;
	public static final String MAIN_MENU;
	public static final String POST_SEND;
	public static final String RETURN_TO_MAIN;
	public static final String POST_ENTER_AMOUNT;
	public static final String POST_CONFIRM_ORDER;
	public static final String AMOUNT_EXCEEDS;
	public static final String WELCOME_BACK;
	public static final String YES;
	public static final String NO;
	public static final String NEXT;
	public static final String PREVIOUS;
	public static final String REQUEST_DONE;
	public static final String LOW_CREDITS;
	public static final String POST_NO_POSTS;
	public static final String VIEW_AGAIN;
	public static final String VIEW_NOTE;
	public static final String VIEW_CONFIRMED;
	public static final String POST_INVALID_POST;
	public static final String NEW_REFERRED_USER;
	public static final String TRACK;
	public final static String REFERRAL_LINK;
	public static final String TRACK_CHOOSE;
	public static final String TRACK_MEMBER_REQUESTS;
	public static final String TRACK_POST_REQUESTS;
	public static final String TRACK_NO_MORE;
	public static final String TRACK_NO_ORDERS;
	public static final String TRACK_POST_TEMPLATE;
	public static final String POST_EXISTS_NO_MORE;
	public static final String REFERRAL_NOTE;
	
	static
	{
//		String temp = "";
//		try
//		{
		WELCOME = " خوش اومدی {first} {last}\n لطفا شماره تلفنت رو بده!";
		RESEND_PHONE_NUMBER = "بدون شماره تلفنت نمی‌تونم ادامه بدم.\n لطف کن شماره تلفنت رو بفرست.";
		PHONE_NUMBER_CONFIRMED =
				"تایید شد. حالا می‌تونی از امکانات روبات استفاده کنی";
		MAIN_MENU = "شما هم اکنون {coins} سکه دارید.\nیکی از گزینه‌های زیر رو انتخاب کن:";
		POST_VIEW = "بازدید از پست‌ها";
		POST_ORDER = "سفارش بازدید";
		CONTACT_US = "تماس با ما";
		POST_SEND = "\uD83D\uDC48 لطفا لینک پستی که قصد افزایش بازدید آن را دارید ارسال کنید، یا آن پست را فوروارد نمایید. محدودیت\u200Cهایی در ارسال پست وجود دارد که برای شناخت آن\u200Cها پیشنهاد می\u200Cشود این راهنما را به صورت کامل بخوانید:\n" +
				"\n" +
				"❗️ به خاطر داشته باشید که لینک پست را \"نباید\" فوروارد کنید زیرا در این صورت خود لینک به عنوان پست در نظر گرفته خواهد شد.\n" +
				"\n" +
				"❗️ همچنین دقت فرمایید که اگر قصد ارسال پست را دارید (و می\u200Cخواهید ربات آن را به صورت لینک شناسایی نکند) حتما باید آن را \"فوروارد نمایید\". پست\u200Cهایی که فورواردی نباشند (کپی پیست شوند) در حال حاضر پشتیبانی نمی\u200Cشوند. \n" +
				"\n" +
				"❗️ با توجه به نکته\u200Cی قبلی، اگر قصد دارید که پیام از جانب شما فوروارد  " +
				"نشود، ابتدا آن را در یک کانال خصوصی یا ربات بینام ارسال کرده و سپس آن را به ربات ما فوروارد کنید.\n" +
				"\n" +
				"\uD83D\uDC48 اگر شما پیامی را از از یک شخص فوروارد نمایید (نه یک کانال) ربات پیام فوروارد شده\u200Cی شما را داخل یک کانال خصوصی فوروارد می\u200Cکند سپس آن را ثبت می\u200Cکند.\n" +
				"\n" +
				"✅ بنابراین این اطمینان حاصل خواهد شد که اگر شما پستی را فوروارد کنید (چه از یک کانال چه از یک کاربر) حتما علامت \uD83D\uDC41\u200D\uD83D\uDDE8 زیر آن پست وجود دارد. حال اگر پست شما از پیش دارای این علامت بوده مقدار بازدید همان مقدار قبلی خواهد بود، در غیر این صورت مقدار بازدید اولیه پست شما برابر با ۱ است. \n" +
				"\n" +
				"✅  در پایان ربات برای اطمینان، در صورتی که مشکلی در ارسال پست به ربات وجود نداشته باشد، آن را برای شما فوروارد می\u200Cکند تا آن را تایید کنید.";
		RETURN_TO_MAIN = "انصراف و بازگشت به منوی اصلی";
		POST_ENTER_AMOUNT = "لطفا تعداد بازدید " +
				"مورد نظر را وارد کنید. هزینه هر بازدید دو سکه است.\n شما در حال حاضر {coins} سکه دارید.";
		POST_CONFIRM_ORDER = "شما قصد خرید {amount} بازدید به ارزش {value} سکه را دارید.\nآیا از سفارش خود اطمینان دارید؟";
		AMOUNT_EXCEEDS = "مقدار سفارش داده شده از اعتبار حساب شما بیش‌تر است. لطفا مقدار کم‌تری وارد کنید.";
		YES = "بله";
		NO = "خیر";
		REQUEST_DONE = "درخواست شما با موفقیت ثبت شد.";
		LOW_CREDITS = "اعتبار شما برای سفارش کافی نیست. لطفا از " +
				"طریق یکی از سه راه زیر اعتبار خود را افزایش دهید:\n۱- بازدید پست\n۲- عضویت در کانال\n۳- خرید سکه";
		POST_NO_POSTS = "در حال حاضر پستی جهت نمایش موجود نیست :(\n لطفا بعدا دوباره امتحان کنید.";
		VIEW_AGAIN = "تایید و ادامه (موجودی: {coins} سکه)";
		VIEW_CONFIRMED = "تایید و بازگشت به منوی اصلی";
		VIEW_NOTE = "لطفا توجه کنید تا زمانی که بازدید را تایید نکنید سکه به حسابتان واریز نخواهد شد";
		POST_INVALID_POST = "پست یا لینکی که ارسال نمودید معتبر نیست.";
		WELCOME_BACK = "شما قبلا در ربات عضو بودید. خوش برگشتید";
		NEW_REFERRED_USER = "کاربر جدیدی با لینک شما ثبت نام کرد. {coins} سکه به حساب شما واریز شد." +
				"\nضمن این که هر میزان سکه که توسط این کاربر خریداری شود، ۵ درصد آن به عنوان اشانتیون " +
				"به شما تقدیم خواهد شد.";
		TRACK = "پیگیری وضعیت سفارشات";
		MEMBER_ORDER = "سفارش عضو (به زودی)";
		REFERRAL_LINK = "لینک معرفی شما";
		TRACK_CHOOSE = "پیگیری وضعیت سفارشات. لطفا یکی از گزینه‌های زیر را انتخاب کنید:";
		TRACK_MEMBER_REQUESTS = "سفارشات ممبر (به زودی)";
		TRACK_POST_REQUESTS = "سفارشات بازدید";
		NEXT = "بعدی";
		PREVIOUS = "قبلی";
		TRACK_NO_MORE = "سفارشی جهت نمایش وجود ندارد";
		TRACK_POST_TEMPLATE = "شناسه سفارش: {id}\n" +
				"تاریخ ثبت: {date}\n" +
				"میزان سفارش: {amount}\n" +
				"بازدید انجام شده: {viewCount}\n" +
				"بازدید باقیمانده: {remaining}\n";
		TRACK_NO_ORDERS = "شما تاکنون سفارشی ثبت نکرده‌اید";
		POST_EXISTS_NO_MORE = "به نظر می‌آید این پست دیگر در تلگرام موجود نیست (پاک شده است)";
		REFERRAL_NOTE = "این لینک معرفی شماست. توجه داشته باشید هر کاربری که با لینک معرفی شما در ربات ثبت نام کند، {coins} سکه به شما تعلق خواهد گرفت. ضمن این که ۵ درصد از مبلغ خرید " +
				"وی در صورتی که از این بات خریدی انجام دهد به موجودی شما افزوده خواهد شد.\nلینک معرفی شما:\n";
//		}

//		catch (UnsupportedEncodingException e)
//		{
//			e.printStackTrace();
//		}
//		WELCOME = temp;
//		System.out.println(temp);
		
	}
}
