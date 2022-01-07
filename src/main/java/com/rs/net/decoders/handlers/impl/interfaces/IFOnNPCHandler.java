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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.net.decoders.handlers.impl.interfaces;

import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.familiar.Familiar.SpecialAttack;
import com.rs.game.player.Inventory;
import com.rs.game.player.Player;
import com.rs.game.player.content.combat.CombatSpell;
import com.rs.game.player.content.skills.magic.Magic;
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
		if (Utils.getInterfaceDefinitionsSize() <= packet.getInterfaceId())
			return;
		if (!player.getInterfaceManager().containsInterface(packet.getInterfaceId()))
			return;
		if (packet.getComponentId() != -1 && Utils.getInterfaceDefinitionsComponentsSize(packet.getInterfaceId()) <= packet.getComponentId())
			return;
		NPC npc = World.getNPCs().get(packet.getNpcIndex());
		if (npc == null || npc.isDead() || npc.hasFinished() || !player.getMapRegionsIds().contains(npc.getRegionId()) || npc.getDefinitions().getIdForPlayer(player.getVars()) == -1)
			return;
		player.stopAll(false);
		if (packet.getInterfaceId() != Inventory.INVENTORY_INTERFACE)
			if (!npc.getDefinitions().hasAttackOption()) {
				player.sendMessage("You can't attack this npc.");
				return;
			}
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
			if ((packet.getInterfaceId() == 747 && packet.getComponentId() == 15) || (packet.getInterfaceId() == 662 && packet.getComponentId() == 65) || (packet.getInterfaceId() == 662 && packet.getComponentId() == 74) || packet.getInterfaceId() == 747 && packet.getComponentId() == 18 || packet.getInterfaceId() == 747 && packet.getComponentId() == 24) {
				if ((packet.getInterfaceId() == 662 && packet.getComponentId() == 74 || packet.getInterfaceId() == 747 && packet.getComponentId() == 18))
					if (player.getFamiliar().getSpecialAttack() != SpecialAttack.ENTITY)
						return;
				if (npc instanceof Familiar familiar) {
					if (familiar == player.getFamiliar()) {
						player.sendMessage("You can't attack your own familiar.");
						return;
					}
					if (!player.getFamiliar().canAttack(familiar.getOwner())) {
						player.sendMessage("You can only attack players in a player-vs-player area.");
						return;
					}
				}
				if (!player.getFamiliar().canAttack(npc)) {
					player.sendMessage("You can only use your familiar in a multi-zone area.");
					return;
				}
				player.getFamiliar().setSpecial(packet.getInterfaceId() == 662 && packet.getComponentId() == 74 || packet.getInterfaceId() == 747 && packet.getComponentId() == 18);
				player.getFamiliar().setTarget(npc);
			}
			break;
		case 950:
		case 193:
		case 192:
			CombatSpell combat = CombatSpell.forId(packet.getInterfaceId(), packet.getComponentId());
			if (combat != null)
				Magic.manualCast(player, npc, combat);
			break;
		}
	}

}
