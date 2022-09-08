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
package com.rs.game.content.world.doors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Doors {

	public static ObjectClickHandler handleDoors = new ObjectClickHandler(new Object[] { 1531, "Door", "Throne Room Door", "Magic door", "Doorway", "Sturdy door", "Bamboo Door", "Long hall door", "Castle door", "Heavy door", "Gate", "Large door", "Metal door", "City gate", "Red door", "Orange door", "Yellow door", "Cell door", "Cell Door", "Wall", "Storeroom Door", "Solid bronze door", "Solid steel door", "Solid black door", "Solid silver door" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			handleDoor(e.getPlayer(), e.getObject());
		}
	};

	public static ObjectClickHandler handleLeftHandedDoors = new ObjectClickHandler(new Object[] { 22921 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			handleLeftHandedDoor(e.getPlayer(), e.getObject());
		}
	};

	public static ObjectClickHandler handleInvertedDoors = new ObjectClickHandler(new Object[] { 1531 }, ObjectType.WALL_INTERACT) {
		@Override
		public void handle(ObjectClickEvent e) {
			handleDoor(e.getPlayer(), e.getObject(), 1);
		}
	};

	public static ObjectClickHandler handleDoubleDoors = new ObjectClickHandler(new Object[] { 704, 707, 708, 709, 1516, 1517, 1519, 1520, 1542, 1543, 1544, 1545, 1560, 1561, 1596, 1597, 2039, 2041, 2058, 2060, 2115, 2116, 2199, 2200, 2255, 2256, 2259, 2260, 2546, 2547, 2548, 2549, 2552, 2553, 2896, 2897, 2391, 2392, 2912, 2913, 2922, 2923, 3020, 3021, 3022, 3023, 2673, 2674, 2786, 2787, 2788, 2789, 3506, 3507, 4423, 4424, 4425, 4426, 4427, 4428, 4429, 4430, 4487, 4491, 4490, 4492, 4629, 4630, 4631, 4632, 4633, 4634, 4963, 4964, 5183, 5186, 5187, 5188,
			2416, 2417, 26207, 5667, 6238, 6240, 6451, 6452, 6871, 6872, 10262, 10263, 10264, 10265, 10423, 10425, 10427, 10429, 10527, 10528, 10529, 10530, 11620, 11621, 11624, 11625, 11716, 11717, 11718, 11719, 11720, 11721, 11722, 11723, 12045, 12047, 12172, 14443, 14444, 14445, 12349, 12350, 12446, 12447, 12448, 12449, 12467, 12468, 13094, 13095, 13096, 13097, 14233, 14234, 14235, 14236, 15604, 15605, 15641, 15644, 15658, 15660, 17091, 17092, 17093, 17094, 18698, 18699, 18700, 18701, 18971, 18973, 20195, 20196, 20197, 20198, 20391, 21403, 21404, 21405, 21406, 21505, 21506, 21507, 21508, 22435, 22437, 24369, 24370,
			24373, 24374, 25638, 25639, 25640, 25641, 25788, 25789, 25790, 25791, 25813, 25814, 25815, 25816, 25825, 25826, 25827, 25828, 26081, 26082, 26083, 26084, 26114, 26115, 27851, 27852, 27853, 27854, 28690, 28691, 28692, 28693, 29315, 29316, 29317, 29318, 30707, 30708, 31814, 31815, 31816, 31817, 31820, 31821, 31822, 31823, 31824, 31825, 31826, 31827, 31829, 31830, 31831, 31832, 31833, 31834, 31841, 31844, 34819, 34820, 34822, 34823, 34825, 34826, 34827, 34828, 36315, 36316, 36317, 36318, 36999, 37002, 39975, 39976, 39978, 39979, 41131, 41132, 41133, 41134, 41174, 41175, 41178, 41179, 45964, 45965,
			45966, 45967, 47240, 47241, 48938, 48939, 48940, 48941, 48942, 48943, 48944, 48945, 49014, 49016, 52176, 52183, 52381, 52313, 52382, 52315, 53671, 53672, 53674, 53675, 59958, 59961, 61051, 61052, 61053, 61054, 64835, 64837, 66599, 66601, 66938, 66940, 66941, 66942 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			handleDoubleDoor(e.getPlayer(), e.getObject());
		}
	};

	public static ObjectClickHandler handleInvertedDoubles = new ObjectClickHandler(new Object[] { 37000, 37003 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			handleDoubleDoor(e.getPlayer(), e.getObject(), true);
		}
	};

	public static ObjectClickHandler handleInvertedDoublesYanille = new ObjectClickHandler(new Object[] { 1517, 1520 }, new WorldTile(2561, 3099, 0), new WorldTile(2561, 3098, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			handleDoubleDoor(e.getPlayer(), e.getObject(), true);
		}
	};

	public static ObjectClickHandler handleGates = new ObjectClickHandler(new Object[] { 166, 167, 1551, 1552, 1553, 1556, 2050, 2051, 2306, 2313, 2320, 2344, 2261, 2262, 2438, 2439, 3015, 3016, 3725, 3726, 4311, 4312, 7049, 7050, 7051, 7052, 15510, 15511, 15512, 15513, 15514, 15515, 15516, 15517, 24560, 24561, 34777, 34778, 34779, 34780, 36912, 36913, 36914, 36915, 37351, 37352, 37353, 37354, 45206, 45207, 45208, 45209, 45210, 45211, 45212, 45213 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			handleGate(e.getPlayer(), e.getObject());
		}
	};

	public static ObjectClickHandler handleInPlaceSingleDoors = new ObjectClickHandler(new Object[] { 4250, 4251, 5887, 5888, 5889, 5890, 5891, 5893, 64833, 64834, 65365, 65367, 65386, 65387, 65573, 65574, 68977, 68978, 68983 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			handleInPlaceSingleDoor(e.getPlayer(), e.getObject());
		}
	};

	public static ObjectClickHandler handleInPlaceDoubleDoors = new ObjectClickHandler(new Object[] { 6553, 6555, 9330, 9738, 69197, 69198 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			handleInPlaceDoubleDoor(e.getPlayer(), e.getObject());
		}
	};

	public static ObjectClickHandler handleWasteMyTimeDoors = new ObjectClickHandler(new Object[] { 52, 53, 71, 73, 74, 79, 80, 1589, 1590, 1591, 2154, 2155, 2184, 2685, 2686, 2687, 2688, 3442, 3626, 5730, 18168, 20341, 20665, 20666, 20695, 21065, 37470, 67747 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().sendMessage("The door is securely locked.");
		}
	};

	public static ObjectClickHandler handleKeyDoors = new ObjectClickHandler(new Object[] { 1804, 2623 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if ((e.getObjectId() == 1804 && e.getPlayer().getInventory().containsItem("brass key")) || (e.getObjectId() == 2623 && e.getPlayer().getInventory().containsItem("dusty key"))) {
				handleDoor(e.getPlayer(), e.getObject());
				return;
			}
			e.getPlayer().sendMessage("The door is securely locked.");
		}
	};

	public static ObjectClickHandler handleWasteMyTimeDoor1 = new ObjectClickHandler(new Object[] { 1591 }, new WorldTile(2794, 3199, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			handleDoor(e.getPlayer(), e.getObject());
		}
	};

	public static ObjectClickHandler handleWasteMyTimeDoor2 = new ObjectClickHandler(new Object[] { 21401 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().sendMessage("The door won't budge.");
		}
	};

	public static boolean isTempMove(ObjectDefinitions defs) {
		switch(defs.id) {
			case 39:
			case 2267:
			case 3437:
			case 45856:
			case 45857:
			case 45858:
			case 45859:
			case 24815:
				return true;
		}
		return !((defs.containsOption("Open") || defs.containsOption("Close")) && defs.interactable != 0);
	}

	public static void handleClosedDoor(Player player, GameObject object) {
		boolean open = object.getDefinitions(player).containsOption("Open");
		int rotation = object.getRotation(open ? 0 : -1);
		WorldTile adjusted = new WorldTile(object);
		switch (rotation) {
		case 0:
			adjusted = adjusted.transform(open ? -1 : 1, 0, 0);
			break;
		case 1:
			adjusted = adjusted.transform(0, open ? 1 : -1, 0);
			break;
		case 2:
			adjusted = adjusted.transform(open ? 1 : -1, 0, 0);
			break;
		case 3:
			adjusted = adjusted.transform(0, open ? -1 : 1, 0);
			break;
		}
		GameObject opp = new GameObject(DoorPair.getOpposingDoor(player, object), object.getType(), object.getRotation(open ? -1 : 1), adjusted);
		if (!isTempMove(opp.getDefinitions(player))) {
			World.removeObject(object);
			World.spawnObject(opp, true);
		} else {
			WorldTile toTile = object.transform(0, 0, 0);
			switch (object.getRotation()) {
			case 0:
				toTile = toTile.transform(player.getX() < object.getX() ? 0 : -1, 0, 0);
				break;
			case 1:
				toTile = toTile.transform(0, player.getY() > object.getY() ? 0 : 1, 0);
				break;
			case 2:
				toTile = toTile.transform(player.getX() > object.getX() ? 0 : 1, 0, 0);
				break;
			case 3:
				toTile = toTile.transform(0, player.getY() < object.getY() ? 0 : -1, 0);
				break;
			}
			World.spawnObjectTemporary(new GameObject(object).setIdNoRefresh(83), 2, true);
			World.spawnObjectTemporary(opp, 2, true);
			player.addWalkSteps(toTile, 3, false);
		}
	}

	public static void handleInPlaceSingleDoor(Player player, GameObject object) {
		ObjectDefinitions openedDef = ObjectDefinitions.getDefs(DoorPair.getOpposingDoor(player, object));
		boolean tempMove = isTempMove(openedDef);
		if (tempMove) {
			World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(player, object), object.getType(), object.getRotation(), object), 2, true);
			WorldTile toTile = object.transform(0, 0, 0);
			switch (object.getRotation()) {
			case 0:
				toTile = toTile.transform(player.getX() < object.getX() ? 0 : -1, 0, 0);
				break;
			case 1:
				toTile = toTile.transform(0, player.getY() > object.getY() ? 0 : 1, 0);
				break;
			case 2:
				toTile = toTile.transform(player.getX() > object.getX() ? 0 : 1, 0, 0);
				break;
			case 3:
				toTile = toTile.transform(0, player.getY() < object.getY() ? 0 : -1, 0);
				break;
			}
			player.addWalkSteps(toTile, 3, false);
		} else
			World.spawnObject(new GameObject(DoorPair.getOpposingDoor(player, object), object.getType(), object.getRotation(), object));
	}

	public static void handleInPlaceDoubleDoor(Player player, GameObject object) {
		ObjectDefinitions openedDef = ObjectDefinitions.getDefs(DoorPair.getOpposingDoor(player, object));
		boolean tempMove = isTempMove(openedDef);
		GameObject[] doors = getNearby(player, object, (t1, t2) -> {
			return t1.getY() > t2.getY();
		}, object.transform(0, 1, 0), object.transform(0, -1, 0));
		if (doors == null)
			doors = getNearby(player, object, (t1, t2) -> {
				return t1.getX() > t2.getX();
			}, object.transform(1, 0, 0), object.transform(-1, 0, 0));
		if (doors == null) {
			handleDoor(player, object);
			return;
		}
		if (tempMove) {
			World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(player, doors[0]), doors[0].getType(), doors[0].getRotation(), doors[0]), 3, true);
			World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(player, doors[1]), doors[1].getType(), doors[1].getRotation(), doors[1]), 3, true);
			switch (object.getRotation()) {
			case 0:
				player.addWalkSteps(object.transform(player.getX() > object.getX() ? -2 : 2, 0, 0), 3, false);
				break;
			case 1:
				player.addWalkSteps(object.transform(0, player.getY() < object.getY() ? 2 : -2, 0), 3, false);
				break;
			case 2:
				player.addWalkSteps(object.transform(player.getX() < object.getX() ? 2 : -2, 0, 0), 3, false);
				break;
			case 3:
				player.addWalkSteps(object.transform(0, player.getY() > object.getY() ? -2 : 2, 0), 3, false);
				break;
			}
		} else {
			World.spawnObject(new GameObject(DoorPair.getOpposingDoor(player, doors[0]), doors[0].getType(), doors[0].getRotation(), doors[0]));
			World.spawnObject(new GameObject(DoorPair.getOpposingDoor(player, doors[1]), doors[1].getType(), doors[1].getRotation(), doors[1]));
		}
	}

	public static void handleDoor(Player player, GameObject object) {
		handleDoor(player, object, 0);
	}

	public static void handleDoor(Player player, GameObject object, int offset) {
		boolean open = object.getDefinitions(player).containsOption("Open");
		int rotation = object.getRotation(open ? 0 + offset : -1 + offset);
		WorldTile adjusted = new WorldTile(object);
		switch (rotation) {
		case 0:
			adjusted = adjusted.transform(open ? -1 : 1, 0, 0);
			break;
		case 1:
			adjusted = adjusted.transform(0, open ? 1 : -1, 0);
			break;
		case 2:
			adjusted = adjusted.transform(open ? 1 : -1, 0, 0);
			break;
		case 3:
			adjusted = adjusted.transform(0, open ? -1 : 1, 0);
			break;
		}
		Door opp = new Door(DoorPair.getOpposingDoor(player, object), object.getType(), object.getRotation(open ? 1 : -1), adjusted, object);
		if (!isTempMove(opp.getDefinitions(player))) {
			if (object instanceof Door door) {
				World.removeObject(object);
				World.spawnObject(door.original, true);
			} else {
				World.removeObject(object);
				World.spawnObject(opp, true);
			}
		} else {
			WorldTile toTile = object.transform(0, 0, 0);
			switch (object.getRotation()) {
			case 0:
				toTile = toTile.transform(player.getX() < object.getX() ? 0 : -1, 0, 0);
				break;
			case 1:
				toTile = toTile.transform(0, player.getY() > object.getY() ? 0 : 1, 0);
				break;
			case 2:
				toTile = toTile.transform(player.getX() > object.getX() ? 0 : 1, 0, 0);
				break;
			case 3:
				toTile = toTile.transform(0, player.getY() < object.getY() ? 0 : -1, 0);
				break;
			}
			World.spawnObjectTemporary(new GameObject(object).setIdNoRefresh(83), 2, true);
			World.spawnObjectTemporary(opp, 2, true);
			player.addWalkSteps(toTile, 3, false);
		}
	}

	public static void handleLeftHandedDoor(Player player, GameObject object) {
		handleLeftHandedDoor(player, object, 0);
	}

	public static void handleLeftHandedDoor(Player player, GameObject object, int offset) {
		boolean open = object.getDefinitions(player).containsOption("Open");
		int rotation = object.getRotation(open ? 0 + offset : -1 + offset);
		WorldTile adjusted = new WorldTile(object);
		switch (rotation) {
		case 0:
			adjusted = adjusted.transform(open ? -1 : 1, 0, 0);
			break;
		case 1:
			adjusted = adjusted.transform(0, open ? 1 : -1, 0);
			break;
		case 2:
			adjusted = adjusted.transform(open ? 1 : -1, 0, 0);
			break;
		case 3:
			adjusted = adjusted.transform(0, open ? -1 : 1, 0);
			break;
		}
		Door opp = new Door(object.getId(), object.getType(), object.getRotation(open ? 3 : -1), adjusted, object);
		if (!isTempMove(opp.getDefinitions(player))) {
			if (object instanceof Door door) {
				World.removeObject(object);
				World.spawnObject(door.original, true);
			} else {
				World.removeObject(object);
				World.spawnObject(opp, true);
			}
		} else {
			WorldTile toTile = object.transform(0, 0, 0);
			switch (object.getRotation()) {
			case 0:
				toTile = toTile.transform(player.getX() < object.getX() ? 0 : -1, 0, 0);
				break;
			case 1:
				toTile = toTile.transform(0, player.getY() > object.getY() ? 0 : 1, 0);
				break;
			case 2:
				toTile = toTile.transform(player.getX() > object.getX() ? 0 : 1, 0, 0);
				break;
			case 3:
				toTile = toTile.transform(0, player.getY() < object.getY() ? 0 : -1, 0);
				break;
			}
			World.spawnObjectTemporary(new GameObject(object).setIdNoRefresh(83), 2, true);
			World.spawnObjectTemporary(opp, 2, true);
			player.addWalkSteps(toTile, 3, false);
		}
	}

	public static void handleOneWayDoor(Player player, GameObject object, int rotation) {
		boolean open = object.getDefinitions(player).containsOption("Open");
		WorldTile adjusted = new WorldTile(object);
		switch (rotation) {
		case 0:
			adjusted = adjusted.transform(open ? -1 : 1, 0, 0);
			break;
		case 1:
			adjusted = adjusted.transform(0, open ? 1 : -1, 0);
			break;
		case 2:
			adjusted = adjusted.transform(open ? 1 : -1, 0, 0);
			break;
		case 3:
			adjusted = adjusted.transform(0, open ? -1 : 1, 0);
			break;
		}
		GameObject opp = new GameObject(DoorPair.getOpposingDoor(player, object), object.getType(), object.getRotation(1), adjusted);
		if (!isTempMove(opp.getDefinitions(player))) {
			World.removeObject(object);
			if (object instanceof Door door)
				World.spawnObject(door.getOriginal());
			else
				World.spawnObject(opp, true);
		} else {
			WorldTile toTile = object.transform(0, 0, 0);
			switch (object.getRotation()) {
			case 0:
				toTile = toTile.transform(player.getX() < object.getX() ? 0 : -1, 0, 0);
				break;
			case 1:
				toTile = toTile.transform(0, player.getY() > object.getY() ? 0 : 1, 0);
				break;
			case 2:
				toTile = toTile.transform(player.getX() > object.getX() ? 0 : 1, 0, 0);
				break;
			case 3:
				toTile = toTile.transform(0, player.getY() < object.getY() ? 0 : -1, 0);
				break;
			}
			World.spawnObjectTemporary(new GameObject(object).setIdNoRefresh(83), 2, true);
			World.spawnObjectTemporary(opp, 2, true);
			player.addWalkSteps(toTile, 3, false);
		}
	}

	public static void handleDoubleDoor(Player player, GameObject object) {
		handleDoubleDoor(player, object, false);
	}

	public static void handleDoubleDoor(Player player, GameObject object, boolean invert) {
		boolean open = object.getDefinitions(player).containsOption("Open");
		ObjectDefinitions openedDef = ObjectDefinitions.getDefs(DoorPair.getOpposingDoor(player, object));
		boolean tempMove = isTempMove(openedDef);
		GameObject[] doors = getNearby(player, object, (t1, t2) -> {
			return t1.getY() > t2.getY();
		}, object.transform(0, 1, 0), object.transform(0, -1, 0));
		if (doors == null)
			doors = getNearby(player, object, (t1, t2) -> {
				return t1.getX() > t2.getX();
			}, object.transform(1, 0, 0), object.transform(-1, 0, 0));
		if (doors == null) {
			handleDoor(player, object);
			return;
		}
		int rotation = doors[0].getRotation(open ? invert ? 2 : 0 : invert ? 3 : 1);
		switch (rotation) {
		case 0:
			if (tempMove) {
				World.spawnObjectTemporary(new GameObject(doors[0]).setIdNoRefresh(83), 2, true);
				World.spawnObjectTemporary(new GameObject(doors[1]).setIdNoRefresh(83), 2, true);
				World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(player, doors[0]), doors[0].getType(), doors[0].getRotation(-1), doors[0].transform(-1, 0, 0)), 2, true);
				World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(player, doors[1]), doors[1].getType(), doors[1].getRotation(1), doors[1].transform(-1, 0, 0)), 2, true);
				player.addWalkSteps(object.transform(player.getX() >= object.getX() ? -1 : 0, 0, 0), 3, false);
			} else {
				World.removeObject(doors[0]);
				World.removeObject(doors[1]);
				if (doors[0] instanceof Door d0 && doors[1] instanceof Door d1) {
					World.spawnObject(d0.getOriginal());
					World.spawnObject(d1.getOriginal());
				} else {
					World.spawnObject(new Door(DoorPair.getOpposingDoor(player, doors[0]), doors[0].getType(), doors[0].getRotation(open ? -1 : 1), open ? doors[0].transform(-1, 0, 0) : doors[0].transform(1, 0, 0), doors[0]));
					World.spawnObject(new Door(DoorPair.getOpposingDoor(player, doors[1]), doors[1].getType(), doors[1].getRotation(open ? 1 : -1), open ? doors[1].transform(-1, 0, 0) : doors[1].transform(1, 0, 0), doors[1]));
				}
			}
			break;
		case 1:
			if (tempMove) {
				World.spawnObjectTemporary(new GameObject(doors[0]).setIdNoRefresh(83), 2, true);
				World.spawnObjectTemporary(new GameObject(doors[1]).setIdNoRefresh(83), 2, true);
				World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(player, doors[0]), doors[0].getType(), doors[0].getRotation(-1), doors[0].transform(0, 1, 0)), 2, true);
				World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(player, doors[1]), doors[1].getType(), doors[1].getRotation(1), doors[1].transform(0, 1, 0)), 2, true);
				player.addWalkSteps(object.transform(0, player.getY() <= object.getY() ? 1 : 0, 0), 3, false);
			} else {
				World.removeObject(doors[0]);
				World.removeObject(doors[1]);
				if (doors[0] instanceof Door d0 && doors[1] instanceof Door d1) {
					World.spawnObject(d0.getOriginal());
					World.spawnObject(d1.getOriginal());
				} else {
					World.spawnObject(new Door(DoorPair.getOpposingDoor(player, doors[0]), doors[0].getType(), doors[0].getRotation(open ? -1 : 1), open ? doors[0].transform(0, 1, 0) : doors[0].transform(0, -1, 0), doors[0]));
					World.spawnObject(new Door(DoorPair.getOpposingDoor(player, doors[1]), doors[1].getType(), doors[1].getRotation(open ? 1 : -1), open ? doors[1].transform(0, 1, 0) : doors[1].transform(0, -1, 0), doors[1]));
				}
			}
			break;
		case 2:
			if (tempMove) {
				World.spawnObjectTemporary(new GameObject(doors[0]).setIdNoRefresh(83), 2, true);
				World.spawnObjectTemporary(new GameObject(doors[1]).setIdNoRefresh(83), 2, true);
				World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(player, doors[0]), doors[0].getType(), doors[0].getRotation(1), doors[0].transform(1, 0, 0)), 2, true);
				World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(player, doors[1]), doors[1].getType(), doors[1].getRotation(-1), doors[1].transform(1, 0, 0)), 2, true);
				player.addWalkSteps(object.transform(player.getX() <= object.getX() ? 1 : 0, 0, 0), 3, false);
			} else {
				World.removeObject(doors[0]);
				World.removeObject(doors[1]);
				if (doors[0] instanceof Door d0 && doors[1] instanceof Door d1) {
					World.spawnObject(d0.getOriginal());
					World.spawnObject(d1.getOriginal());
				} else {
					World.spawnObject(new Door(DoorPair.getOpposingDoor(player, doors[0]), doors[0].getType(), doors[0].getRotation(open ? 1 : -1), open ? doors[0].transform(1, 0, 0) : doors[0].transform(-1, 0, 0), doors[0]));
					World.spawnObject(new Door(DoorPair.getOpposingDoor(player, doors[1]), doors[1].getType(), doors[1].getRotation(open ? -1 : 1), open ? doors[1].transform(1, 0, 0) : doors[1].transform(-1, 0, 0), doors[1]));
				}
			}
			break;
		case 3:
			if (tempMove) {
				World.spawnObjectTemporary(new GameObject(doors[0]).setIdNoRefresh(83), 2, true);
				World.spawnObjectTemporary(new GameObject(doors[1]).setIdNoRefresh(83), 2, true);
				World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(player, doors[0]), doors[0].getType(), doors[0].getRotation(1), doors[0].transform(0, -1, 0)), 2, true);
				World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(player, doors[1]), doors[1].getType(), doors[1].getRotation(-1), doors[1].transform(0, -1, 0)), 2, true);
				player.addWalkSteps(object.transform(0, player.getY() >= object.getY() ? -1 : 0, 0), 3, false);
			} else {
				World.removeObject(doors[0]);
				World.removeObject(doors[1]);
				if (doors[0] instanceof Door d0 && doors[1] instanceof Door d1) {
					World.spawnObject(d0.getOriginal());
					World.spawnObject(d1.getOriginal());
				} else {
					World.spawnObject(new Door(DoorPair.getOpposingDoor(player, doors[0]), doors[0].getType(), doors[0].getRotation(open ? 1 : -1), open ? doors[0].transform(0, -1, 0) : doors[0].transform(0, 1, 0), doors[0]));
					World.spawnObject(new Door(DoorPair.getOpposingDoor(player, doors[1]), doors[1].getType(), doors[1].getRotation(open ? -1 : 1), open ? doors[1].transform(0, -1, 0) : doors[1].transform(0, 1, 0), doors[1]));
				}
			}
			break;
		}
	}

	public static void handleGate(Player player, GameObject object) {
		boolean open = object.getDefinitions(player).containsOption("Open");
		int rotation = object.getRotation(open ? 0 : 1);
		ObjectDefinitions openedDef = ObjectDefinitions.getDefs(DoorPair.getOpposingDoor(player, object));
		boolean tempMove = !((openedDef.containsOption("Open") || openedDef.containsOption("Close")) && openedDef.interactable != 0);
		GameObject[] gates;
		switch (rotation) {
		case 0:
			if (open)
				gates = getNearby(player, object, (t1, t2) -> {
					return t1.getY() > t2.getY();
				}, object.transform(0, -1, 0), object.transform(0, 1, 0));
			else
				gates = getNearby(player, object, (t1, t2) -> {
					return t1.getX() < t2.getX();
				}, object.transform(-1, 0, 0), object.transform(1, 0, 0));
			if (gates == null) {
				handleDoor(player, object);
				return;
			}
			if (tempMove) {
				World.spawnObjectTemporary(new GameObject(gates[0]).setIdNoRefresh(83), 2, true);
				World.spawnObjectTemporary(new GameObject(gates[1]).setIdNoRefresh(83), 2, true);
				World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(player, gates[0]), gates[0].getType(), gates[0].getRotation(open ? -1 : 1), open ? gates[0].transform(-1, 0, 0) : gates[0].transform(1, 0, 0)), 2, true);
				World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(player, gates[1]), gates[1].getType(), gates[1].getRotation(open ? -1 : 1), open ? gates[1].transform(-2, -1, 0) : gates[1].transform(2, 1, 0)), 2, true);
				player.addWalkSteps(object.transform(player.getX() < object.getX() ? 0 : -1, 0, 0), 3, false);
			} else {
				World.removeObject(gates[0]);
				World.removeObject(gates[1]);
				World.spawnObject(new GameObject(DoorPair.getOpposingDoor(player, gates[0]), gates[0].getType(), gates[0].getRotation(open ? -1 : 1), open ? gates[0].transform(-1, 0, 0) : gates[0].transform(1, 0, 0)));
				World.spawnObject(new GameObject(DoorPair.getOpposingDoor(player, gates[1]), gates[1].getType(), gates[1].getRotation(open ? -1 : 1), open ? gates[1].transform(-2, -1, 0) : gates[1].transform(2, 1, 0)));
			}
			break;
		case 1:
			if (open)
				gates = getNearby(player, object, (t1, t2) -> {
					return t1.getX() > t2.getX();
				}, object.transform(1, 0, 0), object.transform(-1, 0, 0));
			else
				gates = getNearby(player, object, (t1, t2) -> {
					return t1.getY() > t2.getY();
				}, object.transform(0, 1, 0), object.transform(0, -1, 0));
			if (gates == null) {
				handleDoor(player, object);
				return;
			}
			if (tempMove) {
				World.spawnObjectTemporary(new GameObject(gates[0]).setIdNoRefresh(83), 2, true);
				World.spawnObjectTemporary(new GameObject(gates[1]).setIdNoRefresh(83), 2, true);
				World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(player, gates[0]), gates[0].getType(), gates[0].getRotation(-1), gates[0].transform(0, 1, 0)), 2, true);
				World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(player, gates[1]), gates[1].getType(), gates[1].getRotation(-1), gates[1].transform(-1, 2, 0)), 2, true);
				player.addWalkSteps(object.transform(0, player.getY() <= object.getY() ? 1 : 0, 0), 3, false);
			} else {
				World.removeObject(gates[0]);
				World.removeObject(gates[1]);
				World.spawnObject(new GameObject(DoorPair.getOpposingDoor(player, gates[0]), gates[0].getType(), gates[0].getRotation(open ? -1 : 1), open ? gates[0].transform(0, 1, 0) : gates[0].transform(0, -1, 0)));
				World.spawnObject(new GameObject(DoorPair.getOpposingDoor(player, gates[1]), gates[1].getType(), gates[1].getRotation(open ? -1 : 1), open ? gates[1].transform(-1, 2, 0) : gates[1].transform(1, -2, 0)));
			}
			break;
		case 2:
			if (open)
				gates = getNearby(player, object, (t1, t2) -> {
					return t1.getY() < t2.getY();
				}, object.transform(0, 1, 0), object.transform(0, -1, 0));
			else
				gates = getNearby(player, object, (t1, t2) -> {
					return t1.getX() > t2.getX();
				}, object.transform(1, 0, 0), object.transform(-1, 0, 0));
			if (gates == null) {
				handleDoor(player, object);
				return;
			}
			if (tempMove) {
				World.spawnObjectTemporary(new GameObject(gates[0]).setIdNoRefresh(83), 2, true);
				World.spawnObjectTemporary(new GameObject(gates[1]).setIdNoRefresh(83), 2, true);
				World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(player, gates[0]), gates[0].getType(), gates[0].getRotation(open ? -1 : 1), open ? gates[0].transform(1, 0, 0) : gates[0].transform(-1, 0, 0)), 2, true);
				World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(player, gates[1]), gates[1].getType(), gates[1].getRotation(open ? -1 : 1), open ? gates[1].transform(2, 1, 0) : gates[1].transform(-2, -1, 0)), 2, true);
				player.addWalkSteps(object.transform(player.getX() > object.getX() ? 0 : 1, 0, 0), 3, false);
			} else {
				World.removeObject(gates[0]);
				World.removeObject(gates[1]);
				World.spawnObject(new GameObject(DoorPair.getOpposingDoor(player, gates[0]), gates[0].getType(), gates[0].getRotation(open ? -1 : 1), open ? gates[0].transform(1, 0, 0) : gates[0].transform(-1, 0, 0)));
				World.spawnObject(new GameObject(DoorPair.getOpposingDoor(player, gates[1]), gates[1].getType(), gates[1].getRotation(open ? -1 : 1), open ? gates[1].transform(2, 1, 0) : gates[1].transform(-2, -1, 0)));
			}
			break;
		case 3:
			if (open)
				gates = getNearby(player, object, (t1, t2) -> {
					return t1.getX() < t2.getX();
				}, object.transform(1, 0, 0), object.transform(-1, 0, 0));
			else
				gates = getNearby(player, object, (t1, t2) -> {
					return t1.getY() < t2.getY();
				}, object.transform(0, 1, 0), object.transform(0, -1, 0));
			if (gates == null) {
				handleDoor(player, object);
				return;
			}
			if (tempMove) {
				World.spawnObjectTemporary(new GameObject(gates[0]).setIdNoRefresh(83), 2, true);
				World.spawnObjectTemporary(new GameObject(gates[1]).setIdNoRefresh(83), 2, true);
				World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(player, gates[0]), gates[0].getType(), gates[0].getRotation(-1), gates[0].transform(0, -1, 0)), 2, true);
				World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(player, gates[1]), gates[1].getType(), gates[1].getRotation(-1), gates[1].transform(1, -2, 0)), 2, true);
				player.addWalkSteps(object.transform(0, player.getY() < object.getY() ? 0 : -1, 0), 3, false);
			} else {
				World.removeObject(gates[0]);
				World.removeObject(gates[1]);
				World.spawnObject(new GameObject(DoorPair.getOpposingDoor(player, gates[0]), gates[0].getType(), gates[0].getRotation(open ? -1 : 1), open ? gates[0].transform(0, -1, 0) : gates[0].transform(0, 1, 0)));
				World.spawnObject(new GameObject(DoorPair.getOpposingDoor(player, gates[1]), gates[1].getType(), gates[1].getRotation(open ? -1 : 1), open ? gates[1].transform(1, -2, 0) : gates[1].transform(-1, 2, 0)));
			}
			break;
		}
	}

	private static GameObject[] getNearby(Player player, GameObject object, BiFunction<WorldTile, WorldTile, Boolean> sort, WorldTile... toCheck) {
		GameObject[] g = new GameObject[2];
		for (WorldTile t : toCheck)
			if (g[0] == null || g[0].getDefinitions().interactable == 0 || !g[0].getDefinitions().getName().equals(object.getDefinitions().getName()))
				g[0] = World.getObject(t, object.getType());
		if (g[0] == null || g[0].getDefinitions().interactable == 0 || !g[0].getDefinitions().getName().equals(object.getDefinitions().getName()))
			return null;
		if (sort.apply(g[0], object)) {
			g[1] = g[0];
			g[0] = object;
		} else
			g[1] = object;
		return g;
	}

	public static void searchDoors(int search) {
		int SEARCH_FOR = search;

		Map<Integer, List<Integer>> doors = new HashMap<>();

		for (int i = 0; i < Utils.getObjectDefinitionsSize(); i++) {
			if (i != SEARCH_FOR)
				continue;
			ObjectDefinitions defs = ObjectDefinitions.getDefs(i);

			if (defs.varp != -1 || defs.varpBit != -1)
				continue;
			if (defs.getName().toLowerCase().contains("door") || defs.getName().toLowerCase().contains("gate") || defs.getName().toLowerCase().contains("fence") || defs.getName().toLowerCase().contains("wall"))
				if (defs.containsOptionIgnoreCase("open")) {
					List<Integer> opened = new ArrayList<>();
					for (int op = 0; op < Utils.getObjectDefinitionsSize(); op++) {
						if (op == i || op < 0 || op > Utils.getObjectDefinitionsSize())
							continue;
						ObjectDefinitions openedDef = ObjectDefinitions.getDefs(op);
						if (!openedDef.containsOptionIgnoreCase("open") && openedDef.modelIds != null && defs.modelIds[0][0] == openedDef.modelIds[0][0])
							if (Arrays.equals(defs.modifiedColors, openedDef.modifiedColors) && Arrays.equals(defs.originalColors, openedDef.originalColors))
								opened.add(op);
					}
					doors.put(i, opened);
				}
		}

		List<Integer> closedList = new ArrayList<>(doors.keySet());
		Collections.sort(closedList);

		for (int closed : closedList) {
			List<Integer> openedList = doors.get(closed);
			if (openedList.isEmpty())
				continue;
			ObjectDefinitions closedDef = ObjectDefinitions.getDefs(closed);
			Logger.debug(Doors.class, "searchDoors", closedDef.id + " - " + closedDef.getName() + " - " + closedDef.modelIds[0][0] + " - " + Arrays.toString(closedDef.types) + " {");
			for (int open : openedList) {
				ObjectDefinitions openedDef = ObjectDefinitions.getDefs(open);
				Logger.debug(Doors.class, "searchDoors", "\t" + openedDef.id + " - " + openedDef.getName() + " - " + openedDef.modelIds[0][0] + " - " + Arrays.toString(openedDef.types) + " - " + Arrays.toString(openedDef.options));
				Logger.debug(Doors.class, "searchDoors", "\tap(" + search + ", " + openedDef.id + ");");
			}
			Logger.debug(Doors.class, "searchDoors", "}");
		}
	}

	public static class Door extends GameObject {
		private GameObject original;

		public Door(int id, ObjectType type, int rotation, WorldTile location, GameObject original) {
			super(id, type, rotation, location);
			this.original = new GameObject(original);
		}

		public GameObject getOriginal() {
			return original;
		}
	}
}
