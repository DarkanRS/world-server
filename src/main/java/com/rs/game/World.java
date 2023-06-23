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
package com.rs.game;

import com.rs.Launcher;
import com.rs.Settings;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.cache.loaders.map.ClipFlag;
import com.rs.db.WorldDB;
import com.rs.engine.thread.LowPriorityTaskExecutor;
import com.rs.engine.thread.WorldThread;
import com.rs.game.content.ItemConstants;
import com.rs.game.content.world.areas.wilderness.WildernessController;
import com.rs.game.map.Chunk;
import com.rs.game.map.ChunkManager;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.EntityList;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.ClipType;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.pathing.WorldCollision;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.*;
import com.rs.lib.game.GroundItem.GroundItemType;
import com.rs.lib.net.packets.encoders.Sound;
import com.rs.lib.net.packets.encoders.Sound.SoundType;
import com.rs.lib.net.packets.encoders.updatezone.AddObject;
import com.rs.lib.util.Logger;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;
import com.rs.lib.util.Utils;
import com.rs.plugin.PluginManager;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.NPCInstanceEvent;
import com.rs.utils.*;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@PluginEventHandler
public final class World {

	public static int SYSTEM_UPDATE_DELAY = -1;
	public static long SYSTEM_UPDATE_START;

	private static final EntityList<Player> PLAYERS = new EntityList<>(Settings.PLAYERS_LIMIT);
	private static final Map<String, Player> PLAYER_MAP_USERNAME = new ConcurrentHashMap<>();
	private static final Map<String, Player> PLAYER_MAP_DISPLAYNAME = new ConcurrentHashMap<>();

	private static final EntityList<NPC> NPCS = new EntityList<>(Settings.NPCS_LIMIT);

	@ServerStartupEvent
	public static final void addSaveFilesTask() {
		LowPriorityTaskExecutor.schedule(() -> Launcher.saveFilesAsync(), 0, Ticks.fromSeconds(30));
	}

	public static final void addPlayer(Player player) {
		PLAYERS.add(player);
		PLAYER_MAP_USERNAME.put(player.getUsername(), player);
		PLAYER_MAP_DISPLAYNAME.put(player.getDisplayName(), player);
		if (player.getSession() != null && !player.getUsername().contains("cli_bot"))
			AccountLimiter.add(player.getSession().getIP());
	}

	public static void removePlayer(Player player) {
		PLAYERS.remove(player);
		PLAYER_MAP_USERNAME.remove(player.getUsername(), player);
		PLAYER_MAP_DISPLAYNAME.remove(player.getDisplayName(), player);
	}

	public static final void addNPC(NPC npc) {
		if (!NPCS.contains(npc))
			NPCS.add(npc);
	}

	public static final void removeNPC(NPC npc) {
		NPCS.remove(npc);
	}

	public static final NPC spawnNPC(int id, Tile tile, Direction direction, boolean permaDeath, boolean withFunction, String customName) {
		NPC n = null;
		if (withFunction) {
			Object fObj = PluginManager.getObj(new NPCInstanceEvent(id, tile, permaDeath));
			if (fObj != null)
				n = (NPC) fObj;
			else
				n = new NPC(id, tile, direction, permaDeath);
		} else
			n = new NPC(id, tile, direction, permaDeath);
		if (n != null)
			n.setPermName(customName);
		return n;
	}

	public static final NPC spawnNPC(int id, Tile tile, boolean permaDeath, boolean withFunction, String customName) {
		return spawnNPC(id, tile, Direction.SOUTH, permaDeath, withFunction, null);
	}


	public static final NPC spawnNPC(int id, Tile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean permaDeath, boolean withFunction) {
		return spawnNPC(id, tile, permaDeath, withFunction, null);
	}

