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
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.net.decoders.handlers.impl.chat;

import com.rs.game.content.skills.dungeoneering.DungeonController;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.QuickChatMessage;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.chat.QCPublic;
import com.rs.lib.util.Utils;
import com.rs.net.LobbyCommunicator;
import com.rs.utils.WorldUtil;

public class QCPublicHandler implements PacketHandler<Player, QCPublic> {

	@Override
	public void handle(Player player, QCPublic packet) {
		if (!player.hasStarted() || !Utils.isQCValid(packet.getQcId()))
			return;
		packet.setCompletedData(WorldUtil.completeQuickMessage(player, packet.getQcId(), packet.getMessageData()));
		switch(packet.getChatType()) {
		case 1, 2, 3 -> LobbyCommunicator.forwardPackets(player, packet);
		case 0 -> {
			if (player.getControllerManager().getController() instanceof DungeonController)
				for (Player party : player.getDungManager().getParty().getTeam())
					party.getPackets().sendPublicMessage(player, new QuickChatMessage(packet.getQcId(), packet.getCompletedData()));
			else
				player.sendPublicChatMessage(new QuickChatMessage(packet.getQcId(), packet.getCompletedData()));
		}
		}
	}

}
