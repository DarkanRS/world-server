package com.rs.game.player.quests.handlers.heroesquest.dialogues;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.quests.Quest;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.player.quests.handlers.heroesquest.HeroesQuest.*;

@PluginEventHandler
public class DialogueTemplate extends Conversation {
	private static final int NPC = -1;

	public DialogueTemplate(Player p) {
		super(p);
		switch (p.getQuestManager().getStage(Quest.HEROES_QUEST)) {
			case NOT_STARTED -> {

			}
			case GET_ITEMS -> {

			}
			case QUEST_COMPLETE -> {

			}
		}
	}
}
