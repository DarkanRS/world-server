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
package com.rs.plugin.events;

import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.plugin.handlers.NPCInteractionDistanceHandler;
import com.rs.plugin.handlers.ObjectInteractionDistanceHandler;
import com.rs.plugin.handlers.PluginHandler;

import java.util.HashMap;
import java.util.Map;

public class ObjectInteractionDistanceEvent implements PluginEvent {

	private static Map<Object, ObjectInteractionDistanceHandler> HANDLERS = new HashMap<>();

	private Player player;
	private GameObject obj;

	public ObjectInteractionDistanceEvent(Player player, GameObject obj) {
		this.player = player;
		this.obj = obj;
	}

	public Player getPlayer() {
		return player;
	}

	public GameObject getObj() {
		return obj;
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		ObjectInteractionDistanceHandler method = HANDLERS.get(obj.getId());
		if (method == null)
			method = HANDLERS.get(obj.getDefinitions().getName(player.getVars()));
		if (method == null)
			return null;
		return method;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		for (Object key : method.keys()) {
			PluginHandler<? extends PluginEvent> old = HANDLERS.put(key, (ObjectInteractionDistanceHandler) method);
			if (old != null)
				System.err.println("ERROR: Duplicate Object Distance methods for key: " + key);
		}
	}
}
