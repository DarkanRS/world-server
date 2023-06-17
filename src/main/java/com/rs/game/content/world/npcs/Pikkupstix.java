package com.rs.game.content.world.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.content.Skillcapes;
import com.rs.game.content.quests.wolfwhistle.QuestPikkupstix;
import com.rs.game.content.quests.wolfwhistle.WolfWhistle;
import com.rs.game.content.skills.summoning.EnchantedHeadwear;
import com.rs.game.content.world.unorganized_dialogue.skillmasters.GenericSkillcapeOwnerD;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Pikkupstix extends Conversation {
	final static int PIKKUPSTIX = 6988;

	// right click options
	final static int TALK_TO = 1;
	final static int TRADE = 3;
	final static int ENCHANT = 4;

	public Pikkupstix(Player p, NPC pikkupstix, boolean greeting) {
		super(p);

		if (greeting) {
			addNPC(PIKKUPSTIX, HeadE.HAPPY_TALKING, "Hello there! Welcome to my humble abode. How can I help you?");
		}

		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {

				option("I have a question about Summoning...", new Dialogue()
						.addOptions(new Options() {
							@Override
							public void create() {
								option("So, what's Summoning all about, then?", new Dialogue()
										.addNPC(PIKKUPSTIX, HeadE.CONFUSED, "In general? Or did you have a specific topic mind?")
										.addOptions(new Options() {
											@Override
											public void create() {
												option("In general.", new Dialogue()
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Well, you already know about Summoning in general; otherwise, we would not be having this conversation!")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Effectively, the skill can be broken into three main parts: summoned familiars, charged items and pets.")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Summoned familiars are spiritual animals that can be called to you from the spirit plane, to serve you for a period of time.")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "These animals can also perform a special move, which is specific to the species. For example, a spirit wolf can perform the Howl special move if you are holding the correct Howl scroll.")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Charged items contain summoning power, but in a stable form.")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "They can be used to physically store any scrolls you have")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Those are the main effects of Summoning.")
																.addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Are you still following me?")
																.addPlayer(HeadE.CALM_TALK, "Yes, I think so.")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "I can supply you with pouches and spirit shards, but you will have to bring your own charms and secondary ingredients.")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Many creatures drop charms when you kill them in combat.")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "There are many different charms in the world: gold, blue, green, crimson and even some specialist ones like obsidian or void.")
																.addPlayer(HeadE.CALM_TALK, "I'll be sure to keep my eyes open for them.")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "The best way to raise your Summoning level is to infuse pouches.")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "You will still learn a little from summoning a familiar, as well as using a scroll and transforming the summoning pouches into scrolls.")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "But nothing is more potent than infusing a spirit into a Summoning pouch in the first place.")
																.addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Do you understand?")
																.addPlayer(HeadE.CHEERFUL_EXPOSITION, "Sure!")
																.addNPC(PIKKUPSTIX, HeadE.CHEERFUL_EXPOSITION, "Good!")
																.addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Where was I?")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Ah, yes, the last part of Summoning: the pets. The more you practice the skill, the more you will comprehend the natural world around you.")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "This is reflected in your increased ability to raise animals as pets, It takes a skilled summoner  to be able to raise some of Gielinor's more exotic animals, such as the lizards of Karamja, or even dragons!")
														//.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Now that I've given you this overview, do you want to know about anything specific?") LOOP ME
												);
												option("Tell me about summoning familiars.", new Dialogue()
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Summoned familiars are at the very core of Summoning. Each familiar is different, and the more powerful the summoner, the more powerful the familiar they can summon.")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "The animals themselves are not real, in the sense that you or I are real; they are spirits drawn from the spirit plane.")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "As a result, they have powers that the animals they resemble do not.")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "They cannot remain in this world indefinitely; they require a constant supply of energy to maintain their link to the spirit plane.")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "This energy is drained from your Summoning skill points.")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Your level of the Summoning skill is not drained, and you can refresh your points back up to your maximum level at an obelisk whenever you wish.")
																.addPlayer(HeadE.CONFUSED, "So, my Summoning skill points are like food to them?")
																.addNPC(PIKKUPSTIX, HeadE.CHEERFUL_EXPOSITION, "Yes, that is an appropriate analogy.")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "The more powerful the familiar, the more it must 'feed' and the more 'food' it will need to be satisfied.")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "As a result, only the most powerful summoners are able to maintain a link from a familiar to the spirit plane, since they are able to provide more 'food' with each Summoning level they gain.")
																.addPlayer(HeadE.CALM_TALK, "I'm starting to get a little hungry now.")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "As you gain mastery of the skill you will be able to have familiars out for the full time they exist. And still have some points over to re-summon them afterwards, if you wish.")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "This is because you are able to feed them the energy they need and have leftovers to spare!")
																.addPlayer(HeadE.CHEERFUL_EXPOSITION, "Great!")
																.addPlayer(HeadE.CONFUSED, "So, what can these familiars do?")
																.addNPC(PIKKUPSTIX, HeadE.LAUGH, "Why not ask me to count every blade of grass on a lawn?")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "The familiars each have unique abilities and even the most experience summoner will not know them all.")
																.addPlayer(HeadE.CALM_TALK, "Well, can you give me some hints?")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Well, some familiars are able to fight with you in combat. They will keep an eye out to see if you are fighting and will intervene, if they can.")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Other familiars will not fight, for various reasons, but they may provide bonuses in other tasks. Some will even carry your items for you, if you need them to.")
																.addPlayer(HeadE.CHEERFUL_EXPOSITION, "Amazing!")
																.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "")
														//.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Would you like to know anything else about summoning?") LOOP ME
												);

												if (player.getQuestManager().isComplete(Quest.WOLF_WHISTLE)) {
													option("Tell me about special moves.", new Dialogue()
																	.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Well, if a Summoning pouch is split apart at an obselisk, then the energy it contained will reconstritue itself - transform - into a scroll. This scroll can then be used to make your familiar perform its special move.")
																	.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "For example, spirit wolves are able to Howl, scaring away an opponent for a short period of time.")
																	.addNPC(PIKKUPSTIX, HeadE.CALM, "Well, each familiar has its own special move, and you can only use scrolls on the familiar that it applies to.")
																	.addPlayer(HeadE.LAUGH, "Or longer, in the case of that giant wolpertinger.")
																	.addNPC(PIKKUPSTIX, HeadE.LAUGH, "Indeed!")
																	.addNPC(PIKKUPSTIX, HeadE.CALM, "Well, each familiar has its own special move, and you can only use scrolls on the familiar that it applies to.")
																	.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "For example, a spirit wolf will only look at you oddly if you wish it to perform a dreadfowl special move.")
																	.addPlayer(HeadE.CONFUSED, "So, what sort of special moves are there?")
																	.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "The special moves are as varied as the familiars themselves.")
																	.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "A good rule of thumb is that if a familiar helps you in combat, then its special move is likely to damage attackers when you use a scroll.")
																	.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "On the other hand, if a familiar is more peaceful by nature, then its special move might heal or provide means to train your other skill - that sort of thing.")
																	.addPlayer(HeadE.CONFUSED, "Are the familiar's special moves similar to its normal abilities?")
																	.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "In general, no. But some familiars' special moves can be a more powerful verison of their normal ability.")
																	.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Take the spirit wolf, for example. Its special move and its normal ability are essentially the same.")
																	.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "However, the special move can be used on any nearby opponent, while the normal ability only works on those opponents you are currently fighting.")
																	.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "If your familiar is fighting with you, it will use its normal ability whenever it can.")
																	.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "It won't however, use its special move unless you have expressly asked it to, by activating a scroll.")
																	.addPlayer(HeadE.CALM_TALK, "I see. Thanks for the information.")
															//.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Would you like to know anything else about summoning?") LOOP ME
													);
												} else {
													option("Tell me about special moves.", new Dialogue()
															.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Well, if a Summoning pouch is split apart at an obselisk, then the energy it contained will reconstritue itself - transform - into a scroll. This scroll can then be used to make your familiar perform its special move.")
															.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "For example, spirit wolves are able to Howl, scaring away an opponent for a short period of time.")
															.addNPC(PIKKUPSTIX, HeadE.CALM, "Well, each familiar has its own special move, and you can only use scrolls on the familiar that it applies to.")
															.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "For example, a spirit wolf will only look at you oddly if you wish it to perform a dreadfowl special move.")
															.addPlayer(HeadE.CONFUSED, "So, what sort of special moves are there?")
															.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "The special moves are as varied as the familiars themselves.")
															.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "A good rule of thumb is that if a familiar helps you in combat, then its special move is likely to damage attackers when you use a scroll.")
															.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "On the other hand, if a familiar is more peaceful by nature, then its special move might heal or provide means to train your other skill - that sort of thing.")
															.addPlayer(HeadE.CONFUSED, "Are the familiar's special moves similar to its normal abilities?")
															.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "In general, no. But some familiars' special moves can be a more powerful verison of their normal ability.")
															.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Take the spirit wolf, for example. Its special move and its normal ability are essentially the same.")
															.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "However, the special move can be used on any nearby opponent, while the normal ability only works on those opponents you are currently fighting.")
															.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "If your familiar is fighting with you, it will use its normal ability whenever it can.")
															.addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "It won't however, use its special move unless you have expressly asked it to, by activating a scroll.")
															.addPlayer(HeadE.CALM_TALK, "I see. Thanks for the information.")
													);
												}
												option("Tell me about charged items."); // unimplemented
												option("Tell me about pets.", new Dialogue()
														.addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "Unfortunately, pets are not implemented yet.")
												);
											}
										}));
								option("Can I buy some Summoning supplies?", () -> ShopsHandler.openShop(player, "taverly_summoning_shop"));
								option("Please tell me about skillcapes.", () -> player.startConversation(new GenericSkillcapeOwnerD(player, 6988, Skillcapes.Summoning)));
							}
						}));

				String quest_option = QuestPikkupstix.getNextOptionTextPikkupstix(p);
				if (quest_option != null) {
					option(quest_option, () -> p.startConversation(new QuestPikkupstix(p, pikkupstix)));
				}

				// only if you know about Pikkenmix
