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
package com.rs.game.content.quests.scorpioncatcher;

import java.util.ArrayList;

import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

/**
 * How this is written: Each seer prophecy/premonition allows you to spawn a
 * scorp in a chunk If you lose the cage you keep the seer prophecy. You can
 * talk to any Seer in Seer's village to get a prophecy.
 */
@QuestHandler(Quest.SCORPION_CATCHER)
@PluginEventHandler
public class ScorpionCatcher extends QuestOutline {
	public final static int NOT_STARTED = 0;
	public final static int LOOK_FOR_SCORPIONS = 1;
	public final static int QUEST_COMPLETE = 2;

	public static final int SCORP_1 = 385;
	public static final int SCORP_2 = 386;
	public static final int SCORP_3 = 387;
	
	public final static int EMPTY_CAGE = 456;
	public final static int CAUGHT_CAGE_1 = 457;
	public final static int CAUGHT_CAGE_2 = 458;
	public final static int CAUGHT_CAGE_3 = 459;

	@Override
	public int getCompletedStage() {
		return QUEST_COMPLETE;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch (stage) {
		case NOT_STARTED -> {
			lines.add("Thormac The Sorcerer has a hobby involving scorpions.");
			lines.add("Unfortunately, three of them have escaped and managed to run");
			lines.add("far away. If you manage to find them all, he may just be able");
			lines.add("to perform an important service for you.");
			lines.add("");
			lines.add("I can start this quest by speaking to Thormac The Sorcerer at the");
			lines.add("top of his tower southwest of Seer's Village");
			lines.add("");
			lines.add("~~~Requirements~~~");
			lines.add("31 prayer");
			lines.add("");
		}
		case LOOK_FOR_SCORPIONS -> {
			if (player.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getB("scorp1LocKnown")) {
				lines.add((caughtScorp(player, SCORP_1) ? "<str>" : "") + "The 1st scorpion is in Taverly Dungeon in a room north of");
				lines.add((caughtScorp(player, SCORP_1) ? "<str>" : "") + "the black demons to the west.");
				lines.add("");
			} else {
				lines.add("I still need to ask the Seer where the 1st scorpion is.");
			}
			if (player.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getB("scorp2LocKnown")) {
				lines.add((caughtScorp(player, SCORP_2) ? "<str>" : "") + "The 2nd scorpion's location is somewhere inside the");
				lines.add((caughtScorp(player, SCORP_2) ? "<str>" : "") + "barbarian agility course.");
				lines.add("");
			} else {
				lines.add("I still need to ask the Seer where the 2nd scorpion is.");
			}
			if (player.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getB("scorp3LocKnown")) {
				lines.add((caughtScorp(player, SCORP_3) ? "<str>" : "") + "The 3rd scorpion's location is somewhere inside the");
				lines.add((caughtScorp(player, SCORP_3) ? "<str>" : "") + "Edgeville monastery.");
				lines.add("");
			} else {
				lines.add("I still need to ask the Seer where the 3rd scorpion is.");
			}
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

	public static NPCClickHandler handleScorpCatches = new NPCClickHandler(new Object[] { SCORP_1, SCORP_2, SCORP_3 }, e -> {
		if (e.getPlayer().getQuestManager().getStage(Quest.SCORPION_CATCHER) != LOOK_FOR_SCORPIONS)
			return;
		if (!e.getPlayer().getInventory().containsOneItem(EMPTY_CAGE, CAUGHT_CAGE_1, CAUGHT_CAGE_2, CAUGHT_CAGE_3)) {
			e.getPlayer().sendMessage("You're not going to pick that up without something to put it in.");
			return;
		}
		if (caughtScorp(e.getPlayer(), e.getNPCId()))
			return;
		setCaughtScorp(e.getPlayer(), e.getNPCId());
		e.getPlayer().getInventory().removeAllItems(EMPTY_CAGE, CAUGHT_CAGE_1, CAUGHT_CAGE_2);
		e.getPlayer().getInventory().addItem(getCageId(e.getPlayer()), 1);
		e.getPlayer().sendMessage("You add the scorpion to the cage.");
	});
	
	public static int getCageId(Player player) {
		return switch(getNumCaught(player)) {
			case 1 -> CAUGHT_CAGE_1;
			case 2 -> CAUGHT_CAGE_2;
			case 3 -> CAUGHT_CAGE_3;
			default -> EMPTY_CAGE;
		};
	}
	
	public static void setCaughtScorp(Player player, int id) {
		int idx = (id - SCORP_1) + 1;
		player.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).setB("scorp"+idx+"Caught", true);
	}
	
	public static boolean caughtScorp(Player player, int id) {
		int idx = (id - SCORP_1) + 1;
		return player.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getB("scorp"+idx+"Caught");
	}
	
	public static int getNumCaught(Player player) {
		int num = 0;
		for (int i = 1;i <= 3;i++)
			if (player.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getB("scorp"+i+"Caught"))
				num++;
		return num;
	}

	@Override
	public void complete(Player player) {
		player.getSkills().addXpQuest(Constants.STRENGTH, 6625);
		getQuest().sendQuestCompleteInterface(player, 456, "6,625 Strength XP");
	}
}
