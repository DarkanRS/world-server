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
package com.rs.game.content.quests.priestinperil;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class KingRoaldPriestInPerilD extends Conversation {
	final int KING_ROALD = 648;

	public KingRoaldPriestInPerilD(Player player) {
		super(player);
		if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 0) {
			player.startConversation(new Dialogue()
					.addPlayer(HeadE.CONFUSED, "Greetings, your majesty.")
					.addNPC(KING_ROALD, HeadE.CONFUSED, "Well hello there. What do you want with the king of Misthalin?")
					.addOptions(ops -> {
						ops.add("I'm looking for a quest!")
								.addPlayer(HeadE.CALM_TALK, "I'm looking for a quest!")
								.addNPC(KING_ROALD, HeadE.CALM_TALK, "A quest you say? Hmm, what an odd request to make of the king. It's funny you should mention it though, as there is something you can do for me.")
								.addNPC(KING_ROALD, HeadE.CALM_TALK, "Are you aware of Paterdomus? It's a temple east of here. It stands on the River Salve and guards the only passage into the deadly lands of Morytania.")
								.addPlayer(HeadE.CALM_TALK, "No, I don't think I know it...")
								.addNPC(KING_ROALD, HeadE.CALM_TALK, "Hmm, how strange that you don't. Well anyway, it has been some days since last I heard from Drezel, the priest who lives there.")
								.addNPC(KING_ROALD, HeadE.CALM_TALK, "Be a sport and go make sure that nothing untoward has happened to the silly old codger for me, would you?")
								.addNext(() -> {
									player.startConversation(new Dialogue()
											.addQuestStart(Quest.PRIEST_IN_PERIL)
											.addPlayer(HeadE.CALM_TALK, "Sure. I don't have anything better to do right now.", () -> player.getQuestManager().setStage(Quest.PRIEST_IN_PERIL, 1))
											.addNPC(KING_ROALD, HeadE.CALM_TALK, "Many thanks adventurer! I would have sent one of my squires but they wanted payment for it!")
									);
								});
						ops.add("Nothing.")
								.addPlayer(HeadE.CALM_TALK, "Nothing");
					}));
		}
		if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 1) {
			player.startConversation(new Dialogue()
					.addPlayer(HeadE.CALM_TALK, "Greetings, your majesty.")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "You have news of Drezel for me?")
					.addOptions(ops4 -> {
						ops4.add("Where am I supposed to go again?")
								.addPlayer(HeadE.CALM_TALK, "Where am I supposed to go again?")
								.addNPC(KING_ROALD, HeadE.CALM_TALK, "The temple of Paterdomus where Drezel lives. It is but a short journey east from here. It lies south of the cliffs, at the source of the River Salve.")
								.addNPC(KING_ROALD, HeadE.CALM_TALK, "Don't worry, you can't miss it.");
						ops4.add("Do I get a reward for this?")
								.addPlayer(HeadE.CALM_TALK, "Do I get a reward for this?")
								.addNPC(KING_ROALD, HeadE.CALM_TALK, "You will be rewarded in the knowledge that you have done the right thing and assisted the king of Misthalin.")
								.addPlayer(HeadE.CALM_TALK, "Soooooo...... that would be a 'no' then?")
								.addNPC(KING_ROALD, HeadE.CALM_TALK, "That is correct.");
						ops4.add("Who's Drezel?")
								.addPlayer(HeadE.CALM_TALK, "Who's Drezel?")
								.addNPC(KING_ROALD, HeadE.CALM_TALK, "Drezel is the priest who lives at Paterdomus, the temple to the east. You're supposed to be making sure that nothing bad has happened to him. Remember?")
								.addPlayer(HeadE.CALM_TALK, "Ooooooooh, THAT Drezel. Yup, I remember.");
						ops4.add("Why do you care about Drezel anyway?")
								.addPlayer(HeadE.CALM_TALK, "Why do you care about Drezel anyway?")
								.addNPC(KING_ROALD, HeadE.CALM_TALK, "Well, that is a slightly impertinent question to ask of your king, but I shall overlook it this time.")
								.addNPC(KING_ROALD, HeadE.CALM_TALK, "As you are no doubt aware, this kingdom worships Saradomin, the god of wisdom and order. As such, it is a peaceful place to live and prosper.")
								.addNPC(KING_ROALD, HeadE.CALM_TALK, "Paterdomus, the temple where Drezel lives, stands on the eastern border of Misthalin. It guards the only passage into the evil lands of Morytania.")
								.addPlayer(HeadE.CALM_TALK, "Evil?")
								.addNPC(KING_ROALD, HeadE.CALM_TALK, "Oh yes. Morytania is a fearful place, filled to the brim with accursed servants of the chaos god Zamorak. All of them are terrible, but none more so than the rulers of the region, the vampyres.")
								.addNPC(KING_ROALD, HeadE.CALM_TALK, "Thankfully, the sacred River Salve marks a natural border between Misthalin and Morytania. Together, the river and the temple prevent any invasion by the vampyres.")
								.addNPC(KING_ROALD, HeadE.CALM_TALK, "The waters of the river are kept blessed with Saradomin's almighty power. This ensures our defences remain strong, as the vampyres cannot cross such a holy barrier.")
								.addNPC(KING_ROALD, HeadE.CALM_TALK, "Drezel is descended from one of the original Saradominist priests who first blessed the river. His job is to ensure nothing happens to the river that might allow the evil of Morytania to invade this land.")
								.addNPC(KING_ROALD, HeadE.CALM_TALK, "This is the reason why the lack of communication from him bothers me somewhat, although I am sure nobody would dare to try and attack our kingdom!");

						ops4.add("I'll get going.")
								.addPlayer(HeadE.HAPPY_TALKING, "I'll get going.");
					})
			);
		}
		if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 2) {
			player.startConversation(new Dialogue()
					.addPlayer(HeadE.CALM_TALK, "Greetings, your majesty.")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "You have news of Drezel for me?")
					.addPlayer(HeadE.CALM_TALK, "Well... I went to the temple like you asked me to... and I spoke to someone inside...")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "Ah, well that must have been Drezel then. What did he say?")
					.addPlayer(HeadE.CALM_TALK, "Well... he seemed to be having some kind of trouble. He asked for my help.")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "Well I expect you to offer him your full assistance in whatever he needs.")
					.addPlayer(HeadE.CALM_TALK, "Well... okay then.")
			);
		}
		if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 3) {
			player.startConversation(new Dialogue()
					.addPlayer(HeadE.CALM_TALK, "Greetings, your majesty.")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "You have news of Drezel for me?")
					.addPlayer(HeadE.CALM_TALK, "Yes, I spoke to the guys at the temple. They said they were being bothered by a dog in the mausoleum, so I went and killed it for them. No problem.")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "YOU DID WHAT???")
					.addPlayer(HeadE.CALM_TALK, "Uh oh...")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "Are you a complete imbecile?")
					.addPlayer(HeadE.CALM_TALK, "Maybe?")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "That mausoleum contain the only passage between Morytania and Misthalin! Not only that, it's built right over the source of the River Salve!")
					.addPlayer(HeadE.CALM_TALK, "Did I make a mistake?")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "YES YOU DID!!!!! Without that 'dog', there's nothing to stop someone from entering the mausoleum to sabotage the blessings on the river. Thanks to you, all of Misthalin is now at risk!")
					.addPlayer(HeadE.CALM_TALK, "B-but... Drezel told me to...")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "No, you absolute cretin! Obviously some fiend has done something to Drezel and tricked your feeble intellect into helping them kill that guard dog!")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "Now you get back there and do whatever is necessary to safeguard the kingdom from attack, or I will see you beheaded for high treason!")
					.addPlayer(HeadE.CALM_TALK, "Y-yes your highness.", () -> player.getQuestManager().setStage(Quest.PRIEST_IN_PERIL, 4))
			);
		}
		if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 4) {
			player.startConversation(new Dialogue()
					.addPlayer(HeadE.CALM_TALK, "Greetings, your majesty.")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "YOU! Why haven't you ensured the border with Morytania is secure yet?")
					.addPlayer(HeadE.CALM_TALK, "Okay, okay... I'm going, I'm going... There's no need to shout...")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "NO NEED TO SHOUT???")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "Listen, and listen well, and see if your puny mind can comprehend this: if the border is not protected, then we are all at the mercy of the vampyres!")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "I would say that me shouting at you for your incompetence is the LEAST of your worries right now. NOW GO!")
			);
		}
		if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 5) {
			player.startConversation(new Dialogue()
					.addPlayer(HeadE.CALM_TALK, "Greetings, your majesty.")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "You have news of Drezel for me?")
					.addPlayer(HeadE.CALM_TALK, "I do indeed, sire. He has been imprisoned by some Zamorakian monks.")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "What? This is wholly unacceptable! I order you to do all that you can to free Drezel immediately!")
					.addPlayer(HeadE.CALM_TALK, "I was doing that anyway.")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "Ah, I see. In that case keep up the good work.")
			);
		}
		if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 6) {
			player.startConversation(new Dialogue()
					.addPlayer(HeadE.CALM_TALK, "Greetings, your majesty.")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "Have you freed Drezel yet?")
					.addPlayer(HeadE.CALM_TALK, "Well, I found the key to his cell and unlocked it, but there's a vampyre in there stopping him leaving.")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "A vampyre? Well, I order you to do something about it at once!")
					.addPlayer(HeadE.CALM_TALK, "Yeah, I was planning on doing that anyway.")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "Good work! Always a place for quick thinkers in my kingdom!")
			);
		}
		if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 7) {
			player.startConversation(new Dialogue()
					.addPlayer(HeadE.CALM_TALK, "Greetings, your majesty.")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "Have you freed Drezel yet?")
					.addPlayer(HeadE.CALM_TALK, "Yes sire.")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "Aha! Good work adventurer! What news of the border defences against Morytania?")
					.addPlayer(HeadE.CALM_TALK, "I, uh, don't know about that...")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "Well get back and find out at once! This is a matter of national security and if I find out we are vulnerable I will hold you personally responsible!")
					.addPlayer(HeadE.CALM_TALK, "Yes sire.")
			);
		}
		if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 8) {
			player.startConversation(new Dialogue()
					.addPlayer(HeadE.CALM_TALK, "Greetings, your majesty.")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "Have you ensured Misthalin's border is fully secured?")
					.addPlayer(HeadE.CALM_TALK, "Not yet. I'm working on it though.")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "Good, good.")
			);
		}
		if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) >= 9) {
			player.startConversation(new Dialogue()
					.addPlayer(HeadE.CALM_TALK, "Greetings, your majesty.")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "Have you ensured Misthalin's border is fully secured?")
					.addPlayer(HeadE.CALM_TALK, "Not yet. I'm working on it though.")
					.addNPC(KING_ROALD, HeadE.CALM_TALK, "Good, good.")
			);
		}
	}
}
