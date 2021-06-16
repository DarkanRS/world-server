package com.rs.net.decoders.handlers.impl.fc;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.fc.FCSetRank;
import com.rs.net.LobbyCommunicator;

public class FCSetRankHandler implements PacketHandler<Player, FCSetRank> {

	@Override
	public void handle(Player player, FCSetRank packet) {
		if (!player.hasStarted())
			return;
		player.getAccount().getSocial().getFriendsChat().setRank(packet.getName(), packet.getRank());
		LobbyCommunicator.updateAccount(player);
	}

}
