package com.rs.net.decoders.handlers.impl.clan;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.clan.CCKick;
import com.rs.net.LobbyCommunicator;

public class CCKickHandler implements PacketHandler<Player, CCKick> {

	@Override
	public void handle(Player player, CCKick p) {
		if (!player.hasStarted())
			return;
		LobbyCommunicator.clanChatKick(player, p.isGuest(), p.getName());
	}

}
