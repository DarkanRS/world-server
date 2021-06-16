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
		if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead())
			return;
		if (player.isLocked())
			return;
		if (Utils.getInterfaceDefinitionsSize() <= packet.getInterfaceId())
			return;
		if (!player.getInterfaceManager().containsInterface(packet.getInterfaceId()))
			return;
		if (packet.getComponentId() != -1 && Utils.getInterfaceDefinitionsComponentsSize(packet.getInterfaceId()) <= packet.getComponentId())
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
				if ((packet.getInterfaceId() == 662 && packet.getComponentId() == 74 || packet.getInterfaceId() == 747 && packet.getComponentId() == 24 || packet.getInterfaceId() == 747 && packet.getComponentId() == 18)) {
					if (player.getFamiliar().getSpecialAttack() != SpecialAttack.ENTITY)
						return;
				}
				if (!player.isCanPvp() || !p2.isCanPvp()) {
					player.sendMessage("You can only attack players in a player-vs-player area.");
					return;
				}
				if (!player.getFamiliar().canAttack(p2)) {
					player.sendMessage("You can only use your familiar in a multi-zone area.");
					return;
				} else {
					player.getFamiliar().setSpecial(packet.getInterfaceId() == 662 && packet.getComponentId() == 74 || packet.getInterfaceId() == 747 && packet.getComponentId() == 18);
					player.getFamiliar().setTarget(p2);
				}
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
