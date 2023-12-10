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
import com.rs.lib.game.Animation;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.KeyPress;

public class KeyPressHandler implements PacketHandler<Player, KeyPress> {

	@Override
	public void handle(Player player, KeyPress packet) {
		player.refreshIdleTime();
		switch(packet.getKeyCode()) {
			case 13 -> {
				if(!player.getTempAttribs().getB("CUTSCENE_INTERFACE_CLOSE_DISABLED"))
					player.closeInterfaces();
				if (player.getInterfaceManager().topOpen(755)) {//World map
					//Send window pane
					player.getPackets().sendWindowsPane(player.resizeable() ? 746 : 548, 2);

					//Reset top of interface stack on client
					player.getInterfaceManager().setDefaultTopInterface();
				}
			}
//			case 33, 48, 49, 50, 83 -> { //WASD walking lmao
//				switch(packet.getKeyCode()) {
//					case 33 -> {
//						player.stopAll();
//						player.resetWalkSteps();
//						player.addWalkSteps(player.getX(), player.getY()+5);
//					}
//					case 48 -> {
//						player.stopAll();
//						player.resetWalkSteps();
//						player.addWalkSteps(player.getX()-5, player.getY());
//					}
//					case 49 -> {
//						player.stopAll();
//						player.resetWalkSteps();
//						player.addWalkSteps(player.getX(), player.getY()-5);
//					}
//					case 50 -> {
//						player.stopAll();
//						player.resetWalkSteps();
//						player.addWalkSteps(player.getX()+5, player.getY());
//					}
//					case 83 -> {
//						GameObject object = World.getObject(player.transform(player.getDirection().getDx(), player.getDirection().getDy(), player.getPlane()), ObjectType.SCENERY_INTERACT);
//						if (object != null)
//							PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP1, true));
//					}
//				}
//			}
//		case 83 -> {
//			if (player.getConversation() != null) {
//				player.getConversation().process(0);
//			}
//		}
//		default -> player.sendMessage("Keycode: " + packet.getKeyCode());
		}
	}

}
