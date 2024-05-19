package com.rs.game.content.items;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class WolfStaff {

    public static ItemClickHandler PenguinStaff = new ItemClickHandler(new Object[] { 19329 }, new String[] { "Wolf-dance" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(14302));
        e.getPlayer().setNextSpotAnim(new SpotAnim(120));
    });
}
