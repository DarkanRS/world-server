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
package com.rs.game.player.content;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.impl.skillmasters.GenericSkillcapeOwnerD;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnNPCEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class EnchantedHeadwear {

	public enum Headwear {
		ADAMANT_FULL_HELM (1161, 12658, 12659, 30, 1, 1, 1, 50),
		RUNE_FULL_HELM    (1163, 12664, 12665, 40, 1, 1, 30, 60),
		DRAGON_HELM       (6967, 12666, 12667, 60, 1, 1, 50, 110),
		BERSERKER_HELM    (3751, 12674, 12675, 45, 1, 1, 30, 70),
		WARRIOR_HELM      (3753, 12676,12677, 45, 1, 1, 30, 70),
		HELM_OF_NEITIZNOT (10828, 12680, 12681, 55, 1, 1, 45, 90),

		SNAKESKIN_BANDANA (6326, 12660, 12661, 30, 30, 1, 20, 50),
		ARCHER_HELM       (3749, 12672, 12673, 45, 45, 1, 30, 70),
		ARMADYL_HELMET    (11718, 12670, 12671, 70, 70, 1, 60, 120),

		SPLITBARK_HELM (3385, 12662, 12663, 40, 1, 40, 30, 50),
		FARSEER_HELM   (3755, 12678, 12679, 45, 1, 45, 30, 70),
		LUNAR_HELM     (10609, 12668, 12669, 40, 1, 60, 55, 110),

		SLAYER_HELMET             (13263, 14636, 14637, 10, 20, 20, 20, 50),
		FULL_SLAYER_HELMET        (15492, 15496, 15497, 10, 20, 20, 20, 50),
		RED_FULL_SLAYER_HELMET    (22528, 22530, 22532, 10, 20, 20, 20, 50),
		BLUE_FULL_SLAYER_HELMET   (22534, 22536, 22538, 10, 20, 20, 20, 50),
		GREEN_FULL_SLAYER_HELMET  (22540, 22542, 22544, 10, 20, 20, 20, 50),
		YELLOW_FULL_SLAYER_HELMET (22546, 22548, 22550, 10, 20, 20, 20, 50),

		BLUE_FEATHER_HEADDRESS   (12210, 12210, 12212, 1, 20, 1, 50, 40),
		YELLOW_FEATHER_HEADDRESS (12213, 12213, 12215, 1, 20, 1, 50, 40),
		RED_FEATHER_HEADDRESS    (12216, 12216, 12218, 1, 20, 1, 50, 40),
		STRIPY_FEATHER_HEADDRESS (12219, 12219, 12221, 1, 20, 1, 50, 40),
		ORANGE_FEATHER_HEADDRESS (12222, 12222, 12224, 1, 20, 1, 50, 40),
		ANTLERS                  (12204, 12204, 12206, 1, 1, 1, 10, 40),
		LIZARD_SKULL             (12207, 12207, 12209, 1, 1, 1, 30, 65);

		int STANDARD_HELMET;
		int ENCHANTED_HELMET;
		int CHARGED_HELMET;
		int DEFENCE_REQUIREMENT;
		int RANGED_REQUIREMENT;
		int MAGIC_REQUIREMENT;
		int SUMMONING_REQUIREMENT;
		int SCROLL_LIMIT;

		Headwear (int id, int enchantedId, int chargedId, int defenceLevel, int rangeLevel, int magicLevel, int summoningLevel, int scrolls) {
			STANDARD_HELMET = id;
			ENCHANTED_HELMET = enchantedId;
			CHARGED_HELMET = chargedId;
			DEFENCE_REQUIREMENT = defenceLevel;
			RANGED_REQUIREMENT = rangeLevel;
			MAGIC_REQUIREMENT = magicLevel;
			SUMMONING_REQUIREMENT = summoningLevel;
			SCROLL_LIMIT = scrolls;
		}

		public static Headwear getHeadwear(int id) {
			for (Headwear helm : Headwear.values())
				if (helm.STANDARD_HELMET == id || helm.ENCHANTED_HELMET == id || helm.CHARGED_HELMET == id)
					return helm;
			return null;
		}
	}

	public static ItemOnNPCHandler headwearOnPikkupstix = new ItemOnNPCHandler(6988) {
		@Override
		public void handle(ItemOnNPCEvent e) {
			Headwear helm = Headwear.getHeadwear(e.getItem().getId());
			if (helm != null) {
				System.out.println(helm.STANDARD_HELMET);
				if (helm.STANDARD_HELMET == helm.ENCHANTED_HELMET) {
					e.getPlayer().sendMessage("This headwear is already resonating with familiar magic. There is no need to enchant it.");
					return;
				}

				if (e.getPlayer().getSkills().getLevel(Constants.SUMMONING) >= helm.SUMMONING_REQUIREMENT) {
					if (e.getItem().getId() == helm.STANDARD_HELMET) {
						e.getPlayer().getInventory().deleteItem(e.getItem().getSlot(), e.getItem());
						e.getPlayer().getInventory().addItem(helm.ENCHANTED_HELMET, 1);
						e.getPlayer().sendMessage("Pikkupstix magically enchants your headwear.");
						return;
					}

					if (e.getItem().getId() == helm.ENCHANTED_HELMET) {
						e.getPlayer().getInventory().deleteItem(e.getItem().getSlot(), e.getItem());
						e.getPlayer().getInventory().addItem(helm.STANDARD_HELMET, 1);
						e.getPlayer().sendMessage("Pikkupstix removes the enchantment from your headwear.");
						return;
					}
				} else
					e.getPlayer().sendMessage("You must have a summoning level of " + helm.SUMMONING_REQUIREMENT + " to enchant this.");
			}
		}
	};

	public static NPCClickHandler pikkupstixEnchanting = new NPCClickHandler(6988) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getOpNum() == 1)
				e.getPlayer().startConversation(new GenericSkillcapeOwnerD(e.getPlayer(), 6988, Skillcapes.Summoning));
			else if (e.getOpNum() == 3)
				ShopsHandler.openShop(e.getPlayer(), "taverly_summoning_shop");
			else if (e.getOpNum() == 4) {
				if (e.getPlayer().getInventory().getFreeSlots() < 28)
					for (Item i : e.getPlayer().getInventory().getItems().getItems())
						if ((null != i) && (null != Headwear.getHeadwear(i.getId()))) {
							e.getPlayer().startConversation(new Dialogue().addSimple("That is a fine piece of headwear you have there. If you give me a closer look, I may be able to enchant it."));
							return;
						}
				e.getPlayer().startConversation(new Dialogue().addSimple("If you bring me the right headwear, I may be able to assist in enchanting it."));

			}
		}
	};

	//	@ItemEquipHandler ( ids = { 12658, 12659, 12664, 12665, 12666, 12667, 12674, 12675, 12676,12677, 12680, 12681, 12660, 12661, 12672, 12673, 12670, 12671, 12662, 12663,
	//			12678, 12679, 12668, 12669, 14636, 14637, 15496, 15497, 22530, 22532, 22536, 22538, 22542, 22544, 22548, 22550, 12210, 12212, 12213, 12215, 12216, 12218, 12219,
	//			12221, 12222, 12224, 12204, 12206, 12207, 12209 })
	public static boolean canEquip(int itemId, Player player) {
		Headwear helm =  Headwear.getHeadwear(itemId);
		if ((null != helm) && (helm.STANDARD_HELMET != itemId)) {
			boolean meetsRequirements = true;
			if (player.getSkills().getLevelForXp(Constants.DEFENSE) < helm.DEFENCE_REQUIREMENT) {
				if (meetsRequirements)
					player.sendMessage("You are not high enough level to use this item.");
				meetsRequirements = false;
				player.sendMessage("You need to have a Defence level of " + helm.DEFENCE_REQUIREMENT + " to equip this.");
			}
			if (player.getSkills().getLevelForXp(Constants.RANGE) < helm.RANGED_REQUIREMENT) {
				if (meetsRequirements)
					player.sendMessage("You are not high enough level to use this item.");
				meetsRequirements = false;
				player.sendMessage("You need to have a Range level of " + helm.RANGED_REQUIREMENT + " to equip this.");
			}
			if (player.getSkills().getLevelForXp(Constants.MAGIC) < helm.MAGIC_REQUIREMENT) {
				if (meetsRequirements)
					player.sendMessage("You are not high enough level to use this item.");
				meetsRequirements = false;
				player.sendMessage("You need to have a Magic level of " + helm.MAGIC_REQUIREMENT + " to equip this.");

			}
			if (player.getSkills().getLevelForXp(Constants.SUMMONING) < helm.SUMMONING_REQUIREMENT) {
				if (meetsRequirements)
					player.sendMessage("You are not high enough level to use this item.");
				meetsRequirements = false;
				player.sendMessage("You need to have a Summoning level of " + helm.SUMMONING_REQUIREMENT + " to equip this.");
			}

			if (!meetsRequirements)
				return false;
		}
		return true;
	}
}
