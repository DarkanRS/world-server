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
package com.rs.game.model.entity.actions;

import com.rs.game.content.Effect;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.pathing.EntityStrategy;
import com.rs.game.model.entity.pathing.RouteFinder;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class EntityFollow extends Action {

	private Entity target;

	public EntityFollow(Entity target) {
		this.target = target;
	}

	@Override
	public boolean start(Entity player) {
		player.setNextFaceEntity(target);
		if (checkAll(player))
			return true;
		player.setNextFaceEntity(null);
		return false;
	}

	private boolean checkAll(Entity player) {
		if (player.isDead() || player.hasFinished() || target.isDead() || target.hasFinished())
			return false;
		if (player.getPlane() != target.getPlane())
			return false;
		if (player.hasEffect(Effect.FREEZE))
			return true;
		int distanceX = player.getX() - target.getX();
		int distanceY = player.getY() - target.getY();
		int size = player.getSize();
		int maxDistance = 16;
		if (player.getPlane() != target.getPlane() || distanceX > size + maxDistance || distanceX < -1 - maxDistance || distanceY > size + maxDistance || distanceY < -1 - maxDistance)
			return false;
		int lastFaceEntity = target.getLastFaceEntity();
		WorldTile toTile = target.getTileBehind() != null && Utils.getDistance(target.getTile(), target.getTileBehind()) <= 3 ? target.getTileBehind() : target.getBackfacingTile();
		if (lastFaceEntity == player.getClientIndex() && target.getActionManager().getAction() instanceof EntityFollow)
			player.addWalkSteps(toTile.getX(), toTile.getY());
		else if (!player.lineOfSightTo(target, true) || !WorldUtil.isInRange(player.getX(), player.getY(), size, target.getX(), target.getY(), target.getSize(), 0)) {
			int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, player.getX(), player.getY(), player.getPlane(), player.getSize(), new EntityStrategy(target), true);
			if (steps == -1)
				return false;

			if (steps > 0) {
				player.resetWalkSteps();

				int[] bufferX = RouteFinder.getLastPathBufferX();
				int[] bufferY = RouteFinder.getLastPathBufferY();
				for (int step = steps - 1; step >= 0; step--)
					if (!player.addWalkSteps(bufferX[step], bufferY[step], 25, true, true))
						break;
			}
			return true;
		}
		return true;
	}

	@Override
	public boolean process(Entity player) {
		return checkAll(player);
	}

	@Override
	public int processWithDelay(Entity player) {
		return 0;
	}

	@Override
	public void stop(final Entity player) {
		player.setNextFaceEntity(null);
	}
}
