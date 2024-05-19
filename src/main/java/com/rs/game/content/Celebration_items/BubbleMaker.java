package com.rs.game.content.Celebration_items;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class BubbleMaker {

    public static ItemClickHandler BubbleMaker = new ItemClickHandler(new Object[] { 20716 }, new String[] { "Blow-bubbles" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(10940));
        e.getPlayer().setNextSpotAnim(new SpotAnim(721));
    });
}
