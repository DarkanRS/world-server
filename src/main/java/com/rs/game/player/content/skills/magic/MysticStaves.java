package com.rs.game.player.content.skills.magic;

import com.rs.game.player.dialogues.Dialogue;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class MysticStaves  {

	public static int[] battlestaves = { 1397, 1395, 1399, 1393, 3053, 6562, 11736 };
	public static int[] mystics = { 1405, 1403, 1407, 1401, 3054, 6563, 11738 };
	
	static class ThormacD extends Dialogue {

		@Override
		public void start() {
			stage = 0;
			sendNPCDialogue(389, CALM_TALK, "Hello, is there something you needed?");
		}

		@Override
		public void run(int interfaceId, int componentId) {
			if (stage == 0) {
				stage = 1;
				sendOptionsDialogue(player, "What would you like to say?", "I'm looking for a quest.", "Could you enchant my battlestaves please?");
			} else if (stage == 1) {
				if (componentId == OPTION_1) {
					stage = 2;
					sendPlayerDialogue(CALM_TALK, "I'm looking for a quest.");
				} else {
					stage = 100;
					sendPlayerDialogue(CALM_TALK, "Could you cnchant my battlestaves please?");
				}
			} else if (stage == 2) {
				stage = 3;
				sendNPCDialogue(389, CALM_TALK, "I have no quests to offer at the moment. (Scorpion Catcher)");
			} else if (stage == 100) {
				stage = 101;
				sendNPCDialogue(389, CALM_TALK, "Of course. Which ones would you like enchanted?");
			} else if (stage == 101) {
				end();
				player.getInterfaceManager().sendInterface(332);
			} else {
				end();
			}
		}

		@Override
		public void finish() {
			
		}
		
	}
	
	public static ButtonClickHandler handleButtons = new ButtonClickHandler(332) {
		@Override
		public void handle(ButtonClickEvent e) {
			int staffIdx = e.getComponentId()-21;
			if (e.getPlayer().getInventory().containsItem(995, 40000) && e.getPlayer().getInventory().containsItem(battlestaves[staffIdx], 1)) {
				e.getPlayer().getInventory().deleteItem(995, 40000);
				e.getPlayer().getInventory().deleteItem(battlestaves[staffIdx], 1);
				e.getPlayer().getInventory().addItem(mystics[staffIdx], 1);
			} else {
				e.getPlayer().sendMessage("You need 40,000 coins and a battlestaff of the correct type to enchant.");
			}
		}
	};
	
	public static NPCClickHandler talkOp = new NPCClickHandler("Thormac") {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().getDialogueManager().execute(new ThormacD(), new Object[] {});
		}
	};

}
