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

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.content.skills.dungeoneering.DungeonConstants.GuardianMonster;
import com.rs.game.content.skills.dungeoneering.DungeonConstants.SkillDoors;
import com.rs.game.content.skills.dungeoneering.rooms.BossRoom;
import com.rs.game.content.skills.dungeoneering.rooms.HandledRoom;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

import java.util.*;
import java.util.stream.Collectors;

public final class DungeonUtils {

	public static int getMaxFloor(int level) {
		return (level + 1) / 2;
		//return (level / 2) + 1;
	}

	public static int getLevel(int floor) {
		return (floor * 2) - 1;
		//return (floor - 1) * 2;
	}

	public static boolean isBindAmmo(Item item) {
		if (item.getId() == 19867 || item.getId() == 19868) //runes box
			return true;
		for (int id : DungeonConstants.ARROWS)
			if (item.getId() == id)
				return true;
		for (int id : DungeonConstants.RUNES)
			if (item.getId() == id)
				return true;
		return false;
	}

	public static int getMaxBindItems(int level) {
		if (level >= 120)
			return 5;
		if (level >= 90)
			return 4;
		if (level >= 50)
			return 3;
		if (level >= 20)
			return 2;
		return 1;
	}

	public static int getBindedId(Item item) {
		String name = item.getName();
		for (int i = 15775; i <= 16272; i++)
			if (ItemDefinitions.getDefs(i).name.replace(" (b)", "").equals(name))
				return i;
		for (int i = 19865; i <= 19866; i++)
			if (ItemDefinitions.getDefs(i).name.replace(" (b)", "").equals(name))
				return i;
		return -1;
	}

	public static int getClosestToCombatLevel(int[] npcIds, int target) {
		List<Integer> ids = Arrays.stream(npcIds).boxed().collect(Collectors.toList());
		Collections.shuffle(ids);
		return ids.stream()
				.min(Comparator.comparingInt(i -> Math.abs(NPCDefinitions.getDefs(i).combatLevel - target)))
				.orElseThrow(() -> new NoSuchElementException("No suitable combat level found"));
	}

	public static int[] getFloorThemeRange(int floorId) {
		if (floorId <= 11)
			return new int[] { 1, 11 };
		if (floorId <= 17)
			return new int[] { 12, 17 };
		if (floorId <= 29)
			return new int[] { 18, 29 };
		if (floorId <= 35)
			return new int[] { 30, 35 };
		if (floorId <= 47)
			return new int[] { 36, 47 };
		return new int[] { 48, 60 };
	}

	public static DungeonConstants.FloorType getFloorType(int floorId) {
		if (floorId <= 11)
			return DungeonConstants.FloorType.Frozen;
		if (floorId <= 17)
			return DungeonConstants.FloorType.Abandoned;
		if (floorId <= 29)
			return DungeonConstants.FloorType.Furnished;
		if (floorId <= 35)
			return DungeonConstants.FloorType.Abandoned;
		if (floorId <= 47)
			return DungeonConstants.FloorType.Occult;
		return DungeonConstants.FloorType.Warped;
	}

	public static int getSafeMusic(DungeonConstants.FloorType type) {
		return DungeonConstants.SAFE_MUSICS[type.ordinal()][Utils.random(DungeonConstants.DANGEROUS_MUSICS[type.ordinal()].length)];
	}

	public static int getDangerousMusic(DungeonConstants.FloorType type) {
		return DungeonConstants.DANGEROUS_MUSICS[type.ordinal()][Utils.random(DungeonConstants.DANGEROUS_MUSICS[type.ordinal()].length)];
	}

	public static RoomReference getRandomRoom(Room[][] map) {
		return new RoomReference(Utils.random(map.length), Utils.random(map[0].length));
	}

	public static int getHunterCreature() {
		return DungeonConstants.HUNTER_CREATURES[Utils.random(DungeonConstants.HUNTER_CREATURES.length)];
	}

	public static BossRoom getBossRoomWithChunk(DungeonConstants.FloorType type, int x, int y) {
		for (BossRoom room : DungeonConstants.BOSS_ROOMS[type.ordinal()])
			if (room.getChunkX() == x && room.getChunkY() == y)
				return room;
		return null;
	}

