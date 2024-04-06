package com.rs.game.content.minigames.pyramidplunder

import com.rs.cache.loaders.ObjectType
import com.rs.engine.dialogue.sendOptionsDialogue
import com.rs.engine.pathfinder.Direction
import com.rs.engine.pathfinder.Direction.Companion.rotateClockwise
import com.rs.engine.pathfinder.WalkStep
import com.rs.game.World.broadcastLoot
import com.rs.game.World.getObject
import com.rs.game.map.ChunkManager
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.OwnedNPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.game.model.`object`.GameObject
import com.rs.lib.Constants
import com.rs.lib.game.Animation
import com.rs.lib.game.Item
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemEquip
import com.rs.plugin.kts.onObjectClick
import com.rs.plugin.kts.onPlayerStep
import com.rs.utils.DropSets
import com.rs.utils.drop.DropTable

val PYRAMID_PLUNDER_EXIT_TILE: Tile = Tile.of(3288, 2801, 0)
val PYRAMID_PLUNDER_DOORS: Array<Int> = arrayOf(16539, 16540, 16541, 16542)
private const val PHARAOHS_SCEPTRE = 9044
private const val SCEPTRE_OF_THE_GODS = 21536
private val BLACK_IBIS = intArrayOf(21532, 21533, 21534, 21535)

private const val STATE_CLOSED = 0
private const val STATE_EMPTY = 1
private const val STATE_CHECKED_FOR_SNAKES = 2
private const val STATE_SNAKE_CHARMED = 3

