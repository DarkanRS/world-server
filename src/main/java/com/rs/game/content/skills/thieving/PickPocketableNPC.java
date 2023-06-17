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
package com.rs.game.content.skills.thieving;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;
import com.rs.utils.DropSets;
import com.rs.utils.drop.DropSet;
import com.rs.utils.drop.DropTable;

import java.util.HashMap;
import java.util.Map;

public enum PickPocketableNPC {

	MAN(177, 240,
			new short[] { 1, 2, 3, 4, 5, 6, 16, 24, 25, 170, 5924, 7873, 7874, 7875, 7876, 7877, 7878, 7879, 7880, 7881, 7882, 7883, 7884, 12345, 12346, 12347 },
			new byte[] { 1, 11, 21, 31 },
			new byte[] { 1, 1, 11, 21 },
			8.0, 4, 10,
			new DropTable(995, 3)),

	FARMER(155, 240,
			new short[] { 7, 1757, 1758, 1760 },
			new byte[] { 10, 20, 30, 40 },
			new byte[] { 1, 10, 20, 30 },
			14.5, 4, 10,
			"pp_farmer"),

	FEMALE_HAM(135, 240,
			new short[] { 1715 },
			new byte[] { 15, 25, 35, 45 },
			new byte[] { 1, 15, 25, 35 },
			18.5, 3, 10,
			"pp_femaleham"),

	MALE_HAM(135, 240,
			new short[] { 1714, 1716 },
			new byte[] { 20, 30, 40, 50 },
			new byte[] { 1, 20, 30, 40 },
			22.5, 3, 20,
			"pp_maleham"),

	HAM_GUARD(135, 240,
			new short[] { 1710, 1711, 1712 },
			new byte[] { 20, 30, 40, 50 },
			new byte[] { 1, 20, 30, 40 },
			22.5, 3, 30,
			new DropTable(995, 1, 50)),

	WARRIOR(125, 240,
			new short[] { 15, 18 },
			new byte[] { 25, 35, 45, 55 },
			new byte[] { 1, 25, 35, 45 },
			26, 5, 20,
			new DropTable(995, 18)),

	ROGUE(115, 240,
			new short[] { 187, 2267, 2268, 2269, 8122 },
			new byte[] { 32, 42, 52, 62 },
			new byte[] { 1, 32, 42, 52 },
			35.5, 4, 20,
			new DropTable(995, 20)),

	CAVE_GOBLIN(100, 240,
			new short[] { 5752, 5753, 5754, 5755, 5756, 5757, 5758, 5759, 5760, 5761, 5762, 5763, 5764, 5765, 5766, 5767, 5768 },
			new byte[] { 36, 46, 56, 66 },
			new byte[] { 1, 36, 46, 56 },
			40, 4, 10,
			"pp_cavegoblin"),

	MASTER_FARMER(90, 240,
			new short[] { 2234, 2235, 3299 },
			new byte[] { 38, 48, 58, 68 },
			new byte[] { 1, 38, 48, 58 },
			43, 4, 30,
			"pp_masterfarmer"),

	GUARD(45, 240,
			new short[] { 9, 32, 206, 296, 297, 298, 299, 344, 345, 346, 368, 678, 812, 5919, 5920, 5921, 5922 },
			new byte[] { 40, 50, 60, 70 },
			new byte[] { 1, 40, 50, 60 },
			46.5, 4, 20,
			new DropTable(995, 30)),

	FREMENNIK_CITIZEN(35, 240,
			new short[] { 2462 },
			new byte[] { 45, 55, 65, 75 },
			new byte[] { 1, 45, 55, 65 },
			65, 4, 20,
			new DropTable(995, 40)),

	DESERT_PHOENIX(35, 240,
			new short[] { 1911 },
			new byte[] { 25, 127, 127, 127 },
			new byte[] { 1, 1, 1, 1 }, 26,
			new Animation(-1), 4, 20,
			new DropTable(4621, 1)),

	ARDOUGNE_KNIGHT(41, 240,
			new short[] { 23, 26 },
			new byte[] { 55, 65, 75, 85 },
			new byte[] { 1, 55, 65, 75 },
			84.3, 4, 30,
			new DropTable(995, 50)),

	BANDIT(35, 240,
			new short[] { 5050 },
			new byte[] { 55, 65, 75, 85 },
			new byte[] { 1, 55, 65, 75 },
			84.3, 4, 50,
			new DropTable(995, 50)),

	WATCHMEN_MENAPHITES(17, 156,
			new short[] { 1905 },
			new byte[] { 65, 75, 85, 95 },
			new byte[] { 1, 65, 75, 85 },
			137.5, 4, 50,
			new DropTable(995, 60)),

	PALADIN(17, 150,
			new short[] { 20, 2256 },
			new byte[] { 70, 80, 90, 100 },
			new byte[] { 1, 70, 80, 90 },
			151.75, 4, 30,
			"pp_paladin"),

