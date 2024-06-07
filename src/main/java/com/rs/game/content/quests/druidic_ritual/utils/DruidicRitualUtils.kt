package com.rs.game.content.quests.druidic_ritual.utils

import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.lib.util.Utils

class DruidicRitualUtils(val player: Player) {

    fun handleEnchantedMeat(rawItemId: Int, enchantedItemId: Int) {
        if (player.inventory.containsItem(rawItemId) && !player.inventory.containsItem(enchantedItemId)) {
            player.inventory.replace(rawItemId, enchantedItemId)
        } else if (player.inventory.containsItem(enchantedItemId)) {
            player.sendMessage("You already have ${Utils.addArticle(Item(enchantedItemId).name)}.")
        }
    }

    fun hasEnchantedItems(): Boolean {
        return player.inventory.containsItem(ENCHANTED_RAW_BEAR_MEAT) &&
                player.inventory.containsItem(ENCHANTED_RAW_RAT_MEAT) &&
                player.inventory.containsItem(ENCHANTED_RAW_CHICKEN) &&
                player.inventory.containsItem(ENCHANTED_RAW_BEEF)
    }

}
