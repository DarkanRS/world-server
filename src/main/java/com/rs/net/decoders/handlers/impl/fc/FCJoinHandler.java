package com.rs.net.decoders.handlers.impl.fc;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.fc.FCJoin;
import com.rs.net.LobbyCommunicator;

public class FCJoinHandler implements PacketHandler<Player, FCJoin> {

	@Override
	public void handle(Player player, FCJoin packet) {
		if ((!player.hasStarted()))
			return;
		if (packet.getName() == null) {
			if (player.getSocial().getCurrentFriendsChat() != null)
				LobbyCommunicator.leaveFC(player);
			return;
		}
		LobbyCommunicator.joinFC(player, packet.getName());
	}

}
