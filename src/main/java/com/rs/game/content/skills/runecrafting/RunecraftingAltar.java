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

import com.rs.engine.miniquest.Miniquest;
import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.content.miniquests.abyss.EnterTheAbyss;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.world.areas.wilderness.WildernessController;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.*;
import static com.rs.game.content.skills.runecrafting.Runecrafting.RCRune;

import java.util.ArrayList;

import static com.rs.game.content.skills.runecrafting.Runecrafting.PURE_ESS;

@PluginEventHandler
public class RunecraftingAltar {

	public static final int WICKED_HOOD = 22332;
	public static final int WICKED_HOOD_INTER = 1153;

	public static final int OMNI_TIARA = 13655;
	public static final int OMNI_TALISMAN = 13649;
	public static final int OMNI_TALISMAN_STAFF = 13642;

	public enum WickedHoodRune {
		MIND(558, 25, 50, 8013, 1448, 5529),
		AIR(556, 24, 55, 8007, 1438, 5527),
		WATER(555, 27, 51, 8006, 1444, 5531),
		EARTH(557, 29, 53, 8008, 1440, 5535),
		FIRE(554, 31, 52, 8005, 1442, 5537),
		BODY(559, 33, 54, 8014, 1446, 5534),
		CHAOS(562, 82, 57, 8015, 1452, 5543),
		BLOOD(565, 91, 62, 8017, 1450, 5549),
		DEATH(560, 89, 61, 8018, 1456, 5547),
		SOUL(-1, 93, 65, 8012, 1460, 5551),
		LAW(563, 87, 60, 8016, 1458, 5545),
		NATURE(561, 85, 59, 8011, 1462, 5541),
		ASTRAL(9075, 83, 58, 8010, -1, 9106),
		COSMIC(564, 80, 56, 8009, 1454, 5539),
		ELEMENTAL(-1, 96, 64, 8019, 5516, -1),
		OMNI(-1, 95, 63, 8020, OMNI_TALISMAN, OMNI_TIARA);

		private final int buttonId;
		private final int runeId;
		private final int spriteComp;
		private final int spriteId;
		private final int talismanId;
		private final int tiaraId;

		WickedHoodRune(int runeId, int componentId, int spriteComp, int spriteId, int talismanId, int tiaraId) {
			buttonId = componentId;
			this.runeId = runeId;
			this.spriteComp = spriteComp;
			this.spriteId = spriteId;
			this.talismanId = talismanId;
			this.tiaraId = tiaraId;
		}

		public int getRuneId() {
			return runeId;
		}

		public int getComponentId() {
			if (buttonId == 24)
				return 7;
			if (buttonId == 82)
				return 5;
			if (buttonId == 96)
				return 100;
			if (buttonId == 95)
				return 99;
			return buttonId + 1;
		}

		public int getButtonId() {
			return buttonId;
		}

		public int getSpriteComp() {
			return spriteComp;
		}

		public int getSpriteId() {
			return spriteId;
		}

		public int getTalismanId() {
			return talismanId;
		}

		public int getTiaraId() {
			return tiaraId;
		}
	}

	public static void sendWickedHoodInter(Player player) {
		player.getInterfaceManager().sendInterface(WICKED_HOOD_INTER);
		refreshHood(player);
	}

	public static void refreshHood(Player player) {
		player.getPackets().setIFText(WICKED_HOOD_INTER, 139, "" + player.getDailySubI("wickedEss", 100));
		player.getPackets().setIFText(WICKED_HOOD_INTER, 134, "" + player.getDailySubI("wickedTeles", 2));
		player.getPackets().setIFText(WICKED_HOOD_INTER, 143, "" + player.getDailySubI("wickedRunes", player.getUsedOmniTalisman() ? 2 : 1));
		for (WickedHoodRune rune : WickedHoodRune.values())
			if (player.hasWickedHoodTalisman(rune) || player.hasWickedHoodTalisman(rune))
				player.getPackets().setIFGraphic(WICKED_HOOD_INTER, rune.getSpriteComp(), rune.getSpriteId());
	}

	public static ItemClickHandler handleWickedHood = new ItemClickHandler(new Object[] { WICKED_HOOD }, new String[] { "Activate", "Teleport" }, e -> {
		if (e.getOption().equals("Activate")) {
			e.getPlayer().getTempAttribs().removeO("whr");
			sendWickedHoodInter(e.getPlayer());
		} else
			Magic.sendNormalTeleportSpell(e.getPlayer(), 0, 0, Tile.of(3106, 3162, 1), null, null);
	});

