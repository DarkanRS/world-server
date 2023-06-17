package com.rs.game.content.world.areas.falador.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Egil extends Conversation {
    private static final int npcId = 6642;

    public static NPCClickHandler Egil = new NPCClickHandler(new Object[]{ npcId }, e -> {
        switch (e.getOption()) {
            
            case "Talk-to" -> e.getPlayer().startConversation(new Egil(e.getPlayer()));
        }
    });

    public Egil(Player player) {
        super(player);
        addNPC(npcId, HeadE.HAPPY_TALKING, "Welcome to the Artisan's Workshop. " );
        addNPC(npcId, HeadE.HAPPY_TALKING,  "We've got normal smithing in the front, and burial smithing in the back! " );
        addNPC(npcId, HeadE.HAPPY_TALKING,  "Also, I'll occasionally stop by and hand you some ceremonial sword plans to complete for extra experience.");
        addOptions(new Options() {
            @Override
            public void create() {

                option("What's a ceremonial sword?", new Dialogue()
                        .addPlayer(HeadE.CALM_TALK, "What's a ceremonial sword?")
                        .addNPC(npcId, HeadE.HAPPY_TALKING,"A ceremonial sword distinguishes the wielder from his peers. They are items of beauty and craftsmanship that are favoured by officers and the very rich.")
                        .addPlayer(HeadE.CALM_TALK, "I'm guessing they're expensive?")
                        .addNPC(npcId, HeadE.HAPPY_TALKING,"Quite so. We use only the purest metal to get the most malleable and perfect swords.")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "Each blade requires a lot of ore, which we smelt into a pure, dense metal.")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "But if we give you some plans, we'll supply all the materials so you'll be able to freely smith your ceremonial sword!")
                );
                option("Who are you?", new Dialogue()
                        .addPlayer(HeadE.CALM_TALK, "Who are you?")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "I am Egil, ceremonial sword specialist and, apparently, one of the workshop's more senior artisans.")
                        .addPlayer(HeadE.CONFUSED, "Apparently?")
                        .addNPC(npcId, HeadE.HAPPY_TALKING,"I don't hold much stock in titles. They garnish the ego, but are rather impractical.")
                );
            }


        });
    }


}