	MONKEY_KNIFE_FIGHTER(15, 200,
			new short[] { 13195, 13212, 13213 },
			new byte[] { 70, 127, 127, 127 },
			new byte[] { 1, 1, 1, 1 },
			150, 4, 10,
			"pp_mkf"),

	GNOME(13, 120,
			new short[] { 66, 67, 68, 168, 169, 2249, 2250, 2251, 2371, 2649, 2650, 6002, 6004 },
			new byte[] { 75, 85, 95, 105 },
			new byte[] { 1, 75, 85, 95 },
			198.5, new Animation(191), 4, 10,
			"pp_gnome"),

	HERO(13, 120,
			new short[] { 21 },
			new byte[] { 80, 90, 100, 110 },
			new byte[] { 1, 80, 90, 100 },
			275, 5, 40,
			"pp_hero"),

	ELF(6, 98,
			new short[] { 2364, 2365, 2366 },
			new byte[] { 85, 95, 105, 115 },
			new byte[] { 1, 85, 95, 105 },
			353, 10, 50,
			new DropTable(995, 1, 50)),

	DWARF_TRADER(1, 150,
			new short[] { 2109, 2110, 2111, 2112, 2113, 2114, 2115, 2116, 2117, 2118, 2119, 2120, 2121, 2122, 2123, 2124, 2125, 2126 },
			new byte[] { 90, 100, 110, 120 },
			new byte[] { 1, 90, 100, 110 },
			556.5, 6, 10,
			"pp_dwarftrader");

	private static final Map<Short, PickPocketableNPC> NPCS = new HashMap<>();

	public static PickPocketableNPC get(int id) {
		return NPCS.get((short) id);
	}

	static {
		for (PickPocketableNPC data : PickPocketableNPC.values())
			for (short id : data.npcIds)
				NPCS.put(id, data);
	}

	private int rate1, rate99;
	private short[] npcIds;
	private byte[] thievingLevels;
	private byte[] agilityLevels;
	private double experience;
	private byte stunTime;
	private byte stunDamage;
	private String table;
	private DropSet loot;
	private Animation stunAnimation = null;

	private PickPocketableNPC(int rate1, int rate99, short[] npcIds, byte[] thievingLevel, byte[] agilityLevel, double experience, int stunTime, int stunDamage, DropTable... loot) {
		this.rate1 = rate1;
		this.rate99 = rate99;
		this.npcIds = npcIds;
		thievingLevels = thievingLevel;
		agilityLevels = agilityLevel;
		this.experience = experience;
		this.stunTime = (byte) stunTime;
		this.stunDamage = (byte) stunDamage;
		if (loot.length > 0)
			this.loot = new DropSet(loot);
	}

	private PickPocketableNPC(int rate1, int rate99, short[] npcIds, byte[] thievingLevel, byte[] agilityLevel, double experience, int stunTime, int stunDamage, String table) {
		this(rate1, rate99, npcIds, thievingLevel, agilityLevel, experience, stunTime, stunDamage);
		this.table = table;
	}

	private PickPocketableNPC(int rate1, int rate99, short[] npcIds, byte[] thievingLevel, byte[] agilityLevel, double experience, Animation stunAnimation, int stunTime, int stunDamage, DropTable... loot) {
		this.rate1 = rate1;
		this.rate99 = rate99;
		this.npcIds = npcIds;
		thievingLevels = thievingLevel;
		agilityLevels = agilityLevel;
		this.experience = experience;
		this.stunTime = (byte) stunTime;
		this.stunDamage = (byte) stunDamage;
		this.stunAnimation = stunAnimation;
		this.loot = new DropSet(loot);
	}

	private PickPocketableNPC(int rate1, int rate99, short[] npcIds, byte[] thievingLevel, byte[] agilityLevel, double experience, Animation stunAnimation, int stunTime, int stunDamage, String table) {
		this(rate1, rate99, npcIds, thievingLevel, agilityLevel, experience, stunAnimation, stunTime, stunDamage);
		this.table = table;
	}

	public short[] getNpcIds() {
		return npcIds;
	}

	public byte[] getThievingLevels() {
		return thievingLevels;
	}

	public byte[] getAgilityLevels() {
		return agilityLevels;
	}

	public double getExperience() {
		return experience;
	}

	public byte getStunTime() {
		return stunTime;
	}

	public byte getStunDamage() {
		return stunDamage;
	}

	public DropSet getLoot() {
		if (table != null)
			return DropSets.getDropSet(table);
		return loot;
	}

	public Animation getStunAnimation() {
		return stunAnimation;
	}

	public boolean rollSuccess(Player player) {
		return Utils.skillSuccess(player.getSkills().getLevel(Constants.THIEVING), player.getAuraManager().getThievingMul() + (hasArdyCloak(player) ? 0.1 : 0.0), rate1, rate99);
	}

	public static boolean hasArdyCloak(Player player) {
		switch(player.getEquipment().getCapeId()) {
		case 15349:
		case 19748:
		case 9777:
		case 9778:
			return true;
		default:
			return false;
		}
	}
}
