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
package com.rs.game.content.world.areas.falador.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.AchievementTitles;
import com.rs.game.content.world.LoyaltyShop;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
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
		Player player = e.getPlayer();
		NPC npc = e.getNPC();
		switch (e.getOpNum()) {
			case 1 -> {
				if (player.getAuraManager().getJotSkills() >= 10) {
					player.startConversation(new Dialogue()
							.addNPC(npc, HeadE.CHEERFUL, "Good day, my friend! Good day!").voiceEffect(12253)
							.addNPC(npc, HeadE.CHEERFUL, "Wait! I have something here for you...").voiceEffect(11083)
							.addItem(20960, "Xuan hands you a reward book for completing the Jack of Trades.", () -> {
								player.getInventory().addItemDrop(20960, 1);
								player.getAuraManager().deactivate();
								player.getAuraManager().clearJotFlags();
								player.incrementCount("Jack of Trades completed");
							}));
					return;
				}
				player.startConversation(new Dialogue()
						.addNPC(npc, HeadE.CHEERFUL, "It is my privilege to offer you access to an exclusive stock of the finest and most exotic wares.").voiceEffect(13331)
						.addOptions("What would you like help with?", (ops) -> {
							ops.add("Check the Loyalty Point Shop", () -> LoyaltyShop.open(player));
							ops.add("Re-apply my account type title", player::applyAccountTitle);
							ops.add("See your available titles", () -> AchievementTitles.openInterface(player));
							ops.add("Clear my title")
									.addOptions("Really clear your title?", (ops2) -> {
										ops2.add("Yes", player::clearTitle);
										ops2.add("No");
									});
						}));
			}
			case 3 -> LoyaltyShop.open(player);
			case 4 -> {
				player.sendOptionDialogue("Really clear your title?", (ops) -> {
					ops.add("Yes", player::clearTitle);
					ops.add("No");
				});
			}
		}
	});

}
