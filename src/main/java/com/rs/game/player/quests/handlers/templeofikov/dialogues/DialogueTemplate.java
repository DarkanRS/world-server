package com.rs.game.player.quests.handlers.templeofikov.dialogues;

import static com.rs.game.player.quests.handlers.templeofikov.TempleOfIkov.HELP_LUCIEN;
import static com.rs.game.player.quests.handlers.templeofikov.TempleOfIkov.NOT_STARTED;
import static com.rs.game.player.quests.handlers.templeofikov.TempleOfIkov.QUEST_COMPLETE;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.quests.Quest;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class DialogueTemplate extends Conversation {
	@SuppressWarnings("unused")
	private static final int NPC = -1;
	public DialogueTemplate(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.TEMPLE_OF_IKOV)) {
			case NOT_STARTED -> {

			}
			case HELP_LUCIEN -> {

			}
			case QUEST_COMPLETE ->  {

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
