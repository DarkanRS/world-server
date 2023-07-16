package com.rs.game.content.world.areas.falador.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class SirTiffyCashien extends Conversation {
    private static final int npcId = 2290;

    public static NPCClickHandler SirTiffyCashien = new NPCClickHandler(new Object[]{ npcId }, e -> {
        switch (e.getOption()) {
            
            case "Talk-to" -> e.getPlayer().startConversation(new SirTiffyCashien(e.getPlayer()));
        }
    });

    public SirTiffyCashien(Player player) {
        super(player);
        if(!Quest.RECRUITMENT_DRIVE.isImplemented()) {
            addPlayer(HeadE.HAPPY_TALKING, "Hello." );
            addNPC(npcId, HeadE.HAPPY_TALKING, "What ho, " + player.getPronoun("sir", "milady")+ ". Spiffing day for a walk in the park, what?" );
            addOptions(new Options() {
                @Override
                public void create() {

                    option("Can I buy some armour?", new Dialogue()
                            .addPlayer(HeadE.CALM_TALK, "Can I buy some armour?")
                            .addNPC(npcId, HeadE.HAPPY_TALKING,"Of course, dear " + player.getPronoun("boy", "gal") + ".")
                            .addNext(() -> {
                                ShopsHandler.openShop(player, "initiate_rank_armory");
                            })
                    );
                    option("Goodbye.", new Dialogue()
                            .addPlayer(HeadE.CALM_TALK, "Well, see you around, Tiffy.")
                            .addNPC(npcId, HeadE.HAPPY_TALKING, "Well, see you around, Tiffy.")
                    );
                }


            });
        }
        else if(player.getQuestManager().getStage(Quest.RECRUITMENT_DRIVE) == 0) {
            addPlayer(HeadE.HAPPY_TALKING, "Hello." );
            addNPC(npcId, HeadE.HAPPY_TALKING, "What ho, " + player.getPronoun("sir", "milady")+ ". Spiffing day for a walk in the park, what?" );
            addPlayer(HeadE.CONFUSED, "Spiffing?" );
            addNPC(npcId, HeadE.HAPPY_TALKING, "Absolutely, top-hole! Well, can't stay and chat all day, dontchaknow! Ta-ta for now!");
            addPlayer(HeadE.CONFUSED, "Erm...goodbye." );
        }
    }
}