@ServerStartupEvent
fun mapPyramidPlunder() {
    onObjectClick(16458) { (player) ->
        val ctrl = player.kickOrGetController() ?: return@onObjectClick
        player.sendOptionsDialogue {
            opExec("Yes") { ctrl.exitMinigame() }
            op("No")
        }
    }

    onObjectClick(16518, 16519, 16520, 16521, 16522, 16523, 16524, 16525, 16526, 16527, 16528, 16529, 16530, 16531, 16532) { (player, urnObject, option) ->
        val ctrl = player.kickOrGetController() ?: return@onObjectClick
        player.lock()
        val urnState = player.vars.getVarBit(urnObject.definitions.varpBit)
        if (urnState == STATE_EMPTY) {
            player.unlock()
            player.sendMessage("The urn is empty.")
            return@onObjectClick
        }
        when (option) {
            "Check for Snakes" -> {
                player.unlock()
                player.skills.addXp(Constants.THIEVING, getRoomBaseXP(ctrl.currentRoom))
                ctrl.updateObject(urnObject, STATE_CHECKED_FOR_SNAKES)
                return@onObjectClick
            }

            "Charm Snake" -> {
                player.unlock()
                if (player.inventory.containsItem(4605, 1)) {
                    player.anim(1877)
                    ctrl.updateObject(urnObject, STATE_SNAKE_CHARMED)
                } else player.sendMessage("You need a snake charm flute for that!")
            }

            "Search" -> player.schedule {
                player.faceObject(urnObject)
                wait(1)
                player.anim(4340)
                wait(2)
                if (rollUrnSuccess(player, ctrl.currentRoom, urnState)) {
                    player.anim(4342)
                    player.skills.addXp(Constants.THIEVING, (getRoomBaseXP(ctrl.currentRoom) * (if (urnState == 0) 3.0 else 2.0)))
                    ctrl.updateObject(urnObject, STATE_EMPTY)
                    player.loot("pp_urn", ctrl.currentRoom)
                } else {
                    player.anim(4341)
                    player.applyHit(Hit(player.skills.getLevel(Constants.HITPOINTS) / 5, Hit.HitLook.TRUE_DAMAGE))
                    player.poison.makePoisoned(30)
                    player.forceTalk("Ow!")
                }
                wait(2)
                player.unlock()
                player.processReceivedHits()
            }
        }
    }

    onObjectClick(16537) { (player, obj) ->
        val ctrl = player.kickOrGetController() ?: return@onObjectClick
        if (Utils.randomInclusive(0, 4) == 1)
            OwnedNPC(player, 2001, Tile.of(player.tile), false).combatTarget = player
        ctrl.updateObject(obj, 1)
        player.skills.addXp(Constants.THIEVING, getRoomBaseXP(ctrl.currentRoom) * 2.0)
        player.loot("pp_sarcophagus", ctrl.currentRoom)
    }

    onObjectClick(16547) { (player, obj, option) ->
        if (option != "Open") return@onObjectClick
        val ctrl = player.kickOrGetController() ?: return@onObjectClick
        val lvlReq = (ctrl.currentRoom + 1) * 10 + 1
        if (player.skills.getLevel(Constants.STRENGTH) < lvlReq) {
            player.sendMessage("You need $lvlReq strength...")
            return@onObjectClick
        }
        player.lock()
        val success = rollSarcophagusSuccess(player, ctrl.currentRoom)
        player.schedule {
            player.faceObject(obj)
            wait(1)
            player.anim(if (success) 4345 else 4344)
            wait(2)
            ctrl.updateObject(obj, if (success) 1 else 0)
            wait(3)
            if (success) {
                if (Utils.randomInclusive(0, 4) == 1)
                    OwnedNPC(player, 2015, Tile.of(player.tile), false).combatTarget = player
                player.skills.addXp(Constants.STRENGTH, getRoomBaseXP(ctrl.currentRoom))
                ctrl.updateObject(obj, 2)
                player.loot("pp_sarcophagus", ctrl.currentRoom)
            } else {
                player.applyHit(Hit(player.skills.getLevel(Constants.HITPOINTS) / 5, Hit.HitLook.TRUE_DAMAGE))
                player.forceTalk("Ow!")
            }
            wait(2)
            player.unlock()
        }
    }

    onObjectClick(59795) { (player, obj, option) ->
        val ctrl = player.kickOrGetController() ?: return@onObjectClick
        when(option) {
            "Search" -> player.sendMessage("The sarcophagus has already been looted.")
            "Open" -> {
                if (Utils.randomInclusive(0, 4) == 1)
                    OwnedNPC(player, 2015, Tile.of(player.tile), false).combatTarget = player
                ctrl.updateObject(obj, 1)
                player.skills.addXp(Skills.RUNECRAFTING, getRoomBaseXP(ctrl.currentRoom))
                player.loot("pp_sarcophagus_engraved", ctrl.currentRoom)
            }
        }
    }

    onObjectClick(*PYRAMID_PLUNDER_DOORS) { (player, obj, option) ->
        val ctrl = player.kickOrGetController() ?: return@onObjectClick
        when(option) {
            "Enter" -> if (obj.id == ctrl.correctDoor)
                ctrl.nextRoom()
            else
                player.sendMessage("You've already checked this door and found it's not the right way.")

            "Pick-lock" -> {
                player.lock()
                player.schedule {
                    player.faceObject(obj)
                    player.anim(832)
                    wait(1)
                    if (Utils.skillSuccess(player.skills.getLevel(Skills.THIEVING), if (player.inventory.containsOneItem(1523, 11682)) 1.3 else 1.0, 190, 190)) {
                        player.skills.addXp(Constants.THIEVING, getRoomBaseXP(ctrl.currentRoom) * 2.0)
                        ctrl.updateObject(obj, 1)
                    } else {
                        player.sendMessage("You fail to pick the lock.")
                        player.unlock()
                        return@schedule
                    }
                    wait(2)
                    if (obj.id == ctrl.correctDoor)
                        ctrl.nextRoom()
                    else
                        player.sendMessage("The door leads nowhere.")
                    wait(1)
                    player.unlock()
                }
            }
        }
    }

    onObjectClick(16517) { (player, obj) ->
        val ctrl = player.kickOrGetController() ?: return@onObjectClick
        val lvlReq = (ctrl.currentRoom + 1) * 10 + 1
        if (player.skills.getLevel(Constants.THIEVING) < lvlReq) {
            player.sendMessage("You need a thieving level of $lvlReq or higher...")
            return@onObjectClick
        }
        player.passTrap(obj)
    }

    //TODO sceptre of the gods triggers varbit 3419, 3420, and 3421 to spawn extra
    //urns in rooms 7 and 8

    val rightHandSpearTraps: Array<Tile> = arrayOf(
        Tile.of(1927, 4473, 0), Tile.of(1928, 4473, 0),
        Tile.of(1930, 4452, 0), Tile.of(1930, 4453, 0),
        Tile.of(1955, 4474, 0), Tile.of(1954, 4474, 0),
        Tile.of(1961, 4444, 0), Tile.of(1961, 4445, 0),
        Tile.of(1927, 4428, 0), Tile.of(1926, 4428, 0),
        Tile.of(1944, 4425, 0), Tile.of(1945, 4425, 0),
        Tile.of(1974, 4424, 0), Tile.of(1975, 4424, 0)
    )
    val leftHandSpearTraps: Array<Tile> = arrayOf(
        Tile.of(1927, 4472, 0), Tile.of(1928, 4472, 0),
        Tile.of(1931, 4452, 0), Tile.of(1931, 4453, 0),
        Tile.of(1955, 4473, 0), Tile.of(1954, 4473, 0),
        Tile.of(1962, 4444, 0), Tile.of(1962, 4445, 0),
        Tile.of(1927, 4427, 0), Tile.of(1926, 4427, 0),
        Tile.of(1944, 4424, 0), Tile.of(1945, 4424, 0),
        Tile.of(1974, 4423, 0), Tile.of(1975, 4423, 0)
    )

    onPlayerStep(*rightHandSpearTraps) { (player, step, tile) ->
        player.triggerWallTrap(step, tile, 2) } //90 degree turn

    onPlayerStep(*leftHandSpearTraps) { (player, step, tile) ->
        player.triggerWallTrap(step, tile, 6) } //270 degree turn
}

