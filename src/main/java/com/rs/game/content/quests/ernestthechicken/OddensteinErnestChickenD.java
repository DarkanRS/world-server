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
package com.rs.game.content.quests.ernestthechicken;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.Ticks;

import java.util.List;

@PluginEventHandler
public class OddensteinErnestChickenD extends Conversation {
	private static final int ODDENSTEIN = 286;
	private static final int ERNEST = 287;

	static int PRESSURE_GAUGE = 271;
	static int RUBBER_TUBE = 276;
	static int OIL_CAN = 277;

	public OddensteinErnestChickenD(Player player) {
		super(player);
		switch (player.getQuestManager().getStage(Quest.ERNEST_CHICKEN)) {
		case ErnestTheChicken.NOT_STARTED:
		case ErnestTheChicken.STARTED:
		case ErnestTheChicken.KNOWS_ABOUT_CHICKEN:
		case ErnestTheChicken.QUEST_COMPLETE:
			addNPC(ODDENSTEIN, HeadE.HAPPY_TALKING, "Be careful in here, there's lots of dangerous equipment.");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					if(player.getQuestManager().getStage(Quest.ERNEST_CHICKEN) == ErnestTheChicken.STARTED
							|| player.getQuestManager().getStage(Quest.ERNEST_CHICKEN) == ErnestTheChicken.KNOWS_ABOUT_CHICKEN)
						option("I'm looking for a guy called Ernest", new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "I'm looking for a guy called Ernest.")
								.addNPC(ODDENSTEIN, HeadE.HAPPY_TALKING, "Ah, Ernest - top notch bloke - he's helping me with my experiments.")
								.addPlayer(HeadE.SKEPTICAL_THINKING, "So you know where he is?")
								.addNPC(ODDENSTEIN, HeadE.HAPPY_TALKING, "He's that chicken over there.")
								.addPlayer(HeadE.SKEPTICAL_THINKING, "Ernest is a chicken? Are you sure?")
								.addNPC(ODDENSTEIN, HeadE.HAPPY_TALKING, "Oh, he isn't normally a chicken, or, at least, he wasn't until he helped me test my" +
										" pouletmorph machine. ")
								.addNPC(ODDENSTEIN, HeadE.TALKING_ALOT, "It was originally going to be called a transmutation machine, but after testing it, pouletmorph " +
										"seems more appropriate.", () -> {
											player.getQuestManager().setStage(Quest.ERNEST_CHICKEN, ErnestTheChicken.KNOWS_ABOUT_CHICKEN);
										})
								.addOptions("Choose an option:", new Options() {
									@Override
									public void create() {
										option("I'm glad Veronica didn't actually get engaged to a chicken", new Dialogue()
												.addNPC(ODDENSTEIN, HeadE.HAPPY_TALKING, "Who's Veronica?")
												.addPlayer(HeadE.ANGRY, "Ernest's fiancée. She probably doesn't want to marry a chicken.")
												.addNPC(ODDENSTEIN, HeadE.HAPPY_TALKING, "Oh, I dunno. She could have free eggs for breakfast every morning")
												.addPlayer(HeadE.FRUSTRATED, "I think you'd better change him back")
												.addPlayer(HeadE.ANGRY, "Change him back this instant!")
												.addNPC(ODDENSTEIN, HeadE.NERVOUS, "Umm... It's not so easy...")
												.addNPC(ODDENSTEIN, HeadE.NERVOUS, "My machine is broken, and the house gremlins have run off with some vital bits.")
												.addPlayer(HeadE.FRUSTRATED, "Well I can look for them.")
												.addNPC(ODDENSTEIN, HeadE.CALM_TALK, "That would be a help. They'll be somewhere in the manor house or its grounds, " +
														"the gremlins never get further than the entrance gate.")
												.addNPC(ODDENSTEIN, HeadE.CALM_TALK, "I'm missing the pressure gauge and a rubber tube. They've also taken my oil " +
														"can, which I'm going to need to get this thing started again.", () -> {
															player.getQuestManager().setStage(Quest.ERNEST_CHICKEN, ErnestTheChicken.NEEDS_PARTS);
														}));
										option("Change him back this instant", new Dialogue()
												.addPlayer(HeadE.ANGRY, "Change him back this instant!")
												.addNPC(ODDENSTEIN, HeadE.NERVOUS, "Umm... It's not so easy...")
												.addNPC(ODDENSTEIN, HeadE.NERVOUS, "My machine is broken, and the house gremlins have run off with some vital bits.")
												.addPlayer(HeadE.FRUSTRATED, "Well I can look for them.")
												.addNPC(ODDENSTEIN, HeadE.CALM_TALK, "That would be a help. They'll be somewhere in the manor house or its grounds, " +
														"the gremlins never get further than the entrance gate.")
												.addNPC(ODDENSTEIN, HeadE.CALM_TALK, "I'm missing the pressure gauge and a rubber tube. They've also taken my oil " +
														"can, which I'm going to need to get this thing started again.", () -> {
															player.getQuestManager().setStage(Quest.ERNEST_CHICKEN, ErnestTheChicken.NEEDS_PARTS);
														}));
									}
								}));
					option("What does this machine do?", new Dialogue()
							.addPlayer(HeadE.SKEPTICAL_THINKING, "What does this machine do?")
							.addNPC(ODDENSTEIN, HeadE.NERVOUS, "Nothing at the moment... It's broken. It's meant to be a transmutation machine.")
							.addNPC(ODDENSTEIN, HeadE.HAPPY_TALKING, "It has also spent time as a time travel machine, and a dramatic lightning generator, and a " +
									"thing for generating monsters."));
					option("Is this your house?", new Dialogue()
							.addPlayer(HeadE.CALM_TALK, "Is this your house?")
							.addNPC(ODDENSTEIN, HeadE.HAPPY_TALKING, "No, I'm just one of the tenants. It belongs to the count who lives in the basement.")
							.addNPC(ODDENSTEIN, HeadE.HAPPY_TALKING, "Be careful in here, there's lots of dangerous equipment."));
				}
			});
			break;
		case ErnestTheChicken.NEEDS_PARTS:
			if(player.getInventory().containsItem(OIL_CAN) && player.getInventory().containsItem(PRESSURE_GAUGE) && player.getInventory().containsItem(RUBBER_TUBE)) {
				addPlayer(HeadE.HAPPY_TALKING, "I have gotten all your parts!");
				addSimple("You give a rubber tube, a pressure gauge, and a can of oil to the professor.");
				addSimple("Oddenstein starts up the machine.");
				addSimple("The machine hums and shakes.", ()-> {
					List<NPC> npcs = World.getNPCsInChunkRange(player.getChunkId(), 1);
					for(NPC npc : npcs)
						if(npc.getId() == 3290)
							npc.transformIntoNPC(287);
					WorldTasks.schedule(new Task() {
						@Override
						public void run() {
							for(NPC npc : npcs)
								if(npc.getId() == 287)
									npc.transformIntoNPC(3290);
							player.getVars().setVar(32, 3);
						}
					}, Ticks.fromSeconds(60));
					player.getQuestManager().setStage(Quest.ERNEST_CHICKEN, ErnestTheChicken.ERNEST_NOT_CHICKEN);

				});
				addNPC(ERNEST, HeadE.HAPPY_TALKING, "Thank you, it was dreadfully irritating being a chicken. How can *cluck* I ever thank you?");
				addPlayer(HeadE.HAPPY_TALKING, "A cash reward is always nice...");
				addNPC(ERNEST, HeadE.HAPPY_TALKING, "Of course, of course. You may as well have these eggs and *cluck* feathers I mysteriously *bwark*" +
						" found in my pockets.");
				addNext(()->{
					player.getVars().setVar(32, 3);
					player.getInventory().deleteItem(OIL_CAN, 1);
					player.getInventory().deleteItem(RUBBER_TUBE, 1);
					player.getInventory().deleteItem(PRESSURE_GAUGE, 1);
					player.getQuestManager().completeQuest(Quest.ERNEST_CHICKEN);});

			}
			else {
				player.getVars().setVar(32, 3);
				addNPC(ODDENSTEIN, HeadE.HAPPY_TALKING, "Have you found them yet?");
				addPlayer(HeadE.SAD_MILD, "I'm afraid I don't have all of them yet!");
				addNPC(ODDENSTEIN, HeadE.HAPPY_TALKING, "Remember, I need a rubber tube, a pressure gauge and a can of oil. Then your friend can stop " +
						"being a chicken.");
			}
			break;
		case ErnestTheChicken.ERNEST_NOT_CHICKEN:
			addNPC(ODDENSTEIN, HeadE.HAPPY_TALKING, "Ernest left this for you...");
			addNext(()->{
				player.getInventory().deleteItem(OIL_CAN, 1);
				player.getInventory().deleteItem(RUBBER_TUBE, 1);
				player.getInventory().deleteItem(PRESSURE_GAUGE, 1);
				player.getQuestManager().completeQuest(Quest.ERNEST_CHICKEN);});

			break;
		}
	}

	public static NPCClickHandler handleProfessor = new NPCClickHandler(new Object[] { ODDENSTEIN }, e -> e.getPlayer().startConversation(new OddensteinErnestChickenD(e.getPlayer()).getStart()));
}
