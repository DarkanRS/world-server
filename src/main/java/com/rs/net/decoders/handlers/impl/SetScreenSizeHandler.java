package com.rs.net.decoders.handlers.impl;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.SetScreenSize;

public class SetScreenSizeHandler implements PacketHandler<Player, SetScreenSize> {

	@Override
	public void handle(Player player, SetScreenSize packet) {
		player.setScreenWidth(packet.getWidth());
		player.setScreenHeight(packet.getHeight());
		if (!player.hasStarted() || player.hasFinished() || packet.getDisplayMode() == player.getDisplayMode() || !player.getInterfaceManager().containsInterface(742))
			return;
		player.setDisplayMode(packet.getDisplayMode());
		player.getInterfaceManager().removeAll();
		player.getInterfaceManager().sendInterfaces();
		player.getInterfaceManager().sendInterface(742);
	}

}
