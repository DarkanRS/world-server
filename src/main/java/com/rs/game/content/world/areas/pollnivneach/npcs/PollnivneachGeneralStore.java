package com.rs.game.content.world.areas.pollnivneach.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class PollnivneachGeneralStore {
    public static NPCClickHandler HandlePollnivneachGeneralStore = new NPCClickHandler(new Object[]{ 1866 }, new String[] { "Talk-to" }, e -> {
        Player player = e.getPlayer();
        NPC npc = e.getNPC();
        player.startConversation(new Dialogue()
                .addNPC(npc, HeadE.CALM_TALK, "Are you in need of provisions?")
                .addOptions(options -> {
                    options.add("Yes thank you.", () -> ShopsHandler.openShop(e.getPlayer(), "pollnivneach_general_store"));
                    options.add("No thanks.");
                })
        );
    });
}
