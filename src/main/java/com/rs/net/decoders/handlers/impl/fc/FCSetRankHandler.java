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
package com.rs.net.decoders.handlers.impl.fc;

import com.rs.game.player.Player;
import com.rs.lib.model.FriendsChat.Rank;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.fc.FCSetRank;
import com.rs.lib.net.packets.encoders.social.MessageGame.MessageType;
import com.rs.net.LobbyCommunicator;

public class FCSetRankHandler implements PacketHandler<Player, FCSetRank> {

	@Override
	public void handle(Player player, FCSetRank packet) {
		if (!player.hasStarted())
			return;
		if (player.getTempAttribs().getB("fcLock")) {
			player.sendMessage(MessageType.FC_NOTIFICATION, "Please wait before updating rank.");
			return;
		}
		player.getTempAttribs().setB("fcLock", true);
		player.getAccount().getSocial().getFriendsChat().setRank(packet.getName(), Rank.forId(packet.getRank()));
		LobbyCommunicator.updateAccount(player, res -> {});
		LobbyCommunicator.updateFC(player, res -> player.getTempAttribs().setB("fcLock", false));
	}

}
