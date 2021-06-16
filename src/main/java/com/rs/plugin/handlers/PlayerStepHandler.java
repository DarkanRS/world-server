package com.rs.plugin.handlers;

import java.util.ArrayList;
import java.util.List;

import com.rs.lib.game.WorldTile;
import com.rs.plugin.events.PlayerStepEvent;

public abstract class PlayerStepHandler extends PluginHandler<PlayerStepEvent> {
	
	public PlayerStepHandler(WorldTile... tiles) {
		super(null);
		List<Integer> tileHashes = new ArrayList<>();
		for (WorldTile tile : tiles)
			tileHashes.add(tile.getTileHash());
		this.keys = tileHashes.toArray();
	}
}
