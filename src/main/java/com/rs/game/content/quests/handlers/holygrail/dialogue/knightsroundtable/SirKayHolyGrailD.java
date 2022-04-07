package com.rs.game.content.quests.handlers.holygrail.dialogue.knightsroundtable;

import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.GIVE_AURTHUR_HOLY_GRAIL;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.GO_TO_ENTRANA;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.GO_TO_MCGRUBOR;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.QUEST_COMPLETE;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.SPEAK_TO_FISHER_KING;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.SPEAK_TO_PERCIVAL;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class SirKayHolyGrailD extends Conversation {
	private static final int NPC = 241;
	public SirKayHolyGrailD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.HOLY_GRAIL)) {
			case GO_TO_ENTRANA, GO_TO_MCGRUBOR, SPEAK_TO_FISHER_KING, SPEAK_TO_PERCIVAL, GIVE_AURTHUR_HOLY_GRAIL -> {
				addNPC(NPC, HeadE.CALM_TALK, "Good day " + p.getPronoun("sir", "madam") + "! I hear you are questing for the Holy Grail?");
				addPlayer(HeadE.HAPPY_TALKING, "That's right. Any hints?");
				addNPC(NPC, HeadE.CALM_TALK, "Unfortunately not.");
			}
			case QUEST_COMPLETE -> {
				addNPC(NPC, HeadE.CALM_TALK, "You got the Holy Grail!");
				addPlayer(HeadE.HAPPY_TALKING, "Yes, I did...");
			}
		}
	}
}
