package com.rs.game.content.holidayevent_items;

import com.rs.lib.game.Animation;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class ToyKite {

    public static ItemClickHandler handle = new ItemClickHandler(new Object[] { 12844 }, new String[] { "Fly" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(8990));

    });
}
