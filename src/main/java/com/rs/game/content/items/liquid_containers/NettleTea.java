package com.rs.game.content.items.liquid_containers;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;

@PluginEventHandler

public class NettleTea {
    private static final int nettleTea = 4239;
    private static final int emptyPorcelainCup = 4244;
    private static final int cupOfNettleTea = 4245;
    private static final int emptyBowl = 1923;
    private static final int milkBucket = 1927;
    private static final int milkyNettleTea = 4246;
    private static final int emptyBucket = 1925;

    public static ItemOnItemHandler handleNettleTeaBowl = new ItemOnItemHandler(nettleTea, emptyPorcelainCup, e -> {
        if (!e.getPlayer().getInventory().containsItem(nettleTea, 1) && !e.getPlayer().getInventory().containsItem(emptyPorcelainCup, 1))
            return;
        e.getItem1().setId(cupOfNettleTea);
        e.getItem2().setId(emptyBowl);
        e.getPlayer().getInventory().refresh();
    });

    public static ItemOnItemHandler handleMilkyNettleTea = new ItemOnItemHandler(cupOfNettleTea, milkBucket, e -> {
        if (!e.getPlayer().getInventory().containsItem(nettleTea, 1) && !e.getPlayer().getInventory().containsItem(milkBucket, 1))
            return;
        e.getItem1().setId(milkyNettleTea);
        e.getItem2().setId(emptyBucket);
        e.getPlayer().getInventory().refresh();
    });
}