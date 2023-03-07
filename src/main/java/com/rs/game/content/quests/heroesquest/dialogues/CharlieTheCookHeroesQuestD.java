package com.rs.game.content.quests.heroesquest.dialogues;

import static com.rs.game.content.quests.heroesquest.HeroesQuest.GET_ITEMS;

import com.rs.game.content.quests.shieldofarrav.ShieldOfArrav;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class CharlieTheCookHeroesQuestD extends Conversation {
	private static final int NPC = 794;

	public CharlieTheCookHeroesQuestD(Player player) {
		super(player);
		switch (player.getQuestManager().getStage(Quest.HEROES_QUEST)) {
			case GET_ITEMS -> {
				if (ShieldOfArrav.isPhoenixGang(player)) {
					addNPC(NPC, HeadE.CALM_TALK, "Hey! What are you doing back here?");
					addPlayer(HeadE.HAPPY_TALKING, "I'm looking for a gherkin...");
					addNPC(NPC, HeadE.CALM_TALK, "Aaaaaah... a fellow Phoenix! So, tell me compadre... what brings you to sunny Brimhaven?");
					addPlayer(HeadE.HAPPY_TALKING, "I want to steal Scareface Pete's candlesticks.");
					addNPC(NPC, HeadE.CALM_TALK, "Ah yes, of course. The candlesticks. Well, I have to be honest with you compadre, we haven't " +
							"made much progress in that task ourselves so far. We can however offer a little assistance.");
					addNPC(NPC, HeadE.CALM_TALK, "We have our own gaurd in their ranks who is willing to take a bribe.", () -> {
						player.getQuestManager().getAttribs(Quest.HEROES_QUEST).setB("phoenix_trick", true);
					});
					addPlayer(HeadE.HAPPY_TALKING, "Okay thanks!");
				}
			}
			default -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello");
				addNPC(NPC, HeadE.CALM_TALK, "What are you doing back here? You should be back here.");
				addPlayer(HeadE.HAPPY_TALKING, "Okay, I'll leave...");
			}
		}
	}
}
