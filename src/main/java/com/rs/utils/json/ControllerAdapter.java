package com.rs.utils.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.rs.game.player.controllers.Controller;

public class ControllerAdapter implements JsonSerializer<Controller>, JsonDeserializer<Controller> {

	@Override
	public JsonElement serialize(Controller controller, Type type, JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.add("type", new JsonPrimitive(controller.getClass().getSimpleName()));
		result.add("properties", context.serialize(controller, controller.getClass()));
		return result;
	}

	@Override
	public Controller deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		String type = jsonObject.get("type").getAsString();
		JsonElement element = jsonObject.get("properties");

		try {
			String thePackage = "com.rs.game.player.controllers.";
			return context.deserialize(element, Class.forName(thePackage + type));
		} catch (ClassNotFoundException cnfe) {
			throw new JsonParseException("Unknown element type: " + type, cnfe);
		}
	}

}