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
package com.rs.game.content.skills.herblore;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnItemEvent;
import com.rs.plugin.handlers.ItemOnItemHandler;

@PluginEventHandler
public class FlaskPotions {
	static final int EMPTY_FLASK= 23191;
	static final int EMPTY_POTION=229;

	static final int[] POTIONS = new int[Flasks.values().length * 4];
	static {
		initializePotions();
	}

	/**
	 * In order of the Herblore skill menu
	 */
	private enum Flasks {//[FLASK], [DOSES {1,2,3,4}]
		SERUM207(23549 , new int[] {3414, 3412, 3410, 3408}),
		SERUM208(23603 , new int[] { 3419, 3418, 3417, 3416}),

		ATTACK(23195 , new int[] { 125, 123, 121, 2428}),
		ANTI_POISON(23315 , new int[] { 179, 177, 175, 2446}),
		RELICYM_BALM(23537 , new int[] { 4848, 4846, 4844, 4842}),
		STRENGTH(23207 , new int[] { 119, 117, 115, 113}),
		RESTORE(23219 , new int[] { 131, 129, 127, 2430}),
		GUTHIX_BALANCE(23555 , new int[] { 7666, 7664, 7662, 7660}),
		ENERGY(23375 , new int[] { 3014, 3012, 3010, 3008}),
		DEFENCE(23231 , new int[] { 137, 135, 133, 2432}),
		AGILITY(23411 , new int[] { 3038, 3036, 3034, 3032 }),
		COMBAT(23447 , new int[] { 9745, 9743, 9741, 9739}),
		PRAYER(23243 , new int[] { 143, 141, 139, 2434}),
		SUMMONING(23621 , new int[] { 12146, 12144, 12142, 12140}),
		CRAFTING(23459 , new int[] { 14844, 14842, 14840, 14838}),
		SUPER_ATTACK(23255 , new int[] { 149, 147, 145, 2436}),
		SUPER_ANTI_POISON(23327 , new int[] { 185, 183, 181, 2448}),
		FISHING(23267 , new int[] { 155, 153, 151, 2438}),
		SUPER_ENERGY(23387 , new int[] { 3022, 3020, 3018, 3016}),
		HUNTER(23435 , new int[] { 10004, 10002, 10000, 9998}),
		JUJU_HUNTER(23161 , new int[] { 20026, 20025, 20024, 20023}),
		SUPER_STRENGTH(23279 , new int[] { 161, 159, 157, 2440}),
		MAGIC_ESSENCE(23633 , new int[] { 9024, 9023, 9022, 9021}),
		FLETCHING(23471 , new int[] { 14852, 14850, 14848, 14846}),
		JUJU_SCENTLESS(23167 , new int[] { 20030, 20029, 20028, 20027}),
		WEAPON_POISON(25509 , new int[] { 25491, 25489, 25487, 25485}),
		SUPER_RESTORE(23399 , new int[] { 3030, 3028, 3026, 3024}),
		JUJU_FARMING(23143 , new int[] { 20014, 20013, 20012, 20011}),
		SANFEW_SERUM(23567 , new int[] { 10931, 10929, 10927, 10925}),
		SUPER_DEFENCE(23291 , new int[] { 167, 165, 163, 2442}),
		JUJU_COOKING(23137 , new int[] { 20010, 20009, 20008, 20007}),
		ANTI_POISON_PLUS(23579 , new int[] { 5949, 5947, 5945, 5943}),
		ANTI_FIRE(23363 , new int[] { 2458, 2456, 2454, 2452}),
		JUJU_FISHING(23155 , new int[] { 20022, 20021, 20020, 20019}),
		JUJU_WOOD_CUTTING(23149 , new int[] { 20018, 20017, 20016, 20015}),
		RANGING(23303 , new int[] { 173, 171, 169, 2444}),
		WEAPON_POISON_PLUS(25521 , new int[] { 25499, 25497, 25495, 25493}),
		JUJU_MINING(23131 , new int[] { 20006, 20005, 20004, 20003}),
		SARADOMIN_BLESSING(23173 , new int[] { 20034, 20033, 20032, 20031}),
		GUTHIX_GIFT(23179 , new int[] { 20038, 20037, 20036, 20035}),
		ZAMORAK_FAVOUR(23185 , new int[] { 20042, 20041, 20040, 20039}),
		MAGIC(23423 , new int[] { 3046, 3044, 3042, 3040}),
		ZAMORAK_BREW(23339, new int[] { 193, 191, 189, 2450}),
		ANTI_POISON_PLUS_PLUS(3591 , new int[] { 5958, 5956, 5954, 5952}),
		SARA_BREW(23351, new int[] {6691, 6689, 6687, 6685}),
		WEAPON_POISON_PLUS_PLUS(25533 , new int[] { 25507, 25505, 25503, 25501}),
		RECOVERY_SPECIAL(23483 , new int[] { 15303, 15302, 15301, 15300}),
		SUPER_ANTI_FIRE(23489 , new int[] { 15307, 15306, 15305, 15304}),
		EXTREME_ATTACK(23495 , new int[] { 15311, 15310, 15309, 15308}),
		EXTREME_STRENGTH(23501 , new int[] { 15315, 15314, 15313, 15312}),
		EXTREME_DEFENCE(23507 , new int[] { 15319, 15318, 15317, 15316}),
		EXTREME_MAGIC(23513 , new int[] { 15323, 15322, 15321, 15320}),
		EXTREME_RANGING(23519 , new int[] { 15327, 15326, 15325, 15324}),
		SUPER_PRAYER(23525 , new int[] { 15331, 15330, 15329, 15328}),
		PRAYER_RENEWAL(23609 , new int[] { 21636, 21634, 21632, 21630}),
		OVERLOAD(23531 , new int[] { 15335, 15334, 15333, 15332});
		// ( , new int[] {}),
		public final int flaskId;
		public final int[] potions;

