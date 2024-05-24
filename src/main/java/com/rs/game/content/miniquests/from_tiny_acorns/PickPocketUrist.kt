package com.rs.game.content.miniquests.from_tiny_acorns

import com.rs.engine.dialogue.HeadE
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.actions.PlayerAction
import com.rs.game.tasks.WorldTasks
import com.rs.lib.Constants
import com.rs.lib.util.Utils

class PickPocketUrist(private val npc: NPC) : PlayerAction() {
    private var success = false

    override fun start(player: Player): Boolean {
        if (checkAll(player)) {
            success = rollSuccess(player)
            player.faceEntityTile(npc)
            WorldTasks.delay(0) {
                player.anim(881)
            }
            setActionDelay(player, 2)
            player.lock()
            return true
        }
        return false
    }

    override fun process(player: Player): Boolean {
        return checkAll(player)
    }

    override fun processWithDelay(player: Player): Int {
        if (!success) {
            player.npcDialogue(npc.id, HeadE.ANGRY, "Oi! Leave that alone.")
        } else {
            player.inventory.addItem(18649, 1)
            player.sendMessage("You steal a golden talisman out of Urist's back pocket.")
        }
        stop(player)
        return -1
    }

    override fun stop(player: Player) {
        player.unlock()
        player.stopFaceEntity()
        setActionDelay(player, 1)
    }

    fun rollSuccess(player: Player): Boolean {
        return Utils.skillSuccess(player.skills.getLevel(Constants.THIEVING), player.auraManager.thievingMul, 185, 255)
    }

    private fun checkAll(player: Player): Boolean {
        if (player.isDead || player.hasFinished() || player.hasPendingHits()) return false
        if (player.attackedBy != null && player.inCombat()) {
            player.sendMessage("You can't do this while you're under combat.")
            return false
        }
        return true
    }
}

