// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.holidayevents.easter

import com.rs.game.World
import com.rs.game.map.Chunk
import com.rs.game.map.ChunkManager
import com.rs.game.tasks.WorldTasks
import com.rs.lib.game.Item
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import it.unimi.dsi.fastutil.ints.IntSet

const val ENABLED = false

private var eggsPerChunk = 2
private var regionsToSpawn = IntSet.of(12850, 11828, 12084, 12853, 12597, 12342, 10806, 10547, 13105)

@ServerStartupEvent(ServerStartupEvent.Priority.POST_PROCESS)
fun startTasks() {
    if (!ENABLED) return
    ChunkManager.permanentlyPreloadRegions(regionsToSpawn)
    WorldTasks.scheduleHalfHourly {
        spawnEggs()
        World.sendWorldMessage("<col=FF0000><shad=000000>Easter Eggs have spawned in various cities around the world!", false)
    }
}

private fun countEggs(chunkId: Int): Int {
    var eggsCount = 0
    val itemSpawns = ChunkManager.getChunk(chunkId).allGroundItems
    if (itemSpawns != null && itemSpawns.size > 0) itemSpawns.forEach { spawn ->
        if (spawn.id == 1961) eggsCount += 1
    }
    return eggsCount
}

private fun spawnEggs() {
    World.mapRegionIdsToChunks(regionsToSpawn, 0).forEach { chunkId ->
        val chunk = ChunkManager.getChunk(chunkId)
        val eggsNeeded = eggsPerChunk - countEggs(chunkId)

        if (eggsNeeded > 0) {
            repeat(eggsNeeded) {
                World.getRandomWalkableTileInChunk(chunk)?.let {
                    World.addGroundItemNoExpire(Item(1961), it)
                }
            }
        }
    }
}