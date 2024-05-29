package com.rs.game.content.quests.sheepherder.utils

import com.rs.engine.pathfinder.Direction
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.utils.Ticks
import kotlin.math.abs

class SickSheepNPC(id: Int, tile: Tile) : NPC(id, tile) {
    var enteredEnclosure = false
    var timeInEnclosureTicks = 0
    private val respawnTile = tile

    private val enclosureLocations = mapOf(
        RED_SHEEP to Tile.of(2597, 3362, 0),
        GREEN_SHEEP to Tile.of(2598, 3361, 0),
        BLUE_SHEEP to Tile.of(2597, 3360, 0),
        YELLOW_SHEEP to Tile.of(2596, 3359, 0)
    )

    override fun processNPC() {
        val proddedRecently = tempAttribs.getB("proddedRecently")
        val player = tempAttribs.getO<Player>("player")

        super.processNPC()

        if (proddedRecently && !enteredEnclosure && tickCounter - tempAttribs.getL("lastProddedTick") >= 15) {
            handleProddedRecently()
        }

        if (isNearGate()) {
            moveIntoEnclosure(player)
        }

        if (enteredEnclosure) {
            handleInEnclosure(player)
        }

        if (distanceFromGate() > 90) {
            player?.sendMessage("The sheep has strayed too far and has returned to its herd.")
            resetSheep(teleport = true)
        }
    }

    private fun handleProddedRecently() {
        val destination = findRandomTileNearRespawn()
        forceTalk("Baa!")
        soundEffect(ANGRY_SHEEP_SOUND, true)
        setForceWalk(destination)
        resetSheep()
    }

    private fun isNearGate(): Boolean {
        return tile.distance(Tile(2594, 3362, 0)) <= 1 || tile.distance(Tile(2594, 3361, 0)) <= 1
    }

    private fun moveIntoEnclosure(player: Player?) {
        if (!enteredEnclosure) {
            soundEffect(ANGRY_SHEEP_SOUND, true)
            forceTalk("Baa")
            move(Tile.of(2595, 3362, 0))
            isIgnoreNPCClipping = true
            setForceWalk(enclosureLocations[id]!!)
            setRandomWalk(false)
            player?.sendMessage("The sheep obligingly jumps over the gate and into the enclosure!")
            enteredEnclosure = true
        }
    }

    private fun handleInEnclosure(player: Player?) {
        faceDir(Direction.SOUTH)
        if (player != null && player.hasFinished()) {
            resetSheep(teleport = true)
        }
        timeInEnclosureTicks++
        if (timeInEnclosureTicks >= Ticks.fromMinutes(5)) {
            player?.sendMessage("You left the sheep in the enclosure too long. It's jumped back out!")
            resetSheep(teleport = true)
        }
    }

    private fun resetSheep(teleport: Boolean = false) {
        if (teleport) tele(respawnTile)
        tempAttribs.removeB("proddedRecently")
        tempAttribs.removeL("lastProddedTick")
        tempAttribs.removeO<Player>("player")
        enteredEnclosure = false
        timeInEnclosureTicks = 0
        stopFaceEntity()
        forceWalkRespawnTile()
        setRandomWalk(true)
        isIgnoreNPCClipping = false
    }

    private fun findRandomTileNearRespawn(): Tile {
        val randomX = respawnTile.x - 3 + (0..6).random()
        val randomY = respawnTile.y - 3 + (0..6).random()
        return Tile.of(randomX, randomY, respawnTile.plane)
    }

    private fun distanceFromGate(): Int {
        return tile.distance(Tile(2594, 3362, 0))
    }

    private fun Tile.distance(other: Tile): Int {
        return abs(x - other.x) + abs(y - other.y)
    }
}
