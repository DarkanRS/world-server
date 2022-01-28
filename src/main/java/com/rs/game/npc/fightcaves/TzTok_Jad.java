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
package com.rs.game.npc.fightcaves;

import com.rs.game.Entity;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.controllers.FightCavesController;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class TzTok_Jad extends FightCavesNPC {

	private boolean spawnedMinions;
	private FightCavesController controller;

	public TzTok_Jad(int id, WorldTile tile, FightCavesController controller) {
		super(id, tile);
		this.controller = controller;
		setNoDistanceCheck(true);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (!spawnedMinions && getHitpoints() < getMaxHitpoints() / 2) {
			spawnedMinions = true;
			controller.spawnHealers();
		}
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasks.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
					setNextSpotAnim(new SpotAnim(2924 + getSize()));
				} else if (loop >= defs.getDeathDelay()) {
					reset();
					finish();
					controller.win();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

}
