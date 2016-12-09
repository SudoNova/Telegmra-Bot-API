package org.telegram.objects;

/**
 * Created by HellScre4m on 12/9/2016.
 */
public class Venue implements TObject
{
	private Location location;
	private String title;
	private String address;
	private String foursquare_id;
	
	public Location getLocation ()
	{
		return location;
	}
	
	public void setLocation (Location location)
	{
		this.location = location;
	}
	
	public String getTitle ()
	{
		return title;
	}
	
	public void setTitle (String title)
	{
		this.title = title;
	}
	
	public String getAddress ()
	{
		return address;
	}
	
	public void setAddress (String address)
	{
		this.address = address;
	}
	
	public String getFoursquare_id ()
	{
		return foursquare_id;
	}
	
	public void setFoursquare_id (String foursquare_id)
	{
		this.foursquare_id = foursquare_id;
	}
}
