package com.rs.game.content.items;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class Bouquet {

    public static ItemClickHandler Bouquet = new ItemClickHandler(new Object[] { 20714 }, new String[] { "Throw" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(10951));
        e.getPlayer().setNextSpotAnim(new SpotAnim(1343));          //gfx 1343 is one of the animations

        //e.getPlayer().setNextAnimation(new Animation(10964)); // This is the picking up the bouquet animation.
    });
}


// This one is not fully finished yet. Still missing some animations.