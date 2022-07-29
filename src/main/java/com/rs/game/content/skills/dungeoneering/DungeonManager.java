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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.cores.CoresManager;
import com.rs.game.World;
import com.rs.game.content.combat.CombatDefinitions.Spellbook;
import com.rs.game.content.skills.dungeoneering.DungeonConstants.GuardianMonster;
import com.rs.game.content.skills.dungeoneering.DungeonConstants.KeyDoors;
import com.rs.game.content.skills.dungeoneering.DungeonConstants.MapRoomIcon;
import com.rs.game.content.skills.dungeoneering.DungeonConstants.SkillDoors;
import com.rs.game.content.skills.dungeoneering.npcs.DivineSkinweaver;
import com.rs.game.content.skills.dungeoneering.npcs.Dreadnaut;
import com.rs.game.content.skills.dungeoneering.npcs.DungeonNPC;
import com.rs.game.content.skills.dungeoneering.npcs.DungeonSkeletonBoss;
import com.rs.game.content.skills.dungeoneering.npcs.DungeonSlayerNPC;
import com.rs.game.content.skills.dungeoneering.npcs.FleshspoilerHaasghenahk;
import com.rs.game.content.skills.dungeoneering.npcs.ForgottenWarrior;
import com.rs.game.content.skills.dungeoneering.npcs.GluttonousBehemoth;
import com.rs.game.content.skills.dungeoneering.npcs.Gravecreeper;
import com.rs.game.content.skills.dungeoneering.npcs.Guardian;
import com.rs.game.content.skills.dungeoneering.npcs.HobgoblinGeomancer;
import com.rs.game.content.skills.dungeoneering.npcs.HopeDevourer;
import com.rs.game.content.skills.dungeoneering.npcs.IcyBones;
import com.rs.game.content.skills.dungeoneering.npcs.KalGerWarmonger;
import com.rs.game.content.skills.dungeoneering.npcs.LakkTheRiftSplitter;
import com.rs.game.content.skills.dungeoneering.npcs.LexicusRunewright;
import com.rs.game.content.skills.dungeoneering.npcs.LuminscentIcefiend;
import com.rs.game.content.skills.dungeoneering.npcs.MastyxTrap;
import com.rs.game.content.skills.dungeoneering.npcs.NecroLord;
import com.rs.game.content.skills.dungeoneering.npcs.NightGazerKhighorahk;
import com.rs.game.content.skills.dungeoneering.npcs.Rammernaut;
import com.rs.game.content.skills.dungeoneering.npcs.RuneboundBehemoth;
import com.rs.game.content.skills.dungeoneering.npcs.Sagittare;
import com.rs.game.content.skills.dungeoneering.npcs.ShadowForgerIhlakhizan;
import com.rs.game.content.skills.dungeoneering.npcs.SkeletalAdventurer;
import com.rs.game.content.skills.dungeoneering.npcs.Stomp;
import com.rs.game.content.skills.dungeoneering.npcs.ToKashBloodChiller;
import com.rs.game.content.skills.dungeoneering.npcs.UnholyCrossbearer;
import com.rs.game.content.skills.dungeoneering.npcs.WarpedGulega;
import com.rs.game.content.skills.dungeoneering.npcs.WorldGorgerShukarhazh;
import com.rs.game.content.skills.dungeoneering.npcs.YkLagorThunderous;
import com.rs.game.content.skills.dungeoneering.npcs.bosses.DungeonBoss;
import com.rs.game.content.skills.dungeoneering.npcs.bosses.asteafrostweb.AsteaFrostweb;
import com.rs.game.content.skills.dungeoneering.npcs.bosses.balak.BalLakThePummeler;
import com.rs.game.content.skills.dungeoneering.npcs.bosses.blink.Blink;
import com.rs.game.content.skills.dungeoneering.npcs.bosses.bulwark.BulwarkBeast;
import com.rs.game.content.skills.dungeoneering.npcs.misc.DungeonFishSpot;
import com.rs.game.content.skills.dungeoneering.npcs.misc.DungeonHunterNPC;
import com.rs.game.content.skills.dungeoneering.rooms.BossRoom;
import com.rs.game.content.skills.dungeoneering.rooms.HandledPuzzleRoom;
import com.rs.game.content.skills.dungeoneering.rooms.StartRoom;
import com.rs.game.content.skills.dungeoneering.rooms.puzzles.PoltergeistRoom;
import com.rs.game.content.skills.dungeoneering.rooms.puzzles.PoltergeistRoom.Poltergeist;
import com.rs.game.content.skills.dungeoneering.skills.DungeoneeringFishing;
import com.rs.game.content.skills.dungeoneering.skills.DungeoneeringMining;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.Skill;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub;
import com.rs.game.model.object.GameObject;
import com.rs.game.model.object.OwnedObject;
import com.rs.game.region.RegionBuilder.DynamicRegionReference;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;



public class DungeonManager {

	private static final Map<Object, DungeonManager> cachedDungeons = Collections.synchronizedMap(new HashMap<Object, DungeonManager>());
	public static final AtomicLong keyMaker = new AtomicLong();

	private DungeonPartyManager party;
	private Dungeon dungeon;
	private VisibleRoom[][] visibleMap;
	private DynamicRegionReference region;
	private int stage; //0 - not loaded. 1 - loaded. 2 - new one not loaded, old one loaded(rewards screen)
	private RewardsTimer rewardsTimer;
	private DestroyTimer destroyTimer;
	private long time;
	private List<KeyDoors> keyList;
	private List<OwnedObject> farmingPatches;
	private String key;

	private WorldTile groupGatestone;
	private List<MastyxTrap> mastyxTraps;

	//force saving deaths
	private Map<String, Integer> partyDeaths;

	private boolean tutorialMode;

	private DungeonBoss temporaryBoss; //must for gravecreeper, cuz... it gets removed from npc list :/

	// 7554
	public DungeonManager(DungeonPartyManager party) {
		this.party = party;
		tutorialMode = party.getMaxComplexity() < 6;
		load();
		keyList = new CopyOnWriteArrayList<>();
		farmingPatches = new CopyOnWriteArrayList<>();
		mastyxTraps = new CopyOnWriteArrayList<>();
		partyDeaths = new ConcurrentHashMap<>();
	}

	public void clearKeyList() {
		for (Player player : party.getTeam()) {
			player.getPackets().sendRunScriptReverse(6072);
			player.getPackets().sendVarc(1875, 0); //forces refresh
		}
	}

	public void setKey(KeyDoors key, boolean add) {
		if (add) {
			keyList.add(key);
			for (Player player : party.getTeam())
				player.sendMessage("<col=D2691E>Your party found a key: " + ItemDefinitions.getDefs(key.getKeyId()).getName());
		} else {
			keyList.remove(key);
			for (Player player : party.getTeam())
				player.sendMessage("<col=D2691E>Your party used a key: " + ItemDefinitions.getDefs(key.getKeyId()).getName());
		}
		for (Player player : party.getTeam()) {
			player.getPackets().sendVarc(1812 + key.getIndex(), add ? 1 : 0);
			if (key.getIndex() != 64)
				player.getPackets().sendVarc(1875, keyList.contains(KeyDoors.GOLD_SHIELD) ? 1 : 0);
		}
	}

	public boolean isAtBossRoom(WorldTile tile) {
		return isAtBossRoom(tile, -1, -1, false);
	}

	public boolean isAtBossRoom(WorldTile tile, int x, int y, boolean check) {
		Room room = getRoom(getCurrentRoomReference(tile));
		if (room == null || !(room.getRoom() instanceof BossRoom))
			return false;
		if (check) {
			BossRoom bRoom = (BossRoom) room.getRoom();
			if (x != bRoom.getChunkX() || y != bRoom.getChunkY())
				return false;
		}
		return true;
	}

	public boolean isBossOpen() {
		for (VisibleRoom[] element : visibleMap)
			for (VisibleRoom room : element) {
				if (room == null || !room.isLoaded())
					continue;
				if (isAtBossRoom(getRoomCenterTile(room.reference)))
					return true;
			}
		return false;
	}

	/*
	 * dont use
	 */
	public void refreshKeys(Player player) {
		for (KeyDoors key : keyList)
			player.getPackets().sendVarc(1812 + key.getIndex(), 1);
		player.getPackets().sendVarc(1875, keyList.contains(KeyDoors.GOLD_SHIELD) ? 1 : 0);
	}

	public boolean hasKey(KeyDoors key) {
		return keyList.contains(key);
	}

	/*
	 * when dung ends to make sure no1 dies lo, well they can die but still..
	 */
	public void clearGuardians() {
		for (VisibleRoom[] element : visibleMap)
			for (VisibleRoom element2 : element)
				if (element2 != null)
					element2.forceRemoveGuardians();
	}

	public int getVisibleRoomsCount() {
		int count = 0;
		for (VisibleRoom[] element : visibleMap)
			for (VisibleRoom element2 : element)
				if (element2 != null)
					count++;
		return count;
	}

	public int getVisibleBonusRoomsCount() {
		int count = 0;
		for (int x = 0; x < visibleMap.length; x++)
			for (int y = 0; y < visibleMap[x].length; y++)
				if (visibleMap[x][y] != null && !dungeon.getRoom(new RoomReference(x, y)).isCritPath())
					count++;
		return count;
	}

	public int getLevelModPerc() {
		int totalGuardians = 0;
		int killedGuardians = 0;

		for (VisibleRoom[] element : visibleMap)
			for (VisibleRoom element2 : element)
				if (element2 != null) {
					totalGuardians += element2.getGuardiansCount();
					killedGuardians += element2.getKilledGuardiansCount();
				}

		return totalGuardians == 0 ? 100 : killedGuardians * 100 / totalGuardians;
	}

