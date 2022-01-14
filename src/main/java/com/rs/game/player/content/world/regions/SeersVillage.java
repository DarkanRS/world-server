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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.world.regions;

import com.rs.game.ForceMovement;
import com.rs.game.object.GameObject;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.game.player.content.achievements.AchievementSystemDialogue;
import com.rs.game.player.content.achievements.SetReward;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.content.skills.agility.Agility;
import com.rs.game.player.content.world.AgilityShortcuts;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.handlers.merlinscrystal.knightsroundtable.SirKayMerlinsCrystalD;
import com.rs.game.player.quests.handlers.scorpioncatcher.ScorpionCatcher;
import com.rs.game.player.quests.handlers.scorpioncatcher.SeerScorpionCatcherD;
import com.rs.game.player.quests.handlers.scorpioncatcher.ThormacScorpionCatcherD;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class SeersVillage {

	public static NPCClickHandler handleStankers = new NPCClickHandler(383) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what can I do for you?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.SEERS_HEADBAND).getStart());
						}
					});
				}
			});
		}
	};

	public static ObjectClickHandler grubersWoodFence = new ObjectClickHandler(new Object[] { 51 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(p.getX() < obj.getX())
				WorldTasks.schedule(new WorldTask() {
					int tick = 0;

					@Override
					public void run() {
						if(tick == 0) {
							p.setNextForceMovement(new ForceMovement(new WorldTile(2662, 3500, 0), 1, Direction.EAST));
							p.setNextAnimation(new Animation(3844));
						}
						if (tick == 1) {
							p.setNextWorldTile(new WorldTile(2662, 3500, 0));
							stop();
						}
						tick++;
					}
				}, 0, 1);
			else
				WorldTasks.schedule(new WorldTask() {
					int tick = 0;

					@Override
					public void run() {
						if(tick == 0) {
							p.setNextForceMovement(new ForceMovement(new WorldTile(2661, 3500, 0), 1, Direction.WEST));
							p.setNextAnimation(new Animation(3844));
						}
						if (tick == 1) {
							p.setNextWorldTile(new WorldTile(2661, 3500, 0));
							stop();
						}
						tick++;
					}
				}, 0, 1);
		}
	};

	public static NPCClickHandler handleSeer = new NPCClickHandler(388) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.DRUNK, "Uh, what was that dark force? I've never sensed anything like it...");
					addNPC(e.getNPCId(), HeadE.NO_EXPRESSION, "Anyway, sorry about that.");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							if(e.getPlayer().getQuestManager().getStage(Quest.SCORPION_CATCHER) == ScorpionCatcher.LOOK_FOR_SCORPIONS)
								option("About Scorpion Catcher", new Dialogue()
										.addNext(() -> {
											e.getPlayer().startConversation(new SeerScorpionCatcherD(e.getPlayer()).getStart());
										})
										);
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.SEERS_HEADBAND).getStart());
						}
					});
				}
			});
		}
	};

	public static NPCClickHandler handleThormacDialogue = new NPCClickHandler(389) {
		@Override
		public void handle(NPCClickEvent e) {
			int NPC= e.getNPCId();
			if(!e.getPlayer().getQuestManager().isComplete(Quest.SCORPION_CATCHER))
				e.getPlayer().startConversation(new ThormacScorpionCatcherD(e.getPlayer()).getStart());
			else
				/**
				 * Jawa doesn't want to code something no one will use...
				 */
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addPlayer(HeadE.HAPPY_TALKING, "You said you would give me mystic battlestaffs for enchanted battlestaffs for 40k...");
						addNPC(NPC, HeadE.CALM_TALK, "I did didn't I?");
						addNPC(NPC, HeadE.CALM_TALK, "Well, if just one player asks for it then it will happen dealio?");
						addPlayer(HeadE.HAPPY_TALKING, "Deal!");
						addSimple("Some poor developer waits for you...");
						create();
					}
				});
		}
	};

	public static NPCClickHandler handleSirKay = new NPCClickHandler(241) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what can I do for you?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.SEERS_HEADBAND).getStart());
							if(!player.getQuestManager().isComplete(Quest.MERLINS_CRYSTAL))
								option("About Merlin's Crystal", new Dialogue()
										.addNext(()->{e.getPlayer().startConversation(new SirKayMerlinsCrystalD(e.getPlayer()).getStart());}));
						}
					});
				}
			});
		}
	};

	public static ObjectClickHandler handleRoofLadder = new ObjectClickHandler(new Object[] { 26118, 26119 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().ladder(e.getPlayer().transform(0, 0, e.getObjectId() == 26118 ? 2 : -2));
		}
	};

	public static ObjectClickHandler handleCoalTruckLogBalance = new ObjectClickHandler(new Object[] { 2296 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 20))
				return;
			AgilityShortcuts.walkLog(e.getPlayer(), e.getPlayer().transform(e.getObject().getRotation() == 1 ? -5 : 5, 0, 0), 4);
		}
	};

	public static ObjectClickHandler handleSinclairMansionLogBalance = new ObjectClickHandler(new Object[] { 9322, 9324 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 48))
				return;
			AgilityShortcuts.walkLog(e.getPlayer(), e.getPlayer().transform(0, e.getObjectId() == 9322 ? -4 : 4, 0), 3);
		}
	};
}
