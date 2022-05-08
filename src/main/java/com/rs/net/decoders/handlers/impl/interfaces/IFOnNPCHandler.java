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
import com.rs.game.content.combat.CombatSpell;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Inventory;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.interfaces.IFOnNPC;
import com.rs.lib.util.Utils;
import com.rs.net.decoders.handlers.InventoryOptionsHandler;

public class IFOnNPCHandler implements PacketHandler<Player, IFOnNPC> {

	@Override
	public void handle(Player player, IFOnNPC packet) {
		if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead() || player.isLocked())
			return;
		if ((Utils.getInterfaceDefinitionsSize() <= packet.getInterfaceId()) || !player.getInterfaceManager().topOpen(packet.getInterfaceId()) || (packet.getComponentId() != -1 && Utils.getInterfaceDefinitionsComponentsSize(packet.getInterfaceId()) <= packet.getComponentId()))
			return;
		NPC npc = World.getNPCs().get(packet.getNpcIndex());
		if (npc == null || npc.isDead() || npc.hasFinished() || !player.getMapRegionsIds().contains(npc.getRegionId()) || npc.getDefinitions().getIdForPlayer(player.getVars()) == -1)
			return;
		player.stopAll(false);
		switch (packet.getInterfaceId()) {
		case Inventory.INVENTORY_INTERFACE:
			Item item = player.getInventory().getItem(packet.getSlotId());
			if (item == null || !player.getControllerManager().processItemOnNPC(npc, item))
				return;
			InventoryOptionsHandler.handleItemOnNPC(player, npc, item, packet.getSlotId());
			break;
		case 662:
		case 747:
			if (player.getFamiliar() == null)
				return;
			player.resetWalkSteps();
			if ((packet.getInterfaceId() == 747 && packet.getComponentId() == 15) || (packet.getInterfaceId() == 662 && packet.getComponentId() == 65) || packet.getInterfaceId() == 747 && packet.getComponentId() == 24)
				player.getFamiliar().commandAttack(npc);
			if ((packet.getInterfaceId() == 662 && packet.getComponentId() == 74) || (packet.getInterfaceId() == 747 && packet.getComponentId() == 18))
				player.getFamiliar().executeSpecial(npc);
			break;
		case 950:
		case 193:
		case 192:
			CombatSpell combat = CombatSpell.forId(packet.getInterfaceId(), packet.getComponentId());
			if (combat != null) {
				if (!npc.getDefinitions().hasAttackOption()) {
					player.sendMessage("You can't attack that.");
					return;
				}
				Magic.manualCast(player, npc, combat);
			}
			break;
		}
	}

}
