package com.rs.game.pathing;

import com.rs.game.Entity;
import com.rs.game.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.lib.util.Vec2;

public enum Direction {
	NORTH(0, 0, 1),
	NORTHEAST(1, 1, 1),
	EAST(2, 1, 0),
	SOUTHEAST(3, 1, -1),
	SOUTH(4, 0, -1),
	SOUTHWEST(5, -1, -1),
	WEST(6, -1, 0),
	NORTHWEST(7, -1, 1);
	
	private int id;
	private int dx;
	private int dy;
	
	private Direction(int id, int dx, int dy) {
		this.id = id;
		this.dx = dx;
		this.dy = dy;
	}
	
	public int getAngle() {
		return Utils.getAngleTo(dx, dy);
	}
	
	public static Direction random() {
		return values()[Utils.random(values().length)];
	}
	
	public int getId() {
		return id;
	}
	
	public boolean isDiagonal() {
		return dx != 0 && dy != 0;
	}
	
	public int getDx() {
		return dx;
	}
	
	public int getDy() {
		return dy;
	}

	public static Direction getById(int id) {
		switch (id) {
		case 0:
			return NORTH;
		case 1:
			return NORTHEAST;
		case 2:
			return EAST;
		case 3:
			return SOUTHEAST;
		case 4:
			return SOUTH;
		case 5:
			return SOUTHWEST;
		case 6:
			return WEST;
		case 7:
			return NORTHWEST;
		default:
			return SOUTH;
		}
	}
	
	public static Direction forDelta(int dx, int dy) {
		if (dy >= 1 && dx >= 1) {
			return NORTHEAST;
		} else if (dy <= -1 && dx >= 1) {
			return SOUTHEAST;
		} else if (dy <= -1 && dx <= -1) {
			return SOUTHWEST;
		} else if (dy >= 1 && dx <= -1) {
			return NORTHWEST;
		} else if (dy >= 1) {
			return NORTH;
		} else if (dx >= 1) {
			return EAST;
		} else if (dy <= -1) {
			return SOUTH;
		} else if (dx <= -1) {
			return WEST;
		} else {
			return null;
		}
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
	
	public static Direction getDirectionTo(Entity entity, WorldTile target) {
		Vec2 from = entity.getMiddleWorldTileAsVector();
		Vec2 to = target instanceof Entity ? ((Entity)target).getMiddleWorldTileAsVector() : new Vec2(target);
		Vec2 sub = to.sub(from);
		sub.norm();
		WorldTile delta = sub.toTile();
		return Direction.forDelta(delta.getX(), delta.getY());
	}
}
