package com.sunova.bot;

//import com.mashape.unirest.http.ObjectMapper;

import com.fasterxml.jackson.core.JsonGenerator;
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
	private ObjectWriter writer;
	
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
		writer = mapper.writerFor(TObject.class);
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
				if (!results.getOk())
				{
					System.err.println(new String(input));
				}
				return results;
			}
		}
		catch (IOException e)
		{
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
				System.out.println("update");
				resultValue = p.getCodec().treeToValue(tree, Update.class);
			}
			else if (tree.has("message_id"))
			{
//				System.out.println("message");
				resultValue = p.getCodec().treeToValue(tree, Message.class);
			}
			else if (tree.has("chat"))
			{
				resultValue = p.getCodec().treeToValue(tree.get("chat"), Chat.class);
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
			
			String key = "";
			
			if (value instanceof Chat)
			{
				key = "chat";
			}
			else if (value instanceof Message)
			{
				key = "message";
			}
			else if (value instanceof MessageEntity)
			{
				key = "message_entity";
			}
			gen.writeFieldName(key);
			gen.writeRawValue(writer.writeValueAsString(value));
			gen.writeEndObject();
		}
	}
}