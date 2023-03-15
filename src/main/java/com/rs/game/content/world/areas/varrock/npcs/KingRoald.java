package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.content.quests.shieldofarrav.KingRoaldShieldOfArravD;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class KingRoald {
    public static NPCClickHandler handleKingRoald = new NPCClickHandler(new Object[] { 648 }, new String[] { "Talk-to" }, e -> {
        e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
            {
                addPlayer(HeadE.CHEERFUL, "Hello.");
                if (!e.getPlayer().isQuestComplete(Quest.SHIELD_OF_ARRAV))
                    addOptions("What would you like to say?", new Options() {
                        @Override
                        public void create() {
                            option("About Shield Of Arrav...", new KingRoaldShieldOfArravD(player).getStart());
                            option("Farewell.");
                        }
                    });
                else {
                    addNPC(648, HeadE.HAPPY_TALKING, "Thank you for your good work adventurer!");
                    addPlayer(HeadE.HAPPY_TALKING, "You are welcome.");
                }
                create();
            }
        });
    });
}
