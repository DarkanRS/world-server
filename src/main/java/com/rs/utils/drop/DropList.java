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
package com.rs.utils.drop;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.util.Rational;
import com.rs.lib.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DropList {

	private static double MAX_ROLL = Math.nextDown(1.0);

	private List<DropEntry> drops = new ArrayList<>();
	private double nothingRate = 0.0;
	private boolean overflowed;
	private double overflow;

	public DropList(DropTable... tables) {
		this(new ArrayList<>(Arrays.asList(tables)));
	}

	public DropList(List<DropTable> tables) {
		double curr = 0.0;
		tables.sort((o1, o2) -> {
			if ((o1 == null) || (o2 == null)) return Integer.MAX_VALUE;
			return Double.compare(o1.chance / o1.outOf, o2.chance / o2.outOf);
		});
		for (DropTable table : tables) {
			if (table == null)
				continue;
			double rate = table.getRate();
			if (rate == 0.0) {
				drops.add(new DropEntry(table));
				continue;
			}
			drops.add(new DropEntry(table, curr, curr + rate));
			curr += rate;
			if (curr > 1.0000000000000009) {
				overflowed = true;
				overflow = curr - 1.0;
				System.err.println("Drop rate overflow for table: " + curr + ", " + table.toString());
			}
		}
		double emptySlots = 1.0-curr;
		nothingRate = emptySlots;
		drops.add(new DropEntry(null, curr, curr+emptySlots));
	}

	public boolean isOverflowed() {
		return overflowed;
	}

	public double getOverflow() {
		return overflow;
	}

	public String getNothingFracString() {
		return Rational.toRational(nothingRate).toString();
	}

	public double getNothingRate() {
		return Utils.round(nothingRate, 10);
	}

	public List<Item> genDrop() {
		return genDrop(1.0);
	}

	public List<Item> genDrop(double modifier) {
		return genDrop(null, modifier);
	}

	public List<DropEntry> getDrops() {
		return drops;
	}

	public List<Item> genDrop(Player killer, double modifier) {
		List<Item> finals = new ArrayList<>();

		modifier *= Settings.getConfig().getDropModifier();

		double roll = Utils.clampD(Utils.randomD() * modifier, -100, MAX_ROLL);
		for (DropEntry drop : drops) {
			if ((!drop.isAlways() && roll < drop.getMin()) || (!drop.isAlways() && roll >= drop.getMax()))
				continue;
			DropTable table = drop.getTable();
			if (table == null)
				continue;
			if (table.getRollTable() != null) {
				if (killer != null)
					switch(table.getRollTable().getNames()[0]) {
					case "rdt_gem":
						killer.incrementCount("Gem drop table drops");
						if (killer.getEquipment().getRingId() != -1 && ItemDefinitions.getDefs(killer.getEquipment().getRingId()).getName().toLowerCase().contains("ring of wealth"))
							killer.sendMessage("<col=FACC2E>Your ring of wealth shines brightly!");
						break;
					case "rdt_standard":
						killer.incrementCount("Rare drop table drops");
						if (killer.getEquipment().getRingId() != -1 && ItemDefinitions.getDefs(killer.getEquipment().getRingId()).getName().toLowerCase().contains("ring of wealth"))
							killer.sendMessage("<col=FACC2E>Your ring of wealth shines brightly!");
						break;
					case "rdt_mega_rare":
						killer.incrementCount("Mega rare drop table drops");
						if (killer.getEquipment().getRingId() != -1 && ItemDefinitions.getDefs(killer.getEquipment().getRingId()).getName().toLowerCase().contains("ring of wealth"))
							killer.sendMessage("<col=FACC2E>Your ring of wealth shines brightly!");
						break;
					}
				finals.addAll(table.getRollTable().getDropList().genDrop(modifier));
				continue;
			}
			if (table.isDropOne()) {
				Drop d = table.getDrops()[Utils.random(table.getDrops().length)];
				if (d.getRollTable() == null)
					finals.add(d.toItem());
				else
					for(int i = 0;i < d.getAmount();i++)
						finals.addAll(d.getRollTable().getDropList().genDrop(modifier));
			} else
				for (Drop d : table.getDrops())
					if (d.getRollTable() == null)
						finals.add(d.toItem());
					else
						for(int i = 0;i < d.getAmount();i++)
							finals.addAll(d.getRollTable().getDropList().genDrop(modifier));
		}
		return finals;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (DropEntry d : drops)
			str.append(d + "\n");
		return str.toString();
	}

}
