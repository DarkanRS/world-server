package com.rs.net.decoders.handlers.impl;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.ge.GE;
import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.GEItemSelect;

public class GEItemSelectHandler implements PacketHandler<Player, GEItemSelect> {

	@Override
	public void handle(Player player, GEItemSelect packet) {
		ItemDefinitions def = ItemDefinitions.getDefs(packet.getItemId());
		if (def != null)
			GE.selectItem(player, packet.getItemId(), 1);
	}

}
