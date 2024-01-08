package com.rs.game.content.world.areas.oo_glog.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Dawg {

    private static final int chiefTess = 7051;

    public static NPCClickHandler Frawd = new NPCClickHandler(new Object[]{ 7104 }, new String[] { "Talk-to" }, e -> {
                if (e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT))
                    afterAsAFirstResort(e.getPlayer(), e.getNPC());
                else
                    beforeAsAFirstResort(e.getPlayer(), e.getNPC());
    });

    private static void beforeAsAFirstResort(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addNPC(npc.getId(), HeadE.CAT_DISAPPOINTED, "Grrrr!")
                .addNPC(chiefTess, HeadE.CHILD_LAUGH, "Watch out, human; Dawg like human for breakfast.")
        );
    }

    private static void afterAsAFirstResort(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addPlayer(HeadE.SCARED, "Hi there, um, puppy... cat... thing. Good Dawg.")
                .addNPC(npc.getId(), HeadE.CAT_DISAPPOINTED, "Grrrr!")
                .addNPC(chiefTess, HeadE.CHILD_LAUGH, "Huh, huh! Me think Dawg like you now.")
                .addPlayer(HeadE.SCARED, "He has a funny way of showing it!")
        );
    }
}
