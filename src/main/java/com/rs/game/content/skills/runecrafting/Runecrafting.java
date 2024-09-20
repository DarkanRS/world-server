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

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.quest.Quest;
import com.rs.game.content.combat.CombatDefinitions;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemEquipHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.drop.Drop;
import com.rs.utils.drop.DropList;
import com.rs.utils.drop.DropSet;
import com.rs.utils.drop.DropTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PluginEventHandler
public class Runecrafting {

	public final static int[] LEVEL_REQ = { 1, 25, 50, 75, 90 };
	public final static int AIR_TIARA = 5527, MIND_TIARA = 5529, WATER_TIARA = 5531, BODY_TIARA = 5533, EARTH_TIARA = 5535, FIRE_TIARA = 5537, COSMIC_TIARA = 5539, NATURE_TIARA = 5541, CHAOS_TIARA = 5543,
			LAW_TIARA = 5545, DEATH_TIARA = 5547, BLOOD_TIARA = 5549, SOUL_TIARA = 5551, ASTRAL_TIARA = 9106, OMNI_TIARA = 13655, AIR_TALISMAN = 1438, MIND_TALISMAN = 1448, WATER_TALISMAN = 1444, BODY_TALISMAN = 1446, EARTH_TALISMAN = 1440, FIRE_TALISMAN = 1442,
			COSMIC_TALISMAN = 1454, NATURE_TALISMAN = 1462, CHAOS_TALISMAN = 1452, LAW_TALISMAN = 1458, DEATH_TALISMAN = 1456, BLOOD_TALISMAN = 1450, SOUL_TALISMAN = 1460, ELEMENTAL_TALISMAN = 5516,
			AIR_TALISMAN_STAFF = 13630, MIND_TALISMAN_STAFF = 13631,  WATER_TALISMAN_STAFF = 13632, EARTH_TALISMAN_STAFF = 13633,  FIRE_TALISMAN_STAFF = 13634,  BODY_TALISMAN_STAFF = 13635,  COSMIC_TALISMAN_STAFF = 13636, CHAOS_TALISMAN_STAFF = 13637,
			NATURE_TALISMAN_STAFF = 13638, LAW_TALISMAN_STAFF = 13639, DEATH_TALISMAN_STAFF = 13640, BLOOD_TALISMAN_STAFF = 13641, OMNI_TALISMAN_STAFF = 13642, WICKED_HOOD = 22332;

	public static final int BINDING_NECKLACE = 5521;

	public enum RCRune {
		AIR(1, 5.0, 556, false, 11, 2, 22, 3, 33, 4, 44, 5, 55, 6, 66, 7, 77, 8, 88, 9, 99, 10),
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

		private static final HashMap<Integer, RCRune> BY_RUNE_ID = new HashMap<>();

		static {
			for (RCRune value : values())
				BY_RUNE_ID.put(value.runeId, value);
		}

		public static RCRune forId(int itemId) {
			return BY_RUNE_ID.get(itemId);
		}

		private final int req, runeId;
		private final double xp;
		private final boolean pureEss;
		private final int[] multipliers;