	public static Room[] selectPossibleBossRooms(DungeonConstants.FloorType type, int complexity, int floorId, boolean n, boolean e, boolean s, boolean w, int rotation) {
		ArrayList<Room> possiblities = new ArrayList<>();
		for (BossRoom handledRoom : DungeonConstants.BOSS_ROOMS[type.ordinal()]) {
			if (!handledRoom.isComplexity(complexity) || (handledRoom.getMinFloor() > floorId))
				continue;
			Room room = new Room(handledRoom, rotation);
			if (room.hasNorthDoor() == n && room.hasEastDoor() == e && room.hasSouthDoor() == s && room.hasWestDoor() == w)
				possiblities.add(room);
		}
		//return new Room[] { new Room(DungeonConstants.BOSS_ROOMS[2][4], rotation) };
		return possiblities.toArray(new Room[0]);
	}

	public static boolean theresDoorTo(RoomReference checkRoom, Room[][] map) {
		if (checkRoom.getRoomX() < 0 || checkRoom.getRoomX() >= map.length || checkRoom.getRoomY() < 0 || checkRoom.getRoomY() >= map[checkRoom.getRoomX()].length)
			return false;
		if (checkRoom.getRoomX() != 0 && map[checkRoom.getRoomX() - 1][checkRoom.getRoomY()] != null && map[checkRoom.getRoomX() - 1][checkRoom.getRoomY()].hasEastDoor())
			return true;
		if (checkRoom.getRoomX() != map.length - 1 && map[checkRoom.getRoomX() + 1][checkRoom.getRoomY()] != null && map[checkRoom.getRoomX() + 1][checkRoom.getRoomY()].hasWestDoor())
			return true;
		if (checkRoom.getRoomY() != 0 && map[checkRoom.getRoomX()][checkRoom.getRoomY() - 1] != null && map[checkRoom.getRoomX()][checkRoom.getRoomY() - 1].hasNorthDoor())
			return true;
		if (checkRoom.getRoomY() != map[checkRoom.getRoomX()].length - 1 && map[checkRoom.getRoomX()][checkRoom.getRoomY() + 1] != null && map[checkRoom.getRoomX()][checkRoom.getRoomY() + 1].hasSouthDoor())
			return true;
		return false;

	}

	public static Room[] selectPossibleRooms(HandledRoom[] handledRooms, int complexity, DungeonConstants.FloorType floorType, boolean n, boolean e, boolean s, boolean w) {
		ArrayList<Room> possiblities = new ArrayList<>();
		for (HandledRoom handledRoom : handledRooms) {
			if (!handledRoom.isAvailableOnFloorType(floorType) || !handledRoom.isComplexity(complexity))
				continue;
			for (int rotation = 0; rotation < DungeonConstants.ROTATIONS_COUNT; rotation++) {
				Room room = new Room(handledRoom, rotation);
				if (room.hasNorthDoor() == n && room.hasEastDoor() == e && room.hasSouthDoor() == s && room.hasWestDoor() == w)
					possiblities.add(room);
			}
		}
		return possiblities.toArray(new Room[0]);
	}

	public static Room[] selectPossibleRooms(HandledRoom[] handledRooms, int complexity, DungeonConstants.FloorType floorType, boolean n, boolean e, boolean s, boolean w, int rotation) {
		ArrayList<Room> possiblities = new ArrayList<>();
		for (HandledRoom handledRoom : handledRooms) {
			if (!handledRoom.isAvailableOnFloorType(floorType) || !handledRoom.isComplexity(complexity))
				continue;
			Room room = new Room(handledRoom, rotation);
			if (room.hasNorthDoor() == n && room.hasEastDoor() == e && room.hasSouthDoor() == s && room.hasWestDoor() == w)
				possiblities.add(room);
		}
		return possiblities.toArray(new Room[0]);
	}

