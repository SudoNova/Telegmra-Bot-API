package org.telegram.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by HellScre4m on 12/8/2016.
 */
public class Contact implements TObject
{
	private String phone_number;
	private String first_name;
	private String last_name;
	private int user_id;
	
	@JsonGetter ("phone_number")
	public String getPhone_number ()
	{
		return phone_number;
	}
	
	@JsonSetter ("phone_number")
	public Contact setPhone_number (String phone_number)
	{
		this.phone_number = phone_number;
		return this;
	}
	
	@JsonGetter ("first_name")
	public String getFirst_name ()
	{
		return first_name;
	}
	
	@JsonSetter ("first_name")
	public Contact setFirst_name (String first_name)
	{
		this.first_name = first_name;
		return this;
	}
	
	@JsonGetter ("last_name")
	public String getLast_name ()
	{
		return last_name;
	}
	
	@JsonSetter ("last_name")
	public Contact setLast_name (String last_name)
	{
		this.last_name = last_name;
		return this;
	}
	
	@JsonGetter ("user_id")
	public int getUser_id ()
	{
		return user_id;
	}
	
	@JsonSetter ("user_id")
	public Contact setUser_id (int user_id)
	{
		this.user_id = user_id;
		return this;
	}
}
