package com.rs.game.content.items;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class GoldenHammer {


    public static ItemClickHandler GoldenHammer = new ItemClickHandler(new Object[] { 20084 }, new String[] { "Brandish (2009)", "Spin (2010)" }, e -> {
        switch (e.getOption()) {
            case "Brandish (2009)":
                e.getPlayer().setNextAnimation(new Animation(15150));
                break;

            case "Spin (2010)":
                e.getPlayer().setNextAnimation(new Animation(15149));
                e.getPlayer().setNextSpotAnim(new SpotAnim(2953));
                break;
        }
    }
    );
}