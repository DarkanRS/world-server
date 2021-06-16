package com.rs.net.decoders.handlers.impl;

import com.rs.game.player.Player;
import com.rs.game.player.content.commands.Commands;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.ClientCheat;

public class ClientCheatHandler implements PacketHandler<Player, ClientCheat> {

	@Override
	public void handle(Player player, ClientCheat packet) {
		if (!player.isRunning())
			return;
		try {
			Commands.processCommand(player, packet.getCommand(), true, packet.isClient());
		} catch(Throwable e) {
			
		}
	}

}
