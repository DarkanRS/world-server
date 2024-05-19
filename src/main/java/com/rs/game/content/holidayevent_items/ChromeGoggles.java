package com.rs.game.content.holidayevent_items;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class ChromeGoggles {

    public static ItemClickHandler chromeGoggles = new ItemClickHandler(new Object[] { 22412 }, new String[] { "Emote" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(15185));
        e.getPlayer().setNextSpotAnim(new SpotAnim(1961));
    });
}
