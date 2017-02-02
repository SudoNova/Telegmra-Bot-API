package com.sunova.prebuilt;


/**
 * Created by HellScre4m on 12/7/2016.
 */
public final class Messages
{
	public static final String WELCOME;
	public static final String RESEND_PHONE_NUMBER;
	public static final String PHONE_NUMBER_CONFIRMED;
	public static final String CHANNEL_ORDER;
	public static final String CONTACT_US;
	public static final String MAIN_MENU;
	public static final String RETURN_TO_MAIN;
	public static final String AMOUNT_EXCEEDS;
	public static final String WELCOME_BACK;
	public static final String YES;
	public static final String NO;
	public static final String NEXT;
	public static final String PREVIOUS;
	public static final String REQUEST_DONE;
	public static final String LOW_CREDITS;
	
	public static final String POST_VIEW;
	public static final String POST_ORDER;
	public static final String POST_SEND;
	public static final String POST_ENTER_AMOUNT;
	public static final String POST_CONFIRM_ORDER;
	public static final String POST_NO_POSTS;
	public static final String POST_VIEW_AGAIN;
	public static final String POST_VIEW_NOTE;
	public static final String POST_VIEW_CONFIRMED;
	public static final String POST_INVALID_POST;
	public static final String POST_EXISTS_NO_MORE;
	
	public static final String TRACK;
	public static final String TRACK_CHOOSE;
	public static final String TRACK_MEMBER_REQUESTS;
	public static final String TRACK_POST_REQUESTS;
	public static final String TRACK_NO_MORE;
	public static final String TRACK_NO_ORDERS;
	public static final String TRACK_POST_TEMPLATE;
	
	public static final String REFERRAL_LINK;
	public static final String REFERRAL_NOTE;
	public static final String REFERRAL_NEW;
	
	public static final String CHANNEL_PRIVATE;
	public static final String CHANNEL_ENTER;
	public static final String AMOUNT_ZERO;
	public static final String AMOUNT_ONE;
	public static final String CHANNEL_INVALID_LINK;
	public static final String CHANNEL_NOT_ADMIN;
	public static final String CHANNEL_ENTER_AMOUNT;
	public static final String CHANNEL_ENTER_DESCRIPTION;
	public static final String LENGTH_EXCEEDED;
	public static final String CHANNEL_REGISTER;
	public static final String OUR_CHANNEL;
	
