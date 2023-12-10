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

import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Rune {
	//varbit 4540 = 2, 3 || varbit 5493 != 0 activates elemental/catalytic miniegame runes
	AIR(556, 17780, 16091, 4697, 4695, 4696, 20941, 12850),
	WATER(555, 17781, 16092, 4694, 4695, 4698, 20940, 12850),
	EARTH(557, 17782, 16093, 4696, 4699, 4698, 20942, 12850),
	FIRE(554, 17783, 16094, 4694, 4697, 4699, 20939, 12850),
	BODY(559, 17788, 16099, 20944, 12851),
	MIND(558, 17784, 16095, 20943, 12851),
	COSMIC(564, 17789, 16100, 12851),
	CHAOS(562, 17785, 16096, 20947, 12851),
	NATURE(561, 17791, 16102, 20946, 12851), //18341 and varc 1234 = nature rune count inside staff
	DEATH(560, 17786, 16097, 20945, 12851),
	BLOOD(565, 17787, 16098, 12851),
	SOUL(566, 17793, 16104, 12851),
	ASTRAL(9075, 17790, 16101, 12851),
	LAW(563, 17792, 16103, 20948, 12851),
	ARMADYL(21773, 12851),
	STEAM(4694),
	MIST(4695),
	DUST(4696),
	SMOKE(4697),
	MUD(4698),
	LAVA(4699);

	private final static Map<Integer, Rune> MAP = new HashMap<>();

	static {
		for (Rune r : Rune.values())
			MAP.put(r.id, r);
	}

	public static Rune forId(int itemId) {
		return MAP.get(itemId);
	}

	private final int[] runeIds;
	private final int id;

	Rune(int... runeIds) {
		this.runeIds = runeIds;
		id = runeIds[0];
	}

	public boolean hasInfinite(Player player) {
		if (player.getEquipment().getWeaponId() == 24457)
			return true;
		switch(this) {
		case AIR:
			switch(player.getEquipment().getWeaponId()) {
			case 1381:
			case 23044:
			case 15598:
			case 17009:
			case 17011:
			case 16169:
			case 16170:
			case 1397:
			case 1405:
			case 19327:
			case 21777:
			case 21490:
			case 21496:
			case 21500:
				return true;
			}
			break;
		case WATER:
			if (player.getEquipment().getShieldId() == 18346)
				return true;
			switch(player.getEquipment().getWeaponId()) {
			case 1383:
			case 23045:
			case 16997:
			case 16999:
			case 16163:
			case 16164:
			case 1395:
			case 1403:
			case 6562:
			case 6563:
			case 11736:
			case 11738:
			case 19325:
			case 21491:
			case 21495:
			case 21499:
			case 21506:
			case 21507:
			case 21504:
			case 21505:
				return true;
			}
			break;
		case EARTH:
			switch(player.getEquipment().getWeaponId()) {
			case 1385:
			case 23046:
			case 17001:
			case 17003:
			case 16165:
			case 16166:
			case 1399:
			case 1407:
			case 3053:
			case 3054:
			case 6562:
			case 6563:
			case 19329:
			case 21492:
			case 21497:
			case 21501:
			case 21502:
			case 21503:
			case 21504:
			case 21505:
				return true;
			}
			break;
		case FIRE:
			switch(player.getEquipment().getWeaponId()) {
			case 1387:
			case 23047:
			case 17005:
			case 17007:
			case 16167:
			case 16168:
			case 1393:
			case 1401:
			case 3053:
			case 3054:
			case 11736:
			case 11738:
			case 19323:
			case 21493:
			case 21494:
			case 21498:
			case 21502:
			case 21503:
			case 21506:
			case 21507:
				return true;
			}
			break;
		default:
			return false;
		}
		return false;
	}

	public int id() {
		return id;
	}

	public List<Item> getRunesToDelete(Player player, int num) {
		List<Item> runes = new ArrayList<>();
		if (hasInfinite(player))
			return runes;
		int total = 0;
		for (int runeId : runeIds) {
			int numHeld = player.getInventory().getNumberOf(runeId);
			int numNeeded = num-total;
			if (numHeld > 0) {
				if (numHeld >= numNeeded) {
					runes.add(new Item(runeId, numNeeded));
					return runes;
				}
				runes.add(new Item(runeId, numHeld));
			}
		}
		return null;
	}
}
