package com.rs.net.decoders.handlers.impl;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.RegionLoaded;

public class RegionLoadedHandler implements PacketHandler<Player, RegionLoaded> {

	@Override
	public void handle(Player player, RegionLoaded packet) {
		if (!player.clientHasLoadedMapRegionFinished()) {
			player.setClientHasLoadedMapRegion();
			player.refreshSpawnedObjects();
			player.refreshSpawnedItems();
		}
	}

}
