package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.game.content.achievements.AchievementDef;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Naff {
    public static NPCClickHandler handleNaff = new NPCClickHandler(new Object[] { 359 }, e -> {
        int max = 8;
        if (AchievementDef.meetsRequirements(e.getPlayer(), AchievementDef.Area.VARROCK, AchievementDef.Difficulty.ELITE, false))
            max = 80;
        else if (AchievementDef.meetsRequirements(e.getPlayer(), AchievementDef.Area.VARROCK, AchievementDef.Difficulty.HARD, false))
            max = 64;
        else if (AchievementDef.meetsRequirements(e.getPlayer(), AchievementDef.Area.VARROCK, AchievementDef.Difficulty.MEDIUM, false))
            max = 32;
        else if (AchievementDef.meetsRequirements(e.getPlayer(), AchievementDef.Area.VARROCK, AchievementDef.Difficulty.EASY, false))
            max = 16;
        int amountLeft = max - e.getPlayer().getDailyI("naffStavesBought");
        if (amountLeft <= 0) {
            e.getPlayer().sendMessage("Naff has no staves left today.");
            return;
        }
        if (!e.getPlayer().getInventory().hasFreeSlots()) {
            e.getPlayer().sendMessage("You don't have enough inventory space to buy any staves.");
            return;
        }
        e.getPlayer().sendInputInteger("How many battlestaves would you like to buy? (" + amountLeft +" available)", amount -> {
            int coinsOnPlayer = e.getPlayer().getInventory().getCoinsAsInt();
            int maxBuyable = coinsOnPlayer / 7000;
            if (amount > maxBuyable)
                amount = maxBuyable;
            if (amount > amountLeft)
                amount = amountLeft;
            if (amount <= 0) {
                e.getPlayer().sendMessage("You don't have enough money to buy any staves right now.");
                return;
            }
            final int finalAmount = amount;
            final int cost = 7000 * amount;
            e.getPlayer().sendOptionDialogue("Buy " + amount + " battlestaves for " + Utils.formatNumber(cost) + " coins?", ops -> {
                ops.add("Yes", () -> {
                    if (!e.getPlayer().getInventory().hasCoins(cost)) {
                        e.getPlayer().sendMessage("You don't have enough money for that.");
                        return;
                    }
                    e.getPlayer().getInventory().removeCoins(cost);
                    e.getPlayer().getInventory().addItemDrop(1392, finalAmount);
                    e.getPlayer().setDailyI("naffStavesBought", e.getPlayer().getDailyI("naffStavesBought") + finalAmount);
                });
                ops.add("Not thanks.");
            });
        });
    });
}
