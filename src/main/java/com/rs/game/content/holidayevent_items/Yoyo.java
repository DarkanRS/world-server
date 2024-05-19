package com.rs.game.content.holidayevent_items;

import com.rs.lib.game.Animation;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class Yoyo {
    public static ItemClickHandler handleYoyo = new ItemClickHandler(new Object[] { 4079 }, new String[] { "Play", "Loop", "Walk", "Crazy" }, e -> {
        switch(e.getOption()) {
            case "Play":
                e.getPlayer().setNextAnimation(new Animation(1457));
                break;
            case "Loop":
                e.getPlayer().setNextAnimation(new Animation(1458));
                break;
            case "Walk":
                e.getPlayer().setNextAnimation(new Animation(1459));
                break;
            case "Crazy":
                e.getPlayer().setNextAnimation(new Animation(1460));
                break;
        }
    });
}

