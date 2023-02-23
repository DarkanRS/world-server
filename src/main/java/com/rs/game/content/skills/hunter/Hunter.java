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
package com.rs.game.content.skills.hunter;

import com.rs.cache.loaders.interfaces.IFEvents;
import com.rs.game.content.skills.hunter.traps.BoxStyleTrap;
import com.rs.game.content.skills.hunter.traps.BoxStyleTrap.Status;
import com.rs.game.content.skills.hunter.traps.NetTrap;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.object.OwnedObject;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public final class Hunter {

	public static final Animation CAPTURE_ANIMATION = new Animation(6606);

	public static final Animation TEASING_STICK_ANIM = new Animation(5236);
	public static final Animation NOOSE_WAND_ANIM = new Animation(3297);

	public static ItemClickHandler handleBoxTypeTraps = new ItemClickHandler(new Object[] { 10006, 10008, 10025, 19965 }, new String[] { "Lay", "Activate" }, e -> {
		switch(e.getItem().getId()) {
		case 10006:
			e.getPlayer().getActionManager().setAction(new BoxAction(BoxTrapType.BIRD_SNARE));
			break;
		case 10008:
			e.getPlayer().getActionManager().setAction(new BoxAction(BoxTrapType.BOX));
			break;
		case 10025:
			e.getPlayer().getActionManager().setAction(new BoxAction(BoxTrapType.MAGIC_BOX));
			break;
		case 19965:
			e.getPlayer().getActionManager().setAction(new BoxAction(BoxTrapType.MARASAMAW_PLANT));
			break;
		}
	});

	public static ItemClickHandler handleImpInABox = new ItemClickHandler(new Object[] { 10027, 10028 }, e -> {
		if (e.getOption().equals("Bank")) {
			e.getPlayer().getInterfaceManager().sendInterface(478);
			e.getPlayer().getPackets().setIFEvents(new IFEvents(478, 14, 0, 27).enableRightClickOptions(0, 1));
		}
	});

	public static ItemClickHandler releaseLizards = new ItemClickHandler(new Object[] { 10149, 10146, 10147, 10148 }, new String[] { "Release" }, e -> {
		e.getPlayer().startConversation(new Dialogue().addOptions("How many would you like to release?", new Options() {
			@Override
			public void create() {
				if (e.getPlayer().getInventory().getAmountOf(e.getItem().getId()) > 1)
					option("All", () -> {
						e.getPlayer().getInventory().deleteItem(e.getItem().getId(), e.getPlayer().getInventory().getAmountOf(e.getItem().getId()));
						e.getPlayer().sendMessage("You release the " + (e.getItem().getId() == 10149 ? "lizards" : "salamanders") + " and they dart away.");
					});
				option("One", () -> {
					e.getPlayer().getInventory().deleteItem(e.getItem());
					e.getPlayer().sendMessage("You release the " + (e.getItem().getId() == 10149 ? "lizard" : "salamander") + " and it darts away.");
				});
			}
		}));
	});

	public static ItemOnItemHandler craftPotion = new ItemOnItemHandler(new int[] { 10027, 10028 }, e -> {
		Item usedWith = e.getUsedWith(10027, 10028);
		if (usedWith == null)
			return;
		if (usedWith.getId() == 10027 || usedWith.getId() == 10028) {
			e.getPlayer().sendMessage("You cannot bank an imp using an imp!");
			return;
		}
		boolean costRemoved = false;
		if (e.getPlayer().getInventory().containsItem(10028, 1)) {
			e.getPlayer().getInventory().deleteItem(10028, 1);
			e.getPlayer().getInventory().addItem(10025, 1);
			costRemoved = true;
		} else if (e.getPlayer().getInventory().containsItem(10027, 1)) {
			e.getPlayer().getInventory().deleteItem(10027, 1);
			e.getPlayer().getInventory().addItem(10028, 1);
			costRemoved = true;
		}
		if (costRemoved)
			e.getPlayer().getBank().depositItem(usedWith.getSlot(), usedWith.getAmount(), true);
	});

	public static ButtonClickHandler handleImpInABoxButtons = new ButtonClickHandler(478, e -> {
		if (e.getComponentId() == 14) {
			Item item = e.getPlayer().getInventory().getItem(e.getSlotId());
			if (item == null)
				return;
			if (item.getId() == 10027 || item.getId() == 10028) {
				e.getPlayer().sendMessage("You cannot bank an imp using an imp!");
				return;
			}
			boolean costRemoved = false;
			if (e.getPlayer().getInventory().containsItem(10028, 1)) {
				e.getPlayer().getInventory().deleteItem(10028, 1);
				e.getPlayer().getInventory().addItem(10025, 1);
				costRemoved = true;
			} else if (e.getPlayer().getInventory().containsItem(10027, 1)) {
				e.getPlayer().getInventory().deleteItem(10027, 1);
				e.getPlayer().getInventory().addItem(10028, 1);
				costRemoved = true;
			}
			if (costRemoved)
				e.getPlayer().getBank().depositItem(e.getSlotId(), item.getAmount(), true);
			else
				e.getPlayer().sendMessage("You are unable to bank any more items.");
		}
	});

	public static ObjectClickHandler handleTrapCheck = new ObjectClickHandler(new Object[] { "Net trap", "Magic box", "Magic box failed", "Bird snare", "Box trap", "Shaking box", "Marasamaw plant", "Wilted marasamaw plant", "Shaking marasamaw plant" }, e -> {
		if (!(e.getObject() instanceof BoxStyleTrap))
			return;
		BoxStyleTrap trap = (BoxStyleTrap) e.getObject();
		if (!trap.ownedBy(e.getPlayer())) {
			e.getPlayer().sendMessage("This isn't your trap.");
			return;
		}
		if (e.getOpNum() == ClientPacket.OBJECT_OP1)
			if (trap.getStatus() == Status.FAIL || trap.getStatus() == Status.IDLE)
				trap.dismantle(e.getPlayer());
			else if (trap.getStatus() == Status.SUCCESS)
				trap.check(e.getPlayer());
	});

	public static ItemOnObjectHandler handleBaitTraps = new ItemOnObjectHandler(new Object[] { "Boulder", "Deadfall", "Net trap", "Magic box", "Magic box failed", "Bird snare", "Box trap", "Shaking box", "Marasamaw plant", "Wilted marasamaw plant", "Shaking marasamaw plant" }, e -> {
		if (!(e.getObject() instanceof BoxStyleTrap)) {
			e.getPlayer().sendMessage("This trap isn't baitable.");
			return;
		}
		BoxStyleTrap trap = (BoxStyleTrap) e.getObject();
		if (!trap.ownedBy(e.getPlayer())) {
			e.getPlayer().sendMessage("This isn't your trap.");
			return;
		}
		trap.tryBait(e.getPlayer(), e.getItem().getId());
	});

	public static ObjectClickHandler handleDeadfalls = new ObjectClickHandler(new Object[] { "Boulder", "Deadfall" }, e -> {
		if (e.getOption().equals("Set-trap"))
			e.getPlayer().getActionManager().setAction(new BoxAction(BoxTrapType.DEAD_FALL, e.getObject()));
		else if (e.getOpNum() == ClientPacket.OBJECT_OP1) {
			if (!(e.getObject() instanceof BoxStyleTrap))
				return;
			BoxStyleTrap trap = (BoxStyleTrap) e.getObject();
			if (!trap.ownedBy(e.getPlayer())) {
				e.getPlayer().sendMessage("This isn't your trap.");
				return;
			}
			if (trap.getStatus() == Status.FAIL || trap.getStatus() == Status.IDLE)
				trap.dismantle(e.getPlayer());
			else if (trap.getStatus() == Status.SUCCESS)
				trap.check(e.getPlayer());
		}
	});

	public static ObjectClickHandler handleTreeNets = new ObjectClickHandler(new Object[] { "Young tree" }, e -> {
		if (e.getOption().equals("Set-trap"))
			e.getPlayer().getActionManager().setAction(new BoxAction(BoxTrapType.TREE_NET, e.getObject()));
		else if (e.getOption().equals("Dismantle")) {
			if (e.getObject() instanceof OwnedObject oo && !oo.ownedBy(e.getPlayer())) {
				e.getPlayer().sendMessage("This isn't your trap.");
				return;
			}
			for (OwnedObject o : OwnedObject.getOwnedBy(e.getPlayer()))
				if (o instanceof NetTrap nt && o.getTile().withinDistance(e.getObject().getTile(), 1))
					nt.dismantle(e.getPlayer());
		}
	});

	//	@ButtonClickHandler(ids = {  })
	//	public static void handleJadinko
}
