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
package com.rs.game.model.entity.player;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.interfaces.IFEvents;
import com.rs.cache.loaders.interfaces.IFEvents.UseFlag;
import com.rs.game.World;
import com.rs.game.content.ItemConstants;
import com.rs.game.content.pets.Pet;
import com.rs.game.content.skills.slayer.npcs.ConditionalDeath;
import com.rs.game.content.skills.summoning.Familiar;
import com.rs.game.content.skills.summoning.Pouch;
import com.rs.game.model.entity.interactions.StandardEntityInteraction;
import com.rs.game.model.item.ItemsContainer;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.net.decoders.handlers.InventoryOptionsHandler;
import com.rs.plugin.PluginManager;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemAddedToInventoryEvent;
import com.rs.plugin.events.ItemOnNPCEvent;
import com.rs.plugin.events.ItemOnPlayerEvent;
import com.rs.plugin.events.NPCInteractionDistanceEvent;
import com.rs.plugin.handlers.*;
import com.rs.utils.ItemConfig;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@PluginEventHandler
public final class Inventory {

	private long coins;
	private final ItemsContainer<Item> items;

	private transient Player player;
	private transient double inventoryWeight;
	private final transient Set<Integer> slotsToUpdate = new HashSet<>();
	private transient boolean updateAll = true;

	public static final int INVENTORY_INTERFACE = 679;

	public Inventory() {
		items = new ItemsContainer<>(28, false);
	}

	public static ButtonClickHandler handleInventoryButtons = new ButtonClickHandler(INVENTORY_INTERFACE, e -> {
		if (e.getComponentId() == 0) {
			if (e.getSlotId() > 27 || e.getPlayer().getInterfaceManager().containsInventoryInter())
				return;
			Item item = e.getPlayer().getInventory().getItem(e.getSlotId());
			if (item == null || item.getId() != e.getSlotId2()) {
				e.getPlayer().getInventory().refresh(e.getSlotId());
				return;
			}
			if (e.getPacket() == ClientPacket.IF_OP1)
				InventoryOptionsHandler.handleItemOption1(e.getPlayer(), e.getSlotId(), e.getSlotId2(), item);
			else if (e.getPacket() == ClientPacket.IF_OP2)
				InventoryOptionsHandler.handleItemOption2(e.getPlayer(), e.getSlotId(), e.getSlotId2(), item);
			else if (e.getPacket() == ClientPacket.IF_OP3)
				InventoryOptionsHandler.handleItemOption3(e.getPlayer(), e.getSlotId(), e.getSlotId2(), item);
			else if (e.getPacket() == ClientPacket.IF_OP4)
				InventoryOptionsHandler.handleItemOption4(e.getPlayer(), e.getSlotId(), e.getSlotId2(), item);
			else if (e.getPacket() == ClientPacket.IF_OP5)
				InventoryOptionsHandler.handleItemOption5(e.getPlayer(), e.getSlotId(), e.getSlotId2(), item);
			else if (e.getPacket() == ClientPacket.IF_OP7)
				InventoryOptionsHandler.handleItemOption6(e.getPlayer(), e.getSlotId(), e.getSlotId2(), item);
			else if (e.getPacket() == ClientPacket.IF_OP8)
				InventoryOptionsHandler.handleItemOption7(e.getPlayer(), e.getSlotId(), e.getSlotId2(), item);
			else if (e.getPacket() == ClientPacket.IF_OP10)
				InventoryOptionsHandler.handleItemOption8(e.getPlayer(), e.getSlotId(), e.getSlotId2(), item);
		}
	});

	public static InterfaceOnInterfaceHandler handleItemOnItem = new InterfaceOnInterfaceHandler(INVENTORY_INTERFACE, INVENTORY_INTERFACE, e -> {
		if (e.getPlayer().getInterfaceManager().containsInventoryInter())
			return;
		if (e.getFromSlotId() < 0 || e.getFromSlotId() >= 28 || e.getToSlotId() < 0 || e.getToSlotId() >= 28)
			return;
		Item usedWith = e.getPlayer().getInventory().getItem(e.getToSlotId());
		Item itemUsed = e.getPlayer().getInventory().getItem(e.getFromSlotId());
		if (itemUsed == null || usedWith == null || itemUsed.getId() != e.getFromSlotId2() || usedWith.getId() != e.getToSlotId2())
			return;
		if (e.getFromSlotId() == e.getToSlotId())
			return;
		e.getPlayer().stopAll();
		if (!InventoryOptionsHandler.handleItemOnItem(e.getPlayer(), itemUsed, usedWith, e.getFromSlotId(), e.getToSlotId()))
			e.getPlayer().sendMessage("Nothing interesting happens.");
	});
	