	public static ButtonClickHandler handleWickedHoodInter = new ButtonClickHandler(new Object[] { WICKED_HOOD_INTER }, e -> {
		if (e.getComponentId() == 119) {
			int amountToTake = e.getPlayer().getInventory().getFreeSlots();
			if (amountToTake > e.getPlayer().getDailySubI("wickedEss", 100))
				amountToTake = e.getPlayer().getDailySubI("wickedEss", 100);
			if (amountToTake > 0 && e.getPlayer().getInventory().hasFreeSlots()) {
				e.getPlayer().setDailyI("wickedEss", e.getPlayer().getDailyI("wickedEss") + amountToTake);
				if (e.getPlayer().getUsedElementalTalisman())
					e.getPlayer().getInventory().addItem(7936, amountToTake);
				else
					e.getPlayer().getInventory().addItem(1436, amountToTake);
				refreshHood(e.getPlayer());
			}
		} else if (e.getComponentId() == 111) {
			if (e.getPlayer().getTempAttribs().getO("whr") == null)
				e.getPlayer().sendMessage("You need to select a rune first.");
			else {
				WickedHoodRune selection = e.getPlayer().getTempAttribs().getO("whr");
				if (selection != null) {
					if (selection.name().equals("OMNI") || selection.name().equals("ELEMENTAL")) {
						e.getPlayer().sendMessage("Please choose a valid rune type.");
						return;
					}
					if (selection.name().equals("SOUL")) {
						e.getPlayer().sendMessage("The hood refuses to function.");
						return;
					}
					if (e.getPlayer().hasWickedHoodTalisman(selection)) {
						if (e.getPlayer().getDailySubI("wickedRunes", e.getPlayer().getUsedOmniTalisman() ? 2 : 1) > 0 && e.getPlayer().getInventory().hasFreeSlots()) {
							e.getPlayer().incDailyI("wickedRunes");
							e.getPlayer().getInventory().addItem(selection.getRuneId(), selection.ordinal() > 5 ? 5 : 100);
							refreshHood(e.getPlayer());
						}
					} else
						e.getPlayer().sendMessage("The hood has not learned this rune type yet.");
				}
			}
		} else if (e.getComponentId() == 127) {
			if (e.getPlayer().getTempAttribs().getO("whr") == null)
				e.getPlayer().sendMessage("You need to select a rune first.");
			else {
				WickedHoodRune selection = e.getPlayer().getTempAttribs().getO("whr");
				if (selection != null) {
					if (selection.name().equals("OMNI") || selection.name().equals("ELEMENTAL")) {
						e.getPlayer().sendMessage("Please choose a valid rune type.");
						return;
					}
					if (selection.name().equals("SOUL")) {
						e.getPlayer().sendMessage("The hood refuses to function.");
						return;
					}
					if (e.getPlayer().hasWickedHoodTalisman(selection)) {
						if (e.getPlayer().getDailyI("wickedTeles") < 2) {
							e.getPlayer().incDailyI("wickedTeles");
							Ruins ruins = Ruins.valueOf(selection.name());
							if (ruins.canEnter(e.getPlayer(), false))
								Magic.sendNormalTeleportSpell(e.getPlayer(), 0, 0, Ruins.valueOf(selection.name()).inside, null, null);
						}
					} else
						e.getPlayer().sendMessage("The hood has not learned this rune type yet.");
				}
			}
		} else {
			WickedHoodRune rune = null;
			for (WickedHoodRune r : WickedHoodRune.values())
				if (r.buttonId == e.getComponentId())
					rune = r;
			if (rune != null)
				if (e.getPlayer().hasWickedHoodTalisman(rune)) {
					if (e.getPlayer().getTempAttribs().getO("whr") == null) {
						e.getPlayer().getTempAttribs().setO("whr", rune);
						e.getPlayer().getPackets().setIFHidden(WICKED_HOOD_INTER, rune.getComponentId(), true);
					} else {
						WickedHoodRune old = e.getPlayer().getTempAttribs().setO("whr", rune);
						e.getPlayer().getPackets().setIFHidden(WICKED_HOOD_INTER, old.getComponentId(), false);
						e.getPlayer().getPackets().setIFHidden(WICKED_HOOD_INTER, rune.getComponentId(), true);
					}
				} else
					e.getPlayer().sendMessage("The hood has not learned this rune type yet.");
		}
	});

