package com.rs.game.content.combat

import com.rs.game.content.world.npcs.DoorSupport
import com.rs.game.model.entity.interactions.PlayerCombatInteraction
import com.rs.game.model.entity.interactions.StandardEntityInteraction
import com.rs.lib.Constants
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

@ServerStartupEvent
fun mapNpcAttackOptions() {
    onNpcClick(options = arrayOf("Attack"), checkDistance = false) { e ->
        e.player.stopAll(true)
        e.player.interactionManager.setInteraction(PlayerCombatInteraction(e.player, e.npc))
    }

    onNpcClick(2440, 2443, 2446, options = arrayOf("Destroy"), checkDistance = false) { e ->
        if (e.npc is DoorSupport) {
            if (!(e.npc as DoorSupport).canDestroy(e.player)) {
                e.player.sendMessage("You cannot see a way to open this door...")
                return@onNpcClick
            }
        }
        e.player.stopAll(true)
        e.player.interactionManager.setInteraction(PlayerCombatInteraction(e.player, e.npc))
    }

    onNpcClick(7891, options = arrayOf("Attack")) { e ->
        e.player.interactionManager.setInteraction(StandardEntityInteraction(e.npc, 0) {
            if (!e.player.controllerManager.canAttack(e.npc)) return@StandardEntityInteraction
            e.npc.resetWalkSteps()
            e.player.faceEntity(e.npc)
            if (e.player.skills.getLevelForXp(Constants.ATTACK) < 5) {
                if (e.player.actionManager.actionDelay < 1) {
                    e.player.actionManager.actionDelay = 4
                    e.player.anim(PlayerCombat.getWeaponAttackEmote(e.player.equipment.weaponId, e.player.combatDefinitions.getAttackStyle()))
                    e.player.skills.addXp(Constants.ATTACK, 15.0)
                }
            } else e.player.sendMessage("You have nothing more you can learn from this.")
        })
    }
}
