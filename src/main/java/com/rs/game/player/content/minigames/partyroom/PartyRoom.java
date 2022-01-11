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
package com.rs.game.player.content.minigames.partyroom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import com.rs.cache.loaders.ObjectType;
import com.rs.cache.loaders.interfaces.IFTargetParams;
import com.rs.cores.CoresManager;
import com.rs.game.ForceTalk;
import com.rs.game.World;
import com.rs.game.item.ItemsContainer;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.ItemConstants;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.ItemExamines;
import com.rs.utils.Ticks;

@PluginEventHandler
public class PartyRoom {

	public static final int CHEST_INTERFACE = 647;
	public static final int INVENTORY_INTERFACE = 648;

	public static boolean isDancing = false;
	public static boolean isDropping = false;
	public static int timer = -1;

	private static final int[] BALLOON_IDS = { 115, 116, 117, 118, 119, 120, 121, 122 };
	private static String[] SONG = { "We're the knights of the party room", "We dance round and round like a loon", "Quite often we like to sing", "Unfortunately we make a din", "We're the knights of the party room",
			"Do you like our helmet plumes?", "Everyone's happy now we can move", "Like a party animal in the groove" };

	public static ItemsContainer<Item> store;
	public static ItemsContainer<Item> drop;

	@SuppressWarnings("unchecked")
	@ServerStartupEvent
	public static void loadStorage() throws ClassNotFoundException {
		File storeFile = new File("./data/partyroom/store.json");
		File dropFile = new File("./data/partyroom/drop.json");
		if (!storeFile.exists())
			store = new ItemsContainer<>(216, false);
		else
			try {
				store = (ItemsContainer<Item>) JsonFileManager.loadJsonFile(storeFile, ItemsContainer.class);
			} catch (Exception e) {
				store = new ItemsContainer<>(216, false);
			}
		if (!dropFile.exists())
			drop = new ItemsContainer<>(216, false);
		else
			try {
				drop = (ItemsContainer<Item>) JsonFileManager.loadJsonFile(dropFile, ItemsContainer.class);
			} catch (Exception e) {
				drop = new ItemsContainer<>(216, false);
			}
	}

