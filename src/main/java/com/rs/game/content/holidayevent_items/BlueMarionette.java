package com.rs.game.content.holidayevent_items;


import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class BlueMarionette {

    public static ItemClickHandler handleBlueMarionette = new ItemClickHandler(new Object[]{6865}, new String[]{"Jump", "Walk", "Bow", "Dance"}, e -> {
        switch (e.getOption()) {
            case "Jump":
                e.getPlayer().setNextAnimation(new Animation(3003));
                e.getPlayer().setNextSpotAnim(new SpotAnim(511));
                break;
            case "Walk":
                e.getPlayer().setNextAnimation(new Animation(3004));
                e.getPlayer().setNextSpotAnim(new SpotAnim(512));
                break;
            case "Bow":
                e.getPlayer().setNextAnimation(new Animation(3005));
                e.getPlayer().setNextSpotAnim(new SpotAnim(513));
                break;
            case "Dance":
                e.getPlayer().setNextAnimation(new Animation(3006));
                e.getPlayer().setNextSpotAnim(new SpotAnim(514));
                break;
        }
    }
    );
}