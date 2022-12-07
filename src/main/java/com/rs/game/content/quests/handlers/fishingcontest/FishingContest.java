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
package com.rs.game.content.quests.handlers.fishingcontest;

import java.util.ArrayList;

import com.rs.game.World;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.quests.QuestHandler;
import com.rs.game.content.quests.QuestOutline;
import com.rs.game.content.skills.fishing.Fishing;
import com.rs.game.content.skills.fishing.FishingSpot;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

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
	public ArrayList<String> getJournalLines(Player player, int stage) {
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
	public static ObjectClickHandler Redvine = new ObjectClickHandler(true, new Object[] { "Vine" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(obj.getTile().getRegionId() == 10550) //mcgrubbers wood
				if(p.getInventory().hasFreeSlots()) {
					p.setNextAnimation(new Animation(2282));//herb picking anim
					p.getInventory().addItem(RED_WORM, 1);
					p.sendMessage("You get a worm from the vine...");
				}

		}
	};

	public static NPCClickHandler handlePlayerFishingSpot = new NPCClickHandler(new Object[] { 233 }) {
		@Override
		public void handle(NPCClickEvent e) {
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

		}
	};

	public static ItemClickHandler handleGiantCarp = new ItemClickHandler(337) {
		@Override
		public void handle(ItemClickEvent e) {
			Player p = e.getPlayer();
			if(e.getOption().equalsIgnoreCase("eat"))
				p.sendMessage("It doesn't appear edible...");
			if(e.getOption().equalsIgnoreCase("drop")) {
				e.getPlayer().getInventory().deleteItem(e.getSlotId(), e.getItem());
				World.addGroundItem(e.getItem(), WorldTile.of(e.getPlayer().getTile()), e.getPlayer());
				e.getPlayer().soundEffect(2739);
			}
		}
	};

	/**
	 * There is a clipflag on the garlic pipe and requires a distance or clip handling function.
	 */
	public static ItemOnObjectHandler handlePipeGarlic = new ItemOnObjectHandler(false, new Object[] { 41 }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			Player p = e.getPlayer();
			int stage = p.getQuestManager().getStage(Quest.FISHING_CONTEST);
			p.walkToAndExecute(WorldTile.of(2638, 3445, 0), () -> {
				if (stage == ENTER_COMPETITION || stage == DO_ROUNDS)
					if (e.getItem().getId() == 1550) {//garlic
						if (p.getQuestManager().getAttribs(Quest.FISHING_CONTEST).getB(PIPE_HAS_GARLIC)) {
							p.sendMessage("There is already garlic...");
							return;
						}
						p.getInventory().removeItems(new Item(1550, 1));
						p.getQuestManager().getAttribs(Quest.FISHING_CONTEST).setB(PIPE_HAS_GARLIC, true);
						p.sendMessage("You place the garlic in the pipe.");
						p.lock(8);
						WorldTasks.schedule(new WorldTask() {
							int tick;
							NPC stranger;

							@Override
							public void run() {
								if (tick == 0)
									for (NPC npc : World.getNPCsInRegion(p.getRegionId()))
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
					}
			});


		}
	};



	public static final int FISHING_TROPHY = 26;
	@Override
	public void complete(Player player) {
		player.getSkills().addXpQuest(Constants.FISHING, 2437);
		getQuest().sendQuestCompleteInterface(player, FISHING_TROPHY, "2,437 Fishing XP");
	}
}
