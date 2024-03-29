package com.rs.game.content.miniquests.from_tiny_acorns

import com.rs.engine.miniquest.Miniquest
import com.rs.game.content.skills.thieving.PickPocketableNPC.hasArdyCloak
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.actions.PlayerAction
import com.rs.game.model.`object`.GameObject
import com.rs.game.tasks.WorldTasks
import com.rs.lib.Constants
import com.rs.lib.util.Utils

class StealToyDragon(private val obj: GameObject) : PlayerAction() {

    private var success = false
    override fun start(player: Player): Boolean {
        if (checkAll(player)) {
            success = successful(player)
            player.faceObject(obj)
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
            player.sendMessage("You failed to steal the toy dragon.")
        } else {
            if (player.inventory.hasFreeSlots()) {
                player.inventory.addItem(18651, 1)
                player.sendMessage("You take the toy dragon from the stall.")
                player.vars.setVarBit(7821, 0)
                player.miniquestManager.setStage(Miniquest.FROM_TINY_ACORNS, 2)
            } else {
                player.sendMessage("You do not have enough space to do that.")
            }
        }
        stop(player)
        return -1
    }

    override fun stop(player: Player) {
        player.unlock()
        player.setNextFaceEntity(null)
        setActionDelay(player, 1)
    }

    fun rollSuccess(player: Player): Boolean {
        return Utils.skillSuccess(player.skills.getLevel(Constants.THIEVING), player.auraManager.thievingMul + (if (hasArdyCloak(player)) 0.1 else 0.0), 185, 255)
    }

    private fun successful(player: Player): Boolean {
        if (!rollSuccess(player)) return false
        return true
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