	public boolean enterRoom(Player player, int x, int y) {
		if (x < 0 || y < 0 || x >= visibleMap.length || y >= visibleMap[0].length)
			return false;
		RoomReference roomReference = getCurrentRoomReference(player.getTile());
		player.lock(0);
		if (visibleMap[x][y] == null) {
			loadRoom(x, y);
			return false;
		}
		if (!visibleMap[x][y].isLoaded())
			return false;
		int xOffset = x - roomReference.getRoomX();
		int yOffset = y - roomReference.getRoomY();
		player.setNextWorldTile(new WorldTile(player.getX() + xOffset * 3, player.getY() + yOffset * 3, 0));
		playMusic(player, new RoomReference(x, y));
		return true;
	}

	public void loadRoom(int x, int y) {
		loadRoom(new RoomReference(x, y));
	}

	public void loadRoom(final RoomReference reference) {
		final Room room = dungeon.getRoom(reference);
		if (room == null)
			return;
		VisibleRoom vr;
		if (room.getRoom() instanceof HandledPuzzleRoom pr)
			vr = pr.createVisibleRoom();
		else
			vr = new VisibleRoom();
		visibleMap[reference.getRoomX()][reference.getRoomY()] = vr;
		vr.init(this, reference, party.getFloorType(), room.getRoom());
		openRoom(room, reference, visibleMap[reference.getRoomX()][reference.getRoomY()]);
	}

	public boolean isDestroyed() {
		return dungeon == null;
	}

	public int getBossLevel() {
		return (int) (party.getAverageCombatLevel() * 1.5);
	}

	public void openRoom(final Room room, final RoomReference reference, final VisibleRoom visibleRoom) {
		int chunkOffX = reference.getRoomX() * 2;
		int chunkOffY = reference.getRoomY() * 2;
		region.copy2x2ChunkSquare(chunkOffX, chunkOffY, room.getChunkX(party.getComplexity()), room.getChunkY(party.getFloorType()), room.getRotation(), new int[] { 0, 1 }, () -> {
			int regionId = region.getRegionId();
			for (Player player : party.getTeam()) {
				player.setForceNextMapLoadRefresh(true);
				player.loadMapRegions();
			}
			World.executeAfterLoadRegion(regionId, () -> {
				if (isDestroyed())
					return;
				room.openRoom(DungeonManager.this, reference);
				visibleRoom.openRoom();
				for (int i = 0; i < room.getRoom().getDoorDirections().length; i++) {
					Door door = room.getDoor(i);
					if (door == null)
						continue;
					int rotation = (room.getRoom().getDoorDirections()[i] + room.getRotation()) & 0x3;
					if (door.getType() == DungeonConstants.KEY_DOOR) {
						KeyDoors keyDoor = KeyDoors.values()[door.getId()];
						setDoor(reference, keyDoor.getObjectId(), keyDoor.getDoorId(party.getFloorType()), rotation);
					} else if (door.getType() == DungeonConstants.GUARDIAN_DOOR) {
						setDoor(reference, -1, DungeonConstants.DUNGEON_GUARDIAN_DOORS[party.getFloorType()], rotation);
						if (visibleRoom.roomCleared())  //remove referene since done
							room.setDoor(i, null);
					} else if (door.getType() == DungeonConstants.SKILL_DOOR) {
						SkillDoors skillDoor = SkillDoors.values()[door.getId()];
						int type = party.getFloorType();
						int closedId = skillDoor.getClosedObject(type);
						int openId = skillDoor.getOpenObject(type);
						setDoor(reference, openId == -1 ? closedId : -1, openId != -1 ? closedId : -1, rotation);
					}
				}
				if (room.getRoom().allowResources())
					setResources(room, reference, chunkOffX, chunkOffY);

				if (room.getDropId() != -1)
					setKey(room, reference);
				visibleRoom.setLoaded();
			});
		});
	}

	public void setDoor(RoomReference reference, int lockObjectId, int doorObjectId, int rotation) {
		if (lockObjectId != -1) {
			int[] xy = DungeonManager.translate(1, 7, rotation, 1, 2, 0);
			World.spawnObject(new GameObject(lockObjectId, ObjectType.SCENERY_INTERACT, rotation, region.getLocalX(reference.getBaseX() + xy[0]), region.getLocalY(reference.getBaseY() + xy[1]), 0));
		}
		if (doorObjectId != -1) {
			int[] xy = DungeonManager.translate(0, 7, rotation, 1, 2, 0);
			World.spawnObject(new GameObject(doorObjectId, ObjectType.SCENERY_INTERACT, rotation, region.getLocalX(reference.getBaseX() + xy[0]), region.getLocalY(reference.getBaseY() + xy[1]), 0));
		}
	}

	public void setKey(Room room, RoomReference reference) {
		int[] loc = room.getRoom().getKeySpot();
		if (loc != null) {
			spawnItem(reference, new Item(room.getDropId()), loc[0], loc[1]);
			return;
		}

		spawnItem(reference, new Item(room.getDropId()), 7, 1);
	}

	public void setResources(Room room, RoomReference reference, int chunkOffX, int chunkOffY) {
		if (party.getComplexity() >= 5 && Utils.random(3) == 0)
			//sets thief chest
			for (int i = 0; i < DungeonConstants.SET_RESOURCES_MAX_TRY; i++) {
				int rotation = Utils.random(DungeonConstants.WALL_BASE_X_Y.length);
				int[] b = DungeonConstants.WALL_BASE_X_Y[rotation];
				int x = b[0] + Utils.random(b[2]);
				int y = b[1] + Utils.random(b[3]);
				if (((x >= 6 && x <= 8) && b[2] != 0) || ((y >= 6 && y <= 8) && b[3] != 0))
					continue;
				if (!World.floorFree(0, region.getLocalX(chunkOffX, x), region.getLocalY(chunkOffY, y)) || !World.floorAndWallsFree(0, region.getLocalX(chunkOffX, x - Utils.ROTATION_DIR_X[((rotation + 3) & 0x3)]), region.getLocalY(chunkOffY, y - Utils.ROTATION_DIR_Y[((rotation + 3) & 0x3)]), 1))
					continue;
				room.setThiefChest(Utils.random(10));
				World.spawnObject(new GameObject(DungeonConstants.THIEF_CHEST_LOCKED[party.getFloorType()], ObjectType.SCENERY_INTERACT, ((rotation + 3) & 0x3), region.getLocalX(chunkOffX, x), region.getLocalY(chunkOffY, y), 0));
				if (Settings.getConfig().isDebug())
					System.out.println("Added chest spot.");
				break;
			}
		if (party.getComplexity() >= 4 && Utils.random(3) == 0)
			//sets flower
			for (int i = 0; i < DungeonConstants.SET_RESOURCES_MAX_TRY; i++) {
				int rotation = Utils.random(DungeonConstants.WALL_BASE_X_Y.length);
				int[] b = DungeonConstants.WALL_BASE_X_Y[rotation];
				int x = b[0] + Utils.random(b[2]);
				int y = b[1] + Utils.random(b[3]);
				if (((x >= 6 && x <= 8) && b[2] != 0) || ((y >= 6 && y <= 8) && b[3] != 0))
					continue;
				if (!World.floorFree(0, region.getLocalX(chunkOffX, x), region.getLocalY(chunkOffY, y)))
					continue;
				World.spawnObject(new GameObject(DungeonUtils.getFarmingResource(Utils.random(10), party.getFloorType()), ObjectType.SCENERY_INTERACT, rotation, region.getLocalX(chunkOffX, x), region.getLocalY(chunkOffY, y), 0));
				if (Settings.getConfig().isDebug())
					System.out.println("Added flower spot.");
				break;
			}
		if (party.getComplexity() >= 3 && Utils.random(3) == 0)
			//sets rock
			for (int i = 0; i < DungeonConstants.SET_RESOURCES_MAX_TRY; i++) {
				int rotation = Utils.random(DungeonConstants.WALL_BASE_X_Y.length);
				int[] b = DungeonConstants.WALL_BASE_X_Y[rotation];
				int x = b[0] + Utils.random(b[2]);
				int y = b[1] + Utils.random(b[3]);
				if (((x >= 6 && x <= 8) && b[2] != 0) || ((y >= 6 && y <= 8) && b[3] != 0))
					continue;
				if (!World.floorFree(0, region.getLocalX(chunkOffX, x), region.getLocalY(chunkOffY, y)))
					continue;
				World.spawnObject(new GameObject(DungeonUtils.getMiningResource(Utils.random(DungeoneeringMining.DungeoneeringRocks.values().length), party.getFloorType()), ObjectType.SCENERY_INTERACT, rotation, region.getLocalX(chunkOffX, x), region.getLocalY(chunkOffY, y), 0));
				if (Settings.getConfig().isDebug())
					System.out.println("Added rock spot.");
				break;
			}
		if (party.getComplexity() >= 2 && Utils.random(3) == 0)
			//sets tree
			for (int i = 0; i < DungeonConstants.SET_RESOURCES_MAX_TRY; i++) {
				int rotation = Utils.random(DungeonConstants.WALL_BASE_X_Y.length);
				int[] b = DungeonConstants.WALL_BASE_X_Y[rotation];
				int x = b[0] + Utils.random(b[2]);
				int y = b[1] + Utils.random(b[3]);
				if (((x >= 6 && x <= 8) && b[2] != 0) || ((y >= 6 && y <= 8) && b[3] != 0))
					continue;
				if (!World.wallsFree(0, region.getLocalX(chunkOffX, x), region.getLocalY(chunkOffY, y)) || !World.floorFree(0, region.getLocalX(chunkOffX, x), region.getLocalY(chunkOffY, y)))
					continue;
				x -= Utils.ROTATION_DIR_X[rotation];
				y -= Utils.ROTATION_DIR_Y[rotation];
				if (Settings.getConfig().isDebug())
					System.out.println("Added tree spot");
				World.spawnObject(new GameObject(DungeonUtils.getWoodcuttingResource(Utils.random(10), party.getFloorType()), ObjectType.SCENERY_INTERACT, rotation, region.getLocalX(chunkOffX, x), region.getLocalY(chunkOffY, y), 0));
				break;
			}
		if (party.getComplexity() >= 2) { //sets fish spot
			List<int[]> fishSpots = new ArrayList<>();
			for (int x = 0; x < 16; x++)
				for (int y = 0; y < 16; y++) {
					GameObject o = World.getObjectWithType(new WorldTile(region.getLocalX(chunkOffX, x), region.getLocalY(chunkOffY, y), 0), ObjectType.SCENERY_INTERACT);
					if (o == null || o.getId() != DungeonConstants.FISH_SPOT_OBJECT_ID)
						continue;
					fishSpots.add(new int[]
							{ x, y });
				}
			if (!fishSpots.isEmpty()) {
				int[] spot = fishSpots.get(Utils.random(fishSpots.size()));
				spawnNPC(DungeonConstants.FISH_SPOT_NPC_ID, room.getRotation(), new WorldTile(region.getLocalX(chunkOffX, spot[0]), region.getLocalY(chunkOffY, spot[1]), 0), reference, DungeonConstants.FISH_SPOT_NPC);
				if (Settings.getConfig().isDebug())
					System.out.println("Added fish spot");
			}
		}
	}

