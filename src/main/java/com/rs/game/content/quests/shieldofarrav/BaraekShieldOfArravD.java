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
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;

public class BaraekShieldOfArravD extends Conversation {
	private final int BARAEK = 547;

	public BaraekShieldOfArravD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.SHIELD_OF_ARRAV)) {
		case ShieldOfArrav.TALK_TO_BARAEK_STAGE:
			addOptions("Choose an option: ", new Options() {
				@Override
				public void create() {
					option("Can you tell me where I can find the Phoenix Gang?", new Dialogue()
							.addPlayer(HeadE.SECRETIVE, "Can you tell me where I can find the Phoenix Gang?")
							.addNPC(BARAEK, HeadE.FRUSTRATED, "Sh sh sh, not so loud! You don't want to get me in trouble!")
							.addPlayer(HeadE.SKEPTICAL_THINKING, "So DO you know where they are?")
							.addNPC(BARAEK, HeadE.CALM_TALK,"I may. But I don't want to get into trouble for revealing their hideout.")
							.addNPC(BARAEK, HeadE.TALKING_ALOT, " Of course, if I was, say 10 gold coins richer I may happen to be more inclined to take that " +
									"sort of risk...")
							.addOptions("Choose an option: ", new Options() {
								@Override
								public void create() {
									if(p.getInventory().hasCoins(10))
										option("Alright. Have 10 gold coins.", new Dialogue()
												.addSimple("You give him 10 coins", () -> {
													if (p.getQuestManager().getStage(Quest.SHIELD_OF_ARRAV) == ShieldOfArrav.TALK_TO_BARAEK_STAGE) {
														p.getInventory().removeCoins(10);
														ShieldOfArrav.setStage(p, ShieldOfArrav.AFTER_BRIBE_BARAEK_STAGE);
													}
												}).addNPC(BARAEK, HeadE.SECRETIVE, "To get to the gang hideout, enter Varrock through the south gate. Then, if you take the first turn east, " +
														"somewhere along there is an alleyway to the south.")
												.addNPC(BARAEK, HeadE.SECRETIVE, " The door at the end of there is the entrance to the Phoenix Gang. They're operating " +
														"there under the name of the VTAM Corporation.")
												.addNPC(BARAEK, HeadE.SECRETIVE, "The Phoenixes ain't the types to be messed about.")
												.addOptions("Select an option", new Options() {
													@Override
													public void create() {
														option("You're really bad at giving directions.", new Dialogue()
																.addPlayer(HeadE.WORRIED, "Seriously...I paid 10 gold coins for that? I hope you're a better fur trader than a navigator.")
																.addNPC(BARAEK, HeadE.CALM_TALK, "Hey now...the Phoenix Gang make it their business to not be easy to find.")
																.addNPC(BARAEK, HeadE.CALM_TALK, " Look, if you get lost, talk to Charlie the tramp near the Varrock south gate. " +
																		"He can give you better directions.")
																.addNext(() -> {
																	p.startConversation(new BaraekShieldOfArravD(p).getStart());
																}));
														option("Thanks!", () -> {});
													}
												}));
									else
										option("I haven't got that much.", new Dialogue()
												.addPlayer(HeadE.SAD_MILD_LOOK_DOWN, "I haven't got that much")
												.addNPC(BARAEK, HeadE.ANGRY, "In that case I wouldn't dare tell you anything about the Phoenix Gang.")
												.addNext(() -> {
													p.startConversation(new BaraekShieldOfArravD(p).getStart());
												}));
									option("Yes. I'd like to be 10 gold coins richer.", new Dialogue()
											.addNPC(BARAEK, HeadE.FRUSTRATED, "What? You're meant to bribe me, not the other way around... Oh, forget it!")
											.addNext(() -> {
												p.startConversation(new BaraekShieldOfArravD(p).getStart());
											}));
									option("No, I don't like things like bribery.", new Dialogue()
											.addPlayer(HeadE.SKEPTICAL, "No, I don't like things like bribery.")
											.addNPC(BARAEK, HeadE.LAUGH, "Heh. And you want to deal with the Phoenix Gang? They're involved in much worse than a " +
													"bit of bribery.")
											.addNext(() -> {
												p.startConversation(new BaraekShieldOfArravD(p).getStart());
											}));
									option("Farewell.");
								}
							}));
					option("Farewell");
				}
			});
			break;
		case ShieldOfArrav.AFTER_BRIBE_BARAEK_STAGE:
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("Remind me where I can find the Phoenix Gang.", new Dialogue()
							.addNPC(BARAEK, HeadE.SECRETIVE, "To get to the gang hideout, enter Varrock through the south gate. Then, if you take the first " +
									"turn east, somewhere along there is an alleyway to the south.")
							.addNPC(BARAEK, HeadE.SECRETIVE, " The door at the end of there is the entrance to the Phoenix Gang. They're operating " +
									"there under the name of the VTAM Corporation.")
							.addNPC(BARAEK, HeadE.SECRETIVE, "The Phoenixes ain't the types to be messed about.")
							.addOptions("Select an option", new Options() {
								@Override
								public void create() {
									option("You're really bad at giving directions.", new Dialogue()
											.addPlayer(HeadE.WORRIED, "Seriously...I paid 10 gold coins for that? I hope you're a better fur trader than a navigator.")
											.addNPC(BARAEK, HeadE.CALM_TALK, "Hey now...the Phoenix Gang make it their business to not be easy to find.")
											.addNPC(BARAEK, HeadE.CALM_TALK, " Look, if you get lost, talk to Charlie the tramp near the Varrock south gate. " +
													"He can give you better directions.")
											.addNext(() -> {
												p.startConversation(new BaraekShieldOfArravD(p).getStart());
											}));
									option("Thanks!", () -> {});
								}
							}));
					option("Farewell.");
				}
			});
			break;
		default:
			addNext(() -> {
				p.sendMessage("Nothing interesting happens.");
			});

			break;
		}
	}


}
