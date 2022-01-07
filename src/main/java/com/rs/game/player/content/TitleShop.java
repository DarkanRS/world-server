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
package com.rs.game.player.content;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.world.LoyaltyShop;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class TitleShop {

	public static NPCClickHandler onNPCClick = new NPCClickHandler(13727) {
		@Override
		public void handle(NPCClickEvent e) {
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
				e.getPlayer().sendOptionDialogue("What would you like help with?", new String[] { "Check the Loyalty Point Shop", "Re-apply my account type title", "See your available titles", "Clear my title" }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 1)
							LoyaltyShop.open(e.getPlayer());
						else if (option == 2)
							player.applyAccountTitle();
						else if (option == 3)
							AchievementTitles.openInterface(player);
						else
							player.sendOptionDialogue("Really clear your title?", new String[] { "Yes", "No" }, new DialogueOptionEvent() {
								@Override
								public void run(Player player) {
									if (getOption() == 1)
										player.clearTitle();
								}

							});
					}
				});
				break;
			case 3:
				LoyaltyShop.open(e.getPlayer());
				break;
			case 4:
				e.getPlayer().sendOptionDialogue("Really clear your title?", new String[] { "Yes", "No" }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (getOption() == 1) {
							player.setTitle(null);
							player.setTitleColor(null);
							player.setTitleShading(null);
							player.getAppearance().setTitle(0);
							player.getAppearance().generateAppearanceData();
						}
					}

				});
				break;
			}
		}
	};
}