	public static void save() {
		try {
			File storeFile = new File("./data/partyroom/store.json");
			File dropFile = new File("./data/partyroom/drop.json");
			if (store == null)
				store = new ItemsContainer<>(216, false);
			if (drop == null)
				drop = new ItemsContainer<>(216, false);
			JsonFileManager.saveJsonFile(store, storeFile);
			JsonFileManager.saveJsonFile(drop, dropFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void openChest(Player player) {
		if (!player.getBank().checkPin())
			return;
		if (player.partyDeposit == null)
			player.partyDeposit = new ItemsContainer<>(8, false);
		player.getInterfaceManager().sendInterface(CHEST_INTERFACE);
		player.getInterfaceManager().sendInventoryInterface(INVENTORY_INTERFACE);
		player.getPackets().sendInterSetItemsOptionsScript(INVENTORY_INTERFACE, 0, 93, 4, 7, "Deposit", "Deposit-5", "Deposit-10", "Deposit-All", "Deposit-X");
		player.getPackets().setIFTargetParams(new IFTargetParams(INVENTORY_INTERFACE, 0, 0, 27).enableRightClickOptions(0,1,2,3,4,5,6,9));
		player.getPackets().sendInterSetItemsOptionsScript(CHEST_INTERFACE, 23, 529, 6, 36, "Value");
		player.getPackets().sendInterSetItemsOptionsScript(CHEST_INTERFACE, 24, 91, 6, 36, "Value");
		player.getPackets().sendInterSetItemsOptionsScript(CHEST_INTERFACE, 25, 92, 8, 1, "Withdraw");
		player.getPackets().setIFTargetParams(new IFTargetParams(CHEST_INTERFACE, 23, 0, 215).enableRightClickOptions(0,1,2,3,4,5,9));
		player.getPackets().setIFTargetParams(new IFTargetParams(CHEST_INTERFACE, 24, 0, 215).enableRightClickOptions(0,9));
		player.getPackets().setIFTargetParams(new IFTargetParams(CHEST_INTERFACE, 25, 0, 7).enableRightClickOptions(0,9));
		player.getPackets().setIFHidden(CHEST_INTERFACE, 26, true);
		refreshItems(player);
		player.setCloseInterfacesEvent(() -> {
			for (Item item : player.partyDeposit.getItems())
				if (item != null)
					player.getInventory().addItem(item);
			player.partyDeposit.clear();
		});
	}

	public static void purchase(Player player, boolean balloons) {
		if (balloons) {
			if (isDropping) {
				player.sendMessage("Please wait for the previous party to end.");
				return;
			}
			for (Item item : store.getItems()) {
				if (item == null)
					continue;
				if (drop.freeSlots() <= 0)
					break;
				drop.add(item);
				store.remove(item);
			}
			if (drop.getUsedSlots() < 5) {
				player.sendMessage("Please deposit 5 or more items into the chest before starting.");
				return;
			}
			startBalloonTimer();
		} else if (!isDancing)
			startDancingKnights();
	}

	public static void startDancingKnights() {
		isDancing = true;
		final NPC[] npcs = new NPC[6];
		for (int i = 0; i < 6; i++) {
			npcs[i] = new NPC(660, new WorldTile(3043 + i, 3378, 0));
			npcs[i].setFaceAngle(0);
		}
		WorldTasks.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop < SONG.length)
					npcs[2].setNextForceTalk(new ForceTalk(SONG[loop]));
				else {
					for (int i = 0; i < 6; i++)
						npcs[i].finish();
					isDancing = false;
					stop();
				}
				loop++;
			}
		}, 0, 3);

	}

	public static int getTimeLeft() {
		return (timer * 600) / 1000;
	}

	public static int getRandomBalloon() {
		return BALLOON_IDS[Utils.random(BALLOON_IDS.length)];
	}

	public static Item getNextItem() {
		ArrayList<Item> items = new ArrayList<>();
		for (Item item : drop.getItems())
			if (item != null)
				items.add(item);
		if (items.isEmpty())
			return null;
		Item item = items.get(Utils.random(items.size()));
		drop.remove(item);
		return item;
	}

	public static ObjectClickHandler handleBalloons = new ObjectClickHandler(new Object[] { 115, 116, 117, 118, 119, 120, 121, 122 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject() instanceof Balloon balloon) {
				if (e.getPlayer().isIronMan()) {
					e.getPlayer().sendMessage("You can't pop a party balloon as an ironman.");
					if (balloon.getItem() != null)
						e.getPlayer().sendMessage("You would have gotten " + balloon.getItem().getDefinitions().getName() + " though.");
					return;
				}
				balloon.handlePop(e.getPlayer());
			} else {
				e.getPlayer().setNextAnimation(new Animation(794));
				e.getPlayer().lock();
				World.removeObject(e.getObject());
				final GameObject poppedBalloon = new GameObject(e.getObject().getId() + 8, ObjectType.SCENERY_INTERACT, e.getObject().getRotation(), e.getObject().getX(), e.getObject().getY(), e.getObject().getPlane());
				World.spawnObject(poppedBalloon);
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						World.removeObject(poppedBalloon);
						e.getPlayer().unlock();
					}
				}, 1);
			}
		}
	};

	public static void spawnBalloons() {
		ArrayList<Balloon> balloons = new ArrayList<>();
		for (int x = 3038; x < 3054; x++)
			for (int y = 3373; y < 3384; y++) {
				if (x <= 3049 && x >= 3042 && y == 3378)
					continue;
				if (World.floorAndWallsFree(0, x, y, 1) && World.getObject(new WorldTile(x, y, 0)) == null)
					balloons.add(new Balloon(getRandomBalloon(), 0, x, y, 0));
			}
		Collections.shuffle(balloons);
		for (Balloon balloon : balloons)
			if (balloon != null)
				World.spawnObjectTemporary(balloon.setItem(getNextItem()), Ticks.fromMinutes(2));
		CoresManager.schedule(() -> {
			try {
				isDropping = false;
				timer = -1;
			} catch (Throwable e) {
				Logger.handle(e);
			}
		}, Ticks.fromMinutes(2));
	}

	public static void startBalloonTimer() {
		isDropping = true;
		timer = 200;
	}

	public static void yellNpcs() {
		for (NPC npc : World.getNPCs()) {
			if (!npc.getDefinitions().getName().toLowerCase().contains("banker") && !npc.getDefinitions().getName().toLowerCase().contains("party"))
				continue;
			npc.setNextForceTalk(new ForceTalk("There is a drop party worth " + Utils.formatLong(getTotalCoins()) + " coins starting in " + getTimeLeft() + " seconds!"));
		}
	}

	public static void refreshItems(Player player) {
		player.getPackets().sendItems(529, store);
		player.getPackets().sendItems(91, drop);
		player.getPackets().sendItems(92, player.partyDeposit);
	}

	public static void addToChest(Player player) {
		for (Item item : player.partyDeposit.getItems())
			if (item != null) {
				if (store.freeSlots() <= 0) {
					player.sendMessage("There was not enough room in the chest for all those items.");
					return;
				}
				player.partyDeposit.remove(item);
				store.add(item);
			}
		refreshItems(player);
	}

	public static void addDeposit(Player player, Item item, int amount) {
		if (!ItemConstants.isTradeable(item)) {
			player.sendMessage("You can't deposit that.");
			return;
		}
		if (player.getRights() == Rights.ADMIN) {
			player.sendMessage("Administrators cannot add items to this chest.");
			return;
		}
		if (player.getInventory().getAmountOf(item.getId()) < amount)
			amount = player.getInventory().getAmountOf(item.getId());
		if (player.getInventory().containsItem(item.getId(), amount)) {
			if (player.partyDeposit.freeSlots() <= 0) {
				player.sendMessage("Please deposit or withdraw some items before putting more in.");
				return;
			}
			if (!item.getDefinitions().isStackable() && player.partyDeposit.freeSlots() < amount)
				amount = player.partyDeposit.freeSlots();
			player.partyDeposit.add(new Item(item.getId(), amount));
			player.getInventory().deleteItem(item.getId(), amount);
		}
		refreshItems(player);
	}

	public static void removeDeposit(Player player, int slot) {
		Item item = player.partyDeposit.get(slot);
		if (item == null)
			return;
		if (player.partyDeposit.contains(item)) {
			if (!player.getInventory().addItem(item)) {
				player.sendMessage("You don't have enough space in your inventory.");
				return;
			}
			player.partyDeposit.remove(new Item(item.getId(), item.getAmount()));
		}
		refreshItems(player);
	}

	public static ButtonClickHandler handleButtons = new ButtonClickHandler(CHEST_INTERFACE, INVENTORY_INTERFACE) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getInterfaceId() == CHEST_INTERFACE) {
				if (e.getComponentId() == 25) {
					Item item = e.getPlayer().partyDeposit.get(e.getSlotId());
					if (item == null)
						return;
					if (e.getPacket() == ClientPacket.IF_OP1)
						removeDeposit(e.getPlayer(), e.getSlotId());
					else
						e.getPlayer().sendMessage(ItemExamines.getExamine(item));
				} else if (e.getComponentId() == 21)
					addToChest(e.getPlayer());
				else if (e.getComponentId() == 23) {
					Item item = store.get(e.getSlotId());
					if (item == null)
						return;
					if (e.getPacket() == ClientPacket.IF_OP1)
						e.getPlayer().sendMessage("Item valued at: " + item.getDefinitions().getValue());
					else
						e.getPlayer().sendMessage(ItemExamines.getExamine(item));
				}
			} else if (e.getInterfaceId() == INVENTORY_INTERFACE) {
				final Item item = e.getPlayer().getInventory().getItem(e.getSlotId());
				if (item == null)
					return;
				switch (e.getPacket()) {
				case IF_OP1:
					addDeposit(e.getPlayer(), item, 1);
					break;
				case IF_OP2:
					addDeposit(e.getPlayer(), item, 5);
					break;
				case IF_OP3:
					addDeposit(e.getPlayer(), item, 10);
					break;
				case IF_OP4:
					addDeposit(e.getPlayer(), item, e.getPlayer().getInventory().getAmountOf(item.getId()));
					break;
				case IF_OP5:
					e.getPlayer().sendInputInteger("How many would you like to deposit?", amount -> addDeposit(e.getPlayer(), item, amount));
					break;
				case IF_OP10:
					e.getPlayer().sendMessage(ItemExamines.getExamine(item));
					break;
				default:
					break;
				}
			}
		}
	};

	public static long getTotalCoins() {
		long total = 0;
		for (Item item : drop.getItems())
			if (item != null)
				total += item.getDefinitions().getValue() * item.getAmount();
		return total;
	}

}
