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
package com.rs.game.content.skills.summoning;

import java.util.Arrays;

import com.rs.cache.loaders.EnumDefinitions;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.interfaces.IFEvents;
import com.rs.game.content.ItemConstants;
import com.rs.game.content.skills.dungeoneering.DungeonController;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ItemOnItemEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;

@PluginEventHandler
public class Summoning {

	public static enum ScrollTarget {
		ITEM, COMBAT, CLICK, OBJECT, ENTITY
	}

	public static final int POUCHES_INTERFACE = 672, SCROLLS_INTERFACE = 666;
	private static final int SCROLL_ANIM = 723;
	private static final int POUCH_ANIM = 725;
	private static final int POUCH_SPOTANIM = 1207;

	public static boolean isFamiliar(int npcId) {
		return EnumDefinitions.getEnum(1320).getValues().containsValue(npcId);
	}

	public static boolean isFollower(int npcId) {
		return EnumDefinitions.getEnum(1279).getValues().containsKey((long) npcId);
	}
	
	public static ItemOnItemHandler handleCarveTurnip = new ItemOnItemHandler(946, 12134) {
		@Override
		public void handle(ItemOnItemEvent e) {
			e.getPlayer().repeatAction(2, () -> {
				if (!e.getPlayer().getInventory().containsItem(946, 1)) {
					e.getPlayer().sendMessage("You need a knife to cut the turnip.");
					return false;
				}
				if (!e.getPlayer().getInventory().containsItem(12134))
					return false;
				e.getPlayer().getInventory().deleteItem(12134, 1);
				e.getPlayer().getInventory().addItem(12153, 1);
				e.getPlayer().anim(6702);
				return true;
			});
		}
	};

	public static ItemClickHandler handleSummonOps = new ItemClickHandler(Arrays.stream(Pouch.values()).map(p -> p.getId()).toArray(), new String[] { "Summon" }) {
		@Override
		public void handle(ItemClickEvent e) {
			Pouch pouches = Pouch.forId(e.getItem().getId());
			if (pouches != null) {
				if (e.getPlayer().getSkills().getLevelForXp(Constants.SUMMONING) >= pouches.getLevel())
					spawnFamiliar(e.getPlayer(), pouches);
				else
					e.getPlayer().sendMessage("You need a summoning level of " + pouches.getLevel() + " to summon this familiar.");
			}
		}
	};
	
	private static void spawnFamiliar(Player player, Pouch pouch) {
		if (player.getFamiliar() != null || player.getPet() != null) {
			player.sendMessage("You already have a follower.");
			return;
		}
		if (!player.getControllerManager().canSummonFamiliar())
			return;
		if (player.getSkills().getLevel(Constants.SUMMONING) < pouch.getSummoningCost()) {
			player.sendMessage("You do not have enought summoning points to spawn this.");
			return;
		}
		if (player.getSkills().getLevelForXp(Constants.SUMMONING) < pouch.getLevel()) {
			player.sendMessage("You need a summoning level of " + pouch.getLevel() + " in order to use this pouch.");
			return;
		}
		WorldTile spawnTile = player.getNearestTeleTile(NPCDefinitions.getDefs(pouch.getBaseNpc(), player.getVars()).size);
		if (spawnTile == null) {
			player.sendMessage("Theres not enough space to summon your familiar here.");
			return;
		}
		player.getInventory().deleteItem(pouch.getId(), 1);
		player.getSkills().drainSummoning(pouch.getSummoningCost());
		player.setFamiliar(new Familiar(player, pouch, spawnTile, -1, true));
		player.getFamiliar().sendFollowerDetails();
	}

	public static boolean hasPouch(Player player) {
		for (Pouch pouch : Pouch.values())
			if (player.getInventory().containsOneItem(pouch.getId()))
				return true;
		return false;
	}

	public static void openInfusionInterface(Player player, boolean dung) {
		player.getInterfaceManager().sendInterface(POUCHES_INTERFACE);
		player.getPackets().sendPouchInfusionOptionsScript(dung, POUCHES_INTERFACE, 16, 78, 8, 10, "Infuse<col=FF9040>", "Infuse-5<col=FF9040>", "Infuse-10<col=FF9040>", "Infuse-X<col=FF9040>", "Infuse-All<col=FF9040>", "List<col=FF9040>");
		player.getPackets().setIFEvents(new IFEvents(POUCHES_INTERFACE, 16, 0, 462).enableRightClickOptions(0,1,2,3,4,6));
	}

