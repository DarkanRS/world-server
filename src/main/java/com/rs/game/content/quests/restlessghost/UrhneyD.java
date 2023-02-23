package com.rs.game.content.quests.restlessghost;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;

public class UrhneyD extends Conversation {
	private int NPC = 458;
	public UrhneyD(Player player) {
		super(player);
		if (player.getQuestManager().getStage(Quest.RESTLESS_GHOST) == 0) {
			addNPC(NPC, HeadE.FRUSTRATED, "Get out of my house!");
			return;
		}
		if (player.getQuestManager().getStage(Quest.RESTLESS_GHOST) == 1) {
			addNPC(NPC, HeadE.FRUSTRATED, "Get out of my house!");
			if (!player.getInventory().containsItem(552, 1)) {//needs amulet
				addPlayer(HeadE.HAPPY_TALKING, "Father Aereck told me to come talk to you about a ghost haunting his graveyard.");
				addNPC(NPC, HeadE.FRUSTRATED, "Oh the silly old fool. Here, take this amulet and see if you can communicate with the spectre", () ->{
					player.getInventory().addItem(552, 1);
					player.getQuestManager().setStage(Quest.RESTLESS_GHOST, 2);
				});
				addPlayer(HeadE.HAPPY_TALKING, "Thank you. I'll try.");
				return;
			}
			addNext(()->{
				player.getQuestManager().setStage(Quest.RESTLESS_GHOST, 2);
			});
			return;
		}
		addNPC(NPC, HeadE.CALM_TALK, "What do you need now?");
		if (!player.getInventory().containsItem(552, 1)) { //needs amulet
			addPlayer(HeadE.HAPPY_TALKING, "I've lost my amulet of ghostspeak.");
			addNPC(NPC, HeadE.CALM_TALK, "Have another one then. But be more careful next time!", ()->{
				player.getInventory().addItem(552, 1);
			});
			addPlayer(HeadE.HAPPY_TALKING, "Thank you. I'll try.");
		}
		create();
	}

}