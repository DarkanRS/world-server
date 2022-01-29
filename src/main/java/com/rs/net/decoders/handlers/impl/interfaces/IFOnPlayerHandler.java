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
import com.rs.game.npc.familiar.Familiar.SpecialAttack;
import com.rs.game.player.Player;
import com.rs.game.player.content.combat.CombatSpell;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.interfaces.IFOnPlayer;
import com.rs.lib.util.Utils;
import com.rs.net.decoders.handlers.InventoryOptionsHandler;

public class IFOnPlayerHandler implements PacketHandler<Player, IFOnPlayer> {

	@Override
	public void handle(Player player, IFOnPlayer packet) {
		if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead() || player.isLocked())
			return;
		if ((Utils.getInterfaceDefinitionsSize() <= packet.getInterfaceId()) || !player.getInterfaceManager().containsInterface(packet.getInterfaceId()) || (packet.getComponentId() != -1 && Utils.getInterfaceDefinitionsComponentsSize(packet.getInterfaceId()) <= packet.getComponentId()))
			return;
		Player p2 = World.getPlayers().get(packet.getPlayerIndex());
		if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
			return;
		player.stopAll(false);
		switch (packet.getInterfaceId()) {
		case 1110:
			if (packet.getComponentId() == 87) {
				//TODO send clan invite
			}
			break;

		case 679:
			InventoryOptionsHandler.handleItemOnPlayer(player, p2, packet.getSlotId());
			break;

		case 662:
		case 747:
			if (player.getFamiliar() == null)
				return;
			player.resetWalkSteps();
			if ((packet.getInterfaceId() == 747 && packet.getComponentId() == 15) || (packet.getInterfaceId() == 662 && packet.getComponentId() == 65) || (packet.getInterfaceId() == 662 && packet.getComponentId() == 74) || packet.getInterfaceId() == 747 && packet.getComponentId() == 18) {
				if ((packet.getInterfaceId() == 662 && packet.getComponentId() == 74 || packet.getInterfaceId() == 747 && packet.getComponentId() == 24 || packet.getInterfaceId() == 747 && packet.getComponentId() == 18))
					if (player.getFamiliar().getSpecialAttack() != SpecialAttack.ENTITY)
						return;
				if (!player.isCanPvp() || !p2.isCanPvp()) {
					player.sendMessage("You can only attack players in a player-vs-player area.");
					return;
				}
				if (!player.getFamiliar().canAttack(p2)) {
					player.sendMessage("You can only use your familiar in a multi-zone area.");
					return;
				}
				player.getFamiliar().setSpecial(packet.getInterfaceId() == 662 && packet.getComponentId() == 74 || packet.getInterfaceId() == 747 && packet.getComponentId() == 18);
				player.getFamiliar().setTarget(p2);
			}
			break;
		case 193:
		case 192:
			CombatSpell combat = CombatSpell.forId(packet.getInterfaceId(), packet.getComponentId());
			if (combat != null)
				Magic.manualCast(player, p2, combat);
			break;
		}
	}
}
