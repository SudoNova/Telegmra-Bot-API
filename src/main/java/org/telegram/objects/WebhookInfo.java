package org.telegram.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by HellScre4m on 2/2/2017.
 */
public class WebhookInfo implements TObject
{
	private String url;
	private boolean has_custom_certificate;
	private int pending_update_count;
	private int last_error_date;
	private String last_error_message;
	private int max_connections;
	private String[] allowed_updates;
	
	@JsonGetter ("url")
	public String getUrl ()
	{
		return url;
	}
	
	@JsonSetter ("url")
	public WebhookInfo setUrl (String url)
	{
		this.url = url;
		return this;
	}
	
	@JsonGetter ("has_custom_certificate")
	public boolean has_custom_certificate ()
	{
		return has_custom_certificate;
	}
	
	@JsonSetter ("has_custom_certificate")
	public WebhookInfo setHas_custom_certificate (boolean has_custom_certificate)
	{
		this.has_custom_certificate = has_custom_certificate;
		return this;
	}
	
	@JsonGetter ("pending_update_count")
	public int getPending_update_count ()
	{
		return pending_update_count;
	}
	
	@JsonSetter ("pending_update_count")
	public WebhookInfo setPending_update_count (int pending_update_count)
	{
		this.pending_update_count = pending_update_count;
		return this;
	}
	
	@JsonGetter ("last_error_date")
	public int getLast_error_date ()
	{
		return last_error_date;
	}
	
	@JsonSetter ("last_error_date")
	public WebhookInfo setLast_error_date (int last_error_date)
	{
		this.last_error_date = last_error_date;
		return this;
	}
	
	@JsonGetter ("last_error_message")
	public String getLast_error_message ()
	{
		return last_error_message;
	}
	
	@JsonSetter ("last_error_message")
	public WebhookInfo setLast_error_message (String last_error_message)
	{
		this.last_error_message = last_error_message;
		return this;
	}
	
	@JsonGetter ("max_connections")
	public int getMax_connections ()
	{
		return max_connections;
	}
	
	@JsonSetter ("max_connections")
	public WebhookInfo setMax_connections (int max_connections)
	{
		this.max_connections = max_connections;
		return this;
	}
	
	@JsonGetter ("allowed_updates")
	public String[] getAllowed_updates ()
	{
		return allowed_updates;
	}
	
	@JsonSetter ("allowed_updates")
	public WebhookInfo setAllowed_updates (String[] allowed_updates)
	{
		this.allowed_updates = allowed_updates;
		return this;
	}
}
