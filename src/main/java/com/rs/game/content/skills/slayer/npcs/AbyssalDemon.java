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
package com.rs.game.content.skills.slayer.npcs;

import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.utils.WorldUtil;

@PluginEventHandler
public class AbyssalDemon extends NPC {

	public AbyssalDemon(int id, Tile tile) {
		super(id, tile);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		Entity target = getCombat().getTarget();
		if (target != null && WorldUtil.isInRange(target.getX(), target.getY(), target.getSize(), getX(), getY(), getSize(), 4) && Utils.random(50) == 0)
			sendTeleport(Utils.random(2) == 0 ? target : this);
	}

	private void sendTeleport(Entity entity) {
		if (entity.isLocked())
			return;
		int entitySize = entity.getSize();
		for (int c = 0; c < 10; c++) {
			Direction dir = Direction.values()[Utils.random(Direction.values().length)];
			if (World.checkWalkStep(entity.getPlane(), entity.getX(), entity.getY(), dir, entitySize)) {
				entity.setNextSpotAnim(new SpotAnim(409));
				entity.setNextTile(entity.transform(dir.getDx(), dir.getDy(), 0));
				break;
			}
		}
	}

	public static NPCInstanceHandler toAbyssalDemon = new NPCInstanceHandler(new Object[] { 1615 }, (npcId, tile) -> new AbyssalDemon(npcId, tile));
}