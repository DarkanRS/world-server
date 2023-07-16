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
package com.rs.game.content.skills.crafting.urns;

import com.rs.game.content.skills.magic.Rune;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;

import java.util.HashMap;
import java.util.Map;

public enum Urn {
	CRACKED_SMELTING(20271, new Animation(6384), new Animation(4580), Constants.SMITHING, 4, 200, 14, 23.1),
	FRAGILE_SMELTING(20277, new Animation(6385), new Animation(6380), Constants.SMITHING, 17, 312.5, 16, 31.8),
	SMELTING(20283, new Animation(6386), new Animation(6381), Constants.SMITHING, 35, 750, 29, 42),
	STRONG_SMELTING(20289, new Animation(6387), new Animation(6382), Constants.SMITHING, 49, 1250, 36, 70),
	CRACKED_WOODCUTTING(20295, new Animation(10279), new Animation(8713), Constants.WOODCUTTING, 4, 800, 15.4, 23.1),
	FRAGILE_WOODCUTTING(20301, new Animation(10280), new Animation(8727), Constants.WOODCUTTING, 15, 2125, 20, 30),
	WOODCUTTING(20307, new Animation(10281), new Animation(8729), Constants.WOODCUTTING, 44, 4125, 32, 48),
	STRONG_WOODCUTTING(20313, new Animation(10828), new Animation(8730), Constants.WOODCUTTING, 61, 8312.5, 38.8, 58.2),
	CRACKED_FISHING(20319, new Animation(6474), new Animation(6394), Constants.FISHING, 2, 750, 12, 18),
	FRAGILE_FISHING(20325, new Animation(6475), new Animation(6463), Constants.FISHING, 15, 1750, 20, 30),
	FISHING(20331, new Animation(6769), new Animation(6471), Constants.FISHING, 41, 2500, 31.2, 46.8),
	STRONG_FISHING(20337, new Animation(6770), new Animation(6472), Constants.FISHING, 53, 3000, 36, 54),
	DECORATED_FISHING(20343, new Animation(6789), new Animation(6473), Constants.FISHING, 76, 9500, 48, 72),
	CRACKED_COOKING(20349, new Animation(8649), new Animation(6794), Constants.COOKING, 2, 2000, 12, 18),
	FRAGILE_COOKING(20355, new Animation(8651), new Animation(6795), Constants.COOKING, 12, 2750, 16, 24),
	COOKING(20361, new Animation(8652), new Animation(7126), Constants.COOKING, 36, 4750, 28.6, 42.9),
	STRONG_COOKING(20367, new Animation(8654), new Animation(7133), Constants.COOKING, 51, 5250, 35, 52.5),
	DECORATED_COOKING(20373, new Animation(8691), new Animation(8629), Constants.COOKING, 81, 7737.5, 52, 78),
	CRACKED_MINING(20379, new Animation(11420), new Animation(10829), Constants.MINING, 1, 437.5, 13, 16.8),
	FRAGILE_MINING(20385, new Animation(11421), new Animation(10830), Constants.MINING, 17, 1000, 20, 31.8),
	MINING(20391, new Animation(11425), new Animation(10831), Constants.MINING, 32, 1625, 28, 40.8),
	STRONG_MINING(20397, new Animation(11447), new Animation(10947), Constants.MINING, 48, 2000, 36, 49.2),
	DECORATED_MINING(20403, new Animation(11448), new Animation(11419), Constants.MINING, 78, 3125, 51, 57),
	IMPIOUS(20409, new Animation(4567), new Animation(4292), Constants.PRAYER, 2, 100, 12, 18),
	ACCURSED(20415, new Animation(4569), new Animation(4541), Constants.PRAYER, 26, 312.5, 25, 37.5),
	INFERNAL(20421, new Animation(4578), new Animation(4542), Constants.PRAYER, 62, 1562.5, 40, 60);

	public static Map<Integer, Urn> NR_IDS = new HashMap<>();
	public static Map<Integer, Urn> FILL_IDS = new HashMap<>();
	public static Map<Integer, Urn> FULL_IDS = new HashMap<>();

	static {
		for (Urn urn : Urn.values()) {
			NR_IDS.put(urn.nrId(), urn);
			FILL_IDS.put(urn.fillId(), urn);
			FULL_IDS.put(urn.fullId(), urn);
		}
	}

	public static Urn forNRId(int id) {
		return NR_IDS.get(id);
	}

	public static Urn forFillId(int id) {
		return FILL_IDS.get(id);
	}

	public static Urn forFullId(int id) {
		return FULL_IDS.get(id);
	}

	private Animation readyAnim, teleAnim;
	private int unfId, skillId, level;
	private double xpToFill, spinXp, fireXp;

	private Urn(int unfId, Animation readyAnim, Animation teleAnim, int skillId, int level, double xpToFill, double spinXp, double fireXp) {
		this.unfId = unfId;
		this.readyAnim = readyAnim;
		this.teleAnim = teleAnim;
		this.skillId = skillId;
		this.level = level;
		this.xpToFill = xpToFill;
		this.spinXp = spinXp;
		this.fireXp = fireXp;
	}

	public Animation getReadyAnim() {
		return readyAnim;
	}

	public Animation getTeleAnim() {
		return teleAnim;
	}

	public int getLevel() {
		return level;
	}

	public int unfId() {
		return unfId;
	}

	public int nrId() {
		return unfId+1;
	}

	public int rId() {
		return unfId+3;
	}

	public int fillId() {
		return unfId+4;
	}

	public int fullId() {
		return unfId+5;
	}

	public Rune getRune() {
		switch(skillId) {
		case Constants.COOKING:
		case Constants.SMITHING:
			return Rune.FIRE;
		case Constants.FISHING:
			return Rune.WATER;
		case Constants.MINING:
		case Constants.WOODCUTTING:
			return Rune.EARTH;
		case Constants.PRAYER:
			return Rune.AIR;
		}
		return Rune.AIR;
	}

	public int getSkill() {
		return skillId;
	}

	public double getFillXp() {
		return xpToFill;
	}

	public double getTeleXp() {
		switch(this) {
		case IMPIOUS:
		case ACCURSED:
		case INFERNAL:
			return xpToFill * 1.20;
		default:
			return xpToFill * 0.20;
		}
	}

	public double getUnfXp() {
		return spinXp;
	}

	public double getFireXp() {
		return fireXp;
	}
}
