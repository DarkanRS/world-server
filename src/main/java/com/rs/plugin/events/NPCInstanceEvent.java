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
package com.rs.plugin.events;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.lib.game.Tile;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.plugin.handlers.PluginHandler;

import java.util.HashMap;
import java.util.Map;

public class NPCInstanceEvent implements PluginEvent {

	private static Map<Object, NPCInstanceHandler> HANDLERS = new HashMap<>();

	private int npcId;
	private Tile tile;
	private boolean spawned;

	public NPCInstanceEvent(int npcId, Tile tile, boolean spawned) {
		this.npcId = npcId;
		this.tile = tile;
		this.spawned = spawned;
	}

	public int getNpcId() {
		return npcId;
	}

	public Tile getTile() {
		return tile;
	}

	public boolean isSpawned() {
		return spawned;
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		NPCInstanceHandler method = HANDLERS.get(npcId);
		if (method == null)
			method = HANDLERS.get(NPCDefinitions.getDefs(npcId).getName());
		if (method == null)
			return null;
		return method;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		for (Object key : method.keys()) {
			PluginHandler<? extends PluginEvent> old = HANDLERS.put(key, (NPCInstanceHandler) method);
			if (old != null)
				System.err.println("ERROR: Duplicate NPC instance methods for key: " + key);
		}
	}
}
