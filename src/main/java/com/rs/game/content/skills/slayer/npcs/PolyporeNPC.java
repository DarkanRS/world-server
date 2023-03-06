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

import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class PolyporeNPC extends NPC {

	private int realId;

	public PolyporeNPC(int id, Tile tile, boolean spawned) {
		super(id, tile, spawned);
		realId = id;
		setRandomWalk(true);
		if (id == 14698)
			setCantFollowUnderCombat(id == 14698);

	}

	@Override
	public void reset() {
		setNPC(realId);
		super.reset();
	}

	public boolean canInfect() {
		return realId == getId();
	}

	@Override
	public void handlePreHit(final Hit hit) {
		if (hit.getLook() == HitLook.MELEE_DAMAGE || hit.getLook() == HitLook.RANGE_DAMAGE)
			hit.setDamage(hit.getDamage() / 5);
		super.handlePreHit(hit);
	}

	public int getInfectEmote() {
		switch (realId) {
		case 14688:
			return 15484;
		case 14690:
			return 15507;
		case 14692:
			return 15514;
		case 14696:
			return 15466;
		case 14698:
			return 15477;
		case 14700:
			return 15492;
		default:
			return -1;
		}
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 14688, 14689, 14690, 14691, 14692, 14693, 14696, 14697, 14698, 14699, 14700, 14701 }, (npcId, tile) -> {
		return new PolyporeNPC(npcId, tile, false);
	});
}
