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
import com.rs.game.content.skills.summoning.familiars.Familiar;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public class FamiliarAdapter implements JsonSerializer<Familiar>, JsonDeserializer<Familiar> {

	private static Map<String, Class<?>> FAMILIAR_CLASSES = new HashMap<>();
	
	@ServerStartupEvent
	public static void init() {
		try {
			List<Class<?>> classes = Utils.getSubClasses("com.rs", Familiar.class);
			for (Class<?> clazz : classes) {
				if (FAMILIAR_CLASSES.put(clazz.getSimpleName(), clazz) != null)
					System.out.println("Duplicate familiar class: " + clazz.getName());
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		System.out.println("Loaded " + FAMILIAR_CLASSES.size() + " familiars...");
	}
	
	@Override
	public JsonElement serialize(Familiar familiar, Type type, JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.add("type", new JsonPrimitive(familiar.getClass().getSimpleName()));
		result.add("properties", context.serialize(familiar, familiar.getClass()));
		return result;
	}

	@Override
	public Familiar deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		String type = jsonObject.get("type").getAsString();
		JsonElement element = jsonObject.get("properties");
		Class<?> clazz = FAMILIAR_CLASSES.get(type);
		if (clazz == null)
			throw new RuntimeException("Controller not found: " + type);
		return context.deserialize(element, clazz);
	}

}