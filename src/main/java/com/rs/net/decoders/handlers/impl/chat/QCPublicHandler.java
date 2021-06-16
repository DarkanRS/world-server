package com.rs.net.decoders.handlers.impl.chat;

import com.rs.game.player.Player;
import com.rs.lib.game.QuickChatMessage;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.chat.QCPublic;
import com.rs.lib.util.Utils;
import com.rs.net.LobbyCommunicator;

public class QCPublicHandler implements PacketHandler<Player, QCPublic> {

	@Override
	public void handle(Player player, QCPublic packet) {
		if (!player.hasStarted())
			return;
		if (player.getLastPublicMessage() > System.currentTimeMillis())
			return;
		player.setLastPublicMessage(System.currentTimeMillis() + 300);
		if (!Utils.isQCValid(packet.getQcId()))
			return;
		byte[] data = QCPrivateHandler.completeQuickMessage(player, packet.getQcId(), packet.getMessageData());
		if (packet.getChatType() == 0)
			player.sendPublicChatMessage(new QuickChatMessage(packet.getQcId(), data));
		else if (packet.getChatType() == 1)
			LobbyCommunicator.sendFCQuickChat(player, new QuickChatMessage(packet.getQcId(), data));
		else if (packet.getChatType() == 2)
			LobbyCommunicator.sendCCQuickChat(player, new QuickChatMessage(packet.getQcId(), data));
		else if (packet.getChatType() == 3)
			LobbyCommunicator.sendGCCQuickChat(player, new QuickChatMessage(packet.getQcId(), data));
	}

}
