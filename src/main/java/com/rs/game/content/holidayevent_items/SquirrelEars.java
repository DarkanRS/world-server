package com.rs.game.content.holidayevent_items;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class SquirrelEars {

    public static ItemClickHandler SquirrelEars = new ItemClickHandler(new Object[] { "Squirrel ears" }, new String[] { "Summon Minion", "Juggle" }, e -> {
        switch (e.getOption()) {
            case "Summon Minion":
                e.getPlayer().setNextAnimation(new Animation(-1));
                e.getPlayer().setNextSpotAnim(new SpotAnim(-1));
                break;

            case "Juggle":
                e.getPlayer().setNextAnimation(new Animation(12265));
                e.getPlayer().setNextSpotAnim(new SpotAnim(2145));
                break;
        }
    });
}
