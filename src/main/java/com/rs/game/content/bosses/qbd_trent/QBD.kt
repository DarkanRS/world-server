package com.rs.game.content.bosses.qbd_trent

import com.rs.engine.pathfinder.Direction
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.npc.NPC
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.npcCombat

class QBD(npcId: Int, tile: Tile) : NPC(npcId, tile, Direction.SOUTH, true) {
    var phase = 0

    init {
        isForceMultiArea = true
        isCantFollowUnderCombat = true
        isIgnoreDocile = true
        capDamage = 1000
    }
}

@ServerStartupEvent
fun mapQbdFeatures() {
    instantiateNpc(15454, 15506, 15507, 15508, 15509) { npcId, tile -> QBD(npcId, tile) }

    npcCombat(
        15454, 15506, 15507, 15508, 15509,
        script = ::qbdAttack
    )
}

private const val MELEE_LEFT = 16743
private const val MELEE_CENTER = 16717
private const val MELEE_RIGHT = 16744
private const val RANGE = 16719
private const val SPAWN_SOULS = 16721
private const val SIPHON_SOULS = 16739
private const val SPAWN_GROTWORMS = 16722
private const val SPAWN_GROTWORMS_STOP = 16723
private const val EXTREMELY_HOT_FLAMES = 16745
private const val FIRE_WALLS = 16746
private const val FIRE_WALL_CONTINUE = 16747
private const val FIRE_WALL_STOP = 16748
private const val WAKE_UP = 16848

fun qbdAttack(npc: NPC, target: Entity): Int {
    npc.anim(MELEE_CENTER)
    return 5
}