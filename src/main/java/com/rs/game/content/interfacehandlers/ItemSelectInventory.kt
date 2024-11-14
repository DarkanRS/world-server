package com.rs.game.content.interfacehandlers

import com.rs.game.content.interfacehandlers.ItemSelectInventory.Mode
import com.rs.game.content.world.areas.gu_tanoth.npcs.Bogrog
import com.rs.game.ge.GE
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onButtonClick

@ServerStartupEvent
fun mapItemSelectInventoryButtonHandler() {
    onButtonClick(107) { e ->
        val player = e.player
        val item = player.inventory.getItem(e.slotId) ?: return@onButtonClick
        val modeName = player.tempAttribs.getO<String>("itemSelectMode") ?: return@onButtonClick
        val mode = Mode.valueOf(modeName)

        when (mode) {
            Mode.GE_SELL -> GE.handleSellInv(player, e.slotId, e.componentId)
            Mode.BOGROG -> Bogrog.handleBogrogActions(player, item, e.packet)
        }
    }
}

object ItemSelectInventory {

    enum class Mode {
        GE_SELL,
        BOGROG
    }

    @JvmStatic
    fun openItemSelectInventory(player: Player, mode: Mode) {
        player.tempAttribs.setO<String>("itemSelectMode", mode.name)

        val options = getOptions(mode)
        val rightClickOpsRange = IntArray(options.size) { it }
        player.interfaceManager.sendInventoryInterface(107)
        player.packets.sendItems(93, player.inventory.items)
        player.packets.setIFRightClickOps(107, 18, 0, 27, *rightClickOpsRange)
        player.packets.sendInterSetItemsOptionsScript(107, 18, 93, 4, 7, *options)
        player.interfaceManager.closeChatBoxInterface();
    }

    fun getOptions(mode: Mode): Array<String> {
        return when (mode) {
            Mode.GE_SELL -> arrayOf("Offer")
            Mode.BOGROG -> arrayOf("Value", "Swap-1", "Swap-5", "Swap-10", "Swap-X", "Examine")
        }
    }
}
