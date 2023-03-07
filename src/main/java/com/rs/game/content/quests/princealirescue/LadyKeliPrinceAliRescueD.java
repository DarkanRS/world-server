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
package com.rs.game.content.quests.princealirescue;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class LadyKeliPrinceAliRescueD extends Conversation {
	private final static int LADY_KELI = 919;
	private final int ORIGINAL_OPTIONS = 0;
	private final int LATEST_PLAN_OPTIONS = 1;

	public LadyKeliPrinceAliRescueD(Player player) {
		super(player);
		addPlayer(HeadE.HAPPY_TALKING, "Are you the famous Lady Keli?");
		addPlayer(HeadE.HAPPY_TALKING, "Leader of the toughest gang of mercenary killers around?");
		addNPC(LADY_KELI, HeadE.HAPPY_TALKING, "I am Keli, you have heard of me then");
		addNext(()->{
			player.startConversation(new LadyKeliPrinceAliRescueD(player, ORIGINAL_OPTIONS, true));});


	}

	public LadyKeliPrinceAliRescueD(Player player, int convoID, boolean isFirst) {
		super(player);
		switch(convoID) {
			case ORIGINAL_OPTIONS:
				originalOptions(isFirst);
				break;
			case LATEST_PLAN_OPTIONS:
				latestPlanOptions(isFirst);
				break;
		}
	}

	private void originalOptions(boolean isFirst) {
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				if(isFirst)
					option("Heard of you? You are famous in Gielinor!", new Dialogue()
							.addPlayer(HeadE.AMAZED_MILD, "Heard of you? You are famous in Gielinor!")
							.addNPC(LADY_KELI, HeadE.HAPPY_TALKING, "That's very kind of you to say. Reputations are not easily earned. I have managed to succeed" +
									" where many fail.")
							.addNext(()->{player.startConversation(new LadyKeliPrinceAliRescueD(player, ORIGINAL_OPTIONS, false));}));

				option("I have heard a little, but I think Katrine is tougher.", new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "I think Katrine is still tougher.")
						.addNPC(LADY_KELI, HeadE.FRUSTRATED, "Well you can think that all you like. I know those blackarm cowards dare not leave the city. " +
								"Out here, I am toughest. You can tell them that! Now get out of my sight, before I call my guards."));

				if(!isFirst) {
					option("What is your latest plan then?", new Dialogue()
							.addPlayer(HeadE.SECRETIVE, "What is your latest plan then?")
							.addPlayer(HeadE.SKEPTICAL_THINKING, "Of course you need not go into specific details")
							.addNPC(LADY_KELI, HeadE.TALKING_ALOT, "Well, I can tell you, I have a valuable prisoner here in my cells")
							.addNPC(LADY_KELI, HeadE.TALKING_ALOT, "I can expect a high reward to be paid very soon for this guy")
							.addNPC(LADY_KELI, HeadE.SECRETIVE, "I can't tell you who he is, but he is a lot colder now")
							.addNext(()->{player.startConversation(new LadyKeliPrinceAliRescueD(player, LATEST_PLAN_OPTIONS, true));}));
					option("You must have trained a lot for this work", new Dialogue()
							.addPlayer(HeadE.AMAZED_MILD, "You must have trained a lot for this work")
							.addNPC(LADY_KELI, HeadE.TALKING_ALOT, "I have used a sword since I was a small girl")
							.addNPC(LADY_KELI, HeadE.EVIL_LAUGH, "stabbed three people before I was 6 years old"));
				}
				if(isFirst)
					option("I have heard rumours that you kill people.", new Dialogue()
							.addPlayer(HeadE.SECRETIVE, "I have heard rumours that you kill people.")
							.addNPC(LADY_KELI, HeadE.CALM_TALK, "There's always someone ready to spread rumours. I hear all sort of ridiculous things these days.")
							.addNPC(LADY_KELI, HeadE.FRUSTRATED, "I heard a rumour the other day, that some men are wearing skirts")
							.addNPC(LADY_KELI, HeadE.VERY_FRUSTRATED, "If one of my men wore a skirt, he would wish he hadn't")
							.addNext(()->{player.startConversation(new LadyKeliPrinceAliRescueD(player, ORIGINAL_OPTIONS, false));}));
				if(isFirst)
					option("No I have never really heard of you.", new Dialogue()
							.addPlayer(HeadE.CALM_TALK, "No I have never really heard of you.")
							.addNPC(LADY_KELI, HeadE.FRUSTRATED, "You must be new to this land then")
							.addNPC(LADY_KELI, HeadE.FRUSTRATED, "EVERYONE knows of Lady Keli and her prowess with the sword")
							.addOptions("Choose an option:", new Options() {
								@Override
								public void create() {
									option("No, still doesn't ring a bell", new Dialogue()
											.addPlayer(HeadE.CALM_TALK, "No, your name still doesn't ring a bell")
											.addNPC(LADY_KELI, HeadE.ANGRY, "Well, you know of me now")
											.addNPC(LADY_KELI, HeadE.FRUSTRATED, "I will ring your bell if you do not show respect")
											.addOptions("Choose an option:", new Options() {
												@Override
												public void create() {
													option("I do not show respect to killers and hoodlums", new Dialogue()
															.addPlayer(HeadE.FRUSTRATED, "I do not show respect to killers and hoodlums")
															.addNPC(LADY_KELI, HeadE.FRUSTRATED, "You should, you really should")
															.addNPC(LADY_KELI, HeadE.SECRETIVE, "I am wealthy enough to place a bounty on your head")
															.addNPC(LADY_KELI, HeadE.ANGRY, "Or just remove your head myself")
															.addNPC(LADY_KELI, HeadE.VERY_FRUSTRATED, "Now go, I am busy, too busy to fight a would-be hoodlum"));
													option("You must have trained a lot for this work", new Dialogue()
															.addPlayer(HeadE.AMAZED_MILD, "You must have trained a lot for this work")
															.addNPC(LADY_KELI, HeadE.TALKING_ALOT, "I have used a sword since I was a small girl")
															.addNPC(LADY_KELI, HeadE.EVIL_LAUGH, "stabbed three people before I was 6 years old"));
													option("I should not disturb someone as tough as you, great lady", new Dialogue()
															.addPlayer(HeadE.SCARED, "I should not disturb someone as tough as you")
															.addNPC(LADY_KELI, HeadE.CALM_TALK, "I need to do a lot of work, goodbye")
															.addNPC(LADY_KELI, HeadE.CALM_TALK, "When you get a little tougher, maybe I will give you a job"));
												}
											}));

									option("Yes, of course I have heard of you", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "Heard of you? You are famous in Gielinor!")
											.addNPC(LADY_KELI, HeadE.HAPPY_TALKING, "That's very kind of you to say. Reputations are not easily earned. I have managed to succeed" +
													" where many fail.")
											.addNext(()->{player.startConversation(new LadyKeliPrinceAliRescueD(player, ORIGINAL_OPTIONS, false));}));
									option("You must have trained a lot for this work", new Dialogue()
											.addPlayer(HeadE.AMAZED_MILD, "You must have trained a lot for this work")
											.addNPC(LADY_KELI, HeadE.TALKING_ALOT, "I have used a sword since I was a small girl")
											.addNPC(LADY_KELI, HeadE.EVIL_LAUGH, "stabbed three people before I was 6 years old"));
									option("I should not disturb someone as tough as you", new Dialogue()
											.addPlayer(HeadE.SCARED, "I should not disturb someone as tough as you")
											.addNPC(LADY_KELI, HeadE.CALM_TALK, "I need to do a lot of work, goodbye")
											.addNPC(LADY_KELI, HeadE.CALM_TALK, "When you get a little tougher, maybe I will give you a job"));
								}
							}));
				if(!isFirst)
					option("I should not disturb someone as tough as you", new Dialogue()
							.addPlayer(HeadE.SCARED, "I should not disturb someone as tough as you")
							.addNPC(LADY_KELI, HeadE.CALM_TALK, "I need to do a lot of work, goodbye")
							.addNPC(LADY_KELI, HeadE.CALM_TALK, "When you get a little tougher, maybe I will give you a job"));
			}
		});
	}

	private void latestPlanOptions(boolean isFirst) {
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				if(isFirst)
					option("Ah, I see. You must have been very skillful", new Dialogue()
							.addPlayer(HeadE.AMAZED, "You must have been very skilful")
							.addNPC(LADY_KELI, HeadE.HAPPY_TALKING, "Yes, I did most of the work, we had to grab the Pr...")
							.addNPC(LADY_KELI, HeadE.SKEPTICAL_THINKING, "er, we had to grab him under cover of ten of his bodyguards")
							.addNPC(LADY_KELI, HeadE.SECRETIVE, "It was a stroke of genius")
							.addNext(()->{player.startConversation(new LadyKeliPrinceAliRescueD(player, LATEST_PLAN_OPTIONS, false));}));
				option("Thats great, are you sure they will pay?", new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "Are you sure they will pay?")
						.addNPC(LADY_KELI, HeadE.EVIL_LAUGH, "They will pay, or we will cut his hair off and send it to them")
						.addPlayer(HeadE.HAPPY_TALKING, "Don't you think that something tougher, maybe cut his finger off would work better?")
						.addNPC(LADY_KELI, HeadE.HAPPY_TALKING, "Thats a good idea. I could use talented people like you")
						.addNPC(LADY_KELI, HeadE.SKEPTICAL_THINKING, "I may call on you if I need work doing"));
				option("Can you be sure they will not try to get him out?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Can you be sure they will not try to get him out?")
						.addNPC(LADY_KELI, HeadE.CALM_TALK, "There is no way to release him. The only key to the door is on a chain around my neck")
						.addNPC(LADY_KELI, HeadE.CALM_TALK, "And the locksmith who made the lock, died suddenly when he had finished")
						.addNPC(LADY_KELI, HeadE.HAPPY_TALKING, "There is not another key like this in the world")
						.addOptions("Choose an option:", new Options() {
							@Override
							public void create() {
								option("Could I see the key please", new Dialogue()
										.addPlayer(HeadE.CALM_TALK, "Could I see the key please, just for a moment")
										.addPlayer(HeadE.CALM_TALK, "It would be something I can tell my grandchildren")
										.addPlayer(HeadE.CALM_TALK, "When you are even more famous than you are now")
										.addNPC(LADY_KELI, HeadE.HAPPY_TALKING, "When you put it that way, I am sure you can see it")
										.addNPC(LADY_KELI, HeadE.HAPPY_TALKING, "You cannot steal the key, it is on an Adamantite chain")
										.addNPC(LADY_KELI, HeadE.HAPPY_TALKING, "I cannot see the harm")
										.addSimple("Keli shows you a small key on a stronglooking chain")
										.addOptions("Choose an option:", new Options() {
											@Override
											public void create() {
												option("Could I touch the key for a moment please", new Dialogue()
														.addPlayer(HeadE.CALM_TALK, "Could I touch the key for a moment please")
														.addNPC(LADY_KELI, HeadE.CALM_TALK, "Only for a moment then")
														.addNext(() -> {
															player.startConversation(new Conversation(player) {
																{
																	if(player.getInventory().containsItem(PrinceAliRescue.SOFT_CLAY, 1)) {
																		addSimple("(You put a piece of your soft clay in your hand)");
																		addSimple("(As you touch the key, you take an imprint of it)", () -> {
																			player.getInventory().deleteItem(PrinceAliRescue.SOFT_CLAY, 1);
																			player.getInventory().addItem(PrinceAliRescue.KEY_PRINT, 1);
																		});
																	} else
																		addSimple("You look at the key and give it back");
																	addPlayer(HeadE.HAPPY_TALKING, "Thank you so much, you are too kind, o great Keli");
																	addNPC(LADY_KELI, HeadE.HAPPY_TALKING, "There, run along now, I am very busy");
																	create();
																}
															});
														}));
												option("I should not disturb someone as tough as you", new Dialogue()
														.addPlayer(HeadE.SCARED, "I should not disturb someone as tough as you")
														.addNPC(LADY_KELI, HeadE.CALM_TALK, "I need to do a lot of work, goodbye")
														.addNPC(LADY_KELI, HeadE.CALM_TALK, "When you get a little tougher, maybe I will give you a job"));
											}
										}));

								option("That is a good way to keep secrets", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "That is a good way to keep secrets")
										.addNPC(LADY_KELI, HeadE.SKEPTICAL_HEAD_SHAKE, "It is the best way I know")
										.addNPC(LADY_KELI, HeadE.SECRETIVE, "Dead men tell no tales"));
								option("I should not disturb someone as tough as you", new Dialogue()
										.addPlayer(HeadE.SCARED, "I should not disturb someone as tough as you")
										.addNPC(LADY_KELI, HeadE.CALM_TALK, "I need to do a lot of work, goodbye")
										.addNPC(LADY_KELI, HeadE.CALM_TALK, "When you get a little tougher, maybe I will give you a job"));
							}
						}));
				option("I should not disturb someone as tough as you", new Dialogue()
						.addPlayer(HeadE.SCARED, "I should not disturb someone as tough as you")
						.addNPC(LADY_KELI, HeadE.CALM_TALK, "I need to do a lot of work, goodbye")
						.addNPC(LADY_KELI, HeadE.CALM_TALK, "When you get a little tougher, maybe I will give you a job"));
			}
		});
	}


	public static NPCClickHandler handleLadyKeli = new NPCClickHandler(new Object[] { LADY_KELI }, e -> e.getPlayer().startConversation(new LadyKeliPrinceAliRescueD(e.getPlayer()).getStart()));
}

