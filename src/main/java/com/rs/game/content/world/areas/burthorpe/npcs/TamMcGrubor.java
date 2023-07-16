package com.rs.game.content.world.areas.burthorpe.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class TamMcGrubor extends Conversation {
    private static final int npcId = 14414;

    public TamMcGrubor(Player player) {
        super(player);
        addNPC(npcId, HeadE.SECRETIVE, "You interested in Runecrafting buddy?.. You wanna get some of that wicked action?")
                .addOptions(ops -> {
                    ops.add("No thanks.. You seem a little sketchy to me \"buddy\"..")
                            .addPlayer(HeadE.CONFUSED, "No thanks.. You seem a little sketchy to me \"buddy\"..");
                    ops.add("Sure, give me one of those things.")
                            .addPlayer(HeadE.CHEERFUL, "Sure, give me one of those things.")
                            .addNPC(npcId, HeadE.SECRETIVE, "Alright son. Go ahead and take it. It recharges its power each day.")
                            .addItem(22332, "Tam hands you a wicked hood.", () -> player.getInventory().addItem(22332, 1));
                });
        create();
    }

    public static NPCClickHandler TamMcGruborHandler = new NPCClickHandler(new Object[] { npcId }, e -> {
        if (e.getOption().equalsIgnoreCase("talk-to"))
            e.getPlayer().startConversation(new TamMcGrubor(e.getPlayer()));
    });

}
