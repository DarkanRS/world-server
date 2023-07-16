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
package com.rs.game.content.bosses.godwars.zamorak;

import com.rs.game.content.bosses.godwars.GodWarMinion;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class KrilTstsaroth extends NPC {

	private GodWarMinion[] minions = new GodWarMinion[3];

	public KrilTstsaroth(int id, Tile tile, boolean spawned) {
		super(id, tile, spawned);
		setIntelligentRouteFinder(true);
		setIgnoreDocile(true);
		setForceAggroDistance(64);
		minions[0] = new GodWarMinion(6204, tile.transform(8, 4), spawned);
		minions[1] = new GodWarMinion(6206, tile.transform(-6, 6), spawned);
		minions[2] = new GodWarMinion(6208, tile.transform(-6, -2), spawned);
	}

	@Override
	public void onRespawn() {
		respawnMinions();
	}

	public void respawnMinions() {
		WorldTasks.schedule(2, () -> {
			for (GodWarMinion minion : minions)
				if (minion.hasFinished() || minion.isDead())
					minion.respawn();
		});
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(6203, (npcId, tile) -> new KrilTstsaroth(npcId, tile, false));
}