	public enum Ruins {
		MIND(1, new int[] { 1448, 5529, 13631 }, Tile.of(2793, 4828, 0), Tile.of(2984, 3515, 0), 2453, 2466),
		AIR(1, new int[] { 1438, 5527, 13630 }, Tile.of(2841, 4829, 0), Tile.of(3128, 3407, 0), 2452, 2465),
		WATER(5, new int[] { 1444, 5531, 13632 }, Tile.of(3494, 4832, 0), Tile.of(3183, 3164, 0), 2454, 2467),
		EARTH(9, new int[] { 1440, 5535, 13633 }, Tile.of(2655, 4830, 0), Tile.of(3306, 3472, 0), 2455, 2468),
		FIRE(14, new int[] { 1442, 5537, 13634 }, Tile.of(2577, 4846, 0), Tile.of(3312, 3253, 0), 2456, 2469),
		BODY(20, new int[] { 1446, 5533, 13635 }, Tile.of(2522, 4833, 0), Tile.of(3055, 3444, 0), 2457, 2470),
		COSMIC(27, new int[] { 1454, 5539, 13636 }, Tile.of(2162, 4833, 0), Tile.of(2408, 4379, 0), 2458, 2471),
		CHAOS(35, new int[] { 1452, 5543, 13637 }, Tile.of(2281, 4837, 0), Tile.of(3060, 3589, 0), 2461, 2474),
		ASTRAL(40, new int[] { -1, -1 }, Tile.of(2156, 3863, 0), Tile.of(2156, 3863, 0), 17010, -1),
		NATURE(44, new int[] { 1462, 5541, 13638 }, Tile.of(2400, 4835, 0), Tile.of(2868, 3017, 0), 2460, 2473),
		LAW(54, new int[] { 1458, 5545, 13639 }, Tile.of(2464, 4818, 0), Tile.of(2857, 3380, 0), 2459, 2472),
		DEATH(65, new int[] { 1456, 5547, 13640 }, Tile.of(2208, 4830, 0), Tile.of(1862, 4639, 0), 2462, 2475),
		BLOOD(77, new int[] { 1450, 5549, 13641 }, Tile.of(2468, 4889, 1), Tile.of(3561, 9779, 0), 2464, 2477);

		private final int level;
		private final int[] talisman;
		private final Tile inside;
		private final Tile outside;
		private final int objectId;
		private final int portal;

		Ruins(int level, int[] talisman, Tile inside, Tile outside, int objectId, int portal) {
			this.level = level;
			this.talisman = talisman;
			this.inside = inside;
			this.outside = outside;
			this.objectId = objectId;
			this.portal = portal;
		}

		public int getLevel() {
			return level;
		}

		public int getTalisman() {
			return talisman[0];
		}

		public int getTiara() {
			return talisman[1];
		}

		public int getStaff() {
			return talisman[2];
		}
		
		public boolean canEnter(Player player, boolean teleport) {
			return switch(this) {
				case COSMIC -> {
					if (!player.isQuestComplete(Quest.LOST_CITY, "for the ruin to respond."))
						yield false;
					if (teleport)
						player.delayLock(1, () -> player.setNextTile(inside));
					yield true;
				}
				case BLOOD -> {
					if (!player.isQuestComplete(Quest.LEGACY_OF_SEERGAZE, "for the ruin to respond."))
						yield false;
					if (teleport)
						player.delayLock(1, () -> player.setNextTile(inside));
					yield true;
				}
				default -> {
					if (teleport)
						player.delayLock(1, () -> player.setNextTile(inside));
					yield true;
				}
			};
		}

		public Tile getOutside() {
			return outside;
		}

		public int getObjectId() {
			return objectId;
		}

		public int getPortal() {
			return portal;
		}

	}

	public static boolean checkItems(Player player, Ruins ruins) {
		if ((player.getInventory().containsOneItem(WICKED_HOOD) || player.getEquipment().getHatId() == WICKED_HOOD))
			for (WickedHoodRune r : WickedHoodRune.values())
				if (r.getTalismanId() == ruins.getTalisman())
					if (player.hasWickedHoodTalisman(r))
						return true;
		if (player.getInventory().containsItem(ruins.getTalisman(), 1) || player.getInventory().containsItem(OMNI_TALISMAN, 1) || player.getEquipment().getHatId() == ruins.getTiara() || player.getEquipment().getHatId() == OMNI_TIARA)
			return true;
		if (player.getEquipment().getWeaponId() == ruins.getStaff() || player.getEquipment().getWeaponId() == OMNI_TALISMAN_STAFF)
			return true;
		return false;
	}

