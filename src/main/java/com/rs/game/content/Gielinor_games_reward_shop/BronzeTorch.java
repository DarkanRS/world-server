package com.rs.game.content.Gielinor_games_reward_shop;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class BronzeTorch {

    public static ItemClickHandler BronzeTorch = new ItemClickHandler(new Object[] { 24547 }, new String[] { "Emote" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(17137));
        e.getPlayer().setNextSpotAnim(new SpotAnim(3246));
    });
}
