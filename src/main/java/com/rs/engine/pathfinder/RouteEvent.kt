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
package com.rs.engine.pathfinder

import com.rs.game.World
import com.rs.game.content.Effect
import com.rs.game.map.ChunkManager
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.lib.game.GroundItem
import com.rs.lib.game.Tile
import com.rs.lib.net.packets.encoders.MinimapFlag
import com.rs.utils.WorldUtil

class RouteEvent(private val target: Any, private val event: Runnable) {
    fun processEvent(entity: Entity): Boolean {
        var player: Player? = null
        if (entity is Player) player = entity
        if (!simpleCheck(entity)) {
            if (player != null) {
                player.sendMessage("You can't reach that.")
                player.session.writeToQueue(MinimapFlag())
            }
            return true
        }
        if (!entity.hasWalkSteps()) {
            val route = routeToTarget(entity)
            if (route.failed) {
                if (player != null) {
                    player.sendMessage("You can't reach that.")
                    player.session.writeToQueue(MinimapFlag())
                }
                return true
            }
            if (route.coords.size <= 0) {
                player?.session?.writeToQueue(MinimapFlag())
                event.run()
                return true
            } else
                addSteps(entity, route, true) //TODO reset walk steps on the start of a new route event
            return false
        }
        val route = routeToTarget(entity)
        if (route.failed) {
            if (player != null) {
                player.sendMessage("You can't reach that.")
                player.session.writeToQueue(MinimapFlag())
            }
            return true
        }
        if (route.coords.size <= 0) {
            player?.session?.writeToQueue(MinimapFlag())
            event.run()
            return true
        }
        if (entity.hasEffect(Effect.FREEZE) || (target is Entity && target.hasWalkSteps() && WorldUtil.collides(entity, target))) return false
        return false
    }

    private fun simpleCheck(entity: Entity): Boolean {
        return when(target) {
            is Entity -> entity.plane == target.plane && !target.hasFinished()
            is GameObject -> entity.plane == target.plane && World.getObject(target.tile, target.type) != null
            is GroundItem -> entity.plane == target.tile.plane && ChunkManager.getChunk(target.tile.chunkId).itemExists(target)
            is Tile -> entity.plane == target.plane
            else -> throw RuntimeException("$target is not instanceof any reachable entity.")
        }
    }

    private fun routeToTarget(entity: Entity): Route {
        return when(target) {
            is Entity -> routeEntityToEntity(entity, target)
            is GameObject -> routeEntityToObject(entity, target)
            is GroundItem -> routeEntityToTile(entity, target.tile)
            is Tile -> routeEntityToTile(entity, target)
            else -> throw IllegalStateException("Unexpected value: $target")
        };
    }
}
