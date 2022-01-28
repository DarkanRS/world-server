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

import com.rs.game.World;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.ObjectOp;
import com.rs.net.decoders.handlers.ObjectHandler;

public class ObjectOptionHandler implements PacketHandler<Player, ObjectOp> {

	@Override
	public void handle(Player player, ObjectOp packet) {
		if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead())
			return;
		final WorldTile tile = new WorldTile(packet.getX(), packet.getY(), player.getPlane());
		final int regionId = tile.getRegionId();
		if (!player.getMapRegionsIds().contains(regionId))
			return;
		GameObject mapObject = World.getObjectWithId(tile, packet.getObjectId());
		if (mapObject == null || mapObject.getId() != packet.getObjectId())
			return;
		final GameObject object = mapObject;

		if (packet.getOpcode() == ClientPacket.OBJECT_EXAMINE) {
			ObjectHandler.handleOptionExamine(player, object);
			return;
		}

		if (player.isLocked() || player.getEmotesManager().isAnimating())
			return;

		player.stopAll(false);
		if (packet.isForceRun())
			player.setRun(true);

		switch(packet.getOpcode()) {
		case OBJECT_OP1 -> ObjectHandler.handleOption1(player, object);
		case OBJECT_OP2 -> ObjectHandler.handleOption2(player, object);
		case OBJECT_OP3 -> ObjectHandler.handleOption3(player, object);
		case OBJECT_OP4 -> ObjectHandler.handleOption4(player, object);
		case OBJECT_OP5 -> ObjectHandler.handleOption5(player, object);
		default -> System.err.println("Unexpected object interaction packet: " + packet.getOpcode());
		}
	}

}
