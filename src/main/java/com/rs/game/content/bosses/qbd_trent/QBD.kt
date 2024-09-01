package com.rs.game.content.bosses.qbd_trent

import com.rs.engine.pathfinder.Direction
import com.rs.game.World
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.npc.combat.CombatScriptsHandler
import com.rs.game.model.entity.npc.combat.NPCCombat
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
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

    npcCombat(15454, 15506, 15507, 15508, 15509) { npc, target ->
        val qbd = npc as? QBD
        val player = target as? Player
        if (qbd == null || player == null) return@npcCombat CombatScriptsHandler.getDefaultCombat().apply(npc, target)
        return@npcCombat qbdAttack(qbd, player)
    }
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

fun qbdAttack(qbd: QBD, player: Player): Int {
    qbd.firewall(player, Utils.random(3))
    return 30
}

private fun QBD.firewall(target: Player, variant: Int) {
    val spotAnim = 3158+variant
    var offset = 0
    tasks.scheduleTimer(0, 0) { tick ->
        when(tick) {
            0 -> World.sendProjectile(tile.transform(2, 2), tile.transform(2, -18), spotAnim, 0 to 0, 0, 30, 0, 0)
            15 -> return@scheduleTimer false
            else -> {
                offset++
                val baseTile = tile.transform(2, 2-offset);
                for (x in -10..10) {
                    World.sendSpotAnim(baseTile.transform(x, 0), 502)
                    World.sendSpotAnim(baseTile.transform(x, -1), 502)
                }
            }
        }
        return@scheduleTimer true
    }
}