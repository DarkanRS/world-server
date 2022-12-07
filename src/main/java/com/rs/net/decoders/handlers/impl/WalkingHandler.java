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
import com.rs.game.model.entity.pathing.RouteFinder;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.WorldTile;
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

		int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, player.getX(), player.getY(), player.getPlane(), player.getSize(), new FixedTileStrategy(packet.getX(), packet.getY()), true);
		int[] bufferX = RouteFinder.getLastPathBufferX();
		int[] bufferY = RouteFinder.getLastPathBufferY();
		int last = -1;
		if (steps == -1)
			return;
		player.stopAll();
		for (int i = steps - 1; i >= 0; i--)
			if (!player.addWalkSteps(bufferX[i], bufferY[i], 25, true, true))
				break;
		if (last != -1) {
			WorldTile tile = WorldTile.of(bufferX[last], bufferY[last], player.getPlane());
			player.getSession().writeToQueue(new MinimapFlag(tile.getXInScene(player.getSceneBaseChunkId()), tile.getYInScene(player.getSceneBaseChunkId())));
		}
	}
}
