package com.rs.game.content.world.areas.fremennik_isles;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.holidayevents.halloween.hw09.Halloween2009;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class FremennikIsles {


    public static ObjectClickHandler JatizsoUp = new ObjectClickHandler(new Object[] { 21395 }, e -> {
        e.getPlayer().useLadder(828, e.getPlayer().transform(0, 0, 2));
    });

    public static ObjectClickHandler JatizsoDown = new ObjectClickHandler(new Object[] { 21396 }, e -> {
        e.getPlayer().useLadder(828, e.getPlayer().transform(0, 0, -2));
    });


}
