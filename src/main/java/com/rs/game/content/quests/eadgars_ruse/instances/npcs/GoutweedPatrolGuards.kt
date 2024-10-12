package com.rs.game.content.quests.eadgars_ruse.instances.npcs

import com.rs.game.World.sendProjectile
import com.rs.game.content.quests.eadgars_ruse.utils.*
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.npc.combat.CombatScript
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.instantiateNpc
import kotlin.random.Random

class GoutweedPatrolGuards(id: Int, tile: Tile, private val tiles: Array<Tile>) : NPC(id, tile) {

    private val storeroomTiles: Array<IntArray> = arrayOf(
        intArrayOf(2849, 2860, 10073, 10092),
        intArrayOf(2860, 2865, 10073, 10086)
    )

    private var beenCaught = false

    init {
        super.initEntity()
        isCantFollowUnderCombat = true
        isCantInteract = true
    }

    private var index = 0

    override fun processNPC() {
        if (tiles.isEmpty()) {
            return
        }

        if (withinDistance(tiles[index], 1)) {
            index = (index + 1) % tiles.size
        } else {
            setForceWalk(tiles[index])
        }

        if (!beenCaught)
            for (target in possibleTargets) {
                if (withinDistance(target.tile, 4) && Utils.getAngleTo(target.x - x, target.y - y) == faceAngle) {
                    val player = target as? Player
                    if (player != null) {
                        if (isInStoreroom(target.tile) && !player.inCombat()) {
                            player.let {
                                it.stopAll()
                                forceTalk("!")
                                faceEntity(it)
                                anim(GOUTWEED_GUARD_THROW_ANIM)
                                val projectile = sendProjectile(this, it, ROCK_PROJECTILE, Pair(40, 30), 40, 5, 5)
                                CombatScript.delayHit(this, projectile.taskDelay, player, Hit.range(this, Random.nextInt(1, 61)))
                                player.schedule {
                                    wait (projectile.taskDelay)
                                    player.lock()
                                    player.anim(GOUTWEED_CAUGHT_ANIM)
                                }
                                schedule {
                                    wait(1)
                                    stopFaceEntity()
                                }
                                it.fadeScreen {
                                    it.anim(-1)
                                    it.spotAnim(STUNNED_BIRDS, 5, 100)
                                    it.tele(OUTSIDE_STOREROOM_TILE)
                                    beenCaught = false
                                    it.unlock()
                                }
                            }
                        }
                    }
                }
            }

        super.processNPC()
    }

    private fun isInStoreroom(tile: Tile): Boolean {
        for (range in storeroomTiles) if (tile.x() >= range[0] && tile.x() <= range[1] && tile.y() >= range[2] && tile.y() <= range[3]) return true
        return false
    }

    override fun getPossibleTargets(): List<Entity> {
        return queryNearbyPlayersByTileRangeAsEntityList(5) { player: Player -> !player.isDead && !player.appearance.isHidden && !player.isLocked && lineOfSightTo(player, false) }
    }

    override fun reset() {
        index = 0
        beenCaught = false
        super.reset()
    }
}

@ServerStartupEvent
fun mapGoutweedPatrolGuards() {
    val patrolConfigs = mapOf(
        1142 to arrayOf(Tile(2859, 10092, 0), Tile(2859, 10089, 0), Tile(2850, 10089, 0), Tile(2850, 10092, 0)),

        1143 to arrayOf(Tile(2859, 10089, 0), Tile(2855, 10089, 0), Tile(2855, 10086, 0), Tile(2859, 10086, 0)),

        1144 to arrayOf(Tile(2855, 10089, 0), Tile(2850, 10089, 0), Tile(2850, 10086, 0), Tile(2855, 10086, 0)),

        1145 to arrayOf(Tile(2854, 10086, 0), Tile(2854, 10082, 0), Tile(2852, 10082, 0), Tile(2852, 10078, 0),
            Tile(2850, 10078, 0), Tile(2850, 10086, 0)),

        1146 to arrayOf(Tile(2864,10085,0), Tile(2861, 10085, 0), Tile(2863, 10080, 0), Tile(2860, 10077, 0),
            Tile(2864, 10080, 0)),

        1147 to arrayOf(Tile(2855, 10082, 0), Tile(2852, 10082, 0), Tile(2852, 10078, 0), Tile(2850, 10078, 0),
            Tile(2850, 10075, 0), Tile(2853, 10075, 0), Tile(2853, 10077, 0), Tile(2855, 10077, 0)),

        1148 to arrayOf(Tile(2858, 10082, 0), Tile(2855, 10082, 0), Tile(2855, 10076, 0), Tile(2858, 10076, 0)),

        1149 to arrayOf(Tile(2861, 10085, 0), Tile(2864, 10085, 0), Tile(2864, 10080, 0), Tile(2860, 10077, 0),
            Tile(2863, 10080, 0))
    )

    patrolConfigs.forEach { (id, tiles) -> instantiateNpc(id) { npcId, tile -> GoutweedPatrolGuards(npcId, tile, tiles) } }
}
