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
package com.rs.game.content.minigames.creations;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.map.instance.Instance;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author mgi125, the almighty
 */
public class GameArea {

	public static int[] NONE = { -1, -1 };
	public static int[] BASE = { 240, 712 };
	public static int[] EMPTY = { 241, 715 };
	public static int[] RESERVED_1 = { 240, 713 };
	public static int[] RESERVED_2 = { 241, 712 };
	public static int[] RESERVED_3 = { 241, 713 };
	public static int[] KILN = { 240, 714 };
	public static int[] ALTAR = { 241, 714 };
	public static int[] FOG = { 240, 715 };
	public static int[] RIFT = { 240, 716 };
	public static int[] WALL = { 241, 716 };
	public static int[] ROCK = { 242, 716 };

	public static int[] SKILL_ROCK = { 247, 715 };
	public static int[] SKILL_POOL = { 247, 714 };
	public static int[] SKILL_SWARM = { 247, 713 };
	public static int[] SKILL_TREE = { 247, 712 };

	/**
	 * Contains area flags. 0-3 bits - type 4-7 bits - tier (if any) 8-9 bits -
	 * rotation 10-18 bits - degradation 19-20 bits - wall team 21-23 bits - wall
	 * tier 24-29 bits - wall status Types: 0 - reserved, don't use 1 - base 2 -
	 * empty 3 - rift 4 - wall 5 - fog 6 - large rock 7 - altar 8 - kiln 9 - rock 10
	 * - tree 11 - pool 12 - swarm
	 */
	private int[][] flags;
	/**
	 * Contains base positions.
	 */
	private Instance region;

	public GameArea(int size) {
		flags = new int[size][size];
	}

	/**
	 * Calculate's new random area.
	 */
	public void calculate() {
		for (int[] flag : flags)
			Arrays.fill(flag, 2); // fill with empty area

		set(0, 0, 1, 0, 0); // blue base
		set(0, 1, 0, 0, 0); // reserved space for blue base
		set(1, 0, 0, 1, 0); // reserved space for blue base
		set(1, 1, 0, 2, 0); // reserved space for blue base

		set(flags.length - 1, flags.length - 1, 1, 0, 2); // red base
		set(flags.length - 1, flags.length - 2, 0, 0, 2); // reserved space for
		// red base
		set(flags.length - 2, flags.length - 1, 0, 1, 2); // reserved space for
		// red base
		set(flags.length - 2, flags.length - 2, 0, 2, 2); // reserved space for
		// red base

		setWallTeam(0, 0, 1); // flag blue base to blue team
		setWallTeam(flags.length - 1, flags.length - 1, 2); // flag red base to
		// red team

		int total = flags.length * flags.length;
		int skillPlots = (int) (total * 0.3F);
		int obstacles = (int) (total * 0.2F);

		while (skillPlots-- > 0)
			setRandom(100, 0, 0, flags.length, flags.length, Utils.random(4) + 9, skillPlots == 0 ? 5 : Utils.random(5), Utils.random(4), 60);

		while (obstacles-- > 0) {
			int type = Utils.random(5) + 3;
			int rotation = type == 5 ? 3 : Utils.random(4);
			if (!setRandom(100, 0, 0, flags.length, flags.length, type, 0, rotation))
				Logger.debug(GameArea.class, "calculate", "Failed");
		}

		// ensure that at least one kiln per team is created.
		int kilnsBlue = Utils.random(2) + 1;
		int kilnsRed = Utils.random(2) + 1;

		while (kilnsBlue-- > 0)
			setRandom(100, 0, 0, flags.length / 2, flags.length / 2, 8, 0, Utils.random(4));

		while (kilnsRed-- > 0)
			setRandom(100, flags.length / 2, flags.length / 2, flags.length, flags.length, 8, 0, Utils.random(4));
	}

