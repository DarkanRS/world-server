package com.rs.game.model.entity.npc.combat

import com.rs.game.World
import com.rs.game.content.Effect
import com.rs.game.content.combat.CombatSpell
import com.rs.game.content.combat.calculateMagicHit
import com.rs.game.content.combat.delayMagicHit
import com.rs.game.content.combat.getMagicBonusBoost
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.npc.combat.CombatScript.getMagicHit
import com.rs.game.model.entity.npc.combat.CombatScript.getMaxHit
import com.rs.game.model.entity.player.Player
import com.rs.lib.Constants
import com.rs.lib.util.Utils
import java.util.function.Consumer

class NPCCombatUtil {
    companion object {
        @JvmStatic
        fun projectileBounce(npc: NPC, target: Entity, targetsHit: MutableSet<Entity>, projId: Int, hitSpotAnim: Int, clientFramesPerTile: Int, bouncedFrom: Entity? = null, onHit: Consumer<Entity>) {
            World.sendProjectile(bouncedFrom ?: npc, target, projId, delay = 30, speed = clientFramesPerTile, angle = 15) {
                target.spotAnim(hitSpotAnim)
                onHit.accept(target)
                val nextTarget = npc.queryNearbyPlayersByTileRange(2) { !targetsHit.contains(it) }
                    .minByOrNull { Utils.getDistance(it.tile, target.tile) }
                if (nextTarget != null) {
                    targetsHit.add(nextTarget)
                    projectileBounce(npc, nextTarget, targetsHit, projId, hitSpotAnim, clientFramesPerTile, target, onHit)
                }
            }
        }

        @JvmStatic
        fun castSpellAtTarget(npc: NPC, target: Entity, spell: CombatSpell, hitDelay: Int): Boolean {
            val hit = getMagicHit(npc, getMaxHit(npc, spell.getBaseDamage(npc), NPCCombatDefinitions.AttackStyle.MAGE, target))
            if (spell === CombatSpell.STORM_OF_ARMADYL && hit.damage > 0) {
                var minHit = (npc.getLevel(NPCCombatDefinitions.Skill.MAGE) - 77) * 5
                minHit = (minHit * getMagicBonusBoost(npc)).toInt()
                if (hit.damage < minHit) hit.setDamage(hit.damage + minHit)
            }
            hit.setData("combatSpell", spell)
            val sparkle = target.size >= 2 || target.hasEffect(Effect.FREEZE) || target.hasEffect(Effect.FREEZE_BLOCK)
            delayMagicHit(target, hitDelay, hit, {
                if (hit.damage > 0) when (spell) {
                    CombatSpell.ICE_RUSH, CombatSpell.ICE_BURST, CombatSpell.ICE_BLITZ, CombatSpell.ICE_BARRAGE -> {
                        if (sparkle) target.spotAnim(1677, 0, 96)
                        else target.spotAnim(spell.hitSpotAnim)
                        if (spell.landSound != -1) npc.soundEffect(target, spell.landSound, true)
                    }

                    else -> {
                        target.spotAnim(spell.hitSpotAnim)
                        if (spell.landSound != -1) npc.soundEffect(target, spell.landSound, true)
                    }
                } else {
                    target.spotAnim(85, 0, 96)
                    if (spell.splashSound != -1) npc.soundEffect(target, spell.splashSound, true)
                    else npc.soundEffect(target, 227, true)
                }
            }, { spell.onHit(npc, target, hit) }, null)
            return hit.damage > 0
        }
    }
}