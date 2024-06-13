package com.rs.game.content.quests.eadgars_ruse.instances.npcs

import com.rs.engine.pathfinder.Direction
import com.rs.game.content.quests.eadgars_ruse.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.lib.game.Tile

class GoutweedCrateGuard(id: Int, tile: Tile) : NPC(id, tile) {

    private var nextSpotAnimTick = 5
    private var tickCounter = 0
    var caughtPlayer = false

    init {
        faceDir(Direction.NORTH)
        super.initEntity()
        isCantFollowUnderCombat = true
        isCantInteract = true
    }

    private fun handleSpotAnim() {
        if (tickCounter == nextSpotAnimTick) {
            spotAnim(SLEEPING_SPOTANIM)
            nextSpotAnimTick = tickCounter + 5
        }
    }

    override fun processNPC() {
        tickCounter++
        if (!caughtPlayer) {
            handleSpotAnim()
            faceDir(Direction.NORTH)
        }
        super.processNPC()
    }

    override fun reset() {
        caughtPlayer = false
        tickCounter = 0
        nextSpotAnimTick = 5
        faceDir(Direction.NORTH)
        super.reset()
    }
}
