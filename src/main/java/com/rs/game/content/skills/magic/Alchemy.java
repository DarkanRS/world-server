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
package com.rs.game.content.skills.magic;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.content.ItemConstants;
import com.rs.game.content.skills.smithing.Smelting.SmeltingBar;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Inventory;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.InterfaceOnInterfaceHandler;

@PluginEventHandler
public class Alchemy {

	public static InterfaceOnInterfaceHandler handleAlchSuperheat = new InterfaceOnInterfaceHandler(192, new int[] { 38, 50, 59 }, Inventory.INVENTORY_INTERFACE, null, e -> {
		Item item = e.getPlayer().getInventory().getItem(e.getToSlotId());
		if (item == null)
			return;
		switch(e.getFromComponentId()) {
			case 38 -> handleAlchemy(e.getPlayer(), item, true, true);
			case 50 -> handleSuperheat(e.getPlayer(), item, true);
			case 59 -> handleAlchemy(e.getPlayer(), item, false, true);
		}
	});

	public static boolean handleSuperheat(Player player, Item item, boolean useRunes) {
		if (useRunes)
			player.getInterfaceManager().openTab(Sub.TAB_MAGIC);
		if (!player.canCastSpell() || !Magic.checkMagicAndRunes(player, 43, false, useRunes ? new RuneSet(Rune.NATURE, 1, Rune.FIRE, 4) : null))
			return false;
		player.stopAll(false, false, true);
		SmeltingBar bar = SmeltingBar.forOre(player, item.getId());
		if (bar == null) {
			player.sendMessage("You can only cast this spell on an ore.");
			return false;
		}

		if (!player.getInventory().containsItem(bar.getItemsRequired()[0].getId(), bar.getItemsRequired()[0].getAmount())) {
			player.simpleDialogue("You need " + bar.getItemsRequired()[0].getDefinitions().getName() + " to create a " + bar.getProducedBar().getDefinitions().getName() + ".");
			return false;
		}
		if (bar.getItemsRequired().length > 1)
			if (!player.getInventory().containsItem(bar.getItemsRequired()[1].getId(), bar.getItemsRequired()[1].getAmount()) && !(bar.getItemsRequired()[1].getId() == 453 && (player.getI("coalBag")+player.getInventory().getAmountOf(453)) >= bar.getItemsRequired()[1].getAmount())) {
				player.simpleDialogue("You need " + bar.getItemsRequired()[1].getDefinitions().getName() + " to create a " + bar.getProducedBar().getDefinitions().getName() + ".");
				return false;
			}
		if (player.getSkills().getLevel(Constants.SMITHING) < bar.getLevelRequired()) {
			player.simpleDialogue("You need a Smithing level of at least " + bar.getLevelRequired() + " to smelt " + bar.getProducedBar().getDefinitions().getName());
			return false;
		}
		Magic.checkMagicAndRunes(player, 43, true, useRunes ? new RuneSet(Rune.NATURE, 1, Rune.FIRE, 4) : null);
		player.anim(725);
		player.spotAnim(148, 0, 96);
		for (Item itemReq : bar.getItemsRequired())
			if (itemReq.getId() == 453 && player.getInventory().containsItem(18339) && player.getI("coalBag") > 0) {
				int coalBag = player.getI("coalBag");
				if (coalBag > itemReq.getAmount())
					player.save("coalBag", coalBag - itemReq.getAmount());
				else {
					player.save("coalBag", 0);
					player.getInventory().deleteItem(453, itemReq.getAmount()-coalBag);
				}
			} else
				player.getInventory().deleteItem(itemReq.getId(), itemReq.getAmount());
		player.getInventory().addItem(bar.getProducedBar().getId(), 1);
		player.getSkills().addXp(Constants.MAGIC, 53);
		if (bar == SmeltingBar.GOLD && player.getEquipment().getGlovesId() == 776)
			player.getSkills().addXp(Constants.SMITHING, 56.2);
		else
			player.getSkills().addXp(Constants.SMITHING, bar.getExperience());
		player.addSpellDelay(2);
		return true;
	}

	public static boolean handleAlchemy(Player player, Item item, boolean lowAlch, boolean useRunes) {
		if (useRunes)
			player.getInterfaceManager().openTab(Sub.TAB_MAGIC);
		if (!player.canCastSpell())
			return false;
		if ((!ItemConstants.isTradeable(item)) || item.getId() == 995) {
			player.sendMessage("You can't cast alchemy on that!");
			return false;
		}
		if (lowAlch) {
			if (!Magic.checkMagicAndRunes(player, 21, true, useRunes ? new RuneSet(Rune.NATURE, 1, Rune.FIRE, 3) : null))
				return false;
		} else if (!Magic.checkMagicAndRunes(player, 55, true, useRunes ? new RuneSet(Rune.NATURE, 1, Rune.FIRE, 5) : null))
			return false;
		player.stopAll(false, false, true);
		ItemDefinitions def = ItemDefinitions.getDefs(item.getId());
		if (def == null)
			return false;
		ItemDefinitions wDef = null;
		if (player.getEquipment().getItem(Equipment.WEAPON) != null)
			wDef = ItemDefinitions.getDefs(player.getEquipment().getWeaponId());
		player.incrementCount("Items alched");
		if (lowAlch) {
			if (wDef != null && wDef.getName().toLowerCase().contains("staff")) {
				player.setNextAnimation(new Animation(9625));
				player.setNextSpotAnim(new SpotAnim(1692));
			} else {
				player.setNextAnimation(new Animation(712));
				player.setNextSpotAnim(new SpotAnim(112));

			}
			World.soundEffect(player.getTile(), 98);
			player.getInventory().deleteItem(item.getId(), 1);
			player.getInventory().addCoins(def.getSellPrice());
			player.getSkills().addXp(Constants.MAGIC, 31);
			player.addSpellDelay(2);
		} else {
			if (wDef != null && wDef.getName().toLowerCase().contains("staff")) {
				player.setNextAnimation(new Animation(9633));
				player.setNextSpotAnim(new SpotAnim(1693));
			} else {
				player.setNextAnimation(new Animation(713));
				player.setNextSpotAnim(new SpotAnim(113));
			}
			World.soundEffect(player.getTile(), 98); //low alch id... high alch doesnt match osrs... reeeee
			player.getInventory().deleteItem(item.getId(), 1);
			player.getInventory().addCoins(def.getHighAlchPrice());
			player.getSkills().addXp(Constants.MAGIC, 65);
			player.addSpellDelay(4);
		}
		return true;
	}

}
