package com.rs.game.content.quests.handlers.holygrail.dialogue.knightsroundtable;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.*;

@PluginEventHandler
public class SirTristamHolyGrailD extends Conversation {
	private static final int NPC = 243;
	public SirTristamHolyGrailD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.HOLY_GRAIL)) {
			case GO_TO_ENTRANA, GO_TO_MCGRUBOR, SPEAK_TO_FISHER_KING, SPEAK_TO_PERCIVAL, GIVE_AURTHUR_HOLY_GRAIL -> {
				addNPC(NPC, HeadE.CALM_TALK, "Hail Arthur, King of the Britons!");
				addPlayer(HeadE.HAPPY_TALKING, "Um... Hello. I am seeking the Grail.");
				addNPC(NPC, HeadE.CALM_TALK, "Good luck with that!");
			}
			case QUEST_COMPLETE -> {
				addNPC(NPC, HeadE.CALM_TALK, "Hail Arthur, King of the Britons!");
				addNPC(NPC, HeadE.CALM_TALK, "Oh and good job getting the Holy Grail!");
				addPlayer(HeadE.HAPPY_TALKING, "Thanks.");
			}
		}
	}
}