	public WorldTile getRoomCenterTile(RoomReference reference) {
		return getRoomBaseTile(reference).transform(8, 8, 0);
	}

	public WorldTile getRoomBaseTile(RoomReference reference) {
		return new WorldTile((region.getBaseX() + reference.getBaseX()), (region.getBaseY() + reference.getBaseY()), 0);
	}

	public RoomReference getCurrentRoomReference(WorldTile tile) {
		return new RoomReference((tile.getChunkX() - region.getBaseChunkX()) / 2, ((tile.getChunkY() - region.getBaseChunkY()) / 2));
	}

	public Room getRoom(RoomReference reference) {
		return dungeon == null ? null : dungeon.getRoom(reference);
	}

	public VisibleRoom getVisibleRoom(RoomReference reference) {
		if (reference.getRoomX() >= visibleMap.length || reference.getRoomY() >= visibleMap[0].length)
			return null;
		return visibleMap[reference.getRoomX()][reference.getRoomY()];
	}

	public WorldTile getHomeTile() {
		return getRoomCenterTile(dungeon.getStartRoomReference());
	}

	public void telePartyToRoom(RoomReference reference) {
		WorldTile tile = getRoomCenterTile(reference);
		for (Player player : party.getTeam()) {
			player.setNextWorldTile(tile);
			playMusic(player, reference);
		}
	}

	public void playMusic(Player player, RoomReference reference) {
		if (reference.getRoomX() >= visibleMap.length || reference.getRoomY() >= visibleMap[reference.getRoomX()].length)
			return;
		player.getMusicsManager().forcePlayMusic(visibleMap[reference.getRoomX()][reference.getRoomY()].getMusicId());
	}

