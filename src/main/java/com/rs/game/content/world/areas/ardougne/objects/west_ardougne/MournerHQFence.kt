package com.rs.game.content.world.areas.ardougne.objects.west_ardougne

import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick

class MournerHQFence(player: Player, obj: GameObject) {
    init {
        val targetX = if (player.x > obj.x) Tile.of(obj.x+1, obj.y, 0) else Tile.of(obj.x, obj.y, 0)
        val hqTile = if (player.x > obj.x) Tile.of(2541, 3331, 0) else Tile.of(2542, 3331, 0)

        player.walkToAndExecute(targetX) {
            player.forceMove(hqTile, 3844, 25, 75)
            player.sendMessage("You squeeze through the fence.")
        }
    }
}

@ServerStartupEvent
fun mapMournerHQFence() {
    onObjectClick(2068) { (player, obj) -> MournerHQFence(player, obj) }
}
