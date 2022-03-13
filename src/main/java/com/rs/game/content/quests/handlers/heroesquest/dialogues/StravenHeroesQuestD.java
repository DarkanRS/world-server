package com.rs.game.content.quests.handlers.heroesquest.dialogues;

import static com.rs.game.content.quests.handlers.heroesquest.HeroesQuest.GET_ITEMS;
import static com.rs.game.content.quests.handlers.heroesquest.HeroesQuest.QUEST_COMPLETE;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class StravenHeroesQuestD extends Conversation {
	private static final int NPC = 644;

	public StravenHeroesQuestD(Player p) {
		super(p);
		Dialogue impressedStraven = new Dialogue()
				.addNPC(NPC, HeadE.CALM_TALK, "Impressive work on getting the master thieves' armband.")
				.addPlayer(HeadE.HAPPY_TALKING, "Thanks!");
		switch (p.getQuestManager().getStage(Quest.HEROES_QUEST)) {
			case GET_ITEMS -> {
				if (p.getQuestManager().getAttribs(Quest.HEROES_QUEST).getB("got_armband_straven")) {
					if (p.getInventory().containsItem(1579, 1))
						addNext(impressedStraven);
					else {
						addPlayer(HeadE.SAD, "I lost the armband!");
						addNPC(NPC, HeadE.CALM_TALK, "Oh, don't lose it again");
						addSimple("He hands you another...", () -> {
							p.getInventory().addItem(1579, 1);
						});
					}
					return;
				}
				if (p.getInventory().containsItem(1577, 1)) {
					addPlayer(HeadE.HAPPY_TALKING, "I have retrieved a candlestick!");
					addNPC(NPC, HeadE.CALM_TALK, "Hmmm. Not bad, not bad. Let's see it, make sure it's genuine.");
					addSimple("You hand Straven the candlestick.");
					addPlayer(HeadE.HAPPY_TALKING, "So is this enough to get me a Master Thief armband?");
					addNPC(NPC, HeadE.CALM_TALK, "Hmm... I dunno... Aww, go on then. I suppose I'm in a generous mood today.");
					addSimple("Straven hands you a Master Thief armband.", () -> {
						p.getInventory().removeItems(new Item(1577, 1));
						p.getInventory().addItem(1579, 1);
						p.getQuestManager().getAttribs(Quest.HEROES_QUEST).setB("got_armband_straven", true);
					});
					return;
				}
				addPlayer(HeadE.HAPPY_TALKING, "How would I go about getting a Master Thief armband?");
				addNPC(NPC, HeadE.CALM_TALK, "Ooh... tricky stuff. Took me YEARS to get that rank. Well, what some of the more aspiring thieves in our gang are " +
						"working on right now is to steal some very valuable candlesticks from Scarface Pete – the pirate leader on Karamja.");
				addNPC(NPC, HeadE.CALM_TALK, "His security is excellent, and the target very valuable, so that might be enough to get you the rank. Go talk to our" +
						" man Alfonse, the waiter at the Shrimp and Parrot. Use the secret key word 'gherkin' to show you're one of us.");
			}
			case QUEST_COMPLETE -> {
				addNext(impressedStraven);
			}
		}
	}

}
