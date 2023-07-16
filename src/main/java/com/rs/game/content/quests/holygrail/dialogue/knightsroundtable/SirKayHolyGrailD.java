package com.rs.game.content.quests.holygrail.dialogue.knightsroundtable;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.content.quests.holygrail.HolyGrail.*;

@PluginEventHandler
public class SirKayHolyGrailD extends Conversation {
	private static final int NPC = 241;
	public SirKayHolyGrailD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.HOLY_GRAIL)) {
			case GO_TO_ENTRANA, GO_TO_MCGRUBOR, SPEAK_TO_FISHER_KING, SPEAK_TO_PERCIVAL, GIVE_AURTHUR_HOLY_GRAIL -> {
				addNPC(NPC, HeadE.CALM_TALK, "Good day " + player.getPronoun("sir", "madam") + "! I hear you are questing for the Holy Grail?");
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
