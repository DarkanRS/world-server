package com.rs.game.content.skills.agility

import com.rs.engine.pathfinder.RouteEvent
import com.rs.game.World.sendObjectAnimation
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.lib.Constants
import com.rs.lib.game.Animation
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick
import kotlin.math.min
import kotlin.math.roundToInt

@ServerStartupEvent
fun mapWildernessAgility() {
    fun removeWildernessStage(player: Player) {
        player.tempAttribs.removeI("WildernessCourse")
    }
    fun setWildernessStage(player: Player, stage: Int) {
        player.tempAttribs.setI("WildernessCourse", stage)
    }
    fun getWildernessStage(player: Player): Int {
        return player.tempAttribs.getI("WildernessCourse")
    }

    // Ladder down
    onObjectClick(14758) { (player) ->
        player.useStairs(828, Tile(3005, 10362, 0))
    }

    // Ladder up
    onObjectClick(32015) { (player) ->
        player.useStairs(828, Tile(3005, 3962, 0))
    }

    // Gates to & from Wilderness Agility Course
    onObjectClick(65365, 65367) { (player, obj) ->
        if (obj.id == 65365 && !Agility.hasLevel(player, 52)) return@onObjectClick

        val targetTile = if (obj.id == 65365) Tile.of(obj.x, 3931, obj.plane) else Tile.of(obj.x + 1, 3915, obj.plane)
        val initialX = 2998
        val initialY = if (obj.id == 65365) 3916 else 3931
        val initialTile = Tile.of(initialX, initialY, obj.plane)

        val movePlayer = {
            val running = player.run
            player.setRunHidden(false)
            player.lock()
            player.sendMessage("You go through the gate and try to edge over the ridge...", true)
            player.schedule {
                player.appearance.setBAS(155)
                player.addWalkSteps(targetTile.x, targetTile.y, -1, false)
                wait(17)
                player.appearance.setBAS(-1)
                player.setRunHidden(running)
                player.sendMessage("You skillfully balance across the ridge...", true)
                player.unlock()
            }
        }

        if (obj.id == 65367) {
            if (player.tile == initialTile) {
                movePlayer()
            } else {
                player.addWalkSteps(initialX, initialY, 0, false)
                player.lock(2)
                player.schedule {
                    wait(1)
                    movePlayer()
                }
            }
        } else {
            if (player.tile == obj.tile) {
                movePlayer()
            } else {
                player.addWalkSteps(initialX, initialY, 0, false)
                player.lock(2)
                player.schedule {
                    wait(1)
                    movePlayer()
                }
            }
        }
    }

    // Obstacle Pipe
    onObjectClick(65362) { (player, obj) ->
        if (!Agility.hasLevel(player, 49)) return@onObjectClick

        player.lock()
        val running = player.run
        val initialTile = Tile.of(obj.x, if (obj.y == 3938) 3937 else 3950, 0)
        val finalTile = Tile.of(obj.x, if (obj.y == 3938) 3947 else 3940, 0)

        player.setRouteEvent(RouteEvent(initialTile) {
            player.setRunHidden(false)
            player.schedule {
                player.anim(10646)
                player.forceMove(Tile.of(obj.x, if (obj.y == 3938) 3940 else 3947, 0), 20, 90, false) {}
                wait(6)
                player.anim(-1)
                player.tele(finalTile)
                wait(0)
                player.anim(12458)
                player.forceMove(Tile.of(obj.x, if (obj.y == 3938) 3950 else 3937, 0), 10, 60, false) {}
                wait(3)
                player.appearance.setBAS(-1)
                setWildernessStage(player, 0)
                player.setRunHidden(running)
                player.skills.addXp(Constants.AGILITY, 12.5)
                player.unlock()
            }
        })
    }

    // Ropeswing
    onObjectClick(64696) { (player, obj) ->
        if (!Agility.hasLevel(player, 52)) return@onObjectClick
        if (player.y > 3953) {
            player.sendMessage("You can't see a good way to jump from here.")
            return@onObjectClick
        }

        if (obj.attribs.getB("inUse")) {
            player.sendMessage("Someone else is using the ropeswing right now.")
            return@onObjectClick
        }

        player.lock()
        obj.attribs.setB("inUse", true)
        player.setRouteEvent(RouteEvent(Tile.of(3005, 3953, 0)) {
            player.schedule {
                player.faceObject(obj)
                player.anim(751)
                sendObjectAnimation(obj, Animation(497))
                val agilityLevel = player.skills.getLevel(Skills.AGILITY)
                val failureRate = when {
                    agilityLevel > 85 -> 0.0
                    else -> 0.33 - (agilityLevel - 52) * 0.33 / (85 - 52)
                }

                val swingSuccess = Math.random() >= failureRate
                val toTile = Tile.of(obj.x, if (swingSuccess) 3958 else 3955, obj.plane)

                if (swingSuccess) {
                    player.forceMove(toTile, 30, 90)
                    wait(4)
                    player.sendMessage("You skillfully swing across the rope.", true)
                    player.skills.addXp(Constants.AGILITY, 20.0)
                    if (getWildernessStage(player) == 0) setWildernessStage(player, 1)
                } else {
                    player.forceMove(toTile, 30, 55)
                    wait(4)
                    player.sendMessage("You lose your grip and fall down.", true)
                    player.tele(Tile.of(3005, 10356, 0))
                    obj.attribs.removeB("inUse")
                    player.anim(-1)
                    obj.animate(-1)
                    player.forceTalk("Ouch!")
                    val damage = min((player.hitpoints * 26.6 / 100).roundToInt(), 200)
                    player.applyHit(Hit.flat(player, damage))
                    wait(1)
                    player.addWalkSteps(player.x, player.y + 1, -1, false)
                }
                player.unlock()
                obj.attribs.removeB("inUse")
            }
        })
    }

    // Stepping stone
    onObjectClick(64699) { (player) ->
        if (!Agility.hasLevel(player, 52)) return@onObjectClick
        if (player.tile != Tile(3002, 3960, 0)) return@onObjectClick

        player.sendMessage("You carefully start crossing the stepping stones...")
        player.schedule {
            val agilityLevel = player.skills.getLevel(Skills.AGILITY)
            for (i in 0..5) {
                val toTile = Tile.of(3002 - (i + 1), player.y, player.plane)
                val failureRate = when {
                    agilityLevel > 85 -> 0.0
                    else -> 0.33 - (agilityLevel - 52) * 0.33 / (85 - 52)
                }

                if (i == 3 && Math.random() < failureRate) {
                    player.sendMessage("...You lose your footing and fall into the lava.")
                    player.anim(771)
                    wait(1)
                    player.anim(-1)
                    player.tele(3000, 3964, 0)
                    val damage = min((player.hitpoints * 26.6 / 100).roundToInt(), 200)
                    player.applyHit(Hit.flat(player, damage))
                    player.unlock()
                    return@schedule
                }
                player.forceMove(toTile, 741, 5, 30)
                wait(if (i == 0) 3 else 2)
            }
            player.unlock()
            player.sendMessage("...You safely cross to the other side.")
            player.skills.addXp(Constants.AGILITY, 20.0)
            if (getWildernessStage(player) == 1) setWildernessStage(player, 2)
        }
    }

    // Log balance
    onObjectClick(64698) { (player) ->
        if (!Agility.hasLevel(player, 52)) return@onObjectClick
        if (player.tile != Tile(3002, 3945, 0)) {
            player.addWalkSteps(3002, 3945, -1, false)
        }

        player.schedule {
            player.lock()
            val running = player.run
            player.run = false

            player.addWalkSteps(2994, 3945, -1, false)
            player.sendMessage("You walk carefully across the slippery log...", true)

            player.appearance.setBAS(155)
            wait(5)

            val agilityLevel = player.skills.getLevel(Skills.AGILITY)
            val failureRate = when {
                agilityLevel > 85 -> 0.0
                else -> 0.33 - (agilityLevel - 52) * 0.33 / (85 - 52)
            }

            if (Math.random() < failureRate) {
                player.sendMessage("...You lose your footing and fall below.")
                player.anim(2582)
                wait(1)
                player.anim(-1)
                player.tele(2999, 10346, 0)
                player.appearance.setBAS(-1)
                player.forceTalk("Ouch!")
                val damage = min((player.hitpoints * 26.6 / 100).roundToInt(), 200)
                player.applyHit(Hit.flat(player, damage))
                wait(1)
                player.addWalkSteps(player.x, player.y - 1, -1, false)
                if (running) player.run = true
                player.unlock()
                return@schedule
            }

            wait(5)
            player.appearance.setBAS(-1)
            player.skills.addXp(Constants.AGILITY, 20.0)
            player.sendMessage("...You skillfully edge across the gap.", true)
            if (running) player.run = true
            player.unlock()

            if (getWildernessStage(player) == 2) {
                setWildernessStage(player, 3)
            }
        }
    }

    // Cliffside
    onObjectClick(65734) { (player, obj) ->
        if (!Agility.hasLevel(player, 52)) return@onObjectClick
        if (player.y != 3939) return@onObjectClick

        player.setRouteEvent(RouteEvent(Tile.of(2993, 3939, 0)) {
            player.schedule {
                player.faceObject(obj)
                wait(1)
                player.anim(3378)
                val toTile = Tile.of(player.x + 2, 3935, 0)
                wait(4) // Additional wait before teleportation (adjust as needed)
                player.tele(toTile)
                player.anim(-1)
                player.appearance.setBAS(-1)
                player.sendMessage("You reach the top.", true)
                if (getWildernessStage(player) == 3) {
                    player.incrementCount("Wilderness laps")
                    removeWildernessStage(player)
                    player.skills.addXp(Constants.AGILITY, 499.0)
                }
            }
        })
    }
}
