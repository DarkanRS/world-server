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
package com.rs.game.content.skills.summoning;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.ItemConstants;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemEquipHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@PluginEventHandler
public class EnchantedHeadwear {

	public enum Headwear {
		ADAMANT_FULL_HELM (1161, 12658, 12659, 1, 50),
		RUNE_FULL_HELM    (1163, 12664, 12665, 30, 60),
		DRAGON_HELM       (6967, 12666, 12667, 50, 110),
		BERSERKER_HELM    (3751, 12674, 12675, 30, 70),
		WARRIOR_HELM      (3753, 12676,12677, 30, 70),
		HELM_OF_NEITIZNOT (10828, 12680, 12681, 45, 90),

		SNAKESKIN_BANDANA (6326, 12660, 12661, 20, 50),
		ARCHER_HELM       (3749, 12672, 12673, 30, 70),
		ARMADYL_HELMET    (11718, 12670, 12671, 60, 120),

		SPLITBARK_HELM (3385, 12662, 12663, 30, 50),
		FARSEER_HELM   (3755, 12678, 12679, 30, 70),
		LUNAR_HELM     (10609, 12668, 12669, 55, 110),

		SLAYER_HELMET             (13263, 14636, 14637, 20, 50),
		FULL_SLAYER_HELMET        (15492, 15496, 15497, 20, 50),
		RED_FULL_SLAYER_HELMET    (22528, 22530, 22532, 20, 50),
		BLUE_FULL_SLAYER_HELMET   (22534, 22536, 22538, 20, 50),
		GREEN_FULL_SLAYER_HELMET  (22540, 22542, 22544, 20, 50),
		YELLOW_FULL_SLAYER_HELMET (22546, 22548, 22550, 20, 50),

		BLUE_FEATHER_HEADDRESS   (12210, 12210, 12212, 50, 40),
		YELLOW_FEATHER_HEADDRESS (12213, 12213, 12215, 50, 40),
		RED_FEATHER_HEADDRESS    (12216, 12216, 12218, 50, 40),
		STRIPY_FEATHER_HEADDRESS (12219, 12219, 12221, 50, 40),
		ORANGE_FEATHER_HEADDRESS (12222, 12222, 12224, 50, 40),
		ANTLERS                  (12204, 12204, 12206, 10, 40),
		LIZARD_SKULL             (12207, 12207, 12209, 30, 65);
		
		private static Map<Integer, Headwear> MAP = new HashMap<>();
		
		static {
			for (Headwear h : Headwear.values()) {
				MAP.put(h.baseId, h);
				MAP.put(h.chargedId, h);
				MAP.put(h.enchantedId, h);
			}
		}

		int baseId;
		int enchantedId;
		int chargedId;
		int summReq;
		int scrollLimit;

		Headwear(int id, int enchantedId, int chargedId, int summoningLevel, int scrolls) {
			this.baseId = id;
			this.enchantedId = enchantedId;
			this.chargedId = chargedId;
			this.summReq = summoningLevel;
			this.scrollLimit = scrolls;
		}

		public static Headwear forId(int id) {
			return MAP.get(id);
		}
	}

	public static ItemOnNPCHandler headwearOnPikkupstix = new ItemOnNPCHandler(6988, e -> {
		Headwear helm = Headwear.forId(e.getItem().getId());
		if (helm != null) {
			if (e.getPlayer().getSkills().getLevelForXp(Constants.SUMMONING) < helm.summReq) {
				e.getPlayer().sendMessage("You must have a summoning level of " + helm.summReq + " to enchant this.");
				return;
			}

			if (e.getItem().getId() == helm.baseId) {
				e.getItem().setId(helm.enchantedId);
				e.getPlayer().getInventory().refresh(e.getItem().getSlot());
				e.getPlayer().sendMessage("Pikkupstix magically enchants your headwear.");
				return;
			}
			if (e.getItem().getId() == helm.enchantedId) {
				e.getItem().setId(helm.baseId);
				e.getPlayer().getInventory().refresh(e.getItem().getSlot());
				e.getPlayer().sendMessage("Pikkupstix removes the enchantment from your headwear.");
				return;
			}
			if (e.getItem().getId() == helm.chargedId) {
				e.getPlayer().sendMessage("You need to remove your scrolls before unenchanting the helmet.");
				return;
			}
		}
	});
	
	public static ItemEquipHandler canEquipCharged = new ItemEquipHandler(Arrays.stream(Headwear.values()).map(h -> h.chargedId).toArray(), e -> {
		if (e.equip() && !ItemConstants.canWear(new Item(Headwear.forId(e.getItem().getId()).baseId, 1), e.getPlayer()))
			e.cancel();
	});
	
