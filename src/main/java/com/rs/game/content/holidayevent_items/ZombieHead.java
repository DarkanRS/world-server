package com.rs.game.content.holidayevent_items;

import com.rs.lib.game.Animation;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class ZombieHead {

    public static ItemClickHandler ZombieHead = new ItemClickHandler(new Object[]{6722, 10731}, new String[]{"Talk-At", "Display"}, e -> {
        switch (e.getOption()) {

            case "Talk-At":
                e.getPlayer().setNextAnimation(new Animation(2840));
                e.getPlayer().forceTalk("Alas!");
                break;

                case "Display":
                e.getPlayer().setNextAnimation(new Animation(2844));
                    e.getPlayer().forceTalk("MWAHAHAHAHAHAHAH");
                break;
        }
    });

}

