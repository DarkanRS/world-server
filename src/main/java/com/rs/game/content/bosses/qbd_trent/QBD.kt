package com.rs.game.content.bosses.qbd_trent

import com.rs.Settings
import com.rs.cache.loaders.Bonus
import com.rs.engine.pathfinder.Direction
import com.rs.game.World
import com.rs.game.content.combat.getAntifireLevel
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.npc.combat.CombatScript
import com.rs.game.model.entity.npc.combat.CombatScript.delayHit
import com.rs.game.model.entity.npc.combat.CombatScript.getMeleeHit
import com.rs.game.model.entity.npc.combat.CombatScript.getRangeHit
import com.rs.game.model.entity.npc.combat.CombatScriptsHandler
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils.clampI
import com.rs.lib.util.Utils.random
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.npcCombat
import java.util.stream.IntStream.range
import kotlin.math.abs
import kotlin.streams.toList

class QBD(npcId: Int, tile: Tile) : NPC(npcId, tile, Direction.SOUTH, true) {
    var phase = 3

    init {
        setRandomWalk(false)
        isForceMultiArea = true
        isCantFollowUnderCombat = true
        isIgnoreDocile = true
        capDamage = 1000
    }

    override fun processNPC() {
        super.processNPC()
        faceDir(Direction.SOUTH)
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
private const val RANGE = 16720
private const val FIRE_BREATH = 16721
private const val SIPHON_SOULS = 16739
private const val SPAWN_GROTWORMS = 16722
private const val SPAWN_GROTWORMS_STOP = 16723
private const val EXTREMELY_HOT_FLAMES = 16745
private const val FIRE_WALLS = 16746
private const val FIRE_WALL_CONTINUE = 16747
private const val FIRE_WALL_STOP = 16748
private const val WAKE_UP = 16848

fun qbdAttack(qbd: QBD, player: Player): Int {
    val attack = random(4)
    return when(attack) {
        1 -> qbd.firebreath(player)
        2 -> qbd.firewall()
        3 -> qbd.extremelyHotFlames()
        else -> qbd.basicAttack(player)
    }
}

private fun QBD.basicAttack(target: Player): Int {
    if ((y - target.y) <= 4 && random(2) == 0) {
        val xDiff = target.x - middleTile.x
        anim(when {
            xDiff <= -3 -> MELEE_LEFT
            xDiff >= 3 -> MELEE_RIGHT
            else -> MELEE_CENTER
        })
        delayHit(this, 1, target, getMeleeHit(this, CombatScript.getMaxHit(this, 475, Bonus.SLASH_ATT, AttackStyle.MELEE, target)));
    } else {
        anim(RANGE)
        delayHit(this, 1, target, getRangeHit(this, CombatScript.getMaxHit(this, 525, AttackStyle.RANGE, target)));
    }
    return 6
}

private fun QBD.firebreath(target: Player): Int {
    sync(FIRE_BREATH, 3143)
    schedule {
        wait(1)
        target.applyHit(Hit.flat(this@firebreath, when(getAntifireLevel(target, false)) {
            1 -> 194
            2 -> 200
            else -> 950
        }))
    }
    return 6
}

private fun QBD.extremelyHotFlames(): Int {
    fun QBD.baseDamage(target: Entity): Int {
        val distanceFromCenter = abs(target.x - middleTile.x)
        return when {
            distanceFromCenter <= 2 -> {
                target.tempAttribs.setB("canBrandish", true)
                random(695, 774)
            }
            distanceFromCenter <= 6 -> random(490, 520)
            else -> random(275, 350)
        }
    }

    val targets = possibleTargets.filter { it is Player && !it.isDead }
    targets.forEach { (it as? Player)?.sendMessage("<col=FFCC00>The Queen Black Dragon gathers her strength to breath extremely hot flames.</col>") }
    sync(EXTREMELY_HOT_FLAMES, 3152)
    schedule {
        wait(5)
        repeat(3) { num ->
            possibleTargets.filter { it is Player && !it.isDead }.forEach {
                it.applyHit(Hit.flat(this@extremelyHotFlames, when (getAntifireLevel(it, false)) {
                    1 -> baseDamage(it) / 2
                    2 -> baseDamage(it) / 3
                    else -> baseDamage(it)
                }))
                if (num == 2) it.tempAttribs.removeB("canBrandish")
            }
            wait(2)
        }
    }
    return 13
}

private fun QBD.firewall(): Int {
    val numWalls = clampI(phase+1, 0, 3)
    val targets = possibleTargets.filter { it is Player && !it.isDead }
    targets.forEach { (it as? Player)?.sendMessage("<col=FF9900>The Queen Black Dragon takes a huge breath.</col>") }
    schedule {
        sync(FIRE_WALLS, 3155)
        wait(6)
        anim(FIRE_WALL_CONTINUE)
        val variants = range(0, 3).toList().shuffled()
        for (i in 1..numWalls) {
            sendFirewall(variants[i-1])
            if (i != numWalls)
                wait(6)
        }
        wait(2)
        anim(FIRE_WALL_STOP)
    }
    return (numWalls) * 6 + 7
}

private fun QBD.sendFirewall(variant: Int) {
    val targets = possibleTargets.filter { it is Player && !it.isDead }
    val spotAnim = 3158+variant
    var offset = 0
    tasks.scheduleTimer(0, 0) { tick ->
        when(tick) {
            0 -> World.sendProjectile(tile.transform(2, 2), tile.transform(2, -18), spotAnim, 0 to 0, 0, 30, 0, 0)
            18 -> return@scheduleTimer false
            else -> {
                offset++
                val baseTile = tile.transform(2, 2-offset)
                for (x in -10..10) {
                    when(variant) {
                        0 -> if (x == -5) continue
                        1 -> if (x == 4) continue
                        2 -> if (x == -1) continue
                    }
                    val danger1 = baseTile.transform(x, 0)
                    val danger2 = baseTile.transform(x, -1)
                    targets.filter { !it.isDead && (it.isAt(danger1.x, danger1.y) || it.isAt(danger2.x, danger2.y)) }.forEach {
                        it.applyHit(Hit.flat(this, when(getAntifireLevel(it, false)) {
                            1 -> 224
                            2 -> 200
                            else -> 426
                        }))
                    }
//                    if (Settings.getConfig().isDebug) {
//                        World.sendSpotAnim(baseTile.transform(x, 0), 502)
//                        World.sendSpotAnim(baseTile.transform(x, -1), 502)
//                    }
                }
            }
        }
        return@scheduleTimer true
    }
}