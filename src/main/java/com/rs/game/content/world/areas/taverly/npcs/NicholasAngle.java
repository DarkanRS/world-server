package com.rs.game.content.world.areas.taverly.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class NicholasAngle extends Conversation {
    private static final int npcId = 14879;

    public NicholasAngle(Player player) {
        super(player);
        addNPC(npcId, HeadE.CALM, "I'm not surprised you want to fish. Fishing is great!");
        addOptions(new Options() {
            @Override
            public void create() {
                option("I need fishing bait", () -> {
                    ShopsHandler.openShop(player, "nicholas_fishing_shop");
                });
                option("Tell me more about fishing", new Dialogue().addNPC(npcId,
                        HeadE.HAPPY_TALKING,
                        "Fishing is more than a method of gathering food. It's more than a profession. "
                                + "It's a way of life! Fish makes good eating, especially when you need to recover after battle."));
                option("Farewell");
            }
        });
        create();
    }
    public static NPCClickHandler NicholasAngleHandler = new NPCClickHandler(new Object[] { npcId }, e -> {
        if (e.getOption().equalsIgnoreCase("talk-to"))
            e.getPlayer().startConversation(new NicholasAngle(e.getPlayer()));
        if (e.getOption().equalsIgnoreCase("trade"))
            ShopsHandler.openShop(e.getPlayer(), "nicholas_fishing_shop");
    });

}
