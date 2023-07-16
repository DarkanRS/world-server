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

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class MorganVampireSlayerD extends Conversation {
	final static int MORGAN = 755;

	public MorganVampireSlayerD(Player player) {
		super(player);
		switch (player.getQuestManager().getStage(Quest.VAMPYRE_SLAYER)) {
		case VampireSlayer.NOT_STARTED:
			addNPC(MORGAN, HeadE.WORRIED, "Please please help us, bold adventurer!");
			addPlayer(HeadE.SKEPTICAL_THINKING, "What's the problem?");
			addNPC(MORGAN, HeadE.SAD_CRYING, "Our little village has been dreadfully ravaged by an evil vampyre! He lives in the basement of the manor" +
					" to the north, we need someone to get rid of him once and for all!");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("No, vampyres are scary!", new Dialogue()
							.addPlayer(HeadE.SCARED, "No, vampyres are scary!")
							.addNPC(MORGAN, HeadE.SAD, "I don't blame you."));
					option("Ok, I'm up for an adventure.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Ok, I'm up for an adventure.")
							.addNPC(MORGAN, HeadE.CALM_TALK, "I think first you should seek help. I have a friend who is a retired vampyre hunter, " +
									"his name is Dr. Harlow. He may be able to give you some tips. ")
							.addNPC(MORGAN, HeadE.CALM_TALK, "He can normally be found in the Blue Moon Inn in Varrock, he's a bit of an old soak " +
									"these days. Mention his old friend Morgan, I'm sure he wouldn't want me killed by a vampyre.")
							.addPlayer(HeadE.HAPPY_TALKING, "I'll look him up then.")
							.addNext(()->{
								player.getQuestManager().setStage(Quest.VAMPYRE_SLAYER, VampireSlayer.STARTED);}));
					option("Have you got any tips on killing the vampyre?", new Dialogue()
							.addPlayer(HeadE.CALM_TALK, "Have you got any tips on killing the vampyre?")
							.addNPC(MORGAN, HeadE.CALM_TALK, "I think first you should seek help. I have a friend who is a retired vampyre hunter, " +
									"his name is Dr. Harlow. He may be able to give you some tips. ")
							.addNPC(MORGAN, HeadE.CALM_TALK, "He can normally be found in the Blue Moon Inn in Varrock, he's a bit of an old soak " +
									"these days. Mention his old friend Morgan, I'm sure he wouldn't want me killed by a vampyre.")
							.addPlayer(HeadE.HAPPY_TALKING, "I'll look him up then.")
							.addNext(()->{
								player.getQuestManager().setStage(Quest.VAMPYRE_SLAYER, VampireSlayer.STARTED);}));
				}
			});
			break;
		case VampireSlayer.STARTED:
			addNPC(MORGAN, HeadE.CALM_TALK, "How are you doing with the quest?");
			addPlayer(HeadE.CALM_TALK, "I'm still working on it.");
			addNPC(MORGAN, HeadE.SCARED, "Please hurry! Every day we live in fear that we will be the vampyre's next victim!");
			break;
		case VampireSlayer.STAKE_RECIEVED:
			if(player.getTempAttribs().getB("morganHarlowPrompt")) {
				addNPC(MORGAN, HeadE.SCARED, "Please hurry...");
				break;
			}
			addNPC(MORGAN, HeadE.CALM_TALK, "Have you talked to Dr. Harlow?");
			addPlayer(HeadE.CALM_TALK, "Yes, I just need a stake & hammer and I am ready to kill the vampyre.");
			addNPC(MORGAN, HeadE.SCARED, "Great, please hurry every day we live in fear we will be the vampyre's next victim!", ()->{
				player.getTempAttribs().setB("morganHarlowPrompt", true);});

			break;
		case VampireSlayer.VAMPYRE_KILLED:
			addPlayer(HeadE.HAPPY_TALKING, "I have slain the foul creature!");
			addNPC(MORGAN, HeadE.HAPPY_TALKING, "Thank you, thank you! You will always be a hero in our village!");
			addNext(()-> {
				player.getQuestManager().completeQuest(Quest.VAMPYRE_SLAYER);
			});
			break;
		case VampireSlayer.QUEST_COMPLETE:
			addNPC(MORGAN, HeadE.HAPPY_TALKING, "Thank you once again for slaying Count Draynor, we are in your debt.");
			break;
		}
	}

	public static NPCClickHandler handleMorgan = new NPCClickHandler(new Object[] { MORGAN }, e -> e.getPlayer().startConversation(new MorganVampireSlayerD(e.getPlayer()).getStart()));
}
