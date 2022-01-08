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
package com.rs.game.player.controllers;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.game.ForceMovement;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.game.player.actions.FillAction;
import com.rs.game.player.actions.FillAction.Filler;
import com.rs.game.player.content.PlayerLook;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.content.skills.construction.House;
import com.rs.game.player.content.skills.construction.House.ObjectReference;
import com.rs.game.player.content.skills.construction.House.RoomReference;
import com.rs.game.player.content.skills.construction.HouseConstants;
import com.rs.game.player.content.skills.construction.HouseConstants.Builds;
import com.rs.game.player.content.skills.construction.HouseConstants.HObject;
import com.rs.game.player.content.skills.construction.HouseConstants.POHLocation;
import com.rs.game.player.content.skills.construction.ItemOnServantD;
import com.rs.game.player.content.skills.construction.ServantHouseD;
import com.rs.game.player.content.skills.construction.ServantNPC;
import com.rs.game.player.content.skills.construction.SitChair;
import com.rs.game.player.content.skills.construction.TabletMaking;
import com.rs.game.player.content.skills.cooking.Cooking;
import com.rs.game.player.content.skills.cooking.Cooking.Cookables;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.game.player.content.skills.magic.Rune;
import com.rs.game.player.content.skills.magic.RuneSet;
import com.rs.game.player.content.transportation.ItemTeleports;
import com.rs.game.player.dialogues.CookingD;
import com.rs.game.player.dialogues.FillingD;
import com.rs.game.player.dialogues.SimpleMessage;
import com.rs.game.player.dialogues.SimpleNPCMessage;
import com.rs.game.player.quests.Quest;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.events.DialogueOptionEvent;

public class HouseController extends Controller {

	private transient House house;

	public HouseController(House house) {
		this.house = house;
	}

	@Override
	public void start() {
		player.setForceMultiArea(true);
	}

