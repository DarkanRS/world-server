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
package com.rs.game.content.world.npcs;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class RockCrab extends NPC {

	private int realId;

	public RockCrab(int id, Tile tile, boolean spawned) {
		super(id, tile, spawned);
		realId = id;
		setForceAgressive(true); // to ignore combat lvl
	}

	@Override
	public void setTarget(Entity entity) {
		if (realId == getId()) {
			setNextNPCTransformation(realId - 1);
			setHitpoints(getMaxHitpoints());
		}
		super.setTarget(entity);
	}

	@Override
	public void reset() {
		setNPC(realId);
		super.reset();
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 1266, 1268, 2453, 2886 }, (npcId, tile) -> new RockCrab(npcId, tile, false));
}
