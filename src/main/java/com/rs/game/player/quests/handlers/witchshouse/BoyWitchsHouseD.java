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
package com.rs.game.player.quests.handlers.witchshouse;

import static com.rs.game.player.quests.handlers.witchshouse.WitchsHouse.BALL;
import static com.rs.game.player.quests.handlers.witchshouse.WitchsHouse.BOY;
import static com.rs.game.player.quests.handlers.witchshouse.WitchsHouse.FIND_BALL;
import static com.rs.game.player.quests.handlers.witchshouse.WitchsHouse.NOT_STARTED;
import static com.rs.game.player.quests.handlers.witchshouse.WitchsHouse.QUEST_COMPLETE;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.quests.Quest;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class BoyWitchsHouseD extends Conversation {

	public BoyWitchsHouseD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.WITCHS_HOUSE)) {
		case NOT_STARTED ->{
			addPlayer(HeadE.HAPPY_TALKING, "Hello young man.");
			addSimple("*The boy sobs");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("What's the matter?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "What's the matter?")
							.addNPC(BOY, HeadE.CHILD_CRYING, "I've kicked my ball over that hedge, into that garden! The old lady who lives there is scary... She's locked the " +
									"ball in her wooden shed! Can you get my ball back for me please?")
							.addOptions("Start Witch's House?", new Options() {
								@Override
								public void create() {
									option("Yes.", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "Ok, I'll see what I can do.", () -> {
												p.getQuestManager().setStage(Quest.WITCHS_HOUSE, FIND_BALL);
											})
											.addNPC(BOY, HeadE.CHILD_CALM_TALK, "Thanks mister!")
											);
									option("No.", new Dialogue());
								}
							})
							);
					option("Well if you're not going to answer, I'll go.", new Dialogue()
							.addPlayer(HeadE.FRUSTRATED, "Well if you're not going to answer, I'll go")
							.addSimple("*The boy sniffs slightly.")
							);
				}
			});

		}
		case FIND_BALL -> {
			if(p.getInventory().containsItem(BALL, 1)) {
				addPlayer(HeadE.HAPPY_TALKING, "Hi, I have got your ball back. It was MUCH harder than I thought it would be.");
				addSimple("You give the ball back.");
				addNPC(BOY, HeadE.CHEERFUL, "Thank you so much!");
				addNext(() -> {
					p.getInventory().removeItems(new Item(BALL, 1));
					p.getQuestManager().completeQuest(Quest.WITCHS_HOUSE);
				});
			} else {
				addNPC(BOY, HeadE.CHILD_HAPPY_TALK, "I can't wait to get my ball...");
				addPlayer(HeadE.CALM, "...");
			}
		}
		case QUEST_COMPLETE -> {
			addNPC(BOY, HeadE.CHILD_SAD, "I kicked my ball into the witch's garden again...");
			addPlayer(HeadE.FRUSTRATED, "I am not getting it for you again...");
		}
		}
	}

	public static NPCClickHandler handleBoyDialogue = new NPCClickHandler(BOY) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new BoyWitchsHouseD(e.getPlayer()).getStart());
		}
	};
}
