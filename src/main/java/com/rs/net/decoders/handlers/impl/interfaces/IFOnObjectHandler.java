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
import com.rs.game.content.skills.magic.Lunars;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.entity.player.Inventory;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.interfaces.IFOnObject;
import com.rs.lib.util.Utils;
import com.rs.net.decoders.handlers.ObjectHandler;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.InterfaceOnObjectEvent;

public class IFOnObjectHandler implements PacketHandler<Player, IFOnObject> {

	@Override
	public void handle(Player player, IFOnObject packet) {
		if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead())
			return;
		if (player.isLocked() || player.getEmotesManager().isAnimating())
			return;
		final Tile tile = Tile.of(packet.getX(), packet.getY(), player.getPlane());
		if (!player.getMapChunkIds().contains(tile.getChunkId()))
			return;
		GameObject mapObject = World.getObjectWithId(tile, packet.getObjectId());
		if (mapObject == null || mapObject.getId() != packet.getObjectId())
			return;
		final GameObject object = mapObject;
		if (player.isDead() || Utils.getInterfaceDefinitionsSize() <= packet.getInterfaceId() || player.isLocked() || !player.getInterfaceManager().topOpen(packet.getInterfaceId()))
			return;
		player.stopAll();
		if (packet.isForceRun())
			player.setRun(true);
		switch (packet.getInterfaceId()) {
		case 430:
			player.stopAll(true);
			if (packet.getComponentId() == 55)
				Lunars.handleCurePlant(player, object);
			else if (packet.getComponentId() == 24)
				Lunars.handleFertileSoil(player, object);
			break;
		case Inventory.INVENTORY_INTERFACE: // inventory
			final Item item = player.getInventory().getItem(packet.getSlotId());
			if (item == null || item.getId() != packet.getItemId())
				return;
			ObjectHandler.handleItemOnObject(player, object, packet.getInterfaceId(), item, packet.getSlotId());
			break;
		case 662:
		case 747:
			if (player.getFamiliar() == null)
				return;
			player.resetWalkSteps();
			if ((packet.getInterfaceId() == 662 && packet.getComponentId() == 74) || (packet.getInterfaceId() == 747 && packet.getComponentId() == 18))
				player.getFamiliar().castSpecial(object);
			break;
		default:
			PluginManager.handle(new InterfaceOnObjectEvent(player, object, packet.getInterfaceId(), packet.getComponentId(), packet.getSlotId(), false));
			player.setRouteEvent(new RouteEvent(object, () -> {
				player.faceObject(object);
				PluginManager.handle(new InterfaceOnObjectEvent(player, object, packet.getInterfaceId(), packet.getComponentId(), packet.getSlotId(), true));
			}));
			break;
		}
	}
}
