package com.rs.game.player.content.dialogue.impl.skillmasters;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class WiseOldManD extends Conversation {
	
	public WiseOldManD(Player player) {
		super(player);
		
		addNPC(3820, HeadE.HAPPY_TALKING, "Greetings! What can I do for you?");
		if (player.getQuestManager().completedAllQuests()) {
			addPlayer(HeadE.CONFUSED, "I was wondering if you could sell me a quest cape! I have completed all the quests.");
			addNPC(3820, HeadE.LAUGH, "Impressive! I see you have! It will cost you 99,000 coins, though.");
			addOption(DEFAULT_OPTIONS_TITLE, "Yes, I have that with me now.", "Sorry, nevermind.");
			if (player.getInventory().containsItem(995, 99000)) {
				addPlayer(HeadE.HAPPY_TALKING, "Yeah I have that with me. Here you go.");
				addNPC(3820, HeadE.LAUGH, "Wear the cape with pride, adventurer.", () -> {
					if (player.getInventory().containsItem(995, 99000)) {
						player.getInventory().deleteItem(995, 99000);
						player.getInventory().addItemDrop(9814, 1);
						player.getInventory().addItemDrop(9813, 1);
					}
				});
			}
		} else {
			addPlayer(HeadE.CONFUSED, "I'm not sure. What can you do for me?");
			addNPC(3820, HeadE.HAPPY_TALKING, "I can offer you a quest cape once you reach maximum quest points.");
		}
		
		create();
	}
	
	public static NPCClickHandler handleTalk = new NPCClickHandler(3820) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new WiseOldManD(e.getPlayer()));
		}
	};

}
