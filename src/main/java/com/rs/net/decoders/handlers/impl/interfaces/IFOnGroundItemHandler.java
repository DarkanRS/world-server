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
import com.rs.game.content.Effect;
import com.rs.game.content.minigames.treasuretrails.TreasureTrailsManager;
import com.rs.game.content.skills.firemaking.Firemaking;
import com.rs.game.content.skills.firemaking.Firemaking.Fire;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.skills.magic.Rune;
import com.rs.game.content.skills.magic.RuneSet;
import com.rs.game.map.ChunkManager;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.Constants;
import com.rs.lib.game.*;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.interfaces.IFOnGroundItem;
import com.rs.lib.util.Utils;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.PickupItemEvent;

public class IFOnGroundItemHandler implements PacketHandler<Player, IFOnGroundItem> {

	@Override
	public void handle(Player player, IFOnGroundItem packet) {
		if ((Utils.getInterfaceDefinitionsSize() <= packet.getInterfaceId()) || player.isDead() || !player.getInterfaceManager().topOpen(packet.getInterfaceId()))
			return;

		if (packet.getComponentId() != 65535 && Utils.getInterfaceDefinitionsComponentsSize(packet.getInterfaceId()) <= packet.getComponentId())
			return;
		final Tile tile = Tile.of(packet.getX(), packet.getY(), player.getPlane());
		if (!player.getMapChunkIds().contains(tile.getChunkId()))
			return;
		GroundItem groundItem = ChunkManager.getChunk(tile.getChunkId()).getGroundItem(packet.getItemId(), tile, player);
		if (groundItem == null)
			return;
		player.stopAll();
		if (packet.isForceRun())
			player.setRun(packet.isForceRun());
		if (packet.getInterfaceId() == 679) {
			Item item = player.getInventory().getItem(packet.getSlotId());
			if (item == null)
				return;

			player.setRouteEvent(new RouteEvent(groundItem, () -> {
				if (item.getId() == 590) {
					Fire fire = Fire.forId(groundItem.getId());
					if (fire != null)
						player.getActionManager().setAction(new Firemaking(fire, groundItem));
				}
			}));
		} else if (packet.getInterfaceId() == 192 && packet.getComponentId() == 44)
			player.getActionManager().setAction(new PlayerAction() {
				@Override
				public boolean start(Player player) {
					return true;
				}

				public boolean process() {
					if (player.isDead() || player.hasFinished())
						return false;
					final GroundItem item = ChunkManager.getChunk(tile.getChunkId()).getGroundItem(packet.getItemId(), tile, player);
					if ((item == null) || (player.getPlane() != tile.getPlane()))
						return false;
					if (player.hasEffect(Effect.FREEZE))
						return true;
					if (!player.lineOfSightTo(tile, false) || Utils.getDistance(player.getTile(), tile) > 8) {
						if (player.hasWalkSteps())
							player.resetWalkSteps();
						player.calcFollow(tile, 25, true);
						return true;
					}
					if (TreasureTrailsManager.isScroll(item.getId()))
						if (player.getTreasureTrailsManager().hasClueScrollItem()) {
							player.sendMessage("You should finish the clue you are currently doing first.");
							return false;
						}
					if (Magic.checkMagicAndRunes(player, 33, true, new RuneSet(Rune.AIR, 1, Rune.LAW, 1))) {
						player.getActionManager().setActionDelay(3);
						player.resetWalkSteps();
						player.setNextFaceTile(tile);
						player.setNextAnimation(new Animation(711));
						player.getSkills().addXp(Constants.MAGIC, 43);
						player.setNextSpotAnim(new SpotAnim(142, 2, 50, Utils.getAngleTo(tile.getX() - player.getX(), tile.getY() - player.getY())));
						World.sendProjectile(player, tile, 143, 35, 0, 60, 1, 0, 0, p -> {
							final GroundItem gItem = ChunkManager.getChunk(tile.getChunkId()).getGroundItem(packet.getItemId(), tile, player);
							if (gItem == null) {
								player.sendMessage("Too late. It's gone!");
								return;
							}
							World.sendSpotAnim(tile, new SpotAnim(144));
							PickupItemEvent e2 = new PickupItemEvent(player, gItem, true);
							PluginManager.handle(e2);
							if (!e2.isCancelPickup())
								World.removeGroundItem(player, gItem, true);
						});
					}
					return false;
				}

				@Override
				public boolean process(Player player) {
					return process();
				}

				@Override
				public int processWithDelay(Player player) {
					return 0;
				}

				@Override
				public void stop(Player player) {

				}
			});
	}
}
