package com.rs.game.content.quests.holygrail.dialogue;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.content.quests.holygrail.HolyGrail.*;

@PluginEventHandler
public class MerlinHolyGrailD extends Conversation {
	private static final int NPC = 213;
	public MerlinHolyGrailD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.HOLY_GRAIL)) {
			case TALK_TO_MERLIN, GO_TO_ENTRANA, GO_TO_MCGRUBOR -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello. King Arthur has sent me on a quest for the Holy Grail. He thought you could offer some assistance.");
				addNPC(NPC, HeadE.CALM_TALK, "Ah yes... the Holy Grail... That is a powerful artefact indeed. Returning it here would help Camelot a lot." +
						" Due to its nature the Holy Grail is likely to reside in a holy place.");
				addPlayer(HeadE.HAPPY_TALKING, "Any suggestions?");
				addNPC(NPC, HeadE.CALM_TALK, "I believe there is a holy island somewhere not far away... I'm not entirely sure... I spent too long inside that" +
						" crystal! Anyways, go and talk to someone over there. I suppose you could also try speaking to Sir Galahad?");
				addNPC(NPC, HeadE.CALM_TALK, "He returned from the quest many years after everyone else. He seems to know something about it, but he can only " +
						"speak about those experiences cryptically.");
				addPlayer(HeadE.HAPPY_TALKING, "Where can I find Sir Galahad?");
				addNPC(NPC, HeadE.CALM_TALK, "Galahad now lives a life of religious contemplation. He lives somewhere west of McGrubor's Wood I think. Though " +
						"I recommend speaking to someone on the holy island first.",()->{
					player.getQuestManager().setStage(Quest.HOLY_GRAIL, GO_TO_ENTRANA);
				});
			}
			case SPEAK_TO_FISHER_KING -> {
				addNPC(NPC, HeadE.CALM_TALK, "How goes the quest for the Holy Grail?");
				addPlayer(HeadE.HAPPY_TALKING, "I am to find an artifact in Draynor Manor");
				addNPC(NPC, HeadE.CALM_TALK, "I see");
			}
			case SPEAK_TO_PERCIVAL -> {
				addNPC(NPC, HeadE.CALM_TALK, "How goes the quest for the Holy Grail?");
				addPlayer(HeadE.HAPPY_TALKING, "I am trying to find the son of the king of The Fisher Realm!");
				addNPC(NPC, HeadE.AMAZED, "That is a task indeed!");
			}
			case GIVE_AURTHUR_HOLY_GRAIL -> {
				addPlayer(HeadE.HAPPY_TALKING, "I found the Holy Grail!");
				addNPC(NPC, HeadE.AMAZED, "REALLY!!?");
				addPlayer(HeadE.HAPPY_TALKING, "Yes!");
				addNPC(NPC, HeadE.CALM_TALK, "You really need to give The Holy Grail to King Arthur!");
			}
			case QUEST_COMPLETE -> {
				addNPC(NPC, HeadE.CALM_TALK, "I am amazed with you adventurer thank you for getting the Holy Grail back to Camelot!");
			}
		}
	}
}
