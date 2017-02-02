package com.sunova.botframework;

//import com.mashape.unirest.http.ObjectMapper;

import com.fasterxml.jackson.annotation.JsonInclude;
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
	//	private ObjectWriter TObjectWriter;
	private ObjectMapper mapper;
	
	private JsonParser ()
	{
		mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
		module.addDeserializer(TObject.class, new TObjectDeserializer());
//		module.addSerializer(TObject.class, new TObjectSerializer());
		mapper.registerModule(module);
		resultReader = mapper.readerFor(Result.class);
		updateReader = mapper.readerFor(Update.class);
//		TObjectWriter = mapper.writerFor(TObject.class);
	}
	
	public static JsonParser getInstance ()
	{
		if (instance == null)
		{
			instance = new JsonParser();
		}
		return instance;
	}
	
	public Result parseResult (byte[] input) throws IOException, Result
	{
		Result results;
		try
		{
			results = resultReader.readValue(input);
			if (!results.isOk())
			{
				System.err.println(new String(input));
				throw results;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.out.println(new String(input));
			throw mapper.readValue(input, Result.class);
		}
		return results;
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
	
	public String serialize (Object object) throws JsonProcessingException
	{
//		String result =
		return mapper.writeValueAsString(object);
//		System.out.println(result);
//		return result;
	}

//	public String serializeTObject (TObject object)
//	{
//		String result = null;
//		try
//		{
//			result = mapper.writeValueAsString(object);
//		}
//		catch (JsonProcessingException e)
//		{
//			e.printStackTrace();
//		}
//		return result;
//	}
	
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
				resultValue = p.getCodec().treeToValue(tree, Update.class);
			}
			else if (tree.has("message_id"))
			{
				resultValue = p.getCodec().treeToValue(tree, Message.class);
			}
			else if (tree.has("title"))
			{
				resultValue = p.getCodec().treeToValue(tree, Chat.class);
			}
			else if (tree.has("message_entity"))
			{
				resultValue = p.getCodec().treeToValue(tree.get("message_entity"), MessageEntity.class);
			}
			else if (tree.has("id"))
			{
				resultValue = p.getCodec().treeToValue(tree, User.class);
			}
			else if (tree.has("status"))
			{
				resultValue = p.getCodec().treeToValue(tree, ChatMember.class);
			}
			else
			{
				System.out.println("NONE");
			}
			return resultValue;
		}
	}

//	private class TObjectSerializer extends JsonSerializer<TObject>
//	{
//		@Override
//		public void serialize (TObject value, JsonGenerator gen, SerializerProvider serializers) throws IOException
//		{
//			try
//			{
//				gen.writeStartObject();
//			}
//			catch (JsonGenerationException e)
//			{
//				gen.writeFieldName(value.getClass().getSimpleName());
//				gen.writeStartObject();
//			}
//			if (value instanceof ReplyKeyboardMarkup)
//			{
//				ReplyKeyboardMarkup instance = (ReplyKeyboardMarkup) value;
//				gen.writeFieldName("keyboard");
//				gen.writeRawValue(KeyboardButtonWriter.writeValueAsString(instance.getKeyboard()));
//				if (instance.isResize_keyboard())
//				{
//					gen.writeBooleanField("resize_keyboard", true);
//				}
//				if (instance.isOne_time_keyboard())
//				{
//					gen.writeBooleanField("one_time_keyboard", true);
//				}
//				if (instance.isSelective())
//				{
//					gen.writeBooleanField("selective", true);
//				}
//			}
//			else if (value instanceof KeyboardButton)
//			{
//				KeyboardButton instance = (KeyboardButton) value;
//				gen.writeStringField("text", instance.getText());
//				if (instance.isRequest_contact())
//				{
//					gen.writeBooleanField("request_contact", true);
//				}
//				if (instance.isRequest_location())
//				{
//					gen.writeBooleanField("request_location", true);
//				}
//			}
//			else if (value instanceof Update)
//			{
//				Update temp = (Update) value;
//				gen.writeNumberField("update_id", temp.getUpdate_id());
//				if (temp.containsMessage())
//				{
//					gen.writeObjectField("message", temp.getMessage());
//				}
//			}
//			else
//			{
//				Class valueClass = value.getClass();
//				gen.writeObject(value.getClass().cast(value));
//
//			}
////			else if (value instanceof Message)
////			{
////				key = "message";
////			}
////			else if (value instanceof MessageEntity)
////			{
////				key = "message_entity";
////			}
//			gen.writeEndObject();
//		}
//	}
}