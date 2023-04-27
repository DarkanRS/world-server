package com.rs.game.content.quests.heroesquest.dialogues;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.content.quests.heroesquest.HeroesQuest.GET_ITEMS;
import static com.rs.game.content.quests.heroesquest.HeroesQuest.QUEST_COMPLETE;

@PluginEventHandler
public class KatrineHeroesQuestD extends Conversation {
	private static final int NPC = 642;

	public KatrineHeroesQuestD(Player player) {
		super(player);
		Dialogue katrineImpressed = new Dialogue()
				.addNPC(NPC, HeadE.CALM_TALK, "I am impressed you got the master thieves' arm band.")
				.addPlayer(HeadE.HAPPY_TALKING, "Thanks!");
		switch (player.getQuestManager().getStage(Quest.HEROES_QUEST)) {
			case GET_ITEMS -> {
				if (player.getQuestManager().getAttribs(Quest.HEROES_QUEST).getB("got_armband_katrine")) {
					if (player.getInventory().containsItem(1579, 1))
						addNext(katrineImpressed);
					else {
						addPlayer(HeadE.HAPPY_TALKING, "I have lost my master thief's armband...");
						addNPC(NPC, HeadE.CALM_TALK, "Lucky I 'ave a spare ain't it? Don't lose it again.", () -> {
							player.getInventory().addItem(1579, 1);
						});
					}
					return;
				}
				if (player.getInventory().containsItem(1577, 1)) {
					addPlayer(HeadE.HAPPY_TALKING, "I have a candlestick now!");
					addNPC(NPC, HeadE.CALM_TALK, "Wow... is... it REALLY it? This really is a FINE bit of thievery. Us thieves have been trying to get hold" +
							" of this one for a while! You wanted to be ranked as a master thief didn't you? Well, I guess this just about ranks as good enough!");
					addSimple("Katrine gives you a master thief armband.", () -> {
						player.getInventory().removeItems(new Item(1577, 1));
						player.getInventory().addItem(1579, 1);
						player.getQuestManager().getAttribs(Quest.HEROES_QUEST).setB("got_armband_katrine", true);
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
