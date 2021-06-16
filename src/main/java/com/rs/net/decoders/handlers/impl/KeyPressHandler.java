package com.rs.net.decoders.handlers.impl;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.KeyPress;

public class KeyPressHandler implements PacketHandler<Player, KeyPress> {

	@Override
	public void handle(Player player, KeyPress packet) {
		player.refreshIdleTime();
		if (packet.getKeyCode() == 13)
			player.closeInterfaces();
	}

}
