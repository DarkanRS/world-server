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
package com.rs.game.content.quests.vampireslayer;

import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInteractionDistanceHandler;

import java.util.ArrayList;
import java.util.List;

@QuestHandler(Quest.VAMPYRE_SLAYER)
@PluginEventHandler
public class VampireSlayer extends QuestOutline {
	static final int NOT_STARTED = 0;
	static final int STARTED = 1;
	static final int HARLOW_NEED_DRINK = 2;
	static final int STAKE_RECIEVED = 3;
	static final int VAMPYRE_KILLED = 4;
	static final int QUEST_COMPLETE = 5;

	static final int STAKE = 1549;

	@Override
	public int getCompletedStage() {
		return 5;
	}

	@Override
	public List<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch(stage) {
		case NOT_STARTED:
			lines.add("The people of Draynor village live in constant terror. Their numbers");
			lines.add("are dwindling, all due to a foul creature known as a vampyre,");
			lines.add("lurking in the manor to the north.");
			lines.add("");
			lines.add("I can start this quest by speaking to Morgan in Draynor.");
			lines.add("");
			break;
		case STARTED:
			lines.add("Morgan says I can get lessons on defeating a vampyre from");
			lines.add("Dr. Harlow at the Blue Moon Inn in south Varrock.");
			lines.add("");
			break;
		case HARLOW_NEED_DRINK:
			lines.add("It seems I will have to give in and get Dr. Harlow a beer.");
			lines.add("I should get one from the bartender at the Blue Moon Inn");
			lines.add("");
			break;
		case STAKE_RECIEVED:
			lines.add("Dr. Harlow gave me a stake and told me to use it to kill the");
			lines.add("vampyre");
			lines.add("");
			lines.add("To kill the vampyre I need:");
			lines.add("A stake");
			lines.add("A hammer");
			lines.add("Garlic in my inventory (optional)");
			lines.add("");
			lines.add("I can find the vampyre in the Draynor Manor basement.");
			lines.add("");
			break;
		case VAMPYRE_KILLED:
			lines.add("I have killed Count Draynor! I should report back to");
			lines.add("Morgan.");
			lines.add("");
			break;
		case QUEST_COMPLETE:
			lines.add("");
			lines.add("");
			lines.add("QUEST COMPLETE!");
			break;
		default:
			lines.add("Invalid quest stage. Report this to an administrator.");
			break;
		}
		return lines;
	}

	@Override
	public void complete(Player player) {
		player.getSkills().addXpQuest(Skills.ATTACK, 4825);
		getQuest().sendQuestCompleteInterface(player, STAKE, "4825 Attack XP");
	}

	public static NPCInteractionDistanceHandler bartenderBlueMoonDistance = new NPCInteractionDistanceHandler(733, (player, npc) -> 5);
}
