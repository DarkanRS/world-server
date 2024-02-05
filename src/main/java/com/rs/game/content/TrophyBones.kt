package com.rs.game.content

import com.rs.game.content.items.LootInterface
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemAddedToBank
import com.rs.plugin.kts.onItemOnObject
import com.rs.utils.WorldUtil.gsonTreeMapToItemContainer

@ServerStartupEvent
fun mapTrophyBones() {
    onItemAddedToBank(24444) { e ->
        e.player.sendMessage("No boneception today.")
        e.cancel()
    }

    onItemOnObject(arrayOf("Bank booth", "Bank", "Bank chest", "Bank table", "Counter", "Shantay chest", "Darkmeyer Treasury"), arrayOf(24444)) { e ->
        e.apply {
            if (!getObject().getDefinitions(player).containsOption("Bank") && !getObject().getDefinitions(player).containsOption("Use")) {
                player.sendMessage("This isn't a proper bank.")
                return@onItemOnObject
            }
            player.inventory.items.set(item.slot, null)
            player.inventory.refresh(item.slot)

            val items = gsonTreeMapToItemContainer(item.getMetaDataO("trophyBoneItems"))
            if (items == null) {
                player.sendMessage("Your trophy bones don't contain anything.")
                return@onItemOnObject
            }
            LootInterface.open((item.getMetaDataO("trophyBoneOriginator") ?: "Unknown") + "'s Corpse", e.player, items)
        }
    }
}