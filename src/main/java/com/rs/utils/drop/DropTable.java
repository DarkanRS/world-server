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

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.interfaces.IFEvents;
import com.rs.engine.thread.LowPriorityTaskExecutor;
import com.rs.game.model.entity.player.Bank;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.item.ItemsContainer;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.tools.old.CharmDrop;
import com.rs.utils.DropSets;
import com.rs.utils.EffigyDrop;
import com.rs.utils.NPCClueDrops;

import java.util.List;

public class DropTable {

	protected double chance;
	protected double outOf;
	protected boolean dropOne;
	protected Drop[] drops;
	protected String rollTable;

	public DropTable(double chance, double outOf, boolean dropOne, List<Drop> drops) {
		this.chance = chance;
		this.outOf = outOf;
		Drop[] dArr = new Drop[drops.size()];
		drops.toArray(dArr);
		this.drops = dArr;
		this.dropOne = dropOne;
	}

	public DropTable(double chance, double outOf, List<Drop> drops) {
		this.chance = chance;
		this.outOf = outOf;
		Drop[] dArr = new Drop[drops.size()];
		drops.toArray(dArr);
		this.drops = dArr;
		dropOne = true;
	}

	public DropTable(double chance, double outOf, boolean dropOne, Drop... drops) {
		this.chance = chance;
		this.outOf = outOf;
		this.drops = drops;
		this.dropOne = dropOne;
	}

	public DropTable(double chance, double outOf, Drop... drops) {
		this.chance = chance;
		this.outOf = outOf;
		this.drops = drops;
		dropOne = true;
	}

	public DropTable(double chance, double outOf, String rollTable) {
		this.chance = chance;
		this.outOf = outOf;
		this.rollTable = rollTable;
	}

	public DropTable(double chance, double outOf, int itemId, int min, int max) {
		this.chance = chance;
		this.outOf = outOf;
		drops = new Drop[] { new Drop(itemId, min, max) };
	}

	public DropTable(double chance, double outOf, int itemId, int amount) {
		this(chance, outOf, itemId, amount, amount);
	}

	public DropTable(Drop... drops) {
		this(0, 0, true, drops);
	}

	public DropTable(int itemId, int amount) {
		this(0, 0, itemId, amount, amount);
	}

	public DropTable(int itemId, int min, int max) {
		this(0, 0, itemId, min, max);
	}

	public DropTable(WeightedTable table) {
		chance = table.chance;
		drops = table.drops;
		dropOne = table.dropOne;
		outOf = table.outOf;
		rollTable = table.rollTable;
	}

    public static void displayDropsFor(Player player, String tableName, int amount) {
		player.sendMessage("<col=FF0000><shad=000000>Calculating drops...");
		LowPriorityTaskExecutor.execute(() -> {
			long start = System.currentTimeMillis();
			ItemsContainer<Item> dropCollection = new ItemsContainer<>(Bank.MAX_BANK_SIZE, true);

			double modifier = 1.0;
			if (player.getEquipment().wearingRingOfWealth())
				modifier -= 0.01;
			for (int i = 0; i < amount; i++) {
				List<Item> drops = DropSets.getDropSet(tableName).getDropList().genDrop(modifier);
				for (Item item : drops)
					dropCollection.add(item);
			}
			dropCollection.sortByItemId();
			player.getTempAttribs().setB("viewingOtherBank", true);
			player.getVars().setVarBit(8348, 0);
			player.getVars().syncVarsToClient();
			player.getInterfaceManager().sendInterface(762);
			player.getPackets().sendRunScript(2319);
			player.getPackets().setIFText(762, 47, amount + " " + tableName + " rolls");
			player.getPackets().sendItems(95, dropCollection);
			player.getPackets().setIFEvents(new IFEvents(762, 95, 0, 516).enableRightClickOptions(0,1,2,3,4,5,6,9).setDepth(2).enableDrag());
			player.getVars().setVarBit(4893, 1);
			player.getVars().syncVarsToClient();
			player.sendMessage("<col=FF0000><shad=000000>Calculated drops in " + Utils.formatLong(System.currentTimeMillis() - start) + "ms");
		});
    }

    public double getNumerator() {
		return chance;
	}

	public double getDenominator() {
		return outOf;
	}

	public double getRate() {
		return outOf == 0.0 ? 0.0 : chance / outOf;
	}

	public void setChance(int num, int den) {
		chance = num;
		outOf = den;
	}

	public void setChance(double num, double den) {
		chance = num;
		outOf = den;
	}

	public Drop[] getDrops() {
		return drops;
	}

	public DropSet getRollTable() {
		return rollTable == null ? null : DropSets.getDropSet(rollTable);
	}

	public String getRollTableName() {
		return rollTable;
	}

	public Item[] toItemArr() {
		if (dropOne)
			return new Item[] { drops[Utils.random(drops.length)].toItem() };
		Item[] items = new Item[drops.length];
		for (int i = 0;i < items.length;i++)
			items[i] = drops[i].toItem();
		return items;
	}

	public static Item[] calculateDrops(DropSet dropSet) {
		return calculateDrops(null, dropSet);
	}

	public static Item[] calculateDrops(Player killer, DropSet dropSet) {
		if (dropSet == null)
			return new Item[] {};
		double modifier = 1.0;
		if (killer != null)
			if (killer.getEquipment().getRingId() != -1 && ItemDefinitions.getDefs(killer.getEquipment().getRingId()).getName().toLowerCase().contains("ring of wealth"))
				modifier -= 0.01;

		List<Item> drops = dropSet.getDropList().genDrop(killer, modifier);

		Item[] items = new Item[drops.size()];
		for (int i = 0;i < items.length;i++)
			items[i] = drops.get(i);
		return items;
	}

	public boolean isDropOne() {
		return dropOne;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("[ (" + chance + "/" + outOf + ") - ");
		if (rollTable != null)
			return s + rollTable;
		if (drops == null)
			return s + "Nothing";
		for (Drop d : drops)
			s.append("(").append(d.getId()).append("(").append(ItemDefinitions.getDefs(d.getId()).name).append("),").append(d.getMin()).append(",").append(d.getMax()).append(") ]");
		return s.toString();
	}
}
