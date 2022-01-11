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
package com.rs.game.player.content.world;

import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.controllers.QueenBlackDragonController;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class RoyalCrossbow {

	public static ItemClickHandler handleRCB = new ItemClickHandler(new Object[] { 24337, 24338, 24339 }, new String[] { "Split", "Brandish" }) {
		@Override
		public void handle(ItemClickEvent e) {
			switch(e.getOption()) {
			case "Split":
				if (e.getPlayer().getInventory().getFreeSlots() < 4) {
					e.getPlayer().sendMessage("You don't have enough inventory space.");
					return;
				}
				e.getPlayer().sendOptionDialogue("Would you like to undo Thurgo's hard work and retrieve the parts back?", new String[] { "Yes, please.", "No, thanks." }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 1) {
							player.getInventory().deleteItem(e.getItem().getId(), 1);
							player.getInventory().addItemDrop(24303, 1);
							player.getInventory().addItemDrop(24340, 1);
							player.getInventory().addItemDrop(24342, 1);
							player.getInventory().addItemDrop(24344, 1);
							player.getInventory().addItemDrop(24346, 1);
							player.sendMessage("You disassemble the crossbow.");
						}
					}
				});
				break;
			case "Brandish":
				if (!e.getPlayer().getControllerManager().isIn(QueenBlackDragonController.class)) {
					e.getPlayer().sendMessage("There isn't even close to enough heat here. Perhaps the Queen Black Dragon's breath would be hot enough.");
					return;
				}
				if (!e.getPlayer().getTempAttribs().getB("canBrandish")) {
					e.getPlayer().sendMessage("It doesn't seem like a good time to be doing that!");
					return;
				}
				e.getPlayer().setNextAnimation(new Animation(16870));
				e.getPlayer().lock(5);
				WorldTasks.delay(4, () -> e.getPlayer().resetReceivedHits());
				e.getPlayer().sendMessage("You brandish the crossbow and it absorbs the dragon's extremely hot fire.");
				e.getItem().setId(24338);
				e.getItem().deleteMetaData();
				e.getPlayer().getEquipment().refresh(Equipment.WEAPON);
				e.getPlayer().getInventory().refresh();
				break;
			}
		}
	};

	public static NPCClickHandler talkToCerebrum = new NPCClickHandler(15460) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.DRUNK, "Half, adventurer! Go no further! Mortal, wormy peril lies within this cave!");
					addOptions(new Options() {
						@Override
						public void create() {
							option("Wormy?");
							option("Who are you again?");
							option("Don't worry, I eat peril for breakfast.");
							if (!e.getPlayer().containsItems(24303, 24337, 24338, 24339))
								option("Would you happen to have found a Coral Crossbow?", new Dialogue()
										.addNPC(e.getNPCId(), HeadE.DRUNK, "Why, yes I have! The Raptor passed by earlier. He said you might need it.")
										.addItemToInv(e.getPlayer(), new Item(24303, 1), "You recieve the Coral Crossbow."));
						}
					});
				}
			});
		}
	};

}
