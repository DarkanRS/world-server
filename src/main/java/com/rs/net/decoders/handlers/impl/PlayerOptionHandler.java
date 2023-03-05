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
import com.rs.game.model.entity.actions.EntityFollow;
import com.rs.game.model.entity.interactions.PlayerCombatInteraction;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.PlayerOp;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.PlayerClickEvent;

public class PlayerOptionHandler implements PacketHandler<Player, PlayerOp> {

	@Override
	public void handle(Player player, PlayerOp packet) {
		if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead() || player.isLocked())
			return;

		Player target = World.getPlayers().get(packet.getPid());
		if (target == null || target.isDead() || target.hasFinished() || !player.getMapChunkIds().contains(target.getChunkId()))
			return;

		if (packet.isForceRun())
			player.setRun(true);

		if (PluginManager.handle(new PlayerClickEvent(player, target, player.getPlayerOption(packet.getOpcode()), false)))
			return;

		//		player.setRouteEvent(new RouteEvent(target, new Runnable() {
		//			@Override
		//			public void run() {
		//				PluginManager.handle(new PlayerClickEvent(player, target, player.getPlayerOption(packet), true));
		//			}
		//		}));

		switch(packet.getOpcode()) {
		case PLAYER_OP1:
			if (!player.getControllerManager().canPlayerOption1(target) || !player.isCanPvp() || !player.getControllerManager().canAttack(target))
				return;

			if (!player.isCanPvp() || !target.isCanPvp()) {
				player.sendMessage("You can only attack players in a player-vs-player area.");
				return;
			}
			if (!target.isAtMultiArea() || !player.isAtMultiArea()) {
				if (player.getAttackedBy() != target && player.inCombat()) {
					player.sendMessage("You are already in combat.");
					return;
				}
				if (target.getAttackedBy() != player && target.inCombat()) {
					if (!(target.getAttackedBy() instanceof NPC)) {
						player.sendMessage("That player is already in combat.");
						return;
					}
					target.setAttackedBy(player);
				}
			}
			player.stopAll(true);
			player.getInteractionManager().setInteraction(new PlayerCombatInteraction(player, target));
			break;
		case PLAYER_OP2:
			if (!player.getControllerManager().canPlayerOption2(target))
				return;
			player.stopAll(true);
			player.getActionManager().setAction(new EntityFollow(target));
			break;
		case PLAYER_OP3:
			if (!player.getControllerManager().canPlayerOption3(target))
				return;
			player.stopAll(true);
			break;
		case PLAYER_OP4:
			player.stopAll(true);
			if (!player.getControllerManager().canTrade() || !player.getControllerManager().canPlayerOption4(target))
				return;
			player.setRouteEvent(new RouteEvent(target, () -> {
				//					if (p2.getTemporaryAttributtes().get("coopSlayerRequest") == player) {
				//						if (p2.isIronMan() || player.isIronMan()) {
				//							player.sendMessage("Ironmen cannot participate in co-op slayer.");
				//							return;
				//						}
				//						if (p2.getCoopSlayerPartner() != null || player.getCoopSlayerPartner() != null) {
				//							player.sendMessage("You or the other player are already in a co-op slayer group.");
				//							return;
				//						}
				//						p2.setCoopSlayerPartner(player);
				//						player.setCoopSlayerPartner(p2);
				//						p2.sendMessage("<col=01DFA5>You are now in a co-op slayer group with " + player.getDisplayName() + ". Go get a task.");
				//						player.sendMessage("<col=01DFA5>You are now in a co-op slayer group with " + p2.getDisplayName() + ". Go get a task.");
				//						p2.sendMessage("<col=01DFA5>To leave the slayer group, relog.");
				//						player.sendMessage("<col=01DFA5>To leave the slayer group, relog.");
				//						p2.getTemporaryAttributtes().remove("coopSlayerRequest");
				//						return;
				//					}
				if (player.isCantTrade()) {
					player.sendMessage("You are busy.");
					return;
				}
				if (target.getInterfaceManager().containsScreenInter() || target.isCantTrade()) {
					player.sendMessage("The other player is busy.");
					return;
				}
				if (!target.withinDistance(player.getTile(), 14)) {
					player.sendMessage("Unable to find target " + target.getDisplayName());
					return;
				}
				if (target.getTempAttribs().getO("TradeTarget") == player) {
					target.getTempAttribs().removeO("TradeTarget");
					if (!player.getBank().checkPin())
						return;
					if (!target.getBank().checkPin())
						return;
					player.getTrade().openTrade(target);
					target.getTrade().openTrade(player);
					return;
				}
				player.getTempAttribs().setO("TradeTarget", target);
				player.sendMessage("Sending " + target.getDisplayName() + " a request...");
				target.getPackets().sendTradeRequestMessage(player);
			}));
			break;
		case PLAYER_OP5:
			break;
		case PLAYER_OP6:
			break;
		case PLAYER_OP7:
			break;
		case PLAYER_OP8:
			break;
		case PLAYER_OP9:
			player.stopAll();
			ClansManager.openClanInvite(player, target, target.getClan());
			break;
		case PLAYER_OP10:
			break;
		default:
			break;
		}
	}

}
