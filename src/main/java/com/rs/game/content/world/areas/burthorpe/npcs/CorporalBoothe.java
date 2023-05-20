package com.rs.game.content.world.areas.burthorpe.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class CorporalBoothe {
    public static NPCClickHandler handleOps = new NPCClickHandler(new Object[] { 14921 }, e -> {
       switch(e.getOption()) {
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addNPC(14921, HeadE.CONFUSED, "Hello, sir. Can I help?")
                    .addOptions(ops -> {
                        ops.add("What do you do here?")
                                .addNPC(14921, HeadE.CALM_TALK, "This area has been teeming with trolls ever since they broke through the barricades, so I've been stationed here by the Imperial Guard to help assist any new recruits.")
                                .addNPC(14921, HeadE.CALM_TALK, "I also buy and sell combat gear, so if there's anything you need just let me know.");
                        ops.add("Can you give me combat advice?")
                                .addNPC(14921, HeadE.CALM_TALK, "The best advice I can give is to always have some food with you, for healing.")
                                .addNPC(14921, HeadE.CALM_TALK, "There are farming patches, fishing spots and hunting areas in Taverly, to the south, that you can use.");
                    }));
            case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "burthorpe_guard_quartermaster");
       }
    });
}
