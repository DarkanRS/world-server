package com.rs.game.content.quests.monksfriend.dialogues;

import static com.rs.game.content.quests.monksfriend.MonksFriend.GET_BLANKET;
import static com.rs.game.content.quests.monksfriend.MonksFriend.HELP_CEDRIC;
import static com.rs.game.content.quests.monksfriend.MonksFriend.NOT_STARTED;
import static com.rs.game.content.quests.monksfriend.MonksFriend.QUEST_COMPLETE;

import com.rs.game.engine.dialogue.Conversation;
import com.rs.game.engine.quest.Quest;
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
