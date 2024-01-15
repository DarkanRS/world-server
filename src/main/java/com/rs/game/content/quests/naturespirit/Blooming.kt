package com.rs.game.content.quests.naturespirit

import com.rs.cache.loaders.ItemDefinitions
import com.rs.cache.loaders.ObjectType
import com.rs.game.World
import com.rs.game.tasks.WorldTasks
import com.rs.lib.game.Animation
import com.rs.lib.game.SpotAnim
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.events.ItemClickEvent
import com.rs.plugin.events.ObjectClickEvent
import com.rs.plugin.handlers.ItemClickHandler
import com.rs.plugin.handlers.ObjectClickHandler
import com.rs.plugin.kts.onItemClick
import com.rs.plugin.kts.onObjectClick
import com.rs.utils.Ticks
import java.util.*
import java.util.function.Consumer

enum class BloomResource(val druidPouch: Int, val itemId: Int, val objectIds: IntArray) {
    FUNGI_ON_LOG(1, 2970, intArrayOf(3509, 50736)),
    BUDDING_BRANCH(2, 2972, intArrayOf(3511)),
    GOLDEN_PEAR_BUSH(3, 2974, intArrayOf(3513));

    companion object {
        fun forObject(id: Int): BloomResource? = entries.firstOrNull { it.objectIds.contains(id) }
    }
}

@ServerStartupEvent
fun mapBlooming() {
    onObjectClick(arrayOf(3509, 3511, 3513, 50736)) { e ->
        val product = BloomResource.forObject(e.objectId)!!
        val productName = ItemDefinitions.getDefs(product.itemId).getName().lowercase(Locale.getDefault())

        e.player.anim(3659)
        e.player.lock(1)
        e.player.tasks.schedule {
            if (e.player.inventory.addItemDrop(product.itemId, 1)) {
                e.player.sendMessage("You pick a $productName.")
                e.player.incrementCount("$productName bloomed", 1)
                e.getObject().setId(e.getObject().originalId)
            }
        }
    }

    onItemClick(2963, options = arrayOf("Bloom")) { e ->
        if (e.player.prayer.points >= 60) {
            e.player.prayer.drainPrayer(Utils.random(10.0, 60.0))
            e.player.lock(2)
            e.player.anim(9104)
            for (x in -1..1) for (y in -1..1) {
                if (x == 0 && y == 0) continue
                World.sendSpotAnim(e.player.transform(x, y), SpotAnim(263))
                val obj = World.getObject(e.player.transform(x, y), ObjectType.SCENERY_INTERACT) ?: continue
                when (obj.id) {
                    3512, 3510 -> obj.setIdTemporary(obj.id + 1, Ticks.fromSeconds(30))
                    3508, 50718, 50746 -> obj.setIdTemporary(3509, Ticks.fromSeconds(30))
                }
            }
            return@onItemClick
        }
        e.player.sendMessage("You need more prayer points to do this.")
    }
}