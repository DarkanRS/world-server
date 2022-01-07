package com.rs.game.player.quests;

import static com.rs.game.player.quests.handlers.familycrest.FamilyCrest.GIVE_AVAN_JEWLERY;
import static com.rs.game.player.quests.handlers.familycrest.FamilyCrest.NOT_STARTED;
import static com.rs.game.player.quests.handlers.familycrest.FamilyCrest.QUEST_COMPLETE;
import static com.rs.game.player.quests.handlers.familycrest.FamilyCrest.TALK_TO_AVAN;
import static com.rs.game.player.quests.handlers.familycrest.FamilyCrest.TALK_TO_BOOT;
import static com.rs.game.player.quests.handlers.familycrest.FamilyCrest.TALK_TO_CALEB;
import static com.rs.game.player.quests.handlers.familycrest.FamilyCrest.TALK_TO_GEM_TRADER;
import static com.rs.game.player.quests.handlers.familycrest.FamilyCrest.TALK_TO_JOHNATHAN;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class DialogueTemplate extends Conversation {
	private final int FIRST = 0;
	private final int SECOND = 1;
	@SuppressWarnings("unused")
	private static final int NPC = -1;
	public DialogueTemplate(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.FAMILY_CREST)) {
		case NOT_STARTED -> {

		}
		case TALK_TO_CALEB -> {

		}
		case TALK_TO_GEM_TRADER -> {

		}
		case TALK_TO_AVAN -> {

		}
		case TALK_TO_BOOT -> {

		}
		case GIVE_AVAN_JEWLERY -> {

		}
		case TALK_TO_JOHNATHAN -> {

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
