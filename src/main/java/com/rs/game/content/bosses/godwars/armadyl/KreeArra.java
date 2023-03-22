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
package com.rs.game.content.bosses.godwars.armadyl;

import com.rs.game.content.bosses.godwars.GodWarMinion;
import com.rs.game.content.combat.PlayerCombat;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class KreeArra extends NPC {

	private GodWarMinion[] minions = new GodWarMinion[3];

	public KreeArra(int id, Tile tile, boolean spawned) {
		super(id, tile, spawned);
		setIntelligentRouteFinder(true);
		setIgnoreDocile(true);
		setForceAggroDistance(64);
		minions[0] = new GodWarMinion(6223, tile.transform(8, 0), spawned);
		minions[1] = new GodWarMinion(6225, tile.transform(-4, -2), spawned);
		minions[2] = new GodWarMinion(6227, tile.transform(-2, -4), spawned);
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

	@Override
	public boolean canBeAttackedBy(Player player) {
		if (!PlayerCombat.isRanging(player)) {
			player.sendMessage("Kree'arra is flying too high for you to attack using melee.");
			return false;
		}
		return true;
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(6222, (npcId, tile) -> new KreeArra(npcId, tile, false));
}
