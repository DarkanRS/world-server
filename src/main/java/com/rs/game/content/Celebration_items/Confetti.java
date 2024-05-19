package com.rs.game.content.Celebration_items;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class Confetti {

    public static ItemClickHandler Confetti = new ItemClickHandler(new Object[] { 20718 }, new String[] { "Throw" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(10952));
        e.getPlayer().setNextSpotAnim(new SpotAnim(1341));
    });
}
