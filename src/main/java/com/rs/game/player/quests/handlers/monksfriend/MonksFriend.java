package com.rs.game.player.quests.handlers.monksfriend;

import com.rs.game.player.Player;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.QuestHandler;
import com.rs.game.player.quests.QuestOutline;
import com.rs.lib.Constants;
import com.rs.plugin.annotations.PluginEventHandler;

import java.util.ArrayList;

@QuestHandler(Quest.MONKS_FRIEND)
@PluginEventHandler
public class MonksFriend extends QuestOutline {
	public final static int NOT_STARTED = 0;
	public final static int GET_BLANKET = 1;
	public final static int HELP_CEDRIC = 2;
	public final static int QUEST_COMPLETE = 3;


	@Override
	public int getCompletedStage() {
		return QUEST_COMPLETE;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch (stage) {
			case NOT_STARTED -> {
				lines.add("A child has had their blanket stolen! Find the thieves'");
				lines.add("den and return the blanket, then help Brother Omad ");
				lines.add("organise the drinks for the child's birthday party.");
				lines.add("");
				lines.add("I can start this quest by speaking to Brother Omad");
				lines.add("in the Monastery south of Ardougne");
				lines.add("");
			}
			case GET_BLANKET -> {
				lines.add("");
				lines.add("");
			}
			case HELP_CEDRIC -> {
				lines.add("");
				lines.add("");
			}
			case QUEST_COMPLETE -> {
				lines.add("");
				lines.add("");
				lines.add("QUEST COMPLETE!");
			}
			default -> {
				lines.add("Invalid quest stage. Report this to an administrator.");
			}
		}
		return lines;
	}

	@Override
	public void complete(Player player) {
		player.getInventory().addItem(563, 8, true);
		player.getSkills().addXpQuest(Constants.WOODCUTTING, 2000);
		getQuest().sendQuestCompleteInterface(player, 563, "8 Law Runes", "2,000 Woodcutting XP");
	}

}
