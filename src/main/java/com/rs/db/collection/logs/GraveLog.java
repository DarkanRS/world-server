package com.rs.db.collection.logs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.rs.game.content.death.GraveStone;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;

public class GraveLog {
	private String uuid;
	private String player;
	private Tile tile;
	private List<Item> items;

	public GraveLog(String player, GraveStone grave) {
		this.player = player;
		this.tile = grave.getTile();
		items = new ArrayList<>();
		for (GroundItem item : grave.getItems())
			if (item != null)
				items.add(new Item(item));
		this.uuid = UUID.randomUUID().toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GraveLog graveLog = (GraveLog) o;
		return Objects.equals(uuid, graveLog.uuid) && Objects.equals(player, graveLog.player) && Objects.equals(tile, graveLog.tile) && Objects.equals(items, graveLog.items);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid, player, tile, items);
	}
}
