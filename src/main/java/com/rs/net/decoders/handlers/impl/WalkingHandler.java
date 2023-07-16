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

import com.rs.game.content.Effect;
import com.rs.game.model.entity.pathing.FixedTileStrategy;
import com.rs.game.model.entity.pathing.Route;
import com.rs.game.model.entity.pathing.RouteFinder;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.Walk;
import com.rs.lib.net.packets.encoders.MinimapFlag;

public class WalkingHandler implements PacketHandler<Player, Walk> {

	@Override
	public void handle(Player player, Walk packet) {
		player.refreshIdleTime();

		if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead() || player.isLocked())
			return;
		if (player.hasEffect(Effect.FREEZE)) {
			player.sendMessage("A magical force prevents you from moving.");
			return;
		}

		Route route = RouteFinder.find(player.getX(), player.getY(), player.getPlane(), player.getSize(), new FixedTileStrategy(packet.getX(), packet.getY()), true);
		int last = -1;
		if (route.getStepCount() == -1)
			return;
		player.stopAll();
		player.setNextFaceEntity(null);
		for (int i = route.getStepCount() - 1; i >= 0; i--)
			if (!player.addWalkSteps(route.getBufferX()[i], route.getBufferY()[i], 25, true, true))
				break;
		if (last != -1) {
			Tile tile = Tile.of(route.getBufferX()[last], route.getBufferY()[last], player.getPlane());
			player.getSession().writeToQueue(new MinimapFlag(tile.getXInScene(player.getSceneBaseChunkId()), tile.getYInScene(player.getSceneBaseChunkId())));
		}
	}
}
