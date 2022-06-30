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
package com.rs.game.content.skills.runecrafting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ItemEquipEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemEquipHandler;
import com.rs.utils.drop.Drop;
import com.rs.utils.drop.DropList;
import com.rs.utils.drop.DropSet;
import com.rs.utils.drop.DropTable;

@PluginEventHandler
public class Runecrafting {

	public final static int[] LEVEL_REQ = { 1, 25, 50, 75 };
	public final static int AIR_TIARA = 5527, MIND_TIARA = 5529, WATER_TIARA = 5531, BODY_TIARA = 5533, EARTH_TIARA = 5535, FIRE_TIARA = 5537, COSMIC_TIARA = 5539, NATURE_TIARA = 5541, CHAOS_TIARA = 5543,
			LAW_TIARA = 5545, DEATH_TIARA = 5547, BLOOD_TIARA = 5549, SOUL_TIARA = 5551, ASTRAL_TIARA = 9106, OMNI_TIARA = 13655, AIR_TALISMAN = 1438, MIND_TALISMAN = 1448, WATER_TALISMAN = 1444, BODY_TALISMAN = 1446, EARTH_TALISMAN = 1440, FIRE_TALISMAN = 1442,
			COSMIC_TALISMAN = 1454, NATURE_TALISMAN = 1462, CHAOS_TALISMAN = 1452, LAW_TALISMAN = 1458, DEATH_TALISMAN = 1456, BLOOD_TALISMAN = 1450, SOUL_TALISMAN = 1460, ELEMENTAL_TALISMAN = 5516,
			AIR_TALISMAN_STAFF = 13630, MIND_TALISMAN_STAFF = 13631,  WATER_TALISMAN_STAFF = 13632, EARTH_TALISMAN_STAFF = 13633,  FIRE_TALISMAN_STAFF = 13634,  BODY_TALISMAN_STAFF = 13635,  COSMIC_TALISMAN_STAFF = 13636, CHAOS_TALISMAN_STAFF = 13637,
			NATURE_TALISMAN_STAFF = 13638, LAW_TALISMAN_STAFF = 13639, DEATH_TALISMAN_STAFF = 13640, BLOOD_TALISMAN_STAFF = 13641, OMNI_TALISMAN_STAFF = 13642, WICKED_HOOD = 22332;

	public enum RCRune {
		AIR(1, 5.0, 556, false, 11, 2, 22, 3, 34, 4, 44, 5, 55, 6, 66, 7, 77, 8, 88, 9, 99, 10),
		MIND(1, 5.5, 558, false, 14, 2, 28, 3, 42, 4, 56, 5, 70, 6, 84, 7, 98, 8),
		WATER(5, 6.0, 555, false, 19, 2, 38, 3, 57, 4, 76, 5, 95, 6),
		EARTH(9, 6.5, 557, false, 26, 2, 52, 3, 78, 4),
		FIRE(14, 7.0, 554, false, 35, 2, 70, 3),
		BODY(20, 7.5, 559, false, 46, 2, 92, 3),
		COSMIC(27, 8.0, 564, true, 59, 2),
		CHAOS(35, 8.5, 562, true, 74, 2),
		ASTRAL(40, 8.7, 9075, true, 82, 2),
		NATURE(44, 9.0, 561, true, 91, 2),
		LAW(54, 9.5, 563, true),
		DEATH(65, 10.0, 560, true),
		BLOOD(77, 10.5, 565, true),
		SOUL(90, 12.0, 566, true);

		private int req, runeId;
		private double xp;
		private boolean pureEss;
		private int[] multipliers;

		private RCRune(int req, double xp, int runeId, boolean pureEss, int... multipliers) {
			this.req = req;
			this.xp = xp;
			this.runeId = runeId;
			this.pureEss = pureEss;
			this.multipliers = multipliers;
		}

		public int getId() {
			return runeId;
		}

		public boolean isPureEss() {
			return pureEss;
		}
	}

