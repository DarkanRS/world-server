package com.rs.game.content.Gielinor_games_reward_shop;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class SilverTorch {

    public static ItemClickHandler SilverTorch = new ItemClickHandler(new Object[] { 24548 }, new String[] { "Emote" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(17136));
        e.getPlayer().setNextSpotAnim(new SpotAnim(3247));
    });
}
