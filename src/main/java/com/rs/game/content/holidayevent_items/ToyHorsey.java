package com.rs.game.content.holidayevent_items;

import com.rs.game.model.entity.ForceTalk;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class ToyHorsey {

    public static ItemClickHandler ToyHorsey = new ItemClickHandler(new Object[] { 2520, 2522, 2524, 2526 }, new String[] { "Play-with" }, e -> {


        int itemId = 2520;
        if (itemId >= 2520 && itemId <= 2526) {
        String[] phrases = { "Come on Dobbin, we can win the race!", "Hi-ho Silver, and away!", "Neaahhhyyy! Giddy-up horsey!", "Just say neigh to gambling!" };
            e.getPlayer().setNextAnimation(new Animation(918));
        e.getPlayer().setNextForceTalk(new ForceTalk(phrases[Utils.random(phrases.length)]));
        return;
        }
    });
}
