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
import com.rs.game.content.clans.ClansManager;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
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

		if (npc == null || npc.isDead() || npc.hasFinished() || !player.getMapRegionsIds().contains(npc.getRegionId()) || npc.getDefinitions().getIdForPlayer(player.getVars()) == -1)
			return;

		if (packet.getOpcode() == ClientPacket.NPC_EXAMINE) {
			NPCHandler.handleExamine(player, npc);
			if (player.getNSV().getB("clanifyStuff"))
				ClansManager.clanifyNPC(player.getClan(), npc);
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
