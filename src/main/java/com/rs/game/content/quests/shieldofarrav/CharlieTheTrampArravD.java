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
package com.rs.game.content.quests.shieldofarrav;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;

public class CharlieTheTrampArravD extends Conversation {
	private int CHARLIE = 641;

	public CharlieTheTrampArravD(Player p) {
		super(p);
		addNPC(CHARLIE, HeadE.HAPPY_TALKING, "Spare some change, governer?");
		if(p.getInventory().hasCoins(10))
			conversationOptions(p);
		else {
			addPlayer(HeadE.FRUSTRATED, "I don't have gold");
			addNPC(HeadE.SKEPTICAL_HEAD_SHAKE,"That's too bad...");
		}
	}

	public CharlieTheTrampArravD(Player p, boolean restartingConversation) {
		super(p);
		conversationOptions(p);
	}

	private void conversationOptions(Player p) {
		Options bribe = new Options() {
			@Override
			public void create() {
				option("That sounds fair. (Pay 10 gold.)", new Dialogue()
						.addNPC(CHARLIE, HeadE.SECRETIVE, "The ruthless and notorious Black Arm Gang have their headquarters down the alleyway to the" +
								" west of us.")
						.addNPC(CHARLIE, HeadE.SECRETIVE, "Talk to a lady called Katrine. But don't upset her, and tell her I sent you. She's " +
								"pretty dangerous.")
						.addNext(() -> {
							p.getInventory().removeCoins(10);
							ShieldOfArrav.setStage(p, ShieldOfArrav.AFTER_BRIBE_CHARLIE_STAGE);
						}));
				option("10 gold? That's too much. (Pay 5 gold.)", new Dialogue()
						.addNPC(CHARLIE, HeadE.HAPPY_TALKING, "I guess that'll have to do, then.")
						.addSimple("You give him 5 gold", () -> {
							p.getInventory().removeCoins(5);
						})
						.addNPC(CHARLIE, HeadE.HAPPY_TALKING, "Great, thanks.")
						.addPlayer(HeadE.SECRETIVE, "So where is the hideout, then?")
						.addNPC(CHARLIE, HeadE.LAUGH, "If you pay me the other 5 gold, I'll tell you.")
						.addOptions("Select an option: ", new Options() {
							@Override
							public void create() {
								option("You thieving gutter-scum! You'd better watch your back!", new Dialogue()
										.addPlayer(HeadE.ANGRY, "You thieving gutter-scum! You'd better watch your back!")
										.addNPC(CHARLIE, HeadE.LAUGH, "Yeah, sure, whatever. You don't frighten me, pal.")
										.addNext(() -> {
											if(p.getInventory().hasCoins(10))
												p.startConversation(new CharlieTheTrampArravD(p, true).getStart());
										}));
								option("I guess I don't have a choice. (Pay 5 gold.)", new Dialogue()
										.addPlayer(HeadE.VERY_FRUSTRATED, "I guess I don't have a choice.")
										.addSimple("You pay Charlie 5 gold", () -> {
											p.getInventory().removeCoins(5);
											ShieldOfArrav.setStage(p, ShieldOfArrav.AFTER_BRIBE_CHARLIE_STAGE);
										})
										.addNPC(CHARLIE, HeadE.SECRETIVE, "The ruthless and notorious Black Arm Gang have their headquarters down the alleyway " +
												"to the west of us.")
										.addNPC(CHARLIE, HeadE.SECRETIVE, "Talk to a lady called Katrine. But don't upset her, and tell her I sent you. She's " +
												"pretty dangerous."));
								option("Never mind. I'll find it myself.", new Dialogue()
										.addNPC(CHARLIE, HeadE.HAPPY_TALKING, "Well, you know where to find me if you get lost!"));
							}
						}));
			}
		};

		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				if(ShieldOfArrav.isStageInPlayerSave(p, ShieldOfArrav.AFTER_BRIBE_CHARLIE_STAGE)) {
					option("What's in it for me?", new Dialogue()
							.addPlayer(HeadE.TALKING_ALOT, "What's in it for me?")
							.addNPC(CHARLIE, HeadE.TALKING_ALOT, "'What's in it for me?' Whatever happened to charity? Have you no consideration for those " +
									"less fortunate than yourself?")
							.addNext(() -> {
								p.startConversation(new CharlieTheTrampArravD(p, true).getStart());
							}));
					option("Do you know where I can find the Black Arm Gang hideout?", new Dialogue()
							.addPlayer(HeadE.TALKING_ALOT, "Do you know where I can find the Black Arm Gang hideout?")
							.addNPC(CHARLIE, HeadE.TALKING_ALOT, "Short memory, eh?")
							.addNPC(CHARLIE, HeadE.SECRETIVE, "The ruthless and notorious Black Arm Gang have their headquarters down the alleyway to the" +
									" west of us.")
							.addNPC(CHARLIE, HeadE.SECRETIVE, "Talk to a lady called Katrine. But don't upset her, and tell her I sent you. She's " +
									"pretty dangerous."));
				} else if(ShieldOfArrav.isStageInPlayerSave(p, ShieldOfArrav.AFTER_BRIBE_BARAEK_STAGE)) {
					option("What's in it for me?", new Dialogue()
							.addPlayer(HeadE.TALKING_ALOT, "What's in it for me?")
							.addNPC(CHARLIE, HeadE.TALKING_ALOT, "I know you're looking for the Black Arm Gang.")
							.addPlayer(HeadE.SCARED, "How do you know that?")
							.addNPC(CHARLIE, HeadE.SECRETIVE, "In my current profession, I spend a lot of time on the streets. I hear things, and for a mere 10 gold, " +
									"I'll tell you how to join the Black Arm Gang.")
							.addOptions("Select an option:", bribe));
					option("Do you know where I can find the Black Arm Gang hideout?", new Dialogue()
							.addPlayer(HeadE.TALKING_ALOT, "Do you know where I can find the Black Arm Gang hideout?")
							.addNPC(CHARLIE, HeadE.TALKING_ALOT, "Sure, I know everything there is to know about this city. I'd be happy to tell you for 10 gold.")
							.addOptions("Select an option:", bribe));
					option("I'm looking for the Phoenix Gang hideout.", new Dialogue()
							.addNPC(CHARLIE, HeadE.CALM_TALK, "Yeah, I heard you talked to Baraek. It's easy to get lost on the streets of Varrock. Now listen closely... " +
									"To get to the Phoenix hideout, walk east from here. ")
							.addNPC(CHARLIE, HeadE.CALM_TALK, "It's the street with the Blue Moon Inn on the corner. Take the first alley leading south off of the road " +
									"and you'll find the hideout at the end of that alley. It's called the VTAM corporation.")
							.addPlayer(HeadE.CALM_TALK, "VTAM. Got it."));
				}
				option("Is there anything down this alleyway?", new Dialogue()
						.addPlayer(HeadE.SKEPTICAL_THINKING, "Is there anything down this alleyway?")
						.addNPC(CHARLIE, HeadE.TALKING_ALOT, "Funny you should mention that...there is actually. The ruthless and notorious criminal gang known " +
								"as the Black Arm Gang have their headquarters down there.")
						.addPlayer(HeadE.HAPPY_TALKING, "Thanks for the warning!"));
			}
		});
	}

}
