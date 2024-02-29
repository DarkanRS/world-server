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
public class Frawd {

    public static NPCClickHandler Frawd = new NPCClickHandler(new Object[]{ 7048 }, e -> {
        switch (e.getOption()) {
            case "Talk-to" -> {
                if (e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT))
                    afterAsAFirstResort(e.getPlayer(), e.getNPC());
                else
                    beforeAsAFirstResort(e.getPlayer(), e.getNPC());
            }
            case "Trade" ->
                ShopsHandler.openShop(e.getPlayer(),"gift_shop");
        }
    });

    private static void beforeAsAFirstResort(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addNPC(npc.getId(), HeadE.CHILD_ANGRY_HEADSHAKE, "Go away, human. Can't you see me busy here?")
                .addPlayer(HeadE.CONFUSED, "Oh, well, excuse me! My mistake entirely!")
                .addPlayer(HeadE.FRUSTRATED, "Why would I ever think that someone standing behind a cash register might be interested in helping me?")
                .addNPC(npc.getId(), HeadE.CHILD_ANGRY_HEADSHAKE, "Go away, human. We're not open yet!")
                .addPlayer(HeadE.FRUSTRATED, "Well, excuse me!")
        );
    }

    private static void afterAsAFirstResort(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "What you want, human?")
                .addOptions(ops -> {
                    ops.add("So what do you have for sale, then?")
                            .addPlayer(HeadE.CALM_TALK, "So what do you have for sale, then?", () -> ShopsHandler.openShop(player,"gift_shop"));

                    ops.add("Never mind.")
                            .addPlayer(HeadE.CALM_TALK, "Never mind.")
                            .addNPC(npc.getId(), HeadE.CHILD_ANGRY_HEADSHAKE, "You're a bit annoying, human; you know that?")
                            .addPlayer(HeadE.LAUGH, "Thanks. I try.");
                })
        );
    }
}