	/**
	 * Create's dynamic maps using the info from the calculate() method.
	 */
	public void create(Runnable callback) {
		if (region != null)
			throw new RuntimeException("Area already created.");
		region = Instance.of(Helper.EXIT, flags.length, flags.length);
		region.requestChunkBound().thenAccept(e -> {
			List<CompletableFuture<Boolean>> futures = new ObjectArrayList<>();
			for (int x = 0; x < flags.length; x++) {
				for (int y = 0; y < flags.length; y++) {
					int type = getType(x, y);
					int rot = getRotation(x, y);
					int tier = getTier(x, y);
					int[] copy;
					switch (type) {
						case 0: // base pad space
							if (tier == 0)
								copy = RESERVED_1;
							else if (tier == 1)
								copy = RESERVED_2;
							else if (tier == 2)
								copy = RESERVED_3;
							else
								copy = EMPTY;
							break;
						case 1:
							copy = BASE;
							break;
						case 2:
							copy = EMPTY;
							break;
						case 3:
							copy = RIFT;
							break;
						case 4:
							copy = WALL;
							break;
						case 5:
							copy = FOG;
							break;
						case 6:
							copy = ROCK;
							break;
						case 7:
							copy = ALTAR;
							break;
						case 8:
							copy = KILN;
							break;
						case 9:
							copy = SKILL_ROCK;
							break;
						case 10:
							copy = SKILL_TREE;
							break;
						case 11:
							copy = SKILL_POOL;
							break;
						case 12:
							copy = SKILL_SWARM;
							break;
						default:
							copy = EMPTY;
							break;
					}

					if (type >= 9 && type <= 12 && tier > 0) {
						int[] r = new int[2];
						r[0] = copy[0] - tier;
						r[1] = copy[1];
						copy = r;
					} else if (type >= 9 && type <= 12 && tier <= 0)
						copy = EMPTY;
					final int fx = x, fy = y, fcopyx = copy[0], fcopyy = copy[1];
					futures.add(region.copyChunk(fx, fy, 0, fcopyx, fcopyy, 0, rot));
				}
			}
			futures.forEach(CompletableFuture::join);
			World.spawnObject(new GameObject(Helper.BLUE_DOOR_1, ObjectType.WALL_STRAIGHT, 1, getMinX() + Helper.BLUE_DOOR_P1[0], getMinY() + Helper.BLUE_DOOR_P1[1], 0));
			World.spawnObject(new GameObject(Helper.BLUE_DOOR_2, ObjectType.WALL_STRAIGHT, 1, getMinX() + Helper.BLUE_DOOR_P2[0], getMinY() + Helper.BLUE_DOOR_P2[1], 0));
			World.spawnObject(new GameObject(Helper.BLUE_DOOR_1, ObjectType.WALL_STRAIGHT, 2, getMinX() + Helper.BLUE_DOOR_P3[0], getMinY() + Helper.BLUE_DOOR_P3[1], 0));
			World.spawnObject(new GameObject(Helper.BLUE_DOOR_2, ObjectType.WALL_STRAIGHT, 2, getMinX() + Helper.BLUE_DOOR_P4[0], getMinY() + Helper.BLUE_DOOR_P4[1], 0));

			World.spawnObject(new GameObject(Helper.RED_DOOR_1, ObjectType.WALL_STRAIGHT, 3, getMinX() + ((flags.length - 1) * 8) + Helper.RED_DOOR_P1[0], getMinY() + ((flags.length - 1) * 8) + Helper.RED_DOOR_P1[1], 0));
			World.spawnObject(new GameObject(Helper.RED_DOOR_2, ObjectType.WALL_STRAIGHT, 3, getMinX() + ((flags.length - 1) * 8) + Helper.RED_DOOR_P2[0], getMinY() + ((flags.length - 1) * 8) + Helper.RED_DOOR_P2[1], 0));
			World.spawnObject(new GameObject(Helper.RED_DOOR_1, ObjectType.WALL_STRAIGHT, 0, getMinX() + ((flags.length - 1) * 8) + Helper.RED_DOOR_P3[0], getMinY() + ((flags.length - 1) * 8) + Helper.RED_DOOR_P3[1], 0));
			World.spawnObject(new GameObject(Helper.RED_DOOR_2, ObjectType.WALL_STRAIGHT, 0, getMinX() + ((flags.length - 1) * 8) + Helper.RED_DOOR_P4[0], getMinY() + ((flags.length - 1) * 8) + Helper.RED_DOOR_P4[1], 0));

			int managerBlue = Helper.MANAGER_NPCS[Utils.random(Helper.MANAGER_NPCS.length)];
			int managerRed = Helper.MANAGER_NPCS[Utils.random(Helper.MANAGER_NPCS.length)];

			World.spawnNPC(managerBlue, Tile.of(getMinX() + Helper.BLUE_MANAGER_P[0], getMinY() + Helper.BLUE_MANAGER_P[1], 0), -1, false, true).setRandomWalk(false);
			World.spawnNPC(managerRed, Tile.of(getMinX() + ((flags.length - 1) * 8) + Helper.RED_MANAGER_P[0], getMinY() + ((flags.length - 1) * 8) + Helper.RED_MANAGER_P[1], 0), -1, false, true).setRandomWalk(false);
			callback.run();
		});
	}

