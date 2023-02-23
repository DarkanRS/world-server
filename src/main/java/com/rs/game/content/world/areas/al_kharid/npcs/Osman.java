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
package com.rs.game.content.world.areas.al_kharid.npcs;

import com.rs.game.content.quests.princealirescue.OsmanPrinceAliRescueD;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.dialogue.statements.NPCStatement;
import com.rs.engine.dialogue.statements.OptionStatement;
import com.rs.engine.dialogue.statements.PlayerStatement;
import com.rs.engine.dialogue.statements.SimpleStatement;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Osman extends Conversation {

	private static final int npcId = 5282;

	public static NPCClickHandler Osman = new NPCClickHandler(new Object[]{npcId}, e -> {
		int convoID = 1;
		switch (e.getOption()) {
			//Start Conversation
			case "Talk-to" -> e.getPlayer().startConversation(new Osman(e.getPlayer(), convoID));
		}
	});
	
	public Osman(Player player, int convoID) {
		super(player);

		if(convoID == 0)
			if (player.getInventory().containsOneItem(10848, 10849, 10850, 10851)) {
				addNext(new PlayerStatement(HeadE.CHEERFUL, "I have some sq'irk juice for you."));
				addNext(new Dialogue(new SimpleStatement("Osman imparts some Thieving advice to you as a reward for the sq'irk juice."), () -> {
					int totalXp = player.getInventory().getAmountOf(10851) * 350;
					totalXp += player.getInventory().getAmountOf(10848) * 1350;
					totalXp += player.getInventory().getAmountOf(10850) * 2350;
					totalXp += player.getInventory().getAmountOf(10849) * 3000;
					player.getInventory().deleteItem(10848, Integer.MAX_VALUE);
					player.getInventory().deleteItem(10849, Integer.MAX_VALUE);
					player.getInventory().deleteItem(10850, Integer.MAX_VALUE);
					player.getInventory().deleteItem(10851, Integer.MAX_VALUE);
					player.getSkills().addXp(Constants.THIEVING, totalXp);
				}));
			} else {
				addNext(new PlayerStatement(HeadE.CHEERFUL, "Hi, I'd like to talk about sq'irks."));
				addNext(new NPCStatement(convoID, HeadE.CHEERFUL, "Alright, what would you like to know about sq'irks?"));
				addNext("SqOp", new OptionStatement("What would you like to know?", "Where can I find sq'irks?", "Why can't you get the sq'irks yourself?", "How should I squeeze the fruit?", "Is there a reward for getting these sq'irks?", "What's so good about sq'irk juice then?"));

				getStage("SqOp")
				.addNext(new PlayerStatement(HeadE.CONFUSED, "Where can I find sq'irks?"))
				.addNext(new NPCStatement(convoID, HeadE.FRUSTRATED, "There is a sorceress near the south eastern edge of Al Kharid who grows them. Once upon a time we considered each other friends."))
				.addNext(new PlayerStatement(HeadE.CONFUSED, "What happened?"))
				.addNext(new NPCStatement(convoID, HeadE.FRUSTRATED, "We fell out, and now she won't give me any more fruit."))
				.addNext(new PlayerStatement(HeadE.CONFUSED, "So all I have to do is ask her for some fruit for you?"))
				.addNext(new NPCStatement(convoID, HeadE.NERVOUS, "I doubt it will be that easy. She is not renowned for her generosity and is very secretive about her garden's location."))
				.addNext(new PlayerStatement(HeadE.LAUGH, "Oh come on, it should be easy enough to find!"))
				.addNext(new NPCStatement(convoID, HeadE.SHAKING_HEAD, "Her garden has remained hidden even to me - the chief spy of Al Kharid. I belive her garden must be hidden by magical means."))
				.addNext(new PlayerStatement(HeadE.CHEERFUL, "This should be an interesting task. How many sq'irks do you want?"))
				.addNext(new NPCStatement(convoID, HeadE.CHEERFUL, "I'll reward you as many as you can get your hands on but could you please squeeze the fruit into a glass first?"))
				.addNext(getStage("SqOp"));

				getStage("SqOp")
				.addNext(new PlayerStatement(HeadE.CONFUSED, "Why can't you get the sq'irks yourself?"))
				.addNext(new NPCStatement(convoID, HeadE.FRUSTRATED, "I may have mentioned that I had a falling out with Sorceress. Well, unsurprisingly, she refuses to give me any more of her garden's produce."))
				.addNext(getStage("SqOp"));

				getStage("SqOp")
				.addNext(new PlayerStatement(HeadE.CONFUSED, "How should I squeeze the fruit?"))
				.addNext(new NPCStatement(convoID, HeadE.CHEERFUL, "Use a pestle and mortal to squeeze the sq'irks. Make sure you have an empty glass with you to collect the juice."))
				.addNext(getStage("SqOp"));

				getStage("SqOp")
				.addNext(new PlayerStatement(HeadE.CONFUSED, "Is there a reward for getting these sq'irks?"))
				.addNext(new NPCStatement(convoID, HeadE.LAUGH, "Of course there is. I am a generous man. I'll teach you the art of Thieving for your troubles."))
				.addNext(new PlayerStatement(HeadE.CONFUSED, "How much training will you give?"))
				.addNext(new NPCStatement(convoID, HeadE.CHEERFUL, "That depends on the quantity and ripeness of the sq'irks you put into the juice."))
				.addNext(getStage("SqOp"));

				getStage("SqOp")
				.addNext(new PlayerStatement(HeadE.CONFUSED, "What's so good about sq'irk juice then?"))
				.addNext(new NPCStatement(convoID, HeadE.LAUGH, "Ah it's sweet, sweet nectar for a thief or spy; it makes light fingers lighter, fleet fleet flightier and comes in four different colours for those who are easily amused."))
				.addNext(new SimpleStatement("Osman starts salivating at the thought of sq'irk juice."))
				.addNext(new PlayerStatement(HeadE.SKEPTICAL, "It wouldn't have any addictive properties, would it?"))
				.addNext(new NPCStatement(convoID, HeadE.CHEERFUL, "It only holds power over those with poor self-control, something which I have an abundance of."))
				.addNext(new PlayerStatement(HeadE.SKEPTICAL, "I see."))
				.addNext(getStage("SqOp"));
			}
		else
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					if(!player.isQuestComplete(Quest.PRINCE_ALI_RESCUE))
						option("About Prince Ali To The Rescue", new Dialogue() //TODO Non-vanilla dialogue
								.addNext(()->{player.startConversation(new OsmanPrinceAliRescueD(player));}));
					option("Sorcerer's Garden", new Dialogue()
							.addNext(()->{
								player.startConversation(new Osman(player, 0).getStart());
							}));
				}
			});

		create();
	}

}
