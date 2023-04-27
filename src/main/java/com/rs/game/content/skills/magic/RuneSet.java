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
import java.util.List;

public class RuneSet {

	private Rune[] runes;
	private int[] amounts;

	public RuneSet() {

	}

	public RuneSet(Rune r1, int num1) {
		runes = new Rune[] { r1 };
		amounts = new int[] { num1 };
	}

	public RuneSet(Rune r1, int num1, Rune r2, int num2) {
		runes = new Rune[] { r1, r2 };
		amounts = new int[] { num1, num2 };
	}

	public RuneSet(Rune r1, int num1, Rune r2, int num2, Rune r3, int num3) {
		runes = new Rune[] { r1, r2, r3 };
		amounts = new int[] { num1, num2, num3 };
	}

	public RuneSet(Rune r1, int num1, Rune r2, int num2, Rune r3, int num3, Rune r4, int num4) {
		runes = new Rune[] { r1, r2, r3, r4 };
		amounts = new int[] { num1, num2, num3, num4 };
	}

	public boolean meetsRequirements(Player player) {
		if (player.getNSV().getB("infRunes"))
			return true;
		if (runes == null)
			return true;
		for (int i = 0;i < runes.length;i++)
			if (runes[i].getRunesToDelete(player, amounts[i]) == null) {
				player.sendMessage("You don't have enough " + runes[i].toString().toLowerCase() + " runes to cast this spell.");
				return false;
			}
		return true;
	}

	public boolean meetsPortalRequirements(Player player) {
		if (runes == null)
			return true;
		if (player.getNSV().getB("infRunes"))
			return true;
		for (int i = 0;i < runes.length;i++)
			if (runes[i].getRunesToDelete(player, amounts[i]) == null) {
				player.sendMessage("You don't have enough " + runes[i].toString().toLowerCase() + " runes to tune your portal to this location.");
				return false;
			}
		return true;
	}

	public List<Item> getRunesToDelete(Player player) {
		List<Item> toDelete = new ArrayList<>();
		if (runes == null || player.getNSV().getB("infRunes"))
			return toDelete;
		for (int i = 0;i < runes.length;i++) {
			List<Item> rDel = runes[i].getRunesToDelete(player, amounts[i]);
			if (rDel != null)
				toDelete.addAll(rDel);
		}
		return toDelete;
	}

	public void deleteRunes(Player caster) {
		List<Item> toDelete = getRunesToDelete(caster);
		for (Item i : toDelete)
			if (i != null)
				caster.getInventory().deleteItem(i);
	}
}
