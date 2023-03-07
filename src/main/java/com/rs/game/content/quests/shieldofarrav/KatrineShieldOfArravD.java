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

public class KatrineShieldOfArravD extends Conversation {
	private final int KATRINE = 642;

	public KatrineShieldOfArravD(Player player) {
		super(player);
		if(player.getQuestManager().getStage(Quest.SHIELD_OF_ARRAV) < ShieldOfArrav.PROVING_LOYALTY_BLACK_ARM_STAGE) {
			addPlayer(HeadE.TALKING_ALOT, "What is this place?");
			addNPC(KATRINE, HeadE.FRUSTRATED, "It's a private business. Can I help you at all?");
			introductoryConversations(player);
			return;
		}
		if(!ShieldOfArrav.hasGang(player))
			checkAboutCrossbowsConversation(player);
		else if(ShieldOfArrav.isBlackArmGang(player)) {
			addPlayer(HeadE.HAPPY_TALKING, "Hey.");
			addNPC(KATRINE, HeadE.HAPPY_TALKING, "Hey.");
			fellowGangMemberDialogue(player);
		} else if(ShieldOfArrav.isPhoenixGang(player)) {
			addPlayer(HeadE.TALKING_ALOT, "What is this place?");
			addNPC(KATRINE, HeadE.FRUSTRATED, "It's a private business Phoenix scum, please leave.");
		}
	}

	public KatrineShieldOfArravD(Player player, int convoID) {
		super(player);
		switch(convoID) {
		case 0:
			introductoryConversations(player);
			break;
		case 1:
			fellowGangMemberDialogue(player);
			break;
		}

	}

	private void fellowGangMemberDialogue(Player p) {
		addOptions("Who are all those people in there?", new Options() {
			@Override
			public void create() {
				option("Who are all those people in there?", new Dialogue()
						.addNPC(KATRINE, HeadE.HAPPY_TALKING, "They're just various rogues and thieves.")
						.addPlayer(HeadE.TALKING_ALOT, "They don't say a lot...")
						.addNPC(KATRINE, HeadE.CALM_TALK, "Nope.")
						.addNext(() -> {
							p.startConversation(new KatrineShieldOfArravD(p, 1).getStart());
						}));
				option("Teach me to be a top class criminal!", new Dialogue()
						.addNPC(KATRINE, HeadE.HAPPY_TALKING, "Teach yourself")
						.addNext(() -> {
							p.startConversation(new KatrineShieldOfArravD(p, 1).getStart());
						}));
				option("Farewell.");
			}
		});
	}

	private void checkAboutCrossbowsConversation(Player p) {
		addNPC(KATRINE, HeadE.CALM, "Have you got those crossbows for me yet?");
		if(p.getInventory().containsItem(767, 2)) {
			addPlayer(HeadE.HAPPY_TALKING, "Yes I have.");
			addSimple("You give the crossbows to Katrine.", () -> {
				p.getInventory().deleteItem(767, 1);
				p.getInventory().deleteItem(767, 1);
				ShieldOfArrav.setStage(p, ShieldOfArrav.JOINED_BLACK_ARM_STAGE);
				ShieldOfArrav.setGang(p, "Black");
			});
			addNPC(HeadE.HAPPY_TALKING, "You're now a Black Arm Gang member. Feel free to enter any of the rooms of the ganghouse.");
		} else if(p.getInventory().containsItem(767, 1)) {
			addPlayer(HeadE.NERVOUS, "I have one...");
			addNPC(HeadE.HAPPY_TALKING, "I need two. Come back when you have them.");
		} else if(!p.getInventory().containsItem(767)) {
			addPlayer(HeadE.NERVOUS, "No, I haven't yet found them. ");
			addNPC(HeadE.HAPPY_TALKING, "I need two crossbows stolen from the Phoenix Gang weapons stash, which if you head east for a bit, is a building " +
					"on the south side of the road. Come back when you got 'em.");
		}
	}

