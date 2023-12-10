package com.rs.game.content.minigames;

import com.rs.game.model.entity.player.Player;

import java.util.Arrays;

public class MinigameUtil {
    private static final int[] MINIGAME_SUPPLIES = { 12850, 12851, 4049, 4050, 18715, 18716, 18717, 18718, 22373, 22374, 22375, 22376, 22379, 22380 };
    public static void giveFoodAndPotions(Player player) {
        checkAndDeleteFoodAndPotions(player);
        player.getInventory().addItem(12850, 10000);
        player.getInventory().addItem(12851, 10000);
        player.getInventory().addItem(18715, 1);
        player.getInventory().addItem(22373, 2);
        player.getInventory().addItem(22379, 2);
        player.getInventory().addItem(22375, 1);
        player.getInventory().addItem(4049, player.getInventory().getFreeSlots());
    }

    public static void checkAndDeleteFoodAndPotions(Player player) {
        for (int i = 0;i < player.getInventory().getItemsContainerSize();i++) {
            if (player.getInventory().getItem(i) != null && isMinigameSupply(player.getInventory().getItem(i).getId()))
                player.getInventory().deleteItem(i, player.getInventory().getItem(i));
        }
    }

    public static boolean isMinigameSupply(int id) {
        return Arrays.stream(MINIGAME_SUPPLIES).anyMatch(minigame -> minigame == id);
    }
}
