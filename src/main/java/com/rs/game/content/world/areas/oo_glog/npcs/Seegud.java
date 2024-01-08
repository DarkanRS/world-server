package com.rs.game.content.world.areas.oo_glog.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Seegud {


    public static NPCClickHandler Seegud = new NPCClickHandler(new Object[]{ 7052 }, new String[]{"Talk-to"}, e -> {
        if (e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT))
            afterAsAFirstResort(e.getPlayer(), e.getNPC());
        else
            beforeAsAFirstResort(e.getPlayer(), e.getNPC());
    });


    private static void beforeAsAFirstResort(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addPlayer(HeadE.CALM_TALK, "Hello, there! Nice day, isn't it?")
                .addNPC(npc.getId(), HeadE.CHILD_FRUSTRATED, "Hmph, it an okay day. Not so sure is nice day. Bit sticky, bit hot. Makes my bones itch.")
                .addPlayer(HeadE.HAPPY_TALKING, "Makes your bones...itch? How does that work?")
                .addNPC(npc.getId(), HeadE.CHILD_FRUSTRATED, "When you get old, you understand.")
        );
    }

    private static void afterAsAFirstResort(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "Thank you much, human. You may be scrawny, but you not bad thinker.")
                .addOptions(ops -> {
                    ops.add("Thanks!")
                            .addPlayer(HeadE.HAPPY_TALKING, "Thanks!");
                    ops.add("Can you tell me about these pools?")
                            .addPlayer(HeadE.HAPPY_TALKING, "Can you tell me about these pools?")
                            .addNext(() -> {
                                player.startConversation(new Dialogue()
                                        .addOptions(poolOps -> {
                                            poolOps.add("Bandos pool")
                                                    .addPlayer(HeadE.HAPPY_TALKING, "Can you tell me about the red Bandos pool, please?")
                                                    .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "Oh, dat de most mysterious of all pools here - it infused with energy of Bandos.")
                                                    .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "Anyone who takes bath in dat pool can be recognised as one of Bandos' own.")
                                                    .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "No follower of Bandos will raise hand against you when you purified with dat water.");
                                            poolOps.add("Salt-water spring")
                                                    .addPlayer(HeadE.HAPPY_TALKING, "Can you tell me about the salt-water spring, please?")
                                                    .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "Dat pool very interesting - is fed by source under surface of water")
                                                    .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "You feel it moving fast when you bathe there. We take swim in that pool if we know we have a long way to run.")
                                                    .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "Always seems like one not get so tired quickly after a bath in fast waters.");
                                            poolOps.add("Mud bath")
                                                    .addPlayer(HeadE.HAPPY_TALKING, "Can you tell me about the mud pool, please?")
                                                    .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "Dat a very useful pool for young creatures who like to hunt.")
                                                    .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "Mud very good at disguising smells.")
                                                    .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "You take a swim in de mud, you find de little creatures nearly jump into your traps.");
                                            poolOps.add("Sulphur spring")
                                                    .addPlayer(HeadE.HAPPY_TALKING, "Can you tell me about the sulphur spring, please?")
                                                    .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "Hmm, yes, dat an interesting pool, human.")
                                                    .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "Mostly used by ogre shamans and mystics like me, though some like to bathe in it because it smells so nice.")
                                                    .addPlayer(HeadE.SCARED, "It smells like horrible rotten eggs that have been marinated in swamp gas!")
                                                    .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "Yes, very lovely smell - and lasts a long time!")
                                                    .addPlayer(HeadE.SKEPTICAL, "Uh, right.");
                                            poolOps.add("Thermal bath")
                                                    .addPlayer(HeadE.HAPPY_TALKING, "Can you tell me about the thermal bath, please?")
                                                    .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "Ah, dat pool very pure and it runs deep - you can tell by de turquoise colour.")
                                                    .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "You relax in dat pool for a while, you feel much better.")
                                                    .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK,"Ogres use dat pool for many, many years to cure all sorts of ailments.")
                                                    .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "If you poisoned, if you diseased, if you hurt, you just take a swim - you feel better than ever, me promise!");
                                        }));
                            });
                    ops.add("No, thanks.")
                            .addPlayer(HeadE.SHAKING_HEAD, "No, thanks.");
                }));
    }
}