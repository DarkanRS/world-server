package com.rs.game.content.world.areas.oo_glog.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Snurgh {
    public static NPCClickHandler Snurgh = new NPCClickHandler(new Object[]{ 7057 }, new String[]{"Talk-to"}, e -> {
        if (e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT))
            afterAsAFirstResort(e.getPlayer(), e.getNPC());
        else
            beforeAsAFirstResort(e.getPlayer(), e.getNPC());
    });

    private static void beforeAsAFirstResort(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addNPC(npc.getId(), HeadE.CHILD_ANGRY_HEADSHAKE, "Outta de way, human. Dis place not open yet!")
                .addPlayer(HeadE.SAD, "That's not very friendly.")
                .addNPC(npc.getId(), HeadE.CHILD_ANGRY_HEADSHAKE, "Me said OUTTA DE WAY! You no can sleep here!")
                .addPlayer(HeadE.SKEPTICAL_HEAD_SHAKE, "Alright, alright! Keep your hat on! I never said I wanted to.")
        );
    }

    private static void afterAsAFirstResort(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addPlayer(HeadE.CALM_TALK, "How's business?")
                .addNPC(npc.getId(), HeadE.CHILD_HAPPY_TALK, "Is okay. De sleepers like de fluffsie pillows. Dey good for small, soft heads like yours.")
                .addNPC(npc.getId(), HeadE.CHILD_HAPPY_TALK, "You big help to Snurgh. If you ever wants to try dem out, you can stay free anytime.")
                .addPlayer(HeadE.HAPPY_TALKING, "Why, thank you. I'll keep that in mind.")
        );
    }

}
