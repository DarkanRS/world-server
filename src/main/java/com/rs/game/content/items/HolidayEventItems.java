package com.rs.game.content.items;

import com.rs.engine.dialogue.Dialogue;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.player.Equipment;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import java.util.Timer;
import java.util.TimerTask;

@PluginEventHandler
public class HolidayEventItems {

    public static ItemClickHandler BuskinAndSockMask = new ItemClickHandler(new Object[]{22322, 22323}, new String[]{"Flip"}, e -> {
        int swap = e.getItem().getId() == 22322 ? 22323 : 22322;
        if (e.isEquipped()) {
            e.getPlayer().getEquipment().setSlot(Equipment.HEAD, new Item(swap));
            e.getPlayer().getAppearance().generateAppearanceData();
        } else
            e.getPlayer().getInventory().replace(e.getItem(), new Item(swap));
    });

    public static ItemClickHandler CandyCane = new ItemClickHandler(new Object[]{15426}, new String[]{"Spin"}, e -> {
        e.getPlayer().setNextAnimation(new Animation(12664));
    });

    public static ItemClickHandler HeimlandGamesSouvenir = new ItemClickHandler(new Object[]{20078}, new String[]{"Snowsplosion"}, e -> {
        e.getPlayer().setNextAnimation(new Animation(15098));
        e.getPlayer().setNextSpotAnim(new SpotAnim(1283));
    });

    public static ItemClickHandler handleRedMarionette = new ItemClickHandler(new Object[]{"Red marionette"}, new String[]{"Jump", "Walk", "Bow", "Dance"}, e -> {
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

    public static ItemClickHandler handleGreenMarionette = new ItemClickHandler(new Object[]{"Green marionette"}, new String[]{"Jump", "Walk", "Bow", "Dance"}, e -> {
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

    public static ItemClickHandler handleBlueMarionette = new ItemClickHandler(new Object[]{"Blue marionette"}, new String[]{"Jump", "Walk", "Bow", "Dance"}, e -> {
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

    public static ItemClickHandler handleReinhat = new ItemClickHandler(new Object[]{10507}, new String[]{"Emote"}, e -> {
        e.getPlayer().setNextAnimation(new Animation(5059));
        e.getPlayer().setNextSpotAnim(new SpotAnim(859));
        e.getPlayer().setNextSpotAnim(new SpotAnim(263));
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                e.getPlayer().setNextSpotAnim(new SpotAnim(263));
            }
        }, 4000); // 4000 milliseconds = 4 seconds
    });

    public static ItemClickHandler handleRubberChicken = new ItemClickHandler(new Object[]{4566}, new String[]{"Dance", "Operate"}, e -> {
        e.getPlayer().setNextAnimation(new Animation(1835));
        e.getPlayer().jingle(99);
        e.getPlayer().soundEffect(355, 100, false);
    });

    public static ItemClickHandler SaltyClawsHat = new ItemClickHandler(new Object[]{20077}, new String[]{"Dance"}, e -> {
        e.getPlayer().setNextAnimation(new Animation(329));
    });

    public static ItemClickHandler Snowglobe = new ItemClickHandler(new Object[]{11949}, e -> {
        e.getPlayer().setNextAnimation(new Animation(2926));
        e.getPlayer().startConversation(new Dialogue().addNext(new Dialogue(() -> {
            e.getPlayer().getInterfaceManager().sendInterface(659);
            e.getPlayer().setCloseInterfacesEvent(() -> e.getPlayer().setNextAnimation(new Animation(7538)));
        })).addNext(new Dialogue(() -> {
            e.getPlayer().closeInterfaces();
            e.getPlayer().setNextAnimation(new Animation(7528));
            e.getPlayer().setNextSpotAnim(new SpotAnim(1284));
            e.getPlayer().getInventory().addItem(11951, e.getPlayer().getInventory().getFreeSlots());
        })));
    });

    public static ItemClickHandler SquirrelEars = new ItemClickHandler(new Object[]{"Squirrel ears"}, new String[]{"Summon Minion", "Juggle"}, e -> {
        switch (e.getOption()) {
            case "Summon Minion":
                e.getPlayer().setNextAnimation(new Animation(-1)); //Still looking for this one.
                e.getPlayer().setNextSpotAnim(new SpotAnim(-1));
                break;
            case "Juggle":
                e.getPlayer().setNextAnimation(new Animation(12265));
                e.getPlayer().setNextSpotAnim(new SpotAnim(2145));
                break;
        }
    });

    public static ItemClickHandler ToyHorsey = new ItemClickHandler(new Object[]{2520, 2522, 2524, 2526}, new String[]{"Play-with"}, e -> {
        int itemId = 2520;
        if (itemId >= 2520 && itemId <= 2526) {
            String[] phrases = {
                    "Come on Dobbin, we can win the race!",
                    "Hi-ho Silver, and away!",
                    "Neaahhhyyy! Giddy-up horsey!",
                    "Just say neigh to gambling!"
            };
            e.getPlayer().setNextAnimation(new Animation(918));
            e.getPlayer().setNextForceTalk(new ForceTalk(phrases[Utils.random(phrases.length)]));
        }
    });

    public static ItemClickHandler Yoyo = new ItemClickHandler(new Object[]{4079}, new String[]{"Play", "Loop", "Walk", "Crazy"}, e -> {
        switch (e.getOption()) {
            case "Play":
                e.getPlayer().setNextAnimation(new Animation(1457));    // The animation on "play" on weird. Needs to be looked at/fixed. It is the correct ID.
                break;
            case "Loop":
                e.getPlayer().setNextAnimation(new Animation(1458));
                break;
            case "Walk":
                e.getPlayer().setNextAnimation(new Animation(1459));
                break;
            case "Crazy":
                e.getPlayer().setNextAnimation(new Animation(1460));
                break;
        }
    });

    public static ItemClickHandler ZombieHead = new ItemClickHandler(new Object[]{6722, 10731}, new String[]{"Talk-At", "Display", "Question"}, e -> {
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
