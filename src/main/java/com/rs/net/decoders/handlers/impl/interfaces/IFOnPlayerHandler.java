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

import com.rs.game.World;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.interfaces.IFOnPlayer;
import com.rs.lib.util.Utils;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.IFOnPlayerEvent;

public class IFOnPlayerHandler implements PacketHandler<Player, IFOnPlayer> {

	@Override
	public void handle(Player player, IFOnPlayer packet) {
		if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead() || player.isLocked())
			return;
		if ((Utils.getInterfaceDefinitionsSize() <= packet.getInterfaceId()) || !player.getInterfaceManager().topOpen(packet.getInterfaceId()) || (packet.getComponentId() != -1 && Utils.getInterfaceDefinitionsComponentsSize(packet.getInterfaceId()) <= packet.getComponentId()))
			return;
		Player p2 = World.getPlayers().get(packet.getPlayerIndex());
		if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapChunkIds().contains(p2.getChunkId()))
			return;
		player.stopAll(false);
		if (PluginManager.handle(new IFOnPlayerEvent(player, p2, packet.getInterfaceId(), packet.getComponentId(), packet.getSlotId(), packet.getItemId(), false)))
			return;
		player.setRouteEvent(new RouteEvent(p2, () -> {
			player.faceEntity(p2);
			PluginManager.handle(new IFOnPlayerEvent(player, p2, packet.getInterfaceId(), packet.getComponentId(), packet.getSlotId(), packet.getItemId(), true));
		}));
	}
}
