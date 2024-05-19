package com.rs.game.content.holidayevent_items;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class RedMarionette {

    public static ItemClickHandler handleRedMarionette = new ItemClickHandler(new Object[]{6867}, new String[]{"Jump", "Walk", "Bow", "Dance"}, e -> {
        switch (e.getOption()) {
            case "Jump":
                e.getPlayer().setNextAnimation(new Animation(3003));
                e.getPlayer().setNextSpotAnim(new SpotAnim(507));
                break;
            case "Walk":
                e.getPlayer().setNextAnimation(new Animation(3004));
                e.getPlayer().setNextSpotAnim(new SpotAnim(508));
                break;
            case "Bow":
                e.getPlayer().setNextAnimation(new Animation(3005));
                e.getPlayer().setNextSpotAnim(new SpotAnim(509));
                break;
            case "Dance":
                e.getPlayer().setNextAnimation(new Animation(3006));
                e.getPlayer().setNextSpotAnim(new SpotAnim(510));
                break;


        }
    });
}