	public static boolean checkDungeonBounds(RoomReference reference, Room[][] map, Room room) {
		if ((reference.getRoomX() == 0 && room.hasWestDoor()) || (reference.getRoomX() == map.length - 1 && room.hasEastDoor()))
			return false;
		if ((reference.getRoomY() == 0 && room.hasSouthDoor()) || (reference.getRoomY() == map[0].length - 1 && room.hasNorthDoor()))
			return false;
		return true;
	}

	public static boolean isClipped(int objectId) {
		for (int id : DungeonConstants.DUNGEON_BOSS_DOORS)
			if (id == objectId)
				return true;
		for (int id : DungeonConstants.DUNGEON_DOORS)
			if (id == objectId)
				return true;
		/*	for (int id : DungeonConstants.DUNGEON_SKILL_DOORS)
			    if (id == objectId)
				return true;
			for (int id : DungeonConstants.THIEF_CHEST_OPEN)
			    if (id == objectId)
				return true;
			for (int id : DungeonConstants.THIEF_CHEST_LOCKED)
			    if (id == objectId)
				return true;
			if (objectId >= KeyDoors.ORANGE_TRIANGLE.getObjectId() && objectId <= KeyDoors.GOLD_SHIELD.getObjectId())
				return true;*/
		return false;
	}

	public static int getMiningResource(int rockType, DungeonConstants.FloorType floorType) {
		return DungeonConstants.MINING_RESOURCES[floorType.ordinal()] + rockType * 2;
	}

	public static int getWoodcuttingResource(int treeType, DungeonConstants.FloorType floorType) {
		return DungeonConstants.WOODCUTTING_RESOURCES[floorType.ordinal()] + treeType * 2;
	}

	public static int getFarmingResource(int flowerType, DungeonConstants.FloorType floorType) {
		return DungeonConstants.FARMING_RESOURCES[floorType.ordinal()] + flowerType * 2;
	}

	public static int getBattleaxe(int tier) {
		return 15753 + (tier - 1) * 2;
	}

	public static int getGauntlets(int tier) {
		return 16273 + (tier - 1) * 2;
	}

	public static int getPickaxe(int tier) {
		return 16295 + (tier - 1) * 2;
	}

	public static int getBoots(int tier) {
		return 16339 + (tier - 1) * 2;
	}

	public static int getHatchet(int tier) {
		return 16361 + (tier - 1) * 2;
	}

	public static int getLongsword(int tier) {
		return 16383 + (tier - 1) * 2;
	}

	public static int getMaul(int tier) {
		return 16405 + (tier - 1) * 2;
	}

	public static int getArrows(int tier) {
		return 16427 + (tier - 1) * 5;
	}

	public static int getPlatelegs(int tier, boolean male) {
		return (male ? 16669 : 16647) + (tier - 1) * 2;
	}

	public static int getFullHelm(int tier) {
		return 16691 + (tier - 1) * 2;
	}

	public static int getChainbody(int tier) {
		return 16713 + (tier - 1) * 2;
	}

	public static int getDagger(int tier) {
		return 16757 + (tier - 1) * 2;
	}

	public static int getTornBag(int tier) {
		return 17995 + (tier - 1) * 2;
	}

	public static int getOre(int tier) {
		return 17630 + (tier - 1) * 2;
	}

	public static int getBranche(int tier) {
		return 17682 + (tier - 1) * 2;
	}

	public static int getTextile(int tier) {
		return 17448 + (tier - 1) * 2;
	}

	public static int getHerb(int tier) {
		return 17494 + (tier - 1) * 2;
	}

	public static int getSeed(int tier) {
		return 17823 + (tier - 1);
	}

