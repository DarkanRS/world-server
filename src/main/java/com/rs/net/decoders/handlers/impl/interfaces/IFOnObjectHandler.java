package com.rs.net.decoders.handlers.impl.interfaces;

import com.rs.game.World;
import com.rs.game.object.GameObject;
import com.rs.game.pathing.RouteEvent;
import com.rs.game.player.Inventory;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.magic.Lunars;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.interfaces.IFOnObject;
import com.rs.lib.util.Utils;
import com.rs.net.decoders.handlers.ObjectHandler;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.InterfaceOnObjectEvent;

public class IFOnObjectHandler implements PacketHandler<Player, IFOnObject> {
	
	@Override
	public void handle(Player player, IFOnObject packet) {
		if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead())
			return;
		if (player.isLocked() || player.getEmotesManager().isAnimating())
			return;
		final WorldTile tile = new WorldTile(packet.getX(), packet.getY(), player.getPlane());
		int regionId = tile.getRegionId();
		if (!player.getMapRegionsIds().contains(regionId))
			return;
		GameObject mapObject = World.getObjectWithId(tile, packet.getObjectId());
		if (mapObject == null || mapObject.getId() != packet.getObjectId())
			return;
		final GameObject object = mapObject;
		if (player.isDead() || Utils.getInterfaceDefinitionsSize() <= packet.getInterfaceId())
			return;
		if (player.isLocked())
			return;
		if (!player.getInterfaceManager().containsInterface(packet.getInterfaceId()))
			return;
		player.stopAll();
		if (packet.isForceRun())
			player.setRun(true);
		switch (packet.getInterfaceId()) {
		case 430:
			player.stopAll(true);
			if (packet.getComponentId() == 55) {
				Lunars.handleCurePlant(player, object);
			} else if (packet.getComponentId() == 24) {
				Lunars.handleFertileSoil(player, object);
			}
			break;
		case Inventory.INVENTORY_INTERFACE: // inventory
			final Item item = player.getInventory().getItem(packet.getSlotId());
			if (item == null || item.getId() != packet.getItemId())
				return;
			ObjectHandler.handleItemOnObject(player, object, packet.getInterfaceId(), item, packet.getSlotId());
			break;
		default:
			PluginManager.handle(new InterfaceOnObjectEvent(player, object, packet.getInterfaceId(), packet.getComponentId(), packet.getSlotId(), false));
			player.setRouteEvent(new RouteEvent(object, new Runnable() {
				@Override
				public void run() {
					player.faceObject(object);
					PluginManager.handle(new InterfaceOnObjectEvent(player, object, packet.getInterfaceId(), packet.getComponentId(), packet.getSlotId(), true));
				}
			}));
			break;
		}
	}
}
