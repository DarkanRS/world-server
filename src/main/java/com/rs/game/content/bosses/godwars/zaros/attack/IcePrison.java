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

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.bosses.godwars.zaros.Nex;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class IcePrison implements NexAttack {

	@Override
	public int attack(Nex nex, Entity target) {
		nex.setNextForceTalk(new ForceTalk("Die now, in a prison of ice!"));
		nex.voiceEffect(3308);
		nex.setNextAnimation(new Animation(6987));
		World.sendProjectile(nex, target, 362, 20, 20, 20, 0.45, 10, 0);
		final WorldTile base = WorldTile.of(target.getX(), target.getY(), target.getPlane());
		target.getTempAttribs().setB("inIcePrison", true);
		for (int x = -1; x <= 1; x++)
			for (int y = -1; y <= 1; y++) {
				final WorldTile tile = base.transform(x, y, target.getPlane());
				final GameObject object = new GameObject(57263, ObjectType.SCENERY_INTERACT, 0, tile);
				if (!tile.matches(base) && World.floorAndWallsFree(tile, (object.getDefinitions().getSizeX() + object.getDefinitions().getSizeY()) / 2))
					World.spawnObject(object);
				WorldTasks.schedule(new WorldTask() {

					boolean remove = false;

					@Override
					public void run() {
						if (remove) {
							World.removeObject(object);
							stop();
							return;
						}
						remove = true;
						target.getTempAttribs().setB("inIcePrison", false);
						if (target.getX() == tile.getX() && target.getY() == tile.getY()) {
							if (target instanceof Player)
								((Player)target).sendMessage("The centre of the ice prison freezes you to the bone!");
							target.resetWalkSteps();
							target.applyHit(new Hit(nex, Utils.random(600, 800), HitLook.TRUE_DAMAGE));
						}
					}
				}, 8, 0);
			}
		return nex.getAttackSpeed() * 2;
	}

}
