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
package com.rs.game.player.content.skills.construction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.cache.loaders.interfaces.IFTargetParams;
import com.rs.game.DynamicRegion;
import com.rs.game.ForceTalk;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.pet.Pets;
import com.rs.game.player.content.skills.construction.HouseConstants.Builds;
import com.rs.game.player.content.skills.construction.HouseConstants.HObject;
import com.rs.game.player.content.skills.construction.HouseConstants.POHLocation;
import com.rs.game.player.content.skills.construction.HouseConstants.Room;
import com.rs.game.player.content.skills.construction.HouseConstants.Servant;
import com.rs.game.player.controllers.Controller;
import com.rs.game.player.controllers.HouseController;
import com.rs.game.player.dialogues.SimpleMessage;
import com.rs.game.player.managers.InterfaceManager.Tab;
import com.rs.game.region.Region;
import com.rs.game.region.RegionBuilder.DynamicRegionReference;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.utils.RegionUtils;

@PluginEventHandler
public class House {

	public static int LOGGED_OUT = 0, KICKED = 1, TELEPORTED = 2;
	private List<RoomReference> roomsR;

	private byte look;
	private POHLocation location;
	private boolean buildMode;
	private boolean arriveInPortal;
	private Servant servant;
	private byte paymentStage;

	private transient Player player;
	private transient boolean locked;
	private transient CopyOnWriteArrayList<NPC> pets;
	private transient CopyOnWriteArrayList<NPC> npcs;

	private transient List<Player> players;
	private transient DynamicRegionReference region;
	private transient boolean loaded;
	private transient boolean challengeMode;
	private transient ServantNPC servantInstance;

	private PetHouse petHouse;

	private byte build;

	public void setLocation(POHLocation location) {
		this.location = location;
	}

	public POHLocation getLocation() {
		return location;
	}

	private boolean isOwnerInside() {
		return players.contains(player);
	}

