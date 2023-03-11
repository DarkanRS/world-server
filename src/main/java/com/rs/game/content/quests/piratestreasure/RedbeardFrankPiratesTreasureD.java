package com.rs.game.content.quests.piratestreasure;

import static com.rs.game.content.quests.piratestreasure.PiratesTreasure.CHEST_KEY;
import static com.rs.game.content.quests.piratestreasure.PiratesTreasure.GET_TREASURE;
import static com.rs.game.content.quests.piratestreasure.PiratesTreasure.NOT_STARTED;
import static com.rs.game.content.quests.piratestreasure.PiratesTreasure.REDBEARD;
import static com.rs.game.content.quests.piratestreasure.PiratesTreasure.RUM;
import static com.rs.game.content.quests.piratestreasure.PiratesTreasure.SMUGGLE_RUM;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class RedbeardFrankPiratesTreasureD extends Conversation {



	public RedbeardFrankPiratesTreasureD(Player player) {
		super(player);
		switch (player.getQuestManager().getStage(Quest.PIRATES_TREASURE)) {
		case NOT_STARTED -> {
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("I'm in search of treasure.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "I'm in search of treasure.")
							.addNPC(REDBEARD, HeadE.CALM_TALK, "Arr, treasure you be after eh? Well I might be able to tell you where to find some... " +
									"For a price...")
							.addPlayer(HeadE.HAPPY_TALKING, "What sort of price?")
							.addNPC(REDBEARD, HeadE.CALM_TALK, "Well for example if you can get me a bottle of rum... Not just any rum mind... I'd like " +
									"some rum made on Karamja Island. There's no rum like Karamja Rum!")
							.addOptions("Start Pirate's Treasure?:", new Options() {
								@Override
								public void create() {
									option("Ok, I will bring you some rum", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "Ok, I will bring you some rum.", () -> {
												player.getQuestManager().setStage(Quest.PIRATES_TREASURE, SMUGGLE_RUM, true);
												if(player.getInventory().containsItem(RUM, 1)) {
													while(player.getInventory().containsItem(RUM, 1))
														player.getInventory().removeItems(new Item(RUM, 1));
													player.sendMessage("Your Karamja rum gets broken and spilled.");
												}
											})
											.addNPC(REDBEARD, HeadE.CALM_TALK, "Yer a saint, although it'll take a miracle to get it off Karamja.")
											.addPlayer(HeadE.HAPPY_TALKING, "What do you mean?")
											.addNPC(REDBEARD, HeadE.CALM_TALK, "The Customs office has been clampin' down on the export of spirits. You seem " +
													"like a resourceful young lad, I'm sure ye'll be able to find a way to slip the stuff past them.")
											.addPlayer(HeadE.HAPPY_TALKING, "Well I'll give it a shot.")
											.addNPC(REDBEARD, HeadE.CALM_TALK, "Arr, that's the spirit!"));
									option("Not right now", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "Not right now.")
											.addNPC(REDBEARD, HeadE.CALM_TALK, "Fair enough. I'll still be here and thirsty whenever you feel like helpin' out."));
								}
							}));
					option("Arr!", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Arr!")
							.addNPC(REDBEARD, HeadE.CALM_TALK, "Arr!")
							.addNext(()->{
								player.startConversation(new RedbeardFrankPiratesTreasureD(player).getStart());
							}));
					option("Do you have anything for trade?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Do you have anything for trade?")
							.addNPC(REDBEARD, HeadE.CALM_TALK, "Nothin' at the moment, but then again the Customs Agents are on the warpath right now.")
							.addNext(()->{
								player.startConversation(new RedbeardFrankPiratesTreasureD(player).getStart());
							}));
				}
			});

		}
		case SMUGGLE_RUM -> {
			addNPC(REDBEARD, HeadE.CALM_TALK, "Arr, Matey! Have ye brought some rum from yer ol' mate Frank?");
			if(player.getInventory().containsItem(RUM, 1)) {
				addPlayer(HeadE.HAPPY_TALKING, "Yes, I've got some.");
				addNPC(REDBEARD, HeadE.CALM_TALK, "Now a deal's a deal, I'll tell ye about the treasure. I used to serve under a pirate captain called " +
						"One-Eyed Hector. Hector were very successful and became very rich.");
				addNPC(REDBEARD, HeadE.CALM_TALK, "But about a year ago we were boarded by the Customs and Excise Agents. Hector were killed along with " +
						"many of the crew, I were one of the few to escape and I escaped with this.");
				addSimple("Frank happily takes the rum... and... hands you a key.", ()->{
					player.getInventory().removeItems(new Item(RUM, 1));
					player.getInventory().addItem(new Item(CHEST_KEY, 1));
					player.getQuestManager().setStage(Quest.PIRATES_TREASURE, GET_TREASURE, true);
				});
				addNPC(REDBEARD, HeadE.CALM_TALK, "This be Hector's key. I believe it opens his chest on his old room in the Blue Moon Inn in Varrock. " +
						"With any luck his treasure will be in there.");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("Ok thanks, I'll go and get it", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Ok thanks, I'll go and get it")
								);
						option("So why didn't you ever get it?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "So why didn't you ever get it?")
								.addNPC(REDBEARD, HeadE.CALM_TALK, "I'm not allowed in the Blue Moon Inn. Apparently I'm a drunken trouble maker."));
					}
				});

			} else {
				addPlayer(HeadE.HAPPY_TALKING, "No, not yet.");
				addNPC(REDBEARD, HeadE.CALM_TALK, "Not surprising, 'tis no easy task to get it off Karamja.");
				addPlayer(HeadE.HAPPY_TALKING, "What do you mean?");
				addNPC(REDBEARD, HeadE.CALM_TALK, "The Customs office has been clampin' down on the export of spirits. You seem like a resourceful young lad, " +
						"I'm sure ye'll be able to find a way to slip the stuff past them.");
				addPlayer(HeadE.HAPPY_TALKING, "Well I'll give it a shot.");
			}
		}
		case GET_TREASURE -> {
			if(!player.getInventory().containsItem(CHEST_KEY, 1)) {
				addPlayer(HeadE.HAPPY_TALKING, "I lost the chest key!");
				addNPC(REDBEARD, HeadE.CALM_TALK, "Good thing I keep a copy.");
				addSimple("Redbeard hands you another key.", ()->{
					player.getInventory().addItem(new Item(CHEST_KEY, 1));
				});
				return;
			}
			addPlayer(HeadE.HAPPY_TALKING, "Would you like a share of the treasure?");
			addNPC(REDBEARD, HeadE.CALM_TALK, "No lad, you got it fair and square. You enjoy it. It's what Hector would have wanted.");
		}
		}
	}
}
