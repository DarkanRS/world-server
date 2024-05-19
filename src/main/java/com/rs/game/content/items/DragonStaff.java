package com.rs.game.content.items;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class DragonStaff {

    public static ItemClickHandler DragonStaff = new ItemClickHandler(new Object[] { 19323 }, new String[] { "Dragon-dance" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(14300));
        e.getPlayer().setNextSpotAnim(new SpotAnim(118));
    });
}
