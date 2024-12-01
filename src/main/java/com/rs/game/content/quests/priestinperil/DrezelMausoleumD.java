package com.rs.game.content.quests.priestinperil;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;

import static com.rs.game.content.world.areas.morytania.npcs.DrezelKt.DREZEL;

public class DrezelMausoleumD extends Conversation {

	public DrezelMausoleumD(Player player) {
		super(player);
		int remainingEssence = Utils.clampI(50 - player.getQuestManager().getAttribs(Quest.PRIEST_IN_PERIL).getI("essence"), 0, 50);
		if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 9) {
			player.startConversation(new Dialogue()
					.addNPC(DREZEL, HeadE.CALM_TALK, "Ah, " + player.getDisplayName() + ". Glad you made it. Things are worse than I feared down here. I'm not sure if I will be able to repair the damage.")
					.addPlayer(HeadE.CALM_TALK, "Why, what's happened?")
					.addNPC(DREZEL, HeadE.CALM_TALK, "Sadly, with the guard dog gone, there was nothing to stop those Zamorakians from entering the mausoleum. From what I can tell, they have used some kind of evil potion to pollute the Salve.")
					.addNPC(DREZEL, HeadE.CALM_TALK, "The well here is built right over the source of the river. That means that their potion will spread all the way down the Salve, disrupting the blessings placed upon it.")
					.addNPC(DREZEL, HeadE.CALM_TALK, "Before long, there will be nothing to stop the vampyres from invading Misthalin at their leisure.")
					.addPlayer(HeadE.CALM_TALK, "What can we do to prevent that?")
					.addNPC(DREZEL, HeadE.CALM_TALK, "This passage is currently the only route between Morytania and Misthalin. The barrier here draws power from the river.")
					.addNPC(DREZEL, HeadE.CALM_TALK, "I have managed to reinforce the barrier, but I must continue focussing on it to keep it intact. Although the passage is safe for now, I do not know how long I can keep it that way.")
					.addNPC(DREZEL, HeadE.CALM_TALK, "This passage could be the least of our worries soon though. Before long, the vampyres will be able to cross at any point of the river. We won't be able to reinforce all of it.")
					.addPlayer(HeadE.CALM_TALK, "Couldn't you bless the river to purify it? Like you did with the water I took from the well?")
					.addNPC(DREZEL, HeadE.CALM_TALK, "No, that would not work. The power I have from Saradomin is not great enough to cleanse an entire river of this foul Zamorakian pollutant.")
					.addNPC(DREZEL, HeadE.CALM_TALK, "However, I do have one other idea.")
					.addPlayer(HeadE.CALM_TALK, "What's that?")
					.addNPC(DREZEL, HeadE.CALM_TALK, "I believe we might be able to soak up the evil magic that the potion has released into the river.")
					.addNPC(DREZEL, HeadE.CALM_TALK, "I'm sure you know of runestones, used by mages to power their spells. The essence used to create these stones absorbs magical potential from runic altars to be released later.")
					.addPlayer(HeadE.CALM_TALK, "And you think we could use some rune essence to absorb the magic they've released into the Salve?")
					.addNPC(DREZEL, HeadE.CALM_TALK, "Exactly. If you could bring me fifty essence, I should be able to reverse the damage done. Be quick though. The longer we wait, the worse things will get.", () -> player.getQuestManager().setStage(Quest.PRIEST_IN_PERIL, 10))
			);
		}
		if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 10) {
			if (player.getInventory().containsItem(1436) || player.getInventory().containsItem(7936)) {
				int essence = player.getInventory().getAmountOf(1436);
				int pureEssence = player.getInventory().getAmountOf(7936);
				int totalEssence = essence + pureEssence;
				int currentEssence = player.getQuestManager().getAttribs(Quest.PRIEST_IN_PERIL).getI("essence");

				if (remainingEssence == 0) {
					player.startConversation(new Dialogue()
							.addNPC(DREZEL, HeadE.CALM_TALK, "Excellent! That should do it! I will bless these stones and place them within the well. With the river safe, Misthalin should be protected from the vampyres once more!")
							.addNPC(DREZEL, HeadE.CALM_TALK, "Please take this dagger. It has been handed down within my family for generations and is filled with the power of Saradomin.")
							.addNPC(DREZEL, HeadE.CALM_TALK, "You will find that it has the ability to prevent werewolves from adopting their wolf form in combat. Hopefully it comes in useful.", () -> player.getQuestManager().setStage(Quest.PRIEST_IN_PERIL, 11))
					);
					player.getQuestManager().completeQuest(Quest.PRIEST_IN_PERIL);
					player.sendMessage("Drezel blesses you, allowing you to pass through the barrier into Morytania.");
					return;
				}

				if (totalEssence > 0) {
					int essenceToUse = Math.min(remainingEssence, totalEssence);
					player.getInventory().deleteItem(1436, essenceToUse);
					player.getInventory().deleteItem(7936, essenceToUse);
					currentEssence += essenceToUse;
					player.getQuestManager().getAttribs(Quest.PRIEST_IN_PERIL).setI("essence", currentEssence);
					remainingEssence = Math.max(0, 50 - currentEssence);

					if (remainingEssence == 0) {
						player.startConversation(new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "I brought you some rune essence.")
								.addNPC(DREZEL, HeadE.CALM_TALK, "Quickly, give " + (totalEssence == 1 ? "it" : "them") + " to me!")
								.addNPC(DREZEL, HeadE.CALM_TALK, "Excellent! That should do it! I will bless these stones and place them within the well. With the river safe, Misthalin should be protected from the vampyres once more!")
								.addNPC(DREZEL, HeadE.CALM_TALK, "Please take this dagger. It has been handed down within my family for generations and is filled with the power of Saradomin.")
								.addNPC(DREZEL, HeadE.CALM_TALK, "You will find that it has the ability to prevent werewolves from adopting their wolf form in combat. Hopefully it comes in useful.", () -> player.getQuestManager().setStage(Quest.PRIEST_IN_PERIL, 11))
						);
						player.getQuestManager().completeQuest(Quest.PRIEST_IN_PERIL);
						player.sendMessage("Drezel blesses you, allowing you to pass through the barrier into Morytania.");
						return;
					} else {
						player.startConversation(new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "I brought you some rune essence.")
								.addNPC(DREZEL, HeadE.CALM_TALK, "Quickly, give " + (totalEssence == 1 ? "it" : "them") + " to me!")
								.addNPC(DREZEL, HeadE.CALM_TALK, "Thank you. I need " + remainingEssence + " more.")
						);
					}
				} else {
					player.startConversation(new Dialogue()
							.addPlayer(HeadE.CALM_TALK, "What am I supposed to do again?")
							.addNPC(DREZEL, HeadE.CALM_TALK, "Bring me fifty rune essence so that I can undo the damage done by those Zamorakians. I need " + remainingEssence + " more.")
					);
				}
			} else {
				player.startConversation(new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "What am I supposed to do again?")
						.addNPC(DREZEL, HeadE.CALM_TALK, "Bring me fifty rune essence so that I can undo the damage done by those Zamorakians. I need " + remainingEssence + " more.")
				);
			}
		}
		if (player.getQuestManager().isComplete(Quest.PRIEST_IN_PERIL)){
			player.startConversation(new Dialogue()
					.addPlayer(HeadE.CALM_TALK, "So can I pass through that barrier now?")
					.addNPC(DREZEL, HeadE.CALM_TALK, "Into Morytania? Yes, you can. The barrier will prevent the servants of Zamorak from entering Misthalin, but it is safe for those blessed by Saradomin.")
					.addNPC(DREZEL, HeadE.CALM_TALK, "I must warn you though, Morytania is an evil land. The region is filled with creatures and monsters more terrifying than any you'll find on this side of the Salve.")
					.addNPC(DREZEL, HeadE.CALM_TALK, "Worst of all are the vampyres, the rulers of Morytania. Be very cautious of their kind. Many are completely immune to human weaponry. Those that aren't, are still very hard to kill.")
					.addPlayer(HeadE.CALM_TALK, "I'll be careful. Anything else I should know?")
					.addNPC(DREZEL, HeadE.CALM_TALK, "The first settlement you'll likely come across is Canifis, a town of werewolves. They probably won't attack you unless provoked, but you should still be cautious.")
					.addNPC(DREZEL, HeadE.CALM_TALK, "You should keep your Wolfbane dagger to hand. It will prevent them from taking on their wolf form.")
					.addPlayer(HeadE.CALM_TALK, "I see. Thanks.")
					.addNPC(DREZEL, HeadE.CALM_TALK, "One last thing. While in Morytania, stay alert for any mention of the Myreque.")
					.addPlayer(HeadE.CALM_TALK, "The Myreque?")
					.addNPC(DREZEL, HeadE.CALM_TALK, "Yes. They're a band of freedom fighters. They fight to protect the humans trapped in Morytania from the tyranny of the vampyres. Their hope is to one day rid the region of all evil.")
					.addNPC(DREZEL, HeadE.CALM_TALK, "Veliaf, the leader of their Mort Myre group, is an old friend. I imagine they'd appreciate the help of someone like you, if you can find them.")
					.addPlayer(HeadE.CALM_TALK, "I'll keep an eye out for them.")
					.addNPC(DREZEL, HeadE.CALM_TALK, "Right, I think that's everything. Just the blessing itself now. Hold still a moment.")
					.addNPC(DREZEL, HeadE.CALM_TALK, "Good luck out there. Stay safe.")
			);
		}
		create();
	}
}

