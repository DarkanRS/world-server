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
package com.rs.plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.rs.lib.util.Logger;
import com.rs.plugin.events.PluginEvent;
import com.rs.plugin.handlers.PluginHandler;

public class PluginMethodRepository {

	public void addMappedHandler(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		try {
			eventType.getMethod("registerMethod", Class.class, PluginHandler.class).invoke(null, eventType, method);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			System.err.println("Error registering method for event type: " + eventType.getName() + " from registering method");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public boolean handle(PluginEvent event) {
		PluginHandler<PluginEvent> method = (PluginHandler<PluginEvent>) event.getMethod();
		if (method != null) {
			try {
				method.getHandler().accept(event);
				return true;
			} catch (Exception e) {
				Logger.handle(PluginMethodRepository.class, "handle:" + event.getClass().getSimpleName(), e);
				return false;
			}
		}
		List<PluginHandler<? extends PluginEvent>> methods = event.getMethods();
		if (methods == null || methods.size() <= 0)
			return false;
		for (PluginHandler<? extends PluginEvent> m : methods)
			try {
				PluginHandler<PluginEvent> pHandle = (PluginHandler<PluginEvent>) m;
				pHandle.getHandler().accept(event);
			} catch (Exception e) {
				Logger.handle(PluginMethodRepository.class, "handle:" + event.getClass().getSimpleName(), e);
				return false;
			}
		return true;
	}

	@SuppressWarnings("unchecked")
	public Object getObj(PluginEvent event) {
		Object obj = null;
		PluginHandler<PluginEvent> method = (PluginHandler<PluginEvent>) event.getMethod();
		if (method == null) {
			List<PluginHandler<? extends PluginEvent>> methods = event.getMethods();
			if (methods == null || methods.size() <= 0)
				return null;
			for (PluginHandler<? extends PluginEvent> m : methods)
				try {
					PluginHandler<PluginEvent> pHandle = (PluginHandler<PluginEvent>) m;
					obj = pHandle.getObj(event);
				} catch (Exception e) {
					Logger.handle(PluginMethodRepository.class, "getObj:" + event.getClass().getSimpleName(), e);
					return null;
				}
			return obj;
		}
		try {
			obj = method.getObj(event);
			if (obj != null)
				return obj;
		} catch (Exception e) {
			Logger.handle(PluginMethodRepository.class, "getObj:" + event.getClass().getSimpleName(), e);
			return null;
		}
		return null;
	}
}
