package com.rs.game.content.celebrationitems;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

import java.util.Timer;
import java.util.TimerTask;

@PluginEventHandler
public class CelebrationItems {

    public static ItemClickHandler BubbleMaker = new ItemClickHandler(new Object[] { 20716 }, new String[] { "Blow-bubbles" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(10940));
        e.getPlayer().setNextSpotAnim(new SpotAnim(721));
    });

    public static ItemClickHandler Confetti = new ItemClickHandler(new Object[] { 20718 }, new String[] { "Throw" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(10952));
        e.getPlayer().setNextSpotAnim(new SpotAnim(1341));
    });

    public static ItemClickHandler Diamond_Crown = new ItemClickHandler(new Object[] { 24419 }, new String[] { "Celebrate" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(16915));
    });

    public static ItemClickHandler Diamond_Sceptre = new ItemClickHandler(new Object[] { 24418 }, new String[] { "Celebrate" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(16916));
    });

    public static ItemClickHandler Jubilee_Souvenir_Flags = new ItemClickHandler(new Object[] { 24412, 24414, 24416 }, new String[] { "Wave" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(16917));
    });

    public static ItemClickHandler SouvenirMug = new ItemClickHandler(new Object[] { 20725 }, new String[] { "Polish" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(10942));
    });

    public static ItemClickHandler ChromeGoggles = new ItemClickHandler(new Object[] { 22412 }, new String[] { "Emote" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(15185));
        e.getPlayer().setNextSpotAnim(new SpotAnim(1961));
    });

    public static ItemClickHandler TenthAnniversaryCake = new ItemClickHandler(new Object[] { 20113 }, new String[] { "Celebrate" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(6292));
        e.getPlayer().setNextSpotAnim(new SpotAnim(2964));
    });

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

    public static ItemClickHandler handle = new ItemClickHandler(new Object[] { 12844 }, new String[] { "Fly" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(8990));

    });

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
