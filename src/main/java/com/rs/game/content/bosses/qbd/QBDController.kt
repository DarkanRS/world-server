package com.rs.game.content.bosses.qbd

import com.rs.game.content.skills.magic.TeleType
import com.rs.game.map.instance.Instance
import com.rs.game.model.entity.player.InstancedController
import com.rs.game.model.entity.player.managers.InterfaceManager
import com.rs.lib.game.Tile

class QBDController() : InstancedController(Instance.of(Tile.of(1199, 6499, 0), 8, 8, true).setEntranceOffset(intArrayOf(33, 28, 1))) {
    override fun onBuildInstance() {
        player.lock()
        instance.copyMapAllPlanes(176, 792).thenAccept {
            instance.teleportLocal(player, 33, 28, 1)
            player.unlock()
        }
    }

    override fun onDestroyInstance() {
        player.unlock()
        removeInterfaces()
    }

    override fun onTeleported(type: TeleType) {
        player.controllerManager.forceStop()
        removeInterfaces()
    }

    fun removeInterfaces() {
        player.packets.sendVarc(184, -1)
        player.interfaceManager.removeSub(InterfaceManager.Sub.FULL_GAMESPACE_BG)
    }
}
