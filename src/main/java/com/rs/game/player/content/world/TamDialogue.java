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

import com.rs.game.player.dialogues.Dialogue;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class TamDialogue  {
	
	static class TamD extends Dialogue {

		@Override
		public void start() {
			stage = 1;
			sendNPCDialogue(14414, SECRELTY_TALKING, "You interested in Runecrafting buddy?..", "You wanna get some of that wicked action?");
		}

		@Override
		public void run(int interfaceId, int componentId) {
			if (stage == 1) {
				stage = 2;
				sendOptionsDialogue(player, "What would you like to say?", "No thanks.. You seem a little sketchy to me \"buddy\"..", "Sure, give me one of those things.");
			} else if (stage == 2) {
				if (componentId == OPTION_1) {
					stage = -1;
					sendPlayerDialogue(CALM_TALK, "No thanks.. You seem a little sketchy to me \"buddy\"..");
				} else if (componentId == OPTION_2) {
					stage = 3;
					sendPlayerDialogue(HAPPY_TALKING, "Sure, give me one of those things.");
				}
			} else if (stage == 3) {
				stage = -1;
				sendNPCDialogue(14414, SECRELTY_TALKING, "Alright son. Go ahead and take it.", "It recharges its power each day.");
				player.getInventory().addItem(22332, 1);
			} else {
				end();
			}
		}

		@Override
		public void finish() {
			
		}
		
	}
	
	public static NPCClickHandler handleTalkTo = new NPCClickHandler(14414) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().getDialogueManager().execute(new TamD(), new Object[] { });
		}
	};
}
