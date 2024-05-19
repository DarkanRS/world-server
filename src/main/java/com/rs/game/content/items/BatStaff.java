package com.rs.game.content.items;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class BatStaff {


    public static ItemClickHandler BatStaff = new ItemClickHandler(new Object[] { 19327 }, new String[] { "Bat-dance" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(14298));
        e.getPlayer().setNextSpotAnim(new SpotAnim(101));
    });
}
