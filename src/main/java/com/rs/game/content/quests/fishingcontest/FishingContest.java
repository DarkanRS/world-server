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
package com.rs.game.content.quests.fishingcontest;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.World;
import com.rs.game.content.skills.fishing.Fishing;
import com.rs.game.content.skills.fishing.FishingSpot;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import java.util.ArrayList;
import java.util.List;

@QuestHandler(Quest.FISHING_CONTEST)
@PluginEventHandler
public class FishingContest extends QuestOutline {
	public final static int NOT_STARTED = 0;
	public final static int ENTER_COMPETITION = 1;
	public final static int DO_ROUNDS = 2;
	public final static int GIVE_TROPHY = 3;
	public final static int QUEST_COMPLETE = 4;

	public static final int FISHING_PASS = 27;

	public static final String PIPE_HAS_GARLIC = "HAS_GARLIC";
	@Override
	public int getCompletedStage() {
		return QUEST_COMPLETE;
	}

	@Override
	public List<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch(stage) {
			case NOT_STARTED:
				lines.add("The mountain dwarves' home would be an ideal way to get");
				lines.add("across White Wolf mountain safely.");
				lines.add("");
				lines.add("However, the Dwarves aren't too fond of strangers. Austri");
				lines.add("and Vestri will let you through the tunnel under the");
				lines.add("mountain, however, if you can bring them a trophy. The");
				lines.add("trophy is the prize for the annual Hemenster Fishing");
				lines.add("competition.");
				lines.add("");
				lines.add("~~Requirements~~");
				lines.add("10 fishing");
				lines.add("");
				break;
			case ENTER_COMPETITION:
				lines.add("I can take my fishing contest pass to a small contest");
				lines.add("platform northeast of the fishing guild. There I can");
				lines.add("Show my pass and enter. If I need another pass I can");
				lines.add("get it from the Dwarven borthers in Catherby and");
				lines.add("Taverley.");
				lines.add("");
				break;
			case DO_ROUNDS:
				lines.add("The contest has started but it appears I need to find");
				lines.add("a way to get the biggest fish. Perhaps Grandpa Jack");
				lines.add("can help.");
				lines.add("");
				break;
			case GIVE_TROPHY:
				lines.add("Now that I have the trophy I can give it to the");
				lines.add("Dwarven brothers...");
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

	public static final int RED_WORM = 25;
	public static ObjectClickHandler Redvine = new ObjectClickHandler(true, new Object[] { "Vine" }, e -> {
		Player p = e.getPlayer();
		GameObject obj = e.getObject();
		if(obj.getTile().getRegionId() == 10550) //mcgrubbers wood
			if(p.getInventory().hasFreeSlots()) {
				p.setNextAnimation(new Animation(2282));//herb picking anim
				p.getInventory().addItem(RED_WORM, 1);
				p.sendMessage("You get a worm from the vine...");
			}
	});

	public static NPCClickHandler handlePlayerFishingSpot = new NPCClickHandler(new Object[] { 233 }, e -> {
		Player p = e.getPlayer();
		NPC npc = e.getNPC();
		if(npc.getRegionId() == 10549) {
			if (p.getQuestManager().getStage(Quest.FISHING_CONTEST) >= GIVE_TROPHY) {
				p.sendMessage("Nothing interesting happens...");
				return;
			}
			e.getNPC().resetDirection();
			if(p.getQuestManager().getAttribs(Quest.FISHING_CONTEST).getB(PIPE_HAS_GARLIC))
				p.startConversation(new Conversation(p) {
					{//Sinister stranger
						addNPC(3677, HeadE.CALM_TALK, "I think you will find that is my spot.");
						addPlayer(HeadE.HAPPY_TALKING, "Can't you go to another spot?");
						addNPC(3677, HeadE.CALM_TALK, "I can't stand the smell of those pipes...");
						create();
					}
				});
			else if(p.getQuestManager().getStage(Quest.FISHING_CONTEST) > ENTER_COMPETITION)
				e.getPlayer().getActionManager().setAction(new Fishing(FishingSpot.SEA_BAIT, e.getNPC()));
			else
				p.startConversation(new Conversation(p) {
					{
						addNPC(225, HeadE.CALM_TALK, "Hey, you need to pay to enter the competition first! Only 5 coins for the entrance fee!");//Bonzo
						create();
					}
				});
		}
	});

	/**
	 * There is a clipflag on the garlic pipe and requires a distance or clip handling function.
	 */
	public static ItemOnObjectHandler handlePipeGarlic = new ItemOnObjectHandler(false, new Object[] { 41 }, new Object[] { 1550 }, e -> {
		Player p = e.getPlayer();
		int stage = p.getQuestManager().getStage(Quest.FISHING_CONTEST);
		p.walkToAndExecute(Tile.of(2638, 3445, 0), () -> {
			if (stage == ENTER_COMPETITION || stage == DO_ROUNDS)
				if (p.getQuestManager().getAttribs(Quest.FISHING_CONTEST).getB(PIPE_HAS_GARLIC)) {
					p.sendMessage("There is already garlic...");
					return;
				}
			p.getInventory().removeItems(new Item(1550, 1));
			p.getQuestManager().getAttribs(Quest.FISHING_CONTEST).setB(PIPE_HAS_GARLIC, true);
			p.sendMessage("You place the garlic in the pipe.");
			p.lock(8);
			WorldTasks.schedule(new Task() {
				int tick;
				NPC stranger;

				@Override
				public void run() {
					if (tick == 0)
						for (NPC npc : World.getNPCsInChunkRange(p.getChunkId(), 2))
							if (npc.getId() == 3677)//vampyre stranger
								stranger = npc;
					if (tick == 1) {
						if (stranger == null)
							stop();
						stranger.forceTalk("What is that ghastly smell?");
					}
					if (tick == 2) {
						p.startConversation(new Conversation(p) {
							{
								addNPC(3677, HeadE.FRUSTRATED, "Can I take the spot by the willow tree?");
								addPlayer(HeadE.HAPPY_TALKING, "Sure...");
								addNext(() -> {
									p.unlock();
								});
								create();
							}
						});
						stop();
					}

					tick++;
				}
			}, 0, 1);
		});
	});



	public static final int FISHING_TROPHY = 26;
	@Override
	public void complete(Player player) {
		player.getSkills().addXpQuest(Constants.FISHING, 2437);
		sendQuestCompleteInterface(player, FISHING_TROPHY);
	}

	@Override
	public String getStartLocationDescription() {
		return "Talk to Vestri or Austri near White Wolf Mountain.";
	}

	@Override
	public String getRequiredItemsString() {
		return "5 coins, garlic.";
	}

	@Override
	public String getCombatInformationString() {
		return "None.";
	}

	@Override
	public String getRewardsString() {
		return "2,437 Fishing XP<br>" +
				"Access to the White Wolf Mountain shortcut";
	}
}