	public static ItemEquipHandler canEquipEnchanted = new ItemEquipHandler(Arrays.stream(Headwear.values()).map(h -> h.enchantedId).toArray(), e -> {
		if (e.equip() && !ItemConstants.canWear(new Item(Headwear.forId(e.getItem().getId()).baseId, 1), e.getPlayer()))
			e.cancel();
	});

//	public static NPCClickHandler pikkupstixEnchanting = new NPCClickHandler(new Object[] { 6988 }, e -> {
//		if (e.getOpNum() == 1)
//			e.getPlayer().startConversation(new GenericSkillcapeOwnerD(e.getPlayer(), 6988, Skillcapes.Summoning));
//		else if (e.getOpNum() == 3)
//			ShopsHandler.openShop(e.getPlayer(), "taverly_summoning_shop");
//		else if (e.getOpNum() == 4) {
//			if (e.getPlayer().getInventory().getFreeSlots() < 28) {
//				for (Item i : e.getPlayer().getInventory().getItems().array()) {
//					if ((i != null) && (Headwear.forId(i.getId()) != null)) {
//						e.getPlayer().startConversation(new Dialogue().addSimple("That is a fine piece of headwear you have there. If you give me a closer look, I may be able to enchant it."));
//						return;
//					}
//				}
//			}
//			e.getPlayer().startConversation(new Dialogue().addSimple("If you bring me the right headwear, I may be able to assist in enchanting it."));
//		}
//	});
	
	public static ItemOnItemHandler chargeUncharged = new ItemOnItemHandler(Arrays.stream(Headwear.values()).mapToInt(h -> h.enchantedId).toArray(), Arrays.stream(Scroll.values()).mapToInt(s -> s.getId()).toArray(), e -> {
		Headwear wear = Headwear.forId(e.getItem1().getId());
		if (wear == null)
			wear = Headwear.forId(e.getItem2().getId());
		Scroll s = Scroll.forId(e.getItem1().getId());
		if (s == null)
			s = Scroll.forId(e.getItem2().getId());
		final Scroll scroll = s;
		if (wear == null) {
			e.getPlayer().sendMessage("You cannot charge that item with scrolls.");
			return;
		}
		if (scroll == null) {
			e.getPlayer().sendMessage("You don't have any valid scrolls.");
			return;
		}
		e.getPlayer().sendInputInteger("How many would you like to charge the helmet with?", num -> addScrolls(e.getPlayer(), e.getUsedWith(scroll.getId()), scroll, num));
	});
	
	public static ItemClickHandler chargedOps = new ItemClickHandler(Arrays.stream(Headwear.values()).map(h -> h.chargedId).toArray(), new String[] { "Commune", "Uncharge" }, e -> {
		if (e.getOption().equals("Uncharge"))
			removeScrolls(e.getPlayer(), e.getItem());
		else if (e.getOption().equals("Commune"))
			e.getPlayer().sendMessage("You have " + e.getItem().getMetaDataI("summScrollsStored") + " " + ItemDefinitions.getDefs(e.getItem().getMetaDataI("summScrollId")).name + " stored in this helmet.");
	});
	
	public static void addScrolls(Player player, Item item, Scroll scroll, int num) {
		Headwear headwear = Headwear.forId(item.getId());
		if (headwear == null)
			return;
		if (item.getMetaDataI("summScrollId") != scroll.getId() && item.getMetaDataI("summScrollsStored") > 0) {
			player.sendMessage("You already have " + item.getMetaDataI("summScrollsStored") + " " + ItemDefinitions.getDefs(item.getMetaDataI("summScrollId")).name + " stored in this helmet.");
			return;
		}
		if (!player.getInventory().containsItem(scroll.getId(), num))
			num = player.getInventory().getAmountOf(scroll.getId());
		if (num <= 0)
			return;
		player.getInventory().deleteItem(scroll.getId(), num);
		item.addMetaData("summScrollId", scroll.getId());
		item.addMetaData("summScrollsStored", num);
		item.setId(headwear.chargedId);
		player.getInventory().refresh(item.getSlot());
	}
	
	public static void removeScrolls(Player player, Item item) {
		Headwear wear = Headwear.forId(item.getId());
		if (item.getMetaDataI("summScrollId", -1) == -1 || item.getMetaDataI("summScrollsStored", -1) <= 0) {
			player.sendMessage("There's nothing to remove from this helmet.");
			if (wear != null) {
				item.setId(wear.enchantedId);
				player.getInventory().refresh(item.getSlot());
			}
			return;
		}
		player.getInventory().addItemDrop(item.getMetaDataI("summScrollId", -1), item.getMetaDataI("summScrollsStored", -1));
		item.deleteMetaData();
		if (wear != null) {
			item.setId(wear.enchantedId);
			player.getInventory().refresh(item.getSlot());
		}
	}
}