	/**
	 * Destroy's dynamic maps that were created using create() method.
	 */
	public void destroy() {
		if (region == null)
			throw new RuntimeException("Area already destroyed.");
		region.destroy();
		region = null;

	}

	private boolean setRandom(int attempts, int minX, int minY, int maxX, int maxY, int type, int tier, int rotation) {
		return setRandom(attempts, minX, minY, maxX, maxY, type, tier, rotation, 0);
	}

	private boolean setRandom(int attempts, int minX, int minY, int maxX, int maxY, int type, int tier, int rotation, int degradation) {
		while (attempts-- > 1) {
			int x = minX + Utils.random(maxX - minX);
			int y = minY + Utils.random(maxY - minY);
			if (getType(x, y) == 2) {
				set(x, y, type, tier, rotation);
				setDegradation(x, y, degradation);
				return true;
			}
		}
		for (int x = minX; x < maxX; x++)
			for (int y = minY; y < maxY; y++)
				if (getType(x, y) == 2) {
					set(x, y, type, tier, rotation);
					setDegradation(x, y, degradation);
					return true;
				}
		return false;
	}

	public void set(int x, int y, int type, int tier, int rotation) {
		flags[x][y] = (type | (tier << 4) | (rotation << 8));
	}

	public void setDegradation(int x, int y, int deg) {
		flags[x][y] &= ~(0x1FF << 10);
		flags[x][y] |= deg << 10;
	}

	public void setWallTeam(int x, int y, int team) {
		flags[x][y] &= ~(0x3 << 19);
		flags[x][y] |= team << 19;
	}

	public void setWallTier(int x, int y, int tier) {
		flags[x][y] &= ~(0x7 << 21);
		flags[x][y] |= tier << 21;
	}

	public void setWallStatus(int x, int y, int status) {
		flags[x][y] &= ~(0x3F << 24);
		flags[x][y] |= status << 24;
	}

	public int getType(int x, int y) {
		return flags[x][y] & 0xF;
	}

	public int getTier(int x, int y) {
		return (flags[x][y]) >> 4 & 0xF;
	}

	public int getRotation(int x, int y) {
		return (flags[x][y] >> 8) & 0x3;
	}

	public int getDegradation(int x, int y) {
		return (flags[x][y] >> 10) & 0x1FF;
	}

	public int getWallTeam(int x, int y) {
		return (flags[x][y] >> 19) & 0x3;
	}

	public int getWallTier(int x, int y) {
		return (flags[x][y] >> 21) & 0x7;
	}

	public int getWallStatus(int x, int y) {
		return (flags[x][y] >> 24) & 0x3F;
	}

	public int getSize() {
		return flags.length;
	}

	public int getMinX() {
		return region.getBaseX();
	}

	public int getMinY() {
		return region.getBaseY();
	}

	public int getMaxX() {
		return region.getBaseX() + (flags.length << 3);
	}

	public int getMaxY() {
		return region.getBaseY() + (flags.length << 3);
	}

	public int[][] getFlags() {
		return flags;
	}
}