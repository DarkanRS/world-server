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
package com.rs.game.content.bosses.godwars.zaros.attack;

import com.rs.game.content.bosses.godwars.zaros.Nex;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceMovement;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class Drag implements NexAttack {

	@Override
	public int attack(Nex nex, Entity target) {
		int distance = 0;
		Entity settedTarget = null;
		for (Entity t : nex.getPossibleTargets())
			if (t instanceof Player) {
				int thisDistance = (int) Utils.getDistance(t.getX(), t.getY(), nex.getX(), nex.getY());
				if (settedTarget == null || thisDistance > distance) {
					distance = thisDistance;
					settedTarget = t;
				}
			}
		if (settedTarget != null) {
			final Player player = (Player) settedTarget;
			player.lock(3);
			player.setNextAnimation(new Animation(14386));
			player.setNextSpotAnim(new SpotAnim(2767));
			player.setNextForceMovement(new ForceMovement(nex.getTile(), 2, Direction.forDelta(nex.getCoordFaceX(player.getSize()) - player.getX(), nex.getCoordFaceY(player.getSize()) - player.getY())));
			nex.setNextAnimation(new Animation(6986));
			nex.setTarget(player);
			player.setNextAnimation(new Animation(-1));
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					player.setNextTile(Tile.of(nex.getTile()));
					player.sendMessage("You've been injured and you can't use protective prayers!");
					player.setProtectionPrayBlock(12);
					player.sendMessage("You're stunned.");
				}
			});
		}
		return nex.getAttackSpeed();
	}

}
