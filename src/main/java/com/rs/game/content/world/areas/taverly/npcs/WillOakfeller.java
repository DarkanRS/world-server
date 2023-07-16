package com.rs.game.content.world.areas.taverly.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class WillOakfeller extends Conversation {
    private static final int npcId = 14885;

    public WillOakfeller(Player player) {
        super(player);
        addNPC(npcId, HeadE.HAPPY_TALKING, "Want to do some woodcutting, mate?");
        addOptions(new Options() {
            @Override
            public void create() {
                option("I need woodcutting supplies", () -> {
                    ShopsHandler.openShop(player, "will_woodcut_shop");
                });
                option("Farewell");
            }
        });
        create();
    }


    public static NPCClickHandler WillOakfellerHandler = new NPCClickHandler(new Object[] { npcId }, e -> {
        if (e.getOption().equalsIgnoreCase("talk-to"))
            e.getPlayer().startConversation(new WillOakfeller(e.getPlayer()));
        if (e.getOption().equalsIgnoreCase("trade"))
            ShopsHandler.openShop(e.getPlayer(), "will_woodcut_shop");
    });

}
