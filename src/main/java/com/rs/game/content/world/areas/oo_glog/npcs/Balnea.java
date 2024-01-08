package com.rs.game.content.world.areas.oo_glog.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Balnea {

    public static NPCClickHandler Balnea = new NPCClickHandler(new Object[]{ 7047 }, new String[]{"Talk-to"}, e -> {
        if (e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT))
            afterAsAFirstResort(e.getPlayer(), e.getNPC());
        else
            beforeAsAFirstResort(e.getPlayer(), e.getNPC());
    });

    private static void beforeAsAFirstResort(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addPlayer(HeadE.HAPPY_TALKING, "Hi there!")
                .addNPC(npc.getId(), HeadE.SHAKING_HEAD, "I'm ever so busy at the moment; please come back after the grand opening.")
                .addPlayer(HeadE.HAPPY_TALKING, "What grand opening?")
                .addNPC(npc.getId(), HeadE.FRUSTRATED, "I'm sorry, I really can't spare the time to talk to you.")
                .addPlayer(HeadE.CALM_TALK, "Uh, sure.")
        );
    }

    private static void afterAsAFirstResort(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addNPC(npc.getId(), HeadE.HAPPY_TALKING, "Hi, " + player.getDisplayName() + ". What can I do for you today?")
                .addOptions(ops -> {
                    ops.add("So, how's business?")
                            .addPlayer(HeadE.CALM_TALK, "So, how's business?")
                            .addNPC(npc.getId(), HeadE.CALM_TALK, "Word seems to be spreading and, now that we've launched, we're beginning to drill down and strategize about long-term bench-marking and sure-fire value-adding propositions.")
                            .addNPC(npc.getId(), HeadE.CALM_TALK, "Would you like to be involved in some horizon scanning? We could use someone with your talent for joined-up thinking and can-do ideation as we upsize this venture to the next level.")
                            .addPlayer(HeadE.SKEPTICAL, "Ah, yes. Now I remember why I was never going to talk to you again.");

                    ops.add("What did you say about hunting baby platypodes?")
                            .addPlayer(HeadE.HAPPY_TALKING, "What did you say about hunting baby platypodes?")
                            .addNPC(npc.getId(), HeadE.CALM_TALK, "If you have the skill to care for it, you can catch a baby platypus and raise it as a pet.")
                            .addNPC(npc.getId(), HeadE.CALM_TALK, "They need special care, so you can't have more than one at a time, and you can't catch one if you are unable to care for it.")
                            .addNPC(npc.getId(), HeadE.CALM_TALK, "If you do manage to catch one, try feeding it raw fish. They seem to love that.")
                            .addPlayer(HeadE.HAPPY_TALKING, "Thanks for the info!");
                })
        );
    }
}