	//	public static ItemOnObjectHandler craft = new ItemOnObjectHandler(new Object[] { AIR_ALTAR, WATER_ALTAR, EARTH_ALTAR, FIRE_ALTAR }, null, e -> {
//		Player player = e.getPlayer();
//		for (CombinationRunes cr : CombinationRunes.values()) {
//			for (int i = 0; i < cr.altars.length; i++) {
//				if (e.getObject().getId() == cr.altars[i] || (e.getItem().getId() != cr.getTalismans()[i] && e.getItem().getId() != cr.getRunes()[i])) {
//					continue;
//				}
//				if (player.getSkills().getLevel(Constants.RUNECRAFTING) < cr.getLevel() || player.getInventory().getItems().getNumberOf(cr.getTalismans()[i]) <= 0) {
//					continue;
//				}
//
//				int pureEss = player.getInventory().getItems().getNumberOf(Runecrafting.PURE_ESS);
//				int inputRune = player.getInventory().getItems().getNumberOf(cr.getRunes()[i]);
//				if (pureEss == 0) {
//					player.simpleDialogue("You don't have enough pure essence.");
//					return;
//				}
//				if (inputRune == 0) {
//					player.simpleDialogue("You don't have enough " + ItemDefinitions.getDefs(cr.getRunes()[i]).getName() + "s.");
//					return;
//				}
//
//				if (!player.isCastMagicImbue())
//					player.getInventory().deleteItem(cr.getTalismans()[i], 1);
//
//				int maxCraftable = Math.min(inputRune, pureEss);
//				player.getInventory().deleteItem(Runecrafting.PURE_ESS, maxCraftable);
//				player.getInventory().deleteItem(cr.getRunes()[i], maxCraftable);
//				player.setNextSpotAnim(new SpotAnim(186));
//				player.setNextAnimation(new Animation(791));
//				player.lock(5);
//
//				double xp = cr.getXP()[i];
//				if (Runecrafting.hasRcingSuit(player))
//					xp *= 1.025;
//
//				String runeName = ItemDefinitions.getDefs(cr.getCombinationRune()).getName();
//				if (player.getEquipment().getAmuletId() == BINDING_NECKLACE) {
//					player.sendMessage("You bind the temple's power into " + runeName + "s.");
//					player.bindingNecklaceCharges--;
//					if (player.bindingNecklaceCharges <= 0) {
//						player.getEquipment().deleteSlot(Equipment.NECK);
//						player.sendMessage("Your binding necklace disintegrates.");
//						player.bindingNecklaceCharges = 15;
//					}
//					return;
//				} else {
//					player.sendMessage("You attempt to bind " + runeName + "s.");
//					maxCraftable /= 2;
//				}
//				player.getInventory().addItem(cr.getCombinationRune(), maxCraftable);
//				player.getSkills().addXp(Constants.RUNECRAFTING, xp * maxCraftable);
//			}
//		}
//	});



 	public static ItemOnObjectHandler handleAtlarUse = new ItemOnObjectHandler(new Object[] { 2478, 2479, 2480, 2481, 2482, 2483, 2484, 2485, 2486, 2487, 2488, 30624, 7936 }, null, e -> {
		Player player = e.getPlayer();
		RunecraftingTalisman talisman = null;
		if (e.getItem().getId() == PURE_ESS) {
			if (player.getInventory().containsItem(RCRune.AIR.getId()))
				talisman = RunecraftingTalisman.AIR;
			else if (player.getInventory().containsItem(RCRune.WATER.getId()))
				talisman = RunecraftingTalisman.WATER;
			else if (player.getInventory().containsItem(RCRune.EARTH.getId()))
				talisman = RunecraftingTalisman.EARTH;
			else if (player.getInventory().containsItem(RCRune.FIRE.getId()))
				talisman = RunecraftingTalisman.FIRE;
			else {
				RCRune rune = switch (e.getObjectId()) {
					case 2478 -> RCRune.AIR;
					case 2480 -> RCRune.WATER;
					case 2481 -> RCRune.EARTH;
					case 2482 -> RCRune.FIRE;
					default -> null;
				};

				if (rune != null) {
					Runecrafting.runecraft(player, rune, false);
				}
			}
		} else {
			talisman = RunecraftingTalisman.forItemId(e.getItem().getId());
		}
		if (talisman == null) {
			return;
		}
		AltarCombination combination = AltarCombination.getCombinationForTalisman(talisman, e.getObjectId());
		if (combination != null) {
			if (Runecrafting.craftCombinationRune(player, combination))
				return;
		}
		Runecrafting.craftTalisman(player, talisman);
	});

