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
package com.rs.game.content;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.world.LoyaltyShop;
import com.rs.game.model.entity.Entity;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Xuan {
	
	@ServerStartupEvent
	public static void addLoSOverride() {
		Entity.addLOSOverride(13727);
	}

	public static NPCClickHandler onNPCClick = new NPCClickHandler(new Object[] { 13727 }, e -> {
		switch (e.getOpNum()) {
		case 1:
			if (e.getPlayer().getAuraManager().getJotSkills() >= 10) {
				e.getPlayer().startConversation(new Conversation(e.getPlayer()).addNPC(13727, HeadE.CHEERFUL, "Before we go any further, I have a reward for you.").addItem(20960, "Xuan hands you a reward book for completing the Jack of Trades.", () -> {
					e.getPlayer().getInventory().addItemDrop(20960, 1);
					e.getPlayer().getAuraManager().deactivate();
					e.getPlayer().getAuraManager().clearJotFlags();
					e.getPlayer().incrementCount("Jack of Trades completed");
				}));
				return;
			}
			e.getPlayer().sendOptionDialogue("What would you like help with?", ops -> {
				ops.add("Check the Loyalty Point Shop", () -> LoyaltyShop.open(e.getPlayer()));
				ops.add("Re-apply my account type title", () -> e.getPlayer().applyAccountTitle());
				ops.add("See your available titles", () -> AchievementTitles.openInterface(e.getPlayer()));
				ops.add("Clear my title", new Dialogue().addOptions("Really clear your title?", ops2 -> {
					ops2.add("Yes", () -> e.getPlayer().clearTitle());
					ops2.add("No");
				}));
			});
			break;
		case 3:
			LoyaltyShop.open(e.getPlayer());
			break;
		case 4:
			e.getPlayer().sendOptionDialogue("Really clear your title?", ops -> {
				ops.add("Yes", () -> e.getPlayer().clearTitle());
				ops.add("No");
			});
			break;
		}
	});
}
