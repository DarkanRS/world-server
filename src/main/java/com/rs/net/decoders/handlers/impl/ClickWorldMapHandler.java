package com.rs.net.decoders.handlers.impl;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.WorldMapClick;

public class ClickWorldMapHandler implements PacketHandler<Player, WorldMapClick> {

	@Override
	public void handle(Player player, WorldMapClick packet) {
		Integer hash = (Integer) player.getTemporaryAttributes().get("worldHash");
		if (hash == null || packet.getTile().getTileHash() != hash)
			player.getTemporaryAttributes().put("worldHash", packet.getTile().getTileHash());
		else {
			player.getHintIconsManager().removeAll();
			player.getTemporaryAttributes().remove("worldHash");
			player.getHintIconsManager().addHintIcon(packet.getTile().getX(), packet.getTile().getY(), packet.getTile().getPlane(), 20, 0, 2, -1, true);
			player.getVars().setVar(1159, packet.getTile().getTileHash());
		}
	}

}
