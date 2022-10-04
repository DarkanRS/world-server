package com.rs.game.content.quests.handlers.holygrail.dialogue;

import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.GIVE_AURTHUR_HOLY_GRAIL;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.GO_TO_ENTRANA;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.GO_TO_MCGRUBOR;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.NOT_STARTED;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.QUEST_COMPLETE;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.SPEAK_TO_FISHER_KING;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.SPEAK_TO_PERCIVAL;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

import java.lang.SuppressWarnings;

@PluginEventHandler
public class GrailMaidenHolyGrailD extends Conversation {
	@SuppressWarnings("unused")
	private static final int NPC = 210;
	public GrailMaidenHolyGrailD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.HOLY_GRAIL)) {
			case NOT_STARTED -> {

			}
			case GO_TO_ENTRANA -> {


			}
			case GO_TO_MCGRUBOR -> {


			}
			case SPEAK_TO_FISHER_KING -> {


			}
			case SPEAK_TO_PERCIVAL -> {


			}
			case GIVE_AURTHUR_HOLY_GRAIL -> {


			}
			case QUEST_COMPLETE -> {

			}
		}
	}


//    public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC}) {
//        @Override
//        public void handle(NPCClickEvent e) {
//            e.getPlayer().startConversation(new DialogueTemplate(e.getPlayer()).getStart());
//        }
//    };
}
