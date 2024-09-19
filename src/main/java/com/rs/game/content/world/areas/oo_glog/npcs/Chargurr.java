package com.rs.game.content.world.areas.oo_glog.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Chargurr {

    public static NPCClickHandler Chargurr = new NPCClickHandler(new Object[]{ 7055 }, new String[]{ "Talk-to" }, e -> {
        if (e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT))
            afterAsAFirstResort(e.getPlayer(), e.getNPC());
        else
            beforeAsAFirstResort(e.getPlayer(), e.getNPC());
    });

    private static void beforeAsAFirstResort(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "You scrawny, but look like you might make good dessert. You come back when fire lit and I introduce you to cooking spit.")
                .addPlayer(HeadE.SKEPTICAL, "Somehow, this doesn't seem in-line with my personal development plan but, um, thanks for the offer.")
        );
    }

    private static void afterAsAFirstResort(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "Now we open, you want some meat? I cook for you or you cook dem yourself! I also give shiny pretties if you have meats you no want.")
                .addOptions(ops -> {
                    ops.add("Sure, show me what you've got.")
                            .addNext(() -> ShopsHandler.openShop(player, "fresh_meat") );
                    ops.add("Maybe later.")
                            .addPlayer(HeadE.SCARED, "Maybe later.");
                })
        );
    }
}
