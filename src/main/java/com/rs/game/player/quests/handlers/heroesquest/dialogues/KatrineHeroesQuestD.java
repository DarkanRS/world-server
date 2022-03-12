package com.rs.game.player.quests.handlers.heroesquest.dialogues;

import static com.rs.game.player.quests.handlers.heroesquest.HeroesQuest.GET_ITEMS;
import static com.rs.game.player.quests.handlers.heroesquest.HeroesQuest.QUEST_COMPLETE;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.quests.Quest;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class KatrineHeroesQuestD extends Conversation {
	private static final int NPC = 642;

	public KatrineHeroesQuestD(Player p) {
		super(p);
		Dialogue katrineImpressed = new Dialogue()
				.addNPC(NPC, HeadE.CALM_TALK, "I am impressed you got the master thieves' arm band.")
				.addPlayer(HeadE.HAPPY_TALKING, "Thanks!");
		switch (p.getQuestManager().getStage(Quest.HEROES_QUEST)) {
			case GET_ITEMS -> {
				if (p.getQuestManager().getAttribs(Quest.HEROES_QUEST).getB("got_armband_katrine")) {
					if (p.getInventory().containsItem(1579, 1))
						addNext(katrineImpressed);
					else {
						addPlayer(HeadE.HAPPY_TALKING, "I have lost my master thief's armband...");
						addNPC(NPC, HeadE.CALM_TALK, "Lucky I 'ave a spare ain't it? Don't lose it again.", () -> {
							p.getInventory().addItem(1579, 1);
						});
					}
					return;
				}
				if (p.getInventory().containsItem(1577, 1)) {
					addPlayer(HeadE.HAPPY_TALKING, "I have a candlestick now!");
					addNPC(NPC, HeadE.CALM_TALK, "Wow... is... it REALLY it? This really is a FINE bit of thievery. Us thieves have been trying to get hold" +
							" of this one for a while! You wanted to be ranked as a master thief didn't you? Well, I guess this just about ranks as good enough!");
					addSimple("Katrine gives you a master thief armband.", () -> {
						p.getInventory().removeItems(new Item(1577, 1));
						p.getInventory().addItem(1579, 1);
						p.getQuestManager().getAttribs(Quest.HEROES_QUEST).setB("got_armband_katrine", true);
					});
					return;
				}
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
				addNext(katrineImpressed);
			}
		}
	}
}
