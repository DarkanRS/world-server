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
package com.rs.game.player;

import com.rs.cache.loaders.Bonus;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.BodyGlow;
import com.rs.game.item.ItemsContainer;
import com.rs.game.player.content.Effect;
import com.rs.game.player.content.EnchantedHeadwear;
import com.rs.game.player.content.ItemConstants;
import com.rs.game.player.content.interfacehandlers.ItemsKeptOnDeath;
import com.rs.game.player.content.skills.firemaking.Bonfire;
import com.rs.game.player.content.skills.runecrafting.Runecrafting;
import com.rs.game.player.content.transportation.ItemTeleports;
import com.rs.game.player.managers.PriceChecker;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Utils;
import com.rs.plugin.PluginManager;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ItemEquipEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.utils.ItemExamines;
import com.rs.utils.ItemWeights;

@PluginEventHandler
public final class Equipment {

	public static final byte
	HEAD = 0,
	CAPE = 1,
	NECK = 2,
	WEAPON = 3,
	CHEST = 4,
	SHIELD = 5,
	LEGS = 7,
	HANDS = 9,
	FEET = 10,
	RING = 12,
	AMMO = 13,
	AURA = 14;

	public static final int SIZE = 15;

	private ItemsContainer<Item> items;

	private transient Player player;
	private transient int equipmentHpIncrease;
	private transient double equipmentWeight;

