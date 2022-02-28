package com.rs.game.player.quests.handlers.scorpioncatcher;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.quests.Quest;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class ThormacScorpionCatcherD extends Conversation {
	private final static int NPC = 389;
	
	public ThormacScorpionCatcherD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.SCORPION_CATCHER)) {
		case ScorpionCatcher.NOT_STARTED -> {
			addNPC(NPC, HeadE.CALM_TALK, "Hello I am Thormac the Sorcerer, I don't suppose you could be of assistance to me?");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					if(p.getSkills().getLevel(Constants.PRAYER) < 31)
						option("What do you need assistance with?", new Dialogue()
								.addSimple("You need 31 prayer to start Scorpion Catcher."));
					else
						option("What do you need assistance with?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "What do you need assistance with?")
								.addNPC(NPC, HeadE.CALM_TALK, "I've lost my pet scorpions. They're lesser Kharid scorpions, a very rare breed. I left " +
										"their cage door open, now I don't know where they've gone. ")
								.addNPC(NPC, HeadE.CALM_TALK, "There's three of them, and they're quick little beasties. They're all over Runescape.")
								.addPlayer(HeadE.HAPPY_TALKING, "What's in it for me?")
								.addNPC(NPC, HeadE.CALM_TALK, "Well I suppose I can aid you with my skills as a staff sorcerer. Most battlestaffs around here" +
										" are a bit puny.")
								.addNPC(NPC, HeadE.SECRETIVE, "I can beef them up for you a bit.")
								.addOptions("Start Scorption Catcher?", new Options() {
									@Override
									public void create() {
										option("Yes", new Dialogue()
												.addPlayer(HeadE.HAPPY_TALKING, "So how would I go about catching them then?")
												.addNPC(NPC, HeadE.CALM_TALK, "If you go up to the village of Seers, to the North of here, one of them " +
														"will be able to tell you where the scorpions are now.")
												.addNPC(NPC, HeadE.CALM_TALK, "Also, I have a scorpion cage here which you can use to catch them in.")
												.addSimple("Thormac gives you a cage", ()->{
													p.getQuestManager().setStage(Quest.SCORPION_CATCHER, ScorpionCatcher.LOOK_FOR_SCORPIONS);
													p.getInventory().addItem(new Item(ScorpionCatcher.EMPTY_CAGE, 1), true);
												})
												);
										option("No", new Dialogue());
									}
								}));
					option("I'm a little busy.", new Dialogue()
							.addPlayer(HeadE.FRUSTRATED, "I'm a little busy")
							.addNPC(NPC, HeadE.CALM_TALK, "Okay then."));
				}
			});

		}
		case ScorpionCatcher.LOOK_FOR_SCORPIONS -> {
			if (ScorpionCatcher.getNumCaught(p) >= 3) {
				addNPC(NPC, HeadE.CALM_TALK, "How goes your quest?");
				addPlayer(HeadE.HAPPY_TALKING, "I have retrieved all your scorpions.");
				addNPC(NPC, HeadE.CALM_TALK, "Aha, my little scorpions home at last!");
				addNext(() -> {
					p.getQuestManager().completeQuest(Quest.SCORPION_CATCHER);
					p.getInventory().removeAllItems(ScorpionCatcher.EMPTY_CAGE, ScorpionCatcher.CAUGHT_CAGE_1, ScorpionCatcher.CAUGHT_CAGE_2, ScorpionCatcher.CAUGHT_CAGE_3);
				});
			} else if (!p.getInventory().containsOneItem(ScorpionCatcher.EMPTY_CAGE, ScorpionCatcher.CAUGHT_CAGE_1, ScorpionCatcher.CAUGHT_CAGE_2, ScorpionCatcher.CAUGHT_CAGE_3)) {
				addNPC(NPC, HeadE.CALM_TALK, p.getDisplayName() + "!");
				addPlayer(HeadE.HAPPY_TALKING, "Yes?");
				if (p.getInventory().hasFreeSlots()) {
					addNPC(NPC, HeadE.CALM_TALK, "Here is the cage, I found it!", () -> p.getInventory().addItem(ScorpionCatcher.getCageId(p)));
					addPlayer(HeadE.FRUSTRATED, "Great...");
				} else {
					addNPC(NPC, HeadE.CALM_TALK, "I have the cage but you need room for me to give it to you...");
					addPlayer(HeadE.HAPPY_TALKING, "Great!");
				}

			} else {
				addPlayer(HeadE.HAPPY_TALKING, "I've not caught all the scorpions yet.");
				addNPC(NPC, HeadE.CALM_TALK, "Well remember to go speak to the Seers, North of here, if you need any help.");
			}
		}
		}
	}
}
