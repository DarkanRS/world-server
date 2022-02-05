package com.rs.game.player.quests.handlers.monksfriend.dialogues;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.quests.Quest;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.player.quests.handlers.monksfriend.MonksFriend.*;

@PluginEventHandler
public class DialogueTemplate extends Conversation {
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
