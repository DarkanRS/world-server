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
package com.rs.utils;

import com.rs.cache.loaders.QCMesDefinitions;
import com.rs.game.Entity;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.lib.io.OutputStream;
import com.rs.lib.util.Vec2;

public class WorldUtil {
	
	public static byte[] completeQuickMessage(Player player, int fileId, byte[] data) {
		QCMesDefinitions defs = QCMesDefinitions.getDefs(fileId);
		if (defs == null || defs.types == null)
			return null;
		
		OutputStream stream = new OutputStream();
		
		for (int i = 0;i < defs.types.length;i++) {
			switch(defs.types[i]) {
			case STAT_BASE:
				stream.writeByte(player.getSkills().getLevelForXp(defs.configs[i][0]));
				break;
			case TOSTRING_VARP:
				stream.writeInt(player.getVars().getVar(defs.configs[i][0]));
				break;
			case TOSTRING_VARBIT:
				stream.writeInt(player.getVars().getVarBit(defs.configs[i][0]));
				break;
			case ENUM_STRING:
				stream.writeInt(player.getVars().getVar(defs.configs[i][1]-1));
				break;
			case ENUM_STRING_STATBASE:
				stream.writeByte(player.getSkills().getLevelForXp(defs.configs[i][1]));
				break;
			case OBJTRADEDIALOG:
			case OBJDIALOG:
			case LISTDIALOG:
				if (data != null && data.length >= 2)
					return data;
				break;
			case ACTIVECOMBATLEVEL:
				stream.writeByte(player.getSkills().getCombatLevelWithSummoning());
				break;
			case ACC_GETMEANCOMBATLEVEL:
				stream.writeByte(0); //TODO Avg combat level in FC
				break;
			case ACC_GETCOUNT_WORLD:
				stream.writeByte(0); //TODO Count players in FC
				break;
			default:
				System.out.println("Unhandled quickchat type: " + defs);
				break;
			}
		}
		
		return stream.toByteArray();
	}
	
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
