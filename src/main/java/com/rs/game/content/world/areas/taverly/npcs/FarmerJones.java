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
public class FarmerJones extends Conversation {
    private static final int npcId = 14860;

    public FarmerJones(Player player) {
        super(player);
        addNPC(npcId, HeadE.CALM, "Can I help you with your farming troubles?");
        addOptions(new Options() {
            @Override
            public void create() {
                option("I need farming supplies", () ->
                        ShopsHandler.openShop(player, "head_farmer_jones_shop")
                );
                option("Tell me more about farming", new Dialogue().addNPC(npcId,
                                HeadE.HAPPY_TALKING,
                                "By farming you can grow your own plants. You'll start with simple stuff like potatoes"
                                        + " but if you stick at it, you'll even be able to grow trees and the like. Farming's a slow-paced skill. If you want")
                        .addNPC(npcId, HeadE.CALM,
                                " something that only needs checking on occasionally, it'll suit you down to the ground. Plant as many crops as ya can,"
                                        + " as often as ya can. It's only through practice that you'll improve."));
                option("Farewell");
            }
        });
        create();
    }
    public static NPCClickHandler FarmerJonesHandler = new NPCClickHandler(new Object[] { npcId }, e -> {
        if (e.getOption().equalsIgnoreCase("talk-to"))
            e.getPlayer().startConversation(new FarmerJones(e.getPlayer()));
        if (e.getOption().equalsIgnoreCase("trade"))
            ShopsHandler.openShop(e.getPlayer(), "head_farmer_jones_shop");
    });

}
