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
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.interfaces.IFOnNPC;
import com.rs.lib.util.Utils;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.IFOnNPCEvent;

public class IFOnNPCHandler implements PacketHandler<Player, IFOnNPC> {

	@Override
	public void handle(Player player, IFOnNPC packet) {
		if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead() || player.isLocked())
			return;
		if ((Utils.getInterfaceDefinitionsSize() <= packet.getInterfaceId()) || !player.getInterfaceManager().topOpen(packet.getInterfaceId()) || (packet.getComponentId() != -1 && Utils.getInterfaceDefinitionsComponentsSize(packet.getInterfaceId()) <= packet.getComponentId()))
			return;
		NPC npc = World.getNPCs().get(packet.getNpcIndex());
		if (npc == null || npc.isDead() || npc.hasFinished() || !player.getMapChunkIds().contains(npc.getChunkId()) || npc.getDefinitions().getIdForPlayer(player.getVars()) == -1)
			return;
		player.stopAll(true);
		if (PluginManager.handle(new IFOnNPCEvent(player, npc, packet.getInterfaceId(), packet.getComponentId(), packet.getSlotId(), packet.getItemId(), false)))
			return;
		player.setRouteEvent(new RouteEvent(npc, () -> {
			player.faceEntity(npc);
			PluginManager.handle(new IFOnNPCEvent(player, npc, packet.getInterfaceId(), packet.getComponentId(), packet.getSlotId(), packet.getItemId(), true));
		}));
	}

}
