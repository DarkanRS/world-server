package com.rs.utils.drop;

import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.utils.DropSets;

public class Drop {
	
	private int id = -1;
	private int min;
	private int max;
	private String rollTable;
	
	public Drop(String rollTable) {
		this.rollTable = rollTable;
	}
	
	public Drop(int itemId, int min, int max) {
		this.id = itemId;
		this.min = min;
		this.max = max;
	}
	
	public Drop(int itemId, int amount) {
		this.id = itemId;
		this.min = amount;
		this.max = amount;
	}
	
	public Drop(int itemId) {
		this.id = itemId;
		this.min = 1;
		this.max = 1;
	}
	
	public Item toItem() {
		return new Item(id, getAmount());
	}
	
	public int getId() {
		return id;
	}
	
	public int getAmount() {
		int amt = Utils.random(min, max+1);
		if (max < 2)
			amt = 1;
		return amt;
	}

	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}

	public DropSet getRollTable() {
		return rollTable == null ? null : DropSets.getDropSet(rollTable);
	}
	
	@Override
	public String toString() {
		return "[" + id + ", " + min + ", " + max + "]";
	}
}
