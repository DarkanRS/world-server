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
package com.rs.game.content.bosses.godwars.bandos;

import com.rs.cores.CoresManager;
import com.rs.game.content.bosses.godwars.GodWarMinion;
import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class GeneralGraardor extends NPC {

	private GodWarMinion[] minions = new GodWarMinion[3];

	public GeneralGraardor(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		setForceFollowClose(true);
		setIntelligentRouteFinder(true);
		setIgnoreDocile(true);
		minions[0] = new GodWarMinion(6261, tile.transform(-8, 0), spawned);
		minions[1] = new GodWarMinion(6263, tile.transform(0, -6), spawned);
		minions[2] = new GodWarMinion(6265, tile.transform(-4, 4), spawned);
	}

	@Override
	public void onRespawn() {
		respawnMinions();
	}

	public void respawnMinions() {
		CoresManager.schedule(() -> {
			for (GodWarMinion minion : minions)
				if (minion.hasFinished() || minion.isDead())
					minion.respawn();
		}, 2);
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(6260, (npcId, tile) -> new GeneralGraardor(npcId, tile, false));
}
