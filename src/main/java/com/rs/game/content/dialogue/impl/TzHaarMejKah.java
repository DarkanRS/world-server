package com.rs.game.content.dialogue.impl;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.minigames.fightpits.FightPits;
import com.rs.game.model.entity.player.Player;

public class TzHaarMejKah extends Conversation {
	public TzHaarMejKah(Player p, int npcId) {
		super(p);
		addNPC(npcId, HeadE.SKEPTICAL_HEAD_SHAKE, "You want help JalYt-Ket-" + player.getDisplayName() + "?");
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("What is this place?", new Dialogue()
						.addPlayer(HeadE.SKEPTICAL_HEAD_SHAKE, "What is this place?")
						.addNPC(npcId, HeadE.CALM_TALK, "This is the Fight Pit. TzHaar-Xil made it for their sport but many JalYt come here to fight, too.<br>" +
								"If you are wanting to fight then enter the cage, you will be summoned when next round is ready to begin.")
						.addOptions("Choose an option:", new Options() {
								@Override
								public void create() {
									option("Are there any rules?", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "Are there any rules?")
											.addNPC(npcId, HeadE.CALM_TALK, "No rules, you use whatever you want. Last person standing wins and is declared champion, they stay in the pit for next fight.")
											.addOptions("Choose an option:", new Options() {
												@Override
												public void create() {
													option("Do I win anything?", new Dialogue()
															.addPlayer(HeadE.HAPPY_TALKING, "Do I win anything?")
															.addNPC(npcId, HeadE.CALM_TALK, "You ask a lot questions.<br>Champion gets TokKul as reward, more fights the more TokKul they get.")
															.addPlayer(HeadE.HAPPY_TALKING, "...")
															.addNPC(npcId, HeadE.CALM_TALK, "Before you ask, TokKul is like your coins.")
															.addNPC(npcId, HeadE.CALM_TALK, "Gold is like you JalYt, soft and easily broken, we use hard rock forged in fire like TzHaar!")
													);
													option("Sounds good.", new Dialogue());
												}
											})
									);
									option("Ok thanks.", new Dialogue());
								}
							})
				);
				option("Who's the current champion?", new Dialogue()
						.addPlayer(HeadE.SKEPTICAL_HEAD_SHAKE, "Who's the current champion?")
						.addNPC(npcId, HeadE.CALM_TALK, "Ah that would be Y'Haar-Mej-" + (FightPits.currentChampion == null ? "none" : FightPits.currentChampion) + "!")
				);
				option("What did you call me?", new Dialogue()
						.addPlayer(HeadE.SKEPTICAL_HEAD_SHAKE, "What did you call me?")
						.addNPC(npcId, HeadE.CALM_TALK, "Are you not a JalYt-Ket?")
						.addOptions("Choose an option:", new Options() {
							@Override
							public void create() {
								option("What's a 'JalYt-Ket'?", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "What's a 'JalYt-Ket'?")
										.addNPC(npcId, HeadE.CALM_TALK, "That what you are...you tough and strong, no?")
										.addNPC(npcId, HeadE.CALM_TALK, "Well, yes I suppose I am...")
										.addNPC(npcId, HeadE.CALM_TALK, "Then you JalYt-Ket!")
										.addOptions("Choose an option:", new Options() {
											@Override
											public void create() {
												option("What are you then?", new Dialogue()
														.addPlayer(HeadE.HAPPY_TALKING, "What are you then?")
														.addNPC(npcId, HeadE.CALM_TALK, "Foolish JalYt, I am TzHaar-Mej one of the mystics of this city.")
														.addOptions("Choose an option:", new Options() {
															@Override
															public void create() {
																option("What other types are there?", new Dialogue()
																		.addPlayer(HeadE.HAPPY_TALKING, "What other types are there?")
																		.addNPC(npcId, HeadE.CALM_TALK, "There are the mighty TzHaar-Key who guard us, the swift TzHaar-Xil who hunt for our food, and the skilled TzHaar-Hur who craft our homes and tools.")
																		.addPlayer(HeadE.HAPPY_TALKING, "Awesome...")
																);
																option("Ah ok then.", new Dialogue()
																		.addPlayer(HeadE.HAPPY_TALKING, "Ah ok then.")
																		.addNPC(npcId, HeadE.CALM_TALK, "...")
																);
															}
														})
												);
												option("Thanks for explaining it.", new Dialogue()
														.addPlayer(HeadE.HAPPY_TALKING, "Thanks for explaining it.")
														.addNPC(npcId, HeadE.CALM_TALK, "...")
												);
											}
										})
								);
								option("I guess so...?", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "I guess so...?")
										.addNPC(npcId, HeadE.CALM_TALK, "...")
								);
								option("No I'm not!", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "No I'm not!")
										.addNPC(npcId, HeadE.CALM_TALK, "...")
								);
							}
						})
				);
				option("No I'm fine thanks.", new Dialogue());
				
				create();
			}
		});
	}
}
