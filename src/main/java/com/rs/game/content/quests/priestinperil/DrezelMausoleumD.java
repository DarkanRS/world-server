package com.rs.game.content.quests.priestinperil;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class DrezelMausoleumD extends Conversation {
	private static final int Drezel = 1049;

	public static NPCClickHandler HandleDrezel = new NPCClickHandler(new Object[] { Drezel }, new String[] { "Talk-to" },  e -> {
			e.getPlayer().startConversation(new DrezelMausoleumD(e.getPlayer()));
	});

	public DrezelMausoleumD(Player player) {
		super(player);
		int remainingEssence = Utils.clampI(50 - player.getQuestManager().getAttribs(Quest.PRIEST_IN_PERIL).getI("essence"), 0, 50);
		if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 9) {
			player.startConversation(new Dialogue()
					.addNPC(Drezel, HeadE.CALM_TALK, "Ah, " + player.getDisplayName() + ". Glad you made it. Things are worse than I feared down here. I'm not sure if I will be able to repair the damage.")
					.addPlayer(HeadE.CALM_TALK, "Why, what's happened?")
					.addNPC(Drezel, HeadE.CALM_TALK, "Sadly, with the guard dog gone, there was nothing to stop those Zamorakians from entering the mausoleum. From what I can tell, they have used some kind of evil potion to pollute the Salve.")
					.addNPC(Drezel, HeadE.CALM_TALK, "The well here is built right over the source of the river. That means that their potion will spread all the way down the Salve, disrupting the blessings placed upon it.")
					.addNPC(Drezel, HeadE.CALM_TALK, "Before long, there will be nothing to stop the vampyres from invading Misthalin at their leisure.")
					.addPlayer(HeadE.CALM_TALK, "What can we do to prevent that?")
					.addNPC(Drezel, HeadE.CALM_TALK, "This passage is currently the only route between Morytania and Misthalin. The barrier here draws power from the river.")
					.addNPC(Drezel, HeadE.CALM_TALK, "I have managed to reinforce the barrier, but I must continue focussing on it to keep it intact. Although the passage is safe for now, I do not know how long I can keep it that way.")
					.addNPC(Drezel, HeadE.CALM_TALK, "This passage could be the least of our worries soon though. Before long, the vampyres will be able to cross at any point of the river. We won't be able to reinforce all of it.")
					.addPlayer(HeadE.CALM_TALK, "Couldn't you bless the river to purify it? Like you did with the water I took from the well?")
					.addNPC(Drezel, HeadE.CALM_TALK, "No, that would not work. The power I have from Saradomin is not great enough to cleanse an entire river of this foul Zamorakian pollutant.")
					.addNPC(Drezel, HeadE.CALM_TALK, "However, I do have one other idea.")
					.addPlayer(HeadE.CALM_TALK, "What's that?")
					.addNPC(Drezel, HeadE.CALM_TALK, "I believe we might be able to soak up the evil magic that the potion has released into the river.")
					.addNPC(Drezel, HeadE.CALM_TALK, "I'm sure you know of runestones, used by mages to power their spells. The essence used to create these stones absorbs magical potential from runic altars to be released later.")
					.addPlayer(HeadE.CALM_TALK, "And you think we could use some rune essence to absorb the magic they've released into the Salve?")
					.addNPC(Drezel, HeadE.CALM_TALK, "Exactly. If you could bring me fifty essence, I should be able to reverse the damage done. Be quick though. The longer we wait, the worse things will get.", () -> player.getQuestManager().setStage(Quest.PRIEST_IN_PERIL, 10))
			);
		}
		if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 10) {
			if (player.getInventory().containsItem(1436) || player.getInventory().containsItem(7936)) {
				int essence = player.getInventory().getAmountOf(1436);
				int pureEssence = player.getInventory().getAmountOf(7936);
				if (remainingEssence == 0) {
					player.startConversation(new Dialogue()
							.addNPC(Drezel, HeadE.CALM_TALK, "Excellent! That should do it! I will bless these stones and place them within the well. With the river safe, Misthalin should be protected from the vampyres once more!")
							.addNPC(Drezel, HeadE.CALM_TALK, "Please take this dagger. It has been handed down within my family for generations and is filled with the power of Saradomin.")
							.addNPC(Drezel, HeadE.CALM_TALK, "You will find that it has the ability to prevent werewolves from adopting their wolf form in combat. Hopefully it comes in useful.", () -> player.getQuestManager().setStage(Quest.PRIEST_IN_PERIL, 11))
					);
					player.getQuestManager().completeQuest(Quest.PRIEST_IN_PERIL);
					player.sendMessage("Drezel blesses you, allowing you to pass through the barrier into Morytania.");
					return;
				}
				if (remainingEssence > 0) {
					if (essence <= remainingEssence) {
						player.getInventory().deleteItem(1436, essence);
						player.getInventory().deleteItem(7936, pureEssence);
						player.getQuestManager().getAttribs(Quest.PRIEST_IN_PERIL).setI("essence", essence + Utils.clampI(player.getQuestManager().getAttribs(Quest.PRIEST_IN_PERIL).getI("essence"), 0, 50));
						player.getQuestManager().getAttribs(Quest.PRIEST_IN_PERIL).setI("essence", pureEssence + Utils.clampI(player.getQuestManager().getAttribs(Quest.PRIEST_IN_PERIL).getI("essence"), 0, 50));
						remainingEssence = Utils.clampI(50 - player.getQuestManager().getAttribs(Quest.PRIEST_IN_PERIL).getI("essence"), 0, 50);
						player.startConversation(new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "I brought you some rune essence.")
								.addNPC(Drezel, HeadE.CALM_TALK, "Quickly, give them to me!")
								.addNPC(Drezel, HeadE.CALM_TALK, "Thank you. I need " + remainingEssence + " more.")
						);
					} else {
						player.getInventory().deleteItem(1436, remainingEssence);
						player.getInventory().deleteItem(7936, remainingEssence);
						player.getQuestManager().getAttribs(Quest.PRIEST_IN_PERIL).setI("essence", 50);
						player.startConversation(new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "I brought you some more essence.")
								.addNPC(Drezel, HeadE.CALM_TALK, "Quickly, give them to me!")
								.addNPC(Drezel, HeadE.CALM_TALK, "Excellent! That should do it! I will bless these stones and place them within the well. With the river safe, Misthalin should be protected from the vampyres once more!")
								.addNPC(Drezel, HeadE.CALM_TALK, "Please take this dagger. It has been handed down within my family for generations and is filled with the power of Saradomin.")
								.addNPC(Drezel, HeadE.CALM_TALK, "You will find that it has the ability to prevent werewolves from adopting their wolf form in combat. Hopefully it comes in useful.", () -> player.getQuestManager().setStage(Quest.PRIEST_IN_PERIL, 11))
						);
						player.getQuestManager().completeQuest(Quest.PRIEST_IN_PERIL);
						player.sendMessage("Drezel blesses you, allowing you to pass through the barrier into Morytania.");
						return;
					}
				}
				if (Utils.clampI(player.getQuestManager().getAttribs(Quest.PRIEST_IN_PERIL).getI("essence"), 0, 50) > 0) {
					player.startConversation(new Dialogue()
							.addPlayer(HeadE.CALM_TALK, "How much more essence do I need to bring you ?")
							.addNPC(Drezel, HeadE.CALM_TALK, "I need " + remainingEssence + " more.")
					);
				}
			} else {
				player.startConversation(new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "What am I supposed to do again?")
						.addNPC(Drezel, HeadE.CALM_TALK, "Bring me fifty rune essence so that I can undo the damage done by those Zamorakians.")
				);
			}
		}
		if (player.getQuestManager().isComplete(Quest.PRIEST_IN_PERIL)){
			player.startConversation(new Dialogue()
					.addPlayer(HeadE.CALM_TALK, "So can I pass through that barrier now?")
					.addNPC(Drezel, HeadE.CALM_TALK, "Into Morytania? Yes, you can. The barrier will prevent the servants of Zamorak from entering Misthalin, but it is safe for those blessed by Saradomin.")
					.addNPC(Drezel, HeadE.CALM_TALK, "I must warn you though, Morytania is an evil land. The region is filled with creatures and monsters more terrifying than any you'll find on this side of the Salve.")
					.addNPC(Drezel, HeadE.CALM_TALK, "Worst of all are the vampyres, the rulers of Morytania. Be very cautious of their kind. Many are completely immune to human weaponry. Those that aren't, are still very hard to kill.")
					.addPlayer(HeadE.CALM_TALK, "I'll be careful. Anything else I should know?")
					.addNPC(Drezel, HeadE.CALM_TALK, "The first settlement you'll likely come across is Canifis, a town of werewolves. They probably won't attack you unless provoked, but you should still be cautious.")
					.addNPC(Drezel, HeadE.CALM_TALK, "You should keep your Wolfbane dagger to hand. It will prevent them from taking on their wolf form.")
					.addPlayer(HeadE.CALM_TALK, "I see. Thanks.")
					.addNPC(Drezel, HeadE.CALM_TALK, "One last thing. While in Morytania, stay alert for any mention of the Myreque.")
					.addPlayer(HeadE.CALM_TALK, "The Myreque?")
					.addNPC(Drezel, HeadE.CALM_TALK, "Yes. They're a band of freedom fighters. They fight to protect the humans trapped in Morytania from the tyranny of the vampyres. Their hope is to one day rid the region of all evil.")
					.addNPC(Drezel, HeadE.CALM_TALK, "Veliaf, the leader of their Mort Myre group, is an old friend. I imagine they'd appreciate the help of someone like you, if you can find them.")
					.addPlayer(HeadE.CALM_TALK, "I'll keep an eye out for them.")
					.addNPC(Drezel, HeadE.CALM_TALK, "Right, I think that's everything. Just the blessing itself now. Hold still a moment.")
					.addNPC(Drezel, HeadE.CALM_TALK, "Good luck out there. Stay safe.")
			);
		}
	}
}

