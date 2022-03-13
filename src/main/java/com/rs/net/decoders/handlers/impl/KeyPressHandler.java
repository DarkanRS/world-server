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
package com.rs.net.decoders.handlers.impl;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.KeyPress;

public class KeyPressHandler implements PacketHandler<Player, KeyPress> {

	@Override
	public void handle(Player player, KeyPress packet) {
		player.refreshIdleTime();
		if (packet.getKeyCode() == 13) {
			if(!player.getTempAttribs().getB("CUTSCENE_INTERFACE_CLOSE_DISABLED"))
				player.closeInterfaces();
			if (player.getInterfaceManager().containsInterface(755)) {//World map
				//Send window pane
				player.getPackets().sendWindowsPane(player.getInterfaceManager().hasRezizableScreen() ? 746 : 548, 2);

				//Reset top of interface stack on client
				player.getInterfaceManager().setDefaultTopInterface();
			}
		}
	}

}
