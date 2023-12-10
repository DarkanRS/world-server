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
package com.rs.utils;

import com.rs.cache.loaders.QCMesDefinitions;
import com.rs.game.World;
import com.rs.game.content.minigames.soulwars.SoulWars;
import com.rs.game.content.minigames.soulwars.SoulWarsKt;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.social.FCManager;
import com.rs.lib.game.Tile;
import com.rs.lib.game.WorldObject;
import com.rs.lib.io.OutputStream;
import com.rs.lib.util.Vec2;
import com.rs.lib.web.dto.FCData;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Arrays;
import java.util.function.Supplier;

public class WorldUtil {

	public static byte[] completeQuickMessage(Player player, int fileId, byte[] data) {
		QCMesDefinitions defs = QCMesDefinitions.getDefs(fileId);
		if (defs == null || defs.types == null)
			return null;

		OutputStream stream = new OutputStream();
		System.out.println(Arrays.toString(defs.types));
		System.out.println(defs);

		for (int i = 0;i < defs.types.length;i++)
			switch(defs.types[i]) {
			case STAT_BASE -> stream.writeByte(player.getSkills().getLevelForXp(defs.configs[i][0]));
			case TOSTRING_VARP -> stream.writeInt(player.getVars().getVar(defs.configs[i][0]));
			case TOSTRING_VARBIT -> stream.writeInt(player.getVars().getVarBit(defs.configs[i][0]));
			case ENUM_STRING -> stream.writeInt(player.getVars().getVar(defs.configs[i][1]-1));
			case ENUM_STRING_STATBASE -> stream.writeByte(player.getSkills().getLevelForXp(defs.configs[i][1]));
			case OBJTRADEDIALOG, OBJDIALOG, LISTDIALOG -> {
				if (data != null && data.length >= 2)
					return data;
			}
			case ACTIVECOMBATLEVEL -> stream.writeByte(player.getSkills().getCombatLevelWithSummoning());
			case ACC_GETMEANCOMBATLEVEL -> {
				FCData fc = FCManager.getFCData(player.getSocial().getCurrentFriendsChat());
				if (fc == null)
					stream.writeByte(0);
				else {
					int total = 0;
					int count = 0;
					for (String username : fc.getUsernames()) {
						Player p = World.getPlayerByUsername(username);
						if (p != null) {
							count++;
							total += p.getSkills().getCombatLevelWithSummoning();
						}
					}
					stream.writeByte(count > 0 ? total / count : 0);
				}
			}
			case ACC_GETCOUNT_WORLD -> {
				FCData fc = FCManager.getFCData(player.getSocial().getCurrentFriendsChat());
				if (fc == null)
					stream.writeByte(0);
				else {
					int count = 0;
					for (String username : fc.getUsernames()) {
						Player p = World.getPlayerByUsername(username);
						if (p != null)
							count++;
					}
					stream.writeByte(count);
				}
			}
			case TOSTRING_SHARED -> {
				stream.writeInt(SoulWarsKt.getQuickchatVar(defs.configs[i][0]));
			}
			case COUNTDIALOG, ENUM_STRING_CLAN -> { /*TODO*/ }
			default -> {}
			}

		return stream.toByteArray();
	}

	public static Tile targetToTile(Object object) {
		if (object instanceof Tile t)
			return t;
		else if (object instanceof Entity e)
			return e.getTile();
		else if (object instanceof WorldObject w)
			return w.getTile();
		throw new IllegalArgumentException("Invalid target object passed.");
	}

	public static Direction getDirectionTo(Entity entity, Object target) {
		Tile targTile = WorldUtil.targetToTile(target);
		Vec2 from = entity.getMiddleTileAsVector();
		Vec2 to = target instanceof Entity e ? e.getMiddleTileAsVector() : new Vec2(targTile);
		Vec2 sub = to.sub(from);
		sub.norm();
		Tile delta = sub.toTile();
		return Direction.forDelta(delta.getX(), delta.getY());
	}

	public static final int getAngleTo(Direction dir) {
		return ((int) (Math.atan2(-dir.getDx(), -dir.getDy()) * 2607.5945876176133)) & 0x3fff;
	}

	public static Direction getFaceDirection(Tile faceTile, Player player) {
		if (player.getX() < faceTile.getX())
			return Direction.EAST;
		if (player.getX() > faceTile.getX())
			return Direction.WEST;
		if (player.getY() < faceTile.getY())
			return Direction.NORTH;
		else if (player.getY() > faceTile.getY())
			return Direction.SOUTH;
		else
			return Direction.NORTH;
	}

	public static boolean collides(Entity entity, Entity target) {
		return entity.getPlane() == target.getPlane() && collides(entity.getX(), entity.getY(), entity.getSize(), target.getX(), target.getY(), target.getSize());
	}

	public static boolean collides(Tile entity, Tile target) {
		return entity.getPlane() == target.getPlane() && collides(entity.getX(), entity.getY(), 1, target.getX(), target.getY(), 1);
	}

	public static boolean collides(Tile entity, Tile target, int s1, int s2) {
		return entity.getPlane() == target.getPlane() && collides(entity.getX(), entity.getY(), s1, target.getX(), target.getY(), s2);
	}

	public static boolean isInRange(Tile entity, Tile target, int rangeRatio) {
		return entity.getPlane() == target.getPlane() && isInRange(entity.getX(), entity.getY(), 1, target.getX(), target.getY(), 1, rangeRatio);
	}

	public static boolean isInRange(Entity entity, Entity target, int rangeRatio) {
		return entity.getPlane() == target.getPlane() && isInRange(entity.getX(), entity.getY(), entity.getSize(), target.getX(), target.getY(), target.getSize(), rangeRatio);
	}

	public static boolean isInRange(Tile entity, Tile target, int rangeRatio, int s1, int s2) {
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

	public static double getMemUsedPerc() {
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
		MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();

		long jvmHeapUsed = heapMemoryUsage.getUsed() / 1048576L; // in MB
		long jvmNonHeapUsed = nonHeapMemoryUsage.getUsed() / 1048576L; // in MB
		long jvmTotalUsed = jvmHeapUsed + jvmNonHeapUsed;

		long jvmMaxMemory = (heapMemoryUsage.getMax() + nonHeapMemoryUsage.getMax()) / 1048576L; // in MB
		double jvmMemUsedPerc = ((double) jvmTotalUsed / jvmMaxMemory) * 100.0;
		return jvmMemUsedPerc;
	}
}
