package com.rs.game.content.world.areas.tzhaar;

import com.rs.game.content.minigames.fightcaves.FightCavesController;
import com.rs.game.content.minigames.fightpits.FightPits;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class TzHaar {
    public static ObjectClickHandler handleFightCavesEntrance = new ObjectClickHandler(new Object[] { 9356 }, e -> {
        FightCavesController.enterFightCaves(e.getPlayer());
    });

    public static ObjectClickHandler handleFightPitsEntrance = new ObjectClickHandler(new Object[] { 68223 }, e -> {
        FightPits.enterLobby(e.getPlayer(), false);
    });
}
