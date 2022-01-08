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
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.controllers.FightKilnController;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class FightKilnNPC extends NPC {

	private FightKilnController controller;

	public FightKilnNPC(int id, WorldTile tile, FightKilnController controller) {
		super(id, tile, true);
		setForceMultiArea(true);
		setNoDistanceCheck(true);
		setIgnoreDocile(true);
		setForceAgressive(true);
		this.controller = controller;
	}

	private int getDeathGfx() {
		switch (getId()) {
		case 15201:
			return 2926;
		case 15202:
			return 2927;
		case 15203:
			return 2957;
		case 15213:
		case 15214:
		case 15204:
			return 2928;
		case 15205:
			return 2959;
		case 15206:
		case 15207:
			return 2929;
		case 15208:
		case 15211:
		case 15212:
			return 2973;
		default:
			return 2926;
		}
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		controller.checkCrystal();
		setNextSpotAnim(new SpotAnim(getDeathGfx()));
		WorldTasks.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0)
					setNextAnimation(new Animation(defs.getDeathEmote()));
				else if (loop >= defs.getDeathDelay()) {
					reset();
					finish();
					controller.removeNPC();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	@Override
	public List<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<>(1);
		Set<Integer> playerIndexes = World.getRegion(getRegionId()).getPlayerIndexes();
		if (playerIndexes != null)
			for (int npcIndex : playerIndexes) {
				Player player = World.getPlayers().get(npcIndex);
				if (player == null || player.isDead() || player.hasFinished() || !player.isRunning())
					continue;
				possibleTarget.add(player);
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
