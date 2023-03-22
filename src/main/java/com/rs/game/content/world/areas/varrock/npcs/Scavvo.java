package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Scavvo {
    public static NPCClickHandler handleValaineChampsGuild = new NPCClickHandler(new Object[] { 537 }, e -> {
        int NPC = e.getNPCId();
        if (e.getOption().equalsIgnoreCase("talk-to"))
            e.getPlayer().startConversation(new Dialogue()
                    .addNPC(NPC, HeadE.CALM_TALK, "Hello there. Want to have a look at what we're selling today?")
                    .addOptions("Choose an option:", new Options() {
                        @Override
                        public void create() {
                            option("Yes, please.", new Dialogue()
                                    .addPlayer(HeadE.HAPPY_TALKING, "Yes, please.")
                                    .addNext(() -> ShopsHandler.openShop(e.getPlayer(), "scavvos_rune_shop")));
                             option("No, thank you.", new Dialogue().addPlayer(HeadE.HAPPY_TALKING, "No, thank you.").addNPC(NPC, HeadE.CALM_TALK, "Well, alright."));
                        }
                    }));
        if (e.getOption().equalsIgnoreCase("trade"))
            ShopsHandler.openShop(e.getPlayer(), "scavvos_rune_shop");
    });
}
