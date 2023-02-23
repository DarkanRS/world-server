package com.rs.game.content.quests.restlessghost;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;

public class RGhostD extends Conversation {
	private int NPC = 457;
	public RGhostD(Player player) {
		super(player);
		if(player.getEquipment().getAmuletId() == 552) {
			addNPC(NPC, HeadE.CALM_TALK, "Hello mortal.");
			if(player.getInventory().containsItem(553, 1)) {
				addPlayer(HeadE.HAPPY_TALKING, "I found this skull outside.");
				addNPC(NPC, HeadE.CALM_TALK, "That's it! That's my head! Thank you adventurer.<br>Finally I can be at peace.");
				addPlayer(HeadE.HAPPY_TALKING, "You're very welcome. Farewell.");
				player.getInventory().deleteItem(553, 1);
				player.getQuestManager().completeQuest(Quest.RESTLESS_GHOST);
				return;
			}
			addPlayer(HeadE.HAPPY_TALKING, "Hello.");
			addNPC(NPC, HeadE.CALM_TALK, "I seem to have lost my skull. Could you go<br>find it for me please? I want to be released.<br>Last I saw it was a bit south of here by the mining site.");
			addPlayer(HeadE.HAPPY_TALKING, "I think I can handle that that.");
			return;
		}
		addNPC(NPC, HeadE.CALM_TALK, "Woooo woooo wooo woo!");
		create();
	}
}
