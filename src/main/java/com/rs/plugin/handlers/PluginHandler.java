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
package com.rs.plugin.handlers;

import com.rs.plugin.events.PluginEvent;

import java.util.function.Consumer;

public abstract class PluginHandler<T extends PluginEvent> {
	protected Object[] keys;
	protected Consumer<T> handler;

	public PluginHandler(Object[] keys, Consumer<T> handler) {
		this.keys = keys;
		this.handler = handler;
	}

	public boolean handleGlobal(T e) {
		return false;
	}
	
	public Object getObj(T e) {
		return null;
	}

	public Object[] keys() {
		return keys;
	}

	public Consumer<T> getHandler() {
		return handler;
	}
}
