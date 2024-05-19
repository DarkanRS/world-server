package com.rs.game.content.Celebration_items;

import com.rs.lib.game.Animation;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class SouvenirMug {

    public static ItemClickHandler SouvenirMug = new ItemClickHandler(new Object[] { 20725 }, new String[] { "Polish" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(10942));

    });
}
