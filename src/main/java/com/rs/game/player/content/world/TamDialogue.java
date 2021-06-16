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
