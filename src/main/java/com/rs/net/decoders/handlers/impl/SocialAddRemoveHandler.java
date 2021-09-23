package com.rs.net.decoders.handlers.impl;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.SocialAddRemove;
import com.rs.net.LobbyCommunicator;

public class SocialAddRemoveHandler implements PacketHandler<Player, SocialAddRemove> {

	@Override
	public void handle(Player player, SocialAddRemove packet) {
		LobbyCommunicator.forwardPackets(player, packet);
	}

}
