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

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.content.Effect;
import com.rs.game.content.minigames.treasuretrails.TreasureTrailsManager;
import com.rs.game.content.skills.firemaking.Firemaking;
import com.rs.game.content.skills.firemaking.Firemaking.Fire;
import com.rs.game.content.skills.hunter.BoxAction;
import com.rs.game.content.skills.hunter.BoxTrapType;
import com.rs.game.map.ChunkManager;
import com.rs.engine.pathfinder.RouteEvent;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Tile;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.GroundItemOp;
import com.rs.lib.util.Utils;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.PickupItemEvent;
import com.rs.utils.ItemConfig;

public class GroundItemOpHandler implements PacketHandler<Player, GroundItemOp> {

	@Override
	public void handle(Player player, GroundItemOp packet) {
		if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead())
			return;
		if (player.isLocked() || player.hasEffect(Effect.FREEZE))
			return;

		final Tile tile = Tile.of(packet.getX(), packet.getY(), player.getPlane());
		if (!player.getMapChunkIds().contains(tile.getChunkId()))
			return;
		final GroundItem item = ChunkManager.getChunk(tile.getChunkId()).getGroundItem(packet.getObjectId(), tile, player);
		if (item == null)
			return;
		if (packet.getOpcode() == ClientPacket.GROUND_ITEM_EXAMINE) {
			ItemDefinitions def = ItemDefinitions.getDefs(item.getId());
			if (item.getMetaData("combatCharges") != null)
				player.sendMessage("<col=FF0000>It looks like it will last another " + Utils.ticksToTime(item.getMetaDataI("combatCharges")));
			player.getPackets().sendGroundItemMessage(item, ItemConfig.get(item.getId()).getExamine(item) + " General store: " + Utils.formatTypicalInteger(def.getSellPrice()) + " High Alchemy: " + Utils.formatTypicalInteger(def.getHighAlchPrice()));
			return;
		}
		player.stopAll();
		if (packet.isForceRun())
			player.setRun(packet.isForceRun());

		switch(packet.getOpcode()) {
		case GROUND_ITEM_OP1:
			break;
		case GROUND_ITEM_OP2:
			break;
		case GROUND_ITEM_OP3:
			player.setRouteEvent(new RouteEvent(item, () -> {
				final GroundItem groundItem = ChunkManager.getChunk(tile.getChunkId()).getGroundItem(packet.getObjectId(), tile, player);
				if (groundItem == null || !player.getControllerManager().canTakeItem(groundItem)) {
					player.sendMessage("Too late. It's gone!");
					return;
				}
				if (TreasureTrailsManager.isScroll(groundItem.getId()))
					if (player.getTreasureTrailsManager().hasClueScrollItem()) {
						player.sendMessage("You should finish the clue you are currently doing first.");
						return;
					}
				PickupItemEvent e2 = new PickupItemEvent(player, groundItem, false);
				PluginManager.handle(e2);
				if (!e2.isCancelPickup()) {
					player.soundEffect(2582, false);
					World.removeGroundItem(player, groundItem, true);
				}
			}, () -> {
				final GroundItem groundItem = ChunkManager.getChunk(tile.getChunkId()).getGroundItem(packet.getObjectId(), tile, player);
				if (groundItem == null || !player.getControllerManager().canTakeItem(groundItem)) {
					player.sendMessage("Too late. It's gone!");
					return true;
				}
				if (!player.lineOfSightTo(groundItem.getTile(), true)) {
					player.sendMessage("You can't reach that.");
					return true;
				}
				player.anim(833);
				player.faceTile(groundItem.getTile());
				PickupItemEvent e1 = new PickupItemEvent(player, groundItem, false);
				PluginManager.handle(e1);
				if (!e1.isCancelPickup()) {
					player.soundEffect(2582, false);
					World.removeGroundItem(player, groundItem, true);
				}
				return true;
			}));
			break;
		case GROUND_ITEM_OP4:
			player.setRouteEvent(new RouteEvent(item, () -> {
				final GroundItem groundItem = ChunkManager.getChunk(tile.getChunkId()).getGroundItem(packet.getObjectId(), tile, player);
				if (groundItem == null)
					return;

				Fire fire = Fire.forId(groundItem.getId());
				if (fire != null) {
					player.getActionManager().setAction(new Firemaking(fire, groundItem));
					return;
				}

				BoxTrapType trap = BoxTrapType.forId(groundItem.getId());
				if (trap != null) {
					player.getActionManager().setAction(new BoxAction(trap, groundItem));
                }
			}));
			break;
		case GROUND_ITEM_OP5:
			break;
		default:
			break;
		}
	}

}
