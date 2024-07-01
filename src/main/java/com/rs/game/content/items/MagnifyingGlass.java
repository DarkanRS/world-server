package com.rs.game.content.items;

import com.rs.lib.game.Animation;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class MagnifyingGlass {


    public static ItemClickHandler MagnifyingGlass = new ItemClickHandler(new Object[] { 15374 }, new String[] { "Sleuth" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(2936));
    });
}