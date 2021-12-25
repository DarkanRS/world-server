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
package com.rs.game.player.quests.handlers;

import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.quests.Quest;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class FamilyCrest {
	/*
	public static NPCClickHandler handleFamilyGauntlets = new NPCClickHandler(663, 666, 668) {
		@Override
		public void handle(NPCClickEvent e) {
			if (!Quest.FAMILY_CREST.meetsRequirements(e.getPlayer(), "to claim a pair of family gauntlets."))
				return;
			switch(e.getNPCId()) {
			case 663:
				if (!e.getPlayer().containsItem(776))
					e.getPlayer().startConversation(new Conversation(e.getPlayer())
							.addNPC(e.getNPCId(), HeadE.CHEERFUL, "I see you lost your gauntlets. Here, don't lose them again next time!")
							.addItem(776, "Avan hands you a pair of gloves.", () -> {
								e.getPlayer().getInventory().addItem(776, 1);
							}));
				else
					e.getPlayer().startConversation(new Conversation(e.getPlayer())
							.addNPC(e.getNPCId(), HeadE.CHEERFUL, "I hope those gauntlets are faring well for you!"));
				break;
			case 666:
				if (!e.getPlayer().containsItem(775))
					e.getPlayer().startConversation(new Conversation(e.getPlayer())
							.addNPC(e.getNPCId(), HeadE.CHEERFUL, "I see you lost your gauntlets. Here, don't lose them again next time!")
							.addItem(775, "Caleb hands you a pair of gloves.", () -> {
								e.getPlayer().getInventory().addItem(775, 1);
							}));
				else
					e.getPlayer().startConversation(new Conversation(e.getPlayer())
							.addNPC(e.getNPCId(), HeadE.CHEERFUL, "I hope those gauntlets are faring well for you!"));
				break;
			case 668:
				if (!e.getPlayer().containsItem(777))
					e.getPlayer().startConversation(new Conversation(e.getPlayer())
							.addNPC(e.getNPCId(), HeadE.CHEERFUL, "I see you lost your gauntlets. Here, don't lose them again next time!")
							.addItem(777, "Johnathon hands you a pair of gloves.", () -> {
								e.getPlayer().getInventory().addItem(777, 1);
							}));
				else
					e.getPlayer().startConversation(new Conversation(e.getPlayer())
							.addNPC(e.getNPCId(), HeadE.CHEERFUL, "I hope those gauntlets are faring well for you!"));
				break;
			}
		}
	};

	 */
}
