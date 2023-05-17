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

import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.handlers.NPCInteractionDistanceHandler;
import com.rs.plugin.handlers.PluginHandler;

import java.util.HashMap;
import java.util.Map;

public class NPCInteractionDistanceEvent implements PluginEvent {

	private static Map<Object, NPCInteractionDistanceHandler> HANDLERS = new HashMap<>();

	private Player player;
	private NPC npc;

	public NPCInteractionDistanceEvent(Player player, NPC npc) {
		this.player = player;
		this.npc = npc;
	}

	public Player getPlayer() {
		return player;
	}

	public NPC getNpc() {
		return npc;
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		NPCInteractionDistanceHandler method = HANDLERS.get(npc.getId());
		if (method == null)
			method = HANDLERS.get(npc.getDefinitions().getName(player.getVars()));
		if (method == null)
			return null;
		return method;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		for (Object key : method.keys()) {
			PluginHandler<? extends PluginEvent> old = HANDLERS.put(key, (NPCInteractionDistanceHandler) method);
			if (old != null)
				System.err.println("ERROR: Duplicate ItemOnNPC methods for key: " + key);
		}
	}
}
