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
package com.rs.game.content.quests.demonslayer;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class CaptainRovinDemonSlayerD extends Conversation {
	private final int CAPTAIN_ROVIN = 884;

	public CaptainRovinDemonSlayerD(Player player) {
		super(player);
		addNPC(CAPTAIN_ROVIN, HeadE.FRUSTRATED, "What are you doing up here? Only the palace guards are allowed up here.");
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("I am one of the palace guards.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "I am one of the palace guards.")
						.addNPC(CAPTAIN_ROVIN, HeadE.SKEPTICAL, "No, you're not! I know all the palace guards.")
						.addOptions("Choose an option:", new Options() {
							@Override
							public void create() {
								option("I'm a new recruit.", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "I'm a new recruit.")
										.addNPC(CAPTAIN_ROVIN, HeadE.CALM_TALK, "I interview all the new recruits. I'd know if you were one of them.")
										.addPlayer(HeadE.SCARED, "That blows that story out of the water then.")
										.addNPC(CAPTAIN_ROVIN, HeadE.ANGRY, "Get out of my sight."));
								option("I've had extensive plastic surgery.", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "I've had extensive plastic surgery.")
										.addNPC(CAPTAIN_ROVIN, HeadE.SKEPTICAL, "What sort of surgery is that? I've never heard of it. Besides, you look reasonably healthy.")
										.addNPC(CAPTAIN_ROVIN, HeadE.SKEPTICAL, "Why is this relevant anyway? You still shouldn't be here."));
							}
						}));
				option("What about the King?", new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "What about the King?")
						.addNPC(CAPTAIN_ROVIN, HeadE.SKEPTICAL_THINKING, "Well, yes, I suppose we'd let him up. He doesn't generally want to come up here, but if" +
								" he did want to, he could.")
						.addNPC(CAPTAIN_ROVIN, HeadE.ANGRY, "Anyway, you're not the King either. So get out of my sight."));
				if(!player.isQuestComplete(Quest.DEMON_SLAYER)
						&& player.getQuestManager().getStage(Quest.DEMON_SLAYER) >= DemonSlayer.AFTER_SIR_PRYSIN_INTRO_STAGE
						&& !player.getInventory().containsItem(2400))
					option("Yes I know, but this is important.", new Dialogue()
							.addPlayer(HeadE.AMAZED_MILD, "Yes I know, but this is important.")
							.addNPC(CAPTAIN_ROVIN, HeadE.SKEPTICAL_THINKING, "Ok, I'm listening. Tell me what's so important.")
							.addOptions("Choose an option:", new Options() {
								@Override
								public void create() {
									option("There's a demon who wants to invade this city.", new Dialogue()
											.addPlayer(HeadE.AMAZED, "There's a demon who wants to invade this city.")
											.addNPC(CAPTAIN_ROVIN, HeadE.CALM_TALK, "Is it a powerful demon?")
											.addOptions("Choose an option:", new Options() {
												@Override
												public void create() {
													option("Not really.", new Dialogue()
															.addPlayer(HeadE.CALM, "Not really.")
															.addNPC(CAPTAIN_ROVIN, HeadE.CALM_TALK, "Well, I'm sure the palace guards can deal with it, then. Thanks for the information."));
													option("Yes, very.", new Dialogue()
															.addPlayer(HeadE.SCARED, "Yes, very.")
															.addNPC(CAPTAIN_ROVIN, HeadE.SKEPTICAL_THINKING, "As good as the palace guards are, I don't know if they're up to taking on " +
																	"a very powerful demon.")
															.addOptions("Choose an option:", new Options() {
																@Override
																public void create() {
																	option("Yeah, the palace guards are rubbish!", new Dialogue()
																			.addPlayer(HeadE.HAPPY_TALKING, "Yeah, the palace guards are rubbish!")
																			.addNPC(CAPTAIN_ROVIN, HeadE.HAPPY_TALKING, "Yeah, they're--")
																			.addNPC(CAPTAIN_ROVIN, HeadE.ANGRY, "Wait! How dare you insult the palace guards? Get out of my sight!"));
																	option("It's not them who are going to fight the demon, it's me.", new Dialogue()
																			.addPlayer(HeadE.CALM_TALK, "It's not them who are going to fight the demon, it's me.")
																			.addNPC(CAPTAIN_ROVIN, HeadE.AMAZED_MILD, "What, all by yourself? How are you going to do that?")
																			.addPlayer(HeadE.HAPPY_TALKING, "I'm going to use the powerful sword Silverlight, which I believe you have one of " +
																					"the keys for?")
																			.addNPC(CAPTAIN_ROVIN, HeadE.SECRETIVE, "Yes, I do. But why should I give it to you?")
																			.addNext(()-> {
																				player.startConversation(new CaptainRovinDemonSlayerD(player, 0).getStart());}));
																}
															}));
												}
											}));
									option("Erm I forgot.", new Dialogue()
											.addPlayer(HeadE.SAD_MILD, "Erm I forgot.")
											.addNPC(CAPTAIN_ROVIN, HeadE.FRUSTRATED, "Well it can't be that important then.")
											.addPlayer(HeadE.AMAZED_MILD, "How do you know?")
											.addNPC(CAPTAIN_ROVIN, HeadE.ANGRY, "Just go away."));
									option("The castle has just received its ale delivery.", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "The castle has just received its ale delivery.")
											.addNPC(CAPTAIN_ROVIN, HeadE.LAUGH, "Now that is important. However I'm the wrong person to speak to about it. Go talk to the " +
													"kitchen staff."));
								}
							}));
			}
		});

	}

	public CaptainRovinDemonSlayerD(Player player, int convoID) {
		super(player);

		switch(convoID) {
			case 0:
				addOptions("Choose an option", new Options() {
					@Override
					public void create() {
						option("Gypsy Aris said I was destined to kill the demon.", new Dialogue()
								.addPlayer(HeadE.SECRETIVE, "Gypsy Aris said I was destined to kill the demon.")
								.addNPC(CAPTAIN_ROVIN, HeadE.SKEPTICAL_HEAD_SHAKE, "A gypsy? Destiny? I don't believe in that stuff. I got where I am today by hard work, " +
										"not by destiny! Why should I care what that mad old gypsy says?")
								.addNext(()-> {
									player.startConversation(new CaptainRovinDemonSlayerD(player, 0).getStart());}));
						option("Otherwise the demon will destroy the city!", new Dialogue()
								.addPlayer(HeadE.AMAZED, "Otherwise the demon will destroy the city!")
								.addNPC(CAPTAIN_ROVIN, HeadE.ANGRY, " You can't fool me! How do I know you haven't just made that story up to get my key?")
								.addNext(()-> {
									player.startConversation(new CaptainRovinDemonSlayerD(player, 0).getStart());}));
						option("Sir Prysin said you would give me the key.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Sir Prysin said you would give me the key.")
								.addNPC(CAPTAIN_ROVIN, HeadE.SKEPTICAL_HEAD_SHAKE, "Oh, he did, did he? Well I don't report to Sir Prysin, I report directly to the king!")
								.addNPC(CAPTAIN_ROVIN, HeadE.ANGRY, "I didn't work my way up through the ranks of the palace guards so I could take orders from an " +
										"ill-bred moron who only has his job because his great- grandfather was a hero with a silly name!")
								.addNext(()-> {
									player.startConversation(new CaptainRovinDemonSlayerD(player, 0).getStart());}));
						option("Why did he give you one of the keys then?", new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "Why did he give you one of the keys then?")
								.addNPC(CAPTAIN_ROVIN, HeadE.HAPPY_TALKING, "Only because the king ordered him to! The king couldn't get Sir Prysin to part with his " +
										"precious ancestral sword, but he made him lock it up so he couldn't lose it.")
								.addNPC(CAPTAIN_ROVIN, HeadE.HAPPY_TALKING, "I got one key and I think some wizard got another. Now what happened to the third one?")
								.addPlayer(HeadE.FRUSTRATED, "Sir Prysin dropped it down a drain!")
								.addNPC(CAPTAIN_ROVIN, HeadE.LAUGH, "Ha ha ha! The idiot!")
								.addNPC(CAPTAIN_ROVIN, HeadE.HAPPY_TALKING, "Okay, I'll give you the key, just so that it's you that kills the demon and not Sir Prysin!")
								.addNext(()-> {
									if(player.getInventory().hasFreeSlots()) {
										player.getInventory().addItem(2400, 1);
										player.getPackets().sendGameMessage("Rovin gives you the key");
									} else
										player.startConversation(new Conversation(player) {
											{
												addSimple("You need an empty space for the key.");
											}
										});
								}));
					}
				});
				break;
		}

	}


	public static NPCClickHandler handleCaptainRovin = new NPCClickHandler(new Object[] { 884 }, e -> e.getPlayer().startConversation(new CaptainRovinDemonSlayerD(e.getPlayer()).getStart()));
}
