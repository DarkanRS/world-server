package com.rs.game.content.world.areas.burthorpe.npcs;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class CorporalBoothe {
    public static NPCClickHandler handleOps = new NPCClickHandler(new Object[] { 14921 }, e -> {
       switch(e.getOption()) {

       }
    });
}