	public static InterfaceOnPlayerHandler handleItemOnPlayer = new InterfaceOnPlayerHandler(false, new int[] { 679 }, e -> {
		Item item = e.getPlayer().getInventory().getItem(e.getSlotId());
		if (item == null)
			return;
		if (!e.getPlayer().getControllerManager().processItemOnPlayer(e.getTarget(), item, e.getSlotId()))
			return;
		e.getPlayer().stopAll(false);
		if (PluginManager.handle(new ItemOnPlayerEvent(e.getPlayer(), e.getTarget(), item, false)))
			return;
		e.getPlayer().getInteractionManager().setInteraction(new StandardEntityInteraction(e.getTarget(), 0, () -> {
			if (!e.getPlayer().getInventory().containsItem(item.getId(), item.getAmount()))
				return;
			e.getPlayer().faceEntityTile(e.getTarget());
			PluginManager.handle(new ItemOnPlayerEvent(e.getPlayer(), e.getTarget(), item, true));
		}));
	});
	
	public static InterfaceOnNPCHandler handleItemOnNpc = new InterfaceOnNPCHandler(false, new int[] { 679 }, e -> {
		Item item = e.getPlayer().getInventory().getItem(e.getSlotId());
		if (item == null)
			return;
		if (!e.getPlayer().getControllerManager().processItemOnNPC(e.getTarget(), item))
			return;
		e.getPlayer().stopAll(false);
		if (PluginManager.handle(new ItemOnNPCEvent(e.getPlayer(), e.getTarget(), item.setSlot(e.getSlotId()), false)))
			return;
		Object dist = PluginManager.getObj(new NPCInteractionDistanceEvent(e.getPlayer(), e.getTarget()));
		int distance = 0;
		if (dist != null)
			distance = (int) dist;

		e.getPlayer().getInteractionManager().setInteraction(new StandardEntityInteraction(e.getTarget(), distance, () -> {
			if (!e.getPlayer().getInventory().containsItem(item.getId(), item.getAmount()))
				return;
			e.getPlayer().faceEntityTile(e.getTarget());
			
			//TODO move this block to plugins after mapping NPC ids support
			if (e.getTarget() instanceof Familiar f && f.getPouch() == Pouch.GEYSER_TITAN) {
				if (e.getTarget().getId() == 7339 || e.getTarget().getId() == 7339)
					if ((item.getId() >= 1704 && item.getId() <= 1710 && item.getId() % 2 == 0) || (item.getId() >= 10356 && item.getId() <= 10366 && item.getId() % 2 == 0) || (item.getId() == 2572 || (item.getId() >= 20653 && item.getId() <= 20657 && item.getId() % 2 != 0))) {
						for (Item i : e.getPlayer().getInventory().getItems().array()) {
							if (i == null)
								continue;
							if (i.getId() >= 1704 && i.getId() <= 1710 && i.getId() % 2 == 0)
								i.setId(1712);
							else if (i.getId() >= 10356 && i.getId() <= 10362 && i.getId() % 2 == 0)
								i.setId(10354);
							else if (i.getId() == 2572 || (i.getId() >= 20653 && i.getId() <= 20657 && i.getId() % 2 != 0))
								i.setId(20659);
						}
						e.getPlayer().getInventory().refresh();
						e.getPlayer().itemDialogue(1712, "Your ring of wealth and amulet of glory have all been recharged.");
					}
			} else if (e.getTarget() instanceof Pet p) {
				e.getPlayer().faceEntityTile(e.getTarget());
				e.getPlayer().getPetManager().eat(item.getId(), p);
				return;
			} else if (e.getTarget() instanceof ConditionalDeath cd) {
				cd.useHammer(e.getPlayer());
				return;
			}
			
			PluginManager.handle(new ItemOnNPCEvent(e.getPlayer(), e.getTarget(), item, true));
		}));
	});

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void replaceItem(int itemId, int amount, int slot) {
		if (items.get(slot) == null)
			return;
		items.get(slot).setId(itemId);
		items.get(slot).setAmount(amount);
		refresh();
	}

