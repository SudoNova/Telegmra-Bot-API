package com.sunova.botframework;

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
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
		return mapper.writeValueAsString(object);
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
			else if (tree.has("url"))
			{
				resultValue = p.getCodec().treeToValue(tree, WebhookInfo.class);
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
	
}