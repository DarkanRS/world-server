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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.npc.fightkiln;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;

public class HarAkenTentacle extends NPC {

	private HarAken aken;

	public HarAkenTentacle(int id, WorldTile tile, HarAken aken) {
		super(id, tile, true);
		setForceMultiArea(true);
		setCantFollowUnderCombat(true);
		setForceAgressive(true);
		setNextAnimation(new Animation(id == 15209 ? 16238 : 16241));
		freeze(10000000);
		this.aken = aken;
	}

	@Override
	public void sendDeath(Entity source) {
		aken.removeTentacle(this);
		super.sendDeath(source);
	}
	
	@Override
	public boolean ignoreWallsWhenMeleeing() {
		return true;
	}

	@Override
	public List<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>(1);
		Set<Integer> playerIndexes = World.getRegion(getRegionId()).getPlayerIndexes();
		if (playerIndexes != null) {
			for (int npcIndex : playerIndexes) {
				Player player = World.getPlayers().get(npcIndex);
				if (player == null || player.isDead() || player.hasFinished() || !player.isRunning())
					continue;
				possibleTarget.add(player);
			}
		}
		return possibleTarget;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.1;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.1;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.1;
	}
}