	public static ObjectClickHandler handleExitEssMines = new ObjectClickHandler(new Object[] { 2273 }, e -> {
		if (e.getPlayer().lastEssTele != null)
			e.getPlayer().setNextTile(e.getPlayer().lastEssTele);
		else
			e.getPlayer().sendMessage("Couldn't lock on to your entry path to safely exit you. Teleport out another way.");
	});

	public static ObjectClickHandler handleEntrances = new ObjectClickHandler(new Object[] { 2452, 2453, 2454, 2455, 2456, 2457, 2458, 2461, 2460, 2459, 2462, 2464 }, e -> {
		Ruins ruins = null;
		for (Ruins altars : Ruins.values())
			if (e.getObjectId() == altars.getObjectId()) {
				ruins = altars;
				break;
			}
		if (ruins != null) {
			e.getPlayer().sendMessage("You touch the mysterious ruin...");
			final Ruins alterRuins = ruins;
			WorldTasks.delay(2, () -> {
				if (checkItems(e.getPlayer(), alterRuins) && alterRuins.canEnter(e.getPlayer(), true))
					e.getPlayer().sendMessage("...and you appear within the ruin!");
				else
					e.getPlayer().sendMessage("...and nothing happens...");
			});
		}
	});

	public static ObjectClickHandler handleExitPortals = new ObjectClickHandler(new Object[] { 2465, 2466, 2467, 2468, 2469, 2470, 2471, 2474, 2473, 2472, 2475, 2477 }, e -> {
		if (e.getObject().getDefinitions().getName().equals("Portal")) {
			Ruins ruins = null;
			for (Ruins altars : Ruins.values())
				if (e.getObjectId() == altars.getPortal()) {
					ruins = altars;
					break;
				}
			if (ruins != null) {
				e.getPlayer().setNextTile(ruins.getOutside());
				if (ruins.name().equals(Ruins.CHAOS.name()))
					e.getPlayer().getControllerManager().startController(new WildernessController());
			}
		}
	});

	public static NPCClickHandler handleOthers = new NPCClickHandler(new Object[] { 171, 300, 462, 844, 5913 }, new String[] { "Teleport" }, e -> {
		if (!e.getPlayer().isQuestComplete(Quest.RUNE_MYSTERIES)) {
			e.getPlayer().sendMessage("You have no idea where this mage might take you if you try that.");
			return;
		}
		handleEssTele(e.getPlayer(), e.getNPC());
	});

	public static void handleEssTele(Player player, NPC npc) {
		npc.setNextForceTalk(new ForceTalk("Senventior Disthine Molenko!"));
		npc.resetWalkSteps();
		npc.faceEntity(player);
		npc.setNextAnimation(new Animation(722));
		npc.setNextSpotAnim(new SpotAnim(108, 0, 96));
		player.lock();
		WorldTasks.scheduleTimer(0, 0, tick -> {
			switch(tick) {
				case 0 -> World.sendProjectile(npc, player, 109, 5, 5, 5, 0.6, 5, 0);
				case 1 -> player.setNextSpotAnim(new SpotAnim(110, 35, 96));
				case 3 -> {
					if (player.getMiniquestStage(Miniquest.ENTER_THE_ABYSS) == EnterTheAbyss.SCRYING_ORB) {
						if (player.getInventory().containsItem(5519, 1)) {
							Item item = player.getInventory().getItemById(5519);
							if (item != null) {
								ArrayList<Double> visited = item.getMetaDataO("visited");
								if (visited == null)
									visited = new ArrayList<>();
								if (!visited.contains((double) npc.getId()))
									visited.add((double) npc.getId());
								item.setMetaDataO("visited", visited);
								player.sendMessage("The orb in your inventory glows as it absorbs the teleport information. It contains " + visited.size() + " locations.");
								if (visited.size() >= 3) {
									item.setId(5518);
									item.deleteMetaData();
									player.getInventory().refresh();
									player.sendMessage("The orb in your inventory glows brightly. It looks like it's gathered enough information.");
								}
							}
						}
					}
					player.unlock();
					player.lastEssTele = Tile.of(player.getTile());
					player.setNextTile(Tile.of(2911, 4832, 0));
					return false;
				}
			}
			return true;
		});
	}
}
