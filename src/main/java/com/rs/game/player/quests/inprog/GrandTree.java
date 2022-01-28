// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
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
		ArrayList<String> lines = new ArrayList<>();
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