	public void linkPartyToDungeon() {
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				for (Player player : party.getTeam()) {
					resetItems(player, false, false);
					player.reset();
					sendSettings(player);
					if (party.getComplexity() >= 5 && party.isLeader(player))
						player.getInventory().addItem(new Item(DungeonConstants.GROUP_GATESTONE));
					removeMark(player);
					sendStartItems(player);
					player.sendMessage("");
					player.sendMessage("-Welcome to Daemonheim-");
					player.sendMessage("Floor <col=641d9e>" + party.getFloor() + "    <col=ffffff>Complexity <col=641d9e>" + party.getComplexity());
					String[] sizeNames = { "Small", "Medium", "Large", "Test" };
					player.sendMessage("Dungeon Size: " + "<col=641d9e>" + sizeNames[party.getSize()]);
					player.sendMessage("Party Size:Difficulty <col=641d9e>" + party.getTeam().size() + ":" + party.getDificulty());
					if (party.isGuideMode())
						player.sendMessage("<col=641d9e>Guide Mode ON");
					player.sendMessage("");
					player.lock(1);
				}
				resetGatestone();
			}
		});
	}

	public void setTableItems(RoomReference room) {
		addItemToTable(room, new Item(16295)); //novite pickaxe, cuz of boss aswell so 1+
		if (party.getComplexity() >= 2) {
			addItemToTable(room, new Item(DungeonConstants.RUSTY_COINS, 5000 + Utils.random(10000)));
			addItemToTable(room, new Item(17678)); //tinderbox
			addItemToTable(room, new Item(16361)); //novite hatcher
			addItemToTable(room, new Item(17794)); //fish rods
		}
		if (party.getComplexity() >= 3) { //set weap/gear in table
			int rangeTier = DungeonUtils.getTier(party.getMaxLevel(Constants.RANGE));
			if (rangeTier > 8)
				rangeTier = 8;
			addItemToTable(room, new Item(DungeonUtils.getArrows(1 + Utils.random(rangeTier)), 90 + Utils.random(30))); //arround 100 arrows, type random up to best u can, limited to tier 8
			addItemToTable(room, new Item(DungeonConstants.RUNES[0], 90 + Utils.random(30))); //arround 100 air runes
			addItemToTable(room, new Item(DungeonConstants.RUNE_ESSENCE, 90 + Utils.random(30))); //arround 100 essence
			addItemToTable(room, new Item(17754)); //knife
			addItemToTable(room, new Item(17883)); //hammer
			if (party.getComplexity() >= 4)
				addItemToTable(room, new Item(17446)); //needle
		}
		for (@SuppressWarnings("unused")
		Player player : party.getTeam()) {
			for (int i = 0; i < 7 + Utils.random(4); i++)
				//9 food
				addItemToTable(room, new Item(DungeonUtils.getFood(1 + Utils.random(8))));
			if (party.getComplexity() >= 3)
				//set weap/gear in table
				for (int i = 0; i < 1 + Utils.random(3); i++)
					//1 up to 3 pieces of gear or weap
					addItemToTable(room, new Item(DungeonUtils.getRandomGear(1 + Utils.random(8)))); //arround 100 essence
		}
	}

	public void addItemToTable(RoomReference room, Item item) {
		int slot = Utils.random(10); //10 spaces for items
		if (slot < 6)
			spawnItem(room, item, 9 + Utils.random(3), 10 + Utils.random(2));
		else if (slot < 8)
			spawnItem(room, item, 10 + Utils.random(2), 14);
		else
			spawnItem(room, item, 14, 10 + Utils.random(2));
	}

	public void sendStartItems(Player player) {
		if (party.getComplexity() == 1)
			player.simpleDialogue("<col=0000FF>Complexity 1", "Combat only", "Armour and weapons allocated", "No shop stock");
		else if (party.getComplexity() == 2)
			player.simpleDialogue("<col=0000FF>Complexity 2", "+ Fishing, Woodcutting, Firemaking, Cooking", "Armour and weapons allocated", "Minimal shop stock");
		else if (party.getComplexity() == 3)
			player.simpleDialogue("<col=0000FF>Complexity 3", "+ Mining, Smithing weapons, Fletching, Runecrafting", "Armour allocated", "Increased shop stock");
		else if (party.getComplexity() == 4)
			player.simpleDialogue("<col=0000FF>Complexity 4", "+ Smithing armour, Hunter, Farming textiles, Crafting", "Increased shop stock");
		else if (party.getComplexity() == 5)
			player.simpleDialogue("<col=0000FF>Complexity 5", "All skills included", "+ Farming seeds, Herblore, Thieving, Summoning", "Complete shop stock", "Challenge rooms + skill doors");
		if (party.getComplexity() <= 3) {
			int defenceTier = DungeonUtils.getTier(player.getSkills().getLevelForXp(Constants.DEFENSE));
			if (defenceTier > 8)
				defenceTier = 8;
			player.getInventory().addItem(new Item(DungeonUtils.getPlatebody(defenceTier)));
			player.getInventory().addItem(new Item(DungeonUtils.getPlatelegs(defenceTier, player.getAppearance().isMale())));
			if (party.getComplexity() <= 2) {
				int attackTier = DungeonUtils.getTier(player.getSkills().getLevelForXp(Constants.ATTACK));
				if (attackTier > 8)
					attackTier = 8;
				player.getInventory().addItem(new Item(DungeonUtils.getRapier(defenceTier)));
				player.getInventory().addItem(new Item(DungeonUtils.getBattleaxe(defenceTier)));
			}
			int magicTier = DungeonUtils.getTier(player.getSkills().getLevelForXp(Constants.MAGIC));
			if (magicTier > 8)
				magicTier = 8;
			player.getInventory().addItem(new Item(DungeonUtils.getRobeTop(defenceTier < magicTier ? defenceTier : magicTier)));
			player.getInventory().addItem(new Item(DungeonUtils.getRobeBottom(defenceTier < magicTier ? defenceTier : magicTier)));
			if (party.getComplexity() <= 2) {
				player.getInventory().addItem(new Item(DungeonConstants.RUNES[0], 90 + Utils.random(30)));
				player.getInventory().addItem(new Item(DungeonUtils.getStartRunes(player.getSkills().getLevelForXp(Constants.MAGIC)), 90 + Utils.random(30)));
				player.getInventory().addItem(new Item(DungeonUtils.getElementalStaff(magicTier)));
			}
			int rangeTier = DungeonUtils.getTier(player.getSkills().getLevelForXp(Constants.RANGE));
			if (rangeTier > 8)
				rangeTier = 8;
			player.getInventory().addItem(new Item(DungeonUtils.getLeatherBody(defenceTier < rangeTier ? defenceTier : rangeTier)));
			player.getInventory().addItem(new Item(DungeonUtils.getChaps(defenceTier < rangeTier ? defenceTier : rangeTier)));
			if (party.getComplexity() <= 2) {
				player.getInventory().addItem(new Item(DungeonUtils.getShortbow(rangeTier)));
				player.getInventory().addItem(new Item(DungeonUtils.getArrows(rangeTier), 90 + Utils.random(30)));
			}
		}
	}

	public void sendSettings(Player player) {
		if (player.getControllerManager().getController() instanceof DungeonController ctrl)
			ctrl.reset();
		else {
			player.getControllerManager().startController(new DungeonController(DungeonManager.this));
			player.setLargeSceneView(true);
			player.getCombatDefinitions().setSpellbook(Spellbook.DUNGEONEERING);
			player.getPackets().sendVarc(1725, 11);
			setWorldMap(player, true);
		}
		player.getPackets().sendVarc(234, 3);
		player.getInterfaceManager().sendSub(Sub.TAB_QUEST, 939);
		player.getDungManager().refresh();
		player.getInventory().deleteItem(new Item(15707, 28));
		sendRing(player);
		sendBindItems(player);
		wearInventory(player);
	}

	public void rejoinParty(Player player) {
		player.stopAll();
		player.lock(2);
		party.add(player);
		sendSettings(player);
		refreshKeys(player);
		player.setNextWorldTile(getHomeTile());
		playMusic(player, dungeon.getStartRoomReference());
	}

	public void sendBindItems(Player player) {
		Item ammo = player.getDungManager().getBindedAmmo();
		if (ammo != null)
			player.getInventory().addItem(ammo);
		for (Item item : player.getDungManager().getBindedItems().array()) {
			if (item == null)
				continue;
			player.getInventory().addItem(item);
		}
	}

	public void resetItems(Player player, boolean drop, boolean logout) {
		if (drop) {
			for (Item item : player.getEquipment().getItemsCopy()) {
				if (item == null || item.getName().contains("(b)") || item.getName().contains("kinship") || !DungManager.isBannedDungItem(item))
					continue;
				World.addGroundItem(item, new WorldTile(player.getTile()));
			}
			for (Item item : player.getInventory().getItems().array()) {
				if (item == null || item.getName().contains("(b)") || item.getName().contains("kinship") || !DungManager.isBannedDungItem(item))
					continue;
				World.addGroundItem(item, new WorldTile(player.getTile()));
				if (hasLoadedNoRewardScreen() & item.getId() == DungeonConstants.GROUP_GATESTONE)
					setGroupGatestone(new WorldTile(player.getTile()));
			}
		}
		for (Item item : player.getInventory().getItems().array()) {
			if (DungManager.isBannedDungItem(item))
				player.getInventory().deleteItem(item);
		}
		for (Item item : player.getEquipment().getItemsCopy()) {
			if (DungManager.isBannedDungItem(item))
				player.getEquipment().deleteItem(item.getId(), item.getAmount());
		}
		player.getInventory().addItem(15707, 1);
		if (!logout)
			player.getAppearance().generateAppearanceData();
	}

	public void sendRing(Player player) {
		if (player.getInventory().containsItem(15707, 1))
			player.getInventory().deleteItem(15707, 1);
		if (player.getEquipment().containsOneItem(15707, 1))
			player.getEquipment().deleteItem(15707, 1);
		player.getInventory().addItem(player.getDungManager().getActivePerk() != null ? new Item(player.getDungManager().getActivePerk().getItemId()) : new Item(15707));
	}

	public void wearInventory(Player player) {
		boolean worn = false;
		for (int slotId = 0; slotId < 28; slotId++) {
			Item item = player.getInventory().getItem(slotId);
			if (item == null)
				continue;
			Equipment.sendWear(player, slotId, item.getId());
		}
		if (worn) {
			player.getAppearance().generateAppearanceData();
			player.getInventory().getItems().shift();
			player.getInventory().refresh();
		}

	}

	public int getCombatLevelMonster() {
		return (int) (party.getAverageCombatLevel() * DungeonConstants.NPC_COMBAT_LEVEL_COMPLEXITY_MUL[party.getComplexity()-1]);
	}

	public int getMaxCombatLevelMonster() {
		return (int) (party.getAverageCombatLevel() * 1.4 * DungeonConstants.NPC_COMBAT_LEVEL_COMPLEXITY_MUL[party.getComplexity()-1]);
	}

	public void spawnRandomNPCS(RoomReference reference) {
		int floorType = party.getFloorType();
		int combatTotal = (int) (party.getCombatLevel() * 1.2 * DungeonConstants.NPC_COMBAT_LEVEL_COMPLEXITY_MUL[party.getComplexity()-1]);
		int maxLevel = getMaxCombatLevelMonster();
		int numMonsters = Utils.random(1, 3+party.getTeam().size());

		List<Integer> npcs = DungeonUtils.generateRandomMonsters(floorType, maxLevel, combatTotal, numMonsters);

		for (int i : npcs) {
			GuardianMonster m = GuardianMonster.forId(i);
			NPCDefinitions defs = NPCDefinitions.getDefs(i);
			spawnNPC(reference, i, 2 + Utils.getRandomInclusive(13-defs.size), 2 + Utils.getRandomInclusive(13-defs.size), true, m.name().contains("FORGOTTEN_") ? DungeonConstants.FORGOTTEN_WARRIOR : DungeonConstants.GUARDIAN_NPC);
		}
		if (!npcs.isEmpty() && Utils.random(8) == 0) { //slayer creature
			int creature = DungeonSlayerNPC.getSlayerCreature(party);
			if (creature != -1)
				spawnNPC(reference, creature, 2 + Utils.getRandomInclusive(13), 2 + Utils.getRandomInclusive(13), true, DungeonConstants.SLAYER_NPC);
		}
		if (Utils.random(4) == 0)
			spawnNPC(reference, DungeonUtils.getHunterCreature(), 2 + Utils.getRandomInclusive(13), 2 + Utils.getRandomInclusive(13), true, DungeonConstants.HUNTER_NPC);
	}

	public int[] getRoomPos(WorldTile tile) {
		int chunkX = tile.getX() / 16 * 2;
		int chunkY = tile.getY() / 16 * 2;
		int x = tile.getX() - chunkX * 8;
		int y = tile.getY() - chunkY * 8;
		Room room = getRoom(getCurrentRoomReference(tile));
		if (room == null)
			return null;
		return DungeonManager.translate(x, y, (4 - room.getRotation()) & 0x3, 1, 1, 0);
	}

	public static int[] translate(int x, int y, int mapRotation, int sizeX, int sizeY, int objectRotation) {
		int[] coords = new int[2];
		if ((objectRotation & 0x1) == 1) {
			int prevSizeX = sizeX;
			sizeX = sizeY;
			sizeY = prevSizeX;
		}
		if (mapRotation == 0) {
			coords[0] = x;
			coords[1] = y;
		} else if (mapRotation == 1) {
			coords[0] = y;
			coords[1] = 15 - x - (sizeX - 1);
		} else if (mapRotation == 2) {
			coords[0] = 15 - x - (sizeX - 1);
			coords[1] = 15 - y - (sizeY - 1);
		} else if (mapRotation == 3) {
			coords[0] = 15 - y - (sizeY - 1);
			coords[1] = x;
		}
		return coords;
	}

	public DungeonNPC spawnNPC(RoomReference reference, int id, int x, int y) {
		return spawnNPC(reference, id, x, y, false, DungeonConstants.NORMAL_NPC);
	}

	public DungeonNPC spawnNPC(final RoomReference reference, final int id, int x, int y, boolean check, final int type) {
		final int rotation = dungeon.getRoom(reference).getRotation();
		final int size = NPCDefinitions.getDefs(id).size;
		int[] coords = translate(x, y, rotation, size, size, 0);
		WorldTile tile = region.getLocalTile((reference.getBaseX()) + coords[0], (reference.getBaseY()) + coords[1]);
		if (check && !World.floorAndWallsFree(tile, size)) {
			List<WorldTile> tiles = DungeonUtils.getRandomOrderCoords(size);
			for (WorldTile t : tiles) {
				coords = translate(t.getX(), t.getY(), rotation, size, size, 0);
				tile = region.getLocalTile((reference.getBaseX()) + coords[0], (reference.getBaseY()) + coords[1]);
				if (World.floorAndWallsFree(tile, size))
					return spawnNPC(id, rotation, tile, reference, type);
			}
		}
		return spawnNPC(id, rotation, tile, reference, type);
	}

	public GameObject spawnObject(RoomReference reference, int id, ObjectType type, int rotation, int x, int y) {
		if (dungeon == null || dungeon.getRoom(reference) == null)
			return null;
		final int mapRotation = dungeon.getRoom(reference).getRotation();
		ObjectDefinitions defs = ObjectDefinitions.getDefs(id);
		int[] coords = translate(x, y, mapRotation, defs.sizeX, defs.sizeY, rotation);
		GameObject object = new GameObject(id, type, (rotation + mapRotation) & 0x3, region.getLocalX(reference.getBaseX() + coords[0]), region.getLocalY(reference.getBaseY() + coords[1]), 0);
		World.spawnObject(object);
		return object;
	}

	public GameObject spawnObjectForMapRotation(RoomReference reference, int id, ObjectType type, int rotation, int x, int y, int mapRotation) {
		ObjectDefinitions defs = ObjectDefinitions.getDefs(id);
		int[] coords = translate(x, y, mapRotation, defs.sizeX, defs.sizeY, rotation);
		GameObject object = new GameObject(id, type, (rotation + mapRotation) & 0x3, region.getLocalX(reference.getBaseX() + coords[0]), region.getLocalY(reference.getBaseY() + coords[1]), 0);
		World.spawnObject(object);
		return object;
	}

	public GameObject spawnObjectTemporary(RoomReference reference, int id, ObjectType type, int rotation, int x, int y, int ticks) {
		final int mapRotation = dungeon.getRoom(reference).getRotation();
		ObjectDefinitions defs = ObjectDefinitions.getDefs(id);
		int[] coords = translate(x, y, mapRotation, defs.sizeX, defs.sizeY, rotation);
		GameObject object = new GameObject(id, type, (rotation + mapRotation) & 0x3, region.getLocalX(reference.getBaseX() + coords[0]), region.getLocalY(reference.getBaseY() + coords[1]), 0);
		World.spawnObjectTemporary(object, ticks);
		return object;
	}

	public void removeObject(RoomReference reference, int id, ObjectType type, int rotation, int x, int y) {
		final int mapRotation = dungeon.getRoom(reference).getRotation();
		ObjectDefinitions defs = ObjectDefinitions.getDefs(id);
		int[] coords = translate(x, y, mapRotation, defs.sizeX, defs.sizeY, rotation);
		World.removeObject(new GameObject(id, type, (rotation + mapRotation) & 0x3, region.getLocalX(reference.getBaseX() + coords[0]), region.getLocalY(reference.getBaseY() + coords[1]), 0));
	}

	public GameObject getObject(RoomReference reference, int id, int x, int y) {
		final int mapRotation = dungeon.getRoom(reference).getRotation();
		ObjectDefinitions defs = ObjectDefinitions.getDefs(id);
		int[] coords = translate(x, y, mapRotation, defs.sizeX, defs.sizeY, 0);
		return World.getObjectWithId(new WorldTile(region.getLocalX(reference.getBaseX() + coords[0]), region.getLocalY(reference.getBaseY() + coords[1]), 0), id);
	}

	public GameObject getObjectWithType(RoomReference reference, int id, ObjectType type, int x, int y) {
		final int mapRotation = dungeon.getRoom(reference).getRotation();
		ObjectDefinitions defs = ObjectDefinitions.getDefs(id);
		int[] coords = translate(x, y, mapRotation, defs.sizeX, defs.sizeY, 0);
		return World.getObjectWithType(new WorldTile(region.getLocalX(reference.getBaseX() + coords[0]), region.getLocalY(reference.getBaseY() + coords[1]), 0), type);
	}

	public GameObject getObjectWithType(RoomReference reference, ObjectType type, int x, int y) {
		Room room = dungeon.getRoom(reference);
		if(room == null)
			return null;
		final int mapRotation = room.getRotation();
		int[] coords = translate(x, y, mapRotation, 1, 1, 0);
		return World.getObjectWithType(new WorldTile(region.getLocalX(reference.getBaseX() + coords[0]), region.getLocalY(reference.getBaseY() + coords[1]), 0), type);
	}

	public WorldTile getTile(RoomReference reference, int x, int y, int sizeX, int sizeY) {
		Room room = dungeon.getRoom(reference);
		if(room == null)
			return null;
		final int mapRotation = room.getRotation();
		int[] coords = translate(x, y, mapRotation, sizeX, sizeY, 0);
		return new WorldTile(region.getLocalX(reference.getBaseX() + coords[0]), region.getLocalY(reference.getBaseY() + coords[1]), 0);
	}

	public WorldTile getTile(RoomReference reference, int x, int y) {
		return getTile(reference, x, y, 1, 1);
	}

	public WorldTile getRotatedTile(RoomReference reference, int x, int y) {
		final int mapRotation = dungeon.getRoom(reference).getRotation();
		int[] coords = translate(x, y, mapRotation, 1, 1, 0);
		return new WorldTile(region.getLocalX(reference.getBaseX() + coords[0]), region.getLocalY(reference.getBaseY() + coords[1]), 0);
	}

	public void spawnItem(RoomReference reference, Item item, int x, int y) {
		final int mapRotation = dungeon.getRoom(reference).getRotation();
		int[] coords = translate(x, y, mapRotation, 1, 1, 0);
		World.addGroundItem(item, new WorldTile(region.getLocalX(reference.getBaseX() + coords[0]), region.getLocalY(reference.getBaseY() + coords[1]), 0));
	}

	public boolean isFloorFree(RoomReference reference, int x, int y) {
		final int mapRotation = dungeon.getRoom(reference).getRotation();
		int[] coords = translate(x, y, mapRotation, 1, 1, 0);
		return World.floorFree(0, region.getLocalX(reference.getBaseX() + coords[0]), region.getLocalY(reference.getBaseY() + coords[1]));
	}

	public WorldTile getRoomTile(RoomReference reference) {
		final int mapRotation = dungeon.getRoom(reference).getRotation();
		int[] coords = translate(0, 0, mapRotation, 1, 1, 0);
		return new WorldTile(region.getLocalX(reference.getBaseX() + coords[0]), region.getLocalY(reference.getBaseY() + coords[1]), 0);
	}

	public DungeonNPC spawnNPC(int id, int rotation, WorldTile tile, RoomReference reference, int type) {
		DungeonNPC n = null;
		if (type == DungeonConstants.BOSS_NPC) {
			if (id == 9965)
				n = new AsteaFrostweb(tile, this, reference);
			else if (id == 9948)
				n = new GluttonousBehemoth(tile, this, reference);
			else if (id == 9912)
				n = new LuminscentIcefiend(tile, this, reference);
			else if (id == 10059)
				n = new HobgoblinGeomancer(tile, this, reference);
			else if (id == 10040)
				n = new IcyBones(tile, this, reference);
			else if (id == 10024)
				n = new ToKashBloodChiller(tile, this, reference);
			else if (id == 10058)
				n = new DivineSkinweaver(id, tile, this, reference);
			else if (id >= 10629 && id <= 10693)
				n = new DungeonSkeletonBoss(id, tile, this, reference);
			else if (id == 10073)
				n = new BulwarkBeast(tile, this, reference);
			else if (id == 9767)
				n = new Rammernaut(tile, this, reference);
			else if (id == 9782)
				n = new Stomp(tile, this, reference);
			else if (id == 9898)
				n = new LakkTheRiftSplitter(tile, this, reference);
			else if (id == 9753)
				n = new Sagittare(tile, this, reference);
			else if (id == 9725)
				n = new NightGazerKhighorahk(tile, this, reference);
			else if (id == 9842)
				n = new LexicusRunewright(tile, this, reference);
			else if (id == 10128)
				n = new BalLakThePummeler(tile, this, reference);
			else if (id == 10143)
				n = new ShadowForgerIhlakhizan(tile, this, reference);
			else if (id == 11940 || id == 12044 || id == 11999)
				n = new SkeletalAdventurer(id == 11940 ? SkeletalAdventurer.MELEE : id == 11999 ? SkeletalAdventurer.MAGE : SkeletalAdventurer.RANGE, tile, this, reference);
			else if (id == 11812)
				n = new RuneboundBehemoth(tile, this, reference);
			else if (id == 11708)
				n = new Gravecreeper(tile, this, reference);
			else if (id == 11737 || id == 11895)
				n = new NecroLord(tile, this, reference);
			else if (id == 11925)
				n = new FleshspoilerHaasghenahk(tile, this, reference);
			else if (id == 11872)
				n = new YkLagorThunderous(tile, this, reference);
			else if (id == 12737)
				n = new WarpedGulega(tile, this, reference);
			else if (id == 12848)
				n = new Dreadnaut(tile, this, reference);
			else if (id == 12886)
				n = new HopeDevourer(tile, this, reference);
			else if (id == 12478)
				n = new WorldGorgerShukarhazh(tile, this, reference);
			else if (id == 12865)
				n = new Blink(tile, this, reference);
			else if (id == 12752)
				n = new KalGerWarmonger(tile, this, reference);
			else if (id == 10111)
				n = new UnholyCrossbearer(id, tile, this, reference);
			else
				n = new DungeonBoss(id, tile, this, reference);
		} else if (type == DungeonConstants.GUARDIAN_NPC) {
			n = new Guardian(id, tile, this, reference);
			visibleMap[reference.getRoomX()][reference.getRoomY()].addGuardian(n);
		} else if (type == DungeonConstants.FORGOTTEN_WARRIOR) {
			n = new ForgottenWarrior(id, tile, this, reference);
			visibleMap[reference.getRoomX()][reference.getRoomY()].addGuardian(n);
		} else if (type == DungeonConstants.FISH_SPOT_NPC)
			n = new DungeonFishSpot(id, tile, this, DungeoneeringFishing.Fish.values()[Utils.random(DungeoneeringFishing.Fish.values().length - 1)]);
		else if (type == DungeonConstants.SLAYER_NPC)
			n = new DungeonSlayerNPC(id, tile, this);
		else if (type == DungeonConstants.HUNTER_NPC)
			n = new DungeonHunterNPC(id, tile, this);
		else if (type == DungeonConstants.PUZZLE_NPC) {
			if (id == PoltergeistRoom.POLTERGEIST_ID)
				n = new Poltergeist(id, tile, this, reference);
		} else
			n = new DungeonNPC(id, tile, this);
		n.setFaceAngle(Utils.getAngleTo(Utils.ROTATION_DIR_X[(rotation + 3) & 0x3], Utils.ROTATION_DIR_Y[(rotation + 3) & 0x3]));
		return n;
	}

	public int getTargetLevel(int id, boolean boss, double multiplier) {
		double lvl = boss ? party.getCombatLevel() : party.getAverageCombatLevel();
		int randomize = party.getComplexity() * 2 * party.getTeam().size();
		lvl -= randomize;
		lvl += Utils.random(randomize * 2);
		lvl *= party.getDifficultyRatio();
		lvl *= multiplier;
		lvl *= 1D - ((6D - party.getComplexity()) * 0.07D);
		if (party.getTeam().size() > 2 && id != 12752 && id != 11872 && id != 11708 && id != 12865) //blink
			lvl *= 0.7;
		return (int) (lvl < 1 ? 1 : lvl);
	}

	public Map<Skill, Integer> getLevels(boolean boss, int level, int hitpoints) {
		return NPCCombatDefinitions.generateLevels(level, hitpoints/10);
	}

	public void updateGuardian(RoomReference reference) {
		if (visibleMap[reference.getRoomX()][reference.getRoomY()].removeGuardians()) {
			getRoom(reference).removeGuardianDoors();
			for (Player player : party.getTeam()) {
				RoomReference playerReference = getCurrentRoomReference(player.getTile());
				if (playerReference.getRoomX() == reference.getRoomX() && playerReference.getRoomY() == reference.getRoomY())
					playMusic(player, reference);
			}
		}
	}

	public void exitDungeon(final Player player, final boolean logout) {
		resetItems(player, true, logout);
		party.remove(player, logout);
		player.stopAll();
		player.getControllerManager().removeControllerWithoutCheck();
		player.getControllerManager().startController(new DamonheimController());
		resetTraps(player);
		if (player.getFamiliar() != null)
			player.getFamiliar().sendDeath(player);
		if (logout) {
			player.save("isLoggedOutInDungeon", true);
			player.getSkills().restoreSkills();
		}
		else {
			player.reset();
			player.getDungManager().setRejoinKey(null);
			player.useStairs(-1, new WorldTile(DungeonConstants.OUTSIDE, 2), 0, 3);
			player.getCombatDefinitions().removeDungeonneringBook();
			player.getPackets().sendVarc(1725, 1);
			setWorldMap(player, false);
			removeMark(player);
			player.setLargeSceneView(false);
			player.setForceMultiArea(false);
			player.getInterfaceManager().removeOverlay(true);
			player.getMusicsManager().reset();
			player.getAppearance().setBAS(-1);
		}

	}

	public void setWorldMap(Player player, boolean dungIcon) {
		player.getVars().setVarBit(11297, dungIcon ? 1 : 0);
	}

	public void endFarming() {
		for (OwnedObject obj : farmingPatches)
			obj.destroy();
		farmingPatches.clear();
	}

	private void resetTraps(Player player) {
		for (MastyxTrap trap : mastyxTraps) {
			if (!trap.getPlayerName().equals(player.getDisplayName()))
				continue;
			trap.finish();
		}
	}

	public void endMastyxTraps() {
		for (MastyxTrap trap : mastyxTraps)
			trap.finish();
		mastyxTraps.clear();
	}

	public void removeDungeon() {
		cachedDungeons.remove(key);
	}

	public void destroy() {
		if (isDestroyed())
			return;
		endRewardsTimer();
		endDestroyTimer();
		endFarming();
		endMastyxTraps();
		removeDungeon();
		partyDeaths.clear();
		for (VisibleRoom[] element : visibleMap)
			for (VisibleRoom element2 : element)
				if (element2 != null)
					element2.destroy();
		dungeon = null;
		region.destroy();
	}

	public void nextFloor() {
		//int maxFloor = DungeonUtils.getMaxFloor(party.getDungeoneeringLevel());
		if (party.getMaxFloor() > party.getFloor())
			party.setFloor(party.getFloor() + 1);
		if (tutorialMode) {
			int complexity = party.getComplexity();
			if (party.getMaxComplexity() > complexity)
				party.setComplexity(complexity + 1);
		}
		destroy();
		load();
	}

	/*
		1 = M.V.P.
		2 = Leecher
		3 = Berserker
		4 = Sharpshooter
		5 = Battle-mage
		6 = Against All Odds
		7 = Least Harmful
		8 = Nine Lives
		9 = Spontaneous Combustion
		10 = Crafting Catastrophe
		11 = D.I.Y. Disaster
		12 = Fishing Folly
		13 = Meat-shield
		14 = Survivor
		15 = Most Deaths
		16 = Vengeance is Mine!
		17 = Double K.O.
		18 = Jack-of-all-trades
		19 = Kill Stealer
		20 = Knuckle Sandwich
		21 = Gatherer
		22 = David
		23 = Goliath
		24 = Master-of-none
		25 = Untouchable
		26 = Glutton
		27 = Master Chef
		28 = Party Magician
		29 = 'A' for Effort
		30 = Culinary Disaster
		31 = Uneconomical Alcher
		32 = Pickaxe Alcher
		33 = Nothing Special
		34 = Clever Girl
		35 = Handyman
		36 = I Can Has Heim Crab
		37 = It's a Trap!
		38 = Medic!
		39 = Rest in Peace
		40 = Tele-fail
		41 = Too Little, Too Late
		42 = Least I Ain't Chicken
		43 = The Gate Escape
		44 = Balanced!
		45 = Ha-Trick!
		46 = I Choose You!
		47 = Beast Mode
		48 = The Chief
		49 = WoopWoopWoop!
		50 = It's Too Easy!
	 */

	public Integer[] getAchievements(Player player) { //TODO
		List<Integer> achievements = new ArrayList<>();

		DungeonController controller = (DungeonController) player.getControllerManager().getController();

		//solo achievements
		if (controller.isKilledBossWithLessThan10HP())
			achievements.add(6);
		if (controller.getDeaths() == 8)
			achievements.add(8);
		else if (controller.getDeaths() == 0)
			achievements.add(14);
		if (controller.getDamage() != 0 && controller.getDamageReceived() == 0)
			achievements.add(25);

		if (party.getTeam().size() > 1) { //party achievements
			boolean mostMeleeDmg = true;
			boolean mostMageDmg = true;
			boolean mostRangeDmg = true;
			boolean leastDamage = true;
			boolean mostDmgReceived = true;
			boolean mostDeaths = true;
			boolean mostHealedDmg = true;
			for (Player teamMate : party.getTeam()) {
				if (teamMate == player)
					continue;
				DungeonController tmController = (DungeonController) teamMate.getControllerManager().getController();
				if (tmController.getMeleeDamage() >= controller.getMeleeDamage())
					mostMeleeDmg = false;
				if (tmController.getMageDamage() >= controller.getMageDamage())
					mostMageDmg = false;
				if (tmController.getRangeDamage() >= controller.getRangeDamage())
					mostRangeDmg = false;
				if (controller.getDamage() >= tmController.getDamage())
					leastDamage = false;
				if (controller.getDamageReceived() <= tmController.getDamageReceived())
					mostDmgReceived = false;
				if (controller.getDeaths() <= tmController.getDeaths())
					mostDeaths = false;
				if (controller.getHealedDamage() <= tmController.getHealedDamage())
					mostHealedDmg = false;
			}
			if (mostMeleeDmg && mostMageDmg && mostRangeDmg)
				achievements.add(1);
			if (leastDamage && mostDeaths) //leecher
				achievements.add(2);
			if (mostMeleeDmg)
				achievements.add(3);
			if (mostRangeDmg)
				achievements.add(4);
			if (mostMageDmg)
				achievements.add(5);
			if (leastDamage)
				achievements.add(7);
			if (mostDmgReceived)
				achievements.add(13);
			if (mostDeaths)
				achievements.add(15);
			if (mostHealedDmg)
				achievements.add(38);
		}
		if (achievements.size() == 0)
			achievements.add(33);
		return achievements.toArray(new Integer[achievements.size()]);

	}

	public void loadRewards() {
		stage = 2;
		for (Player player : party.getTeam()) {
			for (Item item : player.getInventory().getItems().array()) {
				if (DungManager.isBannedDungItem(item))
					player.getInventory().deleteItem(item);
			}
			for (Item item : player.getEquipment().getItemsCopy()) {
				if (DungManager.isBannedDungItem(item))
					player.getEquipment().deleteItem(item.getId(), item.getAmount());
			}
			player.getAppearance().generateAppearanceData();
			player.stopAll();
			double multiplier = 1;
			if (!player.resizeable())
				player.getInterfaceManager().sendInterface(933);
			else
				player.getInterfaceManager().sendOverlay(933, true);
			player.getPackets().sendRunScriptReverse(2275); //clears interface data from last run
			for (int i = 0; i < 5; i++) {
				Player partyPlayer = i >= party.getTeam().size() ? null : party.getTeam().get(i);
				player.getPackets().sendVarc(1198 + i, partyPlayer != null ? 1 : 0); //sets true that this player exists
				if (partyPlayer == null)
					continue;
				player.getPackets().sendVarcString(310 + i, partyPlayer.getDisplayName());
				Integer[] achievements = getAchievements(partyPlayer);
				for (int i2 = 0; i2 < (achievements.length > 6 ? 6 : achievements.length); i2++)
					player.getPackets().sendVarc(1203 + (i * 6) + i2, achievements[i2]);
			}
			player.getPackets().setIFText(933, 331, Utils.formatTime((World.getServerTicks() - time) * 600));
			player.getPackets().sendVarc(1187, party.getFloor());
			player.getPackets().sendVarc(1188, party.getSize() + 1); //dungeon size, sets bonus aswell
			multiplier += DungeonConstants.DUNGEON_SIZE_BONUS[party.getSize()];
			player.getPackets().sendVarc(1191, party.getTeam().size() * 10 + party.getDificulty()); //teamsize:dificulty
			multiplier += DungeonConstants.DUNGEON_DIFFICULTY_RATIO_BONUS[party.getTeam().size() - 1][party.getDificulty() - 1];
			int levelMod = 0;
			if (getVisibleBonusRoomsCount() != 0)
				//no bonus rooms in c1, would be divide by 0
				levelMod = getVisibleBonusRoomsCount() * 10000 / (dungeon.getRoomsCount() - dungeon.getCritCount());
			player.getPackets().sendVarc(1195, levelMod); //dungeons rooms opened, sets bonus aswell
			multiplier += DungeonConstants.MAX_BONUS_ROOM * levelMod / 10000;
			levelMod = (getLevelModPerc() * 20) - 1000;
			player.getPackets().sendVarc(1236, levelMod); //sets level mod
			multiplier += ((double) levelMod) / 10000.0;
			player.getPackets().sendVarc(1196, party.isGuideMode() ? 1 : 0); //sets guidemode
			if (party.isGuideMode())
				multiplier -= 0.05;
			player.getPackets().sendVarc(1319, DungeonUtils.getMaxFloor(player.getSkills().getLevelForXp(Constants.DUNGEONEERING)));
			player.getPackets().sendVarc(1320, party.getComplexity());
			if (party.getComplexity() != 6)
				multiplier -= (DungeonConstants.COMPLEXITY_PENALTY_BASE[party.getSize()] + (5 - party.getComplexity()) * 0.06);
			double levelDiffPenalty = party.getLevelDiferencePenalty(player);//party.getMaxLevelDiference() > 70 ? DungeonConstants.UNBALANCED_PARTY_PENALTY : 0;
			player.getPackets().sendVarc(1321, (int) (levelDiffPenalty * 10000));
			multiplier -= levelDiffPenalty;
			Integer deaths = partyDeaths.get(player.getUsername());
			double countedDeaths = Math.min(deaths == null ? 0 : deaths.intValue(), 6);
			multiplier *= (1.0 - (countedDeaths * 0.1)); //adds FLAT 10% reduction per death, upto 6
			//base xp is based on a ton of factors, including opened rooms, resources harvested, ... but this is most imporant one
			double floorXP = getFloorXP(party.getFloor(), party.getSize(), getVisibleRoomsCount());
			boolean tickedOff = player.getDungManager().isTickedOff(party.getFloor());
			if (!tickedOff)
				player.getDungManager().tickOff(party.getFloor());
			else {
				int[] range = DungeonUtils.getFloorThemeRange(party.getFloor());
				for (int floor = range[0]; floor <= range[1]; floor++) {
					if (player.getDungManager().getMaxFloor() < floor)
						break;
					if (!player.getDungManager().isTickedOff(floor)) {
						player.sendMessage("Since you have previously completed this floor, floor " + floor + " was instead ticked-off.");
						player.getDungManager().tickOff(floor);
						floorXP = getFloorXP(floor, party.getSize(), getVisibleRoomsCount());
						tickedOff = false;
						break;
					}
				}
			}
			double prestigeXP = tickedOff ? 0 : getFloorXP(player.getDungManager().getPrestige(), party.getSize(), getVisibleRoomsCount());
			player.getVars().setVarBit(7550, player.getDungManager().getCurrentProgress());
			player.getVars().setVarBit(7551, player.getDungManager().getPreviousProgress());
			double averageXP = (floorXP + prestigeXP) / 2;
			multiplier = Math.max(0.1, multiplier);
			double totalXp = averageXP * multiplier;
			int tokens = (int) (totalXp / 10.0);
			player.getPackets().sendVarc(1237, (int) (floorXP * 10));
			player.getPackets().sendVarc(1238, (int) (prestigeXP * 10));
			player.getPackets().sendVarc(1239, (int) (averageXP * 10));
			player.getSkills().addXp(Constants.DUNGEONEERING, totalXp); //force rs xp, cuz we do * xp rate in calcs to make inter show correct xp
			player.getDungManager().addTokens(tokens);
			player.getMusicsManager().forcePlayMusic(770);
			player.incrementCount("Dungeons completed");
			player.incrementCount(DungeonUtils.getFloorTypeName(party.getFloor()) + " floors completed");
			player.incrementCount(DungeonUtils.getSizeName(party.getSize()) + " floors completed");
			if (!tickedOff) {
				if (DungeonUtils.getMaxFloor(player.getSkills().getLevelForXp(Constants.DUNGEONEERING)) < party.getFloor() + 1)
					player.sendMessage("The next floor is not available at your dungeoneering level. Consider resetting your progress to gain best ongoing rate of xp.");
			} else {
				player.sendMessage("<col=D80000>Warning");
				player.sendMessage(":");
				player.sendMessage("You have already completed all the available floors of this theme and thus cannot be awarded prestige xp until you reset your progress or switch theme.");
			}
			if (party.getFloor() == player.getDungManager().getMaxFloor() && party.getFloor() < DungeonUtils.getMaxFloor(player.getSkills().getLevelForXp(Constants.DUNGEONEERING)))
				player.getDungManager().increaseMaxFloor();
			if (player.getDungManager().getMaxComplexity() < 6)
				player.getDungManager().increaseMaxComplexity();
			if (player.getFamiliar() != null)
				player.getFamiliar().sendDeath(player);
		}
		clearGuardians();
	}

	public static int getFloorXP(int floor, int size, int roomsOpened) {
		double baseXP = 0.16*(floor*floor*floor)+0.28*(floor*floor)+76.94*floor+100.0;
		double roomMod = 1.0;
		double sizeMod = 1.0;
		switch(size) {
		case 0 -> {
			roomMod = roomsOpened / 16.0;
		}
		case 1 -> {
			roomMod = roomsOpened / 32.0;
			sizeMod = 2.0;
		}
		case 2 -> {
			roomMod = roomsOpened / 64.0;
			sizeMod = 3.5;
		}
		}
		return (int) (baseXP * sizeMod * roomMod);
	}
	
	public static void printXP(int floor, int size, int prestige, int roomsOpened) {
		int baseXp = getFloorXP(floor, size, roomsOpened);
		int presXp = getFloorXP(prestige, size, roomsOpened);
		int avgXp = (int) ((baseXp+presXp) / 2);
		
		System.out.println("~~~Experience for floor " + floor + " size: " + size + " roomsOpened: " + roomsOpened + "~~~");
		System.out.println("Base XP: " + baseXp);
		System.out.println("Prestige " + prestige + " XP:" + presXp);
		System.out.println("Average XP: " + avgXp);
		System.out.println("Maximum possible XP for floor: " + ((int) (avgXp * 1.56)));
	}

	public void voteToMoveOn(Player player) {
		//loadRewards();
		if (rewardsTimer == null)
			setRewardsTimer();
		rewardsTimer.increaseReadyCount();
	}

	public void ready(Player player) {
		int index = party.getIndex(player);
		if (rewardsTimer == null)
			setRewardsTimer();
		rewardsTimer.increaseReadyCount();
		for (Player p2 : party.getTeam())
			p2.getPackets().sendVarc(1397 + index, 1);
	}

	public DungeonPartyManager getParty() {
		return party;
	}

	public void setRewardsTimer() {
		WorldTasks.schedule(rewardsTimer = new RewardsTimer(), 1, 9);
	}

	public void setDestroyTimer() {
		//cant be already instanced before anyway, afterall only isntances hwen party is 0 and remvoes if party not 0
		WorldTasks.schedule(destroyTimer = new DestroyTimer(), 1, 9);
	}

	public void setMark(Entity target, boolean mark) {
		if (mark)
			for (Player player : party.getTeam())
				player.getHintIconsManager().addHintIcon(6, target, 0, -1, true); //6th slot
		else
			removeMark();
		if (target instanceof DungeonNPC npc)
			npc.setMarked(mark);
	}

	public void setGroupGatestone(WorldTile groupGatestone) {
		this.groupGatestone = groupGatestone;
	}

	public WorldTile getGroupGatestone() {
		if (groupGatestone == null) {
			Player player = party.getGateStonePlayer();
			if (player != null)
				return new WorldTile(player.getTile());
		}
		return groupGatestone;
	}

	public void resetGatestone() {
		groupGatestone = null;
	}

	public void removeMark() {
		for (Player player : party.getTeam())
			removeMark(player);
	}

	public void removeMark(Player player) {
		player.getHintIconsManager().removeHintIcon(6);
	}

	public void endDestroyTimer() {
		if (destroyTimer != null) {
			destroyTimer.stop();
			destroyTimer = null;
		}
	}

	public void endRewardsTimer() {
		if (rewardsTimer != null) {
			rewardsTimer.stop();
			rewardsTimer = null;
		}
	}

	private class DestroyTimer extends WorldTask {
		private long timeLeft;

		public DestroyTimer() {
			timeLeft = 600; //10min
		}

		@Override
		public void run() {
			try {
				if (timeLeft > 0) {
					timeLeft -= 5;
					return;
				}
				destroy();
			} catch (Throwable e) {
				Logger.handle(e);
			}
		}

	}

	private class RewardsTimer extends WorldTask {

		private long timeLeft;
		private boolean gaveRewards;

		public RewardsTimer() {
			timeLeft = party.getTeam().size() * 60;
		}

		public void increaseReadyCount() {
			int reduce = (int) (gaveRewards ? ((double) 45 / (double) party.getTeam().size()) : 60);
			timeLeft = timeLeft > reduce ? timeLeft - reduce : 0;
		}

		@Override
		public void run() {
			try {
				if (timeLeft > 0) {
					for (Player player : party.getTeam())
						player.sendMessage(gaveRewards ? ("Time until next dungeon: " + timeLeft) : (timeLeft + " seconds until dungeon ends."));
					timeLeft -= 5;
				} else if (!gaveRewards) {
					gaveRewards = true;
					timeLeft = 45;
					loadRewards();
				} else
					nextFloor();
			} catch (Throwable e) {
				Logger.handle(e);
			}
		}

	}

	public void setDungeon() {
		key = party.getLeader() + "_" + keyMaker.getAndIncrement();
		cachedDungeons.put(key, this);
		for (Player player : party.getTeam()) {
			player.getDungManager().setRejoinKey(key);
			player.getInterfaceManager().removeOverlay(true);
			player.getInterfaceManager().removeCentralInterface();
		}
	}

	public static void checkRejoin(Player player) {
		Object key = player.getDungManager().getRejoinKey();
		if (key == null)
			return;
		DungeonManager dungeon = cachedDungeons.get(key);
		//either doesnt exit / ur m8s moving next floor(reward screen)
		if (dungeon == null || !dungeon.hasLoadedNoRewardScreen()) {
			player.getDungManager().setRejoinKey(null);
			return;
		}
		dungeon.rejoinParty(player);
	}

	public void load() {
		party.lockParty();
		visibleMap = new VisibleRoom[DungeonConstants.DUNGEON_RATIO[party.getSize()][0]][DungeonConstants.DUNGEON_RATIO[party.getSize()][1]];
		// slow executor loads dungeon as it may take up to few secs
		CoresManager.execute(() -> {
			try {
				clearKeyList();
				dungeon = new Dungeon(DungeonManager.this, party.getFloor(), party.getComplexity(), party.getSize());
				time = World.getServerTicks();
				region = new DynamicRegionReference(dungeon.getMapWidth() * 2, (dungeon.getMapHeight() * 2));
				region.clearMap(new int[1], () -> {
					setDungeon();
					loadRoom(dungeon.getStartRoomReference());
					stage = 1;
				});
			} catch (Throwable e) {
				Logger.handle(e);
			}
		});
	}

	public boolean hasStarted() {
		return stage != 0;
	}

	public boolean isAtRewardsScreen() {
		return stage == 2;
	}

	public boolean hasLoadedNoRewardScreen() {
		return stage == 1;
	}

	public void openMap(Player player) {
		player.getInterfaceManager().sendInterface(942);
		player.getPackets().sendRunScriptReverse(3277); //clear the map if theres any setted
		int protocol = party.getSize() == DungeonConstants.LARGE_DUNGEON ? 0 : party.getSize() == DungeonConstants.MEDIUM_DUNGEON ? 2 : 1;
		for (int x = 0; x < visibleMap.length; x++)
			for (int y = 0; y < visibleMap[x].length; y++)
				if (visibleMap[x][y] != null) { //means exists
					Room room = getRoom(new RoomReference(x, y));
					boolean highLight = party.isGuideMode() && room.isCritPath();
					player.getPackets().sendRunScriptReverse(3278, protocol, getMapIconSprite(room, highLight), y, x);
					if (room.getRoom() instanceof StartRoom)
						player.getPackets().sendRunScriptReverse(3280, protocol, y, x);
					else if (room.getRoom() instanceof BossRoom)
						player.getPackets().sendRunScriptReverse(3281, protocol, y, x);
					if (room.hasNorthDoor() && visibleMap[x][y + 1] == null) {
						Room unknownR = getRoom(new RoomReference(x, y + 1));
						highLight = party.isGuideMode() && unknownR.isCritPath();
						player.getPackets().sendRunScriptReverse(3278, protocol, getMapIconSprite(DungeonConstants.SOUTH_DOOR, highLight), y + 1, x);
					}
					if (room.hasSouthDoor() && visibleMap[x][y - 1] == null) {
						Room unknownR = getRoom(new RoomReference(x, y - 1));
						highLight = party.isGuideMode() && unknownR.isCritPath();
						player.getPackets().sendRunScriptReverse(3278, protocol, getMapIconSprite(DungeonConstants.NORTH_DOOR, highLight), y - 1, x);
					}
					if (room.hasEastDoor() && visibleMap[x + 1][y] == null) {
						Room unknownR = getRoom(new RoomReference(x + 1, y));
						highLight = party.isGuideMode() && unknownR.isCritPath();
						player.getPackets().sendRunScriptReverse(3278, protocol, getMapIconSprite(DungeonConstants.WEST_DOOR, highLight), y, x + 1);
					}
					if (room.hasWestDoor() && visibleMap[x - 1][y] == null) {
						Room unknownR = getRoom(new RoomReference(x - 1, y));
						highLight = party.isGuideMode() && unknownR.isCritPath();
						player.getPackets().sendRunScriptReverse(3278, protocol, getMapIconSprite(DungeonConstants.EAST_DOOR, highLight), y, x - 1);
					}
				}
		int index = 1;
		for (Player p2 : party.getTeam()) {
			RoomReference reference = getCurrentRoomReference(p2.getTile());
			player.getPackets().sendRunScriptReverse(3279, p2.getDisplayName(), protocol, index++, reference.getRoomY(), reference.getRoomX());
		}
	}

	public int getMapIconSprite(int direction, boolean highLight) {
		for (MapRoomIcon icon : MapRoomIcon.values()) {
			if (icon.isOpen())
				continue;
			if (icon.hasDoor(direction))
				return icon.getSpriteId() + (highLight ? MapRoomIcon.values().length : 0);
		}
		return 2879;
	}

	public int getMapIconSprite(Room room, boolean highLight) {
		for (MapRoomIcon icon : MapRoomIcon.values()) {
			if (!icon.isOpen())
				continue;
			if (icon.hasNorthDoor() == room.hasNorthDoor() && icon.hasSouthDoor() == room.hasSouthDoor() && icon.hasWestDoor() == room.hasWestDoor() && icon.hasEastDoor() == room.hasEastDoor())
				return icon.getSpriteId() + (highLight ? MapRoomIcon.values().length : 0);
		}
		return 2878;
	}

	public void openStairs(RoomReference reference) {
		Room room = getRoom(reference);
		int type = 0;

		if (room.getRoom().getChunkX() == 26 && room.getRoom().getChunkY() == 640) //unholy cursed
			type = 1;
		else if (room.getRoom().getChunkX() == 30 && room.getRoom().getChunkY() == 656) //stomp
			type = 2;
		else if ((room.getRoom().getChunkX() == 30 && room.getRoom().getChunkY() == 672) || (room.getRoom().getChunkX() == 24 && room.getRoom().getChunkY() == 690)) //necromancer)
			type = 3;
		else if (room.getRoom().getChunkX() == 26 && room.getRoom().getChunkY() == 690) //world-gorger
			type = 4;
		else if (room.getRoom().getChunkX() == 24 && room.getRoom().getChunkY() == 688) //blink
			type = 5;
		spawnObject(reference, DungeonConstants.LADDERS[party.getFloorType()], ObjectType.SCENERY_INTERACT, (type == 2 || type == 3) ? 0 : 3, type == 4 ? 11 : type == 3 ? 15 : type == 2 ? 14 : 7, type == 5 ? 14 : (type == 3 || type == 2) ? 3 : type == 1 ? 11 : 15);
		getVisibleRoom(reference).setNoMusic();
		for (Player player : party.getTeam()) {
			if (!isAtBossRoom(player.getTile()))
				continue;
			player.getPackets().sendMusicEffect(415);
			playMusic(player, reference);
		}
	}

	public List<OwnedObject> getFarmingPatches() {
		return farmingPatches;
	}

	public void addMastyxTrap(MastyxTrap mastyxTrap) {
		mastyxTraps.add(mastyxTrap);
	}

	public List<MastyxTrap> getMastyxTraps() {
		return mastyxTraps;
	}

	public void removeMastyxTrap(MastyxTrap mastyxTrap) {
		mastyxTraps.remove(mastyxTrap);
		mastyxTrap.finish();
	}

	public void message(RoomReference reference, String message) {
		for (Player player : party.getTeam())
			if (reference.equals(getCurrentRoomReference(player.getTile())))
				player.sendMessage(message);
	}

	public void showBar(RoomReference reference, String name, int percentage) {
		for (Player player : party.getTeam()) {
			RoomReference current = getCurrentRoomReference(player.getTile());
			if (reference.getRoomX() == current.getRoomX() && reference.getRoomY() == current.getRoomY() && player.getControllerManager().getController() instanceof DungeonController ctrl) {
				ctrl.showBar(true, name);
				ctrl.sendBarPercentage(percentage);
			}
		}
	}

	public void hideBar(RoomReference reference) {
		for (Player player : party.getTeam()) {
			RoomReference current = getCurrentRoomReference(player.getTile());
			if (reference.getRoomX() == current.getRoomX() && reference.getRoomY() == current.getRoomY() && player.getControllerManager().getController() instanceof DungeonController ctrl)
				ctrl.showBar(false, null);
		}
	}

	public Map<String, Integer> getPartyDeaths() {
		return partyDeaths;
	}

	/*
	 * Use get npc instead
	 * this being used because gravecreeper gets removed when using special :/
	 */
	@Deprecated
	public DungeonBoss getTemporaryBoss() {
		return temporaryBoss;
	}

	public void setTemporaryBoss(DungeonBoss temporaryBoss) {
		this.temporaryBoss = temporaryBoss;
	}

	public Dungeon getDungeon() {
		return dungeon;
	}
}
