package com.rs.game.content.world.areas.barbarian_village.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.content.quests.scorpioncatcher.ScorpionCatcher;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

public class Peska {
    public static NPCClickHandler handlePeskaBarbarianVillage = new NPCClickHandler(new Object[] { 538 }, e -> {
        int NPC= e.getNPCId();
        if(e.getOption().equalsIgnoreCase("talk-to")) {
            e.getPlayer().startConversation(new Dialogue()
                    .addNPC(NPC, HeadE.CALM_TALK, "Are you interested in buying or selling a helmet?")
                    .addOptions("Choose an option:", new Options() {
                        @Override
                        public void create() {
                            option("I could be, yes.", new Dialogue()
                                    .addPlayer(HeadE.HAPPY_TALKING, "I could be, yes.")
                                    .addNPC(NPC, HeadE.CALM_TALK, "Let me show you my inventory then...")
                                    .addNext(()->{
                                        ShopsHandler.openShop(e.getPlayer(), "helmet_shop");})
                            );
                            option("No, I'll pass on that.", new Dialogue()
                                    .addPlayer(HeadE.HAPPY_TALKING, "No, I'll pass on that.")
                                    .addNPC(NPC, HeadE.CALM_TALK, "Well, alright.")
                            );
                            if(e.getPlayer().getQuestManager().getStage(Quest.SCORPION_CATCHER) == ScorpionCatcher.LOOK_FOR_SCORPIONS
                                    && e.getPlayer().getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getB("scorp2LocKnown"))
                                option("I've heard you have a small scorpion in your possession.", new Dialogue()
                                        .addPlayer(HeadE.HAPPY_TALKING, "I've heard you have a small scorpion in your possession.")
                                        .addNPC(NPC, HeadE.CALM_TALK, "Now how could you know about that, I wonder? Mind you, I don't have it anymore.")
                                        .addNPC(NPC, HeadE.CALM_TALK, "I gave it as a present to my brother Ivor when I visited our outpost northwest of Camelot.")
                                        .addNPC(NPC, HeadE.CALM_TALK, "Well, actually I hid it in his bed so it would nip him. It was a bit of a surprise gift.")
                                        .addPlayer(HeadE.HAPPY_TALKING, "Okay ill look at the barbarian outpost, perhaps you mean the barbarian agility area?")
                                        .addNPC(NPC, HeadE.SECRETIVE, "Perhaps...")
                                );
                        }
                    })
            );


        }
        if(e.getOption().equalsIgnoreCase("trade"))
            ShopsHandler.openShop(e.getPlayer(), "helmet_shop");
    });
}
