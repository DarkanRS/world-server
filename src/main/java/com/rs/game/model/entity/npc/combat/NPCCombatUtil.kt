package com.rs.game.model.entity.npc.combat

import com.rs.game.World
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.npc.NPC
import com.rs.lib.util.Utils
import java.util.function.Consumer

class NPCCombatUtil {
    companion object {
        @JvmStatic
        fun projectileBounce(npc: NPC, target: Entity, targetsHit: MutableSet<Entity>, projId: Int, hitSpotAnim: Int, bounceSpeed: Double, bouncedFrom: Entity? = null, onHit: Consumer<Entity>) {
            World.sendProjectile(bouncedFrom ?: npc, target, projId, 30, 15, bounceSpeed) {
                target.spotAnim(hitSpotAnim)
                onHit.accept(target)
                val nextTarget = npc.queryNearbyPlayersByTileRange(2) { !targetsHit.contains(it) }
                    .minByOrNull { Utils.getDistance(it.tile, target.tile) }
                if (nextTarget != null) {
                    targetsHit.add(nextTarget)
                    projectileBounce(npc, nextTarget, targetsHit, projId, hitSpotAnim, bounceSpeed, target, onHit)
                }
            }
        }
    }
}