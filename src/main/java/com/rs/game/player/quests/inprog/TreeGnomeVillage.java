package com.rs.game.player.quests.inprog;

import java.util.ArrayList;

import com.rs.game.player.Player;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.QuestHandler;
import com.rs.game.player.quests.QuestOutline;
import com.rs.lib.Constants;
import com.rs.plugin.annotations.PluginEventHandler;

@QuestHandler(Quest.TREE_GNOME_VILLAGE)
@PluginEventHandler
public class TreeGnomeVillage extends QuestOutline {

	@Override
	public int getCompletedStage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<String>();
		switch (stage) {
		case 0:
			lines.add("I can start this quest by speaking with King Bolren");
			lines.add("in the center of the Tree Gnome Village.");
			break;
		default:
			lines.add("Invalid quest stage. Report this to an administrator.");
			break;
		}
		return lines;
	}

	@Override
	public void complete(Player player) {
		player.getSkills().addXpQuest(Constants.ATTACK, 11450);
		player.getInventory().addItem(589, 1, true);
		getQuest().sendQuestCompleteInterface(player, 1601, "11,450 Attack XP", "Gnome Amulet of Protection");
	}
}
