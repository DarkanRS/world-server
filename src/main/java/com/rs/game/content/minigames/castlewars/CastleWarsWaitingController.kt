package com.rs.game.content.minigames.castlewars

import com.rs.game.content.skills.magic.TeleType
import com.rs.game.model.entity.Teleport
import com.rs.game.model.entity.player.Controller
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.`object`.GameObject
import com.rs.lib.game.Tile
import com.rs.lib.net.ClientPacket

class CastleWarsWaitingController(private val team: Int) : Controller() {

    override fun start() {
        sendInterfaces()
    }

    fun leave() {
        player.interfaceManager.removeOverlay()
        CastleWars.removeWaitingPlayer(player, team)
    }

    override fun sendInterfaces() {
        player.interfaceManager.sendOverlay(57)
    }

    override fun processButtonClick(interfaceId: Int, componentId: Int, slotId: Int, slotId2: Int, packet: ClientPacket?): Boolean {
        when (interfaceId) {
            387 -> {
                if (componentId == 9 || componentId == 6)
                    player.sendMessage("You can't remove your team's colours.")
                return false
            }
        }
        return true
    }

    override fun canEquip(slotId: Int, itemId: Int): Boolean {
        if (slotId == Equipment.CAPE || slotId == Equipment.HEAD) {
            player.sendMessage("You can't remove your team's colours.")
            return false
        }
        return true
    }

    override fun sendDeath(): Boolean {
        removeController()
        leave()
        return true
    }

    override fun logout(): Boolean {
        player.tile = Tile.of(CastleWars.LOBBY, 2)
        return true
    }

    override fun processTeleport(tele: Teleport?): Boolean {
        player.simpleDialogue("You can't leave just like that!")
        return false
    }

    override fun processObjectClick1(obj: GameObject?): Boolean {
        val id = obj?.id
        if (id == 4389 || id == 4390) {
            removeController()
            leave()
            return false
        }
        return true
    }

    override fun onTeleported(type: TeleType?) {
        removeController()
        leave()
    }

    override fun forceClose() {
        leave()
    }

}