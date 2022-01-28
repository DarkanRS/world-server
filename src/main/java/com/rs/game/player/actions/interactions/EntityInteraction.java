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
package com.rs.game.player.actions.interactions;

import com.rs.game.Entity;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.game.player.content.Effect;
import com.rs.utils.WorldUtil;

public abstract class EntityInteraction extends Interaction {

	protected Entity target;
	private int distance;

	public EntityInteraction(Entity target, int distance) {
		this.target = target;
		this.distance = distance;
	}

	public abstract boolean canStart(Player player);
	public abstract boolean checkAll(Player player);
	public abstract void interact(Player player);
	public abstract void onStop(Player player);

	@Override
	public final boolean start(Player player) {
		player.setNextFaceEntity(target);
		if (!canStart(player))
			return false;
		if (checkDistance(player) && checkAll(player))
			return true;
		player.setNextFaceEntity(null);
		return false;
	}

	@Override
	public final boolean process(Player player) {
		if (checkDistance(player) && checkAll(player)) {
			if (isWithinDistance(player, target)) {
				interact(player);
				player.setNextFaceEntity(null);
				stop(player);
			}
			return true;
		}
		return false;
	}

	public boolean isWithinDistance(Player player, Entity target) {
		if (!player.lineOfSightTo(target, distance == 0) || !WorldUtil.isInRange(player, target, distance + (target.hasWalkSteps() ? 1 : 0)) || WorldUtil.collides(player, target))
			return false;
		return true;
	}

	public final boolean checkDistance(Player player) {
		if (player.isDead() || player.hasFinished() || target.isDead() || target.hasFinished())
			return false;
		int distanceX = player.getX() - target.getX();
		int distanceY = player.getY() - target.getY();
		int size = target.getSize();
		int maxDistance = 16;
		if (player.getPlane() != target.getPlane() || distanceX > size + maxDistance || distanceX < -1 - maxDistance || distanceY > size + maxDistance || distanceY < -1 - maxDistance)
			return false;
		if (player.hasEffect(Effect.FREEZE))
			return !WorldUtil.collides(player, target);
		if (WorldUtil.collides(player, target) && !target.hasWalkSteps()) {
			player.resetWalkSteps();
			return player.calcFollow(target, true);
		}
		if (distance == 0 && !target.hasWalkSteps() && target.getSize() == 1) {
			Direction dir = Direction.forDelta(target.getX() - player.getX(), target.getY() - player.getY());
			if (dir != null)
				switch(dir) {
				case NORTH:
				case SOUTH:
				case EAST:
				case WEST:
					break;
				default:
					player.resetWalkSteps();
					player.calcFollow(target, player.getRun() ? 2 : 1, true, true);
					return true;
				}
		}
		if (!WorldUtil.isInRange(player, target, distance) || !player.lineOfSightTo(target, distance == 0)) {
			if (!player.hasWalkSteps() || target.hasWalkSteps()) {
				player.resetWalkSteps();
				player.calcFollow(target, player.getRun() ? 2 : 1, true, true);
			}
		} else
			player.resetWalkSteps();
		return true;
	}

}