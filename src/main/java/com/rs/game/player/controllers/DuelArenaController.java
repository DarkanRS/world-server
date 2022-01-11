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
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.World;
import com.rs.game.object.GameObject;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.game.player.content.ItemConstants;
import com.rs.game.player.content.Potions.Potion;
import com.rs.game.player.content.minigames.duel.DuelRules;
import com.rs.game.player.content.skills.cooking.Foods.Food;
import com.rs.game.player.dialogues.ForfeitDialouge;
import com.rs.game.player.dialogues.SimpleMessage;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Utils;

public class DuelArenaController extends Controller {

	private transient Player target;
	private boolean ifFriendly, isDueling;

	private final Item[] FUN_WEAPONS = { new Item(4566) };

	private final WorldTile[] LOBBY_TELEPORTS = { new WorldTile(3367, 3275, 0), new WorldTile(3360, 3275, 0), new WorldTile(3358, 3270, 0), new WorldTile(3363, 3268, 0), new WorldTile(3370, 3268, 0), new WorldTile(3367, 3267, 0),
			new WorldTile(3376, 3275, 0), new WorldTile(3377, 3271, 0), new WorldTile(3375, 3269, 0), new WorldTile(3381, 3277, 0) };

	public DuelArenaController(Player target, boolean friendly) {
		this.target = target;
		ifFriendly = friendly;
	}

	@Override
	public void start() {
		openDuelScreen(target, ifFriendly);
	}

	private void openDuelScreen(Player target, boolean ifFriendly) {
		if (!ifFriendly) {
			sendOptions(player);
			player.getLastDuelRules().getStake().clear();
		}
		player.getTempAttribs().setB("acceptedDuel", false);
		player.getPackets().sendItems(134, false, player.getLastDuelRules().getStake());
		player.getPackets().sendItems(134, true, player.getLastDuelRules().getStake());
		player.getPackets().setIFText(ifFriendly ? 637 : 631, ifFriendly ? 16 : 38, " " + Utils.formatPlayerNameForDisplay(target.getUsername()));
		player.getPackets().setIFText(ifFriendly ? 637 : 631, ifFriendly ? 18 : 40, "" + (target.getSkills().getCombatLevel()));
		player.getVars().setVar(286, 0);
		player.getTempAttribs().setB("firstScreen", true);
		player.getInterfaceManager().sendInterface(ifFriendly ? 637 : 631);
		refreshScreenMessage(true, ifFriendly);
		player.setCloseInterfacesEvent(() -> closeDuelInteraction(true, DuelStage.DECLINED));
	}

	private void accept(boolean firstStage) {
		if (!hasTarget())
			return;
		boolean accepted = player.getTempAttribs().getB("acceptedDuel");
		boolean targetAccepted = target.getTempAttribs().getB("acceptedDuel");
		DuelRules rules = player.getLastDuelRules();
		if (!rules.canAccept(player.getLastDuelRules().getStake()))
			return;
		if (accepted && targetAccepted) {
			if (firstStage) {
				if (nextStage())
					((DuelArenaController) target.getControllerManager().getController()).nextStage();
			} else {
				player.setCloseInterfacesEvent(null);
				player.closeInterfaces();
				closeDuelInteraction(true, DuelStage.DONE);
			}
			return;
		}
		player.getTempAttribs().setB("acceptedDuel", true);
		refreshScreenMessages(firstStage, ifFriendly);
	}

