package com.rs.game.content.quests.heroesquest.dialogues;

import static com.rs.game.content.quests.heroesquest.HeroesQuest.GET_ITEMS;

import com.rs.game.content.quests.shieldofarrav.ShieldOfArrav;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class TrobertHeroesQuestD extends Conversation {
	private static final int NPC = 1884;

	public TrobertHeroesQuestD(Player player) {
		super(player);
		Dialogue intro = new Dialogue()
				.addNPC(NPC, HeadE.CALM_TALK, "Welcome to our Brimhaven headquarters. I'm Trobert and I'm in charge here.")
				.addPlayer(HeadE.HAPPY_TALKING, "Pleased to meet you.")
				.addNPC(NPC, HeadE.CALM_TALK, "Likewise.");
		switch (player.getQuestManager().getStage(Quest.HEROES_QUEST)) {
			case GET_ITEMS -> {
				if (ShieldOfArrav.isBlackArmGang(player)) {
					if (player.getInventory().containsItem(1584, 1)) {
						addNPC(NPC, HeadE.CALM_TALK, "Good luck getting those candles...", () -> {
									player.getQuestManager().getAttribs(Quest.HEROES_QUEST).setB("black_arm_trick", true);
								});
						addPlayer(HeadE.CALM_TALK, "Thanks!");
						return;
					}
					addNext(intro
							.addPlayer(HeadE.HAPPY_TALKING, "So can you help me get Scarface Pete's candlesticks?")
							.addNPC(NPC, HeadE.CALM_TALK, "Well, we have made some progress there. We know that one of the only keys to Pete's treasure " +
									"room is carried by Grip, the head guard, so we thought it might be good to get close to him somehow")
							.addNPC(NPC, HeadE.CALM_TALK, "Grip was taking on a new deputy called Hartigen, an Asgarnian Black Knight who was deserting the " +
									"Black Knight Fortress and seeking new employment here on Brimhaven.")
							.addNPC(NPC, HeadE.CALM_TALK, "We managed to waylay him on the journey here, and steal his ID papers. Now all we need is to find " +
									"somebody willing to impersonate him and take the deputy role to get that key for us.")
							.addOptions("Choose an option:", new Options() {
								@Override
								public void create() {
									option("I volunteer to undertake that mission!", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "I volunteer to undertake that mission!")
											.addNPC(NPC, HeadE.CALM_TALK, "Good good. Well, here's the ID papers, take them and introduce yourself to " +
													"the guards at Scarface Pete's mansion, we'll have that treasure in no time.", () -> {
												player.getInventory().addItem(1584, 1);
												player.getQuestManager().getAttribs(Quest.HEROES_QUEST).setB("black_arm_trick", true);
											})
									);
									option("Well, good luck then.", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "Well, good luck then.")
											.addNPC(NPC, HeadE.CALM_TALK, "Someone will show up eventually.")
									);
								}
							})
					);
				} else
					addNext(intro);
			}
			default -> {
				addNext(intro);
			}
		}
	}
}
