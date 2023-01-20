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

import java.util.function.BiFunction;

import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.events.NPCInstanceEvent;

public class NPCInstanceHandler extends PluginHandler<NPCInstanceEvent> {
	
	private BiFunction<Integer, WorldTile, NPC> instantiator;

	public NPCInstanceHandler(Object[] keys, BiFunction<Integer, WorldTile, NPC> instantiator) {
		super(keys, null);
		this.instantiator = instantiator;
	}
	
	public NPCInstanceHandler(int id, BiFunction<Integer, WorldTile, NPC> instantiator) {
		super(new Object[] { id }, null);
		this.instantiator = instantiator;
	}
	
	public NPCInstanceHandler(String name, BiFunction<Integer, WorldTile, NPC> instantiator) {
		super(new Object[] { name }, null);
		this.instantiator = instantiator;
	}

	@Override
	public final boolean handleGlobal(NPCInstanceEvent e) { return false; }

	@Override
	public final Object getObj(NPCInstanceEvent e) {
		NPC npc = instantiator.apply(e.getNpcId(), e.getTile());
		npc.setSpawned(e.isSpawned());
		return npc;
	}

}
