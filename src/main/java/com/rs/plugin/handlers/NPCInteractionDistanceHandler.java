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
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.events.NPCInteractionDistanceEvent;

public class NPCInteractionDistanceHandler extends PluginHandler<NPCInteractionDistanceEvent> {
	
	private BiFunction<Player, NPC, Integer> supplier;

	public NPCInteractionDistanceHandler(Object[] keys, BiFunction<Player, NPC, Integer> supplier) {
		super(keys, null);
		this.supplier = supplier;
	}
	
	public NPCInteractionDistanceHandler(int id, BiFunction<Player, NPC, Integer> supplier) {
		super(new Object[] { id }, null);
		this.supplier = supplier;
	}
	
	public NPCInteractionDistanceHandler(String name, BiFunction<Player, NPC, Integer> supplier) {
		super(new Object[] { name }, null);
		this.supplier = supplier;
	}

	@Override
	public final boolean handleGlobal(NPCInteractionDistanceEvent e) { return false; }

	@Override
	public final Object getObj(NPCInteractionDistanceEvent e) {
		return supplier.apply(e.getPlayer(), e.getNpc());
	}

}
