package com.rs.game.content.quests.naturespirit

import com.rs.cache.loaders.ItemDefinitions
import com.rs.cache.loaders.ObjectType
import com.rs.engine.quest.Quest
import com.rs.game.World
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.SpotAnim
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemClick
import com.rs.plugin.kts.onObjectClick
import com.rs.utils.Ticks
import java.util.*

enum class BloomResource(val druidPouch: Int, val itemId: Int, val objectIds: Map<Int, Int>) {
    FUNGI_ON_LOG(1, 2970, mapOf(3508 to 3509, 50718 to 50736, 50746 to 50791)),
    BUDDING_BRANCH(2, 2972, mapOf(3510 to 3511, 50838 to 50853, 50895 to 50907)),
    GOLDEN_PEAR_BUSH(3, 2974, mapOf(3512 to 3513, 50984 to 51073, 51081 to 51088));

    companion object {
        fun forItem(id: Int): BloomResource? = entries.firstOrNull { it.itemId == id }
        fun forPickableObject(id: Int): BloomResource? = entries.firstOrNull { it.objectIds.containsValue(id) }
        fun forEmptyObject(id: Int): BloomResource? = entries.firstOrNull { it.objectIds.containsKey(id) }
    }
}

@ServerStartupEvent
fun mapBlooming() {
    onItemClick(2957, 2958, options = arrayOf("Fill")) { e ->
        with(e.player) {
            if (getQuestStage(Quest.NATURE_SPIRIT) < STAGE_KILL_GHASTS) {
                sendMessage("You haven't been taught how to use this yet.")
                return@onItemClick
            }
            var filled = false
            inventory.items.array().forEach { item ->
                item?.let {
                    BloomResource.forItem(item.id)?.also {
                        filled = true
                        inventory.deleteItem(item)
                        e.item.amount += it.druidPouch
                        if (e.item.id == 2957) {
                            e.item.id = 2958
                            e.item.amount--
                        }
                    }
                }
            }

            if (filled) inventory.refresh()
            else sendMessage("You need some blossomed items to add something to the druid pouch.")
        }
    }

    onObjectClick(*BloomResource.entries.flatMap { it.objectIds.values.toList() }.toTypedArray()) { e ->
        val product = BloomResource.forPickableObject(e.objectId)!!
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

    onItemClick(2963, options = arrayOf("Bloom")) { castBloom(it.player, true) }
    onItemClick(2968, options = arrayOf("Cast")) { e ->
        if (e.player.getQuestStage(Quest.NATURE_SPIRIT) <= STAGE_GET_BLESSED) {
            e.player.sendMessage("You need to be blessed before you can cast this.")
            return@onItemClick
        }
        castBloom(e.player, false)
        e.item.id = 2969
        e.player.inventory.refresh(e.item.slot)
        e.player.questManager.getAttribs(Quest.NATURE_SPIRIT).setB("castedBloom", true)
    }
}

fun castBloom(player: Player, sickle: Boolean) {
    if (player.prayer.points < 60) {
        player.sendMessage("You need more prayer points to do this.")
        return
    }
    player.prayer.drainPrayer(Utils.random(10.0, 60.0))
    player.lock(2)
    player.anim(if (sickle) 9104 else 9098)
    for (x in -1..1) for (y in -1..1) {
        if (x == 0 && y == 0) continue
        World.sendSpotAnim(player.transform(x, y), SpotAnim(263))
        val obj = World.getObject(player.transform(x, y), ObjectType.SCENERY_INTERACT) ?: continue
        val resource = BloomResource.forEmptyObject(obj.id) ?: continue
        if (!sickle && resource != BloomResource.FUNGI_ON_LOG) continue
        resource.objectIds[obj.id]?.let { obj.setIdTemporary(it, Ticks.fromSeconds(30)) }
    }
}