	public void replace(Item item, Item newItem) {
		if (items.get(item.getSlot()) == null)
			throw new RuntimeException("Yikes. Expected a " + item.getId() + " but was not found.");
		if (item.getId() != items.get(item.getSlot()).getId())
			throw new RuntimeException("Yikes. Expected a " + item.getId() + " but found " + items.get(item.getSlot()).getId() + " instead.");
		items.set(item.getSlot(), newItem);
		refresh();
	}
	
	public void replace(int fromId, int toId) {
		for (Item item : items.array()) {
			if (item == null)
				continue;
			if (item.getId() == fromId) {
				item.setId(toId);
				refresh();
				return;
			}
		}
	}

	public void init() {
		refresh();
		items.initSlots();
		player.getPackets().sendRunScript(5560, coins > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) coins);
	}

	public void unlockInventoryOptions() {
		player.getPackets().setIFEvents(new IFEvents(INVENTORY_INTERFACE, 0, 0, 27)
				.enableUseOptions(UseFlag.GROUND_ITEM,UseFlag.NPC,UseFlag.WORLD_OBJECT,UseFlag.PLAYER,UseFlag.ICOMPONENT)
				.enableRightClickOptions(0,1,2,6,7,9)
				.setDepth(1)
				.enableUseTargetability());
		player.getPackets().setIFEvents(new IFEvents(INVENTORY_INTERFACE, 0, 28, 55).enableDrag());
	}

	public void reset() {
		items.reset();
		init(); // as all slots reseted better just send all again
	}

	public void refresh(int... slots) {
		for (int i : slots)
			slotsToUpdate.add(i);
	}

	public boolean hasMaterials(Item[] mats) {
		for (Item mat : mats)
			if (getAmountOf(mat.getId()) < mat.getAmount())
				return false;
		return true;
	}

	public boolean addItem(int itemId) {
		return addItem(itemId, 1);
	}

	public boolean addItem(int itemId, int amount) {
		Item item = new Item(itemId, amount);
		PluginManager.handle(new ItemAddedToInventoryEvent(player, item));
		if (item.getId() < 0 || item.getAmount() < 0 || !Utils.itemExists(item.getId()) || !player.getControllerManager().canAddInventoryItem(item.getId(), item.getAmount()))
			return false;
		Item[] itemsBefore = items.getItemsCopy();
		if (!items.add(item)) {
			items.add(new Item(item.getId(), items.getFreeSlots()));
			player.sendMessage("Not enough space in your inventory.");
			refreshItems(itemsBefore);
			return false;
		}
		refreshItems(itemsBefore);
		return true;
	}

	public int getAmountOf(int itemId) {
		if (PluginManager.getAmountPlayerHas(player, itemId) != -1)
			return PluginManager.getAmountPlayerHas(player, itemId);
		return items.getNumberOf(itemId);
	}

	public boolean addItem(Item item) {
		PluginManager.handle(new ItemAddedToInventoryEvent(player, item));
		if (item.getId() < 0 || item.getAmount() < 0 || !Utils.itemExists(item.getId()) || !player.getControllerManager().canAddInventoryItem(item.getId(), item.getAmount()))
			return false;
		Item[] itemsBefore = items.getItemsCopy();
		if (!items.add(item)) {
			if (!item.getDefinitions().isStackable() && item.getMetaData() != null)
				items.add(new Item(item.getId(), items.getFreeSlots(), item.getMetaData()));
			player.sendMessage("Not enough space in your inventory.");
			refreshItems(itemsBefore);
			return false;
		}
		refreshItems(itemsBefore);
		return true;
	}

	public void deleteItem(int slot, Item item) {
		if (!player.getControllerManager().canDeleteInventoryItem(item.getId(), item.getAmount()))
			return;
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(slot, item);
		refreshItems(itemsBefore);
	}

	public void removeItems(Item... list) {
		for (Item item : list) {
			if (item == null)
				continue;
			deleteItem(item);
		}
	}
	
	public void removeAllItems(int... list) {
		for (int item : list) {
			if (!containsItem(item, 1))
				continue;
			deleteItem(item, Integer.MAX_VALUE);
		}
	}

	public void deleteItem(int itemId, int amount) {
		if (!player.getControllerManager().canDeleteInventoryItem(itemId, amount))
			return;
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(new Item(itemId, amount));
		refreshItems(itemsBefore);
	}

	public void deleteItem(Item item) {
		if ((item == null) || !player.getControllerManager().canDeleteInventoryItem(item.getId(), item.getAmount()))
			return;
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(item);
		refreshItems(itemsBefore);
	}

	/*
	 * No refresh needed its client to who does it :p
	 */
	public void switchItem(int fromSlot, int toSlot) {
		Item[] itemsBefore = items.getItemsCopy();
		Item fromItem = items.get(fromSlot);
		Item toItem = items.get(toSlot);
		items.set(fromSlot, toItem);
		items.set(toSlot, fromItem);
		refreshItems(itemsBefore);
	}

	public void refreshItems(Item[] itemsBefore) {
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++)
			if (itemsBefore[index] != items.array()[index])
				changedSlots[count++] = index;
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
	}

	public ItemsContainer<Item> getItems() {
		return items;
	}

	public boolean hasFreeSlots() {
		return items.getFreeSlot() != -1;
	}

	public int getFreeSlots() {
		return items.getFreeSlots();
	}

	public int getNumberOf(int itemId) {
		return getNumberOf(itemId, true);
	}

	public int getNumberOf(int itemId, boolean checkToolbelt) {
		if (PluginManager.getAmountPlayerHas(player, itemId) != -1)
			return PluginManager.getAmountPlayerHas(player, itemId);
		int amount = items.getNumberOf(itemId);
		if (checkToolbelt && player.containsTool(itemId))
			amount++;
		return amount;
	}

	public Item getItem(int slot) {
		return items.get(slot);
	}

	public boolean hasRoomFor(Item... item) {
		return hasRoomFor(null, item);
	}

	public boolean hasRoomFor(Item[] deleting, Item... adding) {
		int freeSlots = getFreeSlots();
		int freedSlots = 0;
		if (deleting != null)
			for (Item i : deleting) {
				if (i == null)
					continue;
				if (!i.getDefinitions().isStackable() || (i.getDefinitions().isStackable() && getNumberOf(i.getId(), false) <= i.getAmount()))
					freedSlots++;
			}
		freeSlots += freedSlots;
		int neededSlots = 0;
		for (Item i : adding) {
			if (i == null)
				continue;
			if (i.getDefinitions().isStackable() && (getNumberOf(i.getId()) + i.getAmount()) <= 0)
				return false;
			if (!i.getDefinitions().isStackable() || (i.getDefinitions().isStackable() && getNumberOf(i.getId(), false) <= 0))
				neededSlots++;
		}
		return freeSlots >= neededSlots;
	}

	public Item getItemById(int id) {
		for (Item item : items.array())
			if (item != null && item.getId() == id)
				return item;
		return null;
	}

	public boolean hasFreeSlots(Item[] mats, Item product) {
		boolean allStack = true;
		for (Item i : mats)
			if (i != null)
				if (!i.getDefinitions().isStackable())
					allStack = false;
        return hasRoomFor(product) || (mats.length > 0 && !allStack);
    }

	public int getItemsContainerSize() {
		return items.getSize();
	}

	public boolean containsItem(String name) {
		for (Item item : items.toArray())
			if (item != null)
				if (item.getDefinitions().getName().equalsIgnoreCase(name))
					return true;
		return false;
	}

	public boolean containsItems(int... itemIds) {
		for (int itemId : itemIds)
			if (!items.contains(new Item(itemId, 1)))
				return false;
		return true;
	}

	public boolean containsItems(Item... item) {
		for (Item element : item)
			if (!items.contains(element))
				return false;
		return true;
	}

	public boolean containsItems(int[] itemIds, int[] amounts) {
		int size = itemIds.length > amounts.length ? amounts.length : itemIds.length;
		for (int i = 0; i < size; i++)
			if (!items.contains(new Item(itemIds[i], amounts[i])))
				return false;
		return true;
	}

	public boolean containsItem(Item item) {
		return containsItem(item.getId(), item.getAmount(), true);
	}

	public boolean containsItem(int itemId, int amount) {
		return containsItem(itemId, amount, true);
	}

	public boolean containsItem(int itemId, int amount, boolean checkToolbelt) {
		if (PluginManager.getAmountPlayerHas(player, itemId) != -1)
			return PluginManager.getAmountPlayerHas(player, itemId) >= amount;
        return items.contains(new Item(itemId, amount)) || (checkToolbelt && player.containsTool(itemId));
    }

	public boolean missingItems(int... itemIds) {
		for (int itemId : itemIds) {
			boolean found = false;
			for (Item item : items.array()) {
				if (item == null)
					continue;
				if (item.getId() == itemId)
					found = true;
			}
			if (found)
				return false;
		}
		return true;
	}

	public boolean containsOneItem(int... itemIds) {
		return containsOneItem(true, itemIds);
	}

	public boolean containsOneItem(boolean checkToolbelt, int... itemIds) {
		for (int itemId : itemIds)
			if (items.containsOne(new Item(itemId, 1)) || (checkToolbelt && player.containsTool(itemId)))
				return true;
		return false;
	}

	public void sendExamine(int slotId) {
		if (slotId >= getItemsContainerSize())
			return;
		Item item = items.get(slotId);
		if (item == null)
			return;
		ItemDefinitions def = ItemDefinitions.getDefs(item.getId());
		player.getPackets().sendInventoryMessage(0, slotId, ItemConfig.get(item.getId()).getExamine(item) + (ItemConstants.isTradeable(item) ? (" General store: " + Utils.formatTypicalInteger(item.getDefinitions().getSellPrice()) + " High Alchemy: " + Utils.formatTypicalInteger(def.getHighAlchPrice())) : ""));
		if (item.getMetaData("combatCharges") != null)
			player.sendMessage("<col=FF0000>It looks like it will last another " + Utils.ticksToTime(item.getMetaDataI("combatCharges")));
		else if (item.getMetaData("brawlerCharges") != null)
			player.sendMessage("These gloves have " + item.getMetaDataI("brawlerCharges") + " charges left.");
	}

	public void refresh() {
		updateAll = true;
	}
	
	public void processRefresh() {
		boolean needsRefresh = updateAll || !slotsToUpdate.isEmpty();
		if (updateAll) {
			player.getPackets().sendItems(93, items);
		} else if (!slotsToUpdate.isEmpty()) {
			player.getPackets().sendUpdateItems(93, items, slotsToUpdate.stream().mapToInt(Integer::intValue).toArray());
		}
		if (needsRefresh) {
			updateAll = false;
			slotsToUpdate.clear();
			refreshConfigs();
		}
	}

	public boolean addItem(int itemId, int amount, boolean dropIfInvFull) {
		return addItem(new Item(itemId, amount), dropIfInvFull);
	}

	public boolean addItem(Item item, boolean dropIfInvFull) {
		if (item.getId() < 0 || item.getAmount() < 0 || !Utils.itemExists(item.getId()) || !player.getControllerManager().canAddInventoryItem(item.getId(), item.getAmount()))
			return false;
		Item[] itemsBefore = items.getItemsCopy();
		int numberToDrop = 0;
		if (!items.add(item)) {
			numberToDrop = item.getAmount() - items.getFreeSlots();
			items.add(new Item(item).setAmount(items.getFreeSlots()));
			player.sendMessage("Not enough space in your inventory.");
			World.addGroundItem(item.setAmount(numberToDrop), Tile.of(player.getTile()), player, true, 60);
			refreshItems(itemsBefore);
			return true;
		}
		PluginManager.handle(new ItemAddedToInventoryEvent(player, new Item(item.getId(), numberToDrop)));
		refreshItems(itemsBefore);
		return true;
	}

	public void refreshConfigs() {
		double w = 0;
		for (Item item : items.array()) {
			if (item == null)
				continue;
			w += ItemConfig.get(item.getId()).getWeight(false);
		}
		inventoryWeight = w;
		player.getPackets().refreshWeight(player.getEquipment().getEquipmentWeight() + inventoryWeight);
	}

	public boolean addItemDrop(int id, int amount) {
		return addItem(id, amount, true);
	}

	public boolean addItemDrop(Item item) {
		return addItem(item, true);
	}

	public boolean containsItems(List<Item> list) {
		for (Item item : list)
			if (!items.contains(item))
				return false;
		return true;
	}

	public boolean removeItems(List<Item> list) {
		for (Item item : list) {
			if (item == null)
				continue;
			deleteItem(item);
		}
		return true;
	}

	public double getInventoryWeight() {
		return inventoryWeight;
	}

	public boolean containsItem(int id) {
		return containsItem(id, 1);
	}

	public boolean containsItemNoted(Item item) {
		int itemId = item.getId();
		int notedId = item.getDefinitions().getCertId();
		return containsItem(itemId, item.getAmount()) || containsItem(notedId, item.getAmount());
	}

	public void deleteItemNoted(Item item) {
		int itemId = item.getId();
		int notedId = item.getDefinitions().getCertId();
		if (containsItem(itemId))
			deleteItem(itemId, item.getAmount());
		else
			deleteItem(notedId, item.getAmount());
	}

	public int getTotalNumberOf(int... ids) {
		int count = 0;
		for (int id : ids)
			count += getNumberOf(id);
		return count;
	}
	
	public static ItemClickHandler coinPouch = new ItemClickHandler(new Object[] { 995 }, new String[] { "Add-to-pouch" }, e -> {
		if ((e.getPlayer().getInventory().getCoins() + e.getItem().getAmount()) < 0L) {
			e.getPlayer().sendMessage("Your pouch is full somehow. This kind of money probably deserves a ban LOL.");
			return;
		}
		e.getPlayer().getInventory().deleteItem(e.getItem());
		e.getPlayer().getInventory().addCoins(e.getItem().getAmount());
	});
	
	public void coinPouchToInventory(int num) {
		if (num > coins)
			num = (int) coins;
		if (num <= 0)
			return;
		if ((getNumberOf(995) + num) < 0)
			num = Integer.MAX_VALUE - getNumberOf(995) - 1;
		coins -= num;
		player.getPackets().sendRunScript(5561, num, 0);
		player.getPackets().sendRunScript(5560, coins > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) coins);
		addItemDrop(995, num);
	}

	public void addCoins(int num) {
		if (num <= 0)
			return;
		if ((coins + num) > 0L) {
			coins += num;
			player.getPackets().sendRunScript(5561, num, 1);
			player.getPackets().sendRunScript(5560, coins > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) coins);
			return;
		}
		addItemDrop(995, num);
	}
	
	public boolean hasCoins(int num) {
		if (coins >= num)
			return true;
        return coins + getNumberOf(995) >= num;
    }
	
	public long getCoins() {
		long total = coins + getNumberOf(995);
		if (total < 0L)
			return coins;
		return total;
	}

	public long getPouchCoins() {
		return coins;
	}

	public int getPouchCoinsAsInt() {
		long coins = getPouchCoins();
		if (coins > Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		return (int) coins;
	}
	
	public int getCoinsAsInt() {
		long coins = getCoins();
		if (coins > Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		return (int) coins;
	}
	
	public void removeCoins(int num) {
		removeCoins(num, false);
	}
	
	public void removeCoins(int num, boolean pouchOnly) {
		if (num <= 0)
			return;
		
		if (coins >= num) {
			coins -= num;
			player.getPackets().sendRunScript(5561, num, 0);
			player.getPackets().sendRunScript(5560, coins > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) coins);
			return;
		}
		if (pouchOnly)
			return;
		if (coins > 0) {
			num -= coins;
			player.getPackets().sendRunScript(5561, (int) coins, 0);
			player.getPackets().sendRunScript(5560, coins > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) coins);
			coins = 0;
		}
		if (getNumberOf(995) >= num)
			deleteItem(995, num);
		else {
			player.sendMessage("Error removing coins. Please report how you managed to produce this message.");
			Logger.error(Inventory.class, "removeCoins ("+player.getUsername()+")", Arrays.toString(Thread.currentThread().getStackTrace()));
		}
	}

	public Item findItemByIds(int... ids) {
		for (int itemId : ids) {
			Item item = getItemById(itemId);
			if (item != null)
				return item;
		}
		return null;
	}

}
