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
		if (packet.getMessage().startsWith("::") || packet.getMessage().startsWith(";;")) {
			try {
				Commands.processCommand(player, packet.getMessage().replace("::", "").replace(";;", ""), false, false);
			} catch(Throwable e) { }
			return;
		}
		int effects = (packet.getColor() << 8) | (packet.getEffect() & 0xff);
		switch(player.chatType) {
		case 1, 2, 3 -> LobbyCommunicator.forwardPackets(player, packet.setType(player.chatType));
		default -> {
			if (packet.getMessage() == null || packet.getMessage().replaceAll(" ", "").equals(""))
				return;
			if (player.getAccount().isMuted()) {
				player.sendMessage("You are muted. The mute will be lifted at " + player.getAccount().getUnmuteDate());
				return;
			}
			if (player.getControllerManager().getController() instanceof DungeonController)
				for (Player party : player.getDungManager().getParty().getTeam())
					party.getPackets().sendPublicMessage(player, new PublicChatMessage(packet.getMessage(), effects));
			else
				player.sendPublicChatMessage(new PublicChatMessage(packet.getMessage(), effects));
		}
		}
	}

}