	protected void closeDuelInteraction(boolean started, DuelStage duelStage) {
		Player oldTarget = target;
		if (duelStage != DuelStage.DONE) {
			target = null;
			WorldTasks.schedule(new WorldTask() {

				@Override
				public void run() {
					player.getControllerManager().startController(new DuelController());
				}
			});
			player.getInventory().getItems().addAll(player.getLastDuelRules().getStake());
			player.getInventory().init();
			player.getLastDuelRules().getStake().clear();
		} else {
			removeEquipment();
			player.reset();
			beginBattle(started);
		}
		Controller controller = oldTarget.getControllerManager().getController();
		if (controller == null)
			return;
		DuelArenaController targetConfiguration = (DuelArenaController) controller;
		if (controller instanceof DuelArenaController)
			if (targetConfiguration.hasTarget()) {
				oldTarget.setCloseInterfacesEvent(null);
				oldTarget.closeInterfaces();
				if (duelStage != DuelStage.DONE)
					player.getControllerManager().removeControllerWithoutCheck();
				if (started)
					targetConfiguration.closeDuelInteraction(false, duelStage);
				if (duelStage == DuelStage.DONE)
					player.sendMessage("Your battle will begin shortly.");
				else if (duelStage == DuelStage.SECOND)
					player.sendMessage("<col=ff0000>Please check if these settings are correct.");
				else if (duelStage == DuelStage.DECLINED)
					oldTarget.sendMessage("<col=ff0000>Other player declined the duel!");
				else if (duelStage == DuelStage.DECLINED) {
					oldTarget.sendMessage("You do not have enough space to continue!");
					oldTarget.sendMessage("Other player does not have enough space to continue!");
				}
			}
	}

	public void addItem(int slot, int amount) {
		if (!hasTarget() || player.isIronMan() || player.getRights() == Rights.ADMIN)
			return;
		Item item = player.getInventory().getItem(slot);
		if (item == null)
			return;
		if (!ItemConstants.isTradeable(item)) {
			player.sendMessage("That item cannot be staked!");
			return;
		}
		if (player.getLastDuelRules().getStake().getUsedSlots() > 8) {
			player.sendMessage("There is enough items.");
			return;
		}
		Item[] itemsBefore = player.getLastDuelRules().getStake().getItemsCopy();
		int maxAmount = player.getInventory().getItems().getNumberOf(item);
		if (amount < maxAmount)
			item = new Item(item.getId(), amount);
		else
			item = new Item(item.getId(), maxAmount);
		player.getLastDuelRules().getStake().add(item);
		player.getInventory().deleteItem(slot, item);
		refreshItems(itemsBefore);
		cancelAccepted();
	}

	public void removeItem(final int slot, int amount) {
		if (!hasTarget())
			return;
		Item item = player.getLastDuelRules().getStake().get(slot);
		if (item == null)
			return;
		int maxAmount = player.getLastDuelRules().getStake().getNumberOf(item);
		if (amount < maxAmount)
			item = new Item(item.getId(), amount);
		else
			item = new Item(item.getId(), maxAmount);
		player.getLastDuelRules().getStake().remove(slot, item);
		player.getInventory().addItem(item);
		Item[] itemsBefore = player.getLastDuelRules().getStake().getItemsCopy();
		refreshItems(itemsBefore);
		cancelAccepted();
	}

	private void refreshItems(Item[] itemsBefore) {
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			Item item = player.getLastDuelRules().getStake().getItems()[index];
			if (item != null)
				if (itemsBefore[index] != item)
					changedSlots[count++] = index;
		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
	}

