package org.telegram.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by HellScre4m on 12/9/2016.
 */
public class Location implements TObject
{
	private float longitude;
	private float latitude;
	
	@JsonGetter ("longitude")
	public float getLongitude ()
	{
		return longitude;
	}
	
	@JsonSetter ("longitude")
	public Location setLongitude (float longitude)
	{
		this.longitude = longitude;
		return this;
	}
	
	@JsonGetter ("latitude")
	public float getLatitude ()
	{
		return latitude;
	}
	
	@JsonSetter ("latitude")
	public Location setLatitude (float latitude)
	{
		this.latitude = latitude;
		return this;
	}
}
