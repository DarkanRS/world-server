package com.rs.game.content.world.areas.taverly.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.content.world.unorganized_dialogue.TanningD;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class JackOval extends Conversation {
    private static final int npcId = 14877;

    public JackOval(Player player) {
        super(player);
        addNPC(npcId, HeadE.HAPPY_TALKING,
                "You need to make things. Leather. Pottery. Every piece you make will increase your skill.");
        addOptions(new Options() {
            @Override
            public void create() {
                option("I need crafting supplies", () -> {
                    ShopsHandler.openShop(player, "jack_crafting_shop");
                });
                option("I need you to tan some leather for me.", () -> player.startConversation(new TanningD(player, false, npcId)));
                option("Farewell");
            }
        });
        create();
    }

    public static NPCClickHandler JackOvalHandler = new NPCClickHandler(new Object[] { npcId }, e -> {
        if (e.getOption().equalsIgnoreCase("talk-to"))
            e.getPlayer().startConversation(new JackOval(e.getPlayer()));
        if (e.getOption().equalsIgnoreCase("trade"))
            ShopsHandler.openShop(e.getPlayer(), "jack_crafting_shop");
        if (e.getOption().equalsIgnoreCase("tan-hide"))
            e.getPlayer().startConversation(new TanningD(e.getPlayer(),false, npcId));
    });

}
