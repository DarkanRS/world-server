package com.rs.game.player.content.achievements;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;

public class AchievementSystemDialogue extends Conversation {

	public AchievementSystemDialogue(Player player, int npcId, SetReward reward) {
		super(player);
		
		Dialogue start = addOption("What would you like to say?", "Tell me about the Achievement System.", "Am I eligible for any rewards?", "Sorry, I was just leaving.");
		
		start.addPlayer(HeadE.CHEERFUL, "Tell me about the Achievement System.")
		.addNPC(npcId, HeadE.CHEERFUL_EXPOSITION, "Very well: the Achievement System is a collection of deeds you may wish to complete while adventuring around the world.")
		.addNPC(npcId, HeadE.CHEERFUL_EXPOSITION, "You can earn special rewards for completing certain achievements; at the very least, each is worth a cash bounty from Explorer Jack in Lumbridge.")
		.addNPC(npcId, HeadE.CHEERFUL_EXPOSITION, "Some also give items that will help complete other achievements, any many count as progress towards the set for the area they're in.");
		
		if (reward.hasRequirements(player, reward.getItemIds()[0], false)) {
			start.addOptions("Which item would you like to claim?", new Options() {
				@Override
				public void create() {
					for (int itemId : reward.getItemIds()) {
						option(ItemDefinitions.getDefs(itemId).getName(), new Dialogue()
                            .addOptions(ItemDefinitions.getDefs(itemId).getName(), new Options() {
                                @Override
                                public void create() {
                                    if (reward.hasRequirements(player, itemId, false)) {
                                        option("Claim", new Dialogue()
								.addPlayer(HeadE.CONFUSED, "Could I claim an " + ItemDefinitions.getDefs(itemId).getName() + "?")
								.addNPC(npcId, HeadE.CHEERFUL, "Of course, you've earned it!")
								.addItem(itemId, "You've been handed a " + ItemDefinitions.getDefs(itemId).getName() + ".", () -> {
									player.getInventory().addItem(itemId);
								}));
		} else {
                                        option("My requirements needed", () -> {
                                            if (!reward.hasRequirements(player, itemId))
                                                player.sendMessage("You do not yet meet the requirements for " + ItemDefinitions.getDefs(itemId).getName() + ". They have been listed above.");

                                        });
                                    }
                                    option("Farewell.");
                                }
                            }));
					}
		}
			});
		} else {
			start.addNPC(npcId, HeadE.SHAKING_HEAD, "Unfortunately not. The requirements for claiming the first tier will be listed in your chatbox.", () -> {
                reward.hasRequirements(player, reward.getItemIds()[0]);
            });
		}
		create();
	}

}
