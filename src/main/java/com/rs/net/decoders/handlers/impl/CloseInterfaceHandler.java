package com.rs.net.decoders.handlers.impl;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.CloseInterface;

public class CloseInterfaceHandler implements PacketHandler<Player, CloseInterface> {

	@Override
	public void handle(Player player, CloseInterface packet) {
		if (player.hasStarted() && !player.hasFinished() && !player.isRunning()) {
			player.run();
			return;
		}
		if (player.getInterfaceManager().containsScreenInter())
			player.stopAll();
	}

}
