package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.quests.whatliesbelow.WhatLiesBelow;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Zaff extends Conversation {
    public static final int ID = 546;
    public static NPCClickHandler handleOps = new NPCClickHandler(new Object[] { ID }, e -> {
        switch(e.getOption()) {
            case "Talk-to" -> e.getPlayer().startConversation(new Zaff(e.getPlayer()));
            case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "zaffs_superior_staves");
        }
    });
    public Zaff(Player player) {
        super(player);
        addNPC(ID, HeadE.CHEERFUL, "Would you like to buy or sell some staves or is there something else you need?");
        addOptions(ops -> {
            ops.add("Yes, please.", () -> ShopsHandler.openShop(player, "zaffs_superior_staves"));
            ops.add("No, thank you.");

            ops.add("Do you have any battlestaves?")
                    .addPlayer(HeadE.CONFUSED, "Do you have any battlestaves?")
                    .addNPC(ID, HeadE.CALM_TALK, "I'm not sure. My assistant Naff is in charge of those now.");

            WhatLiesBelow.addZaffOptions(player, ops);
        });
        create();
    }
}
