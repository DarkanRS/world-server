package com.rs.net.decoders.handlers.impl.chat;

import com.rs.game.player.Player;
import com.rs.game.player.content.commands.Commands;
import com.rs.game.player.controllers.DungeonController;
import com.rs.lib.game.PublicChatMessage;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.chat.Chat;
import com.rs.net.LobbyCommunicator;

public class ChatHandler implements PacketHandler<Player, Chat> {

	@Override
	public void handle(Player player, Chat packet) {
		if (!player.hasStarted())
			return;
		if (player.getLastPublicMessage() > System.currentTimeMillis())
			return;
		player.setLastPublicMessage(System.currentTimeMillis() + 300);
		if (packet.getMessage() == null || packet.getMessage().replaceAll(" ", "").equals(""))
			return;
		if (packet.getMessage().startsWith("::") || packet.getMessage().startsWith(";;")) {
			try {
				Commands.processCommand(player, packet.getMessage().replace("::", "").replace(";;", ""), false, false);
			} catch(Throwable e) {
				
			}
			return;
		}
		if (player.getAccount().isMuted()) {
			player.sendMessage("You are muted. The mute will be lifted at " + player.getAccount().getUnmuteDate());
			return;
		}
		int effects = (packet.getColor() << 8) | (packet.getEffect() & 0xff);
		if (player.chatType == 1)
			LobbyCommunicator.sendFCMessage(player, packet.getMessage());
		else if (player.chatType == 2)
			LobbyCommunicator.sendCCMessage(player, packet.getMessage());
		else if (player.chatType == 3)
			LobbyCommunicator.sendGCCMessage(player, packet.getMessage());
		else {
			if (player.getControllerManager().getController() instanceof DungeonController) {
				for (Player party : player.getDungManager().getParty().getTeam()) {
					party.getPackets().sendPublicMessage(player, new PublicChatMessage(packet.getMessage(), effects));
				}
			} else
				player.sendPublicChatMessage(new PublicChatMessage(packet.getMessage(), effects));
		}
		player.setLastMsg(packet.getMessage());
	}

}
