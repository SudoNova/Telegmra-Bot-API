package org.telegram.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by HellScre4m on 12/9/2016.
 */
public class Venue implements TObject
{
	private Location location;
	private String title;
	private String address;
	private String foursquare_id;
	
	@JsonGetter ("location")
	public Location getLocation ()
	{
		return location;
	}
	
	@JsonSetter ("location")
	public Venue setLocation (Location location)
	{
		this.location = location;
		return this;
	}
	
	@JsonGetter ("title")
	public String getTitle ()
	{
		return title;
	}
	
	@JsonSetter ("title")
	public Venue setTitle (String title)
	{
		this.title = title;
		return this;
	}
	
	@JsonGetter ("address")
	public String getAddress ()
	{
		return address;
	}
	
	@JsonSetter ("address")
	public Venue setAddress (String address)
	{
		this.address = address;
		return this;
	}
	
	@JsonGetter ("foursquare_id")
	public String getFoursquare_id ()
	{
		return foursquare_id;
	}
	
	@JsonSetter ("foursquare_id")
	public Venue setFoursquare_id (String foursquare_id)
	{
		this.foursquare_id = foursquare_id;
		return this;
	}
}
