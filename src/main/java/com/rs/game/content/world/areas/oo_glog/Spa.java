package com.rs.game.content.world.areas.oo_glog;

import com.rs.engine.quest.Quest;
import com.rs.game.content.Effect;
import com.rs.game.content.world.AgilityShortcuts;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class Spa {

    public static ObjectClickHandler Mud = new ObjectClickHandler(new Object[] { 29004 }, e -> {
        if (!e.isAtObject())
            return;
        if(!hasRequirements(e.getPlayer()))
            return;
        jumpIn(e.getObject(),e.getPlayer());
        WorldTasks.delay(4, () -> {
            if (e.getPlayer().getTile().withinArea(2592, 2859, 2600, 2862)) {
                e.getPlayer().sendMessage("You jump into the pit of thick, squelching mud.");
                e.getPlayer().addEffect(Effect.MUD, Ticks.fromHours(1));
                e.getPlayer().sendMessage("After a bath in such luxuriant mud, you feel your Hunter skills are extra keen.");
            }
        });
    });

    public static ObjectClickHandler Thermal = new ObjectClickHandler(new Object[] { 29044 }, e -> {
        if (!e.isAtObject())
            return;
        if(!hasRequirements(e.getPlayer()))
            return;
        jumpIn(e.getObject(),e.getPlayer());
        WorldTasks.delay(4, () -> {
            if (e.getPlayer().getTile().withinArea(2572, 2863, 2577, 2866)) {
                e.getPlayer().sendMessage("You jump into the warm, relaxing thermal bath.");
                e.getPlayer().addEffect(Effect.THERMAL, Ticks.fromHours(1));
                e.getPlayer().sendMessage("You feel restored and invigorated.");
            }
            });
    });

    public static ObjectClickHandler Spring = new ObjectClickHandler(new Object[] { 29031 }, e -> {
        if (!e.isAtObject())
            return;
        if(!hasRequirements(e.getPlayer()))
            return;
        jumpIn(e.getObject(),e.getPlayer());
        WorldTasks.delay(4, () -> {
            if (e.getPlayer().getTile().withinArea(2555, 2861, 2559, 2866)) {
                e.getPlayer().sendMessage("You jump into the limpid, salty waters.");
                e.getPlayer().addEffect(Effect.SALTWATER, Ticks.fromMinutes(Math.floor((double) Utils.random(9, 24) / 3)));
                e.getPlayer().sendMessage("You feel energised and hastened after your relaxing soak.");
            }
        });
    });

    public static ObjectClickHandler Sulphur = new ObjectClickHandler(new Object[] { 29018 }, e -> {
        if (!e.isAtObject())
            return;
        if(!hasRequirements(e.getPlayer()))
            return;
        jumpIn(e.getObject(),e.getPlayer());
        WorldTasks.delay(4, () -> {
            if (e.getPlayer().getTile().withinArea(2533, 2853, 2541, 2856)) {
                e.getPlayer().sendMessage("You jump into the bubbling, sulphurous waters.");
                e.getPlayer().addEffect(Effect.SULPHUR, Ticks.fromHours(1));
                e.getPlayer().sendMessage("You feel serene after your rest. Your Prayer points have been restored.");
            }
        });
    });

    public static ObjectClickHandler Banados = new ObjectClickHandler(new Object[] { 29057 }, e -> {
        if(!hasRequirements(e.getPlayer()))
            return;
        if (!e.isAtObject())
            return;
        jumpIn(e.getObject(),e.getPlayer());
        WorldTasks.delay(4, () -> {
            if (e.getPlayer().getTile().withinArea(2522, 2842, 2527, 2848)) {
                e.getPlayer().sendMessage("You jump into the copper-infused waters.");
                e.getPlayer().addEffect(Effect.BANDOSPOOL, Ticks.fromHours(1));
                e.getPlayer().sendMessage("You feel the favour of Bandos wash over you.");
            }
        });
    });

    private static void jumpIn(GameObject object, Player player){
        switch (object.getRotation()) {
            case 3 -> AgilityShortcuts.climbOver(player, player.transform(0, player.getY() >= object.getY() ? -3 : 3, 0), 839);
            case 0 -> AgilityShortcuts.climbOver(player, player.transform(player.getX() >= object.getX() ? -3 : 3, 0, 0), 839);
            case 1 -> AgilityShortcuts.climbOver(player, player.transform(0, player.getY() >= object.getY() ? -3 : 3, 0), 839);
            case 2 -> AgilityShortcuts.climbOver(player, player.transform(player.getX() >= object.getX() ? -3 : 3, 0, 0), 839);
        }
    }

    private static boolean hasRequirements(Player player){
        if (player.isQuestComplete(Quest.AS_A_FIRST_RESORT))
            return true;
        else
            return false;
    }
}
