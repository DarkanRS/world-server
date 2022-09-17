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
package com.rs.game.content.skills.hunter.puropuro;

import com.rs.game.World;
import com.rs.game.content.Effect;
import com.rs.game.content.skills.hunter.FlyingEntityHunter.FlyingEntities;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.ForceMovement;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class PuroPuroController extends Controller {

	private WorldTile entranceTile;

	private static final Item[][] REQUIRED = { { new Item(11238, 3), new Item(11240, 2), new Item(11242, 1) }, { new Item(11242, 3), new Item(11244, 2), new Item(11246, 1) }, { new Item(11246, 3), new Item(11248, 2), new Item(11250, 1) }, { null } };

	private static final Item[] REWARD = { new Item(11262, 1), new Item(11259, 1), new Item(11258, 1), new Item(11260, 3) };

	public PuroPuroController(WorldTile tile) {
		this.entranceTile = tile;
	}

	@Override
	public void start() {
		player.getPackets().setBlockMinimapState(2);
		player.getInterfaceManager().sendOverlay(169);
	}

	@Override
	public void forceClose() {
		player.getPackets().setBlockMinimapState(0);
		player.getInterfaceManager().removeOverlay();
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
			Magic.sendTeleportSpell(player, 6601, -1, 1118, -1, 0, 0, entranceTile, 9, false, Magic.OBJECT_TELEPORT, null);
			return true;
		}
		return true;
	}

	public static ObjectClickHandler pushThrough = new ObjectClickHandler(new Object[] { "Magical wheat" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.isAtObject()) {
				e.getPlayer().faceObject(e.getObject());

				Direction dir = e.getPlayer().getDirection();
				int speed = Utils.randomInclusive(0, 2) * 2;
				int finalSpeed = e.getPlayer().hasEffect(Effect.FARMERS_AFFINITY) ? 3+speed : 6+speed;
				WorldTile finalTile = e.getPlayer().getFrontfacingTile(2);

				GameObject loc = World.getObject(finalTile);
				if (loc != null && loc.getDefinitions(e.getPlayer()).getName().equals("Magical wheat")) {
					e.getPlayer().sendMessage("The wheat here seems unusually stubborn. You cannot push through.");
					return;
				}

				e.getPlayer().lock();
				switch (speed) {
					case 0 -> {
						if (e.getPlayer().hasEffect(Effect.FARMERS_AFFINITY))
							e.getPlayer().sendMessage("You use your strength to push through the wheat in the most efficient fashion.");
						else
							e.getPlayer().sendMessage("You use your strength to push through the wheat.");
					}
					case 2 -> e.getPlayer().sendMessage("You push through the wheat.");
					case 4 -> e.getPlayer().sendMessage("You push through the wheat. It's hard work, though.");
				}

				WorldTasks.scheduleTimer(ticks -> {
					if (ticks == 0) {
						e.getPlayer().setNextForceMovement(new ForceMovement(finalTile, finalSpeed, dir));
						e.getPlayer().setNextAnimation(new Animation(6593 + speed/2));
					}
					if (ticks == finalSpeed) {
						e.getPlayer().unlock();
						e.getPlayer().setNextWorldTile(finalTile);
						if (e.getPlayer().getO("ppStrengthEnabled") == null)
							e.getPlayer().save("ppStrengthEnabled", true);
						if (e.getPlayer().getBool("ppStrengthEnabled"))
							e.getPlayer().getSkills().addXp(Skills.STRENGTH, 4-speed);
						return false;
					}
					return true;
				});
			}
		}
	};

	public static NPCClickHandler handleElnock = new NPCClickHandler(new Object[] { 6070 }) {
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

	public static boolean isRareImpling(int npcId) {
		if (npcId == FlyingEntities.KINGLY_IMPLING_PP.getNpcId() || npcId == FlyingEntities.ZOMBIE_IMPLING_PP.getNpcId() || npcId == FlyingEntities.DRAGON_IMPLING_PP.getNpcId() || npcId == FlyingEntities.PIRATE_IMPLING_PP.getNpcId() || npcId == FlyingEntities.NINJA_IMPLING_PP.getNpcId() || npcId == FlyingEntities.MAGPIE_IMPLING_PP.getNpcId() || npcId == FlyingEntities.NATURE_IMPLING_PP.getNpcId())
			return true;
		return false;
	}

	public static void openPuroInterface(final Player player) {
		player.getInterfaceManager().sendInterface(540);
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
			for (Item item : player.getInventory().getItems().getItemsNoNull()) {
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