package com.rs.game.player.quests.handlers.heroesquest.dialogues;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.quests.Quest;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.player.quests.handlers.heroesquest.HeroesQuest.GET_ITEMS;
import static com.rs.game.player.quests.handlers.heroesquest.HeroesQuest.QUEST_COMPLETE;

@PluginEventHandler
public class KatrineHeroesQuestD extends Conversation {
	private static final int NPC = 642;
	public KatrineHeroesQuestD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.HEROES_QUEST)) {
            case GET_ITEMS -> {
                addPlayer(HeadE.HAPPY_TALKING, "Hey");
                addNPC(NPC, HeadE.CALM_TALK, "Hey");
                addPlayer(HeadE.HAPPY_TALKING, "Is there any way I can get the rank of master thief?");
                addNPC(NPC, HeadE.CALM_TALK, "Master thief? Ain't we the ambitious one! Well, you're gonna have to do something pretty amazing to earn that rank.");
                addPlayer(HeadE.HAPPY_TALKING, "Anything you can suggest?");
                addNPC(NPC, HeadE.CALM_TALK, "Well, some of the MOST coveted prizes in thiefdom right now are in the pirate town of Brimhaven on Karamja. " +
                        "The pirate leader Scarface Pete keeps an extremely valuable candlestick in his Brimhaven mansion. His security is VERY good. ");
                addNPC(NPC, HeadE.CALM_TALK, "We, of course, have gang members in a town like Brimhaven who may be able to help you. Visit our hideout in " +
                        "south-east Brimhaven. To get in you will need to tell them the secret password 'Four leaved clover'.");
            }
            case QUEST_COMPLETE -> {
                addNPC(NPC, HeadE.CALM_TALK, "I am impressed you got the master thieves' arm band.");
                addPlayer(HeadE.HAPPY_TALKING, "Thanks!");
            }
        }
	}
}
