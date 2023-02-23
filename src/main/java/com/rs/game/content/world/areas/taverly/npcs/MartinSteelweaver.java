package com.rs.game.content.world.areas.taverly.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class MartinSteelweaver extends Conversation {
    private static final int npcId = 14874;

    public MartinSteelweaver(Player player) {
        super(player);
        addNPC(npcId, HeadE.HAPPY_TALKING, "Do you need smithing help?");
        addOptions(new Options() {
            @Override
            public void create() {
                option("I need smithing supplies", () -> {
                    ShopsHandler.openShop(player, "martin_smithing_shop");
                });
                option("Farewell");
            }
        });
        create();
    }

    public static NPCClickHandler MartinSteelweaverHandler = new NPCClickHandler(new Object[] { npcId }, e -> {
        if (e.getOption().equalsIgnoreCase("talk-to"))
            e.getPlayer().startConversation(new MartinSteelweaver(e.getPlayer()));
        if (e.getOption().equalsIgnoreCase("trade"))
            ShopsHandler.openShop(e.getPlayer(), "martin_smithing_shop");
    });

}
