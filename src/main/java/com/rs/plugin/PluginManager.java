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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.plugin;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rs.game.player.Player;
import com.rs.game.player.content.skills.smithing.ArtisansWorkshop;
import com.rs.lib.file.FileManager;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.PluginEvent;
import com.rs.plugin.handlers.PluginHandler;

public class PluginManager {

	private static Map<String, HashSet<PluginHandler<PluginEvent>>> UNMAPPED_HANDLERS = new HashMap<>();
	private static List<Method> STARTUP_HOOKS = new ArrayList<>();
	private static PluginMethodRepository REPOSITORY = new PluginMethodRepository();

	private static void addUnmappedHandler(String type, PluginHandler<PluginEvent> method) {
		HashSet<PluginHandler<PluginEvent>> methods = UNMAPPED_HANDLERS.get(type);
		if (methods == null)
			methods = new HashSet<>();
		methods.add(method);
		UNMAPPED_HANDLERS.put(type, methods);
	}

	@SuppressWarnings("unchecked")
	public static void loadPlugins() {
		try {
			long start = System.currentTimeMillis();
			Logger.log("PluginManager", "Loading plugins...");
			ArrayList<Class<?>> eventTypes = Utils.getClasses("com.rs.plugin.events");
			ArrayList<Class<?>> classes = Utils.getClassesWithAnnotation("com.rs", PluginEventHandler.class);
			Set<Method> visitedMethods = new HashSet<>();
			Set<Field> visitedFields = new HashSet<>();
			Logger.log("PluginManager", "Loading " + eventTypes.size() + " event types and " + classes.size() + " plugin enabled classes.");
			int handlers = 0;
			for (Class<?> clazz : classes) {
				for (Method method : clazz.getMethods()) {
					if (!Modifier.isStatic(method.getModifiers()) || visitedMethods.contains(method))
						continue;
					visitedMethods.add(method);
					switch(method.getParameterCount()) {
					case 0:
						if (method.isAnnotationPresent(ServerStartupEvent.class)) {
							STARTUP_HOOKS.add(method);
							handlers++;
						}
						break;
					}
				}
				for (Field field : clazz.getFields()) {
					if (!Modifier.isStatic(field.getModifiers()) || visitedFields.contains(field))
						continue;
					visitedFields.add(field);

					for (Class<?> eventType : eventTypes) {
						if (field.getType() == PluginHandler.class) {
							Class<?> type = ((Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
							if (type != eventType)
								continue;
						} else if (field.getType().getSuperclass() == PluginHandler.class) {
							Class<?> type = ((Class<?>) ((ParameterizedType) field.getType().getGenericSuperclass()).getActualTypeArguments()[0]);
							if (type != eventType)
								continue;
						} else
							continue;
						field.setAccessible(true);
						PluginHandler<PluginEvent> handler = (PluginHandler<PluginEvent>) field.get(null);
						if (handler.keys() == null || handler.keys().length <= 0) {
							addUnmappedHandler(eventType.getName(), handler);
							handlers++;
						} else {
							REPOSITORY.addMappedHandler(eventType, handler);
							handlers++;
						}
					}
				}
			}
			Logger.log("PluginManager", "Loaded " + handlers + " plugin event handlers in " + (System.currentTimeMillis()-start) + "ms.");
		} catch (ClassNotFoundException | IOException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}


	public static void executeStartupHooks() {
		for (Method m : STARTUP_HOOKS) {
			long start = System.currentTimeMillis();
			try {
				m.invoke(null);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				System.err.println("Error executing startup hook: " + m.getDeclaringClass().getName() + "." + m.getName());
				e.printStackTrace();
			}
			long time = System.currentTimeMillis() - start;
			if (time > 100L)
				Logger.log(m.getDeclaringClass().getSimpleName(), "Executed " + m.getName() + " in " + time + "ms...");
		}
	}

	public static boolean handle(PluginEvent event) {
		if (REPOSITORY.handle(event))
			return true;
		HashSet<PluginHandler<PluginEvent>> methods = UNMAPPED_HANDLERS.get(event.getClass().getName());
		if (methods == null)
			return false;
		boolean usedPlugins = false;
		for (PluginHandler<PluginEvent> method : methods)
			if (method.handleGlobal(event))
				usedPlugins = true;
		return usedPlugins;
	}

	public static Object getObj(PluginEvent event) {
		Object obj = REPOSITORY.getObj(event);
		if (obj != null)
			return obj;
		HashSet<PluginHandler<PluginEvent>> methods = UNMAPPED_HANDLERS.get(event.getClass().getName());
		if (methods == null)
			return null;
		for (PluginHandler<PluginEvent> method : methods) {
			obj = method.getObj(event);
			if (obj != null)
				return obj;
		}
		return null;
	}

	public static boolean handle(Method method, Object event) {
		try {
			return (boolean) method.invoke(null, event);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			FileManager.logError(e);
		}
		return false;
	}

	public static int getAmountPlayerHas(Player player, int itemId) {
		if (itemId >= 25629 && itemId <= 25633)
			return ArtisansWorkshop.numStoredOres(player, itemId);
		return -1;
	}
}
