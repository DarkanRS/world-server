package com.rs.game.player.quests.handlers.heroesquest.dialogues;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.quests.Quest;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.player.quests.handlers.heroesquest.HeroesQuest.*;

@PluginEventHandler
public class DialogueTemplate extends Conversation {
	private final int FIRST = 0;
	private final int SECOND = 1;
	@SuppressWarnings("unused")
	private static final int NPC = -1;
	public DialogueTemplate(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.HEROES_QUEST)) {
		case NOT_STARTED -> {

		}
        case GET_ITEMS -> {

		}
		case QUEST_COMPLETE ->  {

		}
		}
	}

	public DialogueTemplate(Player p, int id) {
		super(p);
		switch(id) {
		case FIRST -> {

		}
		case SECOND -> {

		}

		}
	}
	/*
    public static NPCClickHandler handleDialogue = new NPCClickHandler(NPC) {
        @Override
        public void handle(NPCClickEvent e) {
            e.getPlayer().startConversation(new DialogueTemplate(e.getPlayer()).getStart());
        }
    };*/
}
