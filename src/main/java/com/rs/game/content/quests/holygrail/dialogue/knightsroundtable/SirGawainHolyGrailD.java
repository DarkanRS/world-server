package com.rs.game.content.quests.holygrail.dialogue.knightsroundtable;

import static com.rs.game.content.quests.holygrail.HolyGrail.GIVE_AURTHUR_HOLY_GRAIL;
import static com.rs.game.content.quests.holygrail.HolyGrail.GO_TO_ENTRANA;
import static com.rs.game.content.quests.holygrail.HolyGrail.GO_TO_MCGRUBOR;
import static com.rs.game.content.quests.holygrail.HolyGrail.NOT_STARTED;
import static com.rs.game.content.quests.holygrail.HolyGrail.QUEST_COMPLETE;
import static com.rs.game.content.quests.holygrail.HolyGrail.SPEAK_TO_FISHER_KING;
import static com.rs.game.content.quests.holygrail.HolyGrail.SPEAK_TO_PERCIVAL;
import static com.rs.game.content.quests.holygrail.HolyGrail.TALK_TO_MERLIN;

import com.rs.game.engine.dialogue.Conversation;
import com.rs.game.engine.dialogue.HeadE;
import com.rs.game.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class SirGawainHolyGrailD extends Conversation {
	private static final int NPC = 240;
	public SirGawainHolyGrailD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.HOLY_GRAIL)) {
			case NOT_STARTED, TALK_TO_MERLIN -> {
				addNPC(NPC, HeadE.CALM_TALK, "Good day to you " + p.getPronoun("sir", "madam") + "!");
				addPlayer(HeadE.HAPPY_TALKING, "Good day!");
			}
			case GO_TO_ENTRANA, GO_TO_MCGRUBOR, SPEAK_TO_FISHER_KING, SPEAK_TO_PERCIVAL, GIVE_AURTHUR_HOLY_GRAIL -> {
				addNPC(NPC, HeadE.CALM_TALK, "Good day to you " + p.getPronoun("sir", "madam") + "!");
				addPlayer(HeadE.HAPPY_TALKING, "I seek the Grail in the name of Camelot!");
				addNPC(NPC, HeadE.CALM_TALK, "The Grail? That is truly a noble quest indeed. None but Galahad have come close.");
				addPlayer(HeadE.HAPPY_TALKING, "Galahad? Who is he?");
				addNPC(NPC, HeadE.CALM_TALK, "He used to be one of the Knights of the Round Table, but he mysteriously disappeared many years ago.");
				addPlayer(HeadE.HAPPY_TALKING, "Why would he quit being a Knight?");
				addNPC(NPC, HeadE.CALM_TALK, "That is a good question. I'm afraid I don't have the answer.");
			}
			case QUEST_COMPLETE -> {
				addNPC(NPC, HeadE.CALM_TALK, "Getting the Holy Grail was a major victory for us, thank you.");
				addPlayer(HeadE.HAPPY_TALKING, "You are welcome.");
			}
		}
	}
}
