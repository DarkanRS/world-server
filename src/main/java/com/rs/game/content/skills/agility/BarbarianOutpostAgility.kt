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
package com.rs.game.content.skills.agility

import com.rs.cache.loaders.ObjectType
import com.rs.engine.pathfinder.Direction
import com.rs.game.World
import com.rs.game.model.entity.async.schedule
import com.rs.engine.pathfinder.RouteEvent
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.managers.InterfaceManager
import com.rs.game.tasks.Task
import com.rs.game.tasks.WorldTasks
import com.rs.lib.Constants
import com.rs.lib.game.Animation
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick

@ServerStartupEvent
fun mapBarbarianOutpostAgility() {
    fun removeStage(player: Player) {
        player.tempAttribs.removeI("BarbarianOutpostCourse")
    }

    fun setStage(player: Player, stage: Int) {
        player.tempAttribs.setI("BarbarianOutpostCourse", stage)
    }

    fun getStage(player: Player): Int {
        return player.tempAttribs.getI("BarbarianOutpostCourse")
    }

    onObjectClick(32015) { e ->
        if (e.getObject().tile.matches(Tile.of(2547, 9951, 0))) e.player.useLadder(Tile.of(2546, 3551, 0))
    }

    onObjectClick(20210) { e ->
        if (!Agility.hasLevel(e.player, 35)) return@onObjectClick
        e.player.forceMove(Tile.of(e.getObject().x, if (e.player.y >= 3561) 3558 else 3561, e.getObject().plane), 10580, 10, 60) {
            e.player.skills.addXp(Constants.AGILITY, 1.0 / 20.0)
        }
    }

    onObjectClick(43526, checkDistance = false) { e ->
        val targetTile = if (e.getObject().x == 2552) {
            Tile.of(2552, 3554, 0)
        } else {
            Tile.of(2551, 3554, 0)
        }
        e.player.setRouteEvent(RouteEvent(targetTile) {
            if (!Agility.hasLevel(e.player, 35)) return@RouteEvent
            e.player.lock()
            e.player.resetWalkSteps()
            World.sendObjectAnimation(e.getObject(), Animation(497))
            e.player.forceMove(Tile.of(e.getObject().x, 3549, 0), 751, 20, 75) {
                e.player.sendMessage("You skillfully swing across.", true)
                e.player.skills.addXp(Constants.AGILITY, 28.0)
                setStage(e.player, 0)
            }
        })
    }

    onObjectClick(43595, checkDistance = false) { e ->

        e.player.setRouteEvent(RouteEvent(Tile.of(2551, 3546, 0)) {
            if (!Agility.hasLevel(e.player, 35)) return@RouteEvent
            e.player.sendMessage("You walk carefully across the slippery log...", true)
            e.player.forceMove(Tile.of(2541, e.getObject().y, e.getObject().plane), 9908, 20, 12 * 30) {
                e.player.anim(-1)
                e.player.skills.addXp(Constants.AGILITY, 20.7)
                e.player.sendMessage("... and make it safely to the other side.", true)
                if (getStage(e.player) == 0) setStage(e.player, 1)
            }
        })
    }

    onObjectClick(20211) { e ->
        if (!Agility.hasLevel(e.player, 35)) return@onObjectClick
        e.player.sendMessage("You climb the netting...", true)
        e.player.skills.addXp(Constants.AGILITY, 10.2)
        e.player.useStairs(828, Tile.of((e.getObject().x - 1), e.player.y, 1), 1, 2)
        if (getStage(e.player) == 1) setStage(e.player, 2)
    }

    onObjectClick(2302) { e ->
        if (!Agility.hasLevel(e.player, 35)) return@onObjectClick
        val toTile = Tile.of(2532, e.getObject().y, e.getObject().plane)
        e.player.sendMessage("You put your foot on the ledge and try to edge across...", true)
        e.player.lock()
        e.player.schedule {
            e.player.faceObject(e.getObject())
            wait(1)

            e.player.anim(753)
            e.player.appearance.setBAS(157)
            wait(1)

            e.player.addWalkSteps(toTile.x, toTile.y, -1, false)
            wait(3)

            e.player.anim(759)
            e.player.appearance.setBAS(-1)
            e.player.unlock()
            e.player.addWalkSteps(2532, 3546)
            e.player.skills.addXp(Constants.AGILITY, 26.0)
            e.player.sendMessage("You skillfully edge across the gap.", true)
            if (getStage(e.player) == 2) setStage(e.player, 3)
        }
    }

    onObjectClick(1948) { e ->
        if (!Agility.hasLevel(e.player, 35)) return@onObjectClick
        if (e.player.x >= e.getObject().x) {
            e.player.sendMessage("You cannot climb that from this side.")
            return@onObjectClick
        }
        e.player.lock()
        e.player.sendMessage("You climb the low wall...", true)
        e.player.forceMove(Tile.of((e.getObject().x + 1), e.getObject().y,
            e.getObject().plane
        ), 4853, 30, 60) {
            e.player.skills.addXp(Constants.AGILITY, 16.2)
            val stage = getStage(e.player)
            if (stage == 3) setStage(e.player, 4)
            else if (stage == 4) {
                e.player.incrementCount("Barbarian normal laps")
                removeStage(e.player)
                e.player.skills.addXp(Constants.AGILITY, 56.7)
            }
        }
    }

    onObjectClick(43533) { e ->
        if (!Agility.hasLevel(e.player, 90)) return@onObjectClick
        e.player.lock()
        e.player.schedule {
            e.player.faceDir(Direction.NORTH)
            wait(1)
            e.player.anim(10492)
            wait(6)
            e.player.tele(e.player.transform(0, 0, 2))
            e.player.forceMove(Tile.of(2538, 3545, 2), 10493, 10, 30) {
                e.player.skills.addXp(Constants.AGILITY, 15.0)
            }
        }
    }

    onObjectClick(43597, checkDistance = false) { e ->
        if (!Agility.hasLevel(e.player, 90)) return@onObjectClick
        e.player.setRouteEvent(RouteEvent(e.getObject().tile) {
            e.player.lock()
            e.player.schedule {
                e.player.faceDir(Direction.WEST)
                wait(1)
                e.player.anim(10023)
                wait(2)
                e.player.tele(Tile.of(2536, 3546, 3))
                e.player.anim(11794)
                wait(1)
                e.player.unlock()
                e.player.skills.addXp(Constants.AGILITY, 15.0)
            }
        })
    }

    onObjectClick(43587, checkDistance = false) { e ->
        if (!Agility.hasLevel(e.player, 90)) return@onObjectClick
        e.player.setRouteEvent(RouteEvent(Tile.of(2533, 3547, 3)) {
            e.player.lock()
            e.player.schedule {
                e.player.faceTile(Tile.of(2531, 3554, 3))
                wait(1)
                World.sendObjectAnimation(e.getObject(), Animation(11819))
                e.player.forceMove(Tile.of(2532, 3553, 3), 4189, 15, 90) {
                    e.player.skills.addXp(Constants.AGILITY, 15.0)
                    World.sendObjectAnimation(World.getObject(Tile.of(2531, 3554, 3), ObjectType.SCENERY_INTERACT), Animation(7527))
                }
            }
        })
    }

    onObjectClick(43527) { e ->
        if (!Agility.hasLevel(e.player, 90)) return@onObjectClick
        e.player.appearance.setBAS(330)
        e.player.forceMove(Tile.of(2536, 3553, 3), 16079, 10, 90) {
            e.player.stopAll()
            e.player.interfaceManager.removeSubs(
                InterfaceManager.Sub.TAB_INVENTORY,
                InterfaceManager.Sub.TAB_MAGIC,
                InterfaceManager.Sub.TAB_EMOTES,
                InterfaceManager.Sub.TAB_EQUIPMENT,
                InterfaceManager.Sub.TAB_PRAYER
            )
            e.player.skills.addXp(Constants.AGILITY, 15.0)
            e.player.anim(-1)
        }
    }

    onObjectClick(43531) { e ->
        if (!Agility.hasLevel(e.player, 90)) return@onObjectClick
        e.player.lock()
        e.player.anim(2586)
        e.player.appearance.setBAS(-1)
        WorldTasks.schedule(object : Task() {
            override fun run() {
                e.player.unlockNextTick()
                e.player.interfaceManager.sendSubDefaults(
                    InterfaceManager.Sub.TAB_INVENTORY,
                    InterfaceManager.Sub.TAB_MAGIC,
                    InterfaceManager.Sub.TAB_EMOTES,
                    InterfaceManager.Sub.TAB_EQUIPMENT,
                    InterfaceManager.Sub.TAB_PRAYER
                )
                e.player.tele(Tile.of(2538, 3553, 2))
                e.player.anim(2588)
                e.player.skills.addXp(Constants.AGILITY, 15.0)
                stop()
            }
        }, 0)
    }

    onObjectClick(43532) { e ->
        if (!Agility.hasLevel(e.player, 90)) return@onObjectClick
        e.player.lock()
        e.player.forceMove(Tile.of(2540, e.player.y, 2), 11792, 10, 30)
        e.player.schedule {
            e.player.forceMove(Tile.of(2542, e.player.y, 1), 11790, 0, 90)
            wait(3)
            e.player.forceMove(Tile.of(2543, e.player.y, 1), 11791, 0, 30)
            wait(1)
            e.player.anim(2588)
            e.player.tele(Tile.of(2543, e.player.y, 0))
            e.player.skills.addXp(Constants.AGILITY, 15.0)
            if (getStage(e.player) == 1) {
                e.player.incrementCount("Barbarian advanced laps")
                removeStage(e.player)
                e.player.skills.addXp(Constants.AGILITY, 615.0)
            }
            e.player.unlockNextTick()
        }
    }
}
