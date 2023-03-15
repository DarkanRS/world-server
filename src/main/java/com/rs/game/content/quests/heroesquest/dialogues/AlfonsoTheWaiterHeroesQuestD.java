package com.rs.game.content.quests.heroesquest.dialogues;

import static com.rs.game.content.quests.heroesquest.HeroesQuest.GET_ITEMS;

import com.rs.game.content.quests.shieldofarrav.ShieldOfArrav;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class AlfonsoTheWaiterHeroesQuestD extends Conversation {
	private static final int NPC = 793;

	public AlfonsoTheWaiterHeroesQuestD(Player player) {
		super(player);
		Dialogue shop = new Dialogue()
				.addNPC(NPC, HeadE.CALM_TALK, "Welcome to the Shrimp and Parrot. Would you like to order, sir?")
				.addNext(() -> {
					ShopsHandler.openShop(player, "alfonso_waiter_shop");
				});
		switch (player.getQuestManager().getStage(Quest.HEROES_QUEST)) {
			case GET_ITEMS -> {
				if (ShieldOfArrav.isPhoenixGang(player))
					addNext(new Dialogue()
							.addNPC(NPC, HeadE.CALM_TALK, "Welcome to the Shrimp and Parrot. Would you like to order, sir?")
							.addPlayer(HeadE.HAPPY_TALKING, "Do you sell Gherkins?")
							.addNPC(NPC, HeadE.CALM_TALK, "Hmmmm Gherkins eh? Ask Charlie the cook, round the back. He may have some 'gherkins' for you!")
							.addSimple("Alfonse winks at you.")
					);
				else
					addNext(shop);
			}
			default -> {
				addNext(shop);
			}
		}
	}
}
