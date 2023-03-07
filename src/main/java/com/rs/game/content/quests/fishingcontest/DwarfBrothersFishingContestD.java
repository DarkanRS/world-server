package com.rs.game.content.quests.fishingcontest;

import static com.rs.game.content.quests.fishingcontest.FishingContest.DO_ROUNDS;
import static com.rs.game.content.quests.fishingcontest.FishingContest.ENTER_COMPETITION;
import static com.rs.game.content.quests.fishingcontest.FishingContest.FISHING_PASS;
import static com.rs.game.content.quests.fishingcontest.FishingContest.FISHING_TROPHY;
import static com.rs.game.content.quests.fishingcontest.FishingContest.GIVE_TROPHY;
import static com.rs.game.content.quests.fishingcontest.FishingContest.NOT_STARTED;
import static com.rs.game.content.quests.fishingcontest.FishingContest.QUEST_COMPLETE;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class DwarfBrothersFishingContestD extends Conversation {
	public DwarfBrothersFishingContestD(Player player, int NPC) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.FISHING_CONTEST)) {
		case NOT_STARTED -> {
			addNPC(NPC, HeadE.CALM_TALK, "Hmmph. What do you want?");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option(" I was wondering what was down that tunnel?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, " I was wondering what was down that tunnel?")
							.addNPC(NPC, HeadE.CALM_TALK, "You can't go down there!")
							.addOptions("Choose an option:", new Options() {
								@Override
								public void create() {
									option("I didn't want to anyway", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "I didn't want to anyway")
											.addNPC(NPC, HeadE.CALM_TALK, "Good. Because you can’t.")
											.addPlayer(HeadE.HAPPY_TALKING, "Because I don’t want to.")
											.addNPC(NPC, HeadE.CALM_TALK, "Because you can’t. So that’s fine.")
											.addPlayer(HeadE.HAPPY_TALKING, "Yes, it is.")
											.addNPC(NPC, HeadE.CALM_TALK, "Yes. Fine.")
											.addPlayer(HeadE.HAPPY_TALKING, "Absolutely.")
											.addNPC(NPC, HeadE.CALM_TALK, "Alright then.")
											);
									option("Why not?", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "Why not?")
											.addNPC(NPC, HeadE.CALM_TALK, "This is the home of the Mountain Dwarves. How would you like it if I wanted to " +
													"take a shortcut through your home?")
											.addOptions("Choose an option:", new Options() {
												@Override
												public void create() {
													option("Ooh... is this a short cut to somewhere?", new Dialogue()
															.addPlayer(HeadE.HAPPY_TALKING, "Ooh... is this a short cut to somewhere?")
															.addNPC(NPC, HeadE.CALM_TALK, "Well, it is a lot easier to go this way to get past White " +
																	"Wolf Mountain than through those wolf filled passes.")
															);
													option("Oh, sorry, I hadn't realised it was private", new Dialogue()
															.addPlayer(HeadE.HAPPY_TALKING, "Oh, sorry, I hadn't realised it was private")
															.addNPC(NPC, HeadE.CALM_TALK, "Well, it is.")
															);
													if(player.getSkills().getLevel(Constants.FISHING) >= 10)
														option("If you were my friend I wouldn't mind it.", new Dialogue()
																.addPlayer(HeadE.HAPPY_TALKING, "If you were my friend I wouldn't mind it.")
																.addNPC(NPC, HeadE.CALM_TALK, "Yes, but I don't even know you.")
																.addPlayer(HeadE.HAPPY_TALKING, "Well, let's be friends!")
																.addNPC(NPC, HeadE.CALM_TALK, "I don't make friends easily. People need to earn my trust first.")
																.addOptions("Start Fishing Contest?", new Options() {
																	@Override
																	public void create() {
																		option("Yes", new Dialogue()
																				.addPlayer(HeadE.HAPPY_TALKING, "And how am I meant to do that?")
																				.addNPC(NPC, HeadE.CALM_TALK, "My, we are the persistent one aren't we? Well," +
																						" there's a certain gold artefact we're after.")
																				.addNPC(NPC, HeadE.CALM_TALK, "We dwarves are big fans of gold! This artefact " +
																						"is the first prize at the Hemenster fishing competition.")
																				.addNPC(NPC, HeadE.CALM_TALK, "Fortunately we have acquired a pass to enter " +
																						"that competition... Unfortunately Dwarves don't make good fishermen.")
																				.addNPC(NPC, HeadE.CALM_TALK, "Okay, I entrust you with our competition pass." +
																						" Don't forget to take some gold with you for the entrance fee")
																				.addSimple("You got the Fishing Contest Pass!", ()-> {
																					player.getQuestManager().setStage(Quest.FISHING_CONTEST, ENTER_COMPETITION);
																					player.getInventory().addItem(FISHING_PASS, 1);
																				})
																				.addNPC(NPC, HeadE.CALM_TALK, "Go to Hemenster and do us proud!")
																				);
																		option("No", new Dialogue()
																				.addPlayer(HeadE.HAPPY_TALKING, "You're a grumpy little man aren't you?")
																				.addNPC(NPC, HeadE.CALM_TALK, "Don’t you know it.")
																				);
																	}
																})
																);
													else
														option("If you were my friend I wouldn't mind it.", new Dialogue()
																.addSimple("You need 10 fishing to start Fishing Contest"));
												}
											})
											);
									option("I'm bigger than you. Let me by", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "I'm bigger than you. Let me by")
											.addNPC(NPC, HeadE.CALM_TALK, "Go away! You’re not going to bully your way in HERE!")
											);
								}
							})
							);
					option(" I was just stopping to say hello!", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, " I was just stopping to say hello!")
							.addNPC(NPC, HeadE.CALM_TALK, "Hello then.")

							);
					option("Do you have a brother?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Do you have a brother?")
							.addNPC(NPC, HeadE.CALM_TALK, "What if I do! It's no business of yours")

							);
				}
			});

		}
		case ENTER_COMPETITION, DO_ROUNDS -> {
			if(player.getInventory().containsItem(FISHING_PASS, 1)) {
				addNPC(NPC, HeadE.CALM_TALK, "Have you won yet?");
				addPlayer(HeadE.HAPPY_TALKING, "No, not yet.");
				addNPC(NPC, HeadE.CALM_TALK, "Well don't give up! Maybe old Jack can give you a few tips.");
			} else {
				addPlayer(HeadE.HAPPY_TALKING, "I need another competition pass");
				if(player.getInventory().hasFreeSlots())
					addNPC(NPC, HeadE.CALM_TALK, "Hmmm. It's a good job they sent us spares. There you go. Try not to lose that one.", ()->{
						player.getInventory().addItem(FISHING_PASS, 1);
					});
				else
					addNPC(NPC, HeadE.CALM_TALK, "You'll need inventory space first.");
			}
		}
		case GIVE_TROPHY -> {
			if(player.getInventory().containsItem(FISHING_TROPHY, 1)) {
				addNPC(NPC, HeadE.CALM_TALK, "Have you won yet?");
				addPlayer(HeadE.HAPPY_TALKING, "Yes I have!");
				addNPC(NPC, HeadE.CALM_TALK, "Well done! That's brilliant! Do you have the trophy with you?");
				addPlayer(HeadE.HAPPY_TALKING, "Yep, I have it right here!");
				addNPC(NPC, HeadE.CALM_TALK, "Oh! It's even more shiny and gold than I thought possible...");
				addNext(()->{
					player.getInventory().removeItems(new Item(FISHING_TROPHY, 1));
					player.getQuestManager().completeQuest(Quest.FISHING_CONTEST);
				});
			} else {
				addNPC(NPC, HeadE.CALM_TALK, "Have you won yet?");
				addPlayer(HeadE.HAPPY_TALKING, "Yes I have!");
				addNPC(NPC, HeadE.CALM_TALK, "Well done! That's brilliant! Do you have the trophy with you?");
				addPlayer(HeadE.SAD, "No...");
				addNPC(NPC, HeadE.CALM_TALK, "What? Go get it!");
				addPlayer(HeadE.HAPPY_TALKING, "I need to talk to Bonzo back at the competition site.");
			}
		}
		case QUEST_COMPLETE ->  {
			addNPC(NPC, HeadE.CALM_TALK, "Welcome, oh great Fishing Champion! Feel free to pop by and use our tunnel any time.");
			addPlayer(HeadE.HAPPY_TALKING, "Thank you, I think I'll stop by.");
		}
		}
	}

	public static NPCClickHandler handleAustriDialogue = new NPCClickHandler(new Object[] { 232 }, e -> e.getPlayer().startConversation(new DwarfBrothersFishingContestD(e.getPlayer(), 232).getStart()));
	public static NPCClickHandler handleVestriDialogue = new NPCClickHandler(new Object[] { 3679 }, e -> e.getPlayer().startConversation(new DwarfBrothersFishingContestD(e.getPlayer(), 3679).getStart()));
}
