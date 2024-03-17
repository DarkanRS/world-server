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
package com.rs.game.content.world.areas.trollheim

import com.rs.game.World
import com.rs.game.content.bosses.godwars.GodwarsController
import com.rs.game.content.skills.agility.Agility
import com.rs.game.content.world.doors.Doors
import com.rs.game.map.ChunkManager
import com.rs.game.model.entity.pathing.RouteEvent
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.game.model.entity.async.schedule
import com.rs.lib.game.Tile
import com.rs.lib.net.ClientPacket
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onLogin
import com.rs.plugin.kts.onObjectClick

@ServerStartupEvent
fun mapTrollheim() {
    //Unlock Sabbot's cave entrance (1 = mineable, 2 = being mined, 3 = mined)
    onLogin { it.player.vars.setVarBit(10762, 3) }

    onObjectClick(5012) { e -> e.player.tele(Tile.of(2799, 10134, 0)) }

    onObjectClick(5013) { e -> e.player.tele(Tile.of(2796, 3719, 0)) }

    onObjectClick(3758) { e ->
        if (e.objectAt(2906, 10036)) e.player.tele(Tile.of(2922, 3658, 0))
        else if (e.objectAt(2906, 10017)) e.player.tele(Tile.of(2911, 3636, 0))
    }

    onObjectClick(26342) { e ->
        if (e.player.controllerManager.controller == null) {
            e.player.useStairs(828, Tile.of(2881, 5310, 2), 0, 0)
            e.player.controllerManager.startController(GodwarsController())
        } else e.player.sendMessage("Invalid teleport.")
    }

    onObjectClick(35390) { e ->
        val lift = e.opNum == ClientPacket.OBJECT_OP1
        if (e.player.skills.getLevel(if (lift) Skills.STRENGTH else Skills.AGILITY) < 60) {
            e.player.sendMessage("You need a " + (if (lift) "Strength" else "Agility") + " of 60 in order to " + (if (lift) "lift" else "squeeze past") + " this boulder.")
            return@onObjectClick
        }
        val isReturning = e.player.y >= 3709
        val liftAnimation = if (isReturning) 3624 else 3725
        val squeezeAnimation = if (isReturning) 3465 else 3466
        val destination = Tile.of(e.player.x, e.player.y + (if (isReturning) -4 else 4), 0)
        e.player.lock()

        e.player.schedule {
            e.player.faceTile(destination)
            wait(1)
            e.player.anim(if (lift) liftAnimation else squeezeAnimation)
            wait(2)
            if (lift && isReturning) World.sendObjectAnimation(e.getObject(), 318)
            wait(1)
            if (lift && !isReturning) World.sendObjectAnimation(e.getObject(), 318)
            wait(2)
            if (!lift) {
                e.player.anim(-1)
                e.player.tele(destination)
                e.player.unlockNextTick()
                return@schedule
            }
            wait(2)
            if (isReturning) {
                e.player.anim(-1)
                e.player.tele(destination)
                e.player.unlockNextTick()
                return@schedule
            }
            wait(3)
            e.player.anim(-1)
            e.player.tele(destination)
            e.player.unlockNextTick()
        }
    }

    onObjectClick(34395) { e ->
        if (e.getObject().tile.isAt(2920, 3654)) e.player.tele(Tile.of(2907, 10035, 0))
        else if (e.getObject().tile.isAt(2910, 3637)) e.player.tele(Tile.of(2907, 10019, 0))
        else if (e.getObject().tile.isAt(2857, 3578)) e.player.tele(Tile.of(2269, 4752, 0))
        else if (e.getObject().tile.isAt(2885, 3673)) e.player.tele(Tile.of(2893, 10074, 2))
        else if (e.getObject().tile.isAt(2847, 3688)) e.player.tele(Tile.of(2837, 10090, 2))
        else if (e.getObject().tile.isAt(2885, 3673)) e.player.tele(Tile.of(2893, 10074, 2))
        else if (e.getObject().tile.isAt(2796, 3614)) e.player.tele(Tile.of(2808, 10002, 0))
        else e.player.sendMessage("Unhandled TrollheimMisc.handleTrollheimCaveEntrances()")
    }

    onObjectClick(32738, 18834, 18833, 4500, 3774) { e ->
        when (e.getObject().id) {
            32738 -> e.player.tele(Tile.of(2889, 3675, 0))
            18834 -> e.player.ladder(Tile.of(2812, 3669, 0))
            18833 -> e.player.ladder(Tile.of(2831, 10076, 2))
            4500 -> e.player.tele(Tile.of(2795, 3615, 0))
            3774 -> e.player.tele(Tile.of(2848, 3687, 0))
        }
    }

    onObjectClick(67752, 67679, checkDistance = false) { e ->
        if (e.objectId == 67752) {
            e.player.setRouteEvent(RouteEvent(if (e.player.x > e.getObject().x) Tile.of(3434, 4261, 1) else Tile.of(3430, 4261, 1)) {
                e.player.lock()
                e.player.resetWalkSteps()
                World.sendObjectAnimation(e.getObject(), 497)
                e.player.forceMove(if (e.player.x < e.getObject().x) Tile.of(3434, 4261, 1) else Tile.of(3430, 4261, 1), 751, 20, 75)
            })
        } else {
            val goWest = e.player.x > 3419
            e.player.setRouteEvent(RouteEvent(if (goWest) Tile.of(3423, 4260, 1) else Tile.of(3415, 4260, 1)) {
                e.player.lock()
                e.player.faceObject(e.getObject())
                e.player.schedule {
                    for (i in 0..3) {
                        e.player.anim(13495)
                        wait(3)
                        e.player.anim(-1)
                        e.player.tele(e.player.transform(if (goWest) -2 else 2, 0))
                        wait(3)
                    }
                    e.player.unlock()
                }
            })
        }
    }


    onObjectClick(67568, 67569, 67567, 67562, 67572, 67674, 67676, 67678, 67679, 67752, 67570) { e ->
        when (e.objectId) {
            67568 -> e.player.tele(Tile.of(2858, 3577, 0))
            67569 -> e.player.tele(Tile.of(2854, 3617, 0))
            67572 -> e.player.tele(Tile.of(3435, 4240, 2))
            67567 -> e.player.tele(Tile.of(2267, 4758, 0))
            67562 -> e.player.tele(Tile.of(3405, 4284, 2))
            67676 -> { //squeeze gaps
                val deltaX = if (e.getObject().tile.isAt(3421, 4280)) if (e.player.x > e.getObject().x) -2 else 2 else 0
                val deltaY = if (e.getObject().tile.isAt(3421, 4280)) 0 else if (e.player.y > e.getObject().y) -2 else 2
                e.player.lock()
                e.player.schedule {
                    wait(1)
                    e.player.anim(16025)
                    wait(7)
                    e.player.anim(-1)
                    e.player.tele(e.player.transform(deltaX, deltaY))
                    e.player.unlockNextTick()
                }
            }

            67678 -> {
                e.player.lock()
                e.player.addWalkSteps(e.getObject().tile, 2, false)
                e.player.schedule {
                    wait(1)
                    e.player.faceTile(e.player.transform(0, if (e.getObject().tile.isAt(3434, 4275)) 2 else -2))
                    wait(1)
                    e.player.anim(13495)
                    wait(3)
                    e.player.anim(-1)
                    e.player.tele(e.player.transform(0, if (e.getObject().tile.isAt(3434, 4275)) 2 else -2))
                    e.player.unlockNextTick()
                }
            }

            67674, 67570 -> {
                val horizontal = e.getObject().rotation == 0 || e.getObject().rotation == 2
                val dx = if (horizontal) (if (e.getObject().id == 67674) -4 else 4) else 0
                val dy = if (!horizontal) (if (e.getObject().id == 67674) -4 else 4) else 0
                val dz = if (e.getObject().id == 67674) -1 else 1
                climbCliff(e.player, e.player.tile, e.player.transform(dx, dy, dz), e.getObject().id == 67570)
            }
        }
    }

    onObjectClick(34836, 34839) { e ->
        if (!ChunkManager.getChunk(e.getObject().tile.chunkId).baseObjects.contains(e.getObject())) return@onObjectClick
        Doors.handleDoubleDoor(e.player, e.getObject())
    }

    onObjectClick(35391, 3748, 34877, 34889, 34878, 9306, 9305, 3803, 9304, 9303) { e ->
        if (e.getObject().id == 35391) {
            if (e.getObject().rotation == 3 || e.getObject().rotation == 1)
                Agility.handleObstacle(e.player, 3303, 1, e.player.transform(if (e.player.x < e.getObject().x) 2 else -2, 0, 0), 1.0)
            else
                Agility.handleObstacle(e.player, 3303, 1, e.player.transform(0, if (e.player.y < e.getObject().y) 2 else -2, 0), 1.0)
        } else if (e.getObject().id == 3748) {
            if (e.getObject().rotation == 3 || e.getObject().rotation == 1)
                Agility.handleObstacle(e.player, 3377, 2, e.player.transform(if (e.player.x < e.getObject().x) 2 else -2, 0, 0), 1.0)
            else
                Agility.handleObstacle(e.player, 3377, 2, e.player.transform(0, if (e.player.y < e.getObject().y) 2 else -2, 0), 1.0)
        } else if (e.getObject().id == 34877 || e.getObject().id == 34889 || e.objectId == 34878 || e.getObject().id == 3803 || e.getObject().id == 9304 || e.getObject().id == 9303) {
            if (e.getObject().rotation == 0 || e.getObject().rotation == 2)
                Agility.handleObstacle(e.player, if (e.player.x < e.getObject().x) 3381 else 3382, 3, e.player.transform(if (e.player.x < e.getObject().x) 4 else -4, 0, 0), 1.0)
            else
                Agility.handleObstacle(e.player, if (e.player.y < e.getObject().y) 3381 else 3382, 3, e.player.transform(0, if (e.player.y < e.getObject().y) 4 else -4, 0), 1.0)
        } else if (e.getObject().id == 9306 || e.getObject().id == 9305) {
            if (e.getObject().rotation == 0 || e.getObject().rotation == 2)
                Agility.handleObstacle(e.player, if (e.player.x < e.getObject().x) 3382 else 3381, 3, e.player.transform(if (e.player.x < e.getObject().x) 4 else -4, 0, 0), 1.0)
            else
                Agility.handleObstacle(e.player, if (e.player.y < e.getObject().y) 3382 else 3381, 3, e.player.transform(0, if (e.player.y < e.getObject().y) 4 else -4, 0), 1.0)
        }
    }
}

fun climbCliff(player: Player, start: Tile, end: Tile, up: Boolean) {
    player.walkToAndExecute(start) {
        player.lock()
        if (!up) player.addWalkSteps(end.x().toInt(), end.y().toInt(), 1, false)
        player.schedule {
            wait(1)
            player.faceTile(end)
            wait(1)
            player.anim(if (up) 16031 else 16016)
            wait(if (up) 5 else 2)
            player.anim(-1)
            player.tele(end)
            player.unlockNextTick()
        }
    }
}
