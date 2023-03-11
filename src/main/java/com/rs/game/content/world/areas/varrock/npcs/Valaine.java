package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Valaine {
    public static NPCClickHandler handleValaineChampsGuild = new NPCClickHandler(new Object[] { 536 }, e -> {
        int NPC = e.getNPCId();
        if (e.getOption().equalsIgnoreCase("talk-to"))
            e.getPlayer().startConversation(new Dialogue()
                    .addNPC(NPC, HeadE.CALM_TALK, "Hello there. Want to have a look at what we're selling today?")
                    .addOptions("Choose an option:", new Options() {
                        @Override
                        public void create() {
                            option("Yes, please.", new Dialogue()
                                    .addPlayer(HeadE.HAPPY_TALKING, "Yes, please.")
                                    .addNext(() -> ShopsHandler.openShop(e.getPlayer(), "valaines_shop_of_champions")));
                            option("How should I use your shop?", new Dialogue()
                                    .addPlayer(HeadE.HAPPY_TALKING, "How should I use your shop?")
                                    .addNPC(NPC, HeadE.CALM_TALK, "I'm glad you ask! You can buy as many of the items stocked as you wish. You can also sell most items to the shop.")
                                    .addNext(() -> ShopsHandler.openShop(e.getPlayer(), "valaines_shop_of_champions")));
                            option("No, thank you.", new Dialogue().addPlayer(HeadE.HAPPY_TALKING, "No, thank you.").addNPC(NPC, HeadE.CALM_TALK, "Well, alright."));
                        }
                    }));
        if (e.getOption().equalsIgnoreCase("trade"))
            ShopsHandler.openShop(e.getPlayer(), "valaines_shop_of_champions");
    });
}
