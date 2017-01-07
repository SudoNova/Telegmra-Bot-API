package org.telegram.objects;

/**
 * Created by HellScre4m on 4/26/2016.
 */
public class Message implements TObject
{
	private int message_id;
	private int forward_from_message_id;
	private int forward_date;
	private int date;
	private int edit_date;
	private int migrate_to_chat_id;
	private int migrate_from_chat_id;
	private boolean delete_chat_photo;
	private boolean group_chat_created;
	private boolean supergroup_chat_created;
	private boolean channel_chat_created;
	private User from;
	private User forward_from;
	private User new_chat_member;
	private User left_chat_member;
	private Chat chat;
	private Chat forward_from_chat;
	private Message reply_to_message;
	private Message pinned_message;
	private String text;
	private String caption;
	private String new_chat_title;
	private MessageEntity[] entities;
	private ReplyKeyboardMarkup reply_markup;
	private Contact contact;
	private PhotoSize[] photo;
	private PhotoSize[] new_chat_photo;
	private Audio audio;
	private Document document;
	private Game game;
	private Sticker sticker;
	private Video video;
	private Voice voice;
	private Location location;
	private Venue venue;
	
	public int getMigrate_to_chat_id ()
	{
		return migrate_to_chat_id;
	}
	
	public Message setMigrate_to_chat_id (int migrate_to_chat_id)
	{
		this.migrate_to_chat_id = migrate_to_chat_id;
		return this;
	}
	
	public int getMigrate_from_chat_id ()
	{
		return migrate_from_chat_id;
	}
	
	public Message setMigrate_from_chat_id (int migrate_from_chat_id)
	{
		this.migrate_from_chat_id = migrate_from_chat_id;
		return this;
	}
	
	public boolean isDelete_chat_photo ()
	{
		return delete_chat_photo;
	}
	
	public Message setDelete_chat_photo (boolean delete_chat_photo)
	{
		this.delete_chat_photo = delete_chat_photo;
		return this;
	}
	
	public boolean isGroup_chat_created ()
	{
		return group_chat_created;
	}
	
	public Message setGroup_chat_created (boolean group_chat_created)
	{
		this.group_chat_created = group_chat_created;
		return this;
	}
	
	public boolean isSupergroup_chat_created ()
	{
		return supergroup_chat_created;
	}
	
	public Message setSupergroup_chat_created (boolean supergroup_chat_created)
	{
		this.supergroup_chat_created = supergroup_chat_created;
		return this;
	}
	
	public boolean isChannel_chat_created ()
	{
		return channel_chat_created;
	}
	
	public Message setChannel_chat_created (boolean channel_chat_created)
	{
		this.channel_chat_created = channel_chat_created;
		return this;
	}
	
	public User getNew_chat_member ()
	{
		return new_chat_member;
	}
	
	public Message setNew_chat_member (User new_chat_member)
	{
		this.new_chat_member = new_chat_member;
		return this;
	}
	
	public User getLeft_chat_member ()
	{
		return left_chat_member;
	}
	
	public Message setLeft_chat_member (User left_chat_member)
	{
		this.left_chat_member = left_chat_member;
		return this;
	}
	
	public Message getPinned_message ()
	{
		return pinned_message;
	}
	
	public Message setPinned_message (Message pinned_message)
	{
		this.pinned_message = pinned_message;
		return this;
	}
	
	public String getNew_chat_title ()
	{
		return new_chat_title;
	}
	
	public Message setNew_chat_title (String new_chat_title)
	{
		this.new_chat_title = new_chat_title;
		return this;
	}
	
	public PhotoSize[] getNew_chat_photo ()
	{
		return new_chat_photo;
	}
	
	public Message setNew_chat_photo (PhotoSize[] new_chat_photo)
	{
		this.new_chat_photo = new_chat_photo;
		return this;
	}
	
	public Venue getVenue ()
	{
		return venue;
	}
	
	public Message setVenue (Venue venue)
	{
		this.venue = venue;
		return this;
	}
	
