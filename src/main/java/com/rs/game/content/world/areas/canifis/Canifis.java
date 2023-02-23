package com.rs.game.content.world.areas.canifis;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Canifis {

    public static ObjectClickHandler handleNettles = new ObjectClickHandler(new Object[]{"Nettles"}, e -> {
        switch (e.getOption()) {
            case "Pick" -> {
                if (e.getPlayer().getEquipment().wearingGloves())
                    if (e.getPlayer().getInventory().hasFreeSlots()) {
                        e.getPlayer().getInventory().addItem(4241, 1);
                        e.getPlayer().sendMessage("You pick some nettles");
                    } else {
                        e.getPlayer().sendMessage("You have no free space");
                    }
                else {
                    e.getPlayer().sendMessage("You shouldn't try picking these with your bare hands..");
                }
            }
        }

    });
}
