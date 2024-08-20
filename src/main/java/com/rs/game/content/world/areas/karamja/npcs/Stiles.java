package com.rs.game.content.world.areas.karamja.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
@PluginEventHandler

public class Stiles {

    private static void exchangeFish(Player player) {
        int[] noteableFish = {377, 371, 359, 317, 345, 327};
        boolean exchanged = false;

        for (Item item : player.getInventory().getItems().array()) {
            if (item == null)
                continue;

            for (int id : noteableFish) {
                if (item.getId() == id) {
                    player.getInventory().deleteItem(item.getId(), 1);
                    player.getInventory().addItem(item.getDefinitions().getCertId(), 1);
                    exchanged = true;
                }
            }
        }
        if (!exchanged) {
            player.startConversation(new Dialogue()
                    .addNPC(11267,HeadE.SHAKING_HEAD, "Ahhh, ye've nothing that ol' Stiles can exchange. I'll do yer lobbies, yer swordies an' yer tuna, that's all.")
            );
        }
    }

    public static NPCClickHandler StilesExchange = new NPCClickHandler(new Object[] {11267}, new String[] {"Exchange"}, e -> {
        exchangeFish(e.getPlayer());
    });

    public static NPCClickHandler StilesTalk = new NPCClickHandler(new Object[] {11267}, new String[] {"Talk-to"}, e -> {
        NPC npc = e.getNPC();
        e.getPlayer().startConversation(new Dialogue()
                .addPlayer(HeadE.CALM_TALK, "Who are you and why are you here?")
                .addNPC(npc, HeadE.CALM_TALK, "Ahhh, when I were a young'un my name were Nigel but, these days, folks mostly call me Stiles.")
                .addNPC(npc, HeadE.CALM_TALK, "Long time ago, in Draynor Village, there were three brothers who'd exchange yer stuff for bitty bits o' paper, like these new-fangled banknotes we've got today. Niles, Miles an' Giles they called themselves.")
                .addNPC(npc, HeadE.CALM_TALK, "They be long gone, like the golden days, but they were an inspiration to me, so I took this trade myself, an' I changed my name to Stiles.")
                .addPlayer(HeadE.CALM_TALK, "But why are you here, in this place?")
                .addNPC(npc, HeadE.CALM_TALK, "The smell of yon bananas were drivin' me scatty, so I can't go too near the fishing spots.")
                .addNPC(npc, HeadE.CALM_TALK, "A tough-lookin' geezer callin' himself a slayer master tried to give me a nosepeg once, but I bain't wearin' one o' them things. Ol' Stiles has a tender nose.")
                .addNPC(npc, HeadE.CALM_TALK, "So, would ye like me to exchange yer fish now?")
                .addOptions(ops -> {
                    ops.add("Exchange Fish", () -> exchangeFish(e.getPlayer()));
                    ops.add("No thanks.")
                            .addPlayer(HeadE.CONFUSED, "No thanks.");
                }));
    });
}
