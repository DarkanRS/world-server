package com.rs.game.content.quests.dig_site.utils

import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.actions.PlayerAction
import com.rs.game.model.`object`.GameObject
import kotlin.random.Random

class PanningAction(private val obj: GameObject) : PlayerAction() {

    override fun start(player: Player): Boolean {
        if (checkAll(player)) {
            player.lock()
            player.faceObject(obj)
            player.anim(PANNING_ANIM)
            player.sendMessage("You lower the panning tray into the water.", true)
            setActionDelay(player, 4)
            return true
        }
        return false
    }

    override fun process(player: Player): Boolean {
        player.faceObject(obj)
        player.anim(PANNING_ANIM)
        return checkAll(player)
    }

    override fun processWithDelay(player: Player): Int {
        player.startConversation {
            val newTray = if (Random.nextInt(100) < 90) MUD_PANNING_TRAY else GOLD_PANNING_TRAY
            item(newTray, "You lift the full tray from the water.") {
                player.inventory.replace(EMPTY_PANNING_TRAY, newTray)
            }
        }
        stop(player)
        return -1
    }

    override fun stop(player: Player) {
        player.unlock()
        player.stopFaceEntity()
        player.anim(-1)
        setActionDelay(player, 2)
    }

    private fun checkAll(player: Player): Boolean {
        if (player.isDead || player.hasFinished() || player.hasPendingHits()) return false
        if (player.attackedBy != null && player.inCombat()) {
            player.sendMessage("You can't do that whilst you're in combat.")
            return false
        }
        return true
    }
}