	static
	{
//		String temp = "";
//		try
//		{
		WELCOME = " خوش اومدی {first} {last}\n لطفا شماره تلفنت رو بده!\nبرای ارسال شماره تلفن" +
				" نیازی نیست آن را وارد کنی. تنها بر روی دکمه زیر کلیک کن و پیغام را تایید کن" +
				" تا شماره‌ات ارسال شود.";
		RESEND_PHONE_NUMBER = "بدون شماره تلفنت نمی‌تونم ادامه بدم.\n لطف کن شماره تلفنت رو بفرست.";
		PHONE_NUMBER_CONFIRMED =
				"تایید شد. حالا می‌تونی از امکانات روبات استفاده کنی";
		MAIN_MENU = "◀️ منوی اصلی ربات\n" +
				"لطفا توجه " +
				"داشته باشید که ربات در حالت آزمایشی فعال است، بنابراین ممکن است هر لحظه خاموش شود یا به مشکل بخورد.\n" +
				"در صورت بروز مشکل با پشتیبانی برنامه @ViewMemberSupport تماس گرفته گزارش دهید تا سکه جایزه بگیرید.\n" +
				"در هر مرحله با زدن روی عبارت /start به منوی اصلی باز می\u200Cگردید.\n" +
				"کانال رسمی ربات \uD83D\uDC48 @ViewMemberChannel\n" +
				"لطفا یکی از گزینه\u200Cهای زیر را انتخاب کنید (موجودی شما هم اکنون {coins} سکه است)";
		POST_VIEW = "بازدید از پست‌ها";
		POST_ORDER = "سفارش بازدید";
		CONTACT_US = "تماس با ما (پشتیبانی)";
		POST_SEND = "\uD83D\uDC48 پیش از ارسال لطفا دقت کنید. دو راه برای ارسال پست وجود دارد. نخست این که لینک پست را بفرستید (از کانال\u200Cهای عمومی) دوم این که آن را فوروارد کنید (از کانال\u200Cهای عمومی و خصوصی، کاربران و ربات\u200Cها)\n" +
				"\n" +
				"\uD83D\uDC48 اگر لینک را فوروارد کنید به عنوان پست تلقی خواهد شد. همچنین پست\u200Cها را نباید مستقیم ارسال کنید، بلکه باید آن\u200Cها را فوروارد نمایید.\n" +
				"\n" +
				"\uD83D\uDC48 پستی که ارسال می\u200Cکنید تا ۲۴ ساعت به هر کاربر فقط ۱ بار نمایش داده خواهد شد (مگر این که چند بار آن را سفارش دهید) زیرا تلگرام هر ۲۴ ساعت علامت \uD83D\uDC41\u200D\uD83D\uDDE8 زیر پست را برای همان کاربر دوباره اضافه می\u200Cکند\n" +
				"\n" +
				"\uD83D\uDC48 در صورتی که پست از کانال عمومی ارسال شده باشد، حتی اگر آن را چند بار در روز سفارش دهید، قانون ۲۴ ساعت ۱ بازدید برای آن صادق است. بنابراین پیشنهاد می\u200Cکنیم پست\u200Cها را از کانال\u200Cهای عمومی فوروارد نمایید.";
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
		POST_VIEW_AGAIN = "تایید و ادامه (موجودی: {coins} سکه)";
		POST_VIEW_CONFIRMED = "تایید و بازگشت به منوی اصلی";
		POST_VIEW_NOTE = "لطفا توجه کنید تا زمانی که بازدید را تایید نکنید سکه به حسابتان واریز نخواهد شد";
		POST_INVALID_POST = "پست یا لینکی که ارسال نمودید معتبر نیست.";
		WELCOME_BACK = "شما قبلا در ربات عضو بودید. خوش برگشتید";
		REFERRAL_NEW = "کاربر جدیدی با لینک شما ثبت نام کرد. {coins} سکه به حساب شما واریز شد." +
				"\nضمن این که هر میزان سکه که توسط این کاربر خریداری شود، ۵ درصد آن به عنوان اشانتیون " +
				"به شما تقدیم خواهد شد.";
		TRACK = "پیگیری وضعیت سفارشات";
		CHANNEL_ORDER = "سفارش عضو (به زودی)";
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
		REFERRAL_NOTE = "این لینک معرفی شماست. توجه داشته باشید هر کاربری که با لینک " +
				"معرفی شما در ربات ثبت نام کند، {coins} سکه به شما تعلق خواهد گرفت." +
				" ضمن این که ۵ درصد از میزان سکه خریداری شده توسط  " +
				"وی در صورتی که از این بات خریدی انجام دهد به موجودی شما افزوده خواهد شد.\nلینک معرفی شما:\n";
		CHANNEL_PRIVATE = "کانالی که سعی در ثبت آن دارید خصوصی است. کانال‌های خصوصی پشتیبانی نمی‌شوند";
		CHANNEL_ENTER = "لطفا لینک کانال خود را ارسال کنید. کانال‌های خصوصی قابل پذیرش نیستند";
		AMOUNT_ZERO = "لطفا یک مقدار مثبت وارد کنید";
		CHANNEL_INVALID_LINK = "لطفا لینک کانال را به درستی وارد کنید";
		CHANNEL_NOT_ADMIN = "بات ادمین کانال شما نیست. لطفا ابتدا بات را ادمین کنید";
		CHANNEL_ENTER_AMOUNT = "لطفا مقدار را وارد کنید";
		AMOUNT_ONE = "شما فقط تعداد روز را وارد کرده‌اید. لطفا دوباره مقادیر را وارد نمایید.\n" +
				"توجه داشته باشید که باید عدد سمت چپی تعداد روز و عدد سمت راستی تعداد نفرات باشد.";
		CHANNEL_ENTER_DESCRIPTION =
				"شما قصد خرید {persons} عضویت به مدت {days} روز و به ازرش {amount} سکه را دارید.\n" +
						"لطفا توضیحات کانال را وارد بفرمایید. توجه داشته باشید که توضیحات کانال" +
						" نباید بیش از ۲۵۰ کاراکتر باشد.";
		LENGTH_EXCEEDED = "طول توضیحات کانال بیش از ۲۵۰ کاراکتر است. لطفا آن را کوتاه کنید.";
		CHANNEL_REGISTER = "عضویت در کانال‌ها";
		OUR_CHANNEL = "کانال ما";
//		}

//		catch (UnsupportedEncodingException e)
//		{
//			e.printStackTrace();
//		}
//		WELCOME = temp;
//		System.out.println(temp);
		
	}
}