	public static int getRandomGear(int tier) {
		return switch (Utils.random(29)) {
			case 0 -> getLongbow(tier);
			case 1 -> getShortbow(tier);
			case 2 -> getChaps(tier);
			case 3 -> getLeatherBoots(tier);
			case 4 -> getVambrances(tier);
			case 5 -> getLeatherBody(tier);
			case 6 -> getCoif(tier);
			case 7 -> getRobeTop(tier);
			case 8 -> getElementalStaff(tier);
			case 9 -> getBasicStaff(tier);
			case 10 -> getGloves(tier);
			case 11 -> getShoes(tier);
			case 12 -> getRobeBottom(tier);
			case 13 -> getHood(tier);
			case 14 -> getKiteshield(tier);
			case 15 -> getPlatebody(tier);
			case 16 -> getSpear(tier);
			case 17 -> getWarhammer(tier);
			case 18 -> getRapier(tier);
			case 19 -> get2HSword(tier);
			case 20 -> getDagger(tier);
			case 21 -> getChainbody(tier);
			case 22 -> getFullHelm(tier);
			case 23 -> getPlatelegs(tier, true);
			case 24 -> getMaul(tier);
			case 25 -> getLongsword(tier);
			case 26 -> getBoots(tier);
			case 27 -> getGauntlets(tier);
			case 28 -> getBattleaxe(tier);
			default -> getHatchet(tier); // shouldn't
		};
	}

	public static int getRandomRangeGear(int tier) {
		return switch (Utils.random(7)) {
			case 0 -> getLongbow(tier);
			case 1 -> getShortbow(tier);
			case 2 -> getChaps(tier);
			case 3 -> getLeatherBoots(tier);
			case 4 -> getVambrances(tier);
			case 5 -> getLeatherBody(tier);
			case 6 -> getCoif(tier);
			default -> getHatchet(tier); // shouldn't
		};
	}

	public static int getRandomMagicGear(int tier) {
		return switch (Utils.random(7)) {
			case 0 -> getRobeTop(tier);
			case 1 -> getElementalStaff(tier);
			case 2 -> getBasicStaff(tier);
			case 3 -> getGloves(tier);
			case 4 -> getShoes(tier);
			case 5 -> getRobeBottom(tier);
			case 6 -> getHood(tier);
			default -> getHatchet(tier); // shouldn't
		};
	}

	public static int getRandomMeleeGear(int tier) {
		return switch (Utils.random(15)) {
			case 0 -> getKiteshield(tier);
			case 1 -> getPlatebody(tier);
			case 2 -> getSpear(tier);
			case 3 -> getWarhammer(tier);
			case 4 -> getRapier(tier);
			case 5 -> get2HSword(tier);
			case 6 -> getDagger(tier);
			case 7 -> getChainbody(tier);
			case 8 -> getFullHelm(tier);
			case 9 -> getPlatelegs(tier, true);
			case 10 -> getMaul(tier);
			case 11 -> getLongsword(tier);
			case 12 -> getBoots(tier);
			case 13 -> getGauntlets(tier);
			case 14 -> getBattleaxe(tier);
			default -> getHatchet(tier); // shouldn't
		};
	}

	public static int getRandomWeapon(int tier) {
		return switch (Utils.random(12)) {
			case 0 -> getLongbow(tier);
			case 1 -> getShortbow(tier);
			case 2 -> getElementalStaff(tier);
			case 3 -> getBasicStaff(tier);
			case 4 -> getSpear(tier);
			case 5 -> getWarhammer(tier);
			case 6 -> getRapier(tier);
			case 7 -> get2HSword(tier);
			case 8 -> getDagger(tier);
			case 9 -> getMaul(tier);
			case 10 -> getLongsword(tier);
			case 11 -> getBattleaxe(tier);
			default -> getHatchet(tier); // shouldn't
		};
	}

	public static int get2HSword(int tier) {
		return 16889 + (tier - 1) * 2;
	}

	public static int getFood(int tier) {
		return 18159 + (tier - 1) * 2;
	}

	public static int getRapier(int tier) {
		return 16935 + (tier - 1) * 2;
	}

	public static int getWarhammer(int tier) {
		return 17019 + (tier - 1) * 2;
	}

	public static int getSpear(int tier) {
		return 17063 + (tier - 1) * 8;
	}

	public static int getPlatebody(int tier) {
		return 17239 + (tier - 1) * 2;
	}

	public static int getKiteshield(int tier) {
		return 17341 + (tier - 1) * 2;
	}

	public static int getHood(int tier) {
		return 16735 + (tier - 1) * 2;
	}

	public static int getRobeBottom(int tier) {
		return 16845 + (tier - 1) * 2;
	}

