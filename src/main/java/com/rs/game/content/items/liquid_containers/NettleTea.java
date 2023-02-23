package com.rs.game.content.items.liquid_containers;

import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;

@PluginEventHandler

public class NettleTea {
    private static int nettleTea = 4239;
    private static int emptyPorcelainCup = 4244;
    private static int cupOfNettleTea = 4245;
    private static int emptyBowl = 1923;
    private static int milkBucket = 1927;
    private static int milkyNettleTea = 4246;
    private static int emptyBucket = 1925;

    public static ItemOnItemHandler handleNettleTeaBowl = new ItemOnItemHandler(new int[]{ nettleTea, emptyPorcelainCup }, e -> {
        if (e.getPlayer().getInventory().containsItem(nettleTea, 1))
            if (e.getPlayer().getInventory().containsItem(emptyPorcelainCup, 1))
                e.getPlayer().getInventory().removeItems(new Item(nettleTea, 1), new Item(emptyPorcelainCup, 1));
        e.getPlayer().getInventory().addItem(new Item(cupOfNettleTea, 1), true);
        e.getPlayer().getInventory().addItem(new Item(emptyBowl, 1), true);
    });

    public static ItemOnItemHandler handleNettleTeaCup = new ItemOnItemHandler(new int[]{ cupOfNettleTea, milkBucket }, e -> {
        if (e.getPlayer().getInventory().containsItem(nettleTea, 1))
            if (e.getPlayer().getInventory().containsItem(milkBucket, 1))
                e.getPlayer().getInventory().removeItems(new Item(nettleTea, 1), new Item(milkBucket, 1));
        e.getPlayer().getInventory().addItem(new Item(milkyNettleTea, 1), true);
        e.getPlayer().getInventory().addItem(new Item(emptyBucket, 1), true);
    });


    public static ItemClickHandler handleTeaCup = new ItemClickHandler(new Object[]{ 4245, 4246 }, new String[] { "Drink" }, e -> {
            e.getPlayer().getInventory().removeItems(e.getItem());
            e.getPlayer().heal(500);
            e.getPlayer().restoreRunEnergy(20);
            e.getPlayer().getInventory().addItem(4244, 1);
    });


    public static ItemClickHandler handleTeaBowl = new ItemClickHandler(new Object[]{ 4239, 4240 }, new String[] { "Drink" }, e -> {
            e.getPlayer().getInventory().removeItems(e.getItem());
            e.getPlayer().heal(500);
            e.getPlayer().restoreRunEnergy(20);
            e.getPlayer().getInventory().addItem(1923, 1);
    });

    public static ItemClickHandler handleNettleWater = new ItemClickHandler(new Object[]{ 4237 }, new String[] { "Drink" }, e -> {
        if (e.getOption().equalsIgnoreCase("Drink")) {
            e.getPlayer().getInventory().removeItems(e.getItem());
            e.getPlayer().heal(1);
            e.getPlayer().getInventory().addItem(1923, 1);
        }
    });
}