	public static ButtonClickHandler handleDungeoneeringPouchButtons = new ButtonClickHandler(672) {
		static int getPouchID(int slot) {//From blank
			slot = (slot+3)/5 + 5;
			switch (slot % 6) {
				case 0 -> {
					return slot / 6 + 17934;
				}
				case 1 -> {
					return slot / 6 + 17984;
				}
				case 2 -> {
					return slot / 6 + 17944;
				}
				case 3 -> {
					return slot / 6 + 17954;
				}
				case 4 -> {
					return slot / 6 + 17974;
				}
				case 5 -> {
					return slot / 6 + 17964;
				}
			}
			return -1;
		}
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 16) {
				Pouch pouch = Pouch.forId(e.getSlotId2());
				if (pouch == null) {
					if(e.getPlayer().getControllerManager().isIn(DungeonController.class))
						e.getPlayer().sendMessage("You need " + getMaterialListString(Pouch.forId(getPouchID(e.getSlotId()))) + " to create this pouch.");
					if(!e.getPlayer().getControllerManager().isIn(DungeonController.class))
						;//e.getPlayer().sendMessage("You need " + getMaterialListString(Pouch.forId(getPouchID(e.getSlotId()))) + " to create this pouch.");
					return;
				}
				if (e.getPacket() == ClientPacket.IF_OP1)
					handlePouchInfusion(e.getPlayer(), pouch, 1);
				else if (e.getPacket() == ClientPacket.IF_OP2)
					handlePouchInfusion(e.getPlayer(), pouch, 5);
				else if (e.getPacket() == ClientPacket.IF_OP3)
					handlePouchInfusion(e.getPlayer(), pouch, 10);
				else if (e.getPacket() == ClientPacket.IF_OP4)
					handlePouchInfusion(e.getPlayer(), pouch, Integer.MAX_VALUE);
				else if (e.getPacket() == ClientPacket.IF_OP5)
					handlePouchInfusion(e.getPlayer(), pouch, 28);
				else if (e.getPacket() == ClientPacket.IF_OP7)
					e.getPlayer().sendMessage("You need " + getMaterialListString(pouch) + " to create this pouch.");
			} else if (e.getComponentId() == 19 && e.getPacket() == ClientPacket.IF_OP1)
				openScrollInfusionInterface(e.getPlayer(), e.getPlayer().getControllerManager().isIn(DungeonController.class));
		}
	};
	
	public static void openScrollInfusionInterface(Player player, boolean dung) {
		player.getInterfaceManager().sendInterface(SCROLLS_INTERFACE);
		player.getPackets().sendScrollInfusionOptionsScript(dung, SCROLLS_INTERFACE, 16, 78, 8, 10, "Transform<col=FF9040>", "Transform-5<col=FF9040>", "Transform-10<col=FF9040>", "Transform-All<col=FF9040>", "Transform-X<col=FF9040>");
		player.getPackets().setIFEvents(new IFEvents(SCROLLS_INTERFACE, 16, 0, 462).enableRightClickOptions(0,1,2,3,4,5));
	}

	public static ButtonClickHandler handleScrollButtons = new ButtonClickHandler(666) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 16) {
				if (e.getPacket() == ClientPacket.IF_OP1)
					createScroll(e.getPlayer(), e.getSlotId2(), 1);
				else if (e.getPacket() == ClientPacket.IF_OP2)
					createScroll(e.getPlayer(), e.getSlotId2(), 5);
				else if (e.getPacket() == ClientPacket.IF_OP3)
					createScroll(e.getPlayer(), e.getSlotId2(), 10);
				else if (e.getPacket() == ClientPacket.IF_OP4)
					createScroll(e.getPlayer(), e.getSlotId2(), Integer.MAX_VALUE);
			} else if (e.getComponentId() == 18 && e.getPacket() == ClientPacket.IF_OP1)
				openInfusionInterface(e.getPlayer(), e.getPlayer().getControllerManager().isIn(DungeonController.class));
		}
	};

	public static void createScroll(Player player, int itemId, int amount) {
		Scroll scroll = Scroll.forId(itemId);
		if (scroll == null) {
			player.sendMessage("You do not have the pouch required to create this scroll.");
			return;
		}
		if (player.getSkills().getLevelForXp(Constants.SUMMONING) < scroll.fromPouches().get(0).getLevel()) {
			player.sendMessage("You need a summoning level of " + scroll.fromPouches().get(0).getLevel() + " to create " + amount + " " + ItemDefinitions.getDefs(scroll.getId()).getName().toLowerCase() + "s.");
			return;
		}
		boolean hasReqs = false;
		for (Pouch pouch : scroll.fromPouches()) {
			if (player.getInventory().containsItem(pouch.getId()))
				hasReqs = true;
		}
		if (!hasReqs) {
			player.sendMessage("You do not have the pouch required to create this scroll.");
			return;
		}
		for (int i = 0;i < amount;i++) {
			Pouch pouch = null;
			for (Pouch p : scroll.fromPouches()) {
				if (player.getInventory().containsItem(p.getId())) {
					pouch = p;
					break;
				}
			}
			if (pouch == null)
				break;
			player.getInventory().deleteItem(pouch.getId(), 1);
			player.getInventory().addItem(pouch.getScroll().getId(), 10);
			player.getSkills().addXp(Constants.SUMMONING, pouch.getScroll().getXp());
		}
		player.anim(SCROLL_ANIM);
	}


	public static void handlePouchInfusion(Player player, Pouch pouch, int creationCount) {
		if (ItemConstants.isDungItem(pouch.getId()) && !player.getControllerManager().isIn(DungeonController.class))
			return;
		if (!ItemConstants.isDungItem(pouch.getId()) && player.getControllerManager().isIn(DungeonController.class))
			return;
		Item[] itemReq = pouch.getMaterialList().get();
		for (int i = 0; i < creationCount; i++) {
			if (!player.getInventory().containsItems(itemReq)) {
				player.sendMessage("You need " + getMaterialListString(pouch) + " to create this pouch.");
				return;
			}
			if (player.getSkills().getLevelForXp(Constants.SUMMONING) < pouch.getLevel()) {
				player.sendMessage("You need a summoning level of " + pouch.getLevel() + " to create this pouch.");
				return;
			}
			player.getInventory().removeItems(itemReq);
			player.getInventory().addItem(new Item(pouch.getId(), 1));
			player.getSkills().addXp(Constants.SUMMONING, pouch.getExperience());
		}
		player.sync(POUCH_ANIM, POUCH_SPOTANIM);
	}

	public static String getMaterialListString(Pouch pouch) {
		String list = "";
		if (pouch == null)
			return "nothing";
		Item[] items = pouch.getMaterialList().get();
		for (int i = 0;i < items.length;i++)
			list += items[i].getAmount() + " " + items[i].getDefinitions().name.toLowerCase() + ((i == items.length - 1) ? "" : ", ");
		return list;
	}
}
