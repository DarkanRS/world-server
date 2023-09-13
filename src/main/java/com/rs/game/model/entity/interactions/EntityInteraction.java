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
package com.rs.game.model.entity.interactions;

import com.rs.game.content.Effect;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.utils.WorldUtil;

public abstract class EntityInteraction extends Interaction {

	protected Entity target;
	private int distance;
	private boolean stopFaceOnReached = true;
	private boolean stopWhenReached = true;

	public EntityInteraction(Entity target, int distance) {
		this.target = target;
		this.distance = distance;
	}

	public EntityInteraction keepFacing() {
		stopFaceOnReached = false;
		return this;
	}

	public EntityInteraction continueAfterReached() {
		stopWhenReached = false;
		return this;
	}

	public abstract boolean canStart(Entity entity);
	public abstract boolean checkAll(Entity entity);
	public abstract void interact(Entity entity);
	public abstract void onStop(Entity entity);

	@Override
	public final boolean start(Entity entity) {
		entity.setNextFaceEntity(target);
		if (!canStart(entity))
			return false;
		if (checkDistance(entity) && checkAll(entity))
			return true;
		return false;
	}

	@Override
	public void stop(Entity entity) {
		super.stop(entity);
		entity.setNextFaceEntity(null);
	}

	@Override
	public final boolean process(Entity player) {
		if (checkDistance(player) && checkAll(player)) {
			if (isWithinDistance(player, target, true)) {
				interact(player);
				if (stopFaceOnReached)
					player.setNextFaceEntity(null);
				if (stopWhenReached) {
					stop(player);
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public boolean isWithinDistance(Entity entity, Entity target, boolean addRunSteps) {
		if (entity.hasEffect(Effect.FREEZE))
			addRunSteps = false;
		boolean los = entity.lineOfSightTo(target, distance == 0);
		boolean inRange = WorldUtil.isInRange(entity, target, distance + (addRunSteps ? (target.getRun() ? target.hasWalkSteps() ? 2 : 1 : target.hasWalkSteps() ? 1 : 0) : 0));
		//boolean collides = WorldUtil.collides(player, target);
		if (!los || !inRange)
			return false;
		return true;
	}

	public final boolean checkDistance(Entity entity) {
		if (entity.isDead() || entity.hasFinished() || target.isDead() || target.hasFinished())
			return false;
		int distanceX = entity.getX() - target.getX();
		int distanceY = entity.getY() - target.getY();
		int size = target.getSize();
		int maxDistance = 16;
		if (entity.getPlane() != target.getPlane() || distanceX > size + maxDistance || distanceX < -1 - maxDistance || distanceY > size + maxDistance || distanceY < -1 - maxDistance)
			return false;
		if (entity.hasEffect(Effect.FREEZE))
			return !WorldUtil.collides(entity, target);
		if (WorldUtil.collides(entity, target)) {
			if (!entity.hasWalkSteps() && !target.hasWalkSteps()) {
				entity.resetWalkSteps();
				return entity.calcFollow(target, entity instanceof NPC n ? n.isIntelligentRouteFinder() : true);
			}
			return target instanceof Player ? true : entity instanceof Player ? target.hasWalkSteps() ? target instanceof NPC ? true : false : entity.calcFollow(target, true) : entity.calcFollow(target, entity instanceof NPC n ? n.isIntelligentRouteFinder() : true);
		}
		if (distance == 0 && !target.hasWalkSteps() && target.getSize() == 1) {
			Direction dir = Direction.forDelta(target.getX() - entity.getX(), target.getY() - entity.getY());
			if (dir != null)
				switch(dir) {
				case NORTH:
				case SOUTH:
				case EAST:
				case WEST:
					break;
				default:
					entity.resetWalkSteps();
					entity.calcFollow(target, entity.getRun() ? 2 : 1, entity instanceof NPC n ? n.isIntelligentRouteFinder() : true);
					return true;
				}
		}
		if (!isWithinDistance(entity, target, false)) {
			if (!entity.hasWalkSteps()) {
				entity.resetWalkSteps();
				entity.calcFollow(target, entity.getRun() ? 2 : 1, entity instanceof NPC n ? n.isIntelligentRouteFinder() : true);
			}
		} else {
			entity.resetWalkSteps();
			if (distance == 0 && target.getRun() == entity.getRun())
				entity.calcFollow(target, entity.getRun() ? 2 : 1, entity instanceof NPC n ? n.isIntelligentRouteFinder() : true);
		}
		return true;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
}