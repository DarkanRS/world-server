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
package com.rs.game.content.skills.dungeoneering.npcs.combat;

import java.util.LinkedList;
import java.util.List;

import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.npcs.FamishedEye;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class FamishedEyeCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Famished warrior-eye", "Famished ranger-eye", "Famished mage-eye" };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		final FamishedEye eye = (FamishedEye) npc;

		if (eye.isInactive())
			return 0;
		if (!eye.isFirstHit()) {
			eye.setFirstHit(true);
			return Utils.random(5, 15);
		}

		npc.setNextAnimation(new Animation(14916));
		WorldTasks.schedule(new WorldTask() {

			private List<Tile> tiles;
			private Tile targetTile;

			int cycles;

			@Override
			public void run() {
				cycles++;
				if (cycles == 1) {
					tiles = new LinkedList<>();
					targetTile = Tile.of(target.getTile());
					World.sendProjectile(eye, targetTile, 2849, 35, 30, 41, 0, 15, 0);
				} else if (cycles == 2)
					for (int x = -1; x < 2; x++)
						for (int y = -1; y < 2; y++) {
							Tile attackedTile = targetTile.transform(x, y, 0);
							if (x != y)
								World.sendProjectile(targetTile, attackedTile, 2851, 35, 0, 26, 40, 16, 0);
							tiles.add(attackedTile);
						}
				else if (cycles == 3) {
					for (Tile tile : tiles) {
						if (!tile.matches(targetTile))
							World.sendSpotAnim(tile, new SpotAnim(2852, 35, 5));
						for (Entity t : eye.getPossibleTargets())
							if (t.matches(tile))
								t.applyHit(new Hit(eye, (int) Utils.random(eye.getMaxHit() * .25, eye.getMaxHit()), HitLook.TRUE_DAMAGE));
					}
					tiles.clear();
					stop();
					return;
				}
			}
		}, 0, 0);
		return Utils.random(5, 35);
	}
}
