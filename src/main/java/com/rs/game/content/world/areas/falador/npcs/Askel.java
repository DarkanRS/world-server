package com.rs.game.content.world.areas.falador.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Askel extends Conversation {
    private static final int npcId = 6663;

    public static NPCClickHandler Askel = new NPCClickHandler(new Object[]{ npcId }, e -> {
        switch (e.getOption()) {
            
            case "Talk-to" -> e.getPlayer().startConversation(new Askel(e.getPlayer()));
        }
    });

    public Askel(Player player) {
        super(player);
        addNPC(npcId, HeadE.HAPPY_TALKING, "Welcome. What brings you to the home of the artisans?");
        addOptions(new Options() {
            @Override
            public void create() {

                option("What's an artisan?", new Dialogue()
                        .addPlayer(HeadE.CALM_TALK, "What's an artisan?")
                        .addNPC(npcId, HeadE.HAPPY_TALKING,"Artisans are a collective of skilled smiths who work towards upholding quality as their principle ethos. ")
                        .addNPC(npcId, HeadE.HAPPY_TALKING,"Smithing can be seen as a coarse, unloving undertaking...bashing out platebodies as quickly as you can.")
                        .addNPC(npcId, HeadE.HAPPY_TALKING,"It's not like that here. Recently, we've opened our doors to humans.")
                        .addNPC(npcId, HeadE.HAPPY_TALKING,"Some of our stauncher members aren't happy about this, but if you work hard I'm sure you'll earn our respect. ")
                );
                option("Tell me more about yourself.", new Dialogue()
                        .addPlayer(HeadE.CALM_TALK, "Tell me more about yourself.")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "I built the furnace that rather dominates this room. Took me four and a half years; carved the rock myself. ")
                        .addNPC(npcId, HeadE.HAPPY_TALKING,"If you need any pointers on how to use it, talk to Egil. ")
                        .addNPC(npcId, HeadE.HAPPY_TALKING,"I don't do much actual smithing any more. I mainly potter around the workshop, helping where I can.")
                );

            }


        });
    }


}
