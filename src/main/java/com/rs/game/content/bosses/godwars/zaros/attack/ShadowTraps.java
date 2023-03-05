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

import java.util.HashMap;
import java.util.List;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.bosses.godwars.zaros.Nex;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class ShadowTraps implements NexAttack {

	@Override
	public int attack(Nex nex, Entity target) {
		nex.setNextForceTalk(new ForceTalk("Fear the Shadow!"));
		nex.voiceEffect(3314);
		nex.setNextAnimation(new Animation(6984));
		nex.setNextSpotAnim(new SpotAnim(1215));
		List<Entity> possibleTargets = nex.getPossibleTargets();
		final HashMap<String, int[]> tiles = new HashMap<>();
		for (Entity t : possibleTargets) {
			String key = t.getX() + "_" + t.getY();
			if (!tiles.containsKey(t.getX() + "_" + t.getY())) {
				tiles.put(key, new int[] { t.getX(), t.getY() });
				World.spawnObjectTemporary(new GameObject(57261, ObjectType.SCENERY_INTERACT, 0, t.getX(), t.getY(), 0), 4);
			}
		}
		WorldTasks.schedule(new WorldTask() {
			private boolean firstCall;

			@Override
			public void run() {
				if (!firstCall) {
					List<Entity> possibleTargets = nex.getPossibleTargets();
					for (int[] tile : tiles.values()) {
						World.sendSpotAnim(Tile.of(tile[0], tile[1], 0), new SpotAnim(383));
						for (Entity t : possibleTargets)
							if (t.getX() == tile[0] && t.getY() == tile[1])
								t.applyHit(new Hit(nex, Utils.getRandomInclusive(400) + 400, HitLook.TRUE_DAMAGE));
					}
					firstCall = true;
				} else
					stop();
			}

		}, 3, 3);
		return nex.getAttackSpeed();
	}

}
