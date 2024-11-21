package com.rs.game.content.quests.sheep_herder.utils

import com.rs.engine.pathfinder.Direction
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.npc.OwnedNPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.utils.Ticks
import kotlin.math.abs
import kotlin.random.Random

class SickSheepOwnedNPC(owner: Player, id: Int, tile: Tile, var officialRespawnTile: Tile, hideFromOtherPlayers: Boolean) : OwnedNPC(owner, id, tile, hideFromOtherPlayers) {

    var enteredEnclosure = false
    private var timeInEnclosureTicks = 0

    init {
        isAutoDespawnAtDistance = false
    }

    private val enclosureLocations = mapOf(
        RED_SHEEP to Tile.of(2597, 3362, 0),
        GREEN_SHEEP to Tile.of(2598, 3361, 0),
        BLUE_SHEEP to Tile.of(2597, 3360, 0),
        YELLOW_SHEEP to Tile.of(2596, 3359, 0)
    )

    override fun faceEntityTile(target: Entity?) {
        // Stop sheep facing player
    }

    override fun processNPC() {
        val proddedRecently = tempAttribs.getB("proddedRecently")

        super.processNPC()

        if (proddedRecently && !enteredEnclosure && tickCounter - tempAttribs.getL("lastProddedTick") >= Ticks.fromSeconds(Utils.random(7, 10))) {
            handleNotProddedRecently()
        }

        if (isNearGate()) {
            moveIntoEnclosure()
        }

        if (enteredEnclosure) {
            handleInEnclosure()
        }

        if (distanceFromGate() > 90) {
            owner.sendMessage("The sheep has strayed too far and has returned to its herd.")
            resetSheep(teleport = true)
        }
    }

    private fun handleNotProddedRecently() {
        resetSheep()
    }

    private fun isNearGate(): Boolean {
        return tile.distance(Tile(2594, 3362, 0)) <= 1 || tile.distance(Tile(2594, 3361, 0)) <= 1
    }

    private fun moveIntoEnclosure() {
        if (!enteredEnclosure) {
            soundEffect(ANGRY_SHEEP_SOUND, true)
            forceTalk("Baa")
            move(Tile.of(2595, 3362, 0))
            isIgnoreNPCClipping = true
            setForceWalk(enclosureLocations[id]!!)
            setRandomWalk(false)
            owner.sendMessage("The sheep obligingly jumps over the gate and into the enclosure!")
            enteredEnclosure = true
        }
    }

    private fun handleInEnclosure() {
        faceDir(Direction.SOUTH)
        if (owner != null && owner.hasFinished()) {
            resetSheep(teleport = true)
        }
        timeInEnclosureTicks++
        if (timeInEnclosureTicks >= Ticks.fromMinutes(5)) {
            owner.sendMessage("You left the sheep in the enclosure too long. It's jumped back out!")
            resetSheep(teleport = true)
        }
    }

    fun resetSheep(teleport: Boolean = false) {
        val normalNPC = convertToNormalNPC()
        var destination = findRandomTileNearRespawn()

        while (SheepHerderUtils().isInNoGoZone(destination)) {
            destination = findRandomTileNearRespawn()
        }

        if (teleport) normalNPC.tele(this.officialRespawnTile) else normalNPC.setForceWalk(this.officialRespawnTile)
        normalNPC.forceTalk("Baa!")
        normalNPC.soundEffect(ANGRY_SHEEP_SOUND, true)
        normalNPC.setForceWalk(destination)
    }

    private fun findRandomTileNearRespawn(): Tile {
        val randomX = officialRespawnTile.x - 3 + Random.nextInt(6)
        val randomY = officialRespawnTile.y - 3 + Random.nextInt(6)
        return Tile.of(randomX, randomY, officialRespawnTile.plane)
    }

    private fun distanceFromGate(): Int {
        return tile.distance(Tile(2594, 3362, 0))
    }

    private fun Tile.distance(other: Tile): Int {
        return abs(x - other.x) + abs(y - other.y)
    }

    private fun convertToNormalNPC(): SickSheepNPC {
        return SickSheepNPC(this.id, this.tile, this.officialRespawnTile, this.direction).apply {
            this.faceDir(this@SickSheepOwnedNPC.direction)
        }.also {
            this.finish()
        }
    }

}
