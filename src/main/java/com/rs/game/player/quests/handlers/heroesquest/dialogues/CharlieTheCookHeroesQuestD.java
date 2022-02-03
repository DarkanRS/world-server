package com.rs.game.player.quests.handlers.heroesquest.dialogues;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.handlers.heroesquest.HeroesQuest;
import com.rs.game.player.quests.handlers.shieldofarrav.ShieldOfArrav;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.player.quests.handlers.heroesquest.HeroesQuest.*;

@PluginEventHandler
public class CharlieTheCookHeroesQuestD extends Conversation {
	private static final int NPC = 794;
	public CharlieTheCookHeroesQuestD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.HEROES_QUEST)) {
            case GET_ITEMS -> {
                if(ShieldOfArrav.isPhoenixGang(p)) {
                    addNPC(NPC, HeadE.CALM_TALK, "Hey! What are you doing back here?");
                    addPlayer(HeadE.HAPPY_TALKING, "I'm looking for a gherkin...");
                    addNPC(NPC, HeadE.CALM_TALK, "Aaaaaah... a fellow Phoenix! So, tell me compadre... what brings you to sunny Brimhaven?");
                    addPlayer(HeadE.HAPPY_TALKING, "I want to steal Scareface Pete's candlesticks.");
                    addNPC(NPC, HeadE.CALM_TALK, "Ah yes, of course. The candlesticks. Well, I have to be honest with you compadre, we haven't " +
                            "made much progress in that task ourselves so far. We can however offer a little assistance.");
                    addNPC(NPC, HeadE.CALM_TALK, "We have our own gaurd in their ranks who is willing to take a bribe.", ()->{
                        p.getQuestManager().getAttribs(Quest.HEROES_QUEST).setB("phoenix_trick", true);
                    });
                    addPlayer(HeadE.HAPPY_TALKING, "Okay thanks!");
                }
            }
            default ->  {
                addPlayer(HeadE.HAPPY_TALKING, "Hello");
                addNPC(NPC, HeadE.CALM_TALK, "What are you doing back here? You should be back here.");
                addPlayer(HeadE.HAPPY_TALKING, "Okay, I'll leave...");
            }
		}
	}
}
