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
package com.rs.game.npc.pest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.minigames.pest.PestControl;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class PestMonsters extends NPC {

	protected PestControl manager;
	protected int portalIndex;

	public PestMonsters(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned, int index, PestControl manager) {
		super(id, tile, spawned);
		this.manager = manager;
		portalIndex = index;
		setForceMultiArea(true);
		setForceAgressive(true);
		setIgnoreDocile(true);
		setForceAggroDistance(70);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (!getCombat().hasTarget())
			checkAggressivity();
	}

	@Override
	public List<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<>();
		Set<Integer> playerIndexes = World.getRegion(getRegionId()).getPlayerIndexes();
		if (playerIndexes != null)
			for (int playerIndex : playerIndexes) {
				Player player = World.getPlayers().get(playerIndex);
				if (player == null || player.isDead() || player.hasFinished() || !player.isRunning() || !player.withinDistance(getTile(), 10))
					continue;
				possibleTarget.add(player);
			}
		if (possibleTarget.isEmpty() || Utils.random(3) == 0) {
			possibleTarget.clear();
			possibleTarget.add(manager.getKnight());
		}
		return possibleTarget;
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		manager.getPestCounts()[portalIndex]--;
	}
}
