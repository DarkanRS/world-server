package com.rs.utils;

import com.rs.game.Entity;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Vec2;

public class WorldUtil {
	
	public static Direction getDirectionTo(Entity entity, WorldTile target) {
		Vec2 from = entity.getMiddleWorldTileAsVector();
		Vec2 to = target instanceof Entity e ? e.getMiddleWorldTileAsVector() : new Vec2(target);
		Vec2 sub = to.sub(from);
		sub.norm();
		WorldTile delta = sub.toTile();
		return Direction.forDelta(delta.getX(), delta.getY());
	}
	
	public static final int getAngleTo(Direction dir) {
		return ((int) (Math.atan2(-dir.getDx(), -dir.getDy()) * 2607.5945876176133)) & 0x3fff;
	}
	
	public static Direction getFaceDirection(WorldTile faceTile, Player player) {
		if (player.getX() < faceTile.getX())
			return Direction.EAST;
		else if (player.getX() > faceTile.getX())
			return Direction.WEST;
		else if (player.getY() < faceTile.getY())
			return Direction.NORTH;
		else if (player.getY() > faceTile.getY())
			return Direction.SOUTH;
		else
			return Direction.NORTH;
	}
	
	public static boolean collides(Entity entity, Entity target) {
		return entity.getPlane() == target.getPlane() && collides(entity.getX(), entity.getY(), entity.getSize(), target.getX(), target.getY(), target.getSize());
	}
	
	public static boolean collides(WorldTile entity, WorldTile target) {
		return entity.getPlane() == target.getPlane() && collides(entity.getX(), entity.getY(), entity instanceof Entity e ? e.getSize() : 1, target.getX(), target.getY(), target instanceof Entity e ? e.getSize() : 1);
	}

	public static boolean collides(WorldTile entity, WorldTile target, int s1, int s2) {
		return entity.getPlane() == target.getPlane() && collides(entity.getX(), entity.getY(), s1, target.getX(), target.getY(), s2);
	}
	
	public static boolean isInRange(WorldTile entity, WorldTile target, int rangeRatio) {
		return entity.getPlane() == target.getPlane() && isInRange(entity.getX(), entity.getY(), entity instanceof Entity e ? e.getSize() : 1, target.getX(), target.getY(), target instanceof Entity e ? e.getSize() : 1, rangeRatio);
	}
	
	public static boolean isInRange(Entity entity, Entity target, int rangeRatio) {
		return entity.getPlane() == target.getPlane() && isInRange(entity.getX(), entity.getY(), entity.getSize(), target.getX(), target.getY(), target.getSize(), rangeRatio);
	}

	public static boolean isInRange(WorldTile entity, WorldTile target, int rangeRatio, int s1, int s2) {
		return entity.getPlane() == target.getPlane() && isInRange(entity.getX(), entity.getY(), s1, target.getX(), target.getY(), s2, rangeRatio);
	}
	
	public static boolean collides(int x1, int y1, int size1, int x2, int y2, int size2) {
		int distanceX = x1 - x2;
		int distanceY = y1 - y2;
		return distanceX < size2 && distanceX > -size1 && distanceY < size2 && distanceY > -size1;
	}
	
	public static boolean isInRange(int x1, int y1, int size1, int x2, int y2, int size2, int maxDistance) {
		int distanceX = x1 - x2;
		int distanceY = y1 - y2;
		if (distanceX > size2 + maxDistance || distanceX < -size1 - maxDistance || distanceY > size2 + maxDistance || distanceY < -size1 - maxDistance)
			return false;
		return true;
	}
}
