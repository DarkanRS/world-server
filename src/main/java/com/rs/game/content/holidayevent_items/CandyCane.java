package com.rs.game.content.holidayevent_items;

import com.rs.lib.game.Animation;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class CandyCane {
    public static ItemClickHandler CandyCane = new ItemClickHandler(new Object[] { 15426 }, new String[] { "Spin" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(12664));

    });
}
