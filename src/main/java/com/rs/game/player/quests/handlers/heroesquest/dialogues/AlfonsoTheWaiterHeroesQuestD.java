package com.rs.game.player.quests.handlers.heroesquest.dialogues;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.handlers.heroesquest.HeroesQuest;
import com.rs.game.player.quests.handlers.shieldofarrav.ShieldOfArrav;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.utils.shop.ShopsHandler;

import static com.rs.game.player.quests.handlers.heroesquest.HeroesQuest.*;

@PluginEventHandler
public class AlfonsoTheWaiterHeroesQuestD extends Conversation {
	private static final int NPC = 793;
	public AlfonsoTheWaiterHeroesQuestD(Player p) {
		super(p);
        Dialogue shop = new Dialogue()
                .addNPC(NPC, HeadE.CALM_TALK, "Welcome to the Shrimp and Parrot. Would you like to order, sir?")
                .addNext(() -> {
                    ShopsHandler.openShop(p, "alfonso_waiter_shop");
                });
		switch(p.getQuestManager().getStage(Quest.HEROES_QUEST)) {
            case GET_ITEMS -> {
                if (ShieldOfArrav.isPhoenixGang(p))
                    p.startConversation(new Dialogue()
                            .addNPC(NPC, HeadE.CALM_TALK, "Welcome to the Shrimp and Parrot. Would you like to order, sir?")
                            .addPlayer(HeadE.HAPPY_TALKING, "Do you sell Gherkins?")
                            .addNPC(NPC, HeadE.CALM_TALK, "Hmmmm Gherkins eh? Ask Charlie the cook, round the back. He may have some 'gherkins' for you!")
                            .addSimple("Alfonse winks at you.")
                    );
                else
                    p.startConversation(shop);
            }
            default ->  {
                p.startConversation(shop);
            }
		}
	}
}
