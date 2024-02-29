package com.rs.game.content.world.areas.oo_glog.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Thump {

    @ServerStartupEvent
    public static void addLoSOverride() {
        Entity.addLOSOverride(7100);
        Entity.addLOSOverride(7101);
    }

    //Varbit handles all post quest content, Move to quest once implemented
    public static LoginHandler login = new LoginHandler(e -> {
        if (e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT))
            e.getPlayer().getVars().setVarBit(4322, 1);
        else
            e.getPlayer().getVars().setVarBit(4322, 0);
    });

    public static NPCClickHandler Thump = new NPCClickHandler(new Object[]{ 7101, 7100 }, new String[]{"Talk-to"}, e -> {
        if (!Quest.AS_A_FIRST_RESORT.isImplemented()) {
            afterAsAFirstResort(e.getPlayer(), e.getNPC());
            return;
        }
        if (e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT))            afterAsAFirstResort(e.getPlayer(), e.getNPC());
        else
            beforeAsAFirstResort(e.getPlayer(), e.getNPC());
    });

    private static void beforeAsAFirstResort(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addNPC(7061, HeadE.CHILD_HAPPY_TALK, "C'mere, human. Me need practice at dis massage thing.")
                .addNPC(7061, HeadE.CHILD_HAPPY_TALK, "Me not sure how to do it without breaking spine of small, puny creatures.")
                .addPlayer(HeadE.SCARED, "I...think I'll take a rain check on that.")
        );
    }

    private static void afterAsAFirstResort(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addNPC(7061, HeadE.CHILD_ANGRY_HEADSHAKE, "RAAAAAAAGH!")
                .addNext(() -> npc.forceTalk("Send...help...!"))
        );
    }

}
