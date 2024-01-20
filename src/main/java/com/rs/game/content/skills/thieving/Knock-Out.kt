package com.rs.game.content.skills.thieving

import com.rs.engine.dialogue.HeadE
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.player.Player
import com.rs.lib.Constants
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick
import com.rs.utils.Ticks
import java.util.*

fun rollSuccess(player: Player): Boolean {
    return Utils.skillSuccess(
        player.skills.getLevel(Constants.THIEVING),
        player.auraManager.thievingMul + (if (LootThug.hasArdyCloak(player)) 0.1 else 0.0),
        185,
        255
    )
}

private fun successful(player: Player): Boolean {
    return rollSuccess(player)
}

private fun checkAll(player: Player): Boolean {
    if (player.isDead || player.hasFinished() || player.hasPendingHits()) return false
    if (player.attackedBy != null && player.inCombat()) {
        player.sendMessage("You can't do this while you're under combat.")
        return false
    }
    return true
}

@ServerStartupEvent
fun mapHandlers() {
    val validCoshIds = setOf(18644, 4599, 4600, 4612, 4608, 4609, 4610, 4611, 4612, 4613, 4614, 4615, 4616, 4617, 4618, 4619, 4620, 4621)
    val validTrainingNPC = setOf(11290, 11292, 11288, 11296)
    val response = arrayOf(
        "No, no, not like that.",
        "Spotted you!"
    )

    onNpcClick(11290, 11292, 11288, 11296, options = arrayOf("Knock-out")) { e ->
        if(e.player.equipment.getWeaponId() !in validCoshIds){
            e.player.sendMessage("I'll need a cosh to knock them out")
            return@onNpcClick
        }
        if (e.npcId in validTrainingNPC && e.player.equipment.getWeaponId() != 18644) {
            e.player.sendMessage("I'll need a training cosh to practise my technique with.")
            return@onNpcClick
        }
        val luredBy = e.npc.tempAttribs.getO<Player>("lured")
        if (e.npc.tempAttribs.getO<Player>("lured") == null) {
            e.player.playerDialogue(
                HeadE.SKEPTICAL_THINKING,
                "I need to divert his attention first."
            )
            return@onNpcClick
        }
        if (luredBy != e.player) {
            e.player.sendMessage("Someone else knocked out that target.")
            return@onNpcClick
        }
        if (checkAll(e.player)) {
            if (successful(e.player)) {
                e.player.sendMessage("You smack the " + e.npc.name + " over the head.")
                e.npc.actionManager.forceStop()
                e.npc.tempAttribs.setO<Player>("K.O", e.player)
                e.player.anim(10267)
                e.npc.anim(837)
                e.npc.freeze(Ticks.fromSeconds(10))
                e.npc.tasks.schedule(Ticks.fromSeconds(10)) {
                    e.npc.anim(-1)
                    e.npc.tempAttribs.removeO<Player>("K.O")
                }
            }
            else {
                e.player.anim(10267)
                if(e.npcId in validTrainingNPC) {
                    e.player.sendMessage(
                        "You fail to pick the " + e.npc.definitions.name
                            .lowercase(Locale.getDefault()) + "'s pocket."
                    )
                    e.npc.anim(422)
                    e.npc.faceEntity(e.player)
                    e.player.anim(424)
                    e.player.spotAnim(80, 5, 60)
                    e.player.sendMessage("You've been stunned.")
                    e.player.applyHit(Hit(e.player, 1, Hit.HitLook.TRUE_DAMAGE))
                    e.npc.forceTalk(response[Utils.random(2)])
                }
            }
        }
    }

    onNpcClick(1903, options = arrayOf("Knock-Out")) { e ->
        if(e.player.equipment.getWeaponId() !in validCoshIds){
            e.player.sendMessage("I'll need a cosh to knock them out")
            return@onNpcClick
        }
        if (e.player.equipment.getWeaponId() == 18644) {
            e.player.sendMessage("A training cosh will not be enough to knock them out.")
            return@onNpcClick
        }
        val luredBy = e.npc.tempAttribs.getO<Player>("lured")
        if (e.npc.tempAttribs.getO<Player>("lured") == null) {
            e.player.playerDialogue(
                HeadE.SKEPTICAL_THINKING,
                "I need to divert his attention first."
            )
            return@onNpcClick
        }
        if (luredBy != e.player) {
            e.player.sendMessage("Someone else knocked out that target.")
            return@onNpcClick
        }
        if (checkAll(e.player)) {
            if (successful(e.player)) {
                e.player.sendMessage("You smack the " + e.npc.name + " over the head.")
                e.npc.actionManager.forceStop()
                e.npc.tempAttribs.setO<Player>("K.O", e.player)
                e.player.anim(10267)
                e.npc.anim(837)
                e.npc.freeze(Ticks.fromSeconds(10))
                e.npc.tasks.schedule(Ticks.fromSeconds(10)) {
                    e.npc.anim(-1)
                    e.npc.tempAttribs.removeO<Player>("K.O")
                }
            }
            else {
                e.npc.forceTalk("I'll kill you for that!")
                e.npc.setAttackedBy(e.player)
            }
        }
    }

}

