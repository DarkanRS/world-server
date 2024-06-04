package com.rs.game.content.holidayevent_items;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class Marionette {

    public static ItemClickHandler handleRedMarionette = new ItemClickHandler(new Object[] { "Red marionette" }, new String[] { "Jump", "Walk", "Bow", "Dance" }, e -> {
        switch (e.getOption()) {
            case "Jump":
                e.getPlayer().setNextAnimation(new Animation(3003));
                e.getPlayer().setNextSpotAnim(new SpotAnim(507));
                break;
            case "Walk":
                e.getPlayer().setNextAnimation(new Animation(3004));
                e.getPlayer().setNextSpotAnim(new SpotAnim(508));
                break;
            case "Bow":
                e.getPlayer().setNextAnimation(new Animation(3005));
                e.getPlayer().setNextSpotAnim(new SpotAnim(509));
                break;
            case "Dance":
                e.getPlayer().setNextAnimation(new Animation(3006));
                e.getPlayer().setNextSpotAnim(new SpotAnim(510));
                break;
        }
    });

    public static ItemClickHandler handleGreenMarionette = new ItemClickHandler(new Object[] { "Green marionette" }, new String[] { "Jump", "Walk", "Bow", "Dance" }, e -> {
        switch (e.getOption()) {
            case "Jump":
                e.getPlayer().setNextAnimation(new Animation(3003));
                e.getPlayer().setNextSpotAnim(new SpotAnim(515));
                break;
            case "Walk":
                e.getPlayer().setNextAnimation(new Animation(3004));
                e.getPlayer().setNextSpotAnim(new SpotAnim(516));
                break;
            case "Bow":
                e.getPlayer().setNextAnimation(new Animation(3005));
                e.getPlayer().setNextSpotAnim(new SpotAnim(517));
                break;
            case "Dance":
                e.getPlayer().setNextAnimation(new Animation(3006));
                e.getPlayer().setNextSpotAnim(new SpotAnim(518));
                break;
        }
    });

    public static ItemClickHandler handleBlueMarionette = new ItemClickHandler(new Object[] { "Blue marionette" }, new String[] { "Jump", "Walk", "Bow", "Dance" }, e -> {
        switch (e.getOption()) {
            case "Jump":
                e.getPlayer().setNextAnimation(new Animation(3003));
                e.getPlayer().setNextSpotAnim(new SpotAnim(511));
                break;
            case "Walk":
                e.getPlayer().setNextAnimation(new Animation(3004));
                e.getPlayer().setNextSpotAnim(new SpotAnim(512));
                break;
            case "Bow":
                e.getPlayer().setNextAnimation(new Animation(3005));
                e.getPlayer().setNextSpotAnim(new SpotAnim(513));
                break;
            case "Dance":
                e.getPlayer().setNextAnimation(new Animation(3006));
                e.getPlayer().setNextSpotAnim(new SpotAnim(514));
                break;
        }
    });
}

