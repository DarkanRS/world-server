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
package com.rs.net.decoders.handlers.impl.interfaces;

import com.rs.game.player.Player;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.interfaces.IFContinue;
import com.rs.lib.util.Utils;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.DialogueOptionEvent;

public class IFContinueHandler implements PacketHandler<Player, IFContinue> {

	@Override
	public void handle(Player player, IFContinue packet) {
		if (Utils.getInterfaceDefinitionsSize() <= packet.getInterfaceId() || !player.isRunning() || !player.getInterfaceManager().containsInterface(packet.getInterfaceId()))
			return;
		if (player.getTempAttribs().getO("pluginOption") != null && player.getTempAttribs().removeO("pluginOption") instanceof DialogueOptionEvent doe) {
			doe.setOption(packet.getComponentId() == 11 ? 1 : packet.getComponentId()-11);
			if (player.getInterfaceManager().containsChatBoxInter())
				player.getInterfaceManager().closeChatBoxInterface();
			doe.run(player);
			return;
		}
		if (player.getConversation() != null) {
			player.getConversation().process(packet.getInterfaceId(), packet.getComponentId());
			return;
		}
		player.getDialogueManager().continueDialogue(packet.getInterfaceId(), packet.getComponentId());
		PluginManager.handle(new ButtonClickEvent(player, packet.getInterfaceId(), packet.getComponentId(), -1, -1, ClientPacket.IF_CONTINUE));
	}

}
