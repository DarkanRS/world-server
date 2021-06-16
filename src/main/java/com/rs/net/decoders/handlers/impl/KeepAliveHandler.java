package com.rs.net.decoders.handlers.impl;

import com.rs.game.player.Player;
import com.rs.lib.net.ServerPacket;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.KeepAlive;

public class KeepAliveHandler implements PacketHandler<Player, KeepAlive> {

	@Override
	public void handle(Player player, KeepAlive packet) {
		player.getSession().writeToQueue(ServerPacket.KEEPALIVE);
	}

}