	private void introductoryConversations(Player p) {
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				if(ShieldOfArrav.isStageInPlayerSave(p, ShieldOfArrav.AFTER_BRIBE_CHARLIE_STAGE))
					option("I've heard you're part of the Black Arm Gang.", new Dialogue()
							.addPlayer(HeadE.TALKING_ALOT, "I've heard you're part of the Black Arm Gang.")
							.addNPC(KATRINE, HeadE.FRUSTRATED, "Who told you that?")
							.addOptions("Select an option:", new Options() {
								@Override
								public void create() {
									option("I'd rather not reveal my sources.", new Dialogue()
											.addPlayer(HeadE.SECRETIVE, "I'd rather not reveal my sources.")
											.addNPC(KATRINE, HeadE.SECRETIVE, "Yes, I can understand that. So what do you want with us?")
											.addOptions("Select an option:", joinGang));
									option("It was Charlie, the tramp outside.", new Dialogue()
											.addPlayer(HeadE.TALKING_ALOT, "It was Charlie, the tramp outside.")
											.addNPC(KATRINE, HeadE.FRUSTRATED, "Is that guy still out there? He's getting to be a nuisance. Remind me to send someone to kill him. " +
													"So now you've found us, what do you want?")
											.addOptions("Choose an option:", joinGang));
									option("Everyone knows - it's no great secret.", new Dialogue()
											.addPlayer(HeadE.TALKING_ALOT, "Everyone knows - it's no great secret.")
											.addNPC(KATRINE, HeadE.AMAZED_MILD, "I thought we were safe back here!")
											.addPlayer(HeadE.TALKING_ALOT, "Oh no, not at all... It's so obvious! Even the town guard have caught on...")
											.addNPC(KATRINE, HeadE.TALKING_ALOT, "Wow! We MUST be obvious! I guess they'll be expecting bribes again soon in that case." +
													" Thanks for the information. Is there anything else you want to tell me?")
											.addOptions("Choose an option:", joinGang));
								}
							}));
				option("What sort of business?", new Dialogue()
						.addPlayer(HeadE.SKEPTICAL_THINKING, "What sort of business?")
						.addNPC(KATRINE, HeadE.SECRETIVE, "A small, family business. We give financial advice to other companies.")
						.addNext(() -> {
							p.startConversation(new KatrineShieldOfArravD(p, 0).getStart());
						}));
				option("I'm looking for fame and riches.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "I'm looking for fame and riches.")
						.addNPC(KATRINE, HeadE.FRUSTRATED, "And you expect to find it up the back streets of Varrock?")
						.addNext(() -> {
							p.startConversation(new KatrineShieldOfArravD(p, 0).getStart());
						}));
				option("Farewell.", new Dialogue().addNext(() -> {}));
			}
		});
	}

	Options joinGang = new Options() {
		@Override
		public void create() {
			option("I want to become a member of your gang.", new Dialogue()
					.addPlayer(HeadE.TALKING_ALOT, "I want to become a member of your gang.")
					.addNPC(KATRINE, HeadE.SKEPTICAL_THINKING, "How unusual. Normally we recruit for our gang by watching local thugs and thieves in action. ")
					.addNPC(KATRINE, HeadE.SKEPTICAL_THINKING, "People don't normally waltz in here saying 'hello, can I play'. How can I be sure you can be trusted?")
					.addOptions("Choose an option:", new Options() {
						@Override
						public void create() {
							option("Well, you can give me a try can't you?", new Dialogue()
									.addPlayer(HeadE.TALKING_ALOT, "Well, you can give me a try can't you?")
									.addNPC(KATRINE, HeadE.TALKING_ALOT, "I'm not so sure. Thinking about it... I may have a solution actually.")
									.addNPC(KATRINE, HeadE.TALKING_ALOT, "Our rival gang - the Phoenix Gang - has a weapons stash a little east of here.")
									.addNPC(KATRINE, HeadE.TALKING_ALOT, "We're fresh out of crossbows, so if you could steal a couple of crossbows for us it would be very much" +
											" appreciated. Then I'll be happy to call you a Black Arm.")
									.addPlayer(HeadE.HAPPY_TALKING, "Sounds simple enough. Any particular reason you need two of them?")
									.addNPC(KATRINE, HeadE.TALKING_ALOT, "I have an idea for framing a local merchant who is refusing to pay our, very reasonable, 'keep-your-life-pleasant'" +
											" insurance rates.")
									.addNPC(KATRINE, HeadE.TALKING_ALOT, "I need two phoenix crossbows: one to kill somebody important with and the other to hide in the merchant's " +
											"house where the local law can find it!")
									.addNPC(KATRINE, HeadE.TALKING_ALOT, "When they find it, they'll suspect him of murdering the target for the Phoenix Gang and, hopefully, " +
											"arrest the whole gang! Leaving us as the only gang of thieves in Varrock! Brilliant, eh?")
									.addPlayer(HeadE.SECRETIVE, "Yeah, brilliant. So who are you planning to murder?")
									.addNPC(KATRINE, HeadE.SKEPTICAL, "I haven't decided yet, but it'll need to be somebody important. Say, why are you being so nosey? " +
											"You aren't with the law, are you?")
									.addPlayer(HeadE.SKEPTICAL_HEAD_SHAKE, "No, no! Just curious.")
									.addNPC(KATRINE, HeadE.FRUSTRATED, "You'd better just keep your mouth shut about this plan, or I'll make sure it stays shut for you. " +
											"Now, are you going to go get those crossbows or not?")
									.addOptions("Select an option:", new Options() {
										@Override
										public void create() {
											option("No problem. I'll get you two phoenix crossbows.", new Dialogue()
													.addPlayer(HeadE.HAPPY_TALKING, "No problem. I'll get you two phoenix crossbows.")
													.addNPC(KATRINE, HeadE.HAPPY_TALKING, "Great! You'll find the Phoenix gang's weapon stash just next to a temple, due east of here.")
													.addPlayer(HeadE.HAPPY_TALKING,"I'll get on it!")
													.addNext(() -> {
														ShieldOfArrav.setStage(player, ShieldOfArrav.PROVING_LOYALTY_BLACK_ARM_STAGE);
													}));
											option("Sounds a little tricky. Got anything easier?", new Dialogue()
													.addPlayer(HeadE.WORRIED, "Sounds a little tricky. Got anything easier?")
													.addNPC(KATRINE, HeadE.LAUGH, "If you're not up to a little bit of danger I don't think you've got anything to offer our gang.")
													.addNext(() -> {
														player.startConversation(new KatrineShieldOfArravD(player, 0).getStart());
													}));
										}
									}));
							option("Well, people tell me I have an honest face.", new Dialogue()
									.addNPC(KATRINE, HeadE.SKEPTICAL_HEAD_SHAKE, "How unusual. Someone honest wanting to join a gang of thieves. Excuse me if I remain " +
											"unconvinced.")
									.addNext(() -> {
										player.startConversation(new KatrineShieldOfArravD(player, 0).getStart());
									}));
						}
					}));
			option("I want some hints for becoming a thief.", new Dialogue()
					.addPlayer(HeadE.SECRETIVE, "I want some hints for becoming a thief.")
					.addNPC(KATRINE, HeadE.WORRIED, "Well, I'm sorry luv, I'm not giving away any of my secrets. Not to people who ain't Black Arm members anyway.")
					.addNext(() -> {
						player.startConversation(new KatrineShieldOfArravD(player, 0).getStart());
					}));
			option("I'm looking for the door out of here.", new Dialogue()
					.addPlayer(HeadE.SCARED, "I'm looking for the door out of here.")
					.addSimple("*Katrine groans")
					.addNPC(KATRINE, HeadE.VERY_FRUSTRATED, "Try... the one you just came in?")
					.addNext(() -> {
						player.startConversation(new KatrineShieldOfArravD(player, 0).getStart());
					}));
		}
	};
}
