package com.rs.game.content.minigames.pyramidplunder

import com.rs.engine.pathfinder.Direction
import com.rs.game.World.getNPCsInChunkRange
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.player.Player
import com.rs.game.tasks.WorldTasks
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onChunkEnter
import com.rs.plugin.kts.onObjectClick
import com.rs.utils.Ticks

private val MUMMY_LOCATIONS = arrayOf(
    Tile.of(1934, 4458, 2), Tile.of(1968, 4428, 2),
    Tile.of(1934, 4428, 3), Tile.of(1968, 4458, 3)
)
private val MUMMY_CHUNKS = setOf(3952992, 4034880, 3952960, 4034912)
private var currentMummyRoom = 0

@ServerStartupEvent
fun mapOuterPyramid() {
    WorldTasks.scheduleTimer(0, Ticks.fromMinutes(10)) {
        currentMummyRoom = Utils.randomInclusive(0, 3)
        return@scheduleTimer true
    }

    onObjectClick(16543, 16544, 16545, 16546) { (player, obj) -> enterMummyRoom(player, MUMMY_LOCATIONS[obj.id-16543]) }

    onObjectClick(16459) { (player) ->
        when {
            player.withinDistance(MUMMY_LOCATIONS[0]) -> exitMummyRoom(player, Tile.of(3288, 2801, 0), Direction.NORTH)
            player.withinDistance(MUMMY_LOCATIONS[1]) -> exitMummyRoom(player, Tile.of(3295, 2795, 0), Direction.EAST)
            player.withinDistance(MUMMY_LOCATIONS[2]) -> exitMummyRoom(player, Tile.of(3289, 2788, 0), Direction.SOUTH)
            player.withinDistance(MUMMY_LOCATIONS[3]) -> exitMummyRoom(player, Tile.of(3282, 2794, 0), Direction.WEST)
        }
    }

    onChunkEnter { (entity, chunkId) ->
        val player = entity as? Player ?: return@onChunkEnter
        if (MUMMY_CHUNKS.contains(chunkId))
            moveMummy(player)
    }
}

private fun moveMummy(player: Player) {
    for (npc in getNPCsInChunkRange(player.chunkId, 2))
        if (npc.id == 4476 && npc.withinDistance(MUMMY_LOCATIONS[currentMummyRoom], 5))
            return
    for (npc in getNPCsInChunkRange(player.chunkId, 2))
        if (npc.id == 4476) npc.tele(MUMMY_LOCATIONS[currentMummyRoom])
}

private fun enterMummyRoom(player: Player, tile: Tile) {
    player.lock()
    player.schedule {
        player.interfaceManager.setFadingInterface(115)
        wait(4)
        player.faceDir(Direction.NORTH)
        player.tele(Tile.of(tile.x, tile.y - 8, tile.plane))
        wait(2)
        player.interfaceManager.setFadingInterface(170)
        wait(2)
        player.unlock()
    }
}

private fun exitMummyRoom(player: Player, tile: Tile, dir: Direction) {
    player.lock()
    player.schedule {
        player.interfaceManager.setFadingInterface(115)
        wait(4)
        player.faceDir(dir)
        player.tele(tile)
        wait(2)
        player.interfaceManager.setFadingInterface(170)
        wait(2)
        player.unlock()
    }
}