	public static ButtonClickHandler handleHouseOptions = new ButtonClickHandler(398) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 19)
				e.getPlayer().getInterfaceManager().sendTab(Tab.SETTINGS);
			else if (e.getComponentId() == 15 || e.getComponentId() == 1)
				e.getPlayer().getHouse().setBuildMode(e.getComponentId() == 15);
			else if (e.getComponentId() == 25 || e.getComponentId() == 26)
				e.getPlayer().getHouse().setArriveInPortal(e.getComponentId() == 25);
			else if (e.getComponentId() == 27)
				e.getPlayer().getHouse().expelGuests();
			else if (e.getComponentId() == 29)
				House.leaveHouse(e.getPlayer());
		}
	};

	public static ButtonClickHandler handleCreateRoom = new ButtonClickHandler(402) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() >= 93 && e.getComponentId() <= 115)
				e.getPlayer().getHouse().createRoom(e.getComponentId() - 93);
		}
	};

	public static ButtonClickHandler handleBuild = new ButtonClickHandler(394, 396) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 11)
				e.getPlayer().getHouse().build(e.getSlotId());
		}
	};

	public void expelGuests() {
		if (!isOwnerInside()) {
			player.sendMessage("You can only expel guests when you are in your own house.");
			return;
		}
		kickGuests();
	}

	public void kickGuests() {
		if ((players == null) || (players.size() <= 0))
			return;
		for (Player player : new ArrayList<>(players)) {
			if (isOwner(player))
				continue;
			leaveHouse(player, KICKED);
		}
	}

	public boolean isOwner(Player player) {
		return this.player.getUsername().equalsIgnoreCase(player.getUsername());
	}

	public void enterMyHouse() {
		joinHouse(player);
	}

	public void openRoomCreationMenu(GameObject door) {
		int roomX = player.getChunkX() - region.getBaseChunkX(); // current room
		int roomY = player.getChunkY() - region.getBaseChunkY(); // current room
		int xInChunk = player.getXInChunk();
		int yInChunk = player.getYInChunk();
		if (xInChunk == 7)
			roomX += 1;
		else if (xInChunk == 0)
			roomX -= 1;
		else if (yInChunk == 7)
			roomY += 1;
		else if (yInChunk == 0)
			roomY -= 1;
		openRoomCreationMenu(roomX, roomY, door.getPlane());
	}

	public void removeRoom() {
		int roomX = player.getChunkX() - region.getBaseChunkX(); // current room
		int roomY = player.getChunkY() - region.getBaseChunkY(); // current room
		RoomReference room = getRoom(roomX, roomY, player.getPlane());
		if (room == null)
			return;
		if (room.getZ() != 1) {
			player.getDialogueManager().execute(new SimpleMessage(), "You cannot remove a building that is supporting this room.");
			return;
		}

		RoomReference above = getRoom(roomX, roomY, 2);
		RoomReference below = getRoom(roomX, roomY, 0);

		RoomReference roomTo = above != null && above.getStaircaseSlot() != -1 ? above : below != null && below.getStaircaseSlot() != -1 ? below : null;
		if (roomTo == null) {
			player.getDialogueManager().execute(new SimpleMessage(), "These stairs do not lead anywhere.");
			return;
		}
		openRoomCreationMenu(roomTo.getX(), roomTo.getY(), roomTo.getZ());
	}

	/*
	 * door used to calculate where player facing to create
	 */
	public void openRoomCreationMenu(int roomX, int roomY, int plane) {
		if (!buildMode) {
			player.getDialogueManager().execute(new SimpleMessage(), "You can only do that in building mode.");
			return;
		}
		RoomReference room = getRoom(roomX, roomY, plane);
		if (room != null) {
			if (room.plane == 1 && getRoom(roomX, roomY, room.plane + 1) != null) {
				player.getDialogueManager().execute(new SimpleMessage(), "You can't remove a room that is supporting another room.");
				return;
			}
			if (room.room == Room.THRONE_ROOM && room.plane == 1) {
				RoomReference bellow = getRoom(roomX, roomY, room.plane - 1);
				if (bellow != null && bellow.room == Room.OUTBLIETTE) {
					player.getDialogueManager().execute(new SimpleMessage(), "You can't remove a throne room that is supporting a outbliette.");
					return;
				}
			}
			if ((room.room == Room.GARDEN || room.room == Room.FORMAL_GARDEN) && getPortalCount() < 2)
				if (room == getPortalRoom()) {
					player.getDialogueManager().execute(new SimpleMessage(), "Your house must have at least one exit portal.");
					return;
				}
			player.getDialogueManager().execute(new RemoveRoomD(), room);
		} else {
			if (roomX == 0 || roomY == 0 || roomX == 7 || roomY == 7) {
				player.getDialogueManager().execute(new SimpleMessage(), "You can't create a room here.");
				return;
			}
			if (plane == 2) {
				RoomReference r = getRoom(roomX, roomY, 1);
				if (r == null || (r.room == Room.GARDEN || r.room == Room.FORMAL_GARDEN || r.room == Room.MENAGERIE)) {
					player.getDialogueManager().execute(new SimpleMessage(), "You can't create a room here.");
					return;
				}

			}
			for (int index = 0; index < HouseConstants.Room.values().length - 2; index++) {
				Room refRoom = HouseConstants.Room.values()[index];
				if (player.getSkills().getLevel(Constants.CONSTRUCTION) >= refRoom.getLevel() && player.getInventory().getAmountOf(995) >= refRoom.getPrice())
					player.getPackets().setIFText(402, index + (refRoom == HouseConstants.Room.DUNGEON_STAIRS || refRoom == HouseConstants.Room.DUNGEON_PIT ? 69 : refRoom == HouseConstants.Room.TREASURE_ROOM ? 70 : 68), "<col=008000> " + refRoom.getPrice() + " coins");
			}
			player.getInterfaceManager().sendInterface(402);
			player.getTempAttribs().setO("CreationRoom", new int[] { roomX, roomY, plane });
			player.setCloseInterfacesEvent(() -> player.getTempAttribs().removeO("CreationRoom"));
		}
	}

	public void handleLever(Player player, GameObject object) {
		if (buildMode || player.isLocked())
			return;
		final int roomX = player.getChunkX() - region.getBaseChunkX(); // current room
		final int roomY = player.getChunkY() - region.getBaseChunkY(); // current room
		RoomReference room = getRoom(roomX, roomY, player.getPlane());
		int trap = room.getTrapObject();
		player.setNextAnimation(new Animation(9497));
		if (trap == -1 || trap == HObject.FLOORDECORATION.getId())
			return;
		player.lock(7);
		if (trap == HObject.TRAPDOOR.getId())
			player.sendOptionDialogue("What would you like to do?", new String[] { "Drop into oubliette" }, new DialogueOptionEvent() {

				@Override
				public void run(Player player) {
					if (getOption() == 1)
						dropPlayers(roomX, roomY, 13681, 13681, 13681, 13681);
				}

			});
		else if (trap == HObject.STEELCAGE.getId()) {
			trapPlayers(roomX, roomY, 13681, 13681, 13681, 13681);
			player.sendOptionDialogue("What would you like to do?", new String[] { "Release players", "Nothing." }, new DialogueOptionEvent() {

				@Override
				public void run(Player player) {
					if (getOption() == 1)
						releasePlayers(roomX, roomY, 13681, 13681, 13681, 13681);
				}

			});
		} else if (trap == HObject.LESSERMAGICCAGE.getId()) {
			trapPlayers(roomX, roomY, 13682);
			player.sendOptionDialogue("What would you like to do?", new String[] { "Release players", "Drop into oubliette" }, new DialogueOptionEvent() {

				@Override
				public void run(Player player) {
					if (getOption() == 1)
						releasePlayers(roomX, roomY, 13682);
					else if (getOption() == 2)
						dropPlayers(roomX, roomY, 13682);
				}

			});
		} else if (trap == HObject.GREATERMAGICCAGE.getId()) {
			trapPlayers(roomX, roomY, 13683);
			player.sendOptionDialogue("What would you like to do?", new String[] { "Release players", "Drop into oubliette", "Kick from house" }, new DialogueOptionEvent() {

				@Override
				public void run(Player player) {
					if (getOption() == 1)
						releasePlayers(roomX, roomY, 13683);
					else if (getOption() == 2)
						dropPlayers(roomX, roomY, 13683);
					else if (getOption() == 3)
						kickTrapped(roomX, roomY, 13683);
				}

			});
		}
	}

	public ArrayList<Player> getTrappedPlayers(int x, int y) {
		ArrayList<Player> list = new ArrayList<>();
		for (Player p : players)
			if (p != null && p.getControllerManager().getController() instanceof HouseController)
				if ((p.getX() >= x && p.getX() <= x + 1) && (p.getY() >= y && p.getY() <= y + 1))
					list.add(p);
		return list;
	}

	public void kickTrapped(int roomX, int roomY, int... trapIds) {
		int x = region.getLocalX(roomX, 3);
		int y = region.getLocalY(roomY, 3);
		for (final Player p : getTrappedPlayers(x, y)) {
			if (isOwner(p)) {
				p.setNextForceTalk(new ForceTalk("Trying to kick the house owner... Pfft.."));
				continue;
			}
			leaveHouse(p, KICKED);
		}
		releasePlayers(roomX, roomY, trapIds);
	}

	public void dropPlayers(int roomX, int roomY, int... trapIds) {
		RoomReference roomTo = getRoom(roomX, roomY, 0);
		if (roomTo == null || roomTo.getLadderTrapSlot() == -1) {
			releasePlayers(roomX, roomY, trapIds);
			return;
		}
		int x = region.getLocalX(roomX, 3);
		int y = region.getLocalY(roomY, 3);
		for (final Player p : getTrappedPlayers(x, y)) {
			p.lock(10);
			p.setNextAnimation(new Animation(1950));
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					p.setNextWorldTile(new WorldTile(p.getX(), p.getY(), 0));
					p.setNextAnimation(new Animation(3640));
				}
			}, 5);
		}
		releasePlayers(roomX, roomY, trapIds);
	}

	public void releasePlayers(int roomX, int roomY, int... trapIds) {
		int x = region.getLocalX(roomX, 3);
		int y = region.getLocalY(roomY, 3);
		World.removeObject(new GameObject(trapIds[0], ObjectType.SCENERY_INTERACT, 1, new WorldTile(x, y, player.getPlane())));
		if (trapIds.length > 1)
			World.removeObject(new GameObject(trapIds[1], ObjectType.SCENERY_INTERACT, 0, new WorldTile(x + 1, y, player.getPlane())));
		if (trapIds.length > 2)
			World.removeObject(new GameObject(trapIds[2], ObjectType.SCENERY_INTERACT, 2, new WorldTile(x, y + 1, player.getPlane())));
		if (trapIds.length > 3)
			World.removeObject(new GameObject(trapIds[3], ObjectType.SCENERY_INTERACT, 3, new WorldTile(x + 1, y + 1, player.getPlane())));
		World.removeObject(World.getObjectWithType(new WorldTile(x - 1, y + 1, player.getPlane()), ObjectType.WALL_STRAIGHT));
		World.removeObject(World.getObjectWithType(new WorldTile(x - 1, y, player.getPlane()), ObjectType.WALL_STRAIGHT));
		World.removeObject(World.getObjectWithType(new WorldTile(x, y + 2, player.getPlane()), ObjectType.WALL_STRAIGHT));
		World.removeObject(World.getObjectWithType(new WorldTile(x + 1, y + 2, player.getPlane()), ObjectType.WALL_STRAIGHT));
		World.removeObject(World.getObjectWithType(new WorldTile(x, y - 1, player.getPlane()), ObjectType.WALL_STRAIGHT));
		World.removeObject(World.getObjectWithType(new WorldTile(x + 1, y - 1, player.getPlane()), ObjectType.WALL_STRAIGHT));
		World.removeObject(World.getObjectWithType(new WorldTile(x + 2, y, player.getPlane()), ObjectType.WALL_STRAIGHT));
		World.removeObject(World.getObjectWithType(new WorldTile(x + 2, y + 1, player.getPlane()), ObjectType.WALL_STRAIGHT));
		for (Player p : getTrappedPlayers(x, y))
			p.resetWalkSteps();
	}

	public void trapPlayers(int roomX, int roomY, int... trapIds) {
		int x = region.getLocalX(roomX, 3);
		int y = region.getLocalY(roomY, 3);
		World.spawnObject(new GameObject(trapIds[0], ObjectType.SCENERY_INTERACT, 1, new WorldTile(x, y, player.getPlane())));
		if (trapIds.length > 1)
			World.spawnObject(new GameObject(trapIds[1], ObjectType.SCENERY_INTERACT, 0, new WorldTile(x + 1, y, player.getPlane())));
		if (trapIds.length > 2)
			World.spawnObject(new GameObject(trapIds[2], ObjectType.SCENERY_INTERACT, 2, new WorldTile(x, y + 1, player.getPlane())));
		if (trapIds.length > 3)
			World.spawnObject(new GameObject(trapIds[3], ObjectType.SCENERY_INTERACT, 3, new WorldTile(x + 1, y + 1, player.getPlane())));
		World.spawnObject(new GameObject(13150, ObjectType.WALL_STRAIGHT, 2, new WorldTile(x - 1, y + 1, player.getPlane())));
		World.spawnObject(new GameObject(13150, ObjectType.WALL_STRAIGHT, 2, new WorldTile(x - 1, y, player.getPlane())));
		World.spawnObject(new GameObject(13150, ObjectType.WALL_STRAIGHT, 3, new WorldTile(x, y + 2, player.getPlane())));
		World.spawnObject(new GameObject(13150, ObjectType.WALL_STRAIGHT, 3, new WorldTile(x + 1, y + 2, player.getPlane())));
		World.spawnObject(new GameObject(13150, ObjectType.WALL_STRAIGHT, 1, new WorldTile(x, y - 1, player.getPlane())));
		World.spawnObject(new GameObject(13150, ObjectType.WALL_STRAIGHT, 1, new WorldTile(x + 1, y - 1, player.getPlane())));
		World.spawnObject(new GameObject(13150, ObjectType.WALL_STRAIGHT, 0, new WorldTile(x + 2, y, player.getPlane())));
		World.spawnObject(new GameObject(13150, ObjectType.WALL_STRAIGHT, 0, new WorldTile(x + 2, y + 1, player.getPlane())));
		for (Player p : getTrappedPlayers(x, y))
			p.resetWalkSteps();
	}

	public void climbLadder(Player player, GameObject object, boolean up) {
		if (object == null || region == null)
			return;
		int roomX = object.getChunkX() - region.getBaseChunkX();
		int roomY = object.getChunkY() - region.getBaseChunkY();
		RoomReference room = getRoom(roomX, roomY, object.getPlane());
		if (room == null)
			return;
		if (room.plane == (up ? 2 : 0)) {
			player.sendMessage("You are on the " + (up ? "highest" : "lowest") + " possible level so you cannot add a room " + (up ? "above" : "under") + " here.");
			return;
		}
		RoomReference roomTo = getRoom(roomX, roomY, room.plane + (up ? 1 : -1));
		if (roomTo == null) {
			if (buildMode)
				player.getDialogueManager().execute(new CreateLadderRoomD(), room, up);
			else
				player.sendMessage("This does not lead anywhere.");
			// start dialogue
			return;
		}
		if (roomTo.getLadderTrapSlot() == -1) {
			player.sendMessage("This does not lead anywhere.");
			return;
		}
		int xOff = 0;
		int yOff = 0;
		if (roomTo.getRotation() == 0) {
			yOff = 6;
			xOff = 2;
		} else if (roomTo.getRotation() == 1) {
			yOff = 6;
			xOff = 5;
		} else if (roomTo.getRotation() == 2) {
			yOff = 1;
			xOff = 5;
		} else if (roomTo.getRotation() == 3) {
			yOff = 1;
			xOff = 2;
		}
		player.ladder(new WorldTile(region.getLocalX(roomTo.getX(), xOff), region.getLocalY(roomTo.getY(), yOff), player.getPlane() + (up ? 1 : -1)));
	}

	public WorldTile getCenterTile(RoomReference rRef) {
		if (region == null || rRef == null)
			return null;
		return region.getLocalTile(rRef.x * 8 + 3, rRef.y * 8 + 3);
	}

	public int getPaymentStage() {
		return paymentStage;
	}

	public void resetPaymentStage() {
		paymentStage = 0;
	}

	public void incrementPaymentStage() {
		paymentStage++;
	}

	public void climbStaircase(Player player, GameObject object, boolean up) {
		if (object == null || region == null)
			return;
		int roomX = object.getChunkX() - region.getBaseChunkX();
		int roomY = object.getChunkY() - region.getBaseChunkY();
		RoomReference room = getRoom(roomX, roomY, object.getPlane());
		if (room == null)
			return;
		if (room.plane == (up ? 2 : 0)) {
			player.sendMessage("You are on the " + (up ? "highest" : "lowest") + " possible level so you cannot add a room " + (up ? "above" : "under") + " here.");
			return;
		}
		RoomReference roomTo = getRoom(roomX, roomY, room.plane + (up ? 1 : -1));
		if (roomTo == null) {
			if (buildMode)
				player.getDialogueManager().execute(new CreateRoomStairsD(), room, up);
			else
				player.sendMessage("These stairs do not lead anywhere.");
			// start dialogue
			return;
		}
		if (roomTo.getStaircaseSlot() == -1) {
			player.sendMessage("These stairs do not lead anywhere.");
			return;
		}
		player.useStairs(-1, new WorldTile(player.getX(), player.getY(), player.getPlane() + (up ? 1 : -1)), 0, 1);

	}

	public void removeRoom(RoomReference room) {
		if (roomsR.remove(room)) {
			refreshNumberOfRooms();
			refreshHouse();
		}
	}

	public void createRoom(int slot) {
		Room[] rooms = HouseConstants.Room.values();
		if (slot >= rooms.length)
			return;
		int[] position = player.getTempAttribs().getO("CreationRoom");
		player.closeInterfaces();
		if (position == null)
			return;
		Room room = rooms[slot];
		if ((room == Room.TREASURE_ROOM || room == Room.DUNGEON_CORRIDOR || room == Room.DUNGEON_JUNCTION || room == Room.DUNGEON_PIT || room == Room.DUNGEON_STAIRS) && position[2] != 0) {
			player.sendMessage("That room can only be built underground.");
			return;
		}
		if (room == Room.THRONE_ROOM)
			if (position[2] != 1) {
				player.sendMessage("This room cannot be built on a second level or underground.");
				return;
			}
		if (room == Room.OUTBLIETTE) {
			player.sendMessage("That room can only be built using a throne room trapdoor.");
			return;
		}
		if ((room == Room.GARDEN || room == Room.FORMAL_GARDEN || room == Room.MENAGERIE) && position[2] != 1) {
			player.sendMessage("That room can only be built on ground.");
			return;
		}
		if (room == Room.MENAGERIE && hasRoom(Room.MENAGERIE)) {
			player.sendMessage("You can only build one menagerie.");
			return;
		}
		if (room == Room.GAMES_ROOM && hasRoom(Room.GAMES_ROOM)) {
			player.sendMessage("You can only build one game room.");
			return;
		}
		if (room.getLevel() > player.getSkills().getLevel(Constants.CONSTRUCTION)) {
			player.sendMessage("You need a Construction level of " + room.getLevel() + " to build this room.");
			return;
		}
		if (player.getInventory().getAmountOf(995) < room.getPrice()) {
			player.sendMessage("You don't have enough coins to build this room.");
			return;
		}
		if (roomsR.size() >= getMaxQuantityRooms()) {
			player.sendMessage("You have reached the maxium quantity of rooms.");
			return;
		}
		player.getDialogueManager().execute(new CreateRoomD(), new RoomReference(room, position[0], position[1], position[2], 0));
	}

	public boolean hasRoom(Room room) {
		for (RoomReference r : roomsR)
			if (r.room == room)
				return true;
		return false;
	}

	private int getMaxQuantityRooms() {
		int consLvl = player.getSkills().getLevelForXp(Constants.CONSTRUCTION);
		int maxRoom = 40;
		if (consLvl >= 38) {
			maxRoom += (consLvl - 32) / 6;
			if (consLvl == 99)
				maxRoom++;
		}
		return maxRoom;
	}

	public void createRoom(RoomReference room) {
		if (player.getInventory().getNumberOf(995) < room.room.getPrice()) {
			player.sendMessage("You don't have enough coins to build this room.");
			return;
		}
		player.getInventory().deleteItem(995, room.room.getPrice());
		player.getTempAttribs().setO("CRef", room);
		roomsR.add(room);
		refreshNumberOfRooms();
		refreshHouse();
	}

	public void openBuildInterface(GameObject object, final Builds build) {
		if (!buildMode) {
			player.getDialogueManager().execute(new SimpleMessage(), "You can only do that in building mode.");
			return;
		}
		int roomX = object.getChunkX() - region.getBaseChunkX();
		int roomY = object.getChunkY() - region.getBaseChunkY();
		RoomReference room = getRoom(roomX, roomY, object.getPlane());
		if (room == null)
			return;
		Item[] itemArray = new Item[build.getPieces().length];
		int requirimentsValue = 0;
		for (int index = 0; index < build.getPieces().length; index++) {
			if ((build == Builds.PORTALS1 || build == Builds.PORTALS2 || build == Builds.PORTALS3) && index > 2)
				continue;
			HObject piece = build.getPieces()[index];
			itemArray[index] = new Item(piece.getItemId(), 1);
			if (hasRequirimentsToBuild(false, build, piece))
				requirimentsValue += Math.pow(2, index + 1);
		}
		player.getPackets().sendVarc(841, requirimentsValue);
		player.getPackets().sendItems(398, itemArray);
		player.getPackets().setIFTargetParams(new IFTargetParams(1306, 55, -1, -1).enableContinueButton()); // exit
		// button
		for (int i = 0; i < itemArray.length; i++)
			player.getPackets().setIFTargetParams(new IFTargetParams(1306, 8 + 7 * i, 4, 4).enableContinueButton());
		// options
		player.getInterfaceManager().sendInterface(1306);
		player.getTempAttribs().setO("OpenedBuild", build);
		player.getTempAttribs().setO("OpenedBuildObject", object);
		player.getDialogueManager().execute(new BuildD());
		player.setCloseInterfacesEvent(() -> {
			player.getTempAttribs().removeO("OpenedBuild");
			player.getTempAttribs().removeO("OpenedBuildObject");
		});
	}

	private boolean hasRequirimentsToBuild(boolean warn, Builds build, HObject piece) {
		int level = player.getSkills().getLevel(Constants.CONSTRUCTION);
		if (!build.isWater() && player.getInventory().containsOneItem(9625))
			level += 3;
		if (level < piece.getLevel()) {
			if (warn)
				player.sendMessage("Your construction level is too low.");
			return false;
		}
		if (!player.hasRights(Rights.ADMIN)) {
			if (!player.getInventory().containsItems(piece.getRequirements(player))) {
				if (warn)
					player.sendMessage("You dont have the right materials.");
				return false;
			}
			if (build.isWater() ? !hasWaterCan() : (!player.getInventory().containsItem(HouseConstants.HAMMER, 1) || (!player.getInventory().containsItem(HouseConstants.SAW, 1) && !player.getInventory().containsOneItem(9625)))) {
				if (warn)
					player.sendMessage(build.isWater() ? "You will need a watering can with some water in it instead of hammer and saw to build plants." : "You will need a hammer and saw to build furniture.");
				return false;
			}
		}
		return true;
	}

	public void build(int slot) {
		final Builds build = player.getTempAttribs().getO("OpenedBuild");
		GameObject object = player.getTempAttribs().getO("OpenedBuildObject");
		if (build == null || object == null || build.getPieces().length <= slot)
			return;
		int roomX = object.getChunkX() - region.getBaseChunkX();
		int roomY = object.getChunkY() - region.getBaseChunkY();
		final RoomReference room = getRoom(roomX, roomY, object.getPlane());
		if (room == null)
			return;
		final HObject piece = build.getPieces()[slot];
		if (!hasRequirimentsToBuild(true, build, piece))
			return;
		final ObjectReference oref = room.addObject(build, slot);
		player.closeInterfaces();
		player.lock();
		player.setNextAnimation(new Animation(build.isWater() ? 2293 : 3683));
		if (!player.hasRights(Rights.ADMIN))
			for (Item item : piece.getRequirements(player))
				player.getInventory().deleteItem(item);
		player.lock();
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getSkills().addXp(Constants.CONSTRUCTION, piece.getXP());
				if (build.isWater())
					player.getSkills().addXp(Constants.FARMING, piece.getXP());
				refreshObject(room, oref, false);
				player.lock(1);
			}
		}, 2);
	}

	private void refreshObject(RoomReference rref, ObjectReference oref, boolean remove) {
		int boundX = rref.x * 8;
		int boundY = rref.y * 8;
		Region region = World.getRegion(this.region.getRegionId(), true);
		for (int x = 0; x < 8; x++)
			for (int y = 0; y < 8; y++) {
				GameObject[] objects = region.getObjects(rref.plane, boundX + x, boundY + y);
				if (objects != null)
					for (GameObject object : objects) {
						if (object == null)
							continue;
						int slot = oref.build.getIdSlot(object.getId());
						if (slot == -1)
							continue;
						if (remove)
							World.spawnObject(object);
						else {
							GameObject objectR = new GameObject(object);
							if (oref.getId(slot) == -1)
								World.spawnObject(new GameObject(-1, object.getType(), object.getRotation(), object));
							else {
								objectR.setId(oref.getId(slot));
								World.spawnObject(objectR);
							}
						}
					}
			}
	}

	public boolean hasWaterCan() {
		for (int id = 5333; id <= 5340; id++)
			if (player.getInventory().containsOneItem(id))
				return true;
		return false;
	}

	public void setServantOrdinal(byte ordinal) {
		if (ordinal == -1) {
			removeServant();
			servant = null;
			refreshServantVarBit();
			return;
		}
		refreshServantVarBit();
		servant = HouseConstants.Servant.values()[ordinal];
	}

	public boolean hasServant() {
		return servant != null;
	}

	public void refreshServantVarBit() {
		int bit = servant == null ? 0 : ((servant.ordinal()*2)+1);
		if (servant != null && servant == Servant.DEMON_BUTLER)
			bit = 8;
		player.getVars().setVarBit(2190, bit);
	}

	public void openRemoveBuild(GameObject object) {
		if (!buildMode) {
			player.getDialogueManager().execute(new SimpleMessage(), "You can only do that in building mode.");
			return;
		}
		if (object.getId() == HouseConstants.HObject.EXIT_PORTAL.getId() && getPortalCount() <= 1) {
			player.getDialogueManager().execute(new SimpleMessage(), "Your house must have at least one exit portal.");
			return;
		}
		int roomX = object.getChunkX() - region.getBaseChunkX();
		int roomY = object.getChunkY() - region.getBaseChunkY();
		RoomReference room = getRoom(roomX, roomY, object.getPlane());
		if (room == null)
			return;
		ObjectReference ref = room.getObject(object);
		if (ref != null) {
			if (ref.build.toString().contains("STAIRCASE"))
				if (object.getPlane() != 1) {
					RoomReference above = getRoom(roomX, roomY, 2);
					RoomReference below = getRoom(roomX, roomY, 0);
					if ((above != null && above.getStaircaseSlot() != -1) || (below != null && below.getStaircaseSlot() != -1))
						player.getDialogueManager().execute(new SimpleMessage(), "You cannot remove a building that is supporting this room.");
					return;
				}
			player.getDialogueManager().execute(new RemoveBuildD(), object);
		}
	}

	public void removeBuild(final GameObject object) {
		if (!buildMode) { // imagine u use settings to change while dialogue
			// open, cheater :p
			player.getDialogueManager().execute(new SimpleMessage(), "You can only do that in building mode.");
			return;
		}
		int roomX = object.getChunkX() - region.getBaseChunkX();
		int roomY = object.getChunkY() - region.getBaseChunkY();
		final RoomReference room = getRoom(roomX, roomY, object.getPlane());
		if (room == null)
			return;
		final ObjectReference oref = room.removeObject(object);
		if (oref == null)
			return;
		player.lock();
		player.setNextAnimation(new Animation(3685));
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				World.removeObject(object);
				refreshObject(room, oref, true);
				player.lock(1);
			}
		}, 1);
	}

	public boolean isDoor(GameObject object) {
		return object.getDefinitions().getName().equalsIgnoreCase("Door hotspot");
	}

	public boolean isBuildMode() {
		return buildMode;
	}

	public boolean isDoorSpace(GameObject object) {
		return object.getDefinitions().getName().equalsIgnoreCase("Door space");
	}

	public void switchLock(Player player) {
		if (!isOwner(player)) {
			player.sendMessage("You can only lock your own house.");
			return;
		}
		locked = !locked;
		if (locked)
			player.getDialogueManager().execute(new SimpleMessage(), "Your house is now locked to visitors.");
		else if (buildMode)
			player.getDialogueManager().execute(new SimpleMessage(), "Visitors will be able to enter your house once you leave building mode.");
		else
			player.getDialogueManager().execute(new SimpleMessage(), "You have unlocked your house.");
	}

	public static void enterHouse(Player player, String username) {
		Player owner = World.getPlayer(username); //TODO
		if (owner == null || !owner.isRunning() /*|| !player.getFriendsIgnores().onlineTo(owner)*/ || owner.getHouse() == null || owner.getHouse().locked) {
			player.sendMessage("That player is offline, or has privacy mode enabled.");
			return;
		}
		if (owner.getHouse().location == null || !player.withinDistance(owner.getHouse().location.getTile(), 16)) {
			player.sendMessage("That player's house is at " + Utils.formatPlayerNameForDisplay(owner.getHouse().location.name()).replace("Portal", "") + ".");
			return;
		}
		owner.getHouse().joinHouse(player);
	}

	public boolean joinHouse(final Player player) {
		if (!isOwner(player)) { // not owner
			if (!isOwnerInside() || !loaded) {
				player.sendMessage("That player is offline, or has privacy mode enabled.");
				return false;
			}
			if (buildMode) {
				player.sendMessage("The owner currently has build mode turned on.");
				return false;
			}
		}
		players.add(player);
		sendStartInterface(player);
		player.getControllerManager().startController(new HouseController(this));
		if (loaded) {
			teleportPlayer(player);
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					player.lock(1);
					player.getInterfaceManager().setDefaultTopInterface();
				}
			}, 4);
		} else
			createHouse();
		return true;
	}

	public static void leaveHouse(Player player) {
		Controller controller = player.getControllerManager().getController();
		if (controller == null || !(controller instanceof HouseController)) {
			player.sendMessage("You're not in a house.");
			return;
		}
		player.setCanPvp(false);
		player.removeHouseOnlyItems();
		player.lock(2);
		((HouseController) controller).getHouse().leaveHouse(player, KICKED);
	}

	/*
	 * 0 - logout, 1 kicked/tele outside outside, 2 tele somewhere else
	 */
	public void leaveHouse(Player player, int type) {
		player.setCanPvp(false);
		player.removeHouseOnlyItems();
		player.getControllerManager().removeControllerWithoutCheck();
		if (type == LOGGED_OUT)
			player.setLocation(location.getTile());
		else if (type == KICKED)
			player.useStairs(-1, location.getTile(), 0, 1);
		if (players != null && players.contains(player))
			players.remove(player);
		if (players == null || players.size() == 0)
			destroyHouse();
		if (type != LOGGED_OUT)
			player.lock(2);
		if (player.getAppearance().getRenderEmote() != -1)
			player.getAppearance().setBAS(-1);
		if (isOwner(player) && servantInstance != null)
			servantInstance.setFollowing(false);
		player.getTempAttribs().setB("inBoxingArena", false);
		player.setCanPvp(false);
		player.setForceMultiArea(false);
	}

	private void removeServant() {
		if (servantInstance != null) {
			servantInstance.finish();
			servantInstance = null;
		}
	}

	private void addServant() {
		if (servantInstance == null && servant != null)
			servantInstance = new ServantNPC(this);
	}

	public Servant getServant() {
		return servant;
	}

	private void refreshServant() {
		removeServant();
		addServant();
	}

	public void callServant(boolean bellPull) {
		if (bellPull) {
			player.setNextAnimation(new Animation(3668));
			player.lock(2);
		}
		if (servantInstance == null)
			player.sendMessage("The house has no servant.");
		else {
			servantInstance.setFollowing(true);
			servantInstance.setNextWorldTile(World.getFreeTile(player, 1));
			servantInstance.setNextAnimation(new Animation(858));
			player.getDialogueManager().execute(new ServantDialogue(), servantInstance);
		}
	}

	public ServantNPC getServantInstance() {
		return servantInstance;
	}

	/*
	 * refers to logout
	 */
	public void finish() {
		kickGuests();
		// no need to leave house for owner, controller does that itself
	}

	public void refreshHouse() {
		destroyHouse();
		loaded = false;
		sendStartInterface(player);
		createHouse();
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void sendStartInterface(Player player) {
		player.lock();
		player.getInterfaceManager().setTopInterface(399, false);
		player.getMusicsManager().playMusic(454);
		player.getPackets().sendMusicEffect(22);
	}

	public void teleportPlayer(Player player) {
		player.setNextWorldTile(getPortal());
	}

	public void teleportPlayer(Player player, RoomReference room) {
		player.setNextWorldTile(new WorldTile(region.getLocalX(room.x, 3), region.getLocalY(room.y, 3), room.plane));
	}

	public WorldTile getPortal() {
		if (region == null)
			throw new RuntimeException("BoundChunks were null so room could not be entered.");
		for (RoomReference room : roomsR) {
			if (room == null) {
				System.err.println("RoomReference 'room' object was null.");
				continue;
			}
			if (room.room == HouseConstants.Room.GARDEN || room.room == HouseConstants.Room.FORMAL_GARDEN)
				for (ObjectReference o : room.objects) {
					if (o == null) {
						System.err.println("ObjectReference instance was null");
						continue;
					}
					if (o.getPiece() == HouseConstants.HObject.EXIT_PORTAL || o.getPiece() == HouseConstants.HObject.EXITPORTAL)
						return new WorldTile(region.getLocalX(room.x, 3), region.getLocalY(room.y, 3), room.plane);
				}
		}
		return new WorldTile(region.getLocalX(32), region.getLocalX(32), 1);
	}

	public int getPortalCount() {
		int count = 0;
		for (RoomReference room : roomsR)
			if (room.room == HouseConstants.Room.GARDEN || room.room == HouseConstants.Room.FORMAL_GARDEN)
				for (ObjectReference o : room.objects)
					if (o.getPiece() == HouseConstants.HObject.EXIT_PORTAL || o.getPiece() == HouseConstants.HObject.EXITPORTAL)
						count++;
		return count;
	}

	public RoomReference getMenagerie() {
		for (RoomReference room : roomsR)
			if (room.room == HouseConstants.Room.MENAGERIE)
				for (ObjectReference o : room.objects)
					if (o.getPiece() == HouseConstants.HObject.OAKPETHOUSE || o.getPiece() == HouseConstants.HObject.TEAKPETHOUSE || o.getPiece() == HouseConstants.HObject.MAHOGANYPETHOUSE || o.getPiece() == HouseConstants.HObject.CONSECRATEDPETHOUSE || o.getPiece() == HouseConstants.HObject.DESECRATEDPETHOUSE || o.getPiece() == HouseConstants.HObject.NATURALPETHOUSE)
						return room;
		return null;
	}

	public RoomReference getPortalRoom() {
		for (RoomReference room : roomsR)
			if (room.room == HouseConstants.Room.GARDEN || room.room == HouseConstants.Room.FORMAL_GARDEN)
				for (ObjectReference o : room.objects)
					if (o.getPiece() == HouseConstants.HObject.EXIT_PORTAL || o.getPiece() == HouseConstants.HObject.EXITPORTAL)
						return room;
		return null;
	}

	public House() {
		buildMode = true;
		petHouse = new PetHouse();
		roomsR = new ArrayList<>();
		location = POHLocation.TAVERLY;
		addRoom(HouseConstants.Room.GARDEN, 3, 3, 0, 0);
		getRoom(3, 3, 0).addObject(Builds.CENTREPIECE, 0);
	}

	public boolean addRoom(HouseConstants.Room room, int x, int y, int plane, int rotation) {
		return roomsR.add(new RoomReference(room, x, y, plane, rotation));
	}

	/*
	 * temporary
	 */
	public void reset() {
		build = 1;
		buildMode = true;
		roomsR = new ArrayList<>();
		addRoom(HouseConstants.Room.GARDEN, 3, 3, 1, 0);
		getRoom(3, 3, 1).addObject(Builds.CENTREPIECE, 0);
	}

	public void init() {
		if (build == 0)
			reset();
		players = new ArrayList<>();
		refreshBuildMode();
		refreshArriveInPortal();
		refreshNumberOfRooms();
	}

	public void refreshNumberOfRooms() {
		player.getPackets().sendVarc(944, roomsR.size());
	}

	public void setArriveInPortal(boolean arriveInPortal) {
		this.arriveInPortal = arriveInPortal;
		refreshArriveInPortal();
	}

	public boolean arriveOutsideHouse() {
		return arriveInPortal;
	}

	public void refreshArriveInPortal() {
		player.getVars().setVarBit(6450, arriveInPortal ? 1 : 0);
	}

	public void toggleChallengeMode(Player player) {
		if (isOwner(player)) {
			if (!challengeMode)
				setChallengeMode(true);
			else
				setChallengeMode(false);
		} else
			player.sendMessage("Only the house owner can toggle challenge mode on or off.");

	}

	public void setBuildMode(boolean buildMode) {
		if (this.buildMode == buildMode)
			return;
		this.buildMode = buildMode;
		if (loaded) {
			expelGuests();
			if (isOwnerInside())
				refreshHouse();
		}
		refreshBuildMode();
	}

	public void refreshBuildMode() {
		player.getVars().setVarBit(2176, buildMode ? 1 : 0);
	}

	public RoomReference getRoom(int x, int y, int plane) {
		for (RoomReference room : roomsR)
			if (room.x == x && room.y == y && room.plane == plane)
				return room;
		return null;
	}

	public RoomReference getRoom(GameObject o) {
		int roomX = o.getChunkX() - region.getBaseChunkX();
		int roomY = o.getChunkY() - region.getBaseChunkY();
		return getRoom(roomX, roomY, o.getPlane());
	}

	public List<RoomReference> getRooms() {
		return roomsR;
	}

	public RoomReference getRoom(Room room) {
		for (RoomReference roomR : roomsR)
			if (room == roomR.getRoom())
				return roomR;
		return null;
	}

	public boolean isSky(int x, int y, int plane) {
		return buildMode && plane == 2 && getRoom((x / 8) - region.getBaseChunkX(), (y / 8) - region.getBaseChunkY(), plane) == null;
	}

	public void previewRoom(RoomReference reference, boolean remove) {
		if (!loaded)
			return;
		int boundX = region.getLocalX(reference.x, 0);
		int boundY = region.getLocalY(reference.y, 0);
		int realChunkX = reference.room.getChunkX();
		int realChunkY = reference.room.getChunkY();
		Region region = World.getRegion(RegionUtils.encode(RegionUtils.Structure.REGION, realChunkX / 8, realChunkY / 8), true);
		if (reference.plane == 0)
			for (int x = 0; x < 8; x++)
				for (int y = 0; y < 8; y++) {
					GameObject objectR = new GameObject(-1, ObjectType.SCENERY_INTERACT, reference.rotation, boundX + x, boundY + y, reference.plane);
					if (remove)
						World.removeObject(objectR);
					else
						World.spawnObject(objectR);
				}
		for (int x = 0; x < 8; x++)
			for (int y = 0; y < 8; y++) {
				GameObject[] objects = region.getAllObjects(look & 0x3, (realChunkX & 0x7) * 8 + x, (realChunkY & 0x7) * 8 + y);
				if (objects != null)
					for (GameObject object : objects) {
						if (object == null)
							continue;
						ObjectDefinitions defs = object.getDefinitions();
						if (reference.plane == 0 || defs.containsOption(4, "Build")) {
							GameObject objectR = new GameObject(object);
							int[] coords = DynamicRegion.translate(x, y, reference.rotation, defs.sizeX, defs.sizeY, object.getRotation());
							objectR.setLocation(new WorldTile(boundX + coords[0], boundY + coords[1], reference.plane));
							objectR.setRotation((object.getRotation() + reference.rotation) & 0x3);
							// just a preview. they're not realy there.
							if (remove)
								World.removeObject(objectR);
							else
								World.spawnObject(objectR);
						}
					}
			}
	}

	public void destroyHouse() {
		loaded = false;
		if (pets == null)
			pets = new CopyOnWriteArrayList<>();
		if (npcs == null)
			npcs = new CopyOnWriteArrayList<>();
		for (NPC npc : pets)
			if (npc != null) {
				npc.finish();
				pets.remove(npc);
			}
		for (NPC npc : npcs)
			if (npc != null) {
				npc.finish();
				npcs.remove(npc);
			}
		removeServant();
		npcs.clear();
		pets.clear();
		if (region != null)
			region.destroy();
	}

	private static final int[] DOOR_DIR_X = { -1, 0, 1, 1 };
	private static final int[] DOOR_DIR_Y = { 0, 1, 0, -1 };

	public void createHouse() {
		challengeMode = false;
		Object[][][][] data = new Object[4][8][8][];
		// sets rooms data
		for (RoomReference reference : roomsR)
			data[reference.plane][reference.x][reference.y] = new Object[] { reference.room.getChunkX(), reference.room.getChunkY(), reference.rotation, reference.room.isShowRoof() };
		// sets roof data
		if (!buildMode)
			for (int x = 1; x < 7; x++)
				skipY: for (int y = 1; y < 7; y++)
					for (int plane = 2; plane >= 1; plane--)
						if (data[plane][x][y] != null) {
							boolean hasRoof = (boolean) data[plane][x][y][3];
							if (hasRoof) {
								byte rotation = (byte) data[plane][x][y][2];
								// TODO find best Roof
								data[plane + 1][x][y] = new Object[] { HouseConstants.Roof.ROOF1.getChunkX(), HouseConstants.Roof.ROOF1.getChunkY(), rotation, true };
								continue skipY;
							}
						}
		if (region != null && !region.isDestroyed())
			region.destroy();
		region = new DynamicRegionReference(8, 8);
		region.requestChunkBound(() -> {
			// builds data
			for (int plane = 0; plane < data.length; plane++)
				for (int x = 0; x < data[plane].length; x++)
					for (int y = 0; y < data[plane][x].length; y++)
						if (data[plane][x][y] != null)
							region.copyChunk(x, y, plane, (int) data[plane][x][y][0] + (look >= 4 ? 8 : 0), (int) data[plane][x][y][1], look & 0x3, (byte) data[plane][x][y][2], null);
						else if ((x == 0 || x == 7 || y == 0 || y == 7) && plane == 1)
							region.copyChunk(x, y, plane, HouseConstants.BLACK[0], HouseConstants.BLACK[1], 0, 0, null);
						else if (plane == 1)
							region.copyChunk(x, y, plane, HouseConstants.LAND[0] + (look >= 4 ? 8 : 0), HouseConstants.LAND[1], look & 0x3, 0, null);
						else if (plane == 0)
							region.copyChunk(x, y, plane, HouseConstants.BLACK[0], HouseConstants.BLACK[1], 0, 0, null);
						else
							region.clearChunk(x, y, plane, null);

			World.executeAfterLoadRegion(region.getRegionId(), () -> {
				Region r = World.getRegion(region.getRegionId(), true);
				List<GameObject> spawnedObjects = r.getSpawnedObjects();
				if (spawnedObjects != null)
					for (GameObject object : spawnedObjects)
						World.removeObject(object);
				List<GameObject> removedObjects = new ArrayList<>(r.getRemovedObjects().values());
				if (removedObjects != null)
					for (GameObject object : removedObjects)
						World.spawnObject(object);
				for (RoomReference reference : roomsR) {
					int boundX = reference.x * 8;
					int boundY = reference.y * 8;
					for (int x = 0; x < 8; x++)
						for (int y = 0; y < 8; y++) {
							GameObject[] objects = World.getRegion(region.getRegionId()).getObjects(reference.plane, boundX + x, boundY + y);
							if (objects != null)
								skip: for (GameObject object : objects) {
									if (object == null)
										continue;
									if (object.getDefinitions().containsOption(4, "Build") || (reference.room == Room.MENAGERIE && object.getDefinitions().getName().contains("space"))) {
										if (isDoor(object)) {
											if (!buildMode && object.getPlane() == 2 && getRoom(((object.getX() / 8) - region.getBaseChunkX()) + DOOR_DIR_X[object.getRotation()], ((object.getY() / 8) - region.getBaseChunkY()) + DOOR_DIR_Y[object.getRotation()], object.getPlane()) == null) {
												GameObject objectR = new GameObject(object);
												objectR.setId(HouseConstants.WALL_IDS[look]);
												World.spawnObject(objectR);
												continue;
											}
										} else
											for (ObjectReference o : reference.objects) {
												int slot = o.build.getIdSlot(object.getId());
												if (slot != -1) {
													GameObject objectR = new GameObject(object);
													if (o.getId(slot) == -1)
														World.spawnObject(new GameObject(-1, object.getType(), object.getRotation(), object));
													else if (!spawnNpcs(slot, o, object)) {
														objectR.setId(o.getId(slot));
														World.spawnObject(objectR);
													}
													continue skip;
												}
											}
										if (!buildMode)
											World.removeObject(object);
									} else if (object.getId() == HouseConstants.WINDOW_SPACE_ID) {
										object = new GameObject(object);
										object.setId(HouseConstants.WINDOW_IDS[look]);
										World.spawnObject(object);
									} else if (isDoorSpace(object))
										World.removeObject(object);
								}
						}
				}
				teleportPlayer(player);
				player.setForceNextMapLoadRefresh(true);
				player.loadMapRegions();
				player.lock(1);
				player.getInterfaceManager().setDefaultTopInterface();
				if (!buildMode)
					if (getMenagerie() != null)
						for (Item item : petHouse.getPets().getItems())
							if (item != null)
								addPet(item, false);
				refreshServant();
				if (player.getTempAttribs().getO("CRef") != null && player.getTempAttribs().getO("CRef") instanceof RoomReference toRoom) {
					player.getTempAttribs().removeO("CRef");
					teleportPlayer(player, toRoom);
				}
				loaded = true;
			});
		});
	}

	public boolean containsAnyObject(int... ids) {
		Region region = World.getRegion(this.region.getRegionId(), true);
		List<GameObject> spawnedObjects = region.getSpawnedObjects();
		for (GameObject wo : spawnedObjects)
			for (int id : ids)
				if (wo.getId() == id)
					return true;
		return false;
	}

	public void removePet(Item item, boolean update) {
		if (update && !isOwnerInside())
			return;
		if (!buildMode)
			if (getMenagerie() != null) {
				Pets pet = Pets.forId(item.getId());
				if (pet == null)
					return;

				int npcId = 0;
				if (pet.getGrownItemId() == item.getId())
					npcId = pet.getGrownNpcId();
				else
					npcId = pet.getBabyNpcId();
				for (NPC npc : pets)
					if (npc != null && npc.getId() == npcId) {
						npc.finish();
						pets.remove(npc);
						break;
					}
			}
	}

	public void addPet(Item item, boolean update) {
		if (update && !isOwnerInside())
			return;
		if (!buildMode)
			if (getMenagerie() != null) {
				RoomReference men = getMenagerie();
				WorldTile spawn = new WorldTile(region.getLocalX(men.x, 3), region.getLocalY(men.y, 3), men.plane);

				Pets pet = Pets.forId(item.getId());
				if (pet == null)
					return;

				NPC npc = new NPC(1, spawn);
				if (pet.getGrownItemId() == item.getId())
					npc.setNPC(pet.getGrownNpcId());
				else
					npc.setNPC(pet.getBabyNpcId());
				pets.add(npc);
				npc.setRandomWalk(true);
			}
	}

	public boolean spawnNpcs(int slot, ObjectReference oRef, GameObject object) {
		if (buildMode)
			return false;
		if (oRef.getId(slot) == HouseConstants.HObject.ROCNAR.getId() || oRef.build == Builds.PITGUARD || oRef.build == Builds.GUARDIAN || oRef.build == Builds.GUARD2 || oRef.build == Builds.GUARD3 || oRef.build == Builds.GUARD4 || oRef.build == Builds.GUARD5) {
			if (oRef.getId(slot) == HouseConstants.HObject.DEMON.getId()) {
				spawnNPC(3593, object);
				return true;
			}
			if (oRef.getId(slot) == HouseConstants.HObject.KALPHITESOLDIER.getId()) {
				spawnNPC(3589, object);
				return true;
			} else if (oRef.getId(slot) == HouseConstants.HObject.TOKXIL.getId()) {
				spawnNPC(3592, object);
				return true;
			} else if (oRef.getId(slot) == HouseConstants.HObject.DAGANNOTH.getId()) {
				spawnNPC(3591, object);
				return true;
			} else if (oRef.getId(slot) == HouseConstants.HObject.STEELDRAGON.getId()) {
				spawnNPC(3590, object);
				return true;
			} else if (oRef.getId(slot) == HouseConstants.HObject.SKELETON.getId()) {
				spawnNPC(3581, object);
				return true;
			} else if (oRef.getId(slot) == HouseConstants.HObject.GUARDDOG.getId()) {
				spawnNPC(3582, object);
				return true;
			} else if (oRef.getId(slot) == HouseConstants.HObject.HOBGOBLIN.getId()) {
				spawnNPC(3583, object);
				return true;
			} else if (oRef.getId(slot) == HouseConstants.HObject.BABYREDDRAGON.getId()) {
				spawnNPC(3588, object);
				return true;
			} else if (oRef.getId(slot) == HouseConstants.HObject.HUGESPIDER.getId()) {
				spawnNPC(3585, object);
				return true;
			} else if (oRef.getId(slot) == HouseConstants.HObject.HELLHOUND.getId()) {
				spawnNPC(3586, object);
				return true;
			} else if (oRef.getId(slot) == HouseConstants.HObject.TROLLGUARD.getId()) {
				spawnNPC(3584, object);
				return true;
			} else if (oRef.getId(slot) == HouseConstants.HObject.PITDOG.getId()) {
				spawnNPC(11585, object);
				return true;
			} else if (oRef.getId(slot) == HouseConstants.HObject.PITOGRE.getId()) {
				spawnNPC(11587, object);
				return true;
			} else if (oRef.getId(slot) == HouseConstants.HObject.PITROCKPROTECTER.getId()) {
				spawnNPC(11589, object);
				return true;
			} else if (oRef.getId(slot) == HouseConstants.HObject.PITSCABARITE.getId()) {
				spawnNPC(11591, object);
				return true;
			} else if (oRef.getId(slot) == HouseConstants.HObject.PITBLACKDEMON.getId()) {
				spawnNPC(11593, object);
				return true;
			} else if (oRef.getId(slot) == HouseConstants.HObject.PITIRONDRAGON.getId()) {
				spawnNPC(11595, object);
				return true;
			} else if (oRef.getId(slot) == HouseConstants.HObject.ROCNAR.getId()) {
				spawnNPC(3594, object);
				return true;
			}
			return false;
		}
		return false;
	}

	public void spawnNPC(int id, GameObject object) {
		NPC npc = new NPC(id, new WorldTile(object.getX(), object.getY(), object.getPlane()));
		npcs.add(npc);
		npc.setRandomWalk(false);
		npc.setForceMultiArea(true);
		World.removeObject(object);
	}

	public boolean isWindow(int id) {
		return id == 13830;
	}

	public GameObject getWorldObjectForBuild(RoomReference reference, Builds build) {
		int boundX = region.getLocalX(reference.x, 0);
		int boundY = region.getLocalY(reference.y, 0);
		for (int x = -1; x < 8; x++)
			for (int y = -1; y < 8; y++)
				for (HObject piece : build.getPieces()) {
					GameObject object = World.getObjectWithId(new WorldTile(boundX + x, boundY + y, reference.plane), piece.getId());
					if (object != null)
						return object;
				}
		return null;
	}

	public GameObject getWorldObject(RoomReference reference, int id) {
		int boundX = region.getLocalX(reference.x, 0);
		int boundY = region.getLocalY(reference.y, 0);
		for (int x = -1; x < 8; x++)
			for (int y = -1; y < 8; y++) {
				GameObject object = World.getObjectWithId(new WorldTile(boundX + x, boundY + y, reference.plane), id);
				if (object != null)
					return object;
			}
		return null;
	}

	public static class ObjectReference {

		private int slot;
		private Builds build;

		public ObjectReference(Builds build, int slot) {
			this.build = build;
			this.slot = slot;
		}

		public HObject getPiece() {
			if (slot > build.getPieces().length - 1) {
				System.out.println("Error getting peice for " + build.name());
				return build.getPieces()[0];
			}
			return build.getPieces()[slot];
		}

		public int getId() {
			if (slot > build.getPieces().length - 1) {
				System.out.println("Error getting id for " + build.name());
				return build.getPieces()[0].getId();
			}
			return build.getPieces()[slot].getId();
		}

		public int getSlot() {
			return slot;
		}

		public void setSlot(int slot, GameObject object) {
			this.slot = slot;

			object.setId(build.getPieces()[slot].getId());
		}


		public int[] getIds() {
			if (slot > build.getPieces().length - 1) {
				System.out.println("Error getting ids for " + build.name());
				return build.getPieces()[0].getIds();
			}
			return build.getPieces()[slot].getIds();
		}

		public Builds getBuild() {
			return build;
		}

		public int getId(int slot2) {
			if (slot2 > getIds().length - 1) {
				System.out.println("Error getting id2 for " + build.name());
				return getIds()[0];
			}
			return getIds()[slot2];
		}

	}

	public static class RoomReference {

		public RoomReference(HouseConstants.Room room, int x, int y, int plane, int rotation) {
			this.room = room;
			this.x = (byte) x;
			this.y = (byte) y;
			this.plane = (byte) plane;
			this.rotation = (byte) rotation;
			objects = new ArrayList<>();
		}

		public int getTrapObject() {
			for (ObjectReference object : objects)
				if (object.build.toString().contains("FLOOR"))
					return object.getPiece().getId();
			return -1;
		}

		private HouseConstants.Room room;
		private byte x, y, plane, rotation;
		private List<ObjectReference> objects;

		public int getLadderTrapSlot() {
			for (ObjectReference object : objects)
				if (object.build.toString().contains("OUB_LADDER") || object.build.toString().contains("TRAPDOOR"))
					return object.slot;
			return -1;
		}

		public int getStaircaseSlot() {
			for (ObjectReference object : objects)
				if (object.build.toString().contains("STAIRCASE"))
					return object.slot;
			return -1;
		}

		public boolean isStaircaseDown() {
			for (ObjectReference object : objects)
				if (object.build.toString().contains("STAIRCASE_DOWN"))
					return true;
			return false;
		}

		/*
		 * x,y inside the room chunk
		 */
		public ObjectReference addObject(Builds build, int slot) {
			ObjectReference ref = new ObjectReference(build, slot);
			objects.add(ref);
			return ref;
		}

		public ObjectReference getObject(GameObject object) {
			for (ObjectReference o : objects)
				for (int id : o.getIds())
					if (object.getId() == id)
						return o;
			return null;
		}

		public int getHObjectSlot(HObject hObject) {
			for (ObjectReference o : objects) {
				if (o == null)
					continue;
				if (hObject.getId() == o.getPiece().getId())
					return o.getSlot();
			}
			return -1;
		}

		public boolean containsHObject(HObject hObject) {
			return getHObjectSlot(hObject) != -1;
		}

		public boolean containsBuild(Builds build) {
			return getBuildSlot(build) != -1;
		}

		public int getBuildSlot(Builds build) {
			for (ObjectReference o : objects) {
				if (o == null)
					continue;
				if (o.getBuild() == build)
					return o.getSlot();
			}
			return -1;
		}

		public ObjectReference getBuild(Builds build) {
			for (ObjectReference o : objects) {
				if (o == null)
					continue;
				if (o.getBuild() == build)
					return o;
			}
			return null;
		}

		public ObjectReference removeObject(GameObject object) {
			ObjectReference r = getObject(object);
			if (r != null) {
				objects.remove(r);
				return r;
			}
			return null;
		}

		public void setRotation(int rotation) {
			this.rotation = (byte) rotation;
		}

		public byte getRotation() {
			return rotation;
		}

		public Room getRoom() {
			return room;
		}

		public int getZ() {
			return plane;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

	}

	public void changeLook(int look) {
		if (look > 6 || look < 0)
			return;
		this.look = (byte) look;
	}

	public void setPlayer(Player player) {
		this.player = player;
		if (petHouse == null)
			petHouse = new PetHouse();
		if (pets == null)
			pets = new CopyOnWriteArrayList<>();
		if (npcs == null)
			npcs = new CopyOnWriteArrayList<>();
		petHouse.setPlayer(player);
		refreshServantVarBit();
	}

	public Player getPlayer() {
		return player;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public PetHouse getPetHouse() {
		return petHouse;
	}

	public void setPetHouse(PetHouse petHouse) {
		this.petHouse = petHouse;
	}

	public boolean isChallengeMode() {
		return challengeMode;
	}

	public void setChallengeMode(boolean challengeMode) {
		this.challengeMode = challengeMode;
		for (Player player : players)
			if (player != null && player.getControllerManager().getController() instanceof HouseController) {
				player.sendMessage("<col=FF0000>The owner has turned " + (challengeMode ? "on" : "off") + " PVP dungeon challenge mode.</col>");
				player.sendMessage("<col=FF0000>The dungeon is now " + (challengeMode ? "open" : "closed") + " to PVP combat.</col>");
			}
	}
}