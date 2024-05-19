package com.rs.game.content.items;


import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class CatStaff {

    public static ItemClickHandler CatStaff = new ItemClickHandler(new Object[] { 19331 }, new String[] { "Cat-dance" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(14299));
        e.getPlayer().setNextSpotAnim(new SpotAnim(117));
    });
}
