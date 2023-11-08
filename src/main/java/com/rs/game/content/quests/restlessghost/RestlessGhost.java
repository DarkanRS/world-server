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
package com.rs.game.content.quests.restlessghost;

import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import java.util.ArrayList;
import java.util.List;

@QuestHandler(Quest.RESTLESS_GHOST)
@PluginEventHandler
public class RestlessGhost extends QuestOutline {

	public static int SKULL_CONF = 2130;

	@Override
	public int getCompletedStage() {
		return 4;
	}

	@Override
	public List<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();

		switch(stage) {
		case 0:
			lines.add("I can start this quest by speaking to Father Aereck");
			lines.add("in the lumbridge chapel.");
			break;
		case 1:
			lines.add("Aereck told me there is a ghost haunting his graveyard.");
			lines.add("He told me I should speak with Father Urhney about what");
			lines.add("to do next.");
			lines.add("");
			lines.add("I was told he can be found in southern Lumbridge swamp.");
			break;
		case 2:
			lines.add("I was given an amulet of ghostspeak by Father Urhney.");
			lines.add("The amulet should let me communicate with the ghost somehow.");
			lines.add("");
			lines.add("I should find the ghost and try to figure out what is");
			lines.add("causing it to haunt the church graveyard.");
			break;
		case 3:
			lines.add("I found the ghost and he told me that he has lost his skull.");
			lines.add("");
			lines.add("He said he lost it somewhere near the swamp mines.");
			break;
		case 4:
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
		player.getSkills().addXpQuest(Constants.PRAYER, 1125);
		sendQuestCompleteInterface(player, 553);
	}

	@Override
	public String getStartLocationDescription() {
		return "Talk to Father Aereck in the church east of Lumbridge Castle.";
	}

	@Override
	public String getRequiredItemsString() {
		return "Armour to survive a level 7 skeleton warlock attack.";
	}

	@Override
	public String getCombatInformationString() {
		return "Facing a level 7 skeleton warlock.";
	}

	@Override
	public String getRewardsString() {
		return "125 Prayer XP<br>" +
				"Five ancient bones that each give 200 Prayer XP";
	}

	private static boolean hasSkull(Player player) {
		if (player.getInventory().containsItem(553, 1) || player.isQuestComplete(Quest.RESTLESS_GHOST))
			return true;
		return false;
	}

	private static void refreshSkull(Player player) {
		player.getVars().setVarBit(SKULL_CONF, hasSkull(player) ? 1 : 0);
	}

	public static LoginHandler onLogin = new LoginHandler(e -> refreshSkull(e.getPlayer()));

	public static ObjectClickHandler handleSkullRock = new ObjectClickHandler(new Object[] { 47713 }, e -> {
		if (e.getPlayer().getQuestManager().getStage(Quest.RESTLESS_GHOST) == 3) {
			e.getPlayer().sendMessage("You take the skull.");
			e.getPlayer().getInventory().addItem(553, 1);
			refreshSkull(e.getPlayer());
		}
	});

	public static ObjectClickHandler handleCoffin = new ObjectClickHandler(new Object[] { 2145 }, e -> {
		if (e.getPlayer().getQuestManager().getStage(Quest.RESTLESS_GHOST) == 2) {
			e.getPlayer().getQuestManager().setStage(Quest.RESTLESS_GHOST, 3);
			e.getPlayer().sendMessage("A ghost appears nearby!");
		}
	});

	public static NPCClickHandler talkToNpcs = new NPCClickHandler(new Object[] { 457 }, e -> {
				if (e.getPlayer().getQuestManager().getStage(Quest.RESTLESS_GHOST) == 3)
					e.getPlayer().startConversation(new RGhostD(e.getPlayer()));
	});
}