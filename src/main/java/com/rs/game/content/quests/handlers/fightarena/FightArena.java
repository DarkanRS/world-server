package com.rs.game.content.quests.handlers.fightarena;

import com.rs.game.content.quests.Quest;
import com.rs.game.content.quests.QuestHandler;
import com.rs.game.content.quests.QuestOutline;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.plugin.annotations.PluginEventHandler;

import java.util.ArrayList;

@QuestHandler(Quest.FIGHT_ARENA)
@PluginEventHandler
public class FightArena extends QuestOutline {
	public final static int NOT_STARTED = 0;
	public final static int FREE_JEREMY = 1;
	public final static int ASK_ABOUT_PARTY = 2;
	public final static int HELP_CEDRIC = 3;
	public final static int RETURN_TO_OMAD = 4;
	public final static int QUEST_COMPLETE = 5;


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
				lines.add("organise the drinks for the child's birthday party. your mother");
				lines.add("");
			}
			case FREE_JEREMY -> {
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
		player.getInventory().addItem(995, 1000, true);
		player.getSkills().addXpQuest(Constants.ATTACK, 12_175);
		player.getSkills().addXpQuest(Constants.THIEVING, 2_175);
		getQuest().sendQuestCompleteInterface(player, 75, "12,175 Attack XP", "2,175 Thieving XP", "1,000 Coins");
	}

}
