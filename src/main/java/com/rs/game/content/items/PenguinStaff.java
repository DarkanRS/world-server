package com.rs.game.content.items;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class PenguinStaff {

    public static ItemClickHandler PenguinStaff = new ItemClickHandler(new Object[] { 19325 }, new String[] { "Penguin-dance" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(14301));
        e.getPlayer().setNextSpotAnim(new SpotAnim(119));
    });
}
