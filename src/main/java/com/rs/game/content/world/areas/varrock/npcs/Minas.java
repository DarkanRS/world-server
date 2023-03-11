package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Minas {
    public static NPCClickHandler handleHistorianMinas = new NPCClickHandler(new Object[] { 5931 }, e -> {
        if(e.getOption().equalsIgnoreCase("talk-to"))
            e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
                {
                    addPlayer(HeadE.CHEERFUL, "Hello.");
                    addOptions("What would you like to say?", new Options() {
                        @Override
                        public void create() {
                            if (e.getPlayer().isQuestComplete(Quest.SHIELD_OF_ARRAV) && !((boolean)e.getPlayer().get("claimedArravLamp")))
                                option("About Shield Of Arrav...", new Dialogue()
                                        .addNPC(5931, HeadE.HAPPY_TALKING, "Thank you for returning the shield")
                                        .addSimple("A lamp is placed in your hand")
                                        .addNext(() -> {
                                            e.getPlayer().getInventory().addItem(4447, 1);
                                            e.getPlayer().save("claimedArravLamp", true);
                                        }));
                            else if(e.getPlayer().isQuestComplete(Quest.SHIELD_OF_ARRAV))
                                option("About Shield Of Arrav...", new Dialogue()
                                        .addNPC(5931, HeadE.HAPPY_TALKING, "Thank you for returning the shield"));
                            else
                                option("About Shield Of Arrav...", new Dialogue()
                                        .addPlayer(HeadE.HAPPY_TALKING, "There is nothing to say."));
                            option("Farewell.");
                        }
                    });
                    create();
                }
            });
    });
}
