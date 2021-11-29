package com.rs.net.decoders.handlers.impl;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.pathing.RouteEvent;
import com.rs.game.player.Player;
import com.rs.game.player.content.Effect;
import com.rs.game.player.content.skills.firemaking.Firemaking;
import com.rs.game.player.content.skills.firemaking.Firemaking.Fire;
import com.rs.game.player.content.skills.hunter.BoxAction;
import com.rs.game.player.content.skills.hunter.BoxTrapType;
import com.rs.game.player.managers.TreasureTrailsManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.GroundItemOp;
import com.rs.lib.util.Utils;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.PickupItemEvent;
import com.rs.utils.ItemExamines;

public class GroundItemOpHandler implements PacketHandler<Player, GroundItemOp> {
	
	@Override
	public void handle(Player player, GroundItemOp packet) {
		if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead())
			return;
		if (player.isLocked() || player.hasEffect(Effect.FREEZE))
			return;
		
		final WorldTile tile = new WorldTile(packet.getX(), packet.getY(), player.getPlane());
		final int regionId = tile.getRegionId();
		if (!player.getMapRegionsIds().contains(regionId))
			return;
		final GroundItem item = World.getRegion(regionId).getGroundItem(packet.getObjectId(), tile, player);
		if (item == null)
			return;
		player.stopAll();
		if (packet.isForceRun())
			player.setRun(packet.isForceRun());
		
		switch(packet.getOpcode()) {
		case GROUND_ITEM_EXAMINE:
			ItemDefinitions def = ItemDefinitions.getDefs(item.getId());
			if (item.getMetaData("combatCharges") != null)
				player.sendMessage("<col=FF0000>It looks like it will last another " + Utils.ticksToTime(item.getMetaDataI("combatCharges")));
			player.getPackets().sendGroundItemMessage(player, item, ItemExamines.getExamine(item) + " General store: " + Utils.formatTypicalInteger(def.getSellPrice()) + " High Alchemy: " + Utils.formatTypicalInteger(def.getHighAlchPrice()));
			break;
		case GROUND_ITEM_OP1:
			break;
		case GROUND_ITEM_OP2:
			break;
		case GROUND_ITEM_OP3:
			player.setRouteEvent(new RouteEvent(item, new Runnable() {
				@Override
				public void run() {
					final GroundItem item = World.getRegion(regionId).getGroundItem(packet.getObjectId(), tile, player);
					if (item == null || !player.getControllerManager().canTakeItem(item))
						return;
					if (TreasureTrailsManager.isScroll(item.getId())) {
						if (player.getTreasureTrailsManager().hasClueScrollItem()) {
							player.sendMessage("You should finish the clue you are currently doing first.");
							return;
						}
					}
					if (!World.checkWalkStep(player, item.getTile())) {
						player.setNextAnimation(new Animation(833));
						player.setNextFaceWorldTile(item.getTile());
						player.lock(1);
						PickupItemEvent e = new PickupItemEvent(player, item);
						PluginManager.handle(e);
						if (!e.isCancelPickup())
							World.removeGroundItem(player, item, true);
					} else {
						PickupItemEvent e = new PickupItemEvent(player, item);
						PluginManager.handle(e);
						if (!e.isCancelPickup())
							World.removeGroundItem(player, item, true);
					}
				}
			}));
			break;
		case GROUND_ITEM_OP4:
			player.setRouteEvent(new RouteEvent(item, new Runnable() {
				@Override
				public void run() {
					final GroundItem groundItem = World.getRegion(regionId).getGroundItem(packet.getObjectId(), tile, player);
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
						return;
					}
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
