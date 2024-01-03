package com.rs.game.content.minigames

import com.rs.game.model.entity.player.Player

val MINIGAME_SUPPLIES = arrayOf(12850, 12851, 4049, 4050, 18715, 18716, 18717, 18718, 22373, 22374, 22375, 22376, 22379, 22380)

fun giveFoodAndPotions(player: Player) {
    checkAndDeleteFoodAndPotions(player)
    player.inventory.addItem(12850, 10000)
    player.inventory.addItem(12851, 10000)
    player.inventory.addItem(18715, 1)
    player.inventory.addItem(22373, 2)
    player.inventory.addItem(22379, 2)
    player.inventory.addItem(22375, 1)
    player.inventory.addItem(4049, player.inventory.freeSlots)
}

fun checkAndDeleteFoodAndPotions(player: Player) {
    player.inventory.items.array().forEach { item ->
        if (item != null && isMinigameSupply(item.id))
            player.inventory.deleteItem(item)
    }
}

fun isMinigameSupply(id: Int): Boolean {
    return MINIGAME_SUPPLIES.any { it == id }
}