private fun Player.kickOrGetController(): PyramidPlunderController? {
    val ctrl = controllerManager.getController(PyramidPlunderController::class.java)
    if (ctrl == null) {
        tele(PYRAMID_PLUNDER_EXIT_TILE)
        sendMessage("No idea how you got in here. But get out bad boy.")
        return null
    }
    return ctrl
}

private fun Player.triggerWallTrap(step: WalkStep, tile: Tile, rotation: Int) {
    if (isLocked) return
    activateTrap(tile, rotateClockwise(step.dir, rotation)) //270 degree turn
    hitPlayer(this, step, tile)
}

private fun Player.loot(lootTable: String, room: Int): Boolean {
    var thingLooted = "urns"
    if (lootTable.contains("chest")) thingLooted = "grand chests"
    else if (lootTable.contains("sarcophagus")) thingLooted = "sarcophagi"
    else if (lootTable.contains("engraved")) thingLooted = "engraved sarcophagi"
    incrementCount("Pyramid Plunder $thingLooted looted", 1)
    if (rollForBlackIbis()) return false
    if (lootTable.contains("sarcophagus")) {
        val chance = when (room) {
            1 -> 3500
            2 -> 2250
            3 -> 1250
            4 -> 750
            else -> 650
        }
        if (Utils.random(chance * (if (equipment.wearingRingOfWealth()) 0.97 else 1.0)).toInt() == 0) {
            inventory.addItemDrop(PHARAOHS_SCEPTRE, 1)
            broadcastLoot("$displayName has just received a Pharaoh's sceptre from Pyramid Plunder!")
            return true
        }
    }
    if (!containsItem(SCEPTRE_OF_THE_GODS) && lootTable.contains("engraved")) {
        if (Utils.random(650 * (if (equipment.wearingRingOfWealth()) 0.97 else 1.0)).toInt() == 0) {
            inventory.addItemDrop(Item(SCEPTRE_OF_THE_GODS).addMetaData("teleCharges", 10))
            broadcastLoot("$displayName has just received a Sceptre of the Gods from Pyramid Plunder!")
            return true
        }
    }
    val drops = DropTable.calculateDrops(DropSets.getDropSet(lootTable + room))
    for (item in drops) {
        if (item == null) continue
        inventory.addItemDrop(item)
    }
    return false
}

