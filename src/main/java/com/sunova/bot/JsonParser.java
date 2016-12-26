package com.sunova.bot;

//import com.mashape.unirest.http.ObjectMapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.telegram.objects.*;

import java.io.IOException;

/**
 * Created by HellScre4m on 5/2/2016.
 */
public class JsonParser
{
	private static JsonParser instance;
	private ObjectReader updateReader;
	private ObjectReader resultReader;
	private ObjectWriter TObjectWriter;
	private ObjectWriter KeyboardButtonWriter;
	
	private JsonParser ()
	{
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		module.addDeserializer(TObject.class, new TObjectDeserializer());
		module.addSerializer(TObject.class, new TObjectSerializer());
		mapper.registerModule(module);
		resultReader = mapper.readerFor(Result.class);
		updateReader = mapper.readerFor(Update.class);
		TObjectWriter = mapper.writerFor(TObject.class);
		KeyboardButtonWriter = mapper.writerFor(KeyboardButton[][].class);
	}
	
	public static JsonParser getInstance ()
	{
		if (instance == null)
		{
			instance = new JsonParser();
		}
		return instance;
	}
	
	public Result parseResult (byte[] input)
	{
		try
		{
			{
				Result results = resultReader.readValue(input);
				if (!results.isOk())
				{
					System.err.println(new String(input));
				}
				return results;
			}
		}
		catch (IOException e)
		{
			System.out.println(new String(input));
			e.printStackTrace();
		}
		return null;
	}
	
	public Update parseUpdate (byte[] input)
	{
		try
		{
			{
				return updateReader.readValue(input);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public String deserializeTObject (TObject object)
	{
		String result = null;
		try
		{
			result = TObjectWriter.writeValueAsString(object);
		}
		catch (JsonProcessingException e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	private static class TObjectDeserializer extends JsonDeserializer<TObject>
	{
		@Override
		public TObject deserialize (com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctxt)
				throws IOException
		{
			JsonNode tree = p.readValueAsTree();
			TObject resultValue = null;
			if (tree.has("update_id"))
			{
//				System.out.println("update");
				resultValue = p.getCodec().treeToValue(tree, Update.class);
			}
			else if (tree.has("message_id"))
			{
//				System.out.println("message");
				resultValue = p.getCodec().treeToValue(tree, Message.class);
			}
			else if (tree.has("title"))
			{
				resultValue = p.getCodec().treeToValue(tree, Chat.class);
			}
			else if (tree.has("message_entity"))
			{
//				System.out.println("Message Entitiy");
				resultValue = p.getCodec().treeToValue(tree.get("message_entity"), MessageEntity.class);
			}
			else if (tree.has("id"))
			{
				System.out.println("user");
				resultValue = p.getCodec().treeToValue(tree, User.class);
			}
			else
			{
				System.out.println("NONE");
			}
			return resultValue;
		}
	}
	
	private class TObjectSerializer extends JsonSerializer<TObject>
	{
		@Override
		public void serialize (TObject value, JsonGenerator gen, SerializerProvider serializers) throws IOException
		{
			gen.writeStartObject();
			if (value instanceof ReplyKeyboardMarkup)
			{
				ReplyKeyboardMarkup instance = (ReplyKeyboardMarkup) value;
				gen.writeFieldName("keyboard");
				gen.writeRawValue(KeyboardButtonWriter.writeValueAsString(instance.getKeyboard()));
				if (instance.isResize_keyboard())
				{
					gen.writeBooleanField("resize_keyboard", true);
				}
				if (instance.isOne_time_keyboard())
				{
					gen.writeBooleanField("one_time_keyboard", true);
				}
				if (instance.isSelective())
				{
					gen.writeBooleanField("selective", true);
				}
				
			}
			else if (value instanceof KeyboardButton)
			{
				KeyboardButton instance = (KeyboardButton) value;
				gen.writeStringField("text", instance.getText());
				if (instance.isRequest_contact())
				{
					gen.writeBooleanField("request_contact", true);
				}
				if (instance.isRequest_location())
				{
					gen.writeBooleanField("request_location", true);
				}
			}
//			else if (value instanceof Message)
//			{
//				key = "message";
//			}
//			else if (value instanceof MessageEntity)
//			{
//				key = "message_entity";
//			}
			gen.writeEndObject();
		}
	}
}