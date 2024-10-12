package com.rs.game.content.world.areas.port_phasmatys.npcs;

import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
@PluginEventHandler
public class PiratePete {
    public static NPCClickHandler PiratePete = new NPCClickHandler(new Object[]{2825, 2826},new String[]{"Talk-to"}, e -> {
        if (e.getNPCId() == 2825)
            e.getPlayer().sendOptionDialogue("Would you like to travel to Braindeath Island?", ops -> {
                ops.add("Yes", () -> e.getPlayer().fadeScreen(() -> e.getPlayer().tele(Tile.of(2163, 5112, 1))));
                ops.add("No");
            });
        else if (e.getNPCId() == 2826)
            e.getPlayer().sendOptionDialogue("Would you like to travel back to Port Phasmatys?", ops -> {
                ops.add("Yes", () -> e.getPlayer().fadeScreen( () -> e.getPlayer().tele(Tile.of(3680, 3536, 0))));
                ops.add("No");
            });
    });
}