private fun getRoomBaseXP(roomId: Int) = when (roomId) {
    1 -> 20.0
    2 -> 30.0
    3 -> 50.0
    4 -> 70.0
    5 -> 100.0
    6 -> 150.0
    7 -> 225.0
    8 -> 275.0
    else -> 0.0
}

private fun rollUrnSuccess(player: Player, room: Int, urnState: Int): Boolean {
    var boost = player.auraManager.thievingMul
    if (urnState == STATE_CHECKED_FOR_SNAKES) boost += 0.15
    else if (urnState == STATE_SNAKE_CHARMED) boost += 0.40
    val chances = when (room) {
        1 -> 73 to 213
        2 -> 55 to 207
        3 -> 32 to 203
        4 -> -21 to 197
        5 -> -72 to 193
        6 -> -150 to 187
        7 -> -300 to 183
        else -> -900 to 177
    }
    return Utils.skillSuccess(player.skills.getLevel(Constants.THIEVING), boost, chances.first, chances.second, if (urnState != STATE_CLOSED) 235 else 213)
}

private fun rollSarcophagusSuccess(player: Player, room: Int): Boolean {
    val chances = when (room) {
        1 -> 62 to 256
        2 -> 30 to 226
        3 -> 20 to 196
        4 -> 0 to 166
        5 -> -50 to 136
        6 -> -90 to 106
        7 -> -120 to 76
        else -> -200 to 46
    }
    return Utils.skillSuccess(player.skills.getLevel(Constants.STRENGTH), 1.0, chances.first, chances.second, 188)
}

private fun activateTrap(trapTile: Tile, trapDir: Direction) {
    for (obj in ChunkManager.getChunk(trapTile.chunkId).getBaseObjects()) if (obj.id == 16517) {
        if (trapTile.matches(obj.tile) || (obj.x - trapDir.dx == trapTile.x && obj.y - trapDir.dy == trapTile.y)) {
            obj.animate(Animation(463))
            break
        }
    }
}

private fun hitPlayer(player: Player, step: WalkStep, tile: Tile) {
    player.applyHit(Hit(30, Hit.HitLook.POISON_DAMAGE))
    player.poison.makePoisoned(20)
    val oppositeDir = rotateClockwise(step.dir, 4) //180 degree turn
    player.forceMove(Tile.of(tile.x + oppositeDir.dx, tile.y + oppositeDir.dy, tile.plane), 1237, 25, 45) { player.forceTalk("Ouch!") }
}

private fun Player.rollForBlackIbis(): Boolean {
    if (containsItem(BLACK_IBIS[3])) return false
    var rate = if (equipment.weaponId == SCEPTRE_OF_THE_GODS) 1150 else 2300
    if (equipment.wearingRingOfWealth()) rate = (rate * 0.97).toInt()
    if (Utils.random(rate) == 0) {
        var drop = -1
        for (peices in BLACK_IBIS) {
            if (!containsItem(peices)) {
                drop = peices
                break
            }
        }
        inventory.addItemDrop(drop, 1)
        broadcastLoot("$displayName has just received a peice of Black Ibis from Pyramid Plunder!")
        return true
    }
    return false
}

private fun Player.passTrap(obj: GameObject) {
    val nearbyTiles = arrayOf(obj.tile.transform(0, 1), obj.tile.transform(1, 0), obj.tile.transform(0, -1), obj.tile.transform(-1, 0))
    val farTiles = arrayOf(transform(0, 3), transform(3, 0), transform(0, -3), transform(-3, 0))
    var tileIdx = 0
    for (nearbyTile in nearbyTiles) {
        val obj2: GameObject? = getObject(nearbyTile, ObjectType.SCENERY_INTERACT)
        if (obj2 != null && obj2.id == 16517) break
        tileIdx++
    }
    lock()
    schedule {
        faceObject(obj)
        wait(1)
        anim(832)
        wait(2)
        val hasRun = run
        run = false
        addWalkSteps(farTiles[tileIdx], 4, false)
        wait(2)
        run = hasRun
        skills.addXp(Skills.THIEVING, 10.0)
        wait(1)
        unlock()
    }
}