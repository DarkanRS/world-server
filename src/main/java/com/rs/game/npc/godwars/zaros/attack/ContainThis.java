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
package com.rs.game.npc.godwars.zaros.attack;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;

public class ContainThis implements NexAttack {

	@Override
	public int attack(Nex nex, Entity target) {
		nex.setNextForceTalk(new ForceTalk("Contain this!"));
		nex.playSound(3316, 2);
		nex.setNextAnimation(new Animation(6984));
		final WorldTile base = nex.transform(1, 1, 0);
		nex.resetWalkSteps();
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				for (int y = 2; y >= -2; y-=2)
					for (int x = 2; x >= -2; x-=2) {
						if (x == y)
							continue;
						final WorldTile tile = base.transform(x, y, 0);
						final GameObject object = new GameObject(57262, ObjectType.SCENERY_INTERACT, 0, tile);
						if (tile != base && World.floorAndWallsFree(tile, (object.getDefinitions().getSizeX() + object.getDefinitions().getSizeY()) / 2)) {
							for (Player player : nex.getArena().getPlayers())
								if (player.getX() == tile.getX() && player.getY() == tile.getY()) {
									player.setNextAnimation(new Animation(1113));
									player.applyHit(new Hit(nex, Utils.random(200, 350), HitLook.TRUE_DAMAGE));
									player.sendMessage("The icicle spikes you to the spot!");
									player.sendMessage("You've been injured and can't use " + (player.getPrayer().isCurses() ? "deflect curses" : "protection prayers ") + "!");
									player.resetWalkSteps();
									player.setProtectionPrayBlock(12);
								}
							World.spawnObjectTemporary(object, Ticks.fromSeconds(7));
						}
					}
				return;
			}
		}, 6);
		return nex.getAttackSpeed() * 2;
	}

}
