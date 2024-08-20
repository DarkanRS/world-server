package com.rs.game.content.world.areas.keldagrim.npcs;

import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
@PluginEventHandler
public class DwarvenFerryman {
    public static NPCClickHandler DwarvenFerryman = new NPCClickHandler(new Object[]{9707}, new String[]{"Cross-river"}, e -> {
        switch (e.getNPCId()){
            case 1843 -> e.getPlayer().tele(Tile.of(2836, 10142, 0));
            case 1844 -> e.getPlayer().tele(Tile.of(2839, 10131, 0));
        }
    });
}
