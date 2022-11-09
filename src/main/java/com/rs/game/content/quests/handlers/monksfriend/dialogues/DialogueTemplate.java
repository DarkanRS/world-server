package com.rs.game.content.quests.handlers.monksfriend.dialogues;

import static com.rs.game.content.quests.handlers.monksfriend.MonksFriend.GET_BLANKET;
import static com.rs.game.content.quests.handlers.monksfriend.MonksFriend.HELP_CEDRIC;
import static com.rs.game.content.quests.handlers.monksfriend.MonksFriend.NOT_STARTED;
import static com.rs.game.content.quests.handlers.monksfriend.MonksFriend.QUEST_COMPLETE;

import java.lang.SuppressWarnings;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class DialogueTemplate extends Conversation {
	@SuppressWarnings("unused")
	private static final int NPC = -1;

	public DialogueTemplate(Player p) {
		super(p);
		switch (p.getQuestManager().getStage(Quest.MONKS_FRIEND)) {
			case NOT_STARTED -> {

			}
			case GET_BLANKET -> {

			}
			case HELP_CEDRIC -> {

			}
			case QUEST_COMPLETE -> {

			}
		}
	}
}
