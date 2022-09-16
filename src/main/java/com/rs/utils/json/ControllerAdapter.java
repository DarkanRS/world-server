// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.utils.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.rs.game.model.entity.player.Controller;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.annotations.ServerStartupEvent.Priority;

@PluginEventHandler
public class ControllerAdapter implements JsonSerializer<Controller>, JsonDeserializer<Controller> {
	
	private static Map<String, Class<?>> CONTROLLER_CLASSES = new HashMap<>();
	
	@ServerStartupEvent(Priority.FILE_IO)
	public static void init() {
		try {
			List<Class<?>> classes = Utils.getSubClasses("com.rs", Controller.class);
			for (Class<?> clazz : classes) {
				if (CONTROLLER_CLASSES.put(clazz.getSimpleName(), clazz) != null)
					Logger.error(ControllerAdapter.class, "init", "Duplicate controller class: " + clazz.getName());
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		Logger.info(ControllerAdapter.class, "init", "Loaded " + CONTROLLER_CLASSES.size() + " controllers...");
	}

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
		Class<?> clazz = CONTROLLER_CLASSES.get(type);
		if (clazz == null)
			throw new RuntimeException("Controller not found: " + type);
		return context.deserialize(element, clazz);
	}

}