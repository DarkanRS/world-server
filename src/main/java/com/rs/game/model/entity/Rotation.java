package com.rs.game.model.entity;

import com.rs.lib.game.Tile;

public class Rotation {
	private int data;
	private int mask;
	
	public Rotation() {
		data = -1;
	}
	
	public Rotation(Entity entity) {
		data = entity.getClientIndex();
	}
	
	public Rotation(Tile tile) {
		data |= -0x40000000;
		data += tile.getY() + (tile.getX() << 14);
	}
	
	public Rotation enable(int... slots) {
		for (int i : slots)
			mask |= (1 << i);
		return this;
	}

	public Rotation enableAll() {
		enable(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);
		return this;
	}
	
	public int getData() {
		return data;
	}

	public int getMask() {
		return mask;
	}
}