	public static ItemClickHandler pouches = new ItemClickHandler(new Object[] { 5509, 5510, 5511, 5512, 5513, 5514 }, new String[] { "Fill", "Empty" }) {
		@Override
		public void handle(ItemClickEvent e) {
			if (e.getOption().equals("Fill"))
				switch(e.getItem().getId()) {
				case 5509 -> fillPouch(e.getPlayer(), 0);
				case 5510 -> fillPouch(e.getPlayer(), 1);
				case 5512 -> fillPouch(e.getPlayer(), 2);
				case 5514 -> fillPouch(e.getPlayer(), 3);
				}
			else
				switch(e.getItem().getId()) {
				case 5509 -> emptyPouch(e.getPlayer(), 0);
				case 5510 -> emptyPouch(e.getPlayer(), 1);
				case 5512 -> emptyPouch(e.getPlayer(), 2);
				case 5514 -> emptyPouch(e.getPlayer(), 3);
				}
			e.getPlayer().stopAll(false);
		}
	};
	
	public static ItemEquipHandler shouldShowEnterOption = new ItemEquipHandler(AIR_TIARA, WATER_TIARA, BODY_TIARA, EARTH_TIARA, FIRE_TIARA, COSMIC_TIARA, NATURE_TIARA, CHAOS_TIARA, LAW_TIARA, DEATH_TIARA, BLOOD_TIARA, SOUL_TIARA, ASTRAL_TIARA, OMNI_TIARA) {
		@Override
		public void handle(ItemEquipEvent e) {
			e.getPlayer().getVars().setVar(491, e.equip() ? 1 : 0);
		}
	};

	public static void craftTalisman(Player player, int talisman, int tiara, int staff, double xp) {
		player.sendOptionDialogue("What would you like to imbue?", ops -> {
			ops.add("Tiara", () -> {
				if (player.getInventory().containsItem(5525, 1) && player.getInventory().containsItem(talisman, 1)) {
					player.getInventory().deleteItem(5525, 1);
					player.getInventory().deleteItem(talisman, 1);
					player.getSkills().addXp(Constants.RUNECRAFTING, xp);
					player.getInventory().addItem(tiara, 1);
				} else
					player.sendMessage("You need a tiara to do this.");
			});
			ops.add("Staff", () -> {
				if (player.getInventory().containsItem(13629, 1) && player.getInventory().containsItem(talisman, 1)) {
					player.getInventory().deleteItem(13629, 1);
					player.getInventory().deleteItem(talisman, 1);
					player.getSkills().addXp(Constants.RUNECRAFTING, xp);
					player.getInventory().addItem(staff, 1);
				} else
					player.sendMessage("You need a runecrafting staff to do this.");
			});
		});
	}

	private enum ZMIRune {
		AIR(5.0, 556, 		new double[] { 50.0, 15.0, 12.0, 7.0, 6.0, 5.0, 4.5, 3.0, 2.0, 1.0, 1.0 }),
		MIND(5.5, 558, 		new double[] { 25.0, 18.0, 13.0, 8.0, 6.5, 5.5, 5.0, 3.0, 2.0, 1.0, 1.0 }),
		WATER(6.0, 555, 		new double[] { 12.0, 21.0, 13.5, 9.0, 7.0, 6.0, 5.5, 3.0, 3.0, 2.0, 2.0 }),
		EARTH(6.5, 557, 		new double[] { 6.0, 24.0, 14.0, 11.0, 7.5, 6.5, 6.0, 4.0, 4.0, 3.0, 3.0 }),
		FIRE(7.0, 554, 		new double[] { 3.0, 12.0, 15.0, 12.0, 8.0, 7.0, 7.0, 4.0, 5.0, 4.0, 3.0 }),
		BODY(7.5, 559, 		new double[] { 1.5, 6.0, 16.0, 13.0, 10.0, 7.5, 7.5, 5.0, 6.0, 5.0, 4.0 }),
		COSMIC(8.0, 564, 	new double[] { 0.85, 1.75, 8.0, 20.0, 15.0, 10.0, 9.5, 7.0, 7.0, 6.0, 5.0 }),
		CHAOS(8.5, 562, 		new double[] { 0.6, 0.8, 4.2, 10.0, 20.0, 11.0, 10.5, 9.0, 8.0, 7.0, 6.0 }),
		ASTRAL(8.7, 9075, 	new double[] { 0.45, 0.6, 2.1, 5.0, 10.0, 15.0, 14.0, 12.0, 10.5, 10.0, 9.5 }),
		NATURE(9.0, 561, 	new double[] { 0.3, 0.4, 1.1, 2.5, 5.0, 13.5, 15.5, 15.0, 13.5, 13.5, 13.5 }),
		LAW(9.5, 563, 		new double[] { 0.15, 0.24, 0.55, 1.3, 2.6, 7.0, 8.0, 18.0, 14.5, 14.5, 14.5 }),
		DEATH(10.0, 560, 	new double[] { 0.08, 0.12, 0.32, 0.6, 1.2, 3.5, 4.0, 10.0, 14.5, 16.5, 15.5 }),
		BLOOD(10.5, 565, 	new double[] { 0.05, 0.06, 0.15, 0.4, 0.8, 1.7, 2.0, 5.0, 6.0, 10.0, 13.0 }),
		SOUL(12.0, 566, 		new double[] { 0.02, 0.03, 0.08, 0.2, 0.4, 0.8, 1.0, 2.0, 4.0, 6.5, 9.0 });