	@Override
	public boolean sendDeath() {
		player.lock(7);
		player.stopAll();
		WorldTasks.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				player.stopAll();
				if (loop == 0)
					player.setNextAnimation(new Animation(836));
				else if (loop == 1)
					player.sendMessage("Oh dear, you have died.");
				else if (loop == 3) {
					player.setNextAnimation(new Animation(-1));
					player.setNextWorldTile(house.getPortal());
					player.reset();
					player.setCanPvp(false);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public boolean processNPCClick1(NPC npc) {
		if (npc instanceof ServantNPC) {
			npc.faceEntity(player);
			if (!house.isOwner(player)) {
				player.getDialogueManager().execute(new SimpleNPCMessage(), npc.getId(), "Sorry, I only serve my master.");
				return false;
			}
			player.getDialogueManager().execute(new ServantHouseD(), npc, false);
			return false;
		}
		return true;
	}

	@Override
	public boolean processNPCClick2(NPC npc) {
		if (npc instanceof ServantNPC) {
			npc.faceEntity(player);
			if (!house.isOwner(player)) {
				player.getDialogueManager().execute(new SimpleNPCMessage(), npc.getId(), "The servant ignores your request.");
				return false;
			}
			player.getDialogueManager().execute(new ServantHouseD(), npc, true);
			return false;
		}
		return true;
	}

	@Override
	public boolean processItemOnNPC(NPC npc, Item item) {
		if (npc instanceof ServantNPC) {
			npc.faceEntity(player);
			if (!house.isOwner(player)) {
				player.getDialogueManager().execute(new SimpleNPCMessage(), npc.getId(), "The servant ignores your request.");
				return false;
			}
			player.getDialogueManager().execute(new ItemOnServantD(), npc, item.getId(), house.getServant().isSawmill());
			return false;
		}
		return false;
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processObjectClick5(GameObject object) {
		if (object.getDefinitions().containsOption(4, "Build")) {
			if (!house.isOwner(player)) {
				player.sendMessage("You can only do that in your own house.");
				return false;
			}
			if (house.isDoor(object))
				house.openRoomCreationMenu(object);
			else
				for (final Builds build : HouseConstants.Builds.values())
					if (build.containsId(object.getId())) {
						house.openBuildInterface(object, build);
						return false;
					}
		} else if (object.getDefinitions().containsOption(4, "Remove")) {
			if (!house.isOwner(player)) {
				player.sendMessage("You can only do that in your own house.");
				return false;
			}
			house.openRemoveBuild(object);
		}
		return false;
	}

	@Override
	public boolean processObjectClick1(final GameObject object) {
		if (object.getDefinitions().getOption(1).equalsIgnoreCase("Pray") || (object.getDefinitions().getOption(1).equalsIgnoreCase("Renew-points") && object.getDefinitions().getName().contains("obelisk")))
			return true;
		if (object.getId() == HouseConstants.HObject.EXIT_PORTAL.getId())
			house.leaveHouse(player, House.KICKED);
		else if (object.getId() == HouseConstants.HObject.CLAY_FIREPLACE.getId() || object.getId() == HouseConstants.HObject.STONE_FIREPLACE.getId() || object.getId() == HouseConstants.HObject.MARBLE_FIREPLACE.getId())
			player.sendMessage("Use some logs on the fireplace in order to light it.");
		else if ((object.getId() >= 13581 && object.getId() <= 13587) || (object.getId() >= 13300 && object.getId() <= 13306) || (object.getId() >= 13665 && object.getId() <= 13671) || (object.getId() >= 13694 && object.getId() <= 13696))
			player.getActionManager().setAction(new SitChair(player, object));
		else if (house.isOwner(player) && (object.getId() == 13640 || object.getId() == 13641 || object.getId() == 13639))
			directPortals(player, object);
		else if (object.getId() >= 13615 && object.getId() <= 13635)
			telePlayer(object.getId());
		else if (HouseConstants.Builds.DRESSERS.containsObject(object))
			PlayerLook.openHairdresserSalon(player);
		else if (HouseConstants.Builds.WARDROBE.containsObject(object))
			PlayerLook.openThessaliasMakeOver(player);
		else if (object.getId() == HouseConstants.HObject.GLORY.getId())
			ItemTeleports.transportationDialogue(player, 1712);
		else if (HouseConstants.Builds.LARDER.containsObject(object))
			handleLarders(object);
		else if (HouseConstants.Builds.SHELVES.containsObject(object))
			handleShelves(object);
		else if (HouseConstants.Builds.TOOL1.containsObject(object))
			handleTools(object);
		else if (HouseConstants.Builds.WEAPONS_RACK.containsObject(object))
			handleWeapons(object);
		else if (HouseConstants.Builds.LEVER.containsObject(object))
			handleLever(object);
		else if (HouseConstants.Builds.ROPE_BELL_PULL.containsObject(object)) {
			if (!house.isOwner(player)) {
				player.getPackets()
				.sendGameMessage("I'd better not do this...");
				return false;
			}
			house.callServant(true);
			return false;
		} else if (HouseConstants.Builds.LECTURN.containsObject(object)) {
			if (house.isBuildMode()) {
				player.sendMessage("You cannot do this while in building mode.");
				return false;
			}
			TabletMaking.openTabInterface(player, object.getId() - 13642);
		} else if (HouseConstants.Builds.BOOKCASE.containsObject(object))
			player.sendMessage("You search the bookcase but find nothing.");
		else if (HouseConstants.Builds.STAIRCASE.containsObject(object) || HouseConstants.Builds.STAIRCASE_DOWN.containsObject(object)) {
			if (object.getDefinitions().getOption(1).equals("Climb"))
				player.sendOptionDialogue("Would you like to climb up or down?", new String[] { "Climb up.", "Climb down." }, new DialogueOptionEvent() {

					@Override
					public void run(Player player) {
						if (getOption() == 1)
							house.climbStaircase(player, object, true);
						else
							house.climbStaircase(player, object, false);
					}

				});
			else
				house.climbStaircase(player, object, object.getDefinitions().getOption(1).equals("Climb-up"));
		} else if (HouseConstants.Builds.PETHOUSE.containsObject(object)) {
			if (house.isOwner(player))
				house.getPetHouse().open();
			else
				player.sendMessage("This isn't your pet house.");
		} else if (HouseConstants.Builds.OUB_LADDER.containsObject(object) || HouseConstants.Builds.TRAPDOOR.containsObject(object))
			house.climbLadder(player, object, object.getDefinitions().getOption(1).equals("Climb"));
		else if (HouseConstants.Builds.DOOR.containsObject(object) || HouseConstants.Builds.OUB_CAGE.containsObject(object))
			handleDoor(object);
		else if (HouseConstants.Builds.COMBAT_RING.containsObject(object))
			handleCombatRing(object);
		return false;
	}

	private void handleLever(GameObject object) {
		house.handleLever(player, object);
	}

	private void handleWeapons(GameObject object) {
		if (object.getId() == HObject.GLOVE_RACK.getId())
			sendTakeItemsDialogue(7671, 7673);
		else if (object.getId() == HObject.WEAPON_RACK.getId())
			sendTakeItemsDialogue(7671, 7673, 7675, 7676);
		else if (object.getId() == HObject.EXTRA_WEAPON_RACK.getId())
			sendTakeItemsDialogue(7671, 7673, 7675, 7676, 7679);
	}

	private void handleLarders(GameObject object) {
		if (object.getId() == HObject.WOODEN_LARDER.getId())
			sendTakeItemsDialogue(7738, 1927);
		else if (object.getId() == HObject.OAK_LARDER.getId())
			sendTakeItemsDialogue(7738, 1927, 1944, 1933);
		else if (object.getId() == HObject.TEAK_LARDER.getId())
			sendTakeItemsDialogue(7738, 1927, 1944, 1933, 6701, 1550, 1957, 1985);
	}

	private void handleShelves(GameObject object) {
		if (object.getId() == HObject.WOODEN_SHELVES_1.getId())
			sendTakeItemsDialogue(7688, 7702, 7728);
		else if (object.getId() == HObject.WOODEN_SHELVES_2.getId())
			sendTakeItemsDialogue(7688, 7702, 7728, 7742);
		else if (object.getId() == HObject.WOODEN_SHELVES_3.getId())
			sendTakeItemsDialogue(7688, 7714, 7732, 7742, 1887);
		else if (object.getId() == HObject.OAK_SHELVES_1.getId())
			sendTakeItemsDialogue(7688, 7714, 7732, 7742, 1923);
		else if (object.getId() == HObject.OAK_SHELVES_2.getId())
			sendTakeItemsDialogue(7688, 7714, 7732, 7742, 1923, 1887);
		else if (object.getId() == HObject.TEAK_SHELVES_1.getId())
			sendTakeItemsDialogue(7688, 7726, 7735, 7742, 1923, 2313, 1931);
		else if (object.getId() == HObject.TEAK_SHELVES_2.getId())
			sendTakeItemsDialogue(7688, 7726, 7735, 7742, 1923, 2313, 1931, 1949);
	}

	private void handleTools(GameObject object) {
		if (object.getId() == HObject.TOOL_STORE_1.getId())
			sendTakeItemsDialogue(8794, 2347, 1755, 1735);
		else if (object.getId() == HObject.TOOL_STORE_2.getId())
			sendTakeItemsDialogue(1925, 946, 952, 590);
		else if (object.getId() == HObject.TOOL_STORE_3.getId())
			sendTakeItemsDialogue(1757, 1785, 1733);
		else if (object.getId() == HObject.TOOL_STORE_4.getId())
			sendTakeItemsDialogue(1592, 1595, 1597, 1599, 11065, 5523);
		else if (object.getId() == HObject.TOOL_STORE_5.getId())
			sendTakeItemsDialogue(5341, 5343, 5329, 5331, 5325, 952);
	}

	private void handleDoor(GameObject object) {
		WorldTile target = null;
		Direction direction = Direction.NORTH;
		switch (object.getRotation()) {
		case 0:
			if (player.getX() < object.getX()) {
				target = object.transform(1, 0, 0);
				direction = Direction.EAST;
			} else {
				target = object.transform(-1, 0, 0);
				direction = Direction.WEST;
			}
			break;
		case 1:
			if (player.getY() <= object.getY()) {
				target = object.transform(0, 1, 0);
				direction = Direction.NORTH;
			} else {
				target = object.transform(0, -1, 0);
				direction = Direction.SOUTH;
			}
			break;
		case 2:
			if (player.getX() > object.getX()) {
				target = object.transform(-1, 0, 0);
				direction = Direction.WEST;
			} else {
				target = object.transform(1, 0, 0);
				direction = Direction.EAST;
			}
			break;
		case 3:
			if (player.getY() >= object.getY()) {
				target = object.transform(0, -1, 0);
				direction = Direction.SOUTH;
			} else {
				target = object.transform(0, 1, 0);
				direction = Direction.NORTH;
			}
			break;
		}
		if (target == null)
			return;
		player.lock(1);
		player.setNextAnimation(new Animation(741));
		final WorldTile toTile = target;
		player.setNextForceMovement(new ForceMovement(player, 0, toTile, 1, direction));
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				player.setNextWorldTile(toTile);
			}

		}, 0);
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, ClientPacket packet) {
		if (interfaceId == 400)
			if (packet == ClientPacket.IF_OP1)
				TabletMaking.handleTabletCreation(player, componentId, 1);
			else if (packet == ClientPacket.IF_OP2)
				TabletMaking.handleTabletCreation(player, componentId, 5);
			else if (packet == ClientPacket.IF_OP3)
				TabletMaking.handleTabletCreation(player, componentId, 10);
			else if (packet == ClientPacket.IF_OP4)
				player.sendInputInteger("How many would you like to make?", (amount) -> TabletMaking.handleTabletCreation(player, componentId, amount));
			else if (packet == ClientPacket.IF_OP5)
				TabletMaking.handleTabletCreation(player, componentId, player.getInventory().getAmountOf(1761));
		return true;
	}

