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
package com.rs.net.decoders.handlers.impl.interfaces;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.interfaces.IFOp;
import com.rs.lib.util.Utils;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.ButtonClickEvent;

public class IFOpHandler implements PacketHandler<Player, IFOp> {

	@Override
	public void handle(Player player, IFOp packet) {
		if ((packet.getInterfaceId() >= Utils.getInterfaceDefinitionsSize()) || (player.isLocked() && !player.getTempAttribs().getB("TransformationRing")) || player.isDead() || !player.getInterfaceManager().topOpen(packet.getInterfaceId()))
			return;
		if ((packet.getComponentId() != 65535 && Utils.getInterfaceDefinitionsComponentsSize(packet.getInterfaceId()) <= packet.getComponentId()) || !player.getControllerManager().processButtonClick(packet.getInterfaceId(), packet.getComponentId(), packet.getSlotId(), packet.getItemId(), packet.getOpcode()))
			return;
		PluginManager.handle(new ButtonClickEvent(player, packet.getInterfaceId(), packet.getComponentId(), packet.getSlotId(), packet.getItemId(), packet.getOpcode()));
	}

}
