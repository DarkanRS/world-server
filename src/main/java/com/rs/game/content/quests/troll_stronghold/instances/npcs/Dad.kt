package com.rs.game.content.quests.troll_stronghold.instances.npcs

import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.quest.Quest
import com.rs.game.World
import com.rs.game.content.quests.troll_stronghold.utils.*
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.npc.combat.CombatScript
import com.rs.game.model.entity.npc.combat.CombatScriptsHandler
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.npcCombat
import kotlin.math.sign

class Dad(id: Int, tile: Tile) : NPC(id, tile) {

    private var choseToKill = false

    override fun processNPC() {
        val target = combat.target
        super.processNPC()

        if (target is Player && combat.target != target)
            target.hintIconsManager.removeUnsavedHintIcon()

        if (hitpoints <= 330) {
            if (target is Player && combat.target != null && !choseToKill) {
                combat.target = null
                target.actionManager.forceStop()
                target.interactionManager.forceStop()
                follow(target)
                target.lock()
                target.cutscene {
                    wait(2)
                    dialogue{
                        npc(DAD, T_SCARED, "Stop! You win. Not hurt Dad.")
                        options {
                            op("I'll be going now.") {
                                player(CALM_TALK, "I'll be going now.")
                                exec {
                                    player.unlock()
                                    hitpoints = maxHitpoints
                                    actionManager.forceStop()
                                    player.actionManager.forceStop()
                                    player.stopFaceEntity()
                                    player.setQuestStage(Quest.TROLL_STRONGHOLD, STAGE_FINISHED_DAD)
                                    player.hintIconsManager.removeUnsavedHintIcon()
                                    player.sendMessage("You have defeated Dad.")
                                }
                            }
                            op("I'm not done yet! Prepare to die!") {
                                player(CALM_TALK, "I'm not done yet! Prepare to die!") { target.stopFaceEntity() }
                                exec {
                                    combat.target = target
                                    target.setForceMultiArea(true)
                                    setForceMultiArea(true)
                                    val spectators = World.getNPCsInChunkRange(target.chunkId, 2).filter { it.id in SPECTATORS }
                                    spectators.forEach { spectator ->
                                        spectator.combatTarget = target
                                        spectator.setForceMultiArea(true)
                                    }
                                    choseToKill = true
                                    target.stopFaceEntity()
                                }
                            }
                        }
                    }
                    waitForDialogue()
                }
            }
        }
    }

    override fun canBeAttackedBy(player: Player): Boolean {
        when (player.getQuestStage(Quest.TROLL_STRONGHOLD)) {
            in STAGE_ENTERED_ARENA..STAGE_ENGAGED_DAD -> {
                return true
            }
            in STAGE_FINISHED_DAD..STAGE_COMPLETE -> {
                player.sendMessage("You don't need to fight him again.")
                return false
            }
        }
        player.sendMessage("You have no reason to fight Dad right now.")
        return false
    }

    override fun sendDeath(source: Entity) {
        super.sendDeath(source)
        choseToKill = false
        val player = source as Player
        if (player.getQuestStage(Quest.TROLL_STRONGHOLD) == STAGE_ENGAGED_DAD) {
            player.setQuestStage(Quest.TROLL_STRONGHOLD, STAGE_FINISHED_DAD)
            player.hintIconsManager.removeUnsavedHintIcon()
            player.sendMessage("You have defeated Dad.")
        }
    }

    override fun reset() {
        super.reset()
        isIgnoreNPCClipping = false
        choseToKill = false
    }

}

@ServerStartupEvent
fun mapDadCombat() {
    val previousPositions = mutableMapOf<Entity, Tile>()

    npcCombat(DAD) { npc, target ->

        if (target is Player) {
            if (target.getQuestStage(Quest.TROLL_STRONGHOLD) == STAGE_ENTERED_ARENA) target.setQuestStage(Quest.TROLL_STRONGHOLD, STAGE_ENGAGED_DAD)
            if (!target.hintIconsManager.hasHintIcon(0)) target.hintIconsManager.addHintIcon(npc, 0, -1, false)
        }

        npc.isIgnoreNPCClipping = true

        if (previousPositions[target] == null) {
            previousPositions[target] = target.tileBehind
        }
        val previousPosition = previousPositions[target]!!

        if (target.inMeleeRange(npc) && Utils.random(2) == 0 && npc.hitpoints >= 330) {
            val maxDistance = 4
            val moveToTile = getKnockbackTile(npc, target, previousPosition, maxDistance)

            if (moveToTile != null && moveToTile.x > 2912 && moveToTile.y < 3619) {
                target.schedule {
                    target.faceEntity(npc)
                    target.stun(2)
                    npc.anim(NPC_KNOCKBACK_ANIM)
                    target.forceMove(moveToTile, PLAYER_KNOCKBACK_ANIM, 1, 40)
                    target.actionManager.addActionDelay(2)
                    CombatScript.delayHit(npc, 0, target, CombatScript.getMeleeHit(npc, CombatScript.getMaxHit(npc, npc.combatDefinitions.maxHit, NPCCombatDefinitions.AttackStyle.MELEE, target, 10000.0)))
                    target.stopFaceEntity()
                }
                previousPositions[target] = moveToTile
            } else {
                return@npcCombat CombatScriptsHandler.getDefaultCombat().apply(npc, target)
            }
        } else {
            return@npcCombat CombatScriptsHandler.getDefaultCombat().apply(npc, target)
        }
        return@npcCombat npc.attackSpeed
    }
}

fun getKnockbackTile(npc: Entity, player: Entity, previousPosition: Tile, maxDistance: Int): Tile? {
    val isFacingNpc = player.tile == player.frontfacingTile && player.frontfacingTile == npc.tile

    val directionVectorX: Int
    val directionVectorY: Int

    if (isFacingNpc) {
        directionVectorX = previousPosition.x - player.tile.x
        directionVectorY = previousPosition.y - player.tile.y
    } else {
        directionVectorX = player.tile.x - previousPosition.x
        directionVectorY = player.tile.y - previousPosition.y
    }

    if (directionVectorX != 0 || directionVectorY != 0) {
        for (distance in maxDistance downTo 1) {
            val potentialTile = player.tile.transform(
                sign(directionVectorX.toDouble()).toInt() * distance,
                sign(directionVectorY.toDouble()).toInt() * distance
            )
            if (World.floorAndWallsFree(potentialTile, 1)) {
                return potentialTile
            }
        }
    }
    return null
}