	static final int[] DISABLED_SLOTS = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0 };

	public Equipment() {
		items = new ItemsContainer<>(15, false);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void replace(Item item, Item newItem) {
		if (item != null)
			PluginManager.handle(new ItemEquipEvent(player, item, false));
		if (newItem != null)
			PluginManager.handle(new ItemEquipEvent(player, newItem, true));
		items.set(item.getSlot(), newItem);
		refresh();
	}

	public void init() {
		for (int i = 0;i < items.getSize();i++) {
			Item item = items.get(i);
			if (item == null)
				continue;
			if (!ItemConstants.canWear(item, player)) {
				if (player.getInventory().hasFreeSlots()) {
					player.getInventory().addItem(item);
					player.sendMessage("You no longer meet the requirements for " + item.getDefinitions().getName() + " and it has been unequipped.");
				} else {
					player.getBank().addItem(item, false);
					player.sendMessage("You no longer meet the requirements for " + item.getDefinitions().getName() + " and it has been sent to your bank.");
				}
				items.set(i, null);
			}
		}
		player.getPackets().sendItems(94, items);
		refresh(null);
	}

	public void refresh(int... slots) {
		if (slots != null) {
			player.getPackets().sendUpdateItems(94, items, slots);
			player.getCombatDefinitions().checkAttackStyle();
		}
		player.getCombatDefinitions().refreshBonuses();
		refreshConfigs(slots == null);
	}

	public void reset() {
		for (Item item : getItemsCopy())
			if (item != null)
				PluginManager.handle(new ItemEquipEvent(player, item, false));
		items.reset();
		init();
	}

	public Item getItem(int slot) {
		return items.get(slot);
	}

	public Item getItemById(int id) {
		for (Item item : items.getItems())
			if (item != null && item.getId() == id)
				return item;
		return null;
	}

	public void sendExamine(int slotId) {
		Item item = items.get(slotId);
		if (item == null)
			return;
		player.sendMessage(ItemExamines.getExamine(item));
		if (item.getMetaData("combatCharges") != null)
			player.sendMessage("<col=FF0000>It looks like it will last another " + Utils.ticksToTime(item.getMetaDataI("combatCharges")));
		else if (item.getMetaData("brawlerCharges") != null)
			player.sendMessage("These gloves have " + item.getMetaDataI("brawlerCharges") + " charges left.");
	}

	public boolean containsOneItem(int... itemIds) {
		for (int itemId : itemIds)
			if (items.containsOne(new Item(itemId, 1)))
				return true;
		return false;
	}

	public boolean fullGuthansEquipped() {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int weaponId = player.getEquipment().getWeaponId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1)
			return false;
		return ItemDefinitions.getDefs(helmId).getName().contains("Guthan's") &&
				ItemDefinitions.getDefs(chestId).getName().contains("Guthan's") &&
				ItemDefinitions.getDefs(legsId).getName().contains("Guthan's")
				&& ItemDefinitions.getDefs(weaponId).getName().contains("Guthan's");
	}

	public void refreshConfigs(boolean init) {
		double hpIncrease = 0;
		for (int index = 0; index < items.getSize(); index++) {
			Item item = items.get(index);
			if (item == null)
				continue;
			int id = item.getId();
			if (index == Equipment.HEAD) {
				if (id == 20135 || id == 20137 // torva
						|| id == 20147 || id == 20149 // pernix
						|| id == 20159 || id == 20161 // virtus
						)
					hpIncrease += 66;

			} else if (index == Equipment.CHEST) {
				if (id == 20139 || id == 20141 // torva
						|| id == 20151 || id == 20153 // pernix
						|| id == 20163 || id == 20165 // virtus
						)
					hpIncrease += 200;
			} else if (index == Equipment.LEGS)
				if (id == 20143 || id == 20145 // torva
				|| id == 20155 || id == 20157 // pernix
				|| id == 20167 || id == 20169 // virtus
						)
					hpIncrease += 134;

		}
		if (player.hasEffect(Effect.BONFIRE)) {
			int maxhp = player.getSkills().getLevel(Constants.HITPOINTS) * 10;
			hpIncrease += (maxhp * Bonfire.getBonfireBoostMultiplier(player)) - maxhp;
		}
        hpIncrease +=player.getI(Skills.SKILL_NAME[Constants.HITPOINTS])*10;
		if (player.getHpBoostMultiplier() != 0) {
			int maxhp = player.getSkills().getLevel(Constants.HITPOINTS) * 10;
			hpIncrease += maxhp * player.getHpBoostMultiplier();
		}
		if (hpIncrease != equipmentHpIncrease) {
			equipmentHpIncrease = (int) hpIncrease;
			if (!init)
				player.refreshHitPoints();
		}
		double w = 0;
		for (Item item : items.getItems()) {
			if (item == null)
				continue;
			w += ItemWeights.getWeight(item, true);
		}
		equipmentWeight = w;
		player.getPackets().refreshWeight(player.getInventory().getInventoryWeight() + equipmentWeight);
	}

	public static boolean hideArms(Item item) {
		return item.getDefinitions().isEquipType(6);
	}

	public static boolean hideHair(Item item) {
		return item.getDefinitions().isEquipType(8);
	}

	public static boolean hideBeard(Item item) {
		return item.getDefinitions().isEquipType(11);
	}

	public static int getItemSlot(int itemId) {
		return ItemDefinitions.getDefs(itemId).getEquipSlot();
	}

	public static boolean isTwoHandedWeapon(Item item) {
		return item.getDefinitions().isEquipType(5);
	}

	public int getWeaponBAS() {
		Item weapon = items.get(3);
		if (weapon == null)
			return 1426;
		return weapon.getDefinitions().getRenderAnimId();
	}

	public boolean hasShield() {
		return items.get(5) != null;
	}

	public String getWeaponName() {
		Item item = items.get(WEAPON);
		if (item == null)
			return "";
		return item.getDefinitions().getName();
	}

	public int getWeaponId() {
		Item item = items.get(WEAPON);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getChestId() {
		Item item = items.get(CHEST);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getHatId() {
		Item item = items.get(HEAD);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getShieldId() {
		Item item = items.get(SHIELD);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getLegsId() {
		Item item = items.get(LEGS);
		if (item == null)
			return -1;
		return item.getId();
	}

	public void removeAmmo(int slot, int amount) {
		if (items.get(slot) == null)
			return;
		items.remove(slot, new Item(items.get(slot).getId(), amount));
		refresh(slot);
		if (items.get(slot) == null) {
			player.sendMessage("That was your last one!");
			player.getAppearance().generateAppearanceData();
		}
	}

	public int getAuraId() {
		Item item = items.get(AURA);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getCapeId() {
		Item item = items.get(CAPE);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getRingId() {
		Item item = items.get(RING);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getAmmoId() {
		Item item = items.get(AMMO);
		if (item == null)
			return -1;
		return item.getId();
	}

	public void deleteItem(int itemId, int amount) {
		Item[] itemsBefore = items.getItemsCopy();
		if (items.contains(new Item(itemId, amount)))
			PluginManager.handle(new ItemEquipEvent(player, new Item(itemId, amount), false));
		items.remove(new Item(itemId, amount));
		refreshItems(itemsBefore);
	}

	public void refreshItems(Item[] itemsBefore) {
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++)
			if (itemsBefore[index] != items.getItems()[index])
				changedSlots[count++] = index;
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
	}

	public int getBootsId() {
		Item item = items.get(FEET);
		if (item == null)
			return -1;
		return item.getId();
	}

	public boolean wearingGloves() {
		return items.get(HANDS) != null;
	}

	public int getGlovesId() {
		Item item = items.get(HANDS);
		if (item == null)
			return -1;
		return item.getId();
	}

	public Item get(int slot) {
		return items.get(slot);
	}

	public void set(int slot, Item item) {
		if (items.get(slot) != null)
			PluginManager.handle(new ItemEquipEvent(player, items.get(slot), false));
		if (item != null)
			PluginManager.handle(new ItemEquipEvent(player, item, true));
		items.set(slot, item);
	}

	public void remove(int slot, Item item) {
		if (items.get(slot) != null)
			PluginManager.handle(new ItemEquipEvent(player, items.get(slot), false));
		items.remove(slot, item);
	}

	public void setNoPluginTrigger(int slot, Item item) {
		items.set(slot, item);
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}

	public Item[] getItemsCopy() {
		return items.getItemsCopy();
	}

	public int getEquipmentHpIncrease() {
		return equipmentHpIncrease;
	}

	public void setEquipmentHpIncrease(int hp) {
		equipmentHpIncrease = hp;
	}

	public boolean wearingArmour() {
		return getItem(HEAD) != null || getItem(CAPE) != null || getItem(NECK) != null || getItem(WEAPON) != null || getItem(CHEST) != null || getItem(SHIELD) != null || getItem(LEGS) != null
				|| getItem(HANDS) != null || getItem(FEET) != null;
	}

	public int getId(int slot) {
		Item item = items.get(slot);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getAmuletId() {
		Item item = items.get(NECK);
		if (item == null)
			return -1;
		return item.getId();
	}

	public boolean hasTwoHandedWeapon() {
		Item weapon = items.get(WEAPON);
		return weapon != null && isTwoHandedWeapon(weapon);
	}

	public boolean wearingBlackMask() {
		if (items.get(HEAD) != null)
			if (ItemDefinitions.getDefs(items.get(HEAD).getId()).getName().toLowerCase().contains("black mask"))
				return true;
		return false;
	}

	public boolean wearingHexcrest() {
		if (items.get(HEAD) != null)
			if (items.get(HEAD).getId() == 15488)
				return true;
		return false;
	}

	public boolean wearingFocusSight() {
		if (items.get(HEAD) != null)
			if (items.get(HEAD).getId() == 15490)
				return true;
		return false;
	}

	public boolean wearingSlayerHelmet() {
		if (items.get(HEAD) != null)
			if (items.get(HEAD).getId() == 15492 || items.get(HEAD).getId() == 15496 || items.get(HEAD).getId() == 15497)
				return true;
		return false;
	}

	public float getWitchDoctorBoost() {
		float boost = 1.0f;
		if (items.get(HEAD) != null)
			if (items.get(HEAD).getId() == 20046)
				boost += 0.01f;
		if (items.get(LEGS) != null)
			if (items.get(LEGS).getId() == 20045)
				boost += 0.02f;
		if (items.get(CHEST) != null)
			if (items.get(CHEST).getId() == 20044)
				boost += 0.02f;
		return boost;
	}

	public boolean isWearingSlayerHelmet() {
		Item helmet = items.get(HEAD);
		if (helmet == null)
			return false;
		if (helmet.getDefinitions().getName().toLowerCase().contains("slayer helmet"))
			return true;
		return false;
	}

	public boolean wearingFullCeremonial() {
		if (items.get(HEAD) != null && items.get(CHEST) != null && items.get(LEGS) != null && items.get(HANDS) != null && items.get(FEET) != null)
			if (items.get(HEAD).getId() == 20125 && items.get(CHEST).getId() == 20127 && items.get(LEGS).getId() == 20129 && items.get(HANDS).getId() == 20131 && items.get(FEET).getId() == 20133)
				return true;
		return false;
	}

	public boolean hasFirecape() {
		if (items.get(CAPE) != null) {
			String name = items.get(CAPE).getDefinitions().getName().toLowerCase();
			if (name.contains("fire cape") || name.contains("tokhaar-kal") || name.contains("completionist cape"))
				return true;
		}
		return false;
	}

	public double getEquipmentWeight() {
		return equipmentWeight;
	}

	public int getSalveAmulet() {
		if (getAmuletId() == 4081)
			return 0;
		if (getAmuletId() == 10588)
			return 1;
		return -1;
	}

	public static ButtonClickHandler handle = new ButtonClickHandler(884) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 4) {
				int weaponId = e.getPlayer().getEquipment().getWeaponId();
				if (e.getPlayer().hasInstantSpecial(weaponId)) {
					e.getPlayer().performInstantSpecial(weaponId);
					return;
				}
				e.getPlayer().getCombatDefinitions().switchUsingSpecialAttack();
			} else if (e.getComponentId() >= 7 && e.getComponentId() <= 10)
				e.getPlayer().getCombatDefinitions().setAttackStyle(e.getComponentId() - 7);
			else if (e.getComponentId() == 11)
				e.getPlayer().getCombatDefinitions().switchAutoRetaliate();
		}
	};

	public static ButtonClickHandler handleEquipmentStatsInterface = new ButtonClickHandler(667, 670) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getInterfaceId() == 667) {
				if (e.getComponentId() == 9) {
					if (e.getSlotId() >= 14)
						return;
					Item item = e.getPlayer().getEquipment().getItem(e.getSlotId());
					if (item == null)
						return;
					if (e.getPacket() == ClientPacket.IF_OP10) {
						e.getPlayer().sendMessage(ItemExamines.getExamine(item));
						if (item.getMetaData("combatCharges") != null)
							e.getPlayer().sendMessage("<col=FF0000>It looks like it will last another " + Utils.ticksToTime(item.getMetaDataI("combatCharges")));
					} else if (e.getPacket() == ClientPacket.IF_OP1) {
						sendRemove(e.getPlayer(), e.getSlotId());
						Equipment.refreshEquipBonuses(e.getPlayer());
					}
				} else if (e.getComponentId() == 46 && e.getPlayer().getTempAttribs().removeB("Banking"))
					e.getPlayer().getBank().open();
			} else if (e.getInterfaceId() == 670)
				if (e.getComponentId() == 0) {
					if (e.getSlotId() >= e.getPlayer().getInventory().getItemsContainerSize())
						return;
					Item item = e.getPlayer().getInventory().getItem(e.getSlotId());
					if (item == null)
						return;
					if (e.getPacket() == ClientPacket.IF_OP1) {
						if (e.getPlayer().isEquipDisabled() || !EnchantedHeadwear.canEquip(item.getId(), e.getPlayer()))
							return;
						if (sendWear(e.getPlayer(), e.getSlotId(), item.getId()))
							Equipment.refreshEquipBonuses(e.getPlayer());
					} else if (e.getPacket() == ClientPacket.IF_OP4)
						e.getPlayer().getInventory().sendExamine(e.getSlotId());
				}
		}
	};

	public static ButtonClickHandler handleEquipmentTabButtons = new ButtonClickHandler(387) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getPlayer().getInterfaceManager().containsInventoryInter())
				return;
			if (e.getComponentId() == 40) {
				if (e.getPlayer().getInterfaceManager().containsScreenInter()) {
					e.getPlayer().sendMessage("Please finish what you're doing before opening the price checker.");
					return;
				}
				e.getPlayer().stopAll();
				PriceChecker.openPriceCheck(e.getPlayer());
				return;
			}
			if (e.getComponentId() == 38) {
				openEquipmentBonuses(e.getPlayer(), false);
				openEquipmentBonuses(e.getPlayer(), false);
				return;
			} else if (e.getComponentId() == 41) {
				e.getPlayer().stopAll();
				ItemsKeptOnDeath.openItemsKeptOnDeath(e.getPlayer());
				return;
			} else if (e.getComponentId() == 42) {
				e.getPlayer().getInterfaceManager().sendInterface(1178);
				return;
			} else if (e.getComponentId() == 43) {
				PriceChecker.openPriceCheck(e.getPlayer());
				//				e.getPlayer().sendMessage("Customizations not finished.");
				return;
			}
			Item item = e.getPlayer().getEquipment().getItem(Equipment.getItemSlot(e.getSlotId2()));
			if ((item == null) || PluginManager.handle(new ItemClickEvent(e.getPlayer(), item, e.getSlotId(), item.getDefinitions().getEquipmentOption(getOptionForPacket(e.getPacket())), true)))
				return;
			if (e.getPacket() == ClientPacket.IF_OP10) {
				e.getPlayer().getEquipment().sendExamine(Equipment.getItemSlot(e.getSlotId2()));
				return;
			}
			if (e.getPacket() == ClientPacket.IF_OP1 && PluginManager.handle(new ItemClickEvent(e.getPlayer(), item, e.getSlotId(), "Remove", true)))
				return;
			if (e.getComponentId() == 12) {
				if (e.getPacket() == ClientPacket.IF_OP2)
					ItemTeleports.sendTeleport(e.getPlayer(), e.getPlayer().getEquipment().getItem(Equipment.getItemSlot(e.getSlotId2())), 0, true);
				else if (e.getPacket() == ClientPacket.IF_OP3)
					ItemTeleports.sendTeleport(e.getPlayer(), e.getPlayer().getEquipment().getItem(Equipment.getItemSlot(e.getSlotId2())), 1, true);
				else if (e.getPacket() == ClientPacket.IF_OP4)
					ItemTeleports.sendTeleport(e.getPlayer(), e.getPlayer().getEquipment().getItem(Equipment.getItemSlot(e.getSlotId2())), 2, true);
				else if (e.getPacket() == ClientPacket.IF_OP5)
					ItemTeleports.sendTeleport(e.getPlayer(), e.getPlayer().getEquipment().getItem(Equipment.getItemSlot(e.getSlotId2())), 3, true);
			} else if (e.getComponentId() == 15) {
				if (e.getPacket() == ClientPacket.IF_OP2) {
					int weaponId = e.getPlayer().getEquipment().getWeaponId();
					if (weaponId == 15484)
						e.getPlayer().getInterfaceManager().gazeOrbOfOculus();
				}
			} else if (e.getComponentId() == 33)
				if (e.getPacket() == ClientPacket.IF_OP2)
					ItemTeleports.sendTeleport(e.getPlayer(), e.getPlayer().getEquipment().getItem(Equipment.getItemSlot(e.getSlotId2())), 0, true);
				else if (e.getPacket() == ClientPacket.IF_OP3)
					ItemTeleports.sendTeleport(e.getPlayer(), e.getPlayer().getEquipment().getItem(Equipment.getItemSlot(e.getSlotId2())), 1, true);
		}
	};

	public static void sendRemove(Player player, int slotId) {
		if (slotId >= 15)
			return;
		player.stopAll(false, false);
		Item item = player.getEquipment().getItem(slotId);
		if (item == null || !player.getInventory().addItem(item))
			return;
		if (item.getId() == 4657)
			player.setNextBodyGlow(new BodyGlow(1, 0, 0, 0, 128));
		player.getEquipment().set(slotId, null);
		player.getEquipment().refresh(slotId);
		player.getAppearance().generateAppearanceData();
		if (Runecrafting.isTiara(item.getId()))
			player.getVars().setVar(491, 0);
		if (slotId == 3)
			player.getCombatDefinitions().drainSpec(0);
	}

	public static boolean sendWear(Player player, int slotId, int itemId) {
		return sendWear(player, slotId, itemId, false);
	}

	public static boolean sendWear(Player player, int slotId, int itemId, boolean overrideWear) {
		if (player.hasFinished() || player.isDead())
			return false;
		player.stopAll(false, false);
		Item item = player.getInventory().getItem(slotId);
		if (item == null || item.getId() != itemId)
			return false;
		if (!overrideWear && (!item.getDefinitions().containsOption("Wear") && !item.getDefinitions().containsOption("Wield")))
			return false;
		if (item.getDefinitions().isNoted() || !item.getDefinitions().isWearItem(player.getAppearance().isMale())) {
			player.sendMessage("You can't wear that.");
			return true;
		}
		int targetSlot = Equipment.getItemSlot(itemId);
		if (targetSlot == -1) {
			player.sendMessage("You can't wear that.");
			return true;
		}
		if (!ItemConstants.canWear(item, player))
			return true;
		boolean isTwoHandedWeapon = targetSlot == 3 && Equipment.isTwoHandedWeapon(item);
		if (isTwoHandedWeapon && !player.getInventory().hasFreeSlots() && player.getEquipment().hasShield()) {
			player.sendMessage("Not enough free space in your inventory.");
			return true;
		}
		if (!player.getControllerManager().canEquip(targetSlot, itemId))
			return false;
		player.stopAll(false, false);
		player.getInventory().deleteItem(slotId, item);
		if (targetSlot == 3) {
			if (isTwoHandedWeapon && player.getEquipment().getItem(5) != null) {
				if (!player.getInventory().addItem(player.getEquipment().getItem(5))) {
					player.getInventory().getItems().set(slotId, item);
					player.getInventory().refresh(slotId);
					return true;
				}
				player.getEquipment().set(5, null);
			}
		} else if (targetSlot == 5)
			if (player.getEquipment().getItem(3) != null && Equipment.isTwoHandedWeapon(player.getEquipment().getItem(3))) {
				if (!player.getInventory().addItem(player.getEquipment().getItem(3))) {
					player.getInventory().getItems().set(slotId, item);
					player.getInventory().refresh(slotId);
					return true;
				}
				player.getEquipment().set(3, null);
			}
		if (player.getEquipment().getItem(targetSlot) != null && (itemId != player.getEquipment().getItem(targetSlot).getId() || !item.getDefinitions().isStackable())) {
			if (player.getInventory().getItems().get(slotId) == null && !item.getDefinitions().isStackable()) {
				player.getInventory().getItems().set(slotId, new Item(player.getEquipment().getItem(targetSlot)));
				player.getInventory().refresh(slotId);
			} else
				player.getInventory().addItem(new Item(player.getEquipment().getItem(targetSlot)));
			player.getEquipment().set(targetSlot, null);
		}
		int oldAmt = 0;
		if (player.getEquipment().getItem(targetSlot) != null)
			oldAmt = player.getEquipment().getItem(targetSlot).getAmount();
		Item item2 = new Item(itemId, oldAmt + item.getAmount(), item.getMetaData());
		player.getEquipment().set(targetSlot, item2);
		player.getEquipment().refresh(targetSlot, targetSlot == 3 ? 5 : targetSlot == 3 ? 0 : 3);
		player.getAppearance().generateAppearanceData();
		player.getPackets().sendVarc(779, player.getAppearance().getRenderEmote());
		player.getPackets().sendSound(2240, 0, 1);
		if (targetSlot == 3)
			player.getCombatDefinitions().drainSpec(0);
		return true;
	}

	private static int getOptionForPacket(ClientPacket packet) {
		switch(packet) {
		case IF_OP2:
			return 0;
		case IF_OP3:
			return 1;
		case IF_OP4:
			return 2;
		case IF_OP5:
			return 3;
		case IF_OP6:
			return 4;
		case IF_OP7:
			return 5;
		case IF_OP8:
			return 6;
		case IF_OP9:
			return 7;
		case IF_OP10:
			return 8;
		default:
			return -1;
		}
	}

	public static void openEquipmentBonuses(final Player player, boolean banking) {
		player.stopAll();
		player.getVars().setVarBit(4894, banking ? 1 : 0);
		player.getVars().setVarBit(8348, 1);
		player.getVars().syncVarsToClient();
		player.getPackets().sendItems(93, player.getInventory().getItems());
		player.getPackets().sendInterSetItemsOptionsScript(670, 0, 93, 4, 7, "Equip", "Compare", "Stats", "Examine");
		player.getPackets().setIFRightClickOps(670, 0, 0, 27, 0, 1, 2, 3);
		player.getPackets().setIFRightClickOps(667, 9, 0, 20, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		refreshEquipBonuses(player);
		player.getInterfaceManager().sendInventoryInterface(670);
		player.getInterfaceManager().sendInterface(667);
		WorldTasks.delay(0, () -> {
			player.getPackets().sendRunScript(2319);
		});
		if (banking) {
			player.getTempAttribs().setB("Banking", true);
			player.setCloseInterfacesEvent(() -> player.getTempAttribs().removeB("Banking"));
		}
	}

	public static void refreshEquipBonuses(Player player) {
		player.getPackets().setIFText(667, 28, "Stab: " + player.getCombatDefinitions().getBonus(Bonus.STAB_ATT));
		player.getPackets().setIFText(667, 29, "Slash: " + player.getCombatDefinitions().getBonus(Bonus.SLASH_ATT));
		player.getPackets().setIFText(667, 30, "Crush: " + player.getCombatDefinitions().getBonus(Bonus.CRUSH_ATT));
		player.getPackets().setIFText(667, 31, "Magic: " + player.getCombatDefinitions().getBonus(Bonus.MAGIC_ATT));
		player.getPackets().setIFText(667, 32, "Range: " + player.getCombatDefinitions().getBonus(Bonus.RANGE_ATT));
		player.getPackets().setIFText(667, 33, "Stab: " + player.getCombatDefinitions().getBonus(Bonus.STAB_DEF));
		player.getPackets().setIFText(667, 34, "Slash: " + player.getCombatDefinitions().getBonus(Bonus.SLASH_DEF));
		player.getPackets().setIFText(667, 35, "Crush: " + player.getCombatDefinitions().getBonus(Bonus.CRUSH_DEF));
		player.getPackets().setIFText(667, 36, "Magic: " + player.getCombatDefinitions().getBonus(Bonus.MAGIC_DEF));
		player.getPackets().setIFText(667, 37, "Range: " + player.getCombatDefinitions().getBonus(Bonus.RANGE_DEF));
		player.getPackets().setIFText(667, 38, "Summoning: " + player.getCombatDefinitions().getBonus(Bonus.SUMM_DEF));
		player.getPackets().setIFText(667, 39, "Absorb Melee: " + player.getCombatDefinitions().getBonus(Bonus.ABSORB_MELEE) + "%");
		player.getPackets().setIFText(667, 40, "Absorb Magic: " + player.getCombatDefinitions().getBonus(Bonus.ABSORB_MAGIC) + "%");
		player.getPackets().setIFText(667, 41, "Absorb Ranged: " + player.getCombatDefinitions().getBonus(Bonus.ABSORB_RANGE) + "%");
		player.getPackets().setIFText(667, 42, "Strength: " + player.getCombatDefinitions().getBonus(Bonus.MELEE_STR));
		player.getPackets().setIFText(667, 43, "Ranged Str: " + player.getCombatDefinitions().getBonus(Bonus.RANGE_STR));
		player.getPackets().setIFText(667, 44, "Prayer: " + player.getCombatDefinitions().getBonus(Bonus.PRAYER));
		player.getPackets().setIFText(667, 45, "Magic Damage: " + player.getCombatDefinitions().getBonus(Bonus.MAGIC_STR) + "%");
	}
}
