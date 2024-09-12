package com.rs.game.content.world.areas.burgh_de_rott;

import com.rs.game.content.skills.agility.Agility;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import static com.rs.game.content.world.doors.Doors.handleGate;

@PluginEventHandler
public class BurghDeRott {

    public static ObjectClickHandler handleBDRGate = new ObjectClickHandler(new Object[] { 17757 }, e -> handleGate(e.getPlayer(), e.getObject()));

}