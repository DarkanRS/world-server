package com.rs.game.content.holidayevent_items;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class HeimlandGamesSouvenir {

    public static ItemClickHandler HeimlandGamesSouvenir = new ItemClickHandler(new Object[] { 20078 }, new String[] { "Snowsplosion" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(15098));
        e.getPlayer().setNextSpotAnim(new SpotAnim(1283));
    });
}
