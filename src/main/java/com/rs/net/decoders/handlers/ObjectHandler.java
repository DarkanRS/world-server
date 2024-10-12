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
package com.rs.net.decoders.handlers;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.engine.pathfinder.RouteEvent;
import com.rs.game.content.minigames.ectofuntus.Ectofuntus;
import com.rs.game.content.skills.dungeoneering.rooms.puzzles.FishingFerretRoom;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.events.ObjectClickEvent;

import java.util.Arrays;

public final class ObjectHandler {

	public static void handleOption1(final Player player, final GameObject object) {
		final ObjectDefinitions objectDef = object.getDefinitions(player);
		if (!objectDef.containsOption(0) || PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP1, false)))
			return;

		player.setRouteEvent(new RouteEvent(object, () -> {
			player.stopAll();
			player.faceObject(object);
			if (!player.getControllerManager().processObjectClick1(object) || player.getTreasureTrailsManager().useObject(object))
				return;
			PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP1, true));
        }));
		Logger.debug(ObjectHandler.class, "handleOption1", "Object interaction 1: " + object);
	}

	public static void handleOption2(final Player player, final GameObject object) {
		final ObjectDefinitions def = object.getDefinitions(player);

		if (!def.containsOption(1) || PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP2, false)))
			return;

		player.setRouteEvent(new RouteEvent(object, () -> {
			player.stopAll();
			player.faceObject(object);
			if (!player.getControllerManager().processObjectClick2(object) || player.getTreasureTrailsManager().useObject(object))
				return;
			PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP2, true));
		}));
		Logger.debug(ObjectHandler.class, "handleOption2", "Object interaction 2: " + object);
	}

	public static void handleOption3(final Player player, final GameObject object) {
		final ObjectDefinitions def = object.getDefinitions(player);

		if (!def.containsOption(2) || PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP3, false)))
			return;

		player.setRouteEvent(new RouteEvent(object, () -> {
			player.stopAll();
			player.faceObject(object);
			if (!player.getControllerManager().processObjectClick3(object))
				return;
			PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP3, true));
		}));
		Logger.debug(ObjectHandler.class, "handleOption3", "Object interaction 3: " + object);
	}

	public static void handleOption4(final Player player, final GameObject object) {
		final ObjectDefinitions def = object.getDefinitions(player);

		if (!def.containsOption(3) || PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP4, false)))
			return;

		player.setRouteEvent(new RouteEvent(object, () -> {
			player.stopAll();
			player.faceObject(object);
			if (!player.getControllerManager().processObjectClick4(object) || PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP4, true)))
				return;
			player.sendMessage("Nothing interesting happens.");
			Logger.debug(ObjectHandler.class, "handleOption4", "Object interaction 4: " + object);
		}));
	}

	public static void handleOption5(final Player player, final GameObject object) {
		if (!object.getDefinitions(player).containsOption(4) || PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP5, false)))
			return;

		player.setRouteEvent(new RouteEvent(object, () -> {
			player.stopAll();
			player.faceObject(object);
			if (!player.getControllerManager().processObjectClick5(object) || PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP5, true)))
				return;
			if (object.getId() != -1)
				player.sendMessage("Nothing interesting happens.");
			Logger.debug(ObjectHandler.class, "handleOption5", "Object interaction 5: " + object);
		}));
	}

	public static void handleOptionExamine(final Player player, final GameObject object) {
		player.getPackets().sendObjectMessage(0, 0xFFFFFF, object, "It's " + Utils.addArticle(object.getDefinitions(player).getName()).toLowerCase() + ".");
		if (player.hasRights(Rights.DEVELOPER)) {
			player.sendMessage(object.toString());
			if (object.getDefinitions().varpBit != -1)
				player.sendMessage("Transforms with varbit " + object.getDefinitions().varpBit + " - current obj: " + object.getDefinitions(player).id);
			if (object.getDefinitions().varp != -1)
				player.sendMessage("Transforms with var " + object.getDefinitions().varp + " - current obj: " + object.getDefinitions(player).id);
			if (object.getMeshModifier() != null)
				player.sendMessage(Arrays.toString(object.getMeshModifier().getModelIds()));
		}
	}

	public static void handleItemOnObject(final Player player, final GameObject object, final int interfaceId, final Item item, final int slot) {
		final int itemId = item.getId();
		final ObjectDefinitions objectDef = object.getDefinitions(player);

		if (PluginManager.handle(new ItemOnObjectEvent(player, item, object, false)) || FishingFerretRoom.handleFerretThrow(player, object, item))
			return;

		player.setRouteEvent(new RouteEvent(object, () -> {
			player.faceObject(object);
			if (!player.getControllerManager().handleItemOnObject(object, item) || Ectofuntus.handleItemOnObject(player, itemId, object.getId()))
				return;
			PluginManager.handle(new ItemOnObjectEvent(player, item, object, true));
		}));
	}
}