	public Location getLocation ()
	{
		return location;
	}
	
	public Message setLocation (Location location)
	{
		this.location = location;
		return this;
	}
	
	public int getEdit_date ()
	{
		return edit_date;
	}
	
	public Message setEdit_date (int edit_date)
	{
		this.edit_date = edit_date;
		return this;
	}
	
	public Voice getVoice ()
	{
		return voice;
	}
	
	public Message setVoice (Voice voice)
	{
		this.voice = voice;
		return this;
	}
	
	public Video getVideo ()
	{
		return video;
	}
	
	public Message setVideo (Video video)
	{
		this.video = video;
		return this;
	}
	
	public Sticker getSticker ()
	{
		return sticker;
	}
	
	public Message setSticker (Sticker sticker)
	{
		this.sticker = sticker;
		return this;
	}
	
	public Game getGame ()
	{
		return game;
	}
	
	public Message setGame (Game game)
	{
		this.game = game;
		return this;
	}
	
	public String getCaption ()
	{
		return caption;
	}
	
	public Message setCaption (String caption)
	{
		this.caption = caption;
		return this;
	}
	
	public Document getDocument ()
	{
		return document;
	}
	
	public Message setDocument (Document document)
	{
		this.document = document;
		return this;
	}
	
	public Audio getAudio ()
	{
		return audio;
	}
	
	public Message setAudio (Audio audio)
	{
		this.audio = audio;
		return this;
	}
	
	public PhotoSize[] getPhoto ()
	{
		return photo;
	}
	
	public Message setPhoto (PhotoSize[] photo)
	{
		this.photo = photo;
		return this;
	}
	
	public int getForward_from_message_id ()
	{
		return forward_from_message_id;
	}
	
	public Message setForward_from_message_id (int forward_from_message_id)
	{
		this.forward_from_message_id = forward_from_message_id;
		return this;
	}
	
	public Chat getForward_from_chat ()
	{
		return forward_from_chat;
	}
	
	public Message setForward_from_chat (Chat forward_from_chat)
	{
		this.forward_from_chat = forward_from_chat;
		return this;
	}
	
	public Contact getContact ()
	{
		return contact;
	}
	
	public Message setContact (Contact contact)
	{
		this.contact = contact;
		return this;
	}
	
	public ReplyMarkup getReply_markup ()
	{
		return reply_markup;
	}
	
	public Message setReply_markup (ReplyKeyboardMarkup reply_markup)
	{
		this.reply_markup = reply_markup;
		return this;
	}
	
	public MessageEntity[] getEntities ()
	{
		return entities;
	}
	
	public Message setEntities (MessageEntity[] entities)
	{
		this.entities = entities;
		return this;
	}
	
	public Chat getChat ()
	{
		return chat;
	}
	
	public Message setChat (Chat chat)
	{
		this.chat = chat;
		return this;
	}
	
	public String getText ()
	{
		return text;
	}
	
	public Message setText (String text)
	{
		this.text = text;
		return this;
	}
	
	public Message getReply_to_message ()
	{
		return reply_to_message;
	}
	
	public Message setReply_to_message (Message reply_to_message)
	{
		this.reply_to_message = reply_to_message;
		return this;
	}
	
	public int getMessage_id ()
	{
		return message_id;
	}
	
	public Message setMessage_id (int message_id)
	{
		this.message_id = message_id;
		return this;
	}
	
	public User getFrom ()
	{
		return from;
	}
	
	public Message setFrom (User from)
	{
		this.from = from;
		return this;
	}
	
	public User getForward_from ()
	{
		return forward_from;
	}
	
	public Message setForward_from (User forward_from)
	{
		this.forward_from = forward_from;
		return this;
	}
	
	public int getForward_date ()
	{
		return forward_date;
	}
	
	public Message setForward_date (int forward_date)
	{
		this.forward_date = forward_date;
		return this;
	}
	
	public int getDate ()
	{
		return date;
	}
	
	public Message setDate (int date)
	{
		this.date = date;
		return this;
	}
	
}
