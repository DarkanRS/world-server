package com.rs.game.content.minigames.castlewars

import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.npc.NPC
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.npcCombat

@ServerStartupEvent
fun mapBarricadeCombat() {

    npcCombat(1532) { _, _ ->
        return@npcCombat 0
    }

}

class CastleWarsBarricadeNpc(private var team: Int, tile: Tile) : NPC(1532, tile, true) {

    init {
        isCantFollowUnderCombat = true
    }

    override fun processNPC() {
        if (isDead)
            return
        stopFaceEntity()
        if (id == 1533 && Utils.getRandomInclusive(20) == 0)
            sendDeath(this)
    }

    fun litFire() {
        transformIntoNPC(1533)
        sendDeath(this)
    }

    fun explode() {
        // TODO gfx
        sendDeath(this)
    }

    override fun sendDeath(source: Entity?) {
        resetWalkSteps()
        combat.removeTarget()
        if (id != 1533) {
            anim(null)
            reset()
            setLocation(respawnTile)
            finish()
        } else {
            super.sendDeath(source)
        }
        CastleWars.removeBarricade(team, this)
    }

}