	public static final NPC spawnNPC(int id, Tile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean permaDeath) {
		return spawnNPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, permaDeath, true);
	}

	public static final NPC spawnNPC(int id, Tile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		return spawnNPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false, true);
	}

	public static boolean canLightFire(int plane, int x, int y) {
		if (ClipFlag.flagged(getClipFlags(plane, x, y), ClipFlag.UNDER_ROOF) || (getClipFlags(plane, x, y) & 2097152) != 0 || getObjectWithSlot(Tile.of(x, y, plane), 2) != null)
			return false;
		return true;
	}

	public static boolean floorAndWallsFree(int plane, int x, int y, int size) {
		return floorAndWallsFree(Tile.of(x, y, plane), size);
	}

	public static boolean floorAndWallsFree(Tile tile, int size) {
		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++)
				if (!floorFree(tile.transform(x, y)) || !wallsFree(tile.transform(x, y)))
					return false;
		return true;
	}

	public static boolean floorFree(Tile tile, int size) {
		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++)
				if (!floorFree(tile.transform(x, y)))
					return false;
		return true;
	}

	public static boolean floorFree(int plane, int x, int y, int size) {
		return floorFree(Tile.of(x, y, plane), size);
	}

	public static boolean floorFree(Tile tile) {
		return !ClipFlag.flagged(getClipFlags(tile), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL);
	}

	public static boolean floorFree(int plane, int x, int y) {
		return floorFree(Tile.of(x, y, plane));
	}

	public static boolean wallsFree(Tile tile) {
		return !ClipFlag.flagged(getClipFlags(tile), ClipFlag.BW_NE, ClipFlag.BW_NW, ClipFlag.BW_SE, ClipFlag.BW_SW, ClipFlag.BW_E, ClipFlag.BW_N, ClipFlag.BW_S, ClipFlag.BW_W);
	}

	public static boolean wallsFree(int plane, int x, int y) {
		return wallsFree(Tile.of(x, y, plane));
	}

	public static int getClipFlags(int plane, int x, int y) {
		return WorldCollision.getFlags(Tile.of(x, y, plane));
	}

	public static int getClipFlags(Tile tile) {
		return WorldCollision.getFlags(tile);
	}

	public static boolean hasLineOfSight(Tile t1, Tile t2) {
		if (t1.getPlane() != t2.getPlane())
			return false;
		int plane = t1.getPlane();

		int x1 = t1.getX();
		int x2 = t2.getX();
		int y1 = t1.getY();
		int y2 = t2.getY();

		int dx = x2 - x1;
		int dxAbs = Math.abs(dx);
		int dy = y2 - y1;
		int dyAbs = Math.abs(dy);

		if (dxAbs > dyAbs) {
			int xTile = x1;
			int y = (y1 << 16) + 0x8000;
			int slope = (int) ((double) (dy << 16) / dxAbs); //Runescript no floating point values rofl

			int xInc;
			int xMask;
			if (dx > 0) {
				xInc = 1;
				xMask = ClipFlag.or(ClipFlag.BP_W, ClipFlag.BP_FULL);
			} else {
				xInc = -1;
				xMask = ClipFlag.or(ClipFlag.BP_E, ClipFlag.BP_FULL);
			}
			int yMask;
			if (dy < 0) {
				y -= 1;
				yMask = ClipFlag.or(ClipFlag.BP_N, ClipFlag.BP_FULL);
			} else
				yMask = ClipFlag.or(ClipFlag.BP_S, ClipFlag.BP_FULL);

			while (xTile != x2) {
				xTile += xInc;
				int yTile = y >>> 16;
				if ((getClipFlags(plane, xTile, yTile) & xMask) != 0)
					return false;
				y += slope;
				int newYTile = y >>> 16;
				if (newYTile != yTile && (getClipFlags(plane, xTile, newYTile) & yMask) != 0)
					return false;
			}
		} else {
			int yTile = y1;
			int x = (x1 << 16) + 0x8000;
			int slope = (int) ((double) (dx << 16) / dyAbs);

			int yInc;
			int yMask;
			if (dy > 0) {
				yInc = 1;
				yMask = ClipFlag.or(ClipFlag.BP_S, ClipFlag.BP_FULL);
			} else {
				yInc = -1;
				yMask = ClipFlag.or(ClipFlag.BP_N, ClipFlag.BP_FULL);
			}

			int xMask;
			if (dx < 0) {
				x -= 1;
				xMask = ClipFlag.or(ClipFlag.BP_E, ClipFlag.BP_FULL);
			} else
				xMask = ClipFlag.or(ClipFlag.BP_W, ClipFlag.BP_FULL);
			if (dxAbs == dyAbs) {
				//Runetek 5 diagonal check
				int xInc = (dx > 0 ? 1 : -1);
				int xTile = x1;
				while (yTile != y2) {
					if (((getClipFlags(plane, xTile + xInc, yTile) & xMask) != 0 || (getClipFlags(plane, xTile + xInc, yTile + yInc) & yMask) != 0) &&
						((getClipFlags(plane, xTile, yTile + yInc) & yMask) != 0 || (getClipFlags(plane, xTile + xInc, yTile + yInc) & xMask) != 0))
						return false;
					xTile += xInc;
					yTile += yInc;
				}
			} else
				while (yTile != y2) {
					yTile += yInc;
					int xTile = x >>> 16;
					if ((getClipFlags(plane, xTile, yTile) & yMask) != 0)
						return false;
					x += slope;
					int newXTile = x >>> 16;
					if (newXTile != xTile && (getClipFlags(plane, newXTile, yTile) & xMask) != 0)
						return false;
				}
		}
		return true;
	}

	public static final boolean checkWalkStep(Tile from, Tile to) {
		if (from.matches(to))
			return true;
		return checkWalkStep(from, to, 1);
	}

	public static boolean checkMeleeStep(Object from, int fromSize, Object to, int toSize) {
		Tile fromTile = WorldUtil.targetToTile(from);
		Tile toTile = WorldUtil.targetToTile(to);
		if (fromTile.getPlane() != toTile.getPlane())
			return false;
		Tile closestFrom = fromTile;
		Tile closestTo = toTile;
		double shortest = 1000.0;
		for (int x1 = 0; x1 < fromSize; x1++)
			for (int y1 = 0; y1 < fromSize; y1++)
				for (int x2 = 0; x2 < toSize; x2++)
					for (int y2 = 0; y2 < toSize; y2++) {
						double dist = Utils.getDistance(fromTile.transform(x1, y1), toTile.transform(x2, y2));
						if (dist < shortest) {
							closestFrom = fromTile.transform(x1, y1);
							closestTo = toTile.transform(x2, y2);
							shortest = dist;
						}
					}
		fromTile = closestFrom;
		toTile = closestTo;
		if (to == null || from == null)
			return false;
		if (fromTile.matches(toTile))
			return true;
		return checkWalkStep(fromTile, toTile, 1);
	}

	public static boolean inRange(int absX, int absY, int size, int targetX, int targetY, int targetSize, int distance) {
		if(absX < targetX) {
			/**
			 * West of target
			 */
			int closestX = absX + (size - 1);
			int diffX = targetX - closestX;
			if(diffX > distance)
				return false;
		} else if(absX > targetX) {
			/**
			 * East of target
			 */
			int closestTargetX = targetX + (targetSize - 1);
			int diffX = absX - closestTargetX;
			if(diffX > distance)
				return false;
		}
		if(absY < targetY) {
			/**
			 * South of target
			 */
			int closestY = absY + (size - 1);
			int diffY = targetY - closestY;
			if(diffY > distance)
				return false;
		} else if(absY > targetY) {
			/**
			 * North of target
			 */
			int closestTargetY = targetY + (targetSize - 1);
			int diffY = absY - closestTargetY;
			if(diffY > distance)
				return false;
		}
		return true;
	}

	public static final boolean checkWalkStep(Tile from, Tile to, int size) {
		return checkWalkStep(from, to, size, ClipType.NORMAL);
	}

	public static final boolean checkWalkStep(Tile from, Tile to, int size, ClipType type) {
		Direction dir = Direction.forDelta(to.getX() - from.getX(), to.getY() - from.getY());
		return checkWalkStep(from.getPlane(), from.getX(), from.getY(), dir, size, type);
	}

	public static final boolean checkWalkStep(Tile tile, Direction dir, int size) {
		return checkWalkStep(tile.getPlane(), tile.getX(), tile.getY(), dir.getDx(), dir.getDy(), size);
	}

	public static final boolean checkWalkStep(int plane, int x, int y, Direction dir, int size) {
		return checkWalkStep(plane, x, y, dir, size, ClipType.NORMAL);
	}

	public static final boolean checkWalkStep(int plane, int x, int y, Direction dir, int size, ClipType type) {
		return checkWalkStep(plane, x, y, dir.getDx(), dir.getDy(), size, type);
	}

	public static final boolean checkWalkStep(int plane, int x, int y, int xOffset, int yOffset, int size) {
		return checkWalkStep(plane, x, y, xOffset, yOffset, size, ClipType.NORMAL);
	}

	public static final boolean checkWalkStep(int plane, int x, int y, int xOffset, int yOffset, int size, ClipType type) {
		switch(type) {
			case FLYING:
				if (size == 1) {
					int flags = getClipFlags(plane, x + xOffset, y + yOffset);
					if (xOffset == -1 && yOffset == 0)
						return !ClipFlag.flagged(flags, ClipFlag.BP_FULL, ClipFlag.BP_E);
					if (xOffset == 1 && yOffset == 0)
						return !ClipFlag.flagged(flags, ClipFlag.BP_FULL, ClipFlag.BP_W);
					if (xOffset == 0 && yOffset == -1)
						return !ClipFlag.flagged(flags, ClipFlag.BP_FULL, ClipFlag.BP_N);
					if (xOffset == 0 && yOffset == 1)
						return !ClipFlag.flagged(flags, ClipFlag.BP_FULL, ClipFlag.BP_S);
					if (xOffset == -1 && yOffset == -1)
						return !ClipFlag.flagged(flags, ClipFlag.BP_FULL, ClipFlag.BP_N, ClipFlag.BP_E, ClipFlag.BP_NE) &&
							!ClipFlag.flagged(getClipFlags(plane, x - 1, y), ClipFlag.BP_FULL, ClipFlag.BP_E) &&
							!ClipFlag.flagged(getClipFlags(plane, x, y - 1), ClipFlag.BP_FULL, ClipFlag.BP_N);
					if (xOffset == 1 && yOffset == -1)
						return !ClipFlag.flagged(flags, ClipFlag.BP_FULL, ClipFlag.BP_N, ClipFlag.BP_W, ClipFlag.BP_NW) &&
							!ClipFlag.flagged(getClipFlags(plane, x + 1, y), ClipFlag.BP_FULL, ClipFlag.BP_W) &&
							!ClipFlag.flagged(getClipFlags(plane, x, y - 1), ClipFlag.BP_FULL, ClipFlag.BP_N);
					if (xOffset == -1 && yOffset == 1)
						return !ClipFlag.flagged(flags, ClipFlag.BP_FULL, ClipFlag.BP_E, ClipFlag.BP_S, ClipFlag.BP_SE) &&
							!ClipFlag.flagged(getClipFlags(plane, x - 1, y), ClipFlag.BP_FULL, ClipFlag.BP_E) &&
							!ClipFlag.flagged(getClipFlags(plane, x, y + 1), ClipFlag.BP_FULL, ClipFlag.BP_S);
					if (xOffset == 1 && yOffset == 1)
						return !ClipFlag.flagged(flags, ClipFlag.BP_FULL, ClipFlag.BP_S, ClipFlag.BP_W, ClipFlag.BP_SW) &&
							!ClipFlag.flagged(getClipFlags(plane, x + 1, y), ClipFlag.BP_FULL, ClipFlag.BP_W) &&
							!ClipFlag.flagged(getClipFlags(plane, x, y + 1), ClipFlag.BP_FULL, ClipFlag.BP_S);
				} else if (xOffset == -1 && yOffset == 0) {
					if (ClipFlag.flagged(getClipFlags(plane, x - 1, y), ClipFlag.BP_FULL, ClipFlag.BP_N, ClipFlag.BP_E, ClipFlag.BP_NE) ||
						ClipFlag.flagged(getClipFlags(plane, x - 1, -1 + (y + size)), ClipFlag.BP_FULL, ClipFlag.BP_E, ClipFlag.BP_S, ClipFlag.BP_SE))
						return false;
					for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x - 1, y + sizeOffset), ClipFlag.BP_FULL, ClipFlag.BP_N, ClipFlag.BP_E, ClipFlag.BP_S, ClipFlag.BP_NE, ClipFlag.BP_SE))
							return false;
				} else if (xOffset == 1 && yOffset == 0) {
					if (ClipFlag.flagged(getClipFlags(plane, x + size, y), ClipFlag.BP_FULL, ClipFlag.BP_N, ClipFlag.BP_W, ClipFlag.BP_NW) ||
						ClipFlag.flagged(getClipFlags(plane, x + size, y - (-size + 1)), ClipFlag.BP_FULL, ClipFlag.BP_S, ClipFlag.BP_W, ClipFlag.BP_SW))
						return false;
					for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x + size, y + sizeOffset), ClipFlag.BP_FULL, ClipFlag.BP_N, ClipFlag.BP_S, ClipFlag.BP_W, ClipFlag.BP_NW, ClipFlag.BP_SW))
							return false;
				} else if (xOffset == 0 && yOffset == -1) {
					if (ClipFlag.flagged(getClipFlags(plane, x, y - 1), ClipFlag.BP_FULL, ClipFlag.BP_N, ClipFlag.BP_E, ClipFlag.BP_NE) ||
						ClipFlag.flagged(getClipFlags(plane, x + size - 1, y - 1), ClipFlag.BP_FULL, ClipFlag.BP_N, ClipFlag.BP_W, ClipFlag.BP_NW))
						return false;
					for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x + sizeOffset, y - 1), ClipFlag.BP_FULL, ClipFlag.BP_N, ClipFlag.BP_E, ClipFlag.BP_W, ClipFlag.BP_NW, ClipFlag.BP_NE))
							return false;
				} else if (xOffset == 0 && yOffset == 1) {
					if (ClipFlag.flagged(getClipFlags(plane, x, y + size), ClipFlag.BP_FULL, ClipFlag.BP_E, ClipFlag.BP_S, ClipFlag.BP_SE) ||
						ClipFlag.flagged(getClipFlags(plane, x + (size - 1), y + size), ClipFlag.BP_FULL, ClipFlag.BP_S, ClipFlag.BP_W, ClipFlag.BP_SW))
						return false;
					for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x + sizeOffset, y + size), ClipFlag.BP_FULL, ClipFlag.BP_E, ClipFlag.BP_S, ClipFlag.BP_W, ClipFlag.BP_SE, ClipFlag.BP_SW))
							return false;
				} else if (xOffset == -1 && yOffset == -1) {
					if (ClipFlag.flagged(getClipFlags(plane, x - 1, y - 1), ClipFlag.BP_FULL, ClipFlag.BP_N, ClipFlag.BP_E, ClipFlag.BP_NE))
						return false;
					for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x - 1, y + (-1 + sizeOffset)), ClipFlag.BP_FULL, ClipFlag.BP_N, ClipFlag.BP_E, ClipFlag.BP_S, ClipFlag.BP_NE, ClipFlag.BP_SE) ||
							ClipFlag.flagged(getClipFlags(plane, sizeOffset - 1 + x, y - 1), ClipFlag.BP_FULL, ClipFlag.BP_N, ClipFlag.BP_E, ClipFlag.BP_W, ClipFlag.BP_NW, ClipFlag.BP_NE))
							return false;
				} else if (xOffset == 1 && yOffset == -1) {
					if (ClipFlag.flagged(getClipFlags(plane, x + size, y - 1), ClipFlag.BP_FULL, ClipFlag.BP_N, ClipFlag.BP_W, ClipFlag.BP_NW))
						return false;
					for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x + size, sizeOffset + (-1 + y)), ClipFlag.BP_FULL, ClipFlag.BP_N, ClipFlag.BP_S, ClipFlag.BP_W, ClipFlag.BP_NW, ClipFlag.BP_SW) ||
							ClipFlag.flagged(getClipFlags(plane, x + sizeOffset, y - 1), ClipFlag.BP_FULL, ClipFlag.BP_N, ClipFlag.BP_E, ClipFlag.BP_W, ClipFlag.BP_NW, ClipFlag.BP_NE))
							return false;
				} else if (xOffset == -1 && yOffset == 1) {
					if (ClipFlag.flagged(getClipFlags(plane, x - 1, y + size), ClipFlag.BP_FULL, ClipFlag.BP_E, ClipFlag.BP_S, ClipFlag.BP_SE))
						return false;
					for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x - 1, y + sizeOffset), ClipFlag.BP_FULL, ClipFlag.BP_N, ClipFlag.BP_E, ClipFlag.BP_S, ClipFlag.BP_NE, ClipFlag.BP_SE) ||
							ClipFlag.flagged(getClipFlags(plane, -1 + (x + sizeOffset), y + size), ClipFlag.BP_FULL, ClipFlag.BP_E, ClipFlag.BP_S, ClipFlag.BP_W, ClipFlag.BP_SE, ClipFlag.BP_SW))
							return false;
				} else if (xOffset == 1 && yOffset == 1) {
					if (ClipFlag.flagged(getClipFlags(plane, x + size, y + size), ClipFlag.BP_FULL, ClipFlag.BP_S, ClipFlag.BP_W, ClipFlag.BP_SW))
						return false;
					for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x + sizeOffset, y + size), ClipFlag.BP_FULL, ClipFlag.BP_E, ClipFlag.BP_S, ClipFlag.BP_W, ClipFlag.BP_SE, ClipFlag.BP_SW) ||
							ClipFlag.flagged(getClipFlags(plane, x + size, y + sizeOffset), ClipFlag.BP_FULL, ClipFlag.BP_N, ClipFlag.BP_S, ClipFlag.BP_W, ClipFlag.BP_NW, ClipFlag.BP_SW))
							return false;
				}
				return true;
			case WATER:
				if (size == 1) {
					int flags = getClipFlags(plane, x + xOffset, y + yOffset);
					if (xOffset == -1 && yOffset == 0)
						return ClipFlag.flagged(flags, ClipFlag.PFBW_FLOOR) && !ClipFlag.flagged(flags, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_E);
					if (xOffset == 1 && yOffset == 0)
						return ClipFlag.flagged(flags, ClipFlag.PFBW_FLOOR) && !ClipFlag.flagged(flags, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_W);
					if (xOffset == 0 && yOffset == -1)
						return ClipFlag.flagged(flags, ClipFlag.PFBW_FLOOR) && !ClipFlag.flagged(flags, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N);
					if (xOffset == 0 && yOffset == 1)
						return ClipFlag.flagged(flags, ClipFlag.PFBW_FLOOR) && !ClipFlag.flagged(flags, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_S);
					if (xOffset == -1 && yOffset == -1)
						return ClipFlag.flagged(flags, ClipFlag.PFBW_FLOOR) && !ClipFlag.flagged(flags, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_E, ClipFlag.BW_NE) &&
							!ClipFlag.flagged(getClipFlags(plane, x - 1, y), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_E) &&
							!ClipFlag.flagged(getClipFlags(plane, x, y - 1), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N);
					if (xOffset == 1 && yOffset == -1)
						return ClipFlag.flagged(flags, ClipFlag.PFBW_FLOOR) && !ClipFlag.flagged(flags, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_W, ClipFlag.BW_NW) &&
							!ClipFlag.flagged(getClipFlags(plane, x + 1, y), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_W) &&
							!ClipFlag.flagged(getClipFlags(plane, x, y - 1), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N);
					if (xOffset == -1 && yOffset == 1)
						return ClipFlag.flagged(flags, ClipFlag.PFBW_FLOOR) && !ClipFlag.flagged(flags, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_E, ClipFlag.BW_S, ClipFlag.BW_SE) &&
							!ClipFlag.flagged(getClipFlags(plane, x - 1, y), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_E) &&
							!ClipFlag.flagged(getClipFlags(plane, x, y + 1), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_S);
					if (xOffset == 1 && yOffset == 1)
						return ClipFlag.flagged(flags, ClipFlag.PFBW_FLOOR) && !ClipFlag.flagged(flags, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_S, ClipFlag.BW_W, ClipFlag.BW_SW) &&
							!ClipFlag.flagged(getClipFlags(plane, x + 1, y), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_W) &&
							!ClipFlag.flagged(getClipFlags(plane, x, y + 1), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_S);
				} else if (xOffset == -1 && yOffset == 0) {
					if (ClipFlag.flagged(getClipFlags(plane, x - 1, y), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_E, ClipFlag.BW_NE)
						|| ClipFlag.flagged(getClipFlags(plane, x - 1, -1 + (y + size)), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_E, ClipFlag.BW_S, ClipFlag.BW_SE))
						return false;
					for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x - 1, y + sizeOffset), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_E, ClipFlag.BW_S, ClipFlag.BW_NE, ClipFlag.BW_SE))
							return false;
				} else if (xOffset == 1 && yOffset == 0) {
					if (ClipFlag.flagged(getClipFlags(plane, x + size, y), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_W, ClipFlag.BW_NW)
						|| ClipFlag.flagged(getClipFlags(plane, x + size, y - (-size + 1)), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_S, ClipFlag.BW_W, ClipFlag.BW_SW))
						return false;
					for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x + size, y + sizeOffset), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_S, ClipFlag.BW_W, ClipFlag.BW_NW, ClipFlag.BW_SW))
							return false;
				} else if (xOffset == 0 && yOffset == -1) {
					if (ClipFlag.flagged(getClipFlags(plane, x, y - 1), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_E, ClipFlag.BW_NE)
						|| ClipFlag.flagged(getClipFlags(plane, x + size - 1, y - 1), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_W, ClipFlag.BW_NW))
						return false;
					for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x + sizeOffset, y - 1), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_E, ClipFlag.BW_W, ClipFlag.BW_NW, ClipFlag.BW_NE))
							return false;
				} else if (xOffset == 0 && yOffset == 1) {
					if (ClipFlag.flagged(getClipFlags(plane, x, y + size), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_E, ClipFlag.BW_S, ClipFlag.BW_SE)
						|| ClipFlag.flagged(getClipFlags(plane, x + (size - 1), y + size), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_S, ClipFlag.BW_W, ClipFlag.BW_SW))
						return false;
					for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x + sizeOffset, y + size), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_E, ClipFlag.BW_S, ClipFlag.BW_W, ClipFlag.BW_SE, ClipFlag.BW_SW))
							return false;
				} else if (xOffset == -1 && yOffset == -1) {
					if (ClipFlag.flagged(getClipFlags(plane, x - 1, y - 1), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_E, ClipFlag.BW_NE))
						return false;
					for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x - 1, y + (-1 + sizeOffset)), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_E, ClipFlag.BW_S, ClipFlag.BW_NE, ClipFlag.BW_SE)
							|| ClipFlag.flagged(getClipFlags(plane, sizeOffset - 1 + x, y - 1), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_E, ClipFlag.BW_W, ClipFlag.BW_NW, ClipFlag.BW_NE))
							return false;
				} else if (xOffset == 1 && yOffset == -1) {
					if (ClipFlag.flagged(getClipFlags(plane, x + size, y - 1), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_W, ClipFlag.BW_NW))
						return false;
					for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x + size, sizeOffset + (-1 + y)), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_S, ClipFlag.BW_W, ClipFlag.BW_NW, ClipFlag.BW_SW)
							|| ClipFlag.flagged(getClipFlags(plane, x + sizeOffset, y - 1), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_E, ClipFlag.BW_W, ClipFlag.BW_NW, ClipFlag.BW_NE))
							return false;
				} else if (xOffset == -1 && yOffset == 1) {
					if (ClipFlag.flagged(getClipFlags(plane, x - 1, y + size), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_E, ClipFlag.BW_S, ClipFlag.BW_SE))
						return false;
					for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x - 1, y + sizeOffset), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_E, ClipFlag.BW_S, ClipFlag.BW_NE, ClipFlag.BW_SE)
							|| ClipFlag.flagged(getClipFlags(plane, -1 + (x + sizeOffset), y + size), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_E, ClipFlag.BW_S, ClipFlag.BW_W, ClipFlag.BW_SE, ClipFlag.BW_SW))
							return false;
				} else if (xOffset == 1 && yOffset == 1) {
					if (ClipFlag.flagged(getClipFlags(plane, x + size, y + size), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_S, ClipFlag.BW_W, ClipFlag.BW_SW))
						return false;
					for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x + sizeOffset, y + size), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_E, ClipFlag.BW_S, ClipFlag.BW_W, ClipFlag.BW_SE, ClipFlag.BW_SW)
							|| ClipFlag.flagged(getClipFlags(plane, x + size, y + sizeOffset), ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_S, ClipFlag.BW_W, ClipFlag.BW_NW, ClipFlag.BW_SW))
							return false;
				}
				return true;
			case NOCLIP:
				return true;
			case NORMAL:
			default:
				if (size == 1) {
					int flags = getClipFlags(plane, x + xOffset, y + yOffset);
					if (xOffset == -1 && yOffset == 0)
						return !ClipFlag.flagged(flags, ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_E);
					if (xOffset == 1 && yOffset == 0)
						return !ClipFlag.flagged(flags, ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_W);
					if (xOffset == 0 && yOffset == -1)
						return !ClipFlag.flagged(flags, ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N);
					if (xOffset == 0 && yOffset == 1)
						return !ClipFlag.flagged(flags, ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_S);
					if (xOffset == -1 && yOffset == -1)
						return !ClipFlag.flagged(flags, ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_E, ClipFlag.BW_NE) && !ClipFlag.flagged(getClipFlags(plane, x - 1, y), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_E) && !ClipFlag.flagged(getClipFlags(plane, x, y - 1), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N);
					if (xOffset == 1 && yOffset == -1)
						return !ClipFlag.flagged(flags, ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_W, ClipFlag.BW_NW) && !ClipFlag.flagged(getClipFlags(plane, x + 1, y), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_W) && !ClipFlag.flagged(getClipFlags(plane, x, y - 1), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N);
					if (xOffset == -1 && yOffset == 1)
						return !ClipFlag.flagged(flags, ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_E, ClipFlag.BW_S, ClipFlag.BW_SE) && !ClipFlag.flagged(getClipFlags(plane, x - 1, y), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_E) && !ClipFlag.flagged(getClipFlags(plane, x, y + 1), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_S);
					if (xOffset == 1 && yOffset == 1)
						return !ClipFlag.flagged(flags, ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_S, ClipFlag.BW_W, ClipFlag.BW_SW) && !ClipFlag.flagged(getClipFlags(plane, x + 1, y), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_W) && !ClipFlag.flagged(getClipFlags(plane, x, y + 1), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_S);
				} else if (xOffset == -1 && yOffset == 0) {
					if (ClipFlag.flagged(getClipFlags(plane, x - 1, y), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_E, ClipFlag.BW_NE) || ClipFlag.flagged(getClipFlags(plane, x - 1, -1 + (y + size)), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_E, ClipFlag.BW_S, ClipFlag.BW_SE))
						return false;
					for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x - 1, y + sizeOffset), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_E, ClipFlag.BW_S, ClipFlag.BW_NE, ClipFlag.BW_SE))
							return false;
				} else if (xOffset == 1 && yOffset == 0) {
					if (ClipFlag.flagged(getClipFlags(plane, x + size, y), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_W, ClipFlag.BW_NW) || ClipFlag.flagged(getClipFlags(plane, x + size, y - (-size + 1)), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_S, ClipFlag.BW_W, ClipFlag.BW_SW))
						return false;
					for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x + size, y + sizeOffset), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_S, ClipFlag.BW_W, ClipFlag.BW_NW, ClipFlag.BW_SW))
							return false;
				} else if (xOffset == 0 && yOffset == -1) {
					if (ClipFlag.flagged(getClipFlags(plane, x, y - 1), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_E, ClipFlag.BW_NE) || ClipFlag.flagged(getClipFlags(plane, x + size - 1, y - 1), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_W, ClipFlag.BW_NW))
						return false;
					for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x + sizeOffset, y - 1), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_E, ClipFlag.BW_W, ClipFlag.BW_NW, ClipFlag.BW_NE))
							return false;
				} else if (xOffset == 0 && yOffset == 1) {
					if (ClipFlag.flagged(getClipFlags(plane, x, y + size), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_E, ClipFlag.BW_S, ClipFlag.BW_SE) || ClipFlag.flagged(getClipFlags(plane, x + (size - 1), y + size), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_S, ClipFlag.BW_W, ClipFlag.BW_SW))
						return false;
					for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x + sizeOffset, y + size), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_E, ClipFlag.BW_S, ClipFlag.BW_W, ClipFlag.BW_SE, ClipFlag.BW_SW))
							return false;
				} else if (xOffset == -1 && yOffset == -1) {
					if (ClipFlag.flagged(getClipFlags(plane, x - 1, y - 1), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_E, ClipFlag.BW_NE))
						return false;
					for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x - 1, y + (-1 + sizeOffset)), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_E, ClipFlag.BW_S, ClipFlag.BW_NE, ClipFlag.BW_SE) || ClipFlag.flagged(getClipFlags(plane, sizeOffset - 1 + x, y - 1), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_E, ClipFlag.BW_W, ClipFlag.BW_NW, ClipFlag.BW_NE))
							return false;
				} else if (xOffset == 1 && yOffset == -1) {
					if (ClipFlag.flagged(getClipFlags(plane, x + size, y - 1), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_W, ClipFlag.BW_NW))
						return false;
					for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x + size, sizeOffset + (-1 + y)), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_S, ClipFlag.BW_W, ClipFlag.BW_NW, ClipFlag.BW_SW) || ClipFlag.flagged(getClipFlags(plane, x + sizeOffset, y - 1), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_E, ClipFlag.BW_W, ClipFlag.BW_NW, ClipFlag.BW_NE))
							return false;
				} else if (xOffset == -1 && yOffset == 1) {
					if (ClipFlag.flagged(getClipFlags(plane, x - 1, y + size), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_E, ClipFlag.BW_S, ClipFlag.BW_SE))
						return false;
					for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x - 1, y + sizeOffset), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_E, ClipFlag.BW_S, ClipFlag.BW_NE, ClipFlag.BW_SE) || ClipFlag.flagged(getClipFlags(plane, -1 + (x + sizeOffset), y + size), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_E, ClipFlag.BW_S, ClipFlag.BW_W, ClipFlag.BW_SE, ClipFlag.BW_SW))
							return false;
				} else if (xOffset == 1 && yOffset == 1) {
					if (ClipFlag.flagged(getClipFlags(plane, x + size, y + size), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_S, ClipFlag.BW_W, ClipFlag.BW_SW))
						return false;
					for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
						if (ClipFlag.flagged(getClipFlags(plane, x + sizeOffset, y + size), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_E, ClipFlag.BW_S, ClipFlag.BW_W, ClipFlag.BW_SE, ClipFlag.BW_SW) || ClipFlag.flagged(getClipFlags(plane, x + size, y + sizeOffset), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL, ClipFlag.BW_N, ClipFlag.BW_S, ClipFlag.BW_W, ClipFlag.BW_NW, ClipFlag.BW_SW))
							return false;
				}
				return true;
		}
	}

	public static final boolean containsPlayer(String username) {
		for (Player p2 : PLAYERS) {
			if (p2 == null)
				continue;
			if (p2.getUsername().equals(username))
				return true;
		}
		return false;
	}

	public static Tile getFreeTile(Tile center, int distance) {
		Tile tile = center;
		for (int i = 0; i < 10; i++) {
			tile = Tile.of(center, distance);
			if (World.floorAndWallsFree(tile, 1))
				return tile;
		}
		return center;
	}

	public static Player getPlayerByUsername(String username) {
		return PLAYER_MAP_USERNAME.get(username);
	}

	public static Player getPlayerByDisplay(String displayName) {
		return PLAYER_MAP_DISPLAYNAME.get(Utils.formatPlayerNameForDisplay(displayName));
	}

	public static void forceGetPlayerByDisplay(String displayName, Consumer<Player> result) {
		displayName = Utils.formatPlayerNameForDisplay(displayName);
		Player player = getPlayerByDisplay(displayName);
		if (player != null) {
			result.accept(player);
			return;
		}
		WorldDB.getPlayers().getByUsername(Utils.formatPlayerNameForProtocol(displayName), p -> result.accept(p));
	}

	public static final EntityList<Player> getPlayers() {
		return PLAYERS;
	}

	public static final EntityList<NPC> getNPCs() {
		return NPCS;
	}

	private World() {

	}

	public static final long getTicksTillUpdate() {
		if (SYSTEM_UPDATE_START == 0)
			return -1;
		return (SYSTEM_UPDATE_DELAY - (World.getServerTicks() - SYSTEM_UPDATE_START));
	}

	public static final void safeShutdown(int delay) {
		if (SYSTEM_UPDATE_START != 0)
			return;
		SYSTEM_UPDATE_START = World.getServerTicks();
		SYSTEM_UPDATE_DELAY = delay;
		for (Player player : World.getPlayers()) {
			if (player == null || !player.hasStarted() || player.hasFinished())
				continue;
			player.getPackets().sendSystemUpdate(delay);
		}
		WorldTasks.schedule(delay, () -> {
			try {
				for (Player player : World.getPlayers()) {
					if (player == null || !player.hasStarted())
						continue;
					player.getPackets().sendLogout(true);
					player.realFinish();
					WorldDB.getPlayers().saveSync(player);
				}
				WorldPersistentData.save();
				Launcher.shutdown();
			} catch (Throwable e) {
				Logger.handle(World.class, "safeShutdown", e);
			}
		});
	}

	public static WorldPersistentData getData() {
		return WorldPersistentData.get();
	}

	public static final boolean isSpawnedObject(GameObject object) {
		return ChunkManager.getChunk(object.getTile().getChunkId()).getSpawnedObjects().contains(object);
	}

	public static final void spawnObject(GameObject object) {
		ChunkManager.getChunk(object.getTile().getChunkId()).spawnObject(object, true);
	}

	public static final void spawnObject(GameObject object, boolean clip) {
		ChunkManager.getChunk(object.getTile().getChunkId()).spawnObject(object, clip);
	}

	public static final void unclipTile(Tile tile) {
		WorldCollision.unclip(tile);
	}

	public static final void removeObject(GameObject object) {
		ChunkManager.getChunk(object.getTile().getChunkId()).removeObject(object);
	}

	public static final void spawnObjectTemporary(final GameObject object, int ticks, boolean clip) {
		spawnObject(object, clip);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				try {
					if (!World.isSpawnedObject(object))
						return;
					removeObject(object);
				} catch (Throwable e) {
					Logger.handle(World.class, "spawnObjectTemporary", e);
				}
			}
		}, Utils.clampI(ticks - 1, 0, Integer.MAX_VALUE));
	}

	public static final void spawnObjectTemporary(final GameObject object, int ticks) {
		spawnObjectTemporary(object, ticks, true);
	}

	public static final boolean removeObjectTemporary(final GameObject object, int ticks) {
		if (object == null)
			return false;
		removeObject(object);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				try {
					spawnObject(object);
				} catch (Throwable e) {
					Logger.handle(World.class, "removeObjectTemporary", e);
				}
			}
		}, Utils.clampI(ticks, 0, Integer.MAX_VALUE));
		return true;
	}

	public static final void spawnTempGroundObject(final GameObject object, final int replaceId, int ticks) {
		spawnObject(object);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				try {
					removeObject(object);
					addGroundItem(new Item(replaceId), object.getTile(), null, false, 180);
				} catch (Throwable e) {
					Logger.handle(World.class, "spawnTempGroundObject", e);
				}
			}
		}, Utils.clampI(ticks - 1, 0, Integer.MAX_VALUE));
	}

	public static void allPlayers(Consumer<Player> func) {
		synchronized (PLAYERS) {
			for (Player p : World.getPlayers())
				func.accept(p);
		}
	}

	public static List<GameObject> getSpawnedObjectsInChunkRange(int chunkId, int chunkRadius) {
		List<GameObject> objects = new ArrayList<>();
		Set<Integer> chunkIds = getChunkRadius(chunkId, chunkRadius);
		for (int chunk : chunkIds) {
			for (GameObject obj : ChunkManager.getChunk(chunk).getSpawnedObjects()) {
				if (obj == null)
					continue;
				objects.add(obj);
			}
		}
		return objects;
	}

	public static List<GameObject> getAllObjectsInChunkRange(int chunkId, int chunkRadius) {
		List<GameObject> objects = new ArrayList<>();
		Set<Integer> chunkIds = getChunkRadius(chunkId, chunkRadius);
		for (int chunk : chunkIds) {
			for (GameObject obj : ChunkManager.getChunk(chunk).getAllObjects()) {
				if (obj == null)
					continue;
				objects.add(obj);
			}
		}
		return objects;
	}

	public static List<GameObject> getBaseObjectsInChunkRange(int chunkId, int chunkRadius) {
		List<GameObject> objects = new ArrayList<>();
		Set<Integer> chunkIds = getChunkRadius(chunkId, chunkRadius);
		for (int chunk : chunkIds) {
			for (GameObject obj : ChunkManager.getChunk(chunk).getBaseObjects()) {
				if (obj == null)
					continue;
				objects.add(obj);
			}
		}
		return objects;
	}

	public static List<NPC> getNPCsInChunkRange(int chunkId, int chunkRadius) {
		List<NPC> npcs = new ArrayList<>();
		Set<Integer> chunkIds = getChunkRadius(chunkId, chunkRadius);
		for (int chunk : chunkIds) {
			for (int pid : ChunkManager.getChunk(chunk).getNPCsIndexes()) {
				NPC npc = World.getNPCs().get(pid);
				if (npc == null || npc.hasFinished())
					continue;
				npcs.add(npc);
			}
		}
		return npcs;
	}

	public static List<Player> getPlayersInChunkRange(int chunkId, int chunkRadius) {
		List<Player> players = new ArrayList<>();
		Set<Integer> chunkIds = getChunkRadius(chunkId, chunkRadius);
		for (int chunk : chunkIds) {
			for (int pid : ChunkManager.getChunk(chunk).getPlayerIndexes()) {
				Player player = World.getPlayers().get(pid);
				if (player == null || !player.hasStarted() || player.hasFinished())
					continue;
				players.add(player);
			}
		}
		return players;
	}

	public static List<GroundItem> getAllGroundItemsInChunkRange(int chunkId, int chunkRadius) {
		List<GroundItem> objects = new ArrayList<>();
		Set<Integer> chunkIds = getChunkRadius(chunkId, chunkRadius);
		for (int chunk : chunkIds) {
			for (GroundItem obj : ChunkManager.getChunk(chunk).getAllGroundItems()) {
				if (obj == null)
					continue;
				objects.add(obj);
			}
		}
		return objects;
	}

	public static Set<Integer> getChunkRadius(int chunkId, int radius) {
		Set<Integer> chunksXYLoop = new IntOpenHashSet();
		for (int cx = -radius * Chunk.X_INC; cx <= radius * Chunk.X_INC; cx += Chunk.X_INC)
			for (int cy = -radius; cy <= radius; cy++)
				chunksXYLoop.add(chunkId + cx + cy);
		return chunksXYLoop;
	}

	public static Set<Integer> mapRegionIdsToChunks(Set<Integer> mapRegionsIds) {
		Set<Integer> chunkIds = new IntOpenHashSet();
		for (int regionId : mapRegionsIds) {
			int[] rCoords = MapUtils.decode(Structure.REGION, regionId);
			int cX = rCoords[0] << 3, cY = rCoords[1] << 3;
			for (int plane = 0;plane < 4;plane++)
				for (int x = 0;x < 8;x++)
					for (int y = 0;y < 8;y++)
						chunkIds.add(MapUtils.encode(Structure.CHUNK, cX+x, cY+y, plane));
		}
		return chunkIds;
	}

	public static Set<Integer> mapRegionIdsToChunks(Set<Integer> mapRegionsIds, int plane) {
		Set<Integer> chunkIds = new IntOpenHashSet();
		for (int regionId : mapRegionsIds) {
			int[] rCoords = MapUtils.decode(Structure.REGION, regionId);
			int cX = rCoords[0] << 3, cY = rCoords[1] << 3;
			for (int x = 0;x < 8;x++)
				for (int y = 0;y < 8;y++)
					chunkIds.add(MapUtils.encode(Structure.CHUNK, cX+x, cY+y, plane));
		}
		return chunkIds;
	}

	public static Set<Integer> regionIdToChunkSet(int regionId) {
		Set<Integer> chunkIds = new IntOpenHashSet();
		int[] rCoords = MapUtils.decode(Structure.REGION, regionId);
		int cX = rCoords[0] << 3, cY = rCoords[1] << 3;
		for (int plane = 0;plane < 4;plane++)
			for (int x = 0;x < 8;x++)
				for (int y = 0;y < 8;y++)
					chunkIds.add(MapUtils.encode(Structure.CHUNK, cX+x, cY+y, plane));
		return chunkIds;
	}

	public static final void refreshObject(GameObject object) {
		ChunkManager.getChunk(object.getTile().getChunkId()).addChunkUpdate(new AddObject(object.getTile().getChunkLocalHash(), object));
	}

	public static final GameObject getObject(Tile tile) {
		return ChunkManager.getChunk(tile.getChunkId()).getObject(tile);
	}

	public static final GameObject[] getBaseObjects(Tile tile) {
		return ChunkManager.getChunk(tile.getChunkId()).getBaseObjects(tile);
	}

	public static final GameObject getSpawnedObject(Tile tile) {
		return ChunkManager.getChunk(tile.getChunkId()).getSpawnedObject(tile);
	}

	public static final GameObject getObject(Tile tile, ObjectType type) {
		return ChunkManager.getChunk(tile.getChunkId()).getObject(tile, type);
	}

	public enum DropMethod {
		NORMAL, TURN_UNTRADEABLES_TO_COINS
	}

	public static final GroundItem addGroundItem(Item item, Tile tile) {
		return addGroundItem(item, tile, null, false, -1, DropMethod.NORMAL, -1);
	}

	public static final GroundItem addGroundItem(Item item, Tile tile, Player owner) {
		return addGroundItem(item, tile, owner, true, 60);
	}

	public static final GroundItem addGroundItem(Item item, Tile tile, Player owner, boolean invisible, int hiddenSecs) {
		return addGroundItem(item, tile, owner, invisible, hiddenSecs, DropMethod.NORMAL, 150);
	}

	public static final GroundItem addGroundItem(Item item, Tile tile, Player owner, boolean invisible, int hiddenSecs, DropMethod type) {
		return addGroundItem(item, tile, owner, invisible, hiddenSecs, type, 150);
	}

	@Deprecated
	public static final void addGroundItemForever(Item item, Tile tile) {
		GroundItem groundItem = new GroundItem(item, tile, GroundItemType.FOREVER);
		if (groundItem.getId() == -1)
			return;
		ChunkManager.getChunk(tile.getChunkId()).addGroundItem(groundItem);
	}

	public static final GroundItem addGroundItem(Item item, Tile tile, Player owner, boolean invisible, int hiddenTime, DropMethod type, int deleteTime) {
		if ((item.getId() == -1) || (owner != null && owner.getRights() == Rights.ADMIN))
			return null;
		if (type != DropMethod.NORMAL)
			if (type == DropMethod.TURN_UNTRADEABLES_TO_COINS && !ItemConstants.isTradeable(item)) {
				int price = item.getDefinitions().getValue();
				if (price <= 0)
					return null;
				item.setId(995);
				item.setAmount(price);
			}
		final GroundItem floorItem = new GroundItem(item, tile, owner == null ? null : owner.getUsername(), invisible ? GroundItemType.INVISIBLE : GroundItemType.NORMAL);
		if (floorItem.getAmount() > 1 && !item.getDefinitions().isStackable() && floorItem.getMetaData() == null)
			for (int i = 0; i < floorItem.getAmount(); i++) {
				Item oneItem = new Item(item.getId(), 1);
				GroundItem newItem = new GroundItem(oneItem, tile, owner == null ? null : owner.getUsername(), invisible ? GroundItemType.INVISIBLE : GroundItemType.NORMAL);
				finalizeGroundItem(newItem, tile, owner, hiddenTime, type, deleteTime);
			}
		else
			finalizeGroundItem(floorItem, tile, owner, hiddenTime, type, deleteTime);
		return floorItem;
	}

	private static void finalizeGroundItem(GroundItem item, Tile tile, Player owner, int hiddenSeconds, DropMethod type, int lifeSeconds) {
		if ((item.getId() == -1) || (owner != null && owner.getRights() == Rights.ADMIN))
			return;
		if (ChunkManager.getChunk(tile.getChunkId()).addGroundItem(item)) {
			if (lifeSeconds != -1)
				item.setDeleteTime(Ticks.fromSeconds(lifeSeconds + hiddenSeconds));
			if (item.isInvisible())
				if (hiddenSeconds != -1)
					item.setPrivateTime(Ticks.fromSeconds(hiddenSeconds));
		}
	}

	public static final boolean removeGroundItem(GroundItem groundItem) {
		return ChunkManager.getChunk(groundItem.getTile().getChunkId()).deleteGroundItem(groundItem);
	}

	public static final boolean removeGroundItem(Player player, GroundItem floorItem) {
		return removeGroundItem(player, floorItem, true);
	}

	public static final boolean removeGroundItem(Player player, GroundItem groundItem, boolean add) {
		if (groundItem.getId() == -1)
			return false;
		Chunk chunk = ChunkManager.getChunk(groundItem.getTile().getChunkId());
		if (!chunk.itemExists(groundItem))
			return false;
		if (player.isIronMan() && groundItem.getSourceId() != 0 && groundItem.getSourceId() != player.getUuid()) {
			player.sendMessage("You may not pick up other players items as an ironman.");
			return false;
		}
		if (add && !player.getInventory().hasRoomFor(groundItem)) {
			player.sendMessage("Not enough space in your inventory.");
			return false;
		}
		if (chunk.deleteGroundItem(groundItem)) {
			if (add) {
				if (!player.getInventory().addItem(new Item(groundItem.getId(), groundItem.getAmount(), groundItem.getMetaData())))
					return false;
				if (groundItem.getSourceId() != 0 && groundItem.getSourceId() != player.getUuid())
					WorldDB.getLogs().logPickup(player, groundItem);
			}
			if (groundItem.isRespawn())
				WorldTasks.schedule(Ticks.fromSeconds(15), () -> { //TODO add variable respawn timers to various ground items
					try {
						addGroundItemForever(groundItem, groundItem.getTile());
					} catch (Throwable e) {
						Logger.handle(World.class, "removeGroundItem", e);
					}
				});
			return true;
		}
		return false;
	}

	public static final void sendObjectAnimation(GameObject object, Animation animation) {
		if (object == null)
			return;
		ChunkManager.getChunk(object.getTile().getChunkId()).addObjectAnim(object, animation);
	}

	public static final void sendSpotAnim(Tile tile, SpotAnim anim) {
		ChunkManager.getChunk(tile.getChunkId()).addSpotAnim(tile, anim);
	}

	public static final WorldProjectile sendProjectile(Object from, Object to, int graphicId, int angle, int delay, double speed) {
		return sendProjectile(from, to, graphicId, angle, delay, speed, null);
	}

	public static final WorldProjectile sendProjectile(Object from, Object to, int graphicId, int angle, int delay, double speed, Consumer<WorldProjectile> task) {
		return sendProjectile(from, to, graphicId, 28, 28, delay, speed, angle, 0, task);
	}

	public static final WorldProjectile sendProjectile(Object from, Object to, int graphicId, int angle, double speed) {
		return sendProjectile(from, to, graphicId, angle, speed, null);
	}

	public static final WorldProjectile sendProjectile(Object from, Object to, int graphicId, int angle, double speed, Consumer<WorldProjectile> task) {
		return sendProjectile(from, to, graphicId, 28, 28, 0, speed, angle, 0, task);
	}

	public static final WorldProjectile sendProjectile(Object from, Object to, int graphicId, int startHeight, int endHeight, int startTime, double speed, int angle, int slope) {
		return sendProjectile(from, to, graphicId, startHeight, endHeight, startTime, speed, angle, slope, null);
	}

	public static final WorldProjectile sendProjectile(Object from, Object to, int graphicId, int startHeight, int endHeight, int startTime, double speed, int angle, int slope, Consumer<WorldProjectile> task) {
		Tile fromTile = switch(from) {
			case Tile t -> t;
			case Entity e -> e.getMiddleTile();
			case GameObject g -> g.getTile();
			default -> throw new IllegalArgumentException("Unexpected target type: " + from);
		};
		Tile toTile = switch(to) {
			case Tile t -> t;
			case Entity e -> e.getMiddleTile();
			case GameObject g -> g.getTile();
			default -> throw new IllegalArgumentException("Unexpected target type: " + to);
		};
		if (speed > 20.0)
			speed = speed / 50.0;
		int fromSizeX, fromSizeY;
		if (from instanceof Entity e)
			fromSizeX = fromSizeY = e.getSize();
		else if (from instanceof GameObject go) {
			ObjectDefinitions defs = go.getDefinitions();
			fromSizeX = defs.getSizeX();
			fromSizeY = defs.getSizeY();
		} else
			fromSizeX = fromSizeY = 1;
		int toSizeX, toSizeY;
		if (to instanceof Entity e)
			toSizeX = toSizeY = e.getSize();
		else if (to instanceof GameObject go) {
			ObjectDefinitions defs = go.getDefinitions();
			toSizeX = defs.getSizeX();
			toSizeY = defs.getSizeY();
		} else
			toSizeX = toSizeY = 1;
		slope = fromSizeX * 32;
		WorldProjectile projectile = new WorldProjectile(fromTile, to, graphicId, startHeight, endHeight, startTime, startTime + (speed == -1 ? Utils.getProjectileTimeSoulsplit(fromTile, fromSizeX, fromSizeY, toTile, toSizeX, toSizeY) : Utils.getProjectileTimeNew(fromTile, fromSizeX, fromSizeY, toTile, toSizeX, toSizeY, speed)), slope, angle, task);
		if (graphicId != -1) {
			int chunkId = switch(from) {
				case Tile t -> t.getChunkId();
				case Entity e -> e.getMiddleTile().getChunkId();
				case GameObject g -> g.getTile().getChunkId();
				default -> -1;
			};
			if (chunkId == -1)
				throw new RuntimeException("Invalid source target. Accepts Tiles and Entities.");
			ChunkManager.getChunk(chunkId).addProjectile(projectile);
		}
		return projectile;
	}

	public static final boolean isMultiArea(Tile tile) {
		int chunkId = MapUtils.encode(Structure.CHUNK, tile.getChunkX(), tile.getChunkY());
		return Areas.withinArea("multi", chunkId);
	}

	public static boolean isPvpArea(Player player) {
		return WildernessController.isAtWild(player.getTile());
	}

	public static Sound playSound(Tile tile, Sound sound) {
		ChunkManager.getChunk(tile.getChunkId()).addSound(tile, sound);
		return sound;
	}

	private static Sound playSound(Tile source, int soundId, int delay, SoundType type) {
		return playSound(source, new Sound(soundId, delay, type));
	}

	public static void jingle(Tile source, int jingleId, int delay) {
		playSound(source, jingleId, delay, SoundType.JINGLE);
	}

	public static void jingle(Tile source, int jingleId) {
		playSound(source, jingleId, 0, SoundType.JINGLE);
	}

	public static void musicTrack(Tile source, int trackId, int delay, int volume) {
		playSound(source, trackId, delay, SoundType.MUSIC).volume(volume);
	}

	public static void musicTrack(Tile source, int trackId, int delay) {
		playSound(source, trackId, delay, SoundType.MUSIC);
	}

	public static void musicTrack(Tile source, int trackId) {
		musicTrack(source, trackId, 100);
	}

	public static void soundEffect(Tile source, int soundId, int delay) {
		playSound(source, soundId, delay, SoundType.EFFECT);
	}

	public static void soundEffect(Tile source, int soundId) {
		soundEffect(source, soundId, 0);
	}

	public static void voiceEffect(Tile source, int voiceId, int delay) {
		playSound(source, voiceId, delay, SoundType.VOICE);
	}

	public static void voiceEffect(Tile source, int voiceId) {
		voiceEffect(source, voiceId, 0);
	}

	public static GameObject getClosestObject(int objectId, Tile tile) {
		for (int dist = 0;dist < 16;dist++)
			for (int x = -dist;x < dist;x++)
				for (int y = -dist; y < dist;y++) {
					GameObject object = World.getObject(tile.transform(x, y));
					if (object != null && object.getId() == objectId)
						return object;
				}
		return null;
	}

	public static GameObject getClosestObject(ObjectType type, Tile tile) {
		for (int dist = 0;dist < 16;dist++)
			for (int x = -dist;x < dist;x++)
				for (int y = -dist; y < dist;y++) {
					GameObject object = World.getObject(tile.transform(x, y), type);
					if (object != null && object.getType() == type)
						return object;
				}
		return null;
	}

	public static GameObject getClosestObject(ObjectType type, int objectId, Tile tile) {
		for (int dist = 0;dist < 16;dist++)
			for (int x = -dist;x < dist;x++)
				for (int y = -dist; y < dist;y++) {
					GameObject object = World.getObject(tile.transform(x, y), type);
					if (object != null && object.getId() == objectId)
						return object;
				}
		return null;
	}

	public static GameObject getClosestObject(String name, Tile tile) {
		for (int dist = 0;dist < 16;dist++)
			for (int x = -dist;x < dist;x++)
				for (int y = -dist; y < dist;y++) {
					GameObject object = World.getObject(tile.transform(x, y));
					if (object != null && object.getDefinitions().getName().equals(name))
						return object;
				}
		return null;
	}

	public static GameObject getClosestObject(String name, Tile tile, int range) {
		GameObject closest = null;
		double closestDist = 1000;
		for (int dist = 0;dist < range;dist++)
			for (int x = -dist;x < dist;x++)
				for (int y = -dist; y < dist;y++) {
					GameObject object = World.getObject(tile.transform(x, y));
					if (object != null && object.getDefinitions().getName().equals(name)) {
						double newDist = Utils.getDistance(object.getCoordFace(), tile);
						if (newDist < closestDist) {
							closest = object;
							closestDist = newDist;
						}
					}
				}
		return closest;
	}

	public static final GameObject getObjectWithType(Tile tile, ObjectType type) {
		return ChunkManager.getChunk(tile.getChunkId()).getObjectWithType(tile, type);
	}

	public static final GameObject getObjectWithSlot(Tile tile, int slot) {
		return ChunkManager.getChunk(tile.getChunkId()).getObjectWithSlot(tile, slot);
	}

	public static final boolean containsObjectWithId(Tile tile, int id) {
		return ChunkManager.getChunk(tile.getChunkId()).containsObjectWithId(tile, id);
	}

	public static final GameObject getObjectWithId(Tile tile, int id) {
		return ChunkManager.getChunk(tile.getChunkId()).getObjectWithId(tile, id);
	}

	public static void sendWorldMessage(String message, boolean forStaff) {
		for (Player p : World.getPlayers()) {
			if (p == null || !p.isRunning() || p.isYellOff() || (forStaff && !p.hasRights(Rights.MOD)))
				continue;
			p.sendMessage(message);
		}
	}

	/**
	 * Please someone refactor this. This is beyond disgusting and definitely can be done better.
	 */
	public static Tile findAdjacentFreeTile(Tile tile) {
		List<Direction> unchecked = new ArrayList<>(Arrays.asList(Direction.values()));
		while(!unchecked.isEmpty()) {
			Direction curr = unchecked.get(Utils.random(unchecked.size()));
			if (World.checkWalkStep(tile, curr, 1))
				return tile.transform(curr.getDx(), curr.getDy());
			unchecked.remove(curr);
		}
		return null;
	}

	/**
	 * Please someone refactor this. This is beyond disgusting and definitely can be done better.
	 */
	public static Tile findAdjacentFreeSpace(Tile tile, int size) {
		if (size == 1)
			return findAdjacentFreeTile(tile);
		List<Direction> unchecked = new ArrayList<>(List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST));
		Tile finalTile = null;
		while(!unchecked.isEmpty()) {
			boolean failed = false;
			Direction curr = unchecked.get(Utils.random(unchecked.size()));
			Direction offset = Direction.forDelta(curr.getDx() != 0 ? 0 : 1 * curr.getDy(), curr.getDy() != 0 ? 0 : 1 * curr.getDx());
			Tile startTile = tile.transform(0, 0);
			for (int i = 0;i <= size;i++) {
				for (int row = 0; row < size; row++) {
					Tile from = startTile.transform(offset.getDx() * row, offset.getDy() * row).transform(curr.getDx() * i, curr.getDy() * i);
//					if (Settings.getConfig().isDebug()) {
//						World.sendSpotAnim(null, new SpotAnim(switch (curr) {
//							case NORTH -> 2000;
//							case SOUTH -> 2001;
//							case EAST -> 2017;
//							default -> 1999;
//						}), from);
//					}
					if (!checkWalkStep(from, curr, 1) || (size > 1 && row < (size-1) && !checkWalkStep(from, offset, 1))) {
						failed = true;
						break;
					}
				}
			}
			if (!failed) {
				finalTile = startTile.transform(curr.getDx(), curr.getDy());
				if (curr.getDx() < 0 || curr.getDy() < 0)
					finalTile = finalTile.transform(-size+1, -size+1);
//				if (Settings.getConfig().isDebug())
//					World.sendSpotAnim(null, new SpotAnim(2679), finalTile);
				break;
			}
			unchecked.remove(curr);
		}
		return finalTile;
	}

	public static Tile findClosestAdjacentFreeTile(Tile tile, int dist) {
		//Checks outward - Northeast
		for (int x = 0; x <= dist; x++)
			for (int y = 0; y <= dist; y++)
				if (World.floorFree(tile.getPlane(), tile.getX() + x, tile.getY() + y))
					return tile.transform(x, y, 0);

		//Checks outward - Southeast
		for (int x = 0; x <= dist; x++)
			for (int y = 0; y >= -dist; y--)
				if (World.floorFree(tile.getPlane(), tile.getX() + x, tile.getY() + y))
					return tile.transform(x, y, 0);

		//Checks outward - Southwest
		for (int x = 0; x >= -dist; x--)
			for (int y = 0; y >= -dist; y--)
				if (World.floorFree(tile.getPlane(), tile.getX() + x, tile.getY() + y))
					return tile.transform(x, y, 0);

		//Checks outward - Northwest
		for (int x = 0; x >= -dist; x--)
			for (int y = 0; y <= dist; y++)
				if (World.floorFree(tile.getPlane(), tile.getX() + x, tile.getY() + y))
					return tile.transform(x, y, 0);

		return tile.transform(0, 0, 0);
	}

	public static long getServerTicks() {
		return WorldThread.WORLD_CYCLE;
	}

	public static List<GameObject> getSurroundingBaseObjects(GameObject obj, int radius) {
		ArrayList<GameObject> objects = new ArrayList<>();
		for (GameObject object : ChunkManager.getChunk(obj.getTile().getChunkId()).getBaseObjects()) {
			if (object == null || object.getDefinitions() == null)
				continue;
			if (Utils.getDistance(obj.getTile(), object.getTile()) <= radius)
				objects.add(object);
		}
		return objects;
	}

	public static void broadcastLoot(String message) {
		sendWorldMessage("<img=4><shad=000000><col=00FF00>" + message, false);
	}

	public static void processEntityLists() {
		PLAYERS.processPostTick();
		NPCS.processPostTick();
	}
}
