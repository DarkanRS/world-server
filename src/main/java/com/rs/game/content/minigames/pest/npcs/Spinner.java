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
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;

public class Spinner extends PestMonsters {

	private byte healTicks;

	public Spinner(int id, Tile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned, int index, PestControl manager) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned, index, manager);
	}

	@Override
	public void processNPC() {
		PestPortal portal = manager.getPortals()[portalIndex];
		if (portal.isDead()) {
			explode();
			return;
		}
		if (!portal.isLocked) {
			healTicks++;
			if (!withinDistance(portal.getTile(), 1))
				this.addWalkSteps(portal.getX(), portal.getY());
			else if (healTicks % 6 == 0)
				healPortal(portal);
		}
	}

	private void healPortal(final PestPortal portal) {
		setNextFaceEntity(portal);
		WorldTasks.schedule(new Task() {

			@Override
			public void run() {
				setNextAnimation(new Animation(3911));
				setNextSpotAnim(new SpotAnim(658, 0, 96 << 16));
				if (portal.getHitpoints() != 0)
					portal.heal((portal.getMaxHitpoints() / portal.getHitpoints()) * 45);
				healTicks = 0; /* Saves memory in the long run. Meh */
			}
		});
	}

	private void explode() {
		final NPC npc = this;
		WorldTasks.schedule(new Task() {

			@Override
			public void run() {
				for (Player player : manager.getPlayers()) {
					if (!withinDistance(player, 7))
						continue;
					player.getPoison().makePoisoned(50);
					player.applyHit(new Hit(npc, 50, HitLook.TRUE_DAMAGE));
					npc.reset();
					npc.finish();
				}
			}
		}, 1);
	}
}
