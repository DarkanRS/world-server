package com.rs.game.content.quests.holygrail.dialogue.knightsroundtable;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.content.quests.holygrail.HolyGrail.*;

@PluginEventHandler
public class SirLucanHolyGrailD extends Conversation {
	private static final int NPC = 245;
	public SirLucanHolyGrailD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.HOLY_GRAIL)) {
			case GO_TO_ENTRANA, GO_TO_MCGRUBOR, SPEAK_TO_FISHER_KING, SPEAK_TO_PERCIVAL, GIVE_AURTHUR_HOLY_GRAIL -> {
				addNPC(NPC, HeadE.CALM_TALK, "Hello there adventurer.");
				addPlayer(HeadE.HAPPY_TALKING, "I seek the Grail of legend!");
				addNPC(NPC, HeadE.CALM_TALK, "I'm afraid I don't have any suggestions.");
				addPlayer(HeadE.HAPPY_TALKING, "Thanks. I'll try and find someone who does.");
			}
			case QUEST_COMPLETE -> {
				addNPC(NPC, HeadE.CALM_TALK, "Thank you for getting the Holy Grail!");
				addPlayer(HeadE.HAPPY_TALKING, "You are welcome.");
			}
		}
	}


//    public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC}) {
//        @Override
//        public void handle(NPCClickEvent e) {
//            e.getPlayer().startConversation(new SirLucanHolyGrailD(e.getPlayer()).getStart());
//        }
//    };
}
