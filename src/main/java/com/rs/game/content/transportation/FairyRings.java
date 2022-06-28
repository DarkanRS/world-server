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
package com.rs.game.content.transportation;

import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.world.areas.dungeons.ancientcavern.AncientCavern;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class FairyRings {

	private final static String[][] LETTERS = { { "a", "b", "c", "d" }, { "i", "j", "k", "l" }, { "p", "q", "r", "s" } };
	private final static WorldTile FAIRY_SOURCE = new WorldTile(2412, 4434, 0);
	private final static int FIRST_ANIMATION = 3254, SECOND_ANIMATION = 3255;
	private final static int FIRST_GRAPHICS = 2670, SECOND_GRAPHICS = 2671;

	public static enum Ring {
		DLS(77, "Dungeons: Myreque hideout", new WorldTile(3501, 9821, 0)) {
			@Override
			public boolean meetsRequirements(Player player, boolean silent) {
				return Quest.IN_SEARCH_OF_MYREQUE.meetsRequirements(player, silent ? null : "to use this fairy ring code.");
			}
		},
		DLR(76, "Islands: Poison Waste south of Isafdar", new WorldTile(2213, 3099, 0)),
		DLQ(75, "Kharidian Desert: North of Nardah", new WorldTile(3423, 3016, 0)),
		DLP(74, "Nowhere", null),
		DKS(73, "Kandarin: Snowy Hunter area", new WorldTile(2744, 3719, 0)),
		DKR(72, "Misthalin: Edgeville", new WorldTile(3129, 3496, 0)),
		DKQ(71, "Glacor Cave", new WorldTile(4183, 5726, 0)) {
			@Override
			public boolean meetsRequirements(Player player, boolean silent) {
				return Quest.RITUAL_OF_MAHJARRAT.meetsRequirements(player, silent ? null : "to use this fairy ring code.");
			}
		},
		DKP(70, "Karamja: South of Musa Point", new WorldTile(2900, 3111, 0)),
		DJS(69, "Nowhere", null),
		DJR(68, "Kandarin: Sinclair Mansion (west)", new WorldTile(2676, 3587, 0)),
		DJQ(67, "Nowhere", null),
		DJP(66, "Kandarin: Tower of Life", new WorldTile(2658, 3230, 0)),
		DIS(65, "Misthalin: Wizards' Tower", new WorldTile(3108, 3149, 0)),
		DIR(64, "Other Realms: The Gorak Plane", new WorldTile(3038, 5348, 0)),
		BIQ(31, "Kharidian Desert: Near Kalphite hive", new WorldTile(3251, 3095, 0)),
		BIP(30, "Islands: Polypore Dungeon", new WorldTile(3410, 3324, 0)),
		ALS(29, "Kandarin: McGrubor's Wood", new WorldTile(2644, 3495, 0)),
		ALR(28, "Other Realms: Abyss", new WorldTile(3059, 4875, 0)),
		ALQ(27, "Morytania: Haunted Woods", new WorldTile(3597, 3495, 0)),
		ALP(26, "Kandarin: Feldip Hills", new WorldTile(2468, 4189, 0)) {
			@Override
			public boolean meetsRequirements(Player player, boolean silent) {
				return Quest.FAIRY_TALE_III_BATTLE_AT_ORKS_RIFT.meetsRequirements(player, silent ? null : "to use this fairy ring code.");
			}
		},
		AKS(25, "Feldip Hills: Feldip Hunter area", new WorldTile(2571, 2956, 0)),
		AKR(24, "Nowhere", null),
		AKQ(23, "Kandarin: Piscatoris Hunter area", new WorldTile(2319, 3619, 0)),
		AKP(22, "Nowhere", null),
		AJS(21, "Islands: Penguins near Miscellania", new WorldTile(2500, 3896, 0)),
		AJR(20, "Kandarin: Slayer cave south-east of Rellekka", new WorldTile(2780, 3613, 0)),
		AJQ(19, "Dungeons: Dark cave south of Dorgesh-Kaan", new WorldTile(2735, 5221, 0)) {
			@Override
			public boolean meetsRequirements(Player player, boolean silent) {
				return Quest.DEATH_TO_DORGESHUUN.meetsRequirements(player, silent ? null : "to use this fairy ring code.");
			}
		},
		AJP(18, "Nowhere", null),
		AIS(17, "Nowhere", null),
		AIR(16, "Islands: South of Witchaven", new WorldTile(2700, 3247, 0)),
		AIQ(15, "Asgarnia: Mudskipper Point", new WorldTile(2996, 3114, 0)),
		AIP(14, "Nowhere", null),
		DIQ(63, "Nowhere", null),
		DIP(62, "Mos Le'Harmless: Isle on the coast of Mos Le'Harmless", new WorldTile(3763, 2930, 0)) {
			@Override
			public boolean meetsRequirements(Player player, boolean silent) {
				return Quest.FAIRY_TALE_III_BATTLE_AT_ORKS_RIFT.meetsRequirements(player, silent ? null : "to use this fairy ring code.");
			}
		},
		CLS(61, "Islands: Jungle spiders near Yanille", new WorldTile(2682, 3081, 0)),
		CLR(60, "Ape Atoll: West of the Ape Atoll Agility Course", new WorldTile(2735, 2742, 0)) {
			@Override
			public boolean meetsRequirements(Player player, boolean silent) {
				return Quest.FAIRY_TALE_III_BATTLE_AT_ORKS_RIFT.meetsRequirements(player, silent ? null : "to use this fairy ring code.");
			}
		},
		CLQ(59, "Nowhere", null),
		CLP(58, "Islands: South of Draynor Village", new WorldTile(3082, 3206, 0)),
		CKS(57, "Morytania: Canifis", new WorldTile(3447, 3470, 0)),
		CKR(56, "Karamja: South of Tai Bwo Wannai Village", new WorldTile(2801, 3003, 0)),
		CKQ(55, "Nowhere", null),
		CKP(54, "Other Realms: Cosmic Entity's plane", new WorldTile(2075, 4848, 0)),
		CJS(53, "Nowhere", null),
		CJR(52, "Kandarin: Sinclair Mansion (east)", new WorldTile(2705, 3576, 0)),
		CJQ(51, "Nowhere", null),
		CJP(50, "Nowhere", null),
		CIS(49, "Nowhere", null),
		CIR(48, "Nowhere", null),
		CIQ(47, "Kandarin: North-west of Yanille", new WorldTile(2528, 3127, 0)),
		CIP(46, "North-west Miscellania", new WorldTile(2513, 3884, 0)) {
			@Override
			public boolean meetsRequirements(Player player, boolean silent) {
				return Quest.FREMENNIK_TRIALS.meetsRequirements(player, silent ? null : "to use this fairy ring code.");
			}
		},
		BLS(45, "Nowhere", null),
		BLR(44, "Kandarin: Legend's Guild", new WorldTile(2740, 3351, 0)),
		BLQ(43, "Yu'biusk", new WorldTile(2228, 4244, 1)) {
			@Override
			public boolean meetsRequirements(Player player, boolean silent) {
				return Quest.CHOSEN_COMMANDER.meetsRequirements(player, silent ? null : "to use this fairy ring code.");
			}
		},
		BLP(42, "Dungeons: TzHaar area", new WorldTile(4622, 5147, 0)),
		BKS(41, "Nowhere", null),
		BKR(40, "Morytania: Mort Myre", new WorldTile(3469, 3431, 0)),
		BKQ(39, "Other Realms: Enchanted Valley", new WorldTile(3041, 4532, 0)),
		BKP(38, "Feldip Hills: South of Castle Wars", new WorldTile(2385, 3035, 0)),
		BJS(37, "Nowhere", null),
		BJR(36, "Fisher Realm", new WorldTile(2650, 4730, 0)) {
			@Override
			public boolean meetsRequirements(Player player, boolean silent) {
				return Quest.HOLY_GRAIL.meetsRequirements(player, silent ? null : "to use this fairy ring code.");
			}
		},
		BJQ(35, "Dungeons: Ancient cavern", new WorldTile(1737, 5342, 0)) {
			@Override
			public boolean meetsRequirements(Player player, boolean silent) {
				return player.getVars().getVarBit(AncientCavern.FIXED_RING_VARBIT) == 1;
			}
		},
		BJP(34, "Nowhere", null),
		BIS(33, "Kandarin: Ardougne Zoo", new WorldTile(2635, 3266, 0)),
		BIR(32, "Sparse Plane", new WorldTile(2455, 4396, 0)) {
			@Override
			public boolean meetsRequirements(Player player, boolean silent) {
				return Quest.FAIRY_TALE_III_BATTLE_AT_ORKS_RIFT.meetsRequirements(player, silent ? null : "to use this fairy ring code.");
			}
		},

		;

		private String name;
		private int logId;
		private WorldTile tile;

		private Ring(int logId, String name, WorldTile tile) {
			this.logId = logId;
			this.name = name;
			this.tile = tile;
		}

		public WorldTile getTile() {
			return tile;
		}

		public int getLogId() {
			return logId;
		}

		public boolean meetsRequirements(Player player, boolean silent) {
			return true;
		}
	}

	public static ObjectClickHandler handleRings = new ObjectClickHandler(new Object[] { "Fairy ring", 27331 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObjectId() == 12094) {//Fairy ring by evil chicken
				sendTeleport(e.getPlayer(), new WorldTile(3202, 3169, 0));
				return;
			}
			if(e.getPlayer().getQuestManager().isComplete(Quest.LOST_CITY)) {
				FairyRings.openRingInterface(e.getPlayer(), e.getObject(), e.getObjectId() == 12128);
				return;
			}
			e.getPlayer().startConversation(new Dialogue().addPlayer(HeadE.FRUSTRATED, "I don't know what's supposed to be happening here..."));
			e.getPlayer().sendMessage("You require The Lost City quest");
		}
	};

	public static ButtonClickHandler handleButtons = new ButtonClickHandler(734) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 21)
				confirmRingHash(e.getPlayer());
			else
				handleDialButtons(e.getPlayer(), e.getComponentId());
		}
	};

	public static ButtonClickHandler handleQuickTeleport = new ButtonClickHandler(735) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() >= 14 && e.getComponentId() <= 14 + 64)
				sendRingTeleport(e.getPlayer(), e.getComponentId() - 14);
		}
	};

	public static boolean checkAll(Player player) {
		if ((player.getQuestManager().isComplete(Quest.FAIRY_TALE_III_BATTLE_AT_ORKS_RIFT) && Quest.FAIRY_TALE_III_BATTLE_AT_ORKS_RIFT.meetsRequirements(player)) || player.getEquipment().getWeaponId() == 772 || player.getEquipment().getWeaponId() == 9084)
			return true;
		player.sendMessage("The fairy ring only works for those who wield fairy magic.");
		return false;
	}

	public static boolean openRingInterface(Player player, WorldTile tile, boolean source) {
		if (checkAll(player)) {
			player.addWalkSteps(tile.getX(), tile.getY(), -1, true);
			if (source) {
				player.getInterfaceManager().sendInterface(734);
				sendTravelLog(player);
				resetRingHash(player);
			} else
				sendTeleport(player, FAIRY_SOURCE);
			return true;
		}
		return false;
	}

	private static void sendTravelLog(Player player) {
		player.getInterfaceManager().sendInventoryInterface(735);
		for (Ring r : Ring.values())
			if (r.meetsRequirements(player, true) && !r.name.equals("Nowhere"))
				player.getPackets().setIFText(735, r.logId, "          " + r.name);
	}

	public static boolean confirmRingHash(Player player) {
		int[] locationArray = player.getTempAttribs().removeO("location_array");
		if (locationArray == null)
			return false;
		StringBuilder string = new StringBuilder();
		int index = 0;
		for (int letterValue : locationArray)
			string.append(LETTERS[index++][letterValue]);
		return sendRingTeleport(player, string.toString().toUpperCase());
	}

	public static boolean sendRingTeleport(Player player, int hash) {
		int letter1 = hash / 16;
		hash -= letter1 * 16;
		int letter2 = hash / 4;
		hash -= letter2 * 4;
		int letter3 = hash;
		StringBuilder string = new StringBuilder();
		string.append(LETTERS[0][letter1]);
		string.append(LETTERS[1][letter2]);
		string.append(LETTERS[2][letter3]);
		return sendRingTeleport(player, string.toString().toUpperCase());
	}

	public static boolean sendRingTeleport(Player player, String hash) {
		Ring ring;
		try {
			ring = Ring.valueOf(hash);
		} catch (Throwable e) {
			ring = null;
		}
		if (ring == null || ring.getTile() == null || !ring.meetsRequirements(player, false)) {
			sendTeleport(player, new WorldTile(FAIRY_SOURCE, 2));
			return false;
		}
		sendTeleport(player, ring.getTile());
		return true;
	}

	private static void resetRingHash(Player player) {
		player.getTempAttribs().setO("location_array", new int[3]);
		for (int i = 0; i < 3; i++)
			player.getVars().setVarBit(2341 + i, 0);
		player.getVars().syncVarsToClient();
	}

	public static void sendTeleport(final Player player, final WorldTile tile) {
		Magic.sendTeleportSpell(player, FIRST_ANIMATION, SECOND_ANIMATION, FIRST_GRAPHICS, SECOND_GRAPHICS, 0, 0, tile, 2, false, Magic.OBJECT_TELEPORT, null);
	}

	public static void handleDialButtons(final Player player, int componentId) {
		int[] locationArray = player.getTempAttribs().getO("location_array");
		if (locationArray == null) {
			player.closeInterfaces();
			return;
		}
		int index = (componentId - 23) / 2;
		if (componentId % 2 == 0)
			locationArray[index]++;
		else
			locationArray[index]--;
		locationArray = getCorrectValues(locationArray);
		player.getTempAttribs().setO("location_array", locationArray);
		for (int i = 0; i < 3; i++)
			player.getVars().setVarBit(2341 + i, locationArray[i] == 1 ? 3 : locationArray[i] == 3 ? 1 : locationArray[i]);
		player.getVars().syncVarsToClient();
	}

	private static int[] getCorrectValues(int[] locationArray) {
		int loop = 0;
		for (int values : locationArray)
			locationArray[loop++] = values & 0x3;
		return locationArray;
	}
}