		private double xp;
		private int id;
		private double[] chances;

		private static Map<Integer, DropList> CHANCES = new HashMap<>();
		private static Map<Integer, ZMIRune> BY_ID = new HashMap<>();

		static {
			for (ZMIRune r : ZMIRune.values())
				BY_ID.put(r.id, r);
			for (int i = 0;i < ZMIRune.AIR.chances.length;i++) {
				DropTable[] tables = new DropTable[ZMIRune.values().length];
				for (ZMIRune rune : ZMIRune.values())
					tables[rune.ordinal()] = new DropTable(rune.chances[i], 100.0, new Drop(rune.id));
				CHANCES.put(i, new DropSet(tables).getDropList());
			}
		}

		public static ZMIRune calculate(int threshhold) {
			DropList chances = CHANCES.get(threshhold);
			List<Item> rune = chances.genDrop();
			if (rune.isEmpty())
				return ZMIRune.AIR;
			return BY_ID.get(rune.get(0).getId());
		}

		ZMIRune(double xp, int id, double[] chances) {
			assert(chances.length == 11);
			this.xp = xp;
			this.id = id;
			this.chances = chances;
		}

		public double getXP() {
			return 1.7 * xp;
		}
	}

	public static ZMIRune rollZMIRune(Player player) {
		return ZMIRune.calculate(Utils.clampI(player.getSkills().getLevel(Skills.RUNECRAFTING) >= 99 ? 10 : player.getSkills().getLevel(Skills.RUNECRAFTING) / 10, 0, 10));
	}

	public static void craftZMIAltar(Player player) {
		int level = player.getSkills().getLevel(Constants.RUNECRAFTING);
		int runes = player.getInventory().getItems().getNumberOf(PURE_ESS);
		for (int i = 0; i < RCRune.values().length; i++) {
			if (RCRune.values()[i].req > level)
				break;
		}
		double xp = 0;
		int craftedSoFar = 0;
		if (runes == 0) {
			player.simpleDialogue("You don't have pure essence.");
			return;
		}
		player.getInventory().deleteItem(PURE_ESS, runes);
		for (int i = 0; i < runes; i++) {
			craftedSoFar++;
			if (craftedSoFar >= runes)
				break;
			ZMIRune rune = rollZMIRune(player);
			int amt = Utils.random(1, 3);
			player.incrementCount(ItemDefinitions.getDefs(rune.id).getName() + " runecrafted", amt);
			player.getInventory().addItem(rune.id, amt);
			xp += rune.getXP();
		}
		if (hasRcingSuit(player))
			xp *= 1.025;
		player.getSkills().addXp(Constants.RUNECRAFTING, xp * 2);
		player.setNextSpotAnim(new SpotAnim(186));
		player.setNextAnimation(new Animation(791));
		player.lock(5);
		player.sendMessage("You bind the temple's power into assorted runes.");
	}

	public static void runecraft(Player player, RCRune rune) {
		runecraft(player, rune, false);
	}

