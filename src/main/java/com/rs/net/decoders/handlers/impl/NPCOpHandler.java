package com.rs.net.decoders.handlers.impl;

import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.NPCOp;
import com.rs.net.decoders.handlers.NPCHandler;

public class NPCOpHandler implements PacketHandler<Player, NPCOp> {

	@Override
	public void handle(Player player, NPCOp packet) {
		if (!player.hasStarted() || !player.clientHasLoadedMapRegion())
			return;
		
		if (packet.isForceRun())
			player.setRun(true);
		NPC npc = World.getNPCs().get(packet.getNpcIndex());
				
		if (npc == null || npc.isDead() || npc.hasFinished() || !player.getMapRegionsIds().contains(npc.getRegionId()) || npc.getDefinitions().getIdForPlayer(player.getVars()) == -1) {
			return;
		}
		
		if (packet.getOpcode() == ClientPacket.NPC_EXAMINE) {
			NPCHandler.handleExamine(player, npc);
			return;
		}
		
		if (player.isDead() || player.isLocked())
			return;
				
		switch(packet.getOpcode()) {
		case NPC_OP1:
			NPCHandler.handleOption1(player, npc);
			break;
		case NPC_OP2:
			NPCHandler.handleOption2(player, npc);
			break;
		case NPC_OP3:
			NPCHandler.handleOption3(player, npc);
			break;
		case NPC_OP4:
			NPCHandler.handleOption4(player, npc);
			break;
		case NPC_OP5:
			NPCHandler.handleOption5(player, npc);
			break;
		default:
			break;
		}
	}

}