	public static int getShoes(int tier) {
		return 16911 + (tier - 1) * 2;
	}

	public static int getGloves(int tier) {
		return 17151 + (tier - 1) * 2;
	}

	public static int getBasicStaff(int tier) {
		return 16977 + (tier - 1) * 2;
	}

	public static int getElementalStaff(int tier) {
		if (tier == 2)
			return 17001; // earth
		if (tier == 3)
			return 17005; // fire
		if (tier == 4)
			return 17009; // air
		if (tier == 5)
			return 17013; // catalytic
		if (tier == 6)
			return 16999; // empowered water
		if (tier == 7)
			return 17003; // empowered earth
		if (tier == 8)
			return 17007; // empowered fire
		if (tier == 9)
			return 17011; // empowered air
		if (tier == 10)
			return 17015; // empowered catalytic
		if (tier == 11)
			return 17017; // celestial catalytic
		return 16997; //water
	}

	public static int getStartRunes(int level) {
		if (level >= 41)
			return DungeonConstants.RUNES[6]; //death
		if (level >= 17)
			return DungeonConstants.RUNES[5]; //chaos
		return DungeonConstants.RUNES[4]; //mind
	}

	public static int getRobeTop(int tier) {
		return 17217 + (tier - 1) * 2;
	}

	public static int getCoif(int tier) {
		return 17041 + (tier - 1) * 2;
	}

	public static int getLeatherBody(int tier) {
		return 17173 + (tier - 1) * 2;
	}

	public static int getVambrances(int tier) {
		return 17195 + (tier - 1) * 2;
	}

	public static int getLeatherBoots(int tier) {
		return 17297 + (tier - 1) * 2;
	}

	public static int getChaps(int tier) {
		return 17319 + (tier - 1) * 2;
	}

	public static int getShortbow(int tier) {
		return 16867 + (tier - 1) * 2;
	}

	public static int getLongbow(int tier) {
		return 16317 + (tier - 1) * 2;
	}

	public static int getMaxTier(DungeonPartyManager party, int cap, int... skills) {
		int maxTier = cap;
		for (int skill : skills) {
			int level = party.getMaxLevel(skill);
			int tier = getTier(level);
			if (maxTier > tier)
				maxTier = tier;
		}
		return maxTier;
	}

	public static int getTier(int level) {
		return level == 99 ? 11 : level > 10 ? (1 + level / 10) : 1;

	}

	public static boolean isLadder(int id, DungeonConstants.FloorType type) {
		return DungeonConstants.LADDERS[type.ordinal()] == id;
	}

	public static boolean isOpenSkillDoor(int id, DungeonConstants.FloorType type) {
		for (SkillDoors s : SkillDoors.values()) {
			int openId = s.getOpenObject(type);
			if (openId != -1 && openId == id)
				return true;
		}
		return false;
	}

	public static List<Tile> getRandomOrderCoords(int size) {
		List<Tile> list = new ArrayList<>();
		for (int x = 2;x < (15-size);x++)
			for (int y = 2;y < (15-size);y++)
				list.add(Tile.of(x, y, 0));
		Collections.shuffle(list);
		return list;
	}

	public static List<Integer> generateRandomMonsters(DungeonConstants.FloorType floorType, int maxLevel, int combatTotal, int numMonsters) {
		List<Integer> list = new ArrayList<>();
		if (Utils.random(4) == 0)
			return list;
		int[] combatLevels = Utils.randSum(numMonsters, combatTotal);

		for(int i = 0;i < combatLevels.length;i++)
			if (combatLevels[i] > maxLevel)
				combatLevels[i] = maxLevel;

		List<GuardianMonster> possible = new ArrayList<>(GuardianMonster.forFloor(floorType));
		int atts = 0;
		for (int i = 0;i < numMonsters;) {
			int try1 = getClosestToCombatLevel(possible.get(Utils.random(possible.size())).getNPCIds(), combatLevels[i]);
			if (atts++ > 15 || Utils.diff(NPCDefinitions.getDefs(try1).combatLevel, combatLevels[i]) < 15) {
				list.add(try1);
				i++;
				atts = 0;
			}
		}
		return list;
	}
}