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
	
	public void setMigrate_to_chat_id (int migrate_to_chat_id)
	{
		this.migrate_to_chat_id = migrate_to_chat_id;
	}
	
	public int getMigrate_from_chat_id ()
	{
		return migrate_from_chat_id;
	}
	
	public void setMigrate_from_chat_id (int migrate_from_chat_id)
	{
		this.migrate_from_chat_id = migrate_from_chat_id;
	}
	
	public boolean isDelete_chat_photo ()
	{
		return delete_chat_photo;
	}
	
	public void setDelete_chat_photo (boolean delete_chat_photo)
	{
		this.delete_chat_photo = delete_chat_photo;
	}
	
	public boolean isGroup_chat_created ()
	{
		return group_chat_created;
	}
	
	public void setGroup_chat_created (boolean group_chat_created)
	{
		this.group_chat_created = group_chat_created;
	}
	
	public boolean isSupergroup_chat_created ()
	{
		return supergroup_chat_created;
	}
	
	public void setSupergroup_chat_created (boolean supergroup_chat_created)
	{
		this.supergroup_chat_created = supergroup_chat_created;
	}
	
	public boolean isChannel_chat_created ()
	{
		return channel_chat_created;
	}
	
	public void setChannel_chat_created (boolean channel_chat_created)
	{
		this.channel_chat_created = channel_chat_created;
	}
	
	public User getNew_chat_member ()
	{
		return new_chat_member;
	}
	
	public void setNew_chat_member (User new_chat_member)
	{
		this.new_chat_member = new_chat_member;
	}
	
	public User getLeft_chat_member ()
	{
		return left_chat_member;
	}
	
	public void setLeft_chat_member (User left_chat_member)
	{
		this.left_chat_member = left_chat_member;
	}
	
	public Message getPinned_message ()
	{
		return pinned_message;
	}
	
	public void setPinned_message (Message pinned_message)
	{
		this.pinned_message = pinned_message;
	}
	
	public String getNew_chat_title ()
	{
		return new_chat_title;
	}
	
	public void setNew_chat_title (String new_chat_title)
	{
		this.new_chat_title = new_chat_title;
	}
	
	public PhotoSize[] getNew_chat_photo ()
	{
		return new_chat_photo;
	}
	
	public void setNew_chat_photo (PhotoSize[] new_chat_photo)
	{
		this.new_chat_photo = new_chat_photo;
	}
	
	public Venue getVenue ()
	{
		return venue;
	}
	
	public void setVenue (Venue venue)
	{
		this.venue = venue;
	}
	
	public Location getLocation ()
	{
		return location;
	}
	
	public void setLocation (Location location)
	{
		this.location = location;
	}
	
	public int getEdit_date ()
	{
		return edit_date;
	}
	
	public void setEdit_date (int edit_date)
	{
		this.edit_date = edit_date;
	}
	
	public Voice getVoice ()
	{
		return voice;
	}
	
	public void setVoice (Voice voice)
	{
		this.voice = voice;
	}
	
	public Video getVideo ()
	{
		return video;
	}
	
	public void setVideo (Video video)
	{
		this.video = video;
	}
	
	public Sticker getSticker ()
	{
		return sticker;
	}
	
	public void setSticker (Sticker sticker)
	{
		this.sticker = sticker;
	}
	
	public Game getGame ()
	{
		return game;
	}
	
	public void setGame (Game game)
	{
		this.game = game;
	}
	
	public String getCaption ()
	{
		return caption;
	}
	
	public void setCaption (String caption)
	{
		this.caption = caption;
	}
	
	public Document getDocument ()
	{
		return document;
	}
	
	public void setDocument (Document document)
	{
		this.document = document;
	}
	
	public Audio getAudio ()
	{
		return audio;
	}
	
	public void setAudio (Audio audio)
	{
		this.audio = audio;
	}
	
	public PhotoSize[] getPhoto ()
	{
		return photo;
	}
	
	public void setPhoto (PhotoSize[] photo)
	{
		this.photo = photo;
	}
	
	public int getForward_from_message_id ()
	{
		return forward_from_message_id;
	}
	
	public void setForward_from_message_id (int forward_from_message_id)
	{
		this.forward_from_message_id = forward_from_message_id;
	}
	
	public Chat getForward_from_chat ()
	{
		return forward_from_chat;
	}
	
	public void setForward_from_chat (Chat forward_from_chat)
	{
		this.forward_from_chat = forward_from_chat;
	}
	
	public Contact getContact ()
	{
		return contact;
	}
	
	public void setContact (Contact contact)
	{
		this.contact = contact;
	}
	
	public ReplyKeyboardMarkup getReply_markup ()
	{
		return reply_markup;
	}
	
	public void setReply_markup (ReplyKeyboardMarkup reply_markup)
	{
		this.reply_markup = reply_markup;
	}
	
	public MessageEntity[] getEntities ()
	{
		return entities;
	}
	
	public void setEntities (MessageEntity[] entities)
	{
		this.entities = entities;
	}

//	private
// TODO Add other fields.
	//TODO this class needs to be completed.
	
	public Chat getChat ()
	{
		return chat;
	}
	
	public void setChat (Chat chat)
	{
		this.chat = chat;
	}
	
	public String getText ()
	{
		return text;
	}
	
	public void setText (String text)
	{
		this.text = text;
	}
	
	public Message getReply_to_message ()
	{
		return reply_to_message;
	}
	
	public void setReply_to_message (Message reply_to_message)
	{
		this.reply_to_message = reply_to_message;
	}
	
	public int getMessage_id ()
	{
		return message_id;
	}
	
	public void setMessage_id (int message_id)
	{
		this.message_id = message_id;
	}
	
	public User getFrom ()
	{
		return from;
	}
	
	public void setFrom (User from)
	{
		this.from = from;
	}
	
	public User getForward_from ()
	{
		return forward_from;
	}
	
	public void setForward_from (User forward_from)
	{
		this.forward_from = forward_from;
	}
	
	public int getForward_date ()
	{
		return forward_date;
	}
	
	public void setForward_date (int forward_date)
	{
		this.forward_date = forward_date;
	}
	
	public int getDate ()
	{
		return date;
	}
	
	public void setDate (int date)
	{
		this.date = date;
	}
	
}
