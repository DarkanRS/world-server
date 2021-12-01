// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
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
