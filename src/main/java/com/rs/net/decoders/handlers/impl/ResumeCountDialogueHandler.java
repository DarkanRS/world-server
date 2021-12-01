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
package com.rs.net.decoders.handlers.impl;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.ResumeCountDialogue;
import com.rs.plugin.events.InputIntegerEvent;

public class ResumeCountDialogueHandler implements PacketHandler<Player, ResumeCountDialogue> {

	@Override
	public void handle(Player player, ResumeCountDialogue packet) {
		if (!player.isRunning() || player.isDead())
			return;
		if (player.getInterfaceManager().containsInterface(671) && player.getInterfaceManager().containsInterface(665)) {
			if (player.getFamiliar() == null || player.getFamiliar().getBob() == null)
				return;
			if (packet.getValue() < 0)
				return;
			if (player.getTempAttribs().removeB("bob_isRemove"))
				player.getFamiliar().getBob().removeItem(player.getTempAttribs().removeI("bob_item_X_Slot", 0), packet.getValue());
			else
				player.getFamiliar().getBob().addItem(player.getTempAttribs().removeI("bob_item_X_Slot", 0), packet.getValue());
		} else if (player.getInterfaceManager().containsInterface(335) && player.getInterfaceManager().containsInterface(336)) {
			if (packet.getValue() < 0)
				return;
			if (player.getTempAttribs().removeB("trade_isRemove"))
				player.getTrade().removeItem(player.getTempAttribs().removeI("trade_item_X_Slot", 0), packet.getValue());
			else
				player.getTrade().addItem(player.getTempAttribs().removeI("trade_item_X_Slot", 0), packet.getValue());
		} else {
			if (player.getTempAttribs().getO("pluginInteger") != null && player.getTempAttribs().removeO("pluginInteger") instanceof InputIntegerEvent iie)
				iie.run(packet.getValue());
			return;
		}
	}

}
