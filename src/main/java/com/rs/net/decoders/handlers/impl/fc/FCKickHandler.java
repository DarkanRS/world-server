package com.rs.net.decoders.handlers.impl.fc;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.fc.FCKick;
import com.rs.net.LobbyCommunicator;

public class FCKickHandler implements PacketHandler<Player, FCKick> {

	@Override
	public void handle(Player player, FCKick packet) {
		if (!player.hasStarted())
			return;
		player.setLastPublicMessage(System.currentTimeMillis() + 1000);
		LobbyCommunicator.kickFCPlayer(player, packet.getName());
	}

}
