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
package com.rs.game.player.quests.handlers.ernestthechicken;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.quests.Quest;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class VeronicaErnestChickenD extends Conversation {
	private static final int VERONICA = 285;

	public VeronicaErnestChickenD(Player p) {
		super(p);
		switch (p.getQuestManager().getStage(Quest.ERNEST_CHICKEN)) {
		case ErnestTheChicken.NOT_STARTED:
			addNPC(VERONICA, HeadE.NERVOUS, "Can you please help me? I'm in a terrible spot of trouble. My fiance, Ernest, and I came upon this house. ");
			addNPC(VERONICA, HeadE.UPSET, "Seeing as we were a little lost, Ernest decided to go in and ask for directions. That was an hour ago..." +
					" and that house looks spooky.");
			addNPC(VERONICA, HeadE.UPSET_SNIFFLE, "Can you go and see if you can find him for me?");
			addOptions("Start Ernest The Chicken?", new Options() {
				@Override
				public void create() {
					option("Accept Quest", new Dialogue()
							.addNPC(VERONICA, HeadE.HAPPY_TALKING, "Thank you, thank you. I'm very grateful.", () -> {
								p.getQuestManager().setStage(Quest.ERNEST_CHICKEN, ErnestTheChicken.STARTED);
							})
							.addNPC(VERONICA, HeadE.TALKING_ALOT, "I think I spotted some lights flashing in one of the top floor windows, so you may want to " +
									"head up there first."));
					option("Not right now", new Dialogue()
							.addNPC(VERONICA, HeadE.SAD_MILD, "Oh. I'm so worried. I hope someone will help me soon."));
				}
			});
			break;
		case ErnestTheChicken.STARTED:
			addNPC(VERONICA, HeadE.WORRIED, "Have you found my sweetheart yet?");
			addPlayer(HeadE.CALM_TALK, "No, not yet.");
			break;
		case ErnestTheChicken.KNOWS_ABOUT_CHICKEN:
		case ErnestTheChicken.NEEDS_PARTS:
			addNPC(VERONICA, HeadE.WORRIED, "Have you found my sweetheart yet?");
			addPlayer(HeadE.CALM_TALK, "Yes, he's a chicken.");
			addNPC(VERONICA, HeadE.UPSET_SNIFFLE, "I know he's not exactly brave but I think you're being a bit harsh.");
			addPlayer(HeadE.CALM_TALK, "No no, he's been turned into an actual chicken by a mad scientist.");
			addNPC(VERONICA, HeadE.AMAZED, "Eeeeeek!");
			addNPC(VERONICA, HeadE.AMAZED_MILD, "My poor darling, why must these things happen to us.");
			addPlayer(HeadE.CALM_TALK, "Well I'm doing my best to turn him back.");
			addNPC(VERONICA, HeadE.CALM_TALK, "Well be quick, I'm sure being a chicken can't be good for him.");
			break;

		case ErnestTheChicken.QUEST_COMPLETE:
			addNPC(VERONICA, HeadE.HAPPY_TALKING, "Thank you for rescuing Ernest.");
			addPlayer(HeadE.HAPPY_TALKING, "Where is he now?");
			addNPC(VERONICA, HeadE.HAPPY_TALKING, "Oh he went off to talk to some green warty guy. I'm sure he'll be back soon.");
			break;
		}
	}

	public VeronicaErnestChickenD(Player p, int convoID) {
		super(p);
		switch(convoID) {
		case 0:
			convo1(p);
			break;
		case 1:
			convo2(p);
			break;
		case 2:
			convo3(p);
			break;

		}

	}

	private void convo1(Player p) {

	}

	private void convo2(Player p) {

	}

	private void convo3(Player p) {

	}


	public static NPCClickHandler handleVeronica = new NPCClickHandler(VERONICA) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new VeronicaErnestChickenD(e.getPlayer()).getStart());
		}
	};
}
