package com.rs.game.content.world.areas.burthorpe.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class ImperialGuard extends Conversation {
    private static final int npcId = 1076;

    public static NPCClickHandler ImperialGuard = new NPCClickHandler(new Object[] { npcId }, e -> {
        if (e.getOption().equalsIgnoreCase("talk-to")) {
            e.getPlayer().startConversation(new ImperialGuard(e.getPlayer()));
        }
    });


    public ImperialGuard(Player player) {
        super(player);
        String[] responses = new String[]{
                "Mihi ignosce. Cum homine de cane debeo congredi.",
                "Errare humanum est.",
                "Die dulci freure.",
                "Carpe Diem!",
                "Te audire non possum. Musa sapientum fixa est in aure.",
                "Furnulum pani nolo.",
                "Fac ut gaudeam.",
                "Utinam barbari spatium proprium tuum invadant!",
                "Quantum materiae materietur marmota monax si marmota monax materiam possit materiari?",
                "Sona si Latine loqueris.",
                "Raptus Regaliter.",
                "Nemo dat quod non habet.",
                "Ne auderis delere orbem rigidum meum!",
                "Da mihi sis bubulae frustum assae, solana tuberosa in modo gallico fricta, ac quassum lactatum coagulatum crassum.",
                "Cogito ergo sum.",
                "Vacca foeda.",
                "Di! Ecce hora! Uxor mea me necabit!",
                "Latine loqui coactus sum.",
                "Cave ne ante ullas catapultas ambules.",
                "Fac ut vivas!",
                "Noli me vocare, ego te vocabo.",
                "Meliora cogito.",
                "Braccae tuae aperiuntur.",
                "Vescere bracis meis.",
                "Corripe cervisiam!"
        };

        addPlayer(HeadE.HAPPY_TALKING, "Hi!");
        addNPC(npcId, HeadE.SHAKING_HEAD, responses[(Utils.random(25))]);
        switch (Utils.random(1,4)) {
            case 1 -> {
                addPlayer(HeadE.CONFUSED, "What?!");
            }
            case 2 -> {
                addPlayer(HeadE.CONFUSED, "Huh?!");
            }
            case 3 -> {
                addPlayer(HeadE.CONFUSED, "Er...");
            }
            case 4 -> {
                addPlayer(HeadE.CONFUSED, "OK...");
            }
        }
            if(Utils.random(100) <= 25)
            {
                addPlayer(HeadE.ANGRY, "Wait! Are you insulting me in Latin?");
                addNPC(npcId, HeadE.FRUSTRATED, "Yes!");
                addPlayer(HeadE.FRUSTRATED, "Hmm...");
            }
        create();
    }
}