	private void refresh(int... slots) {
		player.getPackets().sendUpdateItems(134, player.getLastDuelRules().getStake(), 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
		target.getPackets().sendUpdateItems(134, true, player.getLastDuelRules().getStake().getItems(), 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
	}

	public void cancelAccepted() {
		boolean canceled = false;
		if (player.getTempAttribs().getB("acceptedDuel")) {
			player.getTempAttribs().setB("acceptedDuel", false);
			canceled = true;
		}
		if (target.getTempAttribs().getB("acceptedDuel")) {
			target.getTempAttribs().setB("acceptedDuel", false);
			canceled = true;
		}
		if (canceled)
			refreshScreenMessages(canceled, ifFriendly);
	}

	private void openConfirmationScreen(boolean ifFriendly) {
		player.getInterfaceManager().sendInterface(ifFriendly ? 639 : 626);
		refreshScreenMessage(false, ifFriendly);
	}

	private void refreshScreenMessages(boolean firstStage, boolean ifFriendly) {
		refreshScreenMessage(firstStage, ifFriendly);
		((DuelArenaController) target.getControllerManager().getController()).refreshScreenMessage(firstStage, ifFriendly);
	}

	private void refreshScreenMessage(boolean firstStage, boolean ifFriendly) {
		player.getPackets().setIFText(firstStage ? (ifFriendly ? 637 : 631) : (ifFriendly ? 639 : 626), firstStage ? (ifFriendly ? 20 : 41) : (ifFriendly ? 23 : 35), "<col=ff0000>" + getAcceptMessage(firstStage));
	}

	private String getAcceptMessage(boolean firstStage) {
		if (target.getTempAttribs().getB("acceptedDuel"))
			return "Other player has accepted.";
		if (player.getTempAttribs().getB("acceptedDuel"))
			return "Waiting for other player...";
		return firstStage ? "" : "Please look over the agreements to the duel.";
	}

	public boolean nextStage() {
		if (!hasTarget())
			return false;
		if (player.getInventory().getItems().getUsedSlots() + target.getLastDuelRules().getStake().getUsedSlots() > 28) {
			player.setCloseInterfacesEvent(null);
			player.closeInterfaces();
			closeDuelInteraction(true, DuelStage.NO_SPACE);
			return false;
		}
		player.getTempAttribs().setB("acceptedDuel", false);
		openConfirmationScreen(false);
		player.getInterfaceManager().removeInventoryInterface();
		return true;
	}

	private void sendOptions(Player player) {
		player.getInterfaceManager().sendInventoryInterface(628);
		player.getPackets().setIFRightClickOps(628, 0, 0, 27, 0, 1, 2, 3, 4, 5);
		player.getPackets().sendInterSetItemsOptionsScript(628, 0, 93, 4, 7, "Stake-1", "Stake-5", "Stake-10", "Stake-X", "Stake-All", "Examine");
		player.getPackets().setIFRightClickOps(631, 47, 0, 27, 0, 1, 2, 3, 4, 5);
		player.getPackets().sendInterSetItemsOptionsScript(631, 0, 120, 4, 7, "Remove-1", "Remove-5", "Remove-10", "Stake-X", "Remove-All", "Examine");
	}

	public void endDuel(Player victor, Player loser) {
		endDuel(victor, loser, true);
	}

	public void endDuel(final Player victor, final Player loser, boolean removeLoserController) {
		if (isDueling) {
			for (Item item : player.getLastDuelRules().getStake().getItems()) {
				if (item == null)
					continue;
				victor.getInventory().addItem(item);
			}
			for (Item item : target.getLastDuelRules().getStake().getItems()) {
				if (item == null)
					continue;
				victor.getInventory().addItem(item);
			}
			isDueling = false;
		}
		if (loser.getControllerManager().getController() != null && removeLoserController)
			loser.getControllerManager().removeControllerWithoutCheck();
		loser.setCanPvp(false);
		loser.getHintIconsManager().removeUnsavedHintIcon();
		loser.reset();
		loser.closeInterfaces();
		if (victor.getControllerManager().getController() != null)
			victor.getControllerManager().removeControllerWithoutCheck();
		victor.setCanPvp(false);
		victor.getHintIconsManager().removeUnsavedHintIcon();
		victor.reset();
		victor.closeInterfaces();
		startEndingTeleport(victor);
		startEndingTeleport(loser);
		loser.sendMessage("Oh dear, it seems you have lost to " + victor.getDisplayName() + ".");
		victor.sendMessage("Congratulations! You easily defeated " + loser.getDisplayName() + ".");
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				victor.getControllerManager().startController(new DuelController());
				loser.getControllerManager().startController(new DuelController());
			}
		}, 2);
	}

	private void startEndingTeleport(Player player) {
		WorldTile tile = LOBBY_TELEPORTS[Utils.random(LOBBY_TELEPORTS.length)];
		WorldTile teleTile = tile;
		for (int trycount = 0; trycount < 10; trycount++) {
			teleTile = new WorldTile(tile, 2);
			if (World.floorAndWallsFree(teleTile, player.getSize()))
				break;
			teleTile = tile;
		}
		player.setNextWorldTile(teleTile);
	}

	private void removeEquipment() {
		int slot = 0;
		for (int i = 10; i < 23; i++) {
			if (i == 14)
				if (player.getEquipment().hasTwoHandedWeapon())
					Equipment.sendRemove(target, 3);
			if (player.getLastDuelRules().getRule(i)) {
				slot = i - 10;
				Equipment.sendRemove(player, slot);
			}
		}
	}

	private void beginBattle(boolean started) {
		if (started) {
			WorldTile[] teleports = getPossibleWorldTiles();
			int random = Utils.getRandomInclusive(1);
			player.setNextWorldTile(random == 0 ? teleports[0] : teleports[1]);
			target.setNextWorldTile(random == 0 ? teleports[1] : teleports[0]);
		}
		player.stopAll();
		player.lock(2); // fixes mass click steps
		player.reset();
		isDueling = true;
		player.getTempAttribs().setB("startedDuel", true);
		player.getTempAttribs().setB("canFight", false);
		player.setCanPvp(true);
		player.getHintIconsManager().addHintIcon(target, 1, -1, false);
		WorldTasks.schedule(new WorldTask() {
			int count = 3;

			@Override
			public void run() {
				if (count > 0)
					player.setNextForceTalk(new ForceTalk("" + count));
				if (count == 0) {
					player.getTempAttribs().setB("canFight", true);
					player.setNextForceTalk(new ForceTalk("FIGHT!"));
					stop();
				}
				count--;
			}
		}, 0, 2);
	}

	@Override
	public boolean canEat(Food food) {
		if (player.getLastDuelRules().getRule(4) && isDueling) {
			player.sendMessage("You cannot eat during this duel.", true);
			return false;
		}
		return true;
	}

	@Override
	public boolean canPot(Potion pot) {
		if (player.getLastDuelRules().getRule(3) && isDueling) {
			player.sendMessage("You cannot drink during this duel.", true);
			return false;
		}
		return true;
	}

	@Override
	public boolean canMove(Direction dir) {
		if (player.getLastDuelRules().getRule(25) && isDueling) {
			player.sendMessage("You cannot move during this duel!", true);
			return false;
		}
		return true;
	}

	@Override
	public boolean canSummonFamiliar() {
		if (player.getLastDuelRules().getRule(24) && isDueling)
			return true;
		player.sendMessage("Summoning has been disabled during this duel!");
		return false;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getDialogueManager().execute(new SimpleMessage(), "A magical force prevents you from teleporting from the arena.");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getDialogueManager().execute(new SimpleMessage(), "A magical force prevents you from teleporting from the arena.");
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		if (type != -1)
			return;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		player.getDialogueManager().execute(new ForfeitDialouge());
		return true;
	}

	@Override
	public boolean sendDeath() {
		endDuel(target, player);
		player.lock(7);
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
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public boolean login() {
		startEndingTeleport(player);
		removeController();
		return true;
	}

	@Override
	public boolean logout() {
		if (isDueling)
			endDuel(target, player, false);
		else
			closeDuelInteraction(true, DuelStage.DECLINED);
		return isDueling ? true : false;
	}

	@Override
	public boolean keepCombating(Entity victim) {
		DuelRules rules = player.getLastDuelRules();
		boolean isRanging = PlayerCombat.isRanging(player);
		if (!player.getTempAttribs().getB("canFight")) {
			player.sendMessage("The duel hasn't started yet.", true);
			return false;
		}
		if (target != victim)
			return false;
		if (player.getCombatDefinitions().getSpell() != null && rules.getRule(2) && isDueling) {
			player.sendMessage("You cannot use Magic in this duel!", true);
			return false;
		}
		if (isRanging && rules.getRule(0) && isDueling) {
			player.sendMessage("You cannot use Range in this duel!", true);
			return false;
		} else if (!isRanging && rules.getRule(1) && player.getCombatDefinitions().getSpell() == null && isDueling) {
			player.sendMessage("You cannot use Melee in this duel!", true);
			return false;
		} else
			for (Item item : FUN_WEAPONS)
				if (rules.getRule(8) && !player.getInventory().containsItem(item.getId(), item.getAmount())) {
					player.sendMessage("You can only use fun weapons in this duel!");
					return false;
				}
		return true;
	}

	@Override
	public boolean canEquip(int slotId, int itemId) {
		DuelRules rules = player.getLastDuelRules();
		if (isDueling) {
			if (rules.getRule(10 + slotId) || (slotId == 3 && player.getEquipment().hasTwoHandedWeapon() && rules.getRule(15))) {
				player.sendMessage("You can't equip " + ItemDefinitions.getDefs(itemId).getName().toLowerCase() + " during this duel.");
				return false;
			}
		}
		return true;
	}

	private WorldTile[] getPossibleWorldTiles() {
		final int arenaChoice = Utils.getRandomInclusive(2);
		WorldTile[] locations = new WorldTile[2];
		int[] arenaBoundariesX = { 3337, 3367, 3336 };
		int[] arenaBoundariesY = { 3246, 3227, 3208 };
		int[] maxOffsetX = { 14, 14, 16 };
		int[] maxOffsetY = { 10, 10, 10 };

		//Obstacles enabled
		if (player.getLastDuelRules().getRule(6)) {
			arenaBoundariesX = new int[]{ 3336, 3367, 3367 };
			arenaBoundariesY = new int[]{ 3227, 3246, 3208 };
			maxOffsetX = new int[]{ 14, 14, 16 };
			maxOffsetY = new int[]{ 10, 10, 10 };
		}

		int finalX = arenaBoundariesX[arenaChoice] + Utils.getRandomInclusive(maxOffsetX[arenaChoice]);
		int finalY = arenaBoundariesY[arenaChoice] + Utils.getRandomInclusive(maxOffsetY[arenaChoice]);
		locations[0] = (new WorldTile(finalX, finalY, 0));
		if (player.getLastDuelRules().getRule(25)) {
			int direction = Utils.getRandomInclusive(1);
			if (direction == 0)
				finalX--;
			else
				finalY++;
		} else {
			finalX = arenaBoundariesX[arenaChoice] + Utils.getRandomInclusive(maxOffsetX[arenaChoice]);
			finalY = arenaBoundariesY[arenaChoice] + Utils.getRandomInclusive(maxOffsetY[arenaChoice]);
		}
		locations[1] = (new WorldTile(finalX, finalY, 0));
		return locations;
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, ClientPacket packet) {
		synchronized (this) {
			synchronized (target.getControllerManager().getController()) {
				DuelRules rules = player.getLastDuelRules();
				switch (interfaceId) {
				case 271:
					if (rules.getRule(5) && isDueling) {
						player.sendMessage("You can't use prayers in this duel.");
						return false;
					}
					return true;
				case 193:
				case 430:
				case 192:
					if (rules.getRule(2) && isDueling)
						return false;
					return true;
				case 884:
					if (componentId == 4)
						if (rules.getRule(9) && isDueling) {
							player.sendMessage("You can't use special attacks in this duel.");
							return false;
						}
					return true;
				case 631:
					switch (componentId) {
					case 56: // no range
						rules.setRules(0);
						return false;
					case 57: // no melee
						rules.setRules(1);
						return false;
					case 58: // no magic
						rules.setRules(2);
						return false;
					case 59: // fun wep
						rules.setRules(8);
						return false;
					case 60: // no forfiet
						rules.setRules(7);
						return false;
					case 61: // no drinks
						rules.setRules(3);
						return false;
					case 62: // no food
						rules.setRules(4);
						return false;
					case 63: // no prayer
						rules.setRules(5);
						return false;
					case 64: // no movement
						rules.setRules(25);
						if (rules.getRule(6)) {
							rules.setRule(6, false);
							player.sendMessage("You can't have obstacles without movement.");
						}
						return false;
					case 65: // obstacles
						rules.setRules(6);
						if (rules.getRule(25)) {
							rules.setRule(25, false);
							player.sendMessage("You can't have obstacles without movement.");
						}
						return false;
					case 66: // enable summoning
						rules.setRules(24);
						return false;
					case 67:// no spec
						rules.setRules(9);
						return false;
					case 21:// no helm
						rules.setRules(10);
						return false;
					case 22:// no cape
						rules.setRules(11);
						return false;
					case 23:// no ammy
						rules.setRules(12);
						return false;
					case 31:// arrows
						rules.setRules(23);
						return false;
					case 24:// weapon
						rules.setRules(13);
						return false;
					case 25:// body
						rules.setRules(14);
						return false;
					case 26:// shield
						rules.setRules(15);
						return false;
					case 27:// legs
						rules.setRules(17);
						return false;
					case 28:// ring
						rules.setRules(19);
						return false;
					case 29: // bots
						rules.setRules(20);
						return false;
					case 30: // gloves
						rules.setRules(22);
						return false;
					case 107:
						closeDuelInteraction(true, DuelStage.DECLINED);
						return false;
					case 46:
						accept(true);
						return false;
					case 47:
						switch (packet) {
						case IF_OP1:
							removeItem(slotId, 1);
							return false;
						case IF_OP2:
							removeItem(slotId, 5);
							return false;
						case IF_OP3:
							removeItem(slotId, 10);
							return false;
						case IF_OP4:
							Item item = player.getInventory().getItems().get(slotId);
							if (item == null)
								return false;
							removeItem(slotId, player.getLastDuelRules().getStake().getNumberOf(item));
							return false;
						case IF_OP5:
							player.getInventory().sendExamine(slotId);
							return false;
						case IF_OP7:
							player.getInventory().sendExamine(slotId);
							return false;
						default:
							break;
						}
						return false;
					}
				case 628:
					switch (packet) {
					case IF_OP1:
						// addItem(slotId, 1);
						return false;
					case IF_OP2:
						// addItem(slotId, 5);
						return false;
					case IF_OP3:
						// addItem(slotId, 10);
						return false;
					case IF_OP4:
						Item item = player.getInventory().getItems().get(slotId);
						if (item == null)
							return false;
						// addItem(slotId,
						// player.getInventory().getItems().getNumberOf(item));
						return false;
					case IF_OP5:
						player.getInventory().sendExamine(slotId);
						return false;
					case IF_OP7:
						player.getInventory().sendExamine(slotId);
						return false;
					default:
						break;
					}
				case 626:
					switch (componentId) {
					case 43:
						accept(false);
						return false;
					}
				case 637: // friendly
					switch (componentId) {
					case 25: // no range
						rules.setRules(0);
						return false;
					case 26: // no melee
						rules.setRules(1);
						return false;
					case 27: // no magic
						rules.setRules(2);
						return false;
					case 28: // fun wep
						rules.setRules(8);
						return false;
					case 29: // no forfiet
						rules.setRules(7);
						return false;
					case 30: // no drinks
						rules.setRules(3);
						return false;
					case 31: // no food
						rules.setRules(4);
						return false;
					case 32: // no prayer
						rules.setRules(5);
						return false;
					case 33: // no movement
						rules.setRules(25);
						if (rules.getRule(6)) {
							rules.setRule(6, false);
							player.sendMessage("You can't have movement without obstacles.");
						}
						return false;
					case 34: // obstacles
						rules.setRules(6);
						if (rules.getRule(25)) {
							rules.setRule(25, false);
							player.sendMessage("You can't have obstacles without movement.");
						}
						return false;
					case 35: // enable summoning
						rules.setRules(24);
						return false;
					case 36:// no spec
						rules.setRules(9);
						return false;
					case 43:// no helm
						rules.setRules(10);
						return false;
					case 44:// no cape
						rules.setRules(11);
						return false;
					case 45:// no ammy
						rules.setRules(12);
						return false;
					case 53:// arrows
						rules.setRules(23);
						return false;
					case 46:// weapon
						rules.setRules(13);
						return false;
					case 47:// body
						rules.setRules(14);
						return false;
					case 48:// shield
						rules.setRules(15);
						return false;
					case 49:// legs
						rules.setRules(17);
						return false;
					case 50:// ring
						rules.setRules(19);
						return false;
					case 51: // bots
						rules.setRules(20);
						return false;
					case 52: // gloves
						rules.setRules(22);
						return false;
					case 86:
						closeDuelInteraction(true, DuelStage.DECLINED);
						return false;
					case 21:
						accept(true);
						return false;
					}
				case 639:
					switch (componentId) {
					case 25:
						accept(false);
						return false;
					}
				}
			}
		}
		return true;
	}

	public boolean isDueling() {
		return isDueling;
	}

	public boolean hasTarget() {
		return target != null;
	}

	public Entity getTarget() {
		if (hasTarget())
			return target;
		return null;
	}

	enum DuelStage {
		DECLINED, NO_SPACE, SECOND, DONE
	}
}
