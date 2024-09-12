package com.rs.game.content.world.areas.oo_glog;

import com.rs.engine.quest.Quest;
import com.rs.game.content.Effect;
import com.rs.game.content.world.areas.global.AgilityShortcuts;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.Constants;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class Spa {

    public static ObjectClickHandler Mud = new ObjectClickHandler(new Object[] { 29004 }, e -> {
        if(!e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT, "to use the spa."))
            return;
        jumpIn(e.getObject(),e.getPlayer());
        e.getPlayer().getTasks().schedule(4, () -> {
            if (e.getPlayer().getTile().withinArea(2592, 2859, 2600, 2862)) {
                e.getPlayer().sendMessage("You jump into the pit of thick, squelching mud.");
                e.getPlayer().getSkills().adjustStat(0, 0.1, Constants.HUNTER);
                e.getPlayer().sendMessage("After a bath in such luxuriant mud, you feel your Hunter skills are extra keen.");
            }
        });
    });

    public static ObjectClickHandler Thermal = new ObjectClickHandler(new Object[] { 29044 }, e -> {
        if(!e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT, "to use the spa."))
            return;
        jumpIn(e.getObject(),e.getPlayer());
        e.getPlayer().getTasks().schedule(4, () -> {
            if (e.getPlayer().getTile().withinArea(2572, 2863, 2577, 2866)) {
                e.getPlayer().sendMessage("You jump into the warm, relaxing thermal bath.");
                e.getPlayer().addEffect(Effect.OOG_THERMAL_POOL, Ticks.fromHours(1));
                e.getPlayer().sendMessage("You feel restored and invigorated.");
            }
            });
    });

    public static ObjectClickHandler Spring = new ObjectClickHandler(new Object[] { 29031 }, e -> {
        if(!e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT, "to use the spa."))
            return;
        jumpIn(e.getObject(),e.getPlayer());
        e.getPlayer().getTasks().schedule(4, () -> {
            if (e.getPlayer().getTile().withinArea(2555, 2861, 2559, 2866)) {
                e.getPlayer().sendMessage("You jump into the limpid, salty waters.");
                e.getPlayer().addEffect(Effect.OOG_SALTWATER_POOL, Ticks.fromMinutes(Utils.random(9, 24)));
                e.getPlayer().sendMessage("You feel energised and hastened after your relaxing soak.");
            }
        });
    });

    public static ObjectClickHandler Sulphur = new ObjectClickHandler(new Object[] { 29018 }, e -> {
        if(!e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT, "to use the spa."))
            return;
        jumpIn(e.getObject(),e.getPlayer());
        e.getPlayer().getTasks().schedule(4, () -> {
            if (e.getPlayer().getTile().withinArea(2533, 2853, 2541, 2856)) {
                e.getPlayer().sendMessage("You jump into the bubbling, sulphurous waters.");
                e.getPlayer().getPrayer().restorePrayer(e.getPlayer().getSkills().getLevelForXp(Constants.PRAYER) * 10);
                e.getPlayer().sendMessage("You feel serene after your rest. Your Prayer points have been restored.");
            }
        });
    });

    public static ObjectClickHandler Banados = new ObjectClickHandler(new Object[] { 29057 }, e -> {
        if(!e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT, "to use the spa."))
            return;
        jumpIn(e.getObject(),e.getPlayer());
        e.getPlayer().getTasks().schedule(4, () -> {
            if (e.getPlayer().getTile().withinArea(2522, 2842, 2527, 2848)) {
                e.getPlayer().sendMessage("You jump into the copper-infused waters.");
                e.getPlayer().addEffect(Effect.OOG_BANDOS_POOL, Ticks.fromHours(1));
                e.getPlayer().sendMessage("You feel the favour of Bandos wash over you.");
            }
        });
    });

    private static void jumpIn(GameObject object, Player player){
        switch (object.getRotation()) {
            case 3, 1 -> AgilityShortcuts.climbOver(player, player.transform(0, player.getY() >= object.getY() ? -3 : 3, 0), 839);
            case 0, 2 -> AgilityShortcuts.climbOver(player, player.transform(player.getX() >= object.getX() ? -3 : 3, 0, 0), 839);
        }
    }
}
