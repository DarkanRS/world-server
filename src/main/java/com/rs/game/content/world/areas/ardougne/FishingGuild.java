package com.rs.game.content.world.areas.ardougne;

import com.rs.game.content.world.doors.Doors;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class FishingGuild {
    public static ObjectClickHandler handleFishingGate = new ObjectClickHandler(new Object[] { 49016, 49014 }, e -> {
        Player player = e.getPlayer();
        if (player.getSkills().getLevel(Constants.FISHING) < 68) {
            player.sendMessage("You need a Fishing level of 68 in order to pass through this gate.");
            return;
        }
        Doors.handleDoubleDoor(player, e.getObject());
    });

}
