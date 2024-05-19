package com.rs.game.content.holidayevent_items;
import com.rs.lib.game.Animation;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class SaltyClawsHat {

    public static ItemClickHandler SaltyClawsHat = new ItemClickHandler(new Object[] { 20077 }, new String[] { "Dance" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(329));

    });
}
