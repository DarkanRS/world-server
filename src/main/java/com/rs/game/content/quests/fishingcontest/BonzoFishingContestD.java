package com.rs.game.content.quests.fishingcontest;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.fishingcontest.FishingContest.*;

@PluginEventHandler
public class BonzoFishingContestD extends Conversation {
	private static final int NPC = 225;
	public BonzoFishingContestD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.FISHING_CONTEST)) {
		case NOT_STARTED -> {
			addPlayer(HeadE.HAPPY_TALKING, "Hi, what are you doing here?");
			addNPC(NPC, HeadE.CALM_TALK, "I am waiting for the fishing contest to start.");
			addPlayer(HeadE.HAPPY_TALKING, "Oh.");
		}
		case ENTER_COMPETITION -> {
			addNPC(NPC, HeadE.CALM_TALK, "Roll up, roll up! Enter the great Hemenster Fishing Contest! Only 5gp entrance fee!");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					if(player.getInventory().hasCoins(5)) {
						if(player.getQuestManager().getAttribs(Quest.FISHING_CONTEST).getB(PIPE_HAS_GARLIC))
							option("I'll enter the competition please", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "I'll enter the competition please")
									.addNPC(NPC, HeadE.CALM_TALK, "Marvelous!")
									.addNPC(NPC, HeadE.CALM_TALK, "Ok, we've got all the fishermen! It's time to roll! Ok, nearly everyone is in their place " +
											"already. You fish in the spot by the willow tree, and the Sinister Stranger, you fish by the pipes", () -> {
												player.getInventory().removeCoins(5);
												player.getQuestManager().setStage(Quest.FISHING_CONTEST, DO_ROUNDS);
											})
									.addNPC(3677, HeadE.CALM_TALK, "Actually, can I take the willow tree!?")
									.addNPC(NPC, HeadE.CALM_TALK, "Sure, " + player.getDisplayName() + " you go by the pipes...")
									);
						else
							option("I'll enter the competition please", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "I'll enter the competition please")
									.addNPC(NPC, HeadE.CALM_TALK, "Marvelous!")
									.addNPC(NPC, HeadE.CALM_TALK, "Ok, we've got all the fishermen! It's time to roll! Ok, nearly everyone is in their place " +
											"already. You fish in the spot by the willow tree, and the Sinister Stranger, you fish by the pipes", () -> {
												player.getInventory().removeCoins(5);
												player.getQuestManager().setStage(Quest.FISHING_CONTEST, DO_ROUNDS);
											})
									.addNPC(NPC, HeadE.CALM_TALK, "Your fishing competition spot is by the willow tree.")
									);
					}
					else
						option("I'll enter the competition please", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I'll enter the competition please")
								.addNPC(NPC, HeadE.CALM_TALK, "Do you have 5 coins?")
								.addPlayer(HeadE.CALM_TALK, "No I don't.")
								.addNPC(NPC, HeadE.CALM_TALK, "No pay, no play.")
								);
					option("No thanks, I'll just watch the fun.", new Dialogue());
				}
			});

		}
		case DO_ROUNDS -> {
			if(player.getInventory().containsItem(new Item(338, 1))) {//trophy
				addNPC(NPC, HeadE.CALM_TALK, "Okay folks, time's up! Let's see who caught the biggest fish!");
				addPlayer(HeadE.HAPPY_TALKING, "I caught some fish! Here...");
				addSimple("You show your catch");
				addNPC(NPC, HeadE.CALM_TALK, "We have a new winner! The heroic-looking person who was fishing y the pipes has caught the biggest carp " +
						"I've seen since Grandpa Jack used to compete!");
				addItem(FISHING_TROPHY, "You are given the Hemenester fishing trophy!", ()->{
					player.getInventory().removeItems(new Item(338, 1));
					player.getInventory().addItem(FISHING_TROPHY, 1);
					player.getQuestManager().setStage(Quest.FISHING_CONTEST, GIVE_TROPHY);
				});
			} else {
				addNPC(NPC, HeadE.CALM_TALK, "So how are you doing so far?");
				addSimple("You look around...");
				addPlayer(HeadE.SECRETIVE, "I think I might be able to find bigger fish...");
			}
		}
		case GIVE_TROPHY -> {
			if(player.getInventory().containsItem(new Item(FISHING_TROPHY, 1))) {//trophy
				addNPC(NPC, HeadE.CALM_TALK, "Hello champ! So any hints on how to fish?");
				addPlayer(HeadE.HAPPY_TALKING, "I think I'll keep them to myself.");
			} else {
				addNPC(NPC, HeadE.CALM_TALK, "Hey you lost your trophy");
				if(player.getInventory().hasFreeSlots())
					addItem(FISHING_TROPHY, "Bonzo gives you your trophy...", ()->{
						player.getInventory().addItem(FISHING_TROPHY, 1);
					});
				else
					addSimple("You need to make space for your trophy.");
			}
		}
		case QUEST_COMPLETE ->  {
			addNPC(NPC, HeadE.CALM_TALK, "Hello champ! So any hints on how to fish?");
			addPlayer(HeadE.HAPPY_TALKING, "I think I'll keep them to myself.");
		}
		}
	}

	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[] { NPC }, e -> e.getPlayer().startConversation(new BonzoFishingContestD(e.getPlayer()).getStart()));
}