	public static void runecraft(Player player, RCRune rune, boolean span) {
		int actualLevel = player.getSkills().getLevel(Constants.RUNECRAFTING);
		if (actualLevel < rune.req) {
			player.simpleDialogue("You need a runecrafting level of " + rune.req + " to craft this rune.");
			return;
		}
		int runes = player.getInventory().getItems().getNumberOf(PURE_ESS);
		if (!span) {
			for (Item i : player.getInventory().getItems().array()) {
				if (i == null)
					continue;

				int pouch = switch (i.getId()) {
					case 5509 -> 0;
					case 5510 -> 1;
					case 5512 -> 2;
					case 5514 -> 3;
					default -> -1;
				};

				if (pouch == -1)
					continue;

				if (player.getPouchesType()[pouch]) {
					runes += player.getPouches()[pouch];
					player.getPouches()[pouch] = 0;
				} else if (!rune.isPureEss()){
					runes += player.getPouches()[pouch];
					player.getPouches()[pouch] = 0;
				}
			}
		}

		if (runes > 0) {
			if (span)
				runes = 1;
			player.getInventory().deleteItem(PURE_ESS, runes);
		}
		if (!rune.pureEss) {
			int normalEss = player.getInventory().getItems().getNumberOf(RUNE_ESS);

			if (span)
				normalEss = 1;
			if (runes > 0 && span)
				normalEss = 0;
			if (normalEss > 0) {
				player.getInventory().deleteItem(RUNE_ESS, normalEss);
				runes += normalEss;
			}
		}
		if (runes == 0) {
			player.simpleDialogue("You don't have " + (rune.pureEss ? "pure" : "rune") + " essence.");
			return;
		}
		if (rune.runeId == 556 && player.getInventory().containsItem(21774, 1) && player.getSkills().getLevel(Constants.RUNECRAFTING) >= 72) {
			int amount = player.getInventory().getAmountOf(21774);
			if (amount > runes)
				amount = runes;
			int numberArma = amount * numberPerShard(player.getSkills().getLevel(Constants.RUNECRAFTING));
			player.getInventory().deleteItem(21774, amount);
			if (!span) {
				player.setNextSpotAnim(new SpotAnim(186));
				player.setNextAnimation(new Animation(791));
				player.lock(5);
				player.sendMessage("You bind the temple's power into Armadyl runes.");
			}
			player.getSkills().addXp(Constants.RUNECRAFTING, rune.xp * runes);
			player.getInventory().addItem(21773, numberArma);
			player.incrementCount("Armadyl rune runecrafted", numberArma);
			return;
		}
		double totalXp = rune.xp * runes;
		if (hasRcingSuit(player))
			totalXp *= 1.025;
		for (int i = rune.multipliers.length - 2; i >= 0; i -= 2)
			if (actualLevel >= rune.multipliers[i]) {
				runes *= rune.multipliers[i + 1];
				break;
			}
		player.getSkills().addXp(Constants.RUNECRAFTING, totalXp);
		if (!span) {
			player.setNextSpotAnim(new SpotAnim(186));
			player.setNextAnimation(new Animation(791));
			player.lock(5);
			player.sendMessage("You bind the temple's power into " + ItemDefinitions.getDefs(rune.runeId).getName().toLowerCase() + "s.");
		}
		player.getInventory().addItem(rune.runeId, runes);
		player.incrementCount(ItemDefinitions.getDefs(rune.runeId).getName()+" runecrafted", runes);
	}

	public static int numberPerShard(int level) {
		if (level >= 72 && level < 77)
			return 7;
		if (level > 76 && level < 88)
			return 8;
		if (level > 89 && level < 99)
			return 9;
		if (level >= 99)
			return 10;
		return 1;
	}

	public static boolean hasRcingSuit(Player player) {
		if (player.getEquipment().getHatId() == 21485 && player.getEquipment().getChestId() == 21484 && player.getEquipment().getLegsId() == 21486 && player.getEquipment().getBootsId() == 21487)
			return true;
		return false;
	}

	public static void locate(Player p, int xPos, int yPos) {
		String x = "";
		String y = "";
		int absX = p.getX();
		int absY = p.getY();
		if (absX >= xPos)
			x = "west";
		if (absY > yPos)
			y = "south";
		if (absX < xPos)
			x = "east";
		if (absY <= yPos)
			y = "north";
		p.sendMessage("The talisman pulls towards " + y + "-" + x + ".", false);
	}

