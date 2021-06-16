package com.rs.game.player.quests.handlers.waterfallquest;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.quests.Quest;

public class HudonD extends Conversation {
	
	public HudonD(Player player, int hudonId) {
		super(player);
		
		if (player.getQuestManager().getStage(Quest.WATERFALL_QUEST) == 1) {
			addPlayer(HeadE.TALKING_ALOT, "Hey Hudon! Your mother is worried about you! Are you doing alright?");
			addNPC(hudonId, HeadE.CHILD_UNSURE, "Who are you and what do you want? You must want my treasure!");
			addPlayer(HeadE.TALKING_ALOT, "Your mother wants you to come home!");
			addNPC(hudonId, HeadE.CHILD_ANGRY, "No! There is too much treasure to be found!");
			addPlayer(HeadE.CONFUSED, "What treasure?");
			addNPC(hudonId, HeadE.CHILD_HAPPY_TALK, "The treasure inside the waterfall!");
			addPlayer(HeadE.TALKING_ALOT, "Can I look for treasure with you?");
			addNPC(hudonId, HeadE.CHILD_ANGRY, "No! You will steal it all!");
			addPlayer(HeadE.TALKING_ALOT, "Very well then..", () -> {
				player.getQuestManager().setStage(Quest.WATERFALL_QUEST, 2);
			});
		} else if (player.getQuestManager().getStage(Quest.WATERFALL_QUEST) > 1) {
			addNPC(hudonId, HeadE.CHILD_ANGRY, "I already told you! I am not letting you come with me to find the treasure!");
		} else {
			addNPC(hudonId, HeadE.CHILD_ANGRY, "Leave me alone! I have treasure to find!");
		}
		
		create();
	}
}
