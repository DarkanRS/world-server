package com.rs.game.content.quests.holygrail.dialogue.knightsroundtable;

import static com.rs.game.content.quests.holygrail.HolyGrail.GIVE_AURTHUR_HOLY_GRAIL;
import static com.rs.game.content.quests.holygrail.HolyGrail.GO_TO_ENTRANA;
import static com.rs.game.content.quests.holygrail.HolyGrail.GO_TO_MCGRUBOR;
import static com.rs.game.content.quests.holygrail.HolyGrail.QUEST_COMPLETE;
import static com.rs.game.content.quests.holygrail.HolyGrail.SPEAK_TO_FISHER_KING;
import static com.rs.game.content.quests.holygrail.HolyGrail.SPEAK_TO_PERCIVAL;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class SirBedivereHolyGrailD extends Conversation {
	private static final int NPC = 242;
	public SirBedivereHolyGrailD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.HOLY_GRAIL)) {
			case GO_TO_ENTRANA, GO_TO_MCGRUBOR, SPEAK_TO_FISHER_KING, SPEAK_TO_PERCIVAL, GIVE_AURTHUR_HOLY_GRAIL -> {
				addNPC(NPC, HeadE.CALM_TALK, "May I help you? You are looking for the Grail now adventurer?");
				addPlayer(HeadE.HAPPY_TALKING, "Absolutely.");
				addNPC(NPC, HeadE.CALM_TALK, "The best of luck to you! Make the name of Camelot proud, and bring it back to us.");
			}
			case QUEST_COMPLETE -> {
				addNPC(NPC, HeadE.CALM_TALK, "You look in good spirits.");
				addPlayer(HeadE.TALKING_ALOT, "Yup");
				addNPC(NPC, HeadE.CALM_TALK, "That Holy Grail...");
				addPlayer(HeadE.CALM, "...");
				addNPC(NPC, HeadE.CALM_TALK, "I am glad you got it! You ought to be in good spirits");
				addPlayer(HeadE.HAPPY_TALKING, "I appreciate that");
			}
		}
	}
}
