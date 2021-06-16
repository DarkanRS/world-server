package com.rs.game.player.quests.inprog;

import java.util.ArrayList;

import com.rs.game.player.Player;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.QuestHandler;
import com.rs.game.player.quests.QuestOutline;
import com.rs.lib.Constants;
import com.rs.plugin.annotations.PluginEventHandler;

@QuestHandler(Quest.GRAND_TREE)
@PluginEventHandler
public class GrandTree extends QuestOutline {

	@Override
	public int getCompletedStage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<String>();
		switch(stage) {
		case 0:
			lines.add("I can start this quest by speaking with King Narnode");
			lines.add("inside the Grand Tree.");
			break;
		default:
			lines.add("Invalid quest stage. Report this to an administrator.");
			break;
		}
		return lines;
	}

	@Override
	public void complete(Player player) {
		player.getSkills().addXpQuest(Constants.ATTACK, 18400);
		player.getSkills().addXpQuest(Constants.AGILITY, 7900);
		player.getSkills().addXpQuest(Constants.MAGIC, 2150);
		getQuest().sendQuestCompleteInterface(player, 1601, "7,900 Agility XP", "18,400 Attack XP", "2,150 Magic XP");
	}
}
