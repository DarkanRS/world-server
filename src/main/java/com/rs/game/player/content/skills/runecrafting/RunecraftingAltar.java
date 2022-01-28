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
package com.rs.game.player.content.skills.runecrafting;

import com.rs.game.ForceTalk;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.game.player.controllers.WildernessController;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;

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

		private int buttonId;
		private int runeId;
		private int spriteComp;
		private int spriteId;
		private int talismanId;
		private int tiaraId;

		private WickedHoodRune(int runeId, int componentId, int spriteComp, int spriteId, int talismanId, int tiaraId) {
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

	public static ItemClickHandler handleWickedHood = new ItemClickHandler(new Object[] { WICKED_HOOD }, new String[] { "Activate", "Teleport" }) {
		@Override
		public void handle(ItemClickEvent e) {
			if (e.getOption().equals("Activate")) {
				e.getPlayer().getTempAttribs().removeO("whr");
				sendWickedHoodInter(e.getPlayer());
			} else
				Magic.sendNormalTeleportSpell(e.getPlayer(), 0, 0, new WorldTile(3106, 3162, 1));
		}
	};

	public static ButtonClickHandler handleWickedHoodInter = new ButtonClickHandler(WICKED_HOOD_INTER) {
		@Override
		public void handle(ButtonClickEvent e) {
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
								Magic.sendNormalTeleportSpell(e.getPlayer(), 0, 0, Altar.valueOf(selection.name()).inside, null);
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
		}
	};

	public enum Altar {
		MIND(1, new int[] { 1448, 5529, 13631 }, new WorldTile(2793, 4828, 0), new WorldTile(2984, 3515, 0), 2453, 2466),
		AIR(1, new int[] { 1438, 5527, 13630 }, new WorldTile(2841, 4829, 0), new WorldTile(3128, 3407, 0), 2452, 2465),
		WATER(5, new int[] { 1444, 5531, 13632 }, new WorldTile(3494, 4832, 0), new WorldTile(3183, 3164, 0), 2454, 2467),
		EARTH(9, new int[] { 1440, 5535, 13633 }, new WorldTile(2655, 4830, 0), new WorldTile(3306, 3472, 0), 2455, 2468),
		FIRE(14, new int[] { 1442, 5537, 13634 }, new WorldTile(2577, 4846, 0), new WorldTile(3312, 3253, 0), 2456, 2469),
		BODY(20, new int[] { 1446, 5533, 13635 }, new WorldTile(2522, 4833, 0), new WorldTile(3055, 3444, 0), 2457, 2470),
		COSMIC(27, new int[] { 1454, 5539, 13636 }, new WorldTile(2162, 4833, 0), new WorldTile(2408, 4379, 0), 2458, 2471),
		CHAOS(35, new int[] { 1452, 5543, 13637 }, new WorldTile(2281, 4837, 0), new WorldTile(3060, 3589, 0), 2461, 2474),
		ASTRAL(40, new int[] { -1, -1 }, new WorldTile(2156, 3863, 0), new WorldTile(2156, 3863, 0), 17010, -1),
		NATURE(44, new int[] { 1462, 5541, 13638 }, new WorldTile(2400, 4835, 0), new WorldTile(2868, 3017, 0), 2460, 2473),
		LAW(54, new int[] { 1458, 5545, 13639 }, new WorldTile(2464, 4818, 0), new WorldTile(2857, 3380, 0), 2459, 2472),
		DEATH(65, new int[] { 1456, 5547, 13640 }, new WorldTile(2208, 4830, 0), new WorldTile(1862, 4639, 0), 2462, 2475),
		BLOOD(77, new int[] { 1450, 5549, 13641 }, new WorldTile(2468, 4889, 1), new WorldTile(3561, 9779, 0), 2464, 2477);

		private int level;
		private int[] talisman;
		private WorldTile inside;
		private WorldTile outside;
		private int objectId;
		private int portal;

		private Altar(int level, int[] talisman, WorldTile inside, WorldTile outside, int objectId, int portal) {
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

		public WorldTile getInside() {
			return inside;
		}

		public WorldTile getOutside() {
			return outside;
		}

		public int getObjectId() {
			return objectId;
		}

		public int getPortal() {
			return portal;
		}
	}

	public static boolean checkItems(Player player, Altar altar) {
		if ((player.getInventory().containsOneItem(WICKED_HOOD) || player.getEquipment().getHatId() == WICKED_HOOD))
			for (WickedHoodRune r : WickedHoodRune.values())
				if (r.getTalismanId() == altar.getTalisman())
					if (player.hasWickedHoodTalisman(r))
						return true;
		if (player.getInventory().containsItem(altar.getTalisman(), 1) || player.getInventory().containsItem(OMNI_TALISMAN, 1) || player.getEquipment().getHatId() == altar.getTiara() || player.getEquipment().getHatId() == OMNI_TIARA)
			return true;
		if (player.getEquipment().getWeaponId() == altar.getStaff() || player.getEquipment().getWeaponId() == OMNI_TALISMAN_STAFF)
			return true;
		return false;
	}

	public static ObjectClickHandler handleExitEssMines = new ObjectClickHandler(new Object[] { 2273 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().lastEssTele != null)
				e.getPlayer().setNextWorldTile(e.getPlayer().lastEssTele);
			else
				e.getPlayer().sendMessage("Couldn't lock on to your entry path to safely exit you. Teleport out another way.");
		}
	};

	public static ObjectClickHandler handleEntrances = new ObjectClickHandler(new Object[] { 2452, 2453, 2454, 2455, 2456, 2457, 2458, 2461, 2460, 2459, 2462, 2464 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Altar altar = null;
			for (Altar altars : Altar.values())
				if (e.getObjectId() == altars.getObjectId()) {
					altar = altars;
					break;
				}
			if (altar != null) {
				e.getPlayer().sendMessage("You touch the mysterious ruin...");
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						Altar altar = null;
						for (Altar altars : Altar.values())
							if (e.getObjectId() == altars.getObjectId()) {
								altar = altars;
								break;
							}
						if (altar != null && checkItems(e.getPlayer(), altar)) {
							e.getPlayer().sendMessage("...and you appear within the ruin!");
							e.getPlayer().setNextWorldTile(altar.getInside());
						} else
							e.getPlayer().sendMessage("...and nothing happens...");
					}
				}, 2);
			}
		}
	};

	public static ObjectClickHandler handleExitPortals = new ObjectClickHandler(new Object[] { 2465, 2466, 2467, 2468, 2469, 2470, 2471, 2474, 2473, 2472, 2475, 2477 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getDefinitions().getName().equals("Portal")) {
				Altar altar = null;
				for (Altar altars : Altar.values())
					if (e.getObjectId() == altars.getPortal()) {
						altar = altars;
						break;
					}
				if (altar != null) {
					e.getPlayer().setNextWorldTile(altar.getOutside());
					if (altar.name() == Altar.CHAOS.name())
						e.getPlayer().getControllerManager().startController(new WildernessController());
				}
			}
		}
	};

	public static NPCClickHandler handleMageOfZamorak = new NPCClickHandler(2257) {
		@Override
		public void handle(NPCClickEvent e) {
			switch(e.getOption()) {
			case "Trade":
				ShopsHandler.openShop(e.getPlayer(), "zamorak_mage_shop");
				break;
			case "Talk-to":
				Abyss.teleport(e.getPlayer(), e.getNPC());
				break;
			}
		}
	};

	public static NPCClickHandler handleOthers = new NPCClickHandler(462) {
		@Override
		public void handle(NPCClickEvent e) {
			switch(e.getOption()) {
			case "Teleport":
				e.getNPC().setNextForceTalk(new ForceTalk("Senventior Disthine Molenko!"));
				World.sendProjectile(e.getNPC(), e.getPlayer(), 50, 5, 5, 5, 1, 5, 0);
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						e.getPlayer().setNextWorldTile(new WorldTile(2911, 4832, 0));
						e.getPlayer().lastEssTele = new WorldTile(e.getNPC());
					}
				}, 2);
				break;
			}
		}
	};
}
