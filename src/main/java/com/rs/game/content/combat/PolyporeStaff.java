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
package com.rs.game.content.combat;

import com.rs.engine.dialogue.Conversation;
import com.rs.game.content.skills.magic.Rune;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;

@PluginEventHandler
public class PolyporeStaff {

	public static ItemClickHandler handleOptions = new ItemClickHandler(new Object[] { 22494, 22496, 22497 }, new String[] { "Check", "Clean" }, e -> {
		switch(e.getOption()) {
		case "Check":
			int charges = e.getItem().getMetaDataI("polyporeCasts");
			if (charges == -1)
				e.getPlayer().sendMessage("It looks like its got about 3000 casts left.");
			else
				e.getPlayer().sendMessage("It looks like its got about " + charges + " casts left.");
			break;
		case "Clean":
			e.getPlayer().startConversation(new Conversation(e.getPlayer())
					.addSimple("WARNING: You will only be able to recover half of the spores and runes that went into charging the staff.")
					.addOption("Do you want to clean the staff?", "Yes, please.", "No, thanks.")
					.addSimple("You clean the staff, recovering half of the spores and runes.", () -> {
						Item staff = e.getPlayer().getInventory().getItem(e.getSlotId());
						if (staff != null) {
							int recov = 0;
							if (staff.getId() == 22494)
								recov = 3000;
							else
								recov = e.getItem().getMetaDataI("polyporeCasts");
							if (recov > 0) {
								staff.setId(22498);
								e.getPlayer().getInventory().addItemDrop(Rune.FIRE.id(), (recov * 5) / 2);
								e.getPlayer().getInventory().addItemDrop(22448, recov / 2);
								e.getPlayer().getInventory().refresh(e.getSlotId());
							}
						}
					}));
			break;
		}
	});

	public static ItemOnItemHandler handleCreate = new ItemOnItemHandler(22448, new int[] { 22496, 22497, 22498 }, e -> {
		Item stick = e.getUsedWith(22448);
		if (stick.getId() == 22498) {
			boolean canMake = true;
			if (!e.getPlayer().getInventory().containsItem(22448, 3000)) {
				e.getPlayer().sendMessage("You need 3,000 polypore spores to create a polypore staff.");
				canMake = false;
			}
			if (!e.getPlayer().getInventory().containsItem(Rune.FIRE.id(), 15000)) {
				e.getPlayer().sendMessage("You need 15,000 fire runes to create a polypore staff.");
				canMake = false;
			}
			if (!e.getPlayer().getInventory().containsItem(Rune.FIRE.id(), 3000)) {
				e.getPlayer().sendMessage("You need 3,000 chaos runes to create a polypore staff.");
				canMake = false;
			}
			if (!canMake)
				return;
			e.getPlayer().startConversation(new Conversation(e.getPlayer())
					.addSimple("To create a polypore staff you will need 3,000 polypore spores, 3,000 chaos runes and 15,000 fire runes.")
					.addOption("Are you sure you want to create the staff?", "Yes, please.", "No, thanks.")
					.addItem(22494, "You plant the spores on the stick and they quickly grow with the power of the fire runes.", () -> {
						Item staff = e.getPlayer().getInventory().getItem(stick.getSlot());
						if (staff != null) {
							e.getPlayer().setNextAnimation(new Animation(15434));
							e.getPlayer().setNextSpotAnim(new SpotAnim(2032));
							staff.setId(22494);
							e.getPlayer().getInventory().deleteItem(Rune.FIRE.id(), 15000);
							e.getPlayer().getInventory().deleteItem(Rune.CHAOS.id(), 3000);
							e.getPlayer().getInventory().deleteItem(22448, 3000);
							e.getPlayer().getInventory().refresh(stick.getSlot());
							e.getPlayer().getSkills().addXp(Constants.FARMING, 300);
						}
					}));
		} else {
			int charges = stick.getMetaDataI("polyporeCasts");
			if (charges == -1 || charges >= 3000) {
				e.getPlayer().sendMessage("This polypore staff is already full.");
				return;
			}
			int canRecharge = 3000 - charges;
			int maxRecharge = getMaxCharges(e.getPlayer(), canRecharge);
			int newCharges = charges + maxRecharge;
			if (maxRecharge <= 0) {
				e.getPlayer().sendMessage("You need 1 polypore spore, 1 chaos rune, and 5 fire runes per charge.");
				return;
			}
			e.getPlayer().setNextAnimation(new Animation(15434));
			e.getPlayer().setNextSpotAnim(new SpotAnim(2032));
			if (newCharges == 3000) {
				stick.setId(22494);
				stick.deleteMetaData();
				e.getPlayer().getInventory().refresh(stick.getSlot());
			} else
				stick.addMetaData("polyporeCasts", newCharges);
			e.getPlayer().getInventory().deleteItem(Rune.FIRE.id(), maxRecharge * 5);
			e.getPlayer().getInventory().deleteItem(Rune.CHAOS.id(), maxRecharge);
			e.getPlayer().getInventory().deleteItem(22448, maxRecharge);
			e.getPlayer().getSkills().addXp(Constants.FARMING, maxRecharge * 0.1);
			e.getPlayer().sendMessage("You charge the staff with " + maxRecharge + " charges. It now has " + newCharges);
		}
	});
	
	public static int getMaxCharges(Player player, int max) {
		int numSpores = player.getInventory().getNumberOf(22448);
		int numFires = player.getInventory().getNumberOf(Rune.FIRE.id());
		int numChaos = player.getInventory().getNumberOf(Rune.CHAOS.id());
		if (numSpores < max)
			max = numSpores;
		if (numChaos < max)
			max = numChaos;
		if (((int) (numFires/5.0)) < max)
			max = ((int) (numFires/5.0));
		return max;
	}

	public static boolean isWielding(Player player) {
		int weaponId = player.getEquipment().getWeaponId();
		return weaponId == 22494 || weaponId == 22496 || weaponId == 22497;
	}

	public static void drainCharge(Player player) {
		Item staff = player.getEquipment().getItem(Equipment.WEAPON);
		if (staff == null)
			return;
		if (staff.getId() == 22494) {
			staff.setId(22496);
			staff.addMetaData("polyporeCasts", 2999);
			player.getEquipment().refresh(Equipment.WEAPON);
		} else if (staff.getId() == 22496) {
			int charges = staff.getMetaDataI("polyporeCasts");
			if (charges <= 1) {
				staff.deleteMetaData();
				staff.setId(22498);
				player.getEquipment().refresh(Equipment.WEAPON);
				player.getAppearance().generateAppearanceData();
				player.sendMessage("<col=FF0000>Your polypore staff has degraded back into a polypore stick!");
			} else
				staff.addMetaData("polyporeCasts", charges-1);
		}
	}

}
