package com.rs.game.content.world.areas.taverly.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class AlisonElmshaper extends Conversation {
    private static final int npcId = 14858;

    public AlisonElmshaper(Player player) {
        super(player);
        addNPC(npcId, HeadE.CALM_TALK, "Oh, hello. Need something?");
        addOptions(new Options() {
            @Override
            public void create() {
                option("I need fletching supplies", () -> {
                    ShopsHandler.openShop(player, "alison_fletch_shop");
                });
                option("Farewell");
            }
        });
        create();
    }

    public static NPCClickHandler AlisonElmshaperHandler = new NPCClickHandler(new Object[] { npcId }, e -> {
        if (e.getOption().equalsIgnoreCase("talk-to"))
            e.getPlayer().startConversation(new AlisonElmshaper(e.getPlayer()));
        if (e.getOption().equalsIgnoreCase("trade"))
            ShopsHandler.openShop(e.getPlayer(), "alison_fletch_shop");
    });

}
