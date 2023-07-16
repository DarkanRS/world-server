package com.rs.game.content.quests.holygrail.dialogue.knightsroundtable;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.content.quests.holygrail.HolyGrail.*;

@PluginEventHandler
public class SirPelleasHolyGrailD extends Conversation {
	private static final int NPC = 244;
	public SirPelleasHolyGrailD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.HOLY_GRAIL)) {
			case GO_TO_ENTRANA, GO_TO_MCGRUBOR, SPEAK_TO_FISHER_KING, SPEAK_TO_PERCIVAL, GIVE_AURTHUR_HOLY_GRAIL -> {
				addNPC(NPC, HeadE.CALM_TALK, "Greetings to the court of King Arthur!");
				addPlayer(HeadE.HAPPY_TALKING, "Any suggestions on finding the Grail?");
				addNPC(NPC, HeadE.CALM_TALK, "My best guess would be some sort of spell. Merlin is our magic expert. Ask him? Although having said that, " +
						"I believe Galahad found its location once.");
				addPlayer(HeadE.HAPPY_TALKING, "Really? Know where I can find him?");
				addNPC(NPC, HeadE.CALM_TALK, "I'm afraid not. He left here many moons ago and I know not where he went.");
			}
			case QUEST_COMPLETE -> {
				addNPC(NPC, HeadE.CALM_TALK, "Greetings to the court of King Arthur!");
				addPlayer(HeadE.CALM_TALK, "Sure...");
				addNPC(NPC, HeadE.TALKING_ALOT, "Great job on the Holy Grail too!");
				addPlayer(HeadE.HAPPY_TALKING, "Thanks!");
			}
		}
	}
}
