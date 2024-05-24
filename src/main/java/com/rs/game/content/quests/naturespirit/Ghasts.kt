package com.rs.game.content.quests.naturespirit

import com.rs.engine.pathfinder.collision.CollisionStrategyType
import com.rs.game.World
import com.rs.game.content.skills.cooking.Foods
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.npc.combat.CombatScriptsHandler
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.npcCombat

val GHASTS = arrayOf(1052, 1053, 14035, 14036, 14037, 14038, 14039, 14040)

class Ghast(id: Int, tile: Tile) : NPC(id, tile) {
    override fun processNPC() {
        super.processNPC()
        isForceAgressive = true
        setCollisionStrategyType(CollisionStrategyType.FLY)
    }

    override fun canBeAttackedBy(player: Player): Boolean {
        return getDefinitions(player).hasAttackOption()
    }

    override fun canBeAutoRetaliated(): Boolean {
        return false
    }

    override fun getPossibleTargets(): MutableList<Entity> {
        //Can't attack in filliman's grotto zone
        return super.getPossibleTargets().filter { !intArrayOf(879008, 881056, 881057, 879009).contains(it.chunkId) }.toMutableList()
    }

    override fun sendDeath(source: Entity?) {
        spotAnim(265)
        if (source != null && source is Player)
            source.skills.addXp(Skills.PRAYER, 30.0)
        super.sendDeath(source)
    }

    override fun finish() {
        transformIntoNPC(when (id) {
            1053 -> 1052
            14038 -> 14035
            14039 -> 14036
            14040 -> 14037
            else -> id
        })
        super.finish()
    }
}

@ServerStartupEvent
fun mapGhasts() {
    instantiateNpc(*GHASTS) { id, tile -> Ghast(id, tile) }

    npcCombat(*GHASTS) { npc, target ->
        if (npc.definitions.hasAttackOption())
            return@npcCombat CombatScriptsHandler.getDefaultCombat().apply(npc, target)
        if (target is Player) {
            val pouch = target.inventory.getItemById(2958)
            if (pouch != null) {
                npc.anim(npc.combatDefinitions.attackEmote)
                pouch.amount--
                if (pouch.amount <= 0) {
                    pouch.amount = 1
                    pouch.id = 2957
                }
                target.inventory.refresh()
                npc.lock()
                World.sendProjectile(target, npc, 268, 0, 15, 30) {
                    npc.unlock()
                    npc.spotAnim(269, 0, 96)
                    npc.transformIntoNPC(when (npc.id) {
                        1052 -> 1053
                        14035 -> 14038
                        14036 -> 14039
                        14037 -> 14040
                        else -> npc.id
                    })
                }
                return@npcCombat npc.attackSpeed
            }
            if (Utils.random(10) < 3) {
                val foods = target.inventory.items.array().filter { it != null && Foods.isConsumable(it) }
                if (foods.isNotEmpty()) {
                    npc.anim(npc.combatDefinitions.attackEmote)
                    val food = foods.random()
                    food.id = 2959
                    food.amount = 1
                    target.inventory.refresh()
                    return@npcCombat 15
                } else {
                    npc.anim(npc.combatDefinitions.attackEmote)
                    target.applyHit(Hit(npc, Utils.random(10, 30), Hit.HitLook.MELEE_DAMAGE))
                    return@npcCombat 15
                }
            } else {
                target.sendMessage("The ghast misses you.")
                return@npcCombat 15
            }
        }
        return@npcCombat CombatScriptsHandler.getDefaultCombat().apply(npc, target)
    }
}