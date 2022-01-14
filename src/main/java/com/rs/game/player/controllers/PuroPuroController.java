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
package com.rs.game.player.controllers;

import com.rs.game.ForceMovement;
import com.rs.game.World;
import com.rs.game.object.GameObject;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.hunter.FlyingEntityHunter.FlyingEntities;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class PuroPuroController extends Controller {

	private static final Item[][] REQUIRED = { { new Item(11238, 3), new Item(11240, 2), new Item(11242, 1) }, { new Item(11242, 3), new Item(11244, 2), new Item(11246, 1) }, { new Item(11246, 3), new Item(11248, 2), new Item(11250, 1) }, { null } };

	private static final Item[] REWARD = { new Item(11262, 1), new Item(11259, 1), new Item(11258, 1), new Item(11260, 3) };

	@Override
	public void start() {
		player.getPackets().setBlockMinimapState(2);
	}

	@Override
	public void forceClose() {
		player.getPackets().setBlockMinimapState(0);
	}

	@Override
	public void magicTeleported(int type) {
		player.getControllerManager().forceStop();
	}

	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public boolean login() {
		start();
		return false;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		switch (object.getId()) {
		case 25014:
			player.getControllerManager().forceStop();
			Magic.sendTeleportSpell(player, 6601, -1, 1118, -1, 0, 0, new WorldTile(2427, 4446, 0), 9, false, Magic.OBJECT_TELEPORT);
			return true;
		}
		return true;
	}

	public static void pushThrough(final Player player, GameObject object) {
		int objectX = object.getX();
		int objectY = object.getY();
		Direction direction = Direction.NORTH;
		if (player.getX() == objectX && player.getY() < objectY) {
			objectY = objectY + 1;
			direction = Direction.NORTH;
		} else if (player.getX() == objectX && player.getY() > objectY) {
			objectY = objectY - 1;
			direction = Direction.SOUTH;
		} else if (player.getY() == objectY && player.getX() < objectX) {
			objectX = objectX + 1;
			direction = Direction.EAST;
		} else if (player.getY() == objectY && player.getX() > objectX) {
			objectX = objectX - 1;
			direction = Direction.WEST;
		} else {
			objectY = objectY - 1;
			objectX = objectX + 1;
			direction = Direction.SOUTHEAST;
		}
		player.sendMessage(Utils.getRandomInclusive(2) == 0 ? "You use your strength to push through the wheat in the most efficient fashion." : "You use your strength to push through the wheat.");
		player.setNextFaceWorldTile(object);
		player.setNextAnimation(new Animation(6594));
		player.lock();
		final WorldTile tile = new WorldTile(objectX, objectY, 0);
		player.setNextFaceWorldTile(object);
		player.setNextForceMovement(new ForceMovement(tile, 6, direction));
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				player.unlock();
				player.setNextWorldTile(tile);
			}
		}, 6);
	}

	public static NPCClickHandler handleElnock = new NPCClickHandler(6070) {
		@Override
		public void handle(NPCClickEvent e) {
			PuroPuroController.openPuroInterface(e.getPlayer());
		}
	};

	public static ButtonClickHandler handlePuroPuroShopButtons = new ButtonClickHandler(540) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 69)
				confirmPuroSelection(e.getPlayer());
			else if (e.getComponentId() == 71)
				ShopsHandler.openShop(e.getPlayer(), "elnocks_backup_supply");
			else
				handlePuroInterface(e.getPlayer(), e.getComponentId());
		}
	};

	public static WorldTile getRandomTile() {
		return new WorldTile(Utils.random(2558 + 3, 2626 - 3), Utils.random(4285 + 3, 4354 - 3), 0);
	}

	public static int getRandomImplingId() {
		FlyingEntities[] implings = FlyingEntities.values();
		int random = Utils.getRandomInclusive(1000);
		if (random < 3)
			return implings[Utils.random(10, 14)].getNpcId();
		if (random < 80)
			return implings[Utils.random(4, 10)].getNpcId();
		if (random < 300)
			return implings[Utils.random(7)].getNpcId();
		return implings[Utils.getRandomInclusive(5)].getNpcId();
	}

	public static void initPuroImplings() {
		for (int i = 0; i < 5; i++)
			for (int index = 0; index < 11; index++) {
				if (i > 2)
					if (Utils.getRandomInclusive(1) == 0)
						continue;
				World.spawnNPC(PuroPuroController.getRandomImplingId(), PuroPuroController.getRandomTile(), -1, false);
			}
	}

	public static void openPuroInterface(final Player player) {
		player.getInterfaceManager().sendInterface(540); // puro puro
		for (int component = 60; component < 64; component++)
			player.getPackets().setIFHidden(540, component, false);
		player.setCloseInterfacesEvent(() -> player.getTempAttribs().removeI("puro_slot"));
	}

	public static void handlePuroInterface(Player player, int componentId) {
		player.getTempAttribs().setI("puro_slot", (componentId - 20) / 2);
	}

	public static void confirmPuroSelection(Player player) {
		if (player.getTempAttribs().getI("puro_slot") == -1)
			return;
		int slot = player.getTempAttribs().getI("puro_slot");
		Item exchangedItem = REWARD[slot];
		Item[] requriedItems = REQUIRED[slot];
		if (slot == 3) {
			requriedItems = null;
			for (Item item : player.getInventory().getItems().getItems()) {
				if (item == null || FlyingEntities.forItem((short) item.getId()) == null)
					continue;
				requriedItems = new Item[] { item };
			}
		}
		if (requriedItems == null || !player.getInventory().containsItems(requriedItems)) {
			player.sendMessage("You don't have the required items.");
			return;
		}
		if (player.getInventory().addItem(exchangedItem.getId(), exchangedItem.getAmount())) {
			player.getInventory().removeItems(requriedItems);
			player.closeInterfaces();
			player.sendMessage("You exchange the required items for: " + exchangedItem.getName().toLowerCase() + ".");
		}
	}
}
