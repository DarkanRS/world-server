package com.rs.net.decoders.handlers.impl;

import com.rs.game.pathing.FixedTileStrategy;
import com.rs.game.pathing.RouteFinder;
import com.rs.game.player.Player;
import com.rs.game.player.content.Effect;
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
		if (steps == -1) {
			return;
		}
		player.stopAll();
		for (int i = steps - 1; i >= 0; i--) {
			if (!player.addWalkSteps(bufferX[i], bufferY[i], 25, true, true)) {
				break;
			}
		}
		if (last != -1) {
			WorldTile tile = new WorldTile(bufferX[last], bufferY[last], player.getPlane());
			player.getSession().writeToQueue(new MinimapFlag(tile.getXInScene(player.getSceneBaseChunkId()), tile.getYInScene(player.getSceneBaseChunkId())));
		}
	}
}
