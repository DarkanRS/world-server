package com.rs.game.content.world

import com.rs.game.World
import com.rs.game.content.quests.piratestreasure.PiratesTreasure
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.game.tasks.WorldTasks
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick
import com.rs.utils.Ticks
import java.util.stream.IntStream.range

@ServerStartupEvent
fun mapPickables() {
    onObjectClick("Flax", "Cabbage", "Potato", "Wheat", "Onion", "Pineapple Plant") { (player, obj, option) ->
        if (option == "Pick") {
            when (obj.getDefinitions(player).name) {
                "Flax" -> pick(player, obj, 1779)
                "Cabbage" -> pick(player, obj, 1965)
                "Potato" -> pick(player, obj, 1942)
                "Wheat" -> pick(player, obj, 1947)
                "Onion" -> pick(player, obj, 1957)
                "Pineapple Plant" -> pick(player, obj, 2114)
            }
        }
    }

    onObjectClick(2073, 2074, 2075, 2076, 2077, 2078) { (player, obj) ->
        if (obj.id == 2078) {
            player.sendMessage("There are no bananas left on the tree.")
            return@onObjectClick
        }
        player.anim(2280)
        player.inventory.addItem(1963, 1)
        obj.setIdTemporary(obj.id + 1, Ticks.fromSeconds(30))
    }

    onObjectClick(23625, 23626, 23627, 23628, 23629, 23630) { (player, obj) ->
        if (obj.id == 23627 || obj.id == 23630) {
            player.sendMessage("There are no berries left on the bush.")
            return@onObjectClick
        }
        player.anim(2280)
        player.inventory.addItem(if (obj.definitions.name.contains("Red")) 1951 else 753, 1)
        obj.setId(obj.id + 1)
        obj.tasks.scheduleTimer("regrow", Ticks.fromSeconds(15)) {
            if (obj.id > obj.originalId)
                obj.setId(obj.id - 1)
            else
                return@scheduleTimer false
            return@scheduleTimer true
        }
    }
}

private fun pick(player: Player, obj: GameObject, itemId: Int) {
    if (obj.id == 1413) {
        player.sendMessage("There aren't any pineapples left to pick.")
        return
    }
    if (player.inventory.addItem(itemId, 1)) {
        player.anim(827)
        player.lock(2)
        when (itemId) {
            1799 -> if (Utils.random(5) == 0) World.removeObjectTemporary(obj, Ticks.fromMinutes(1))
            2114 -> if (Utils.random(5) == 0) obj.setIdTemporary(1413, Ticks.fromMinutes(1))
            else -> World.removeObjectTemporary(obj, Ticks.fromMinutes(1))
        }
    }
}