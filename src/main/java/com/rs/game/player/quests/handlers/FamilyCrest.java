package com.rs.game.player.quests.handlers;

import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.quests.Quest;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class FamilyCrest {
	
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
}