		RCRune(int req, double xp, int runeId, boolean pureEss, int... multipliers) {
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

	public static ItemClickHandler pouches = new ItemClickHandler(new Object[] { 5509, 5510, 5511, 5512, 5513, 5514, 24204, 24205 }, new String[] { "Fill", "Empty", "Check" }, e -> {
		switch (e.getOption()) {
			case "Fill" -> {
				switch (e.getItem().getId()) {
					case 5509 -> fillPouch(e.getPlayer(), 0);
					case 5510 -> fillPouch(e.getPlayer(), 1);
					case 5512 -> fillPouch(e.getPlayer(), 2);
					case 5514 -> fillPouch(e.getPlayer(), 3);
					case 24204 -> {
						if (e.getPlayer().getInventory().containsItem(24205, 1) || e.getPlayer().getBank().containsItem(24205, 1)) {
							e.getPlayer().sendMessage("You already have a massive pouch.");
							return;
						}
						if (!e.getPlayer().getInventory().hasFreeSlots()) {
							e.getPlayer().sendMessage("Not enough space in your inventory.");
							return;
						}
						e.getPlayer().getInventory().deleteItem(24204, 1);
						e.getPlayer().getInventory().addItem(24205, 1);
					}
					case 24205 -> fillPouch(e.getPlayer(), 4);
				}
			}
			case "Empty" -> {
				switch (e.getItem().getId()) {
					case 5509 -> emptyPouch(e.getPlayer(), 0);
					case 5510 -> emptyPouch(e.getPlayer(), 1);
					case 5512 -> emptyPouch(e.getPlayer(), 2);
					case 5514 -> emptyPouch(e.getPlayer(), 3);
					case 24204 -> e.getPlayer().sendMessage("This pouch contains no essence in it.");
					case 24205 -> emptyPouch(e.getPlayer(), 4);
				}
			}
			case "Check" -> {
				switch (e.getItem().getId()) {
					case 5509 -> e.getPlayer().sendMessage("This pouch has " + e.getPlayer().getPouches()[0] + (e.getPlayer().getPouchesType()[0] ? " pure" : " rune") + " essence in it.", false);
					case 5510 -> e.getPlayer().sendMessage("This pouch has " + e.getPlayer().getPouches()[1] + (e.getPlayer().getPouchesType()[1] ? " pure" : " rune") + " essence in it.", false);
					case 5512 -> e.getPlayer().sendMessage("This pouch has " + e.getPlayer().getPouches()[2] + (e.getPlayer().getPouchesType()[2] ? " pure" : " rune") + " essence in it.", false);
					case 5514 -> e.getPlayer().sendMessage("This pouch has " + e.getPlayer().getPouches()[3] + (e.getPlayer().getPouchesType()[3] ? " pure" : " rune") + " essence in it.", false);
					case 24204 -> e.getPlayer().sendMessage("An empty pouch.");
					case 24205 -> e.getPlayer().sendMessage("This pouch has " + e.getPlayer().getPouches()[4] + (e.getPlayer().getPouchesType()[4] ? " pure" : " rune") + " essence in it.", false);
				}
			}
		}
		e.getPlayer().stopAll(false);
	});
	
	public static ItemEquipHandler shouldShowEnterOption = new ItemEquipHandler(new Object[] { AIR_TIARA, WATER_TIARA, BODY_TIARA, EARTH_TIARA, FIRE_TIARA, COSMIC_TIARA, NATURE_TIARA, CHAOS_TIARA, LAW_TIARA, DEATH_TIARA, BLOOD_TIARA, SOUL_TIARA, ASTRAL_TIARA, OMNI_TIARA }, e -> e.getPlayer().getVars().setVar(491, e.equip() ? 1 : 0));

	public static void craftTalisman(Player player, RunecraftingTalisman talisman) {
		player.sendOptionDialogue("What would you like to imbue?", ops -> {
			ops.add("Tiara", () -> {
				if (!player.getInventory().containsItem(5525, 1) || !player.getInventory().containsItem(talisman.getTalismanId(), 1)) {
					player.sendMessage("You need a tiara to do this.");
					return;
				}
				player.getInventory().deleteItem(5525, 1);
				player.getInventory().deleteItem(talisman.getTalismanId(), 1);
				player.getSkills().addXp(Constants.RUNECRAFTING, talisman.getTiaraExp());
				player.getInventory().addItem(talisman.getTiaraId(), 1);
			});
			ops.add("Staff", () -> {
				if (!player.getInventory().containsItem(13629, 1) || !player.getInventory().containsItem(talisman.getTalismanId(), 1)) {
					player.sendMessage("You need a runecrafting staff to do this.");
					return;
				}
				if (player.getSkills().getLevel(Constants.RUNECRAFTING) < talisman.getStaffLevelReq()) {
					player.sendMessage("You need a runecrafting level of " + talisman.getStaffLevelReq() + " to create a " + new Item(talisman.getStaffId()).getDefinitions().getName() + ".");
					return;
				}
				player.getInventory().deleteItem(13629, 1);
				player.getInventory().deleteItem(talisman.getTalismanId(), 1);
				player.getSkills().addXp(Constants.RUNECRAFTING, talisman.getStaffExp());
				player.getInventory().addItem(talisman.getStaffId(), 1);
			});
		});
	}

	public static boolean craftCombinationRune(Player player, AltarCombination combination) {
		if (player.getSkills().getLevel(Constants.RUNECRAFTING) < combination.getLevelReq()) {
			return false;
		}

		int pureEss = player.getInventory().getNumberOf(PURE_ESS);
		if (pureEss == 0) {
			return false;
		}

		int reagentRunes = player.getInventory().getNumberOf(combination.getReagentRune().getId());
		if (reagentRunes == 0) {
			return false;
		}

		if (!player.isCastMagicImbue()) {
			if (!player.getInventory().containsItem(combination.getTalisman().getTalismanId(), 1)) {
				return false;
			}
			player.getInventory().deleteItem(combination.getTalisman().getTalismanId(), 1);
		}

		int maxCraftable = Math.min(reagentRunes, pureEss);
		player.getInventory().deleteItem(PURE_ESS, maxCraftable);
		player.getInventory().deleteItem(combination.getReagentRune().getId(), maxCraftable);

		double xp = combination.getXp();
		if (Runecrafting.hasRcingSuit(player))
			xp *= 1.025;

		String runeName = new Item(combination.getOutputRuneId()).getName();
		if (player.getEquipment().getAmuletId() == BINDING_NECKLACE) {
			player.sendMessage("You bind the temple's power into " + runeName + "s.");
			player.bindingNecklaceCharges--;
			if (player.bindingNecklaceCharges <= 0) {
				player.getEquipment().deleteSlot(Equipment.NECK);
				player.sendMessage("Your binding necklace disintegrates.");
				player.bindingNecklaceCharges = 15;
			}
		} else {
			player.sendMessage("You attempt to bind " + runeName + "s.");
			maxCraftable /= 2;
		}
		player.getInventory().addItem(combination.getOutputRuneId(), maxCraftable);
		player.getSkills().addXp(Constants.RUNECRAFTING, xp * maxCraftable);
		return true;
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

		private final double xp;
		private final int id;
		private final double[] chances;

		private static final Map<Integer, DropList> CHANCES = new HashMap<>();
		private static final Map<Integer, ZMIRune> BY_ID = new HashMap<>();

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
			return BY_ID.get(rune.getFirst().getId());
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

	private static ZMIRune rollZMIRune(Player player) {
		return ZMIRune.calculate(Utils.clampI(player.getSkills().getLevel(Skills.RUNECRAFTING) >= 99 ? 10 : player.getSkills().getLevel(Skills.RUNECRAFTING) / 10, 0, 10));
	}

	public static void craftZMIAltar(Player player) {
		int level = player.getSkills().getLevel(Constants.RUNECRAFTING);
		int runes = player.getInventory().getItems().getNumberOf(PURE_ESS);

		for (Item i : player.getInventory().getItems().array()) {
			if (i == null)
				continue;

			int pouch = switch (i.getId()) {
				case 5509 -> 0;
				case 5510 -> 1;
				case 5512 -> 2;
				case 5514 -> 3;
				case 24205 -> 4;
				default -> -1;
			};

			if (pouch == -1)
				continue;

			if (player.getPouchesType()[pouch]) { //only grab pure ess for ZMI altar
				runes += player.getPouches()[pouch];
				player.getPouches()[pouch] = 0;
			}
		}
		switch (player.getFamiliarPouch()) {
			case ABYSSAL_PARASITE, ABYSSAL_LURKER, ABYSSAL_TITAN -> {
				runes += player.getFamiliar().getInventory().getNumberOf(PURE_ESS);
				player.getFamiliar().getInventory().removeAll(PURE_ESS);
			}
			case null, default -> {}
		}

		for (int i = 0; i < RCRune.values().length; i++) {
			if (RCRune.values()[i].req > level)
				break;
		}
		double xp = 0;
		if (runes == 0) {
			player.simpleDialogue("You don't have pure essence.");
			return;
		}
		player.getInventory().deleteItem(PURE_ESS, runes);
		for (int i = 0; i < runes; i++) {
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
					case 24205 -> 4;
					default -> -1;
				};

				if (pouch == -1)
					continue;

				if (player.getPouchesType()[pouch]) {
					runes += player.getPouches()[pouch];
					player.getPouches()[pouch] = 0;
				} else if (!rune.pureEss){
					runes += player.getPouches()[pouch];
					player.getPouches()[pouch] = 0;
				}
			}
			switch (player.getFamiliarPouch()) {
				case ABYSSAL_PARASITE, ABYSSAL_LURKER, ABYSSAL_TITAN -> {
					if (!rune.pureEss) {
						runes += player.getFamiliar().getInventory().getUsedSlots();
						player.getFamiliar().getInventory().removeAll(RUNE_ESS);
						player.getFamiliar().getInventory().removeAll(PURE_ESS);
					} else {
						runes += player.getFamiliar().getInventory().getNumberOf(PURE_ESS);
						player.getFamiliar().getInventory().removeAll(PURE_ESS);
					}
				}
				case null, default -> {}
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
			player.getInventory().addItem(PURE_ESS, runes-amount-1);
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
		return player.getEquipment().getHatId() == 21485
				&& player.getEquipment().getChestId() == 21484
				&& player.getEquipment().getLegsId() == 21486
				&& player.getEquipment().getBootsId() == 21487;
	}

	public static void locate(Player p, int ruinsXPos, int ruinsYPos) {
		String direction = "";
		int playerXPos = p.getX();
		int playerYPos = p.getY();

		if (playerXPos < ruinsXPos && playerYPos < ruinsYPos) { direction = "north-east"; }
		if (playerXPos < ruinsXPos && playerYPos == ruinsYPos) { direction = "east"; }
		if (playerXPos < ruinsXPos && playerYPos > ruinsYPos) { direction = "south-east"; }
		if (playerXPos == ruinsXPos && playerYPos > ruinsYPos) { direction = "south"; }
		if (playerXPos > ruinsXPos && playerYPos > ruinsYPos) { direction = "south-west"; }
		if (playerXPos > ruinsXPos && playerYPos == ruinsYPos) { direction = "west"; }
		if (playerXPos > ruinsXPos && playerYPos < ruinsYPos) { direction = "north-west"; }
		if (playerXPos == ruinsXPos && playerYPos < ruinsYPos) { direction = "north"; }

		p.sendMessage("The talisman pulls towards the " + direction + ".", false);
	}

	public static final int[] POUCH_SIZE = { 3, 6, 9, 12, 18 };

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
				case 24205 -> 4;
				default -> -1;
			};

			if (pouch == -1)
				continue;

			if (p.getPouchesType()[pouch] != (essence == PURE_ESS))
				continue;

			if (p.getSkills().getLevel(Skills.RUNECRAFTING) >= LEVEL_REQ[pouch]) {
				if (p.getBank().containsItem(essence, 1)) {
					int essenceToAdd = POUCH_SIZE[pouch] - p.getPouches()[pouch];
					if (essenceToAdd == POUCH_SIZE[pouch])
						p.getPouchesType()[pouch] = p.getBank().getItem(essence).getAmount() > 0;
					if (essenceToAdd > p.getBank().getItem(essence).getAmount())
						essenceToAdd = p.getBank().getItem(essence).getAmount();
					if (essenceToAdd > POUCH_SIZE[pouch] - p.getPouches()[pouch])
						essenceToAdd = POUCH_SIZE[pouch] - p.getPouches()[pouch];
					if (essenceToAdd > 0) {
						p.getBank().removeItem(p.getBank().getSlot(essence), essenceToAdd, true, false);
						p.getPouches()[pouch] += essenceToAdd;
					}
					if (essenceToAdd != 0) {
						p.sendMessage(essenceToAdd + " " + ItemDefinitions.getDefs(essence).getName() + " has been placed into your " + i.getName().toLowerCase() + ".");
						p.getPouchesType()[pouch] = (essence == PURE_ESS);
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
		}
	}

	public static ObjectClickHandler handleCraftOnAltar = new ObjectClickHandler(new Object[] { 2478, 2479, 2480, 2481, 2482, 2483, 2484, 2485, 2486, 2487, 2488, 17010, 30624, 26847 }, e -> {
		RCRune rune = switch (e.getObjectId()) {
			case 2478 -> RCRune.AIR;
			case 2479 -> RCRune.MIND;
			case 2480 -> RCRune.WATER;
			case 2481 -> RCRune.EARTH;
			case 2482 -> RCRune.FIRE;
			case 2483 -> RCRune.BODY;
			case 2484 -> RCRune.COSMIC;
			case 2485 -> RCRune.LAW;
			case 2486 -> RCRune.NATURE;
			case 2487 -> RCRune.CHAOS;
			case 2488 -> RCRune.DEATH;
			case 17010 -> RCRune.ASTRAL;
			case 30624 -> RCRune.BLOOD;
			default -> null;
		};

		if (e.getObjectId() == 17010 && e.getOpNum() == ClientPacket.OBJECT_OP2) {
			e.getPlayer().startConversation(new Dialogue().addOptions("Change spellbooks?", ops -> {
				ops.add("Yes, replace my spellbook.", () -> {
					if (e.getPlayer().getCombatDefinitions().getSpellbook() != CombatDefinitions.Spellbook.LUNAR) {
						if (!e.getPlayer().isQuestComplete(Quest.LUNAR_DIPLOMACY, "to use the Lunar Spellbook."))
							return;
						e.getPlayer().sendMessage("Your mind clears and you switch back to the ancient spellbook.");
						e.getPlayer().getCombatDefinitions().setSpellbook(CombatDefinitions.Spellbook.LUNAR);
					} else {
						e.getPlayer().sendMessage("Your mind clears and you switch back to the normal spellbook.");
						e.getPlayer().getCombatDefinitions().setSpellbook(CombatDefinitions.Spellbook.MODERN);
					}
				});
				ops.add("Nevermind.");
			}));
			return;
		}

		if (e.getObjectId() == 26847)
			Runecrafting.craftZMIAltar(e.getPlayer());
		else if (rune != null)
			Runecrafting.runecraft(e.getPlayer(), rune, false);
	});

	public static ObjectClickHandler handleZmiLadders = new ObjectClickHandler(new Object[] { 26849, 26850 }, e -> {
		e.getPlayer().setNextAnimation(new Animation(828));
		switch (e.getObjectId()) {
			case 26849 -> WorldTasks.delay(1, () -> e.getPlayer().tele(Tile.of(3271, 4861, 0)));
			case 26850 -> WorldTasks.delay(1, () -> e.getPlayer().tele(Tile.of(2452, 3232, 0)));
		}
	});
}