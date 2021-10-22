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