//				option("Talk about Pikkenmix.");

				if (p.getQuestManager().getStage(Quest.WOLF_WHISTLE) > WolfWhistle.WOLPERTINGER_CREATION) {
					option("You really seem to have had some problems with your assistants.", new Dialogue()
							.addNPC(PIKKUPSTIX, HeadE.SAD, "You are right, it has been pretty hard since the war started.")
							.addPlayer(HeadE.CONFUSED, "Well what's happened? Have the trolls been killing them, or has it all been down to accidents?")
							.addNPC(PIKKUPSTIX, HeadE.CALM, "It is a mixture of both, really. To be honest, I have always found that my assistants have had a lot of eagerness and a lot less common sense for self-preservation.")
							.addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Now, let me see...You already know about Stikklebrix...who else was there?")
							.addNPC(PIKKUPSTIX, HeadE.CALM, "There was poor Lunatrix, who went mad. He tried to commune with some elder spirits, and they were not happy.")
							.addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "Last I saw of him, he went running out of the door with his pants on his head...")
							.addNPC(PIKKUPSTIX, HeadE.CALM, "Then there was Ashtrix. He managed to cut his own hand off, the poor fool.")
							.addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "It would have been all right, except he then managed to wedge a hacksaw in the stump, and in his delirium wouldn't let anyone help him.")
							.addNPC(PIKKUPSTIX, HeadE.CALM, "Then there was Spartrix, who was captured by the Black Knights and taken as a slave.")
							.addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "I hear that he tried to lead a slaved rebellion. Which, while noble in itself, is not really the sort of thing a 5-foot, sickly, bookish druid should do.")
							.addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "Especially when unarmed and facing evil knights in full plate mail.")
							.addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Let's see...there was Beezeltrix and the smoke devil; we literally lost Felitrix; those trolls took the twins when they were healing soldiers on the front line...")
							.addNPC(PIKKUPSTIX, HeadE.MORTIFIED, "And the less said about Hastrix, the better!")
							.addPlayer(HeadE.AMAZED, "Wow...you really haven't had much luck with assistants.")
							.addNPC(PIKKUPSTIX, HeadE.SAD, "It has been one heck of a busy week, I can tell you.")
							.addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Now, is there anything else you need?")
							.addNext(() -> p.startConversation(new Pikkupstix(p, pikkupstix, false)))
					);
				}

			}
		});

		create();
	}

	public static NPCClickHandler handlePikkupstixDialogue = new NPCClickHandler(new Object[] { PIKKUPSTIX }, e -> {
		Player p = e.getPlayer();

		switch(e.getOption()) {
			case "Talk-to" -> p.startConversation(new Pikkupstix(e.getPlayer(), e.getNPC(), true));
			case "Trade" -> ShopsHandler.openShop(p, "taverly_summoning_shop");
			case "Enchant" -> {
				for (Item i : p.getInventory().getItems().array()) {
					if ((null != i) && (null != EnchantedHeadwear.Headwear.forId(i.getId()))) {
						p.startConversation(new Dialogue()
								.addNPC(PIKKUPSTIX, HeadE.HAPPY_TALKING, "That is a fine piece of headwear you have there. If you give me a closer look, I may be able to enchant it.")
						);
						return;
					}
				}
				p.startConversation(new Dialogue()
						.addNPC(PIKKUPSTIX, HeadE.HAPPY_TALKING, "If you bring me the right headwear, I may be able to assist in enchanting it.")
						);
			}
		}
	});

}