	public static void checkPouch(Player p, int i) {
		if (i < 0)
			return;
		p.sendMessage("This pouch has " + p.getPouches()[i] + (p.getPouchesType()[i] ? " pure" : " rune")+ " essence in it.", false);
	}

	public static final int[] POUCH_SIZE = { 3, 6, 9, 12 };

	public static final int RUNE_ESS = 1436;
	public static final int PURE_ESS = 7936;

	public static void fillPouch(Player p, int i) {
		if (i < 0)
			return;
		if (LEVEL_REQ[i] > p.getSkills().getLevel(Constants.RUNECRAFTING)) {
			p.sendMessage("You need a Runecrafting level of " + LEVEL_REQ[i] + " to use this pouch.", false);
			return;
		}
		if (!p.getInventory().containsOneItem(RUNE_ESS) && !p.getInventory().containsOneItem(PURE_ESS)) {
			p.sendMessage("You don't have any essence with you.", false);
			return;
		}

		int essenceToAdd = POUCH_SIZE[i] - p.getPouches()[i];
		if (essenceToAdd == POUCH_SIZE[i])
			p.getPouchesType()[i] = p.getInventory().getItems().getNumberOf(PURE_ESS) > 0;

			int essType = p.getPouchesType()[i] ? PURE_ESS : RUNE_ESS;

			if (essenceToAdd > p.getInventory().getItems().getNumberOf(essType))
				essenceToAdd = p.getInventory().getItems().getNumberOf(essType);
			if (essenceToAdd > POUCH_SIZE[i] - p.getPouches()[i])
				essenceToAdd = POUCH_SIZE[i] - p.getPouches()[i];
			if (essenceToAdd > 0) {
				p.getInventory().deleteItem(essType, essenceToAdd);
				p.getPouches()[i] += essenceToAdd;
			}
			if (essenceToAdd == 0) {
				p.sendMessage("Your pouch is full.", false);
				return;
			}
	}

	public static void fillPouchesFromBank(Player p, int essence) {
		for (Item i : p.getInventory().getItems().array()) {

			if (i == null)
				continue;

			int pouch = switch (i.getId()) {
				case 5509 -> 0;
				case 5510 -> 1;
				case 5512 -> 2;
				case 5514 -> 3;
				default -> -1;
			};

			if (pouch == -1)
				continue;

			if (p.getPouches()[pouch] > 0 && p.getPouchesType()[pouch] != (essence == PURE_ESS))
				continue;

			if (p.getSkills().getLevel(Skills.RUNECRAFTING) >= LEVEL_REQ[pouch]) {
				if (p.getBank().containsItem(essence, 1)) {
					int essenceToAdd = POUCH_SIZE[pouch] - p.getPouches()[pouch];
					if (essenceToAdd > p.getBank().getItem(essence).getAmount())
						essenceToAdd = p.getBank().getItem(essence).getAmount();
					if (essenceToAdd > 0) {
						p.getBank().removeItem(p.getBank().getSlot(essence), essenceToAdd, true, false);
						p.getPouches()[pouch] += essenceToAdd;
						p.sendMessage(essenceToAdd + " " + ItemDefinitions.getDefs(essence).getName() + " has been placed into your " + i.getName().toLowerCase() + ".");
						p.getPouchesType()[pouch] = (essence == PURE_ESS ? true : false);
					}
				}
			}
		}
	}

	public static void emptyPouch(Player p, int i) {
		if (i < 0)
			return;
		int essType = p.getPouchesType()[i] ? PURE_ESS : RUNE_ESS;
		int toAdd = p.getPouches()[i];
		if (toAdd > p.getInventory().getFreeSlots())
			toAdd = p.getInventory().getFreeSlots();
		if (toAdd > 0) {
			p.getInventory().addItem(essType, toAdd);
			p.getPouches()[i] -= toAdd;
		}
		if (toAdd == 0) {
			p.sendMessage("Your pouch has no essence left in it.", false);
			return;
		}
	}
}