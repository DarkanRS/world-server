package com.rs.game.content.birthday_items;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class TenthAnniversaryCake {

    public static ItemClickHandler TenthAnniversaryCake = new ItemClickHandler(new Object[] { 20113 }, new String[] { "Celebrate" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(6292));
        e.getPlayer().setNextSpotAnim(new SpotAnim(2964));
    });
}
