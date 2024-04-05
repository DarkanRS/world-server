package com.rs.game.content.world.areas.oo_glog.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Muggh {
    public static NPCClickHandler Muggh = new NPCClickHandler(new Object[]{ 7062 }, new String[]{"Talk-to"}, e -> {
        if (e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT))
            afterAsAFirstResort(e.getPlayer(), e.getNPC());
        else
            beforeAsAFirstResort(e.getPlayer(), e.getNPC());
    });

    private static void beforeAsAFirstResort(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addNPC(npc.getId(), HeadE.CHILD_ANGRY_HEADSHAKE, "Hey, what you doing here? We not open yet.")
                .addPlayer(HeadE.CONFUSED, "Just having a nosey, really.")
                .addNPC(npc.getId(), HeadE.CHILD_LAUGH, "You bring dat nose back here when we open for business. I fix you up good.")
                .addPlayer(HeadE.CONFUSED, "Fix me up?")
                .addNPC(npc.getId(), HeadE.CHILD_LAUGH, "Yeah, me give you facial. Try to make your ugly face look bit nicer.")
                .addPlayer(HeadE.SKEPTICAL, "Charming.")
        );
    }

    private static void afterAsAFirstResort(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addNPC(npc.getId(), HeadE.CHILD_HAPPY_TALK, "Hey, what you doing here? You want me give you facial?")
                .addOptions(options -> {
                    options.add("Why, sure, since you make it sound so delightful.")
                            .addNext(()-> {
                                if (player.getEquipment().getHatId() == 12558) {
                                    player.startConversation(new Dialogue()
                                            .addPlayer(HeadE.CHILD_ANGRY_HEADSHAKE, "Silly human, you already gots face full of muds."));
                                    return;
                                }
                                if(player.getEquipment().getId(Equipment.HEAD) != -1) {
                                    player.startConversation(new Dialogue()
                                            .addNPC(npc.getId(), HeadE.CHILD_ANGRY_HEADSHAKE, "Take dat thing off your head, human, else you no get facial."));
                                }
                                else {
                                    Equipment.sendWear(player, Equipment.HEAD, 12558);
                                    player.sendMessage("Muggh smacks a fistful of mud on your face.");
                                    //TODO npc.anim();
                                    World.sendProjectile(npc, player, 1462, 30, 30, 45, 30, 15);
                                    player.delayLock(2, () -> {
                                        player.getEquipment().setSlot(Equipment.HEAD, new Item(12558));
                                        player.getEquipment().refresh(Equipment.HEAD);
                                        player.getAppearance().generateAppearanceData();
                                    });
                                }
                            });
                    options.add("Um, maybe later.")
                            .addPlayer(HeadE.SKEPTICAL, "Um, maybe later.");
                })
        );
    }


}
