package com.rs.game.content.holidayevent_items;
import com.rs.lib.game.Animation;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

import java.util.Timer;
import java.util.TimerTask;

@PluginEventHandler
public class SpinningPlate {

    public static ItemClickHandler SpinningPlate = new ItemClickHandler(new Object[] { 4613 }, new String[] { "Spin" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(1902));

        // Inside your method or class
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                e.getPlayer().setNextAnimation(new Animation(1904));
            }
        }, 1200); // 4000 milliseconds = 5 seconds
    });
}