	private void handleCombatRing(GameObject object) {
		WorldTile target = null;
		Direction direction = Direction.NORTH;
		switch (object.getRotation()) {
		case 0:
			if (player.getX() < object.getX()) {
				target = object.transform(1, 0, 0);
				direction = Direction.EAST;
				player.getTempAttribs().setB("inBoxingArena", false);
			} else {
				target = object.transform(-1, 0, 0);
				direction = Direction.WEST;
				player.getTempAttribs().setB("inBoxingArena", true);
			}
			break;
		case 1:
			if (player.getY() <= object.getY()) {
				target = object.transform(0, 1, 0);
				direction = Direction.NORTH;
				player.getTempAttribs().setB("inBoxingArena", true);
			} else {
				target = object.transform(0, -1, 0);
				direction = Direction.SOUTH;
				player.getTempAttribs().setB("inBoxingArena", false);
			}
			break;
		case 2:
			if (player.getX() > object.getX()) {
				target = object.transform(-1, 0, 0);
				direction = Direction.WEST;
				player.getTempAttribs().setB("inBoxingArena", false);
			} else {
				target = object.transform(1, 0, 0);
				direction = Direction.EAST;
				player.getTempAttribs().setB("inBoxingArena", true);
			}
			break;
		case 3:
			if (player.getY() >= object.getY()) {
				target = object.transform(0, -1, 0);
				direction = Direction.SOUTH;
				player.getTempAttribs().setB("inBoxingArena", true);
			} else {
				target = object.transform(0, 1, 0);
				direction = Direction.NORTH;
				player.getTempAttribs().setB("inBoxingArena", false);
			}
			break;
		}
		if (target == null)
			return;
		player.lock(1);
		player.setNextAnimation(new Animation(3688));
		final WorldTile toTile = target;
		player.setNextForceMovement(new ForceMovement(player, 0, toTile, 1, direction));
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				player.setNextWorldTile(toTile);
			}

		}, 0);
	}

	@Override
	public boolean processItemOnObject(GameObject object, Item item) {
		if (object.getId() == HouseConstants.HObject.CLAY_FIREPLACE.getId() || object.getId() == HouseConstants.HObject.STONE_FIREPLACE.getId() || object.getId() == HouseConstants.HObject.MARBLE_FIREPLACE.getId()) {
			if (item.getId() != 1511) {
				player.sendMessage("Only ordinary logs can be used to light the fireplace.");
				return false;
			}
			if (!player.getInventory().containsOneItem(590)) {
				player.sendMessage("You do not have the required items to light this.");
				return false;
			}
			player.lock(2);
			player.setNextAnimation(new Animation(3658));
			player.getSkills().addXp(Constants.FIREMAKING, 40);
			final GameObject objectR = new GameObject(object);
			objectR.setId(object.getId() + 1);
			for (final Player player : house.getPlayers())
				player.getPackets().sendRemoveObject(objectR);
			return false;
		}
		if (Builds.SINK.containsObject(object)) {
			Filler fill = FillAction.isFillable(item);
			if (fill != null)
				player.getDialogueManager().execute(new FillingD(), fill);
		} else if (HouseConstants.Builds.STOVE.containsObject(object)) {
			if (item.getId() == 7690) {
				player.getInventory().deleteItem(7690, 1);
				player.getInventory().addItem(7691, 1);
				player.setNextAnimation(new Animation(883));
				player.sendMessage("You boil the kettle of water.");
				return false;
			}
			final Cookables cook = Cooking.isCookingSkill(item);
			if (cook != null) {
				player.getDialogueManager().execute(new CookingD(), cook, object);
				return false;
			}
			player.getDialogueManager().execute(new SimpleMessage(), "You can't cook that on a " + (object.getDefinitions().getName().equals("Fire") ? "fire" : "range") + ".");
			return false;
		}
		return true;
	}

	@Override
	public boolean canDropItem(Item item) {
		if (house.isBuildMode()) {
			player.sendMessage("You cannot drop items while in building mode.");
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick2(GameObject object) {
		if (object.getId() == HouseConstants.HObject.EXIT_PORTAL.getId())
			house.switchLock(player);
		else if (HouseConstants.Builds.STAIRCASE.containsObject(object) || HouseConstants.Builds.STAIRCASE_DOWN.containsObject(object))
			house.climbStaircase(player, object, true);
		else if (HouseConstants.Builds.LEVER.containsObject(object))
			player.sendOptionDialogue("Would you like to toggle PVP challenge mode?", new String[] { "Yes", "No" }, new DialogueOptionEvent() {

				@Override
				public void run(Player player) {
					if (getOption() == 1)
						house.toggleChallengeMode(player);
				}

			});
		return false;
	}

	@Override
	public boolean processObjectClick3(GameObject object) {
		if (HouseConstants.Builds.STAIRCASE.containsObject(object) || HouseConstants.Builds.STAIRCASE_DOWN.containsObject(object))
			house.climbStaircase(player, object, false);
		return false;
	}

	@Override
	public boolean processObjectClick4(GameObject object) {
		if (HouseConstants.Builds.STAIRCASE.containsObject(object) || HouseConstants.Builds.STAIRCASE_DOWN.containsObject(object))
			house.removeRoom();
		return false;
	}

	@Override
	public boolean checkWalkStep(int lastX, int lastY, int nextX, int nextY) {
		return !house.isSky(nextX, nextY, player.getPlane());
	}

	@Override
	public boolean logout() {
		player.setLocation(house.getLocation().getTile());
		player.setNextWorldTile(house.getLocation().getTile());
		house.leaveHouse(player, House.LOGGED_OUT);
		return false;
	}

	@Override
	public boolean login() {
		removeController();
		player.setNextWorldTile(POHLocation.RIMMINGTON.getTile());
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		house.leaveHouse(player, House.TELEPORTED);
	}

	// shouldnt happen
	@Override
	public void forceClose() {
		player.setLocation(house.getLocation().getTile());
		player.setNextWorldTile(house.getLocation().getTile());
		player.removeHouseOnlyItems();
		house.leaveHouse(player, House.TELEPORTED);
	}

	@Override
	public void process() {
		if ((house.isChallengeMode() && player.getPlane() == 0) || player.getTempAttribs().getB("inBoxingArena")) {
			if (!player.isCanPvp())
				player.setCanPvp(true);
		} else if (player.isCanPvp())
			player.setCanPvp(false);
	}

	public House getHouse() {
		return house;
	}

	public void sendTakeItemsDialogue(final int... itemIds) {
		if (itemIds.length <= 5) {
			String[] options = new String[itemIds.length];
			for (int i = 0; i < options.length; i++)
				options[i] = ItemDefinitions.getDefs(itemIds[i]).getName();
			player.sendOptionDialogue("What would you like to take?", options, new DialogueOptionEvent() {

				@Override
				public void run(Player player) {
					if (getOption() == 1)
						player.getInventory().addItem(itemIds[0], 1);
					else if (getOption() == 2) {
						if (itemIds.length > 1)
							player.getInventory().addItem(itemIds[1], 1);
					} else if (getOption() == 3) {
						if (itemIds.length > 2)
							player.getInventory().addItem(itemIds[2], 1);
					} else if (getOption() == 4) {
						if (itemIds.length > 3)
							player.getInventory().addItem(itemIds[3], 1);
					} else if (getOption() == 5)
						if (itemIds.length > 4)
							player.getInventory().addItem(itemIds[4], 1);
				}

			});
		} else if (itemIds.length > 5) {
			String[] options = new String[5];
			for (int i = 0; i < 4; i++)
				options[i] = ItemDefinitions.getDefs(itemIds[i]).getName();
			options[4] = "More";
			player.sendOptionDialogue("What would you like to take?", options, new DialogueOptionEvent() {

				@Override
				public void run(Player player) {
					if (getOption() == 1)
						player.getInventory().addItem(itemIds[0], 1);
					else if (getOption() == 2) {
						if (itemIds.length > 1)
							player.getInventory().addItem(itemIds[1], 1);
					} else if (getOption() == 3) {
						if (itemIds.length > 2)
							player.getInventory().addItem(itemIds[2], 1);
					} else if (getOption() == 4) {
						if (itemIds.length > 3)
							player.getInventory().addItem(itemIds[3], 1);
					} else if (getOption() == 5) {
						String[] options = new String[itemIds.length - 4];
						for (int i = 4; i < 9; i++) {
							if (i > (itemIds.length - 1))
								break;
							options[i - 4] = ItemDefinitions.getDefs(itemIds[i]).getName();
						}
						player.sendOptionDialogue("What would you like to take?", options, new DialogueOptionEvent() {

							@Override
							public void run(Player player) {
								if (getOption() == 1) {
									if (itemIds.length > 4)
										player.getInventory().addItem(itemIds[4], 1);
								} else if (getOption() == 2) {
									if (itemIds.length > 5)
										player.getInventory().addItem(itemIds[5], 1);
								} else if (getOption() == 3) {
									if (itemIds.length > 6)
										player.getInventory().addItem(itemIds[6], 1);
								} else if (getOption() == 4) {
									if (itemIds.length > 7)
										player.getInventory().addItem(itemIds[7], 1);
								} else if (getOption() == 5)
									if (itemIds.length > 8)
										player.getInventory().addItem(itemIds[8], 1);
							}

						});
					}
				}

			});
		} else
			System.err.println("Bro who the fuck needs more than 9 items in a dialogue?");
	}

	public static void directPortals(Player player, GameObject object ) {

		RoomReference currentRoom = player.getHouse().getRoom(object);

		if (currentRoom != null) {
			ObjectReference portal1 = currentRoom.getBuild(Builds.PORTALS1);
			ObjectReference portal2 = currentRoom.getBuild(Builds.PORTALS2);
			ObjectReference portal3 = currentRoom.getBuild(Builds.PORTALS3);

			if (portal1 == null && portal2 == null && portal3 == null) {
				player.startConversation(new Dialogue().addSimple("You should build portal frames first."));
				return;
			}

			Dialogue p1d = getTeleports(player, currentRoom, portal1);
			Dialogue p2d = getTeleports(player, currentRoom, portal2);
			Dialogue p3d = getTeleports(player, currentRoom, portal3);

			Dialogue d = new Dialogue();
			d.addOptions("Redirect which portal?", new Options() {

				@Override
				public void create() {

					if (portal1 == null)
						option("1: No portal frame", () -> { player.sendMessage("You must build a portal frame before you can redirect this."); });
					else
						option("1: " + (ObjectDefinitions.getDefs(portal1.getId()).getName().contains("Portal frame") ? "Nowhere" : ObjectDefinitions.getDefs(portal1.getId()).getName()), p1d);

					if (portal2 == null)
						option("2: No portal frame", () -> { player.sendMessage("You must build a portal frame before you can redirect this."); });
					else
						option("2: " + (ObjectDefinitions.getDefs(portal2.getId()).getName().contains("Portal frame") ? "Nowhere" : ObjectDefinitions.getDefs(portal2.getId()).getName()), p2d);

					if (portal3 == null)
						option("3: No portal frame", () -> { player.sendMessage("You must build a portal frame before you can redirect this."); });
					else
						option("3: " + (ObjectDefinitions.getDefs(portal3.getId()).getName().contains("Portal frame") ? "Nowhere" : ObjectDefinitions.getDefs(portal3.getId()).getName()), p3d);

				}

			});
			player.startConversation(d);
		}
	}

	public static Dialogue getTeleports(Player player, RoomReference room, ObjectReference portal) {
		if (portal == null)
			return null;

		Dialogue teleportOptions = new Dialogue();
		int portalId = portal.getId();
		String frameType = getPortalFrameType(portalId);

		teleportOptions.addOptions("Direct teleport to", new Options() {

			@Override
			public void create() {
				switch (frameType) {
				case "Teak":
					if (portalId != 13615 && !player.getHouse().containsAnyObject(13615, 13622, 13629))
						option("Varrock Teleport", new Dialogue().addOption("This will cost 100 teleports to tune", "Pay runes", "Nevermind").addNext(() -> {
							RuneSet requiredRunes = new RuneSet(Rune.AIR, 300, Rune.LAW, 100, Rune.FIRE, 100);
							if (requiredRunes.meetsPortalRequirements(player)) {
								requiredRunes.deleteRunes(player);
								portal.setSlot(3, player.getHouse().getWorldObject(room, portal.getId()));
							}
						}));
					if (portalId != 13616 && !player.getHouse().containsAnyObject(13616, 13623, 13630))
						option("Lumbridge Teleport", new Dialogue().addOption("This will cost 100 teleports to tune", "Pay runes", "Nevermind").addNext(() -> {
							RuneSet requiredRunes = new RuneSet(Rune.AIR, 300, Rune.LAW, 100, Rune.EARTH, 100);
							if (requiredRunes.meetsPortalRequirements(player)) {
								requiredRunes.deleteRunes(player);
								portal.setSlot(4, player.getHouse().getWorldObject(room, portal.getId()));
							}
						}));
					if (portalId != 13617 && !player.getHouse().containsAnyObject(13617, 13624, 13631))
						option("Falador Teleport", new Dialogue().addOption("This will cost 100 teleports to tune", "Pay runes", "Nevermind").addNext(() -> {
							RuneSet requiredRunes = new RuneSet(Rune.AIR, 300, Rune.LAW, 100, Rune.WATER, 100);
							if (requiredRunes.meetsPortalRequirements(player)) {
								requiredRunes.deleteRunes(player);
								portal.setSlot(5, player.getHouse().getWorldObject(room, portal.getId()));
							}
						}));
					if (portalId != 13618 && !player.getHouse().containsAnyObject(13618, 13625, 13632))
						option("Camelot Teleport", new Dialogue().addOption("This will cost 100 teleports to tune", "Pay runes", "Nevermind").addNext(() -> {
							RuneSet requiredRunes = new RuneSet(Rune.AIR, 500, Rune.LAW, 100);
							if (requiredRunes.meetsPortalRequirements(player)) {
								requiredRunes.deleteRunes(player);
								portal.setSlot(6, player.getHouse().getWorldObject(room, portal.getId()));
							}
						}));
					if (portalId != 13619 && !player.getHouse().containsAnyObject(13619, 13626, 13633))
						option("Ardougne Teleport", new Dialogue().addOption("This will cost 100 teleports to tune", "Pay runes", "Nevermind").addNext(() -> {
							RuneSet requiredRunes = new RuneSet(Rune.LAW, 200, Rune.WATER, 200);
							if (!player.getQuestManager().isComplete(Quest.PLAGUE_CITY) || !Quest.PLAGUE_CITY.meetsRequirements(player)) {
								player.sendMessage("You must complete the quest Plague City to tune your portal to Ardougne.");
								return;
							}
							if (requiredRunes.meetsPortalRequirements(player)) {
								requiredRunes.deleteRunes(player);
								portal.setSlot(7, player.getHouse().getWorldObject(room, portal.getId()));
							}
						}));
					if (portalId != 13620 && !player.getHouse().containsAnyObject(13620, 13627, 13634))
						option("Yanille Teleport", new Dialogue().addOption("This will cost 100 teleports to tune", "Pay runes", "Nevermind").addNext(() -> {
							RuneSet requiredRunes = new RuneSet(Rune.LAW, 200, Rune.EARTH, 200);
							if (!player.getQuestManager().isComplete(Quest.WATCHTOWER) || !Quest.WATCHTOWER.meetsRequirements(player)) {
								player.sendMessage("You must complete the quest Watchtower to tune your portal to Yanille.");
								return;
							}
							if (requiredRunes.meetsPortalRequirements(player)) {
								requiredRunes.deleteRunes(player);
								portal.setSlot(8, player.getHouse().getWorldObject(room, portal.getId()));
							}
						}));
					if (portalId != 13621 && !player.getHouse().containsAnyObject(13621, 13628, 13635))
						option("Kharyrll Teleport", new Dialogue().addOption("This will cost 100 teleports to tune", "Pay runes", "Nevermind").addNext(() -> {
							RuneSet requiredRunes = new RuneSet(Rune.BLOOD, 100, Rune.LAW, 200);
							if (!player.getQuestManager().isComplete(Quest.DESERT_TREASURE) || !Quest.DESERT_TREASURE.meetsRequirements(player)) {
								player.sendMessage("You must complete the quest Desert treasure to tune your portal to Kharyrll (Canifis).");
								return;
							}
							if (requiredRunes.meetsPortalRequirements(player)) {
								requiredRunes.deleteRunes(player);
								portal.setSlot(9, player.getHouse().getWorldObject(room, portal.getId()));
							}
						}));
					option("Nevermind");
					break;
				case "Mahogany":
					if (portalId != 13622 && !player.getHouse().containsAnyObject(13615, 13622, 13629))
						option("Varrock Teleport", new Dialogue().addOption("This will cost 100 teleports to tune", "Pay runes", "Nevermind").addNext(() -> {
							RuneSet requiredRunes = new RuneSet(Rune.AIR, 300, Rune.LAW, 100, Rune.FIRE, 100);
							if (requiredRunes.meetsPortalRequirements(player)) {
								requiredRunes.deleteRunes(player);
								portal.setSlot(10, player.getHouse().getWorldObject(room, portal.getId()));
							}
						}));
					if (portalId != 13623 && !player.getHouse().containsAnyObject(13616, 13623, 13630))
						option("Lumbridge Teleport", new Dialogue().addOption("This will cost 100 teleports to tune", "Pay runes", "Nevermind").addNext(() -> {
							RuneSet requiredRunes = new RuneSet(Rune.AIR, 300, Rune.LAW, 100, Rune.EARTH, 100);
							if (requiredRunes.meetsPortalRequirements(player)) {
								requiredRunes.deleteRunes(player);
								portal.setSlot(11, player.getHouse().getWorldObject(room, portal.getId()));
							}
						}));
					if (portalId != 13624 && !player.getHouse().containsAnyObject(13617, 13624, 13631))
						option("Falador Teleport", new Dialogue().addOption("This will cost 100 teleports to tune", "Pay runes", "Nevermind").addNext(() -> {
							RuneSet requiredRunes = new RuneSet(Rune.AIR, 300, Rune.LAW, 100, Rune.WATER, 100);
							if (requiredRunes.meetsPortalRequirements(player)) {
								requiredRunes.deleteRunes(player);
								portal.setSlot(12, player.getHouse().getWorldObject(room, portal.getId()));
							}
						}));
					if (portalId != 13625 && !player.getHouse().containsAnyObject(13618, 13625, 13632))
						option("Camelot Teleport", new Dialogue().addOption("This will cost 100 teleports to tune", "Pay runes", "Nevermind").addNext(() -> {
							RuneSet requiredRunes = new RuneSet(Rune.AIR, 500, Rune.LAW, 100);
							if (requiredRunes.meetsPortalRequirements(player)) {
								requiredRunes.deleteRunes(player);
								portal.setSlot(13, player.getHouse().getWorldObject(room, portal.getId()));
							}
						}));
					if (portalId != 13626 && !player.getHouse().containsAnyObject(13619, 13626, 13633))
						option("Ardougne Teleport", new Dialogue().addOption("This will cost 100 teleports to tune", "Pay runes", "Nevermind").addNext(() -> {
							RuneSet requiredRunes = new RuneSet(Rune.LAW, 200, Rune.WATER, 200);
							if (!player.getQuestManager().isComplete(Quest.PLAGUE_CITY) || !Quest.PLAGUE_CITY.meetsRequirements(player)) {
								player.sendMessage("You must complete the quest Plague City to tune your portal to Ardougne.");
								return;
							}
							if (requiredRunes.meetsPortalRequirements(player)) {
								requiredRunes.deleteRunes(player);
								portal.setSlot(14, player.getHouse().getWorldObject(room, portal.getId()));
							}
						}));
					if (portalId != 13627 && !player.getHouse().containsAnyObject(13620, 13627, 13634))
						option("Yanille Teleport", new Dialogue().addOption("This will cost 100 teleports to tune", "Pay runes", "Nevermind").addNext(() -> {
							RuneSet requiredRunes = new RuneSet(Rune.LAW, 200, Rune.EARTH, 200);
							if (!player.getQuestManager().isComplete(Quest.WATCHTOWER) || !Quest.WATCHTOWER.meetsRequirements(player)) {
								player.sendMessage("You must complete the quest Watchtower to tune your portal to Yanille.");
								return;
							}
							if (requiredRunes.meetsPortalRequirements(player)) {
								requiredRunes.deleteRunes(player);
								portal.setSlot(15, player.getHouse().getWorldObject(room, portal.getId()));
							}
						}));
					if (portalId != 13628 && !player.getHouse().containsAnyObject(13621, 13628, 13635))
						option("Kharyrll Teleport", new Dialogue().addOption("This will cost 100 teleports to tune", "Pay runes", "Nevermind").addNext(() -> {
							RuneSet requiredRunes = new RuneSet(Rune.BLOOD, 100, Rune.LAW, 200);
							if (!player.getQuestManager().isComplete(Quest.DESERT_TREASURE) || !Quest.DESERT_TREASURE.meetsRequirements(player)) {
								player.sendMessage("You must complete the quest Desert treasure to tune your portal to Kharyrll (Canifis).");
								return;
							}
							if (requiredRunes.meetsPortalRequirements(player)) {
								requiredRunes.deleteRunes(player);
								portal.setSlot(16, player.getHouse().getWorldObject(room, portal.getId()));
							}
						}));
					option("Nevermind");
					break;
				case "Marble":
					if (portalId != 13629 && !player.getHouse().containsAnyObject(13615, 13622, 13629))
						option("Varrock Teleport", new Dialogue().addOption("This will cost 100 teleports to tune", "Pay runes", "Nevermind").addNext(() -> {
							RuneSet requiredRunes = new RuneSet(Rune.AIR, 300, Rune.LAW, 100, Rune.FIRE, 100);
							if (requiredRunes.meetsPortalRequirements(player)) {
								requiredRunes.deleteRunes(player);
								portal.setSlot(17, player.getHouse().getWorldObject(room, portal.getId()));
							}
						}));
					if (portalId != 13630 && !player.getHouse().containsAnyObject(13616, 13623, 13630))
						option("Lumbridge Teleport", new Dialogue().addOption("This will cost 100 teleports to tune", "Pay runes", "Nevermind").addNext(() -> {
							RuneSet requiredRunes = new RuneSet(Rune.AIR, 300, Rune.LAW, 100, Rune.EARTH, 100);
							if (requiredRunes.meetsPortalRequirements(player)) {
								requiredRunes.deleteRunes(player);
								portal.setSlot(18, player.getHouse().getWorldObject(room, portal.getId()));
							}
						}));
					if (portalId != 13631 && !player.getHouse().containsAnyObject(13617, 13624, 13631))
						option("Falador Teleport", new Dialogue().addOption("This will cost 100 teleports to tune", "Pay runes", "Nevermind").addNext(() -> {
							RuneSet requiredRunes = new RuneSet(Rune.AIR, 300, Rune.LAW, 100, Rune.WATER, 100);
							if (requiredRunes.meetsPortalRequirements(player)) {
								requiredRunes.deleteRunes(player);
								portal.setSlot(19, player.getHouse().getWorldObject(room, portal.getId()));
							}
						}));
					if (portalId != 13632 && !player.getHouse().containsAnyObject(13618, 13625, 13632))
						option("Camelot Teleport", new Dialogue().addOption("This will cost 100 teleports to tune", "Pay runes", "Nevermind").addNext(() -> {
							RuneSet requiredRunes = new RuneSet(Rune.AIR, 500, Rune.LAW, 100);
							if (requiredRunes.meetsPortalRequirements(player)) {
								requiredRunes.deleteRunes(player);
								portal.setSlot(20, player.getHouse().getWorldObject(room, portal.getId()));
							}
						}));
					if (portalId != 13633 && !player.getHouse().containsAnyObject(13619, 13626, 13633))
						option("Ardougne Teleport", new Dialogue().addOption("This will cost 100 teleports to tune", "Pay runes", "Nevermind").addNext(() -> {
							RuneSet requiredRunes = new RuneSet(Rune.LAW, 200, Rune.WATER, 200);
							if (!player.getQuestManager().isComplete(Quest.PLAGUE_CITY) || !Quest.PLAGUE_CITY.meetsRequirements(player)) {
								player.sendMessage("You must complete the quest Plague City to tune your portal to Ardougne.");
								return;
							}
							if (requiredRunes.meetsPortalRequirements(player)) {
								requiredRunes.deleteRunes(player);
								portal.setSlot(21, player.getHouse().getWorldObject(room, portal.getId()));
							}
						}));
					if (portalId != 13634 && !player.getHouse().containsAnyObject(13620, 13627, 13634))
						option("Yanille Teleport", new Dialogue().addOption("This will cost 100 teleports to tune", "Pay runes", "Nevermind").addNext(() -> {
							RuneSet requiredRunes = new RuneSet(Rune.LAW, 200, Rune.EARTH, 200);
							if (!player.getQuestManager().isComplete(Quest.WATCHTOWER) || !Quest.WATCHTOWER.meetsRequirements(player)) {
								player.sendMessage("You must complete the quest Watchtower to tune your portal to Yanille.");
								return;
							}
							if (requiredRunes.meetsPortalRequirements(player)) {
								requiredRunes.deleteRunes(player);
								portal.setSlot(22, player.getHouse().getWorldObject(room, portal.getId()));
							}
						}));
					if (portalId != 13635 && !player.getHouse().containsAnyObject(13621, 13628, 13635))
						option("Kharyrll Teleport", new Dialogue().addOption("This will cost 100 teleports to tune", "Pay runes", "Nevermind").addNext(() -> {
							RuneSet requiredRunes = new RuneSet(Rune.BLOOD, 100, Rune.LAW, 200);
							if (!player.getQuestManager().isComplete(Quest.DESERT_TREASURE) || !Quest.DESERT_TREASURE.meetsRequirements(player)) {
								player.sendMessage("You must complete the quest Desert treasure to tune your portal to Kharyrll (Canifis).");
								return;
							}
							if (requiredRunes.meetsPortalRequirements(player)) {
								requiredRunes.deleteRunes(player);
								portal.setSlot(23, player.getHouse().getWorldObject(room, portal.getId()));
							}
						}));
					option("Nevermind");
					break;
				default:
					System.out.println("woahhh this might be broken...");
				}
			}
		});
		return teleportOptions;
	}

	public static String getPortalFrameType(int objectId) {
		if ((objectId >= 13615 && objectId <= 13621) || objectId == 13636)
			return "Teak";
		if ((objectId >= 13622 && objectId <= 13628) || objectId == 13637)
			return "Mahogany";
		if ((objectId >= 13629 && objectId <= 13635) || objectId == 13638)
			return "Marble";
		return "";
	}

	public void telePlayer(int objectId) {
		switch (objectId) {
		case 13615:
		case 13622:
		case 13629:
			Magic.sendNormalTeleportSpell(player, 1, 0, new WorldTile(3212, 3424, 0), null); //Varrock
			break;
		case 13616:
		case 13623:
		case 13630:
			Magic.sendNormalTeleportSpell(player, 1, 0, new WorldTile(3222, 3218, 0), null); //Lumby
			break;
		case 13617:
		case 13624:
		case 13631:
			Magic.sendNormalTeleportSpell(player, 1, 0, new WorldTile(2964, 3379, 0), null); //Falador
			break;
		case 13618:
		case 13625:
		case 13632:
			Magic.sendNormalTeleportSpell(player, 1, 0, new WorldTile(2757, 3478, 0), null); //Camelot
			break;
		case 13619:
		case 13626:
		case 13633:
			Magic.sendNormalTeleportSpell(player, 1, 0, new WorldTile(2664, 3305, 0), null); //Ardougne
			break;
		case 13620:
		case 13627:
		case 13634:
			Magic.sendNormalTeleportSpell(player, 1, 0, new WorldTile(2546, 3095, 0), null); //Yanille
			break;
		case 13621:
		case 13628:
		case 13635:
			Magic.sendNormalTeleportSpell(player, 1, 0, new WorldTile(3492, 3471, 0), null); //Kharyrll
			break;
		default:
			player.sendMessage("Uh-oh... This shouldn't have happened (Object: " + objectId + "). Please report to staff.");
		}
	}
}