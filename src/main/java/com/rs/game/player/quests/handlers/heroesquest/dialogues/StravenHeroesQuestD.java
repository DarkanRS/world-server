package com.rs.game.player.quests.handlers.heroesquest.dialogues;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.quests.Quest;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.player.quests.handlers.heroesquest.HeroesQuest.*;

@PluginEventHandler
public class StravenHeroesQuestD extends Conversation {
	private static final int NPC = 644;
	public StravenHeroesQuestD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.HEROES_QUEST)) {
            case GET_ITEMS -> {
                addPlayer(HeadE.HAPPY_TALKING, "How would I go about getting a Master Thief armband?");
                addNPC(NPC, HeadE.CALM_TALK, "Ooh... tricky stuff. Took me YEARS to get that rank. Well, what some of the more aspiring thieves in our gang are " +
                        "working on right now is to steal some very valuable candlesticks from Scarface Pete – the pirate leader on Karamja.");
                addNPC(NPC, HeadE.CALM_TALK, "His security is excellent, and the target very valuable, so that might be enough to get you the rank. Go talk to our" +
                        " man Alfonse, the waiter at the Shrimp and Parrot. Use the secret key word 'gherkin' to show you're one of us.");
            }
            case QUEST_COMPLETE ->  {
                addNPC(NPC, HeadE.CALM_TALK, "Impressive work on getting the master thieves' armband.");
                addPlayer(HeadE.HAPPY_TALKING, "Thanks!");
            }
		}
	}

}
