package com.rs.game.content.items;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class Broomstick {

    public static ItemClickHandler Broomstick = new ItemClickHandler(new Object[] { 14057 }, new String[] { "Sweep" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(10532));
        e.getPlayer().setNextSpotAnim(new SpotAnim(1866));

    });
}
