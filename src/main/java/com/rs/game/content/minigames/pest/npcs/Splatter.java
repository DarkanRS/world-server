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
package com.rs.game.content.minigames.pest.npcs;

import com.rs.game.content.minigames.pest.PestControl;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class Splatter extends PestMonsters {

	public Splatter(int id, Tile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned, int index, PestControl manager) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned, index, manager);
	}

	@Override
	public void processNPC() {
		super.processNPC();
	}

	private void sendExplosion() {
		final Splatter splatter = this;
		setNextAnimation(new Animation(3888));
		WorldTasks.schedule(new Task() {

			@Override
			public void run() {
				setNextAnimation(new Animation(3889));
				setNextSpotAnim(new SpotAnim(649 + (getId() - 3727)));
				WorldTasks.schedule(new Task() {

					@Override
					public void run() {
						finish();
						for (Entity e : getPossibleTargets())
							if (e.withinDistance(splatter.getTile(), 2))
								e.applyHit(new Hit(splatter, Utils.getRandomInclusive(400), HitLook.TRUE_DAMAGE));
					}
				});
			}
		});
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasks.scheduleTimer(loop -> {
			if (loop == 0)
				sendExplosion();
			else if (loop >= defs.getDeathDelay()) {
				reset();
				return false;
			}
			return true;
		});
	}
}
