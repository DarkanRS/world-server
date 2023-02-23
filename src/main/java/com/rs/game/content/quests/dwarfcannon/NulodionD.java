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
package com.rs.game.content.quests.dwarfcannon;

import com.rs.game.content.DwarfMultiCannon;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class NulodionD extends Conversation {

	private static final int NULODION = 209;
	private static final int AMMO_MOULD = 4;
	private static final int NULODIONS_NOTES = 3;

	public static NPCClickHandler talkToNulodion = new NPCClickHandler(new Object[] { NULODION }, e -> {
		if (e.getOption().equals("Talk-to"))
			e.getPlayer().startConversation(new NulodionD(e.getPlayer()));
		else if (e.getOption().equals("Trade")) {
			if (!e.getPlayer().isQuestComplete(Quest.DWARF_CANNON)) {
				e.getPlayer().startConversation(new Conversation(new Dialogue().addNPC(NULODION, HeadE.CONFUSED, "Who are you?")));
				return;
			}
			ShopsHandler.openShop(e.getPlayer(), "nulodions_cannon_parts");
		} else if (e.getOption().equals("Replace-cannon"))
			if (DwarfMultiCannon.canFreelyReplace(e.getPlayer()))
				e.getPlayer().startConversation(new Conversation(new Dialogue().addNPC(NULODION, HeadE.HAPPY_TALKING, "Please try not to lose it next time..", () -> {
					for (int item : DwarfMultiCannon.CANNON_PIECES[e.getPlayer().getPlacedCannon()-1])
						e.getPlayer().getInventory().addItemDrop(item, 1);
					e.getPlayer().setPlacedCannon(0);
				})));
			else
				e.getPlayer().startConversation(new Conversation(new Dialogue().addNPC(NULODION, HeadE.CONFUSED, "I haven't found any cannons of yours.")));
	});

	public static ItemClickHandler handleNotes = new ItemClickHandler(new Object[] { NULODIONS_NOTES }, new String[] { "Read" }, e -> {
		e.getPlayer().sendMessage("Ammo for the Dwarf Multi Cannon must be made from steel bars. The bars must be heated in a furnace and used with the ammo mould.");
	});

	public NulodionD(Player player) {
		super(player);
		if (player.getQuestManager().getStage(Quest.DWARF_CANNON) == 9) {
			addPlayer(HeadE.NO_EXPRESSION, "Hello there.");
			addNPC(NULODION, HeadE.NO_EXPRESSION, "Can I help you?");
			addPlayer(HeadE.NO_EXPRESSION, "Captain Lawgof sent me. He's having trouble with his cannon.");
			addNPC(NULODION, HeadE.LAUGH, "Of course, we forgot to send the ammo mould!");
			addPlayer(HeadE.NO_EXPRESSION, "It fires a mould?");
			addNPC(NULODION, HeadE.NO_EXPRESSION, "Don't be silly - the ammo's made by using a mould. Here, take these to him. The instructions explain everything.");
			addSimple("The Cannon Engineer gives you some notes and a mould.", () -> {
				player.getInventory().addItemDrop(AMMO_MOULD, 1);
				player.getInventory().addItemDrop(NULODIONS_NOTES, 1);
			});
			addPlayer(HeadE.HAPPY_TALKING, "That's great, thanks.");
			addNPC(NULODION, HeadE.HAPPY_TALKING, "Thank you, adventurer. The Dwarf Black Guard will remember this.");
		} else if (player.isQuestComplete(Quest.DWARF_CANNON) && !player.getInventory().containsItem(AMMO_MOULD) && !player.getBank().containsItem(AMMO_MOULD, 1)) {
			addPlayer(HeadE.UPSET, "Hello again.");
			addPlayer(HeadE.UPSET, "I've lost the cannonball mould.");
			addNPC(NULODION, HeadE.SHAKING_HEAD, "Deary me, you are trouble. Here, take this one.");
			addItem(AMMO_MOULD, "The cannon engineer gives you another ammo mould.", () -> {
				player.getInventory().addItemDrop(AMMO_MOULD, 1);
			});
		} else {
			addPlayer(HeadE.NO_EXPRESSION, "Hello again.");
			addNPC(NULODION, HeadE.NO_EXPRESSION, "Hello.");
		}
		create();
	}

}
