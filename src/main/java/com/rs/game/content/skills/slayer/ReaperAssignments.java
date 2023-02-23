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
package com.rs.game.content.skills.slayer;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class ReaperAssignments  {

	static class ReaperDialogue extends Conversation {

		public ReaperDialogue(Player player) {
			super(player);
			if (!((boolean) player.get("learnedDeath"))) {
				addNPC(15661, HeadE.CALM, "Hello mortal. I am in need of some assistance and I have an offer to propose to you. Listen closely.");
				addNPC(15661, HeadE.CALM, "As you are no doubt aware, I am Death. The Reaper, the Collector of Souls, or any other name civilizations have given me. My task is to retrieve "
						+ "the soul from a dying creature, enabling its passage to the underworld.");
				addPlayer(HeadE.CONFUSED, "So what do you need me for?");
				addNPC(15661, HeadE.CALM, "There is an imbalance in the harmony of life and death. There is far too much... life.");
				addNPC(15661, HeadE.CALM, "My eyes have been on you in this age, " + player.getDisplayName() + ", as have the eyes of many others. You appear to have the skills "
						+ "I require to bring balance.");
				addPlayer(HeadE.CONFUSED, "So you want me to kill stuff?");
				addNPC(15661, HeadE.CALM, "If you wish to put it so indelicately, yes.");
				addPlayer(HeadE.HAPPY_TALKING, "Great! When do I start?");
				addNPC(15661, HeadE.CALM, "Immediately.");
				addNext(() -> {
					player.save("learnedDeath", true);
					player.getInventory().addItemDrop(24806, 1);
					giveNewTask(player);
				});
			} else {
				addNPC(15661, HeadE.CALM, "What is it, mortal? Time is ticking.");
				addOptions("What would you like to say?", new Options() {
					@Override
					public void create() {
						option("I need an assignment.", () -> {
							talkAboutAssignment(player);
						});

						option("I'd like another grim gem.", new Dialogue()
								.addItem(24806, "You receive a grim gem.", () -> {
									player.getInventory().addItem(24806, 1);
								}));

						option("Are there any rewards for this?", new Dialogue()
								.addNPC(15661, HeadE.CALM, "Not yet, mortal. I am still thinking about possible reward options. But I will still keep tally of your points regardless."));
					}
				});
			}
			create();
		}
	}

	public static void talkAboutAssignment(Player player) {
		if (player.getBossTask() == null)
			giveNewTask(player);
		else
			player.startConversation(new Conversation(player).addNext(getRerollTaskDialogue(player)));
	}

	public static void giveNewTask(Player player) {
		if (player.getDailyB("bossTaskCompleted")) {
			player.startConversation(new Conversation(player)
					.addNPC(15661, HeadE.CALM, "The imbalance has been corrected for today; your task is done. Visit me tomorrow for further instructions."));
			return;
		}
		player.setBossTask(BossTask.create());
		player.startConversation(new Conversation(player)
				.addNPC(15661, HeadE.CALM, "I require you to collect " + player.getBossTask().getAmount() + " souls from the following battle: " + player.getBossTask().getName() + ". Can I trust you with this task?")
				.addOptions("Select an Option", new Options() {
					@Override
					public void create() {
						option("You certainly can. Thanks!", () -> {});
						option("I'd like a different assignment.", getRerollTaskDialogue(player));
					}
				}));
	}

	public static Dialogue getRerollTaskDialogue(Player player) {
		Dialogue d = new Dialogue();
		if (player.getDailyI("bossTaskRerolls") < 3)
			d.addNPC(15661, HeadE.CALM, "Do not think I will allow you to change your mind freely. I will only allow you to change it 3 times per day. "
					+ (player.getDailyI("bossTaskRerolls") == 0 ? "" : "You've already used up " + player.getDailyI("bossTaskRerolls") + " of those."))
			.addOption("Are you sure you want to reroll your task?", "Yes, I am sure.", "Nevermind, I will just do this task.")
			.addNext(() -> {
				player.incDailyI("bossTaskRerolls");
				giveNewTask(player);
			});
		else
			d.addNPC(15661, HeadE.CALM, "My patience only stretches so far. You have wasted enough of my time, I must address this imbalance myself. Be gone, mortal.");
		return d;
	}

	public static ItemClickHandler handleGrimGem = new ItemClickHandler(new Object[] { 24806 }, e -> {
		switch(e.getOption()) {
		case "Activate":
			e.getPlayer().startConversation(new ReaperDialogue(e.getPlayer()));
			break;
		case "Kills-left":
			if (e.getPlayer().getBossTask() != null)
				e.getPlayer().sendMessage(e.getPlayer().getBossTask().getMessage());
			else
				e.getPlayer().sendMessage("You do not have a task assigned right now.");
			break;
		case "Assignment":
			talkAboutAssignment(e.getPlayer());
			break;
		}
	});

	public static NPCClickHandler handleDeath = new NPCClickHandler(new Object[] { 15661 }, e -> {
		switch(e.getOpNum()) {
		case 1:
			e.getPlayer().startConversation(new ReaperDialogue(e.getPlayer()));
			break;
		case 3:
			talkAboutAssignment(e.getPlayer());
			break;
		case 4:
			e.getPlayer().sendMessage("Rewards are not implemented at the moment, but you can still gain points. Feel free to post suggestions for rewards in Discord.");
			break;
		}
	});

}
