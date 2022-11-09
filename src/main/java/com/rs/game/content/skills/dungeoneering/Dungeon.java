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
package com.rs.game.content.skills.dungeoneering;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.lang.SuppressWarnings;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.skills.dungeoneering.DungeonConstants.KeyDoors;
import com.rs.game.content.skills.dungeoneering.DungeonConstants.SkillDoors;
import com.rs.game.content.skills.dungeoneering.rooms.BossRoom;
import com.rs.lib.Constants;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;

@SuppressWarnings("unused")
public final class Dungeon {

	private int type;
	private int complexity;
	private int size;
	private long seed;
	private Room[][] map;
	private int creationCount;
	private int critCount;
	private DungeonManager manager;
	private RoomReference startRoom;

	private static Dungeon test;

	public static void main(String[] args) throws IOException {
		Settings.loadConfig();
		Cache.init(Settings.getConfig().getCachePath());

		JFrame frame = new JFrame() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				if (test != null)
					test.draw((Graphics2D) g);
			}
		};

		frame.pack();
		frame.setSize(600, 800);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		long lastDung = System.currentTimeMillis();
		test = new Dungeon(null, 1, 6, DungeonConstants.LARGE_DUNGEON);
		Logger.debug(Dungeon.class, "main", "Generated dungeon in " + (System.currentTimeMillis() - lastDung) + "ms...");
		frame.repaint();
	}

	public void draw(Graphics2D g) {
		g.scale(3, 3);
		g.setColor(Color.black);
		g.fillRect(0, 0, 800, 500);

		g.setFont(new Font("Arial", Font.BOLD, 7));

		g.setColor(Color.ORANGE);
		g.drawString("Orange - Start room", 23, 210);

		g.setColor(Color.YELLOW);
		g.drawString("Yellow - Non-critical room", 23, 220);

		g.setColor(Color.RED);
		g.drawString("Red - Critical path", 23, 230);

		g.setColor(Color.MAGENTA);
		g.drawString("Magenta - Boss Room", 23, 240);

		g.setColor(Color.GREEN);
		g.drawString("Green - Connections", 110, 210);

		g.setColor(Color.GRAY);
		g.drawString("Gray - Key Door", 110, 220);

		g.setColor(Color.WHITE);
		g.drawString("White - Key Item", 110, 230);

		for (int y = 0; y < map[0].length; y++)
			for (int x = 0; x < map.length; x++)
				if (map[x][y] != null) {
					g.setColor(Color.YELLOW);

					if (map[x][y].isCritPath())
						g.setColor(Color.RED);

					g.fillRect(x * 20 + 24, (7 - y) * 20 + 44, 12, 12);

					if (x == test.getStartRoomReference().getRoomX() && y == test.getStartRoomReference().getRoomY()) {
						g.setColor(Color.ORANGE);
						g.fillRect(x * 20 + 27, (7 - y) * 20 + 47, 6, 6);

					}

					if (map[x][y].getRoom() instanceof BossRoom) {
						g.setColor(Color.MAGENTA);
						g.fillRect(x * 20 + 27, (7 - y) * 20 + 47, 6, 6);
					}

					g.setColor(Color.green);
					if (map[x][y].hasNorthDoor())
						g.drawLine(x * 20 + 30, (7 - y) * 20 + 50, x * 20 + 30, ((7 - y) - 1) * 20 + 50);
					if (map[x][y].hasEastDoor())
						g.drawLine(x * 20 + 30, (7 - y) * 20 + 50, (x + 1) * 20 + 30, ((7 - y)) * 20 + 50);
					if (map[x][y].hasSouthDoor())
						g.drawLine(x * 20 + 30, (7 - y) * 20 + 50, x * 20 + 30, ((7 - y) + 1) * 20 + 50);
					if (map[x][y].hasWestDoor())
						g.drawLine(x * 20 + 30, (7 - y) * 20 + 50, (x - 1) * 20 + 30, ((7 - y)) * 20 + 50);
				}

		for (int y = 0; y < map[0].length; y++)
			for (int x = 0; x < map.length; x++)
				if (map[x][y] != null) {
					int key = map[x][y].getDropId();
					if (key != -1) {
						g.setFont(new Font("Arial", Font.BOLD, 5));
						g.setColor(Color.white);
						g.drawString(formatName(ItemDefinitions.getDefs(key).name), x * 20 + 36, (7 - y) * 20 + 48);
					}

					g.setFont(new Font("Arial", Font.BOLD, 7));
					g.setColor(Color.green);

					for (int l = 0; l < map[x][y].getRoom().getDoorDirections().length; l++) {
						int lock0 = 0;// (map[x][y].getDoorTypes()[l] >> 16 & 0xFFFF) - 50208;
						Door door = map[x][y].getDoor(l);
						lock0 = door != null && door.getType() == DungeonConstants.KEY_DOOR ? door.getId() : -1;
						int rotation = (map[x][y].getRoom().getDoorDirections()[l] + map[x][y].getRotation()) & 0x3;
						if (lock0 >= 0 && lock0 < 64)
							if (rotation == DungeonConstants.NORTH_DOOR) {
								g.setFont(new Font("Arial", Font.BOLD, 5));
								g.setColor(Color.LIGHT_GRAY);
								g.drawString(formatName(ItemDefinitions.getDefs(KeyDoors.values()[lock0].getKeyId()).name), x * 20 + 27, (7 - y) * 20 + 48);
							} else if (rotation == DungeonConstants.EAST_DOOR) {
								g.setFont(new Font("Arial", Font.BOLD, 5));
								g.setColor(Color.LIGHT_GRAY);
								g.drawString(formatName(ItemDefinitions.getDefs(KeyDoors.values()[lock0].getKeyId()).name), x * 20 + 31, (7 - y) * 20 + 52);
							} else if (rotation == DungeonConstants.SOUTH_DOOR) {
								g.setFont(new Font("Arial", Font.BOLD, 5));
								g.setColor(Color.LIGHT_GRAY);
								g.drawString(formatName(ItemDefinitions.getDefs(KeyDoors.values()[lock0].getKeyId()).name), x * 20 + 27, (7 - y) * 20 + 55);
							} else if (rotation == DungeonConstants.WEST_DOOR) {
								g.setFont(new Font("Arial", Font.BOLD, 5));
								g.setColor(Color.LIGHT_GRAY);
								g.drawString(formatName(ItemDefinitions.getDefs(KeyDoors.values()[lock0].getKeyId()).name), x * 20 + 24, (7 - y) * 20 + 52);
							}

					}
				}
	}

	public static String formatName(String key) {
		char[] arr = key.toUpperCase().toCharArray();
		String name = "" + arr[0];
		for (int i = 0; i < arr.length - 1; i++)
			if (arr[i] == ' ' && arr[i + 1] != 'K')
				name += arr[i + 1];
		return name;
	}

	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}

	public Dungeon(DungeonManager manager, int floorId, int complexity, int size) {
		this.manager = manager;
		type = DungeonUtils.getFloorType(floorId);
		this.complexity = complexity;
		this.size = size;

		seed = System.nanoTime();
		if(manager.getParty().customSeed) {
			seed = manager.getParty().getStartingSeed();
			manager.getParty().customSeed = false;
		}

		// seed = 3022668148508890112L;
		Random random = new Random(seed);
		DungeonStructure structure = new DungeonStructure(size, random, complexity);
		// map structure to matrix dungeon
		map = new Room[DungeonConstants.DUNGEON_RATIO[size][0]][DungeonConstants.DUNGEON_RATIO[size][1]];
		RoomNode base = structure.getBase();

		Room[] possibilities;
		startRoom = new RoomReference(base.x, base.y);
		List<RoomNode> children = base.getChildrenR();
		children.add(base);
		long eligiblePuzzleRooms = children.stream().filter(r -> r.children.size() > 0 && r.children.stream().allMatch(c -> c.lock == -1)).count();
		double puzzleChance = complexity < 6 ? 0 : 0.1 * children.size() / eligiblePuzzleRooms;
		for (RoomNode node : children) {
			creationCount++;
			boolean puzzle = false;
			if (node == base)
				possibilities = DungeonUtils.selectPossibleRooms(DungeonConstants.START_ROOMS, complexity, type, base.north(), base.east(), base.south(), base.west());
			else if (node.isBoss)
				possibilities = DungeonUtils.selectPossibleBossRooms(type, complexity, floorId, node.north(), node.east(), node.south(), node.west(), node.rotation());
			else if (node.children.size() > 0 && node.children.stream().allMatch(c -> c.lock == -1) && puzzleChance > random.nextDouble()) {
				puzzle = true;
				possibilities = DungeonUtils.selectPossibleRooms(DungeonConstants.PUZZLE_ROOMS, complexity, type, node.north(), node.east(), node.south(), node.west(), node.rotation());
			} else
				possibilities = DungeonUtils.selectPossibleRooms(DungeonConstants.NORMAL_ROOMS, complexity, type, node.north(), node.east(), node.south(), node.west());
			map[node.x][node.y] = possibilities[random.nextInt(possibilities.length)];
			if (node.isCritPath) {
				critCount++;
				map[node.x][node.y].setCritPath(true);
			}
			if (node.key != -1)
				map[node.x][node.y].setDropId(KeyDoors.values()[node.key].getKeyId());
			for (int doorDir = 0; doorDir < map[node.x][node.y].getRoom().getDoorDirections().length; doorDir++) {
				int rotation = (map[node.x][node.y].getRoom().getDoorDirections()[doorDir] + map[node.x][node.y].getRotation()) & 0x3;
				RoomNode neighbor = structure.getRoom(node.x + Utils.ROTATION_DIR_X[rotation], node.y + Utils.ROTATION_DIR_Y[rotation]);
				if (neighbor.parent == node)
					if (puzzle)
						map[node.x][node.y].setDoor(doorDir, new Door(DungeonConstants.CHALLENGE_DOOR));
					else if (neighbor.lock != -1)
						map[node.x][node.y].setDoor(doorDir, new Door(DungeonConstants.KEY_DOOR, neighbor.lock));
					else if (complexity >= 5 && random.nextInt(3) == 0) {
						int doorIndex = random.nextInt(DungeonConstants.SkillDoors.values().length);
						SkillDoors sd = DungeonConstants.SkillDoors.values()[doorIndex];
						if (sd.getClosedObject(type) == -1) // some frozen skill doors dont exist
							continue;
						int level = manager == null ? 1 : neighbor.isCritPath ? (manager.getParty().getMaxLevel(sd.getSkillId()) - random.nextInt(10)) : random.nextInt(sd.getSkillId() == Constants.SUMMONING || sd.getSkillId() == Constants.PRAYER ? 100 : 106);
						map[node.x][node.y].setDoor(doorDir, new Door(DungeonConstants.SKILL_DOOR, doorIndex, level < 1 ? 1 : level));
					} else if (complexity >= 3 && random.nextInt(2) == 0)
						map[node.x][node.y].setDoor(doorDir, new Door(DungeonConstants.GUARDIAN_DOOR));
			}
		}

	}

	public long getSeed() {
		return seed;
	}

	public int getRoomsCount() {
		return creationCount;
	}

	public int getCritCount() {
		return critCount;
	}

	public Room getRoom(RoomReference reference) {
		if (reference.getRoomX() < 0 || reference.getRoomY() < 0 || map.length <= reference.getRoomX() || map[reference.getRoomX()].length <= reference.getRoomY())
			return null;
		return map[reference.getRoomX()][reference.getRoomY()];
	}

	public int getMapWidth() {
		return map.length;
	}

	public int getMapHeight() {
		return map[0].length;
	}

	public RoomReference getStartRoomReference() {
		return startRoom;
	}

	public int getType() {
		return type;
	}

	public int getComplexity() {
		return complexity;
	}

	public int getSize() {
		return size;
	}

	public Room[][] getMap() {
		return map;
	}
}
