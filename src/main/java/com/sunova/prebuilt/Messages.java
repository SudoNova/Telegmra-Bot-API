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
	public static final String CHANNEL_JOIN_CHANNELS;
	public static final String OUR_CHANNEL;
	public static final String CHANNEL_TEMPLATE;
	public static final String CHANNEL_JOIN;
	public static final String CHANNEL_NEXT;
	public static final String CHANNEL_NO_CHANNEL;
	public static final String CHANNEL_JOIN_CONFIRMED;
	public static final String CHANNEL_JOIN_DENIED;
	public static final String CHANNEL_ADMIN_REVOKED;
	public static final String CHANNEL_USER_LEFT_CHANNEL;
	public static final String TRACK_CHANNEL_TEMPLATE;
	public static final String TRACK_JOINS;
	public static final String GUIDANCE;
	public static final String TUTORIALS;
	public static final String CHANNEL_NOT_EXIST;
	
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
				"لطفا توجه داشته باشید که ربات در حالت آزمایشی فعال است، بنابراین ممکن است هر لحظه خاموش شود یا به مشکل بخورد.\n" +
				"در هر مرحله با زدن روی عبارت /start به منوی اصلی باز می\u200Cگردید.\n" +
				"لطفا یکی از گزینه\u200Cهای زیر را انتخاب کنید (موجودی شما هم اکنون {coins} سکه است)";
		POST_VIEW = "بازدید از پست‌ها";
		POST_ORDER = "سفارش بازدید";
		CONTACT_US = "تماس با ما (پشتیبانی)";
		POST_SEND = "\uD83D\uDC48 لطفا پست مورد نظر را فوروارد کنید، یا لینک آن را ارسال نمایید\n" +
				"\uD83D\uDC48 لینک را می\u200Cتوانید فوروارد کنید\n" +
				"\uD83D\uDC48 پست حتما باید به شکل\u200Cهای بالا" +
				" (فوروارد یا ارسال لینک) ارسال شده باشد وگرنه شناسایی نمی شود.";
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
		CHANNEL_ORDER = "سفارش عضو";
		REFERRAL_LINK = "لینک معرفی شما";
		TRACK_CHOOSE = "پیگیری وضعیت سفارشات. لطفا یکی از گزینه‌های زیر را انتخاب کنید:";
		TRACK_MEMBER_REQUESTS = "سفارشات عضو";
		TRACK_POST_REQUESTS = "سفارشات بازدید";
		NEXT = "بعدی";
		PREVIOUS = "قبلی";
		TRACK_NO_MORE = "سفارشی جهت نمایش وجود ندارد";
		TRACK_POST_TEMPLATE = "شناسه سفارش: {id}\n" +
				"تاریخ ثبت: {date}\n" +
				"میزان سفارش: {amount}\n" +
				"بازدید انجام شده: {viewCount}\t\t\t" +
				"بازدید باقیمانده: {remaining}\n";
		TRACK_CHANNEL_TEMPLATE = "شناسه سفارش: {id}\n" +
				"آدرس کانال: @{userName}\n" +
				"تاریخ ثبت: {date}\n" +
				"تعداد (نفر): {persons}\t" +
				"مدت (روز): {days}\n" +
				"کاربر عضو شده: {entered}\t\t\t" +
				"کاربر خارج شده: {left}\n" +
				"سکه بازگردانده شده: {returned}\t\t\t" +
				"کاربر باقی مانده: {remaining}\n";
		TRACK_NO_ORDERS = "شما تاکنون سفارشی ثبت نکرده‌اید";
		POST_EXISTS_NO_MORE = "این پست به دلیل مغایرت با قوانین پاک شده است.";
		REFERRAL_NOTE = "این لینک معرفی شماست. توجه داشته باشید هر کاربری که با لینک " +
				"معرفی شما در ربات ثبت نام کند، {coins} سکه به شما تعلق خواهد گرفت." +
				" ضمن این که ۵ درصد از میزان سکه خریداری شده توسط  " +
				"وی در صورتی که از این بات خریدی انجام دهد به موجودی شما افزوده خواهد شد.\nلینک معرفی شما:\n";
		CHANNEL_PRIVATE = "کانالی که سعی در ثبت آن دارید خصوصی است. کانال‌های خصوصی پشتیبانی نمی‌شوند";
		CHANNEL_ENTER = "\uD83D\uDC48 لطفا لینک یا یوزرنیم کانال خود را ارسال کنید.\n" +
				"\uD83D\uDC48 کانال\u200Cهای خصوصی قابل پذیرش نیستند.\n" +
				"\uD83D\uDC48 پیش از ارسال لینک باید ابتدا ربات ما را در کانال خود ادمین کنید.\n" +
				"\uD83D\uDC48 پس از پایان سفارش می\u200Cتوانید ربات را از ادمینی کانال خود حذف کنید.\n" +
				"\n" +
				"✅ طریقه\u200Cای ادمین کردن ربات در کانال:\n" +
				"به تنظیمات کانال رجوع کرده و در قسمت Add Administrators، در کادری که می\u200Cتوانید نام مدیر مورد نظر را تایپ کنید، نام ربات یعنی @ViewMemberBot را تایپ کنید، و زمانی که ربات در لیست زیر کادر ظاهر شد، آن را انتخاب کنید تا پس از تایید در کانال شما ادمین شود.";
		AMOUNT_ZERO = "لطفا یک مقدار مثبت وارد کنید";
		CHANNEL_INVALID_LINK = "لطفا لینک کانال را به درستی وارد کنید";
		CHANNEL_NOT_ADMIN = "بات ادمین کانال شما نیست. لطفا ابتدا بات را ادمین کنید، سپس دوباره لینک را وارد نمایید."
				+ "\n"
				+ "✅ طریقه\u200Cای ادمین کردن ربات در کانال:\n" +
				"به تنظیمات کانال رجوع کرده و در قسمت Add Administrators، در کادری که می\u200Cتوانید نام مدیر مورد نظر را تایپ کنید، نام ربات یعنی @ViewMemberBot را تایپ کنید، و زمانی که ربات در لیست زیر کادر ظاهر شد، آن را انتخاب کنید تا پس از تایید در کانال شما ادمین شود.";
		CHANNEL_ENTER_AMOUNT =
				"لطفا مقدار سفارش را بر حسب نفر-روز وارد کنید. مثلا اگر می‌خواهید 10 نفر را به مدت 2 روز " +
						"در کانال خود عضو کنید، باید به ترتیب (از راست به چپ) اعداد 10 و 2 را وارد کنید. بدین صورت:\n" +
						"2 10\n" +
						"در این صورت 10 نفر به مدت 2 روز سفارش داده اید.";
		AMOUNT_ONE = "شما فقط تعداد روز را وارد کرده‌اید. لطفا دوباره مقادیر را وارد نمایید.\n" +
				"توجه داشته باشید که باید عدد سمت چپی تعداد روز و عدد سمت راستی تعداد نفرات باشد.";
		CHANNEL_ENTER_DESCRIPTION =
				"شما قصد خرید {persons} عضویت به مدت {days} روز و به ارزش {amount} سکه را دارید.\n" +
						"لطفا توضیحات کانال را وارد بفرمایید. توجه داشته باشید که توضیحات کانال" +
						" نباید بیش از ۲۵۰ کاراکتر باشد.";
		LENGTH_EXCEEDED = "طول توضیحات کانال بیش از ۲۵۰ کاراکتر است. لطفا آن را کوتاه کنید.";
		CHANNEL_JOIN_CHANNELS = "عضویت در کانال‌ها";
		OUR_CHANNEL = "کانال ما";
		CHANNEL_TEMPLATE = "نام کانال: {name}\n" +
				"توضیحات کانال: {description}\n" +
				"آدرس کانال: {username}\n" +
				"تعداد روزهای سفارش: {days}";
		CHANNEL_JOIN = "عضو شدم!";
		CHANNEL_NEXT = "بعدی";
		CHANNEL_NO_CHANNEL = "در حال حاضر کانالی موجود نیست :(\nلطفا بعدا دوباره سعی کنید";
		CHANNEL_JOIN_CONFIRMED = "عضویت شما در کانال فوق تایید شد. لطفا توجه داشته باشید:\n" +
				"1- به ازای هر ۲۴ ساعت حضور در کانال ۱ سکه دریافت می‌کنید.\n" +
				"2- اولین سکه ۲۴ ساعت دیگر به حساب شما واریز خواهد شد.\n" +
				"3- اگر در حین انجام سفارش کانال را ترک کنید، دیگر سکه‌ای به حساب شما واریز نخواهد شد." +
				" در عین حال شما جریمه نیز نخواهید شد.";
		CHANNEL_JOIN_DENIED =
				"عضویت شما در کانال فوق تایید نشده است. لطفا ابتدا با لمس کردن لینک کانال در آن عضو شوید.";
		CHANNEL_ADMIN_REVOKED = "متاسفانه ربات ما از ادمینی کانال شما @{name} حذف شده. لطفا ربات را تا اتمام سفارش " +
				"در کانال خود ادمین کنید. پس از اتمام سفارش می‌توانید ربات را از ادمینی کانال حذف کنید.\nدر صورت عدم" +
				" انجام این کار، ربات نمی‌تواند از عضویت افراد در کانال شما اطمینان پیدا کند.";
		CHANNEL_USER_LEFT_CHANNEL = "شما از کانال @{name} خارج شده‌اید. لطفا مجددا عضو کانال شوید.\n" +
				"اگر تا ۲۰ دقیقه دیگر عضو کانال نشوید، ربات آن را انصراف شما تلقی خواهد کرد.\n" +
				"در عین حال جریمه نیز نخواهید شد.";
		TRACK_JOINS = "کانال‌های عضو";
		GUIDANCE = "راهنمای ربات";
		TUTORIALS = "\uD83D\uDC48 راهنمای ربات\n" +
				"\n" +
				"✅ در این ربات شما تعدادی سکه دارید. با استفاده از این سکه\u200Cها می\u200Cتوانید برای کانال خود عضو، یا برای پست\u200Cهای مد نظر خود بازدید سفارش دهید.\n" +
				"\n" +
				"✅ برای کسب سکه می\u200Cتوانید از پست\u200Cهای دیگران بازدید کنید یا در کانال\u200Cها عضو شوید. در آینده نزدیک خرید سکه نیز در ربات گنجانده خواهد شد.\n" +
				"\n" +
				"✅ در قسمت پیگیری وضعیت سفارشات می\u200Cتوانید سفارشات بازدید یا عضو خود را با وضعیت دقیق مشاهده نمایید. همچنین می\u200Cتوانید لیست کانال\u200Cهایی که از طریق ربات در آن\u200Cها عضویت سفارشی دارید را ببینید.\n" +
				"\n" +
				"✅ لینک معرفی شما،\u200C لینکی است که اگر به دیگران ارائه دهید و به وسیله آن ثبت نام کنند، از ما هدیه دریافت می\u200Cکنید.\n";
		CHANNEL_NOT_EXIST = "این کانال در تلگرام وجود ندارد. لطفا در وارد نمودن آدرس دقت کنید.";
		
		
	}
}
