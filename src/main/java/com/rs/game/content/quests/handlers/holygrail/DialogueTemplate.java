package com.rs.game.content.quests.handlers.holygrail;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.*;

@PluginEventHandler
public class DialogueTemplate extends Conversation {
	@SuppressWarnings("unused")
	private static final int NPC = -1;
	public DialogueTemplate(Player p) {
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
