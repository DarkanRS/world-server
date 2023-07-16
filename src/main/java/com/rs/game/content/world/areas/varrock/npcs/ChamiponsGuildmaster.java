package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.content.quests.dragonslayer.GuildMasterDragonSlayerD;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class ChamiponsGuildmaster {
    public static NPCClickHandler handleGuildMaster = new NPCClickHandler(new Object[] { 198 }, e -> {
        if (e.getPlayer().getQuestManager().getQuestPoints() <= 31) {
            e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
                {
                    addNPC(e.getNPCId(), HeadE.FRUSTRATED, "You really shouldn't be in here, but I will let that slide...");
                    create();
                }
            });
            return;
        }
        e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
            {
                addNPC(e.getNPCId(), HeadE.CHEERFUL, "Greetings!");
                addOptions("What would you like to say?", new Options() {
                    @Override
                    public void create() {
                        option("What is this place?", new Dialogue()
                                .addPlayer(HeadE.HAPPY_TALKING, "What is this place?")
                                .addNPC(198, HeadE.HAPPY_TALKING, "This is the Champions' Guild. Only adventurers who have proved themselves worthy " +
                                        "by gaining influence from quests are allowed in here."));
                        if(!e.getPlayer().isQuestComplete(Quest.DRAGON_SLAYER))
                            option("About Dragon Slayer", new Dialogue()
                                    .addNext(()->{e.getPlayer().startConversation(new GuildMasterDragonSlayerD(e.getPlayer()).getStart());}));
                    }
                });
                create();
            }
        });
    });
}
