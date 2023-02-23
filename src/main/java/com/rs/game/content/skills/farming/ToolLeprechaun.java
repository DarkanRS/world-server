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
package com.rs.game.content.skills.farming;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class ToolLeprechaun {

	//inventory inter 74 = jadinko related storage

	public static void openToolStorage(Player player) {
		for (StorableItem i : StorableItem.values())
			i.updateVars(player);
		player.getInterfaceManager().sendInterface(125);
		player.getInterfaceManager().sendInventoryInterface(126);
	}

	public static void storeTool(Player player, int itemId, int amount) {
		StorableItem item = StorableItem.forId(itemId);
		if (item == null)
			return;
		player.storeLeprechaunItem(item, itemId, amount);
	}

	public static void takeTool(Player player, int itemId, int amount) {
		StorableItem item = StorableItem.forId(itemId);
		if (item == null)
			return;
		player.takeLeprechaunItem(item, amount);
	}
	
	@ServerStartupEvent
	public static void addLoSOverrides() {
		Entity.addLOSOverrides("Tool leprechaun", "Tool Leprechaun");
	}

	public static NPCClickHandler handleToolLeprechaun = new NPCClickHandler(new Object[] { "Tool leprechaun", "Tool Leprechaun" }, e -> {
		switch(e.getOption()) {
		case "Exchange":
		case "Exchange-tools":
		case "Exchange-potions":
			openToolStorage(e.getPlayer());
			break;
		case "Talk-to":
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Ah, 'tis a foine day, to be sure! Can I help ye with tool storage, or a trip to Winkin's Farm, or what?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("Tool storage.", new Dialogue()
									.addNPC(e.getNPCId(), HeadE.CHEERFUL, "We'll hold onto yer rake, seed dibber, spade, secateurs, waterin' can and trowel - but mind it's not one of them fancy trowels only archaeologists use.")
									.addNPC(e.getNPCId(), HeadE.CHEERFUL, "We'll take a few buckets an' scarecrows off yer hands too, and even yer compost and supercompost. There's room in our shed for plenty of compost, so bring it on.")
									.addNPC(e.getNPCId(), HeadE.CHEERFUL, "Also, if ye hands us yer Farming produce, we might be able to change it into banknotes.")
									.addNPC(e.getNPCId(), HeadE.CONFUSED, "So, do ye want to be using the store?")
									.addOption("What would you like to say?", "Yes, please.", "Nevermind.")
									.addNext(() -> openToolStorage(e.getPlayer())));

							option("Winkin's farm.", new Dialogue().addNPC(e.getNPCId(), HeadE.UPSET, "I'm sorry mate, I've been instructed that I'm not allowed to do that yet!"));
						}
					});
				}
			});
			break;
		case "Teleport":
			e.getPlayer().sendMessage("Vinesweeper is not available yet.");
			break;
		}
	});

	public static ItemOnNPCHandler handleItemOnLeprechaun = new ItemOnNPCHandler(new Object[] { "Tool leprechaun", "Tool Leprechaun" }, e -> {
		if (ProduceType.isProduce(e.getItem().getId()) && e.getItem().getDefinitions().getCertId() != -1) {
			int num = e.getPlayer().getInventory().getNumberOf(e.getItem().getId());
			e.getPlayer().getInventory().deleteItem(e.getItem().getId(), num);
			e.getPlayer().getInventory().addItem(new Item(e.getItem().getDefinitions().getCertId(), num));
			return;
		}
		e.getPlayer().sendMessage("The leprechaun cannot note that item for you.");
	});

	public static ButtonClickHandler handleWithdrawInter = new ButtonClickHandler(125, e -> {
		switch(e.getPacket()) {
		case IF_OP1:
			takeTool(e.getPlayer(), e.getSlotId2(), 1);
			break;
		case IF_OP2:
			takeTool(e.getPlayer(), e.getSlotId2(), 5);
			break;
		case IF_OP3:
			takeTool(e.getPlayer(), e.getSlotId2(), Integer.MAX_VALUE);
			break;
		case IF_OP4:
			e.getPlayer().sendInputInteger("How many would you like to take?", num -> takeTool(e.getPlayer(), e.getSlotId2(), num));
			break;
		default:
			break;
		}
	});

	public static ButtonClickHandler handleInventoryInter = new ButtonClickHandler(126, e -> {
		switch(e.getPacket()) {
		case IF_OP1:
			storeTool(e.getPlayer(), e.getSlotId2(), 1);
			break;
		case IF_OP2:
			storeTool(e.getPlayer(), e.getSlotId2(), 5);
			break;
		case IF_OP3:
			storeTool(e.getPlayer(), e.getSlotId2(), Integer.MAX_VALUE);
			break;
		case IF_OP4:
			e.getPlayer().sendInputInteger("How many would you like to store?", num -> storeTool(e.getPlayer(), e.getSlotId2(), num));
			break;
		default:
			break;
		}
	});
}
