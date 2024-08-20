package com.rs.game.content.world.areas.daemonheim.npcs;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class FremennikShipmaster {
    public static NPCClickHandler FremennikShipmasterSail = new NPCClickHandler(new Object[]{ 9707, 9708, 14847 }, e -> {
        switch (e.getNPCId()){
            case 9708, 14847 -> com.rs.game.content.world.areas.rellekka.npcs.FremennikShipmaster.sail(e.getPlayer(), false);
            case 9707 -> com.rs.game.content.world.areas.rellekka.npcs.FremennikShipmaster.sail(e.getPlayer(), true);
        }
    });
}