		Flasks(final int flaskId, final int[] potions) {
			this.flaskId = flaskId;
			this.potions = potions;
		}

		public int[] getPotions() { return potions; }

		public static Flasks getFlaskEnum(int potionId) {
			for (Flasks flask : Flasks.values())
				for (int itemId : flask.potions)
					if (itemId == potionId)
						return flask;
			return null;
		}

		public static int getFlaskIdFromPotionId(int potionId) {
			return getFlaskEnum(potionId).flaskId;
		}

		public static int getDose(int potion) {
			for (Flasks flask : Flasks.values())
				for (int i = 0; i < flask.potions.length; i++)
					if (flask.potions[i] == potion)
						return i + 1;
			return -1;
		}
	}

	static void initializePotions() {
		int potionIndex = 0;
		for (Flasks flask : Flasks.values())
			for (int i = 0; i < 4; i++)
				POTIONS[potionIndex++] = flask.getPotions()[i];
	}
	
	private static void decantIntoFlask(ItemOnItemEvent e, int dosesLeft) {
		Player p = e.getPlayer();
		int usedOnFlask = e.getUsedWith(EMPTY_FLASK).getId();
		if (dosesLeft == 6) {
			dosesLeft = dosesLeft -Flasks.getDose(usedOnFlask);
			p.getInventory().deleteItem(e.getUsedWith(EMPTY_FLASK));
		}

		for (int potionId : Flasks.getFlaskEnum(usedOnFlask).getPotions())
			if (p.getInventory().containsItem(potionId)) {
				if (dosesLeft -Flasks.getDose(potionId) < 0) {
					int dosesOver =Flasks.getDose(potionId) - dosesLeft;
					int potionIdOver =Flasks.getFlaskEnum(potionId).getPotions()[dosesOver - 1];
					p.getInventory().deleteItem(potionId, 1);
					p.getInventory().addItemDrop(potionIdOver, 1);
					p.getInventory().replace(e.getUsedWith(usedOnFlask), new Item(Flasks.getFlaskIdFromPotionId(potionId), 1));
					break;
				}
				if (dosesLeft -Flasks.getDose(potionId) == 0) { // base case
					p.getInventory().deleteItem(potionId, 1);
					p.getInventory().addItem(EMPTY_POTION, 1);
					p.getInventory().addItem(EMPTY_POTION, 1);
					p.getInventory().replace(e.getUsedWith(usedOnFlask), new Item(Flasks.getFlaskIdFromPotionId(potionId), 1));
					return;
				}
				if (dosesLeft -Flasks.getDose(potionId) > 0) {
					dosesLeft = dosesLeft -Flasks.getDose(potionId);
					p.getInventory().deleteItem(potionId, 1);
					p.getInventory().addItem(EMPTY_POTION, 1);
					decantIntoFlask(e, dosesLeft);
					break;
				}
			}
	}

	private static boolean inventoryHasEnoughPotion(ItemOnItemEvent e) {
		int doses = 6;
		int usedOnFlask = e.getUsedWith(EMPTY_FLASK).getId();
		for (int potionId : Flasks.getFlaskEnum(usedOnFlask).getPotions())
			if (e.getPlayer().getInventory().containsItem(potionId)) {
				int potionCount =e.getPlayer().getInventory().getAmountOf(potionId);
				int potionDose =Flasks.getDose(potionId);
				doses = doses - potionCount*potionDose;
				if (doses<=0)
					return true;
			}
		return false;
	}

	public static ItemOnItemHandler handlePotionOnFlask = new ItemOnItemHandler(EMPTY_FLASK, POTIONS, e -> {
		if (inventoryHasEnoughPotion(e)) {
			decantIntoFlask(e, 6);
			e.getPlayer().getPackets().sendGameMessage("You fill the flask");
		}
	});
}
