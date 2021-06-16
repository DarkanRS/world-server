package com.rs.game.player.actions;

import com.rs.game.Entity;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.utils.WorldUtil;

public abstract class EntityInteractionAction extends Action {
	
	protected Entity target;
	private int distance;
	
	public EntityInteractionAction(Entity target, int distance) {
		this.target = target;
		this.distance = distance;
	}

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
		return checkDistance(player) && checkAll(player);
	}

	public boolean isWithinDistance(Player player, Entity target) {
		if (!player.lineOfSightTo(target, distance == -1) || !WorldUtil.isInRange(player, target, distance + (player.hasWalkSteps() && target.hasWalkSteps() ? (player.getRun() && target.getRun() ? 2 : 1) : 0)) || WorldUtil.collides(player, target))
			return false;
		return true;
	}

	@Override
	public final int processWithDelay(Player player) {
		if (!isWithinDistance(player, target))
			return 0;
		return loopWithDelay(player);
	}
	
	public abstract boolean canStart(Player player);
	public abstract boolean checkAll(Player player);
	public abstract int loopWithDelay(Player player);
	public abstract void onStop(Player player);
	
	@Override
	public final void stop(final Player player) {
		player.setNextFaceEntity(null);
		onStop(player);
	}
	
	public final boolean checkDistance(Player player) {
		if (player.isDead() || player.hasFinished() || target.isDead() || target.hasFinished()) {
			return false;
		}
		int distanceX = player.getX() - target.getX();
		int distanceY = player.getY() - target.getY();
		int size = target.getSize();
		int maxDistance = 16;
		if (player.getPlane() != target.getPlane() || distanceX > size + maxDistance || distanceX < -1 - maxDistance || distanceY > size + maxDistance || distanceY < -1 - maxDistance) {
			return false;
		}
		if (player.isFrozen())
			return !WorldUtil.collides(player, target);
		if (WorldUtil.collides(player, target) && !target.hasWalkSteps()) {
			player.resetWalkSteps();
			return player.calcFollow(target, true);
		}
		if (distance == 0 && !target.hasWalkSteps() && target.getSize() == 1) {
			Direction dir = Direction.forDelta(target.getX() - player.getX(), target.getY() - player.getY());
			if (dir != null) {
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