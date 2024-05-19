package com.rs.game.content.holidayevent_items;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import java.util.Timer;
import java.util.TimerTask;

@PluginEventHandler
public class ReindeerHat {
    public static ItemClickHandler handleReinhat = new ItemClickHandler(new Object[] { 10507 }, new String[] { "Emote" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(5059));
        e.getPlayer().setNextSpotAnim(new SpotAnim(859));
        e.getPlayer().setNextSpotAnim(new SpotAnim(263));
// Inside your method or class
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                e.getPlayer().setNextSpotAnim(new SpotAnim(263));
            }
        }, 4000); // 4000 milliseconds = 5 seconds
    });
}

