package com.rs.game.content.holidayevent_items;

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.InterfaceManager;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class EasterRing {

    public static ItemClickHandler handleItemOption = new ItemClickHandler(new Object[]{"Easter ring",}, new String[]{"Wear"}, e -> {
        if (e.getPlayer().inCombat(10000) || e.getPlayer().hasBeenHit(10000)) {
            return;
        }

        if (e.getItem().getName().equals("Easter ring")) {
            transformInto(e.getPlayer(), 3689 + Utils.random(5));
            e.getPlayer().soundEffect(1520, false);
        }
    });

    public static ButtonClickHandler handleDeactivationButton = new ButtonClickHandler(375, e -> {
        if (e.getComponentId() == 3)
            deactivateTransformation(e.getPlayer());
    });

    public static void transformInto(Player player, int npcId) {
        player.stopAll(true, true, true);
        player.lock();
        player.getAppearance().transformIntoNPC(npcId);
        player.getInterfaceManager().sendSub(InterfaceManager.Sub.TAB_INVENTORY, 375);
        player.getTempAttribs().setB("TransformationRing", true);
    }

    public static void deactivateTransformation(Player player) {
        player.getTempAttribs().removeB("TransformationRing");
        player.unlock();
        player.setNextAnimation(new Animation(14884));
        player.getAppearance().transformIntoNPC(-1);
        player.getInterfaceManager().sendSubDefault(InterfaceManager.Sub.TAB_INVENTORY);
    }
    }

