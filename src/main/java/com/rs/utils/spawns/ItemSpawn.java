package com.rs.utils.spawns;

import com.rs.game.World;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;

public class ItemSpawn {

	private String comment;
	private int itemId;
	private int amount;
	private WorldTile tile;
	
	public ItemSpawn(int itemId, int amount, WorldTile tile, String comment) {
		this.itemId = itemId;
		this.amount = amount;
		this.tile = tile;
		this.comment = comment;
	}
	
	@SuppressWarnings("deprecation")
	public void spawn() {
		World.addGroundItemForever(new Item(itemId, amount), tile);
	}
	
	public WorldTile getTile() {
		return tile;
	}
	
	public int getItemId() {
		return itemId;
	}
	
	public int getAmount() {
		return amount;
	}

	public String getComment() {
		return comment;
	}
}
