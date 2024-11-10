package com.rs.game.content.interfacehandlers

import com.rs.game.content.achievements.AchievementSetRewards.handleAlchemyActions
import com.rs.game.content.achievements.AchievementSetRewards.handleSuperheatAction
import com.rs.game.content.interfacehandlers.ItemSelectWindow.Mode
import com.rs.game.content.world.areas.gu_tanoth.npcs.Bogrog
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onButtonClick

@ServerStartupEvent
fun mapItemSelectWindowButtonHandler() {
    onButtonClick(12) { e ->
        val player = e.player
        val item = player.inventory.getItem(e.slotId) ?: return@onButtonClick
        val type = player.tempAttribs.getI("itemSelectType")
        val modeName = player.tempAttribs.getO<String>("itemSelectMode") ?: return@onButtonClick
        val mode = Mode.valueOf(modeName)

        when (mode) {
            Mode.ALCHEMY -> handleAlchemyActions(player, item, type)
            Mode.SUPERHEAT -> handleSuperheatAction(player, item)
            Mode.BOGROG -> Bogrog.handleBogrogActions(player, item, e.packet)
        }
    }
}

object ItemSelectWindow {

    enum class Mode {
        ALCHEMY,
        SUPERHEAT,
        BOGROG
    }

    @JvmStatic
    fun openItemSelectWindow(player: Player, type: Int, mode: Mode) {
        player.tempAttribs.setI("itemSelectType", type)
        player.tempAttribs.setO<String>("itemSelectMode", mode.name)

        val options = getOptions(type, mode)
        val rightClickOpsRange = IntArray(options.size) { it }
        player.packets.setIFText(12, 11, getTitle(type, mode))
        player.packets.sendInterSetItemsOptionsScript(12, 13, 93, 7, 4, *options)
        player.packets.setIFRightClickOps(12, 13, 0, 27, *rightClickOpsRange)
        player.interfaceManager.sendInterface(12)
    }

    fun getTitle(type: Int, mode: Mode): String {
        return when (mode) {
            Mode.ALCHEMY -> when (type) {
                0 -> "Low-alchemy"
                1 -> "High-alchemy"
                else -> "Unknown Action"
            }
            Mode.SUPERHEAT -> "Superheat"
            Mode.BOGROG -> "Boggy's Roggy"
            else -> "Unknown Action"
        }
    }

    fun getOptions(type: Int, mode: Mode): Array<String> {
        return when (mode) {
            Mode.ALCHEMY -> when (type) {
                0 -> arrayOf("Low-alchemy")
                1 -> arrayOf("High-alchemy")
                else -> arrayOf("Unknown Option")
            }
            Mode.SUPERHEAT -> arrayOf("Superheat")
            Mode.BOGROG -> arrayOf("Value", "Swap-1", "Swap-5", "Swap-10", "Swap-X", "Examine")
            else -> arrayOf("Unknown Option")
        }
    }
}
