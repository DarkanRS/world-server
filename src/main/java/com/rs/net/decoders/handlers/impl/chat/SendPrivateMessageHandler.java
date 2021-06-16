package com.rs.net.decoders.handlers.impl.chat;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.chat.SendPrivateMessage;
import com.rs.net.LobbyCommunicator;

public class SendPrivateMessageHandler implements PacketHandler<Player, SendPrivateMessage> {

	@Override
	public void handle(Player player, SendPrivateMessage packet) {
		if (!player.hasStarted())
			return;
		if (player.getAccount().isMuted()) {
			player.sendMessage("You are muted. The mute will be lifted at " + player.getAccount().getUnmuteDate());
			return;
		}
		LobbyCommunicator.sendPM(player, packet.getToUsername(), packet.getMessage());
	}

}
