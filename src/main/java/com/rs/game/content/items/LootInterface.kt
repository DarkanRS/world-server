package com.rs.game.content.items

import com.rs.cache.loaders.ItemDefinitions
import com.rs.game.content.ItemConstants
import com.rs.game.model.entity.player.Player
import com.rs.game.model.item.ItemsContainer
import com.rs.lib.game.Item
import com.rs.lib.net.ClientPacket
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onButtonClick
import com.rs.utils.ItemConfig

@ServerStartupEvent
fun mapLootInterface() {
    onButtonClick(1284) { e ->
        val container = e.player.tempAttribs.getO<ItemsContainer<Item?>>("lootInterfaceContainer")
        if (container == null) {
            e.player.tempAttribs.removeO<Any>("lootInterfaceContainer")
            e.player.closeInterfaces()
            e.player.sendMessage("Loot interface container was null. Aborting.")
            return@onButtonClick
        }
        when(e.componentId) {
            7 -> {
                val item = container[e.slotId] ?: return@onButtonClick
                when (e.packet) {
                    ClientPacket.IF_OP1 -> {
                        if (e.player.inventory.addItem(Item(item).setAmount(1))) {
                            item.amount--
                            if (item.amount <= 0)
                                container[e.slotId] = null
                            e.player.packets.sendUpdateItems(100, container, e.slotId)
                        }
                    }
                    ClientPacket.IF_OP2 -> {
                        if (e.player.bank.addItem(item, true)) {
                            container[e.slotId] = null
                            e.player.packets.sendUpdateItems(100, container, e.slotId)
                        }
                    }
                    ClientPacket.IF_OP3 -> {
                        container[e.slotId] = null
                        e.player.packets.sendUpdateItems(100, container, e.slotId)
                    }
                    ClientPacket.IF_OP4 -> sendExamine(e.player, item)
                    else -> {}
                }
            }
            8 -> {
                for (slot in 0 until container.size) {
                    if (container[slot] == null) continue
                    if (e.player.bank.addItem(container[slot], true)) container[slot] = null
                }
                if (!container.isEmpty) e.player.packets.sendItems(100, container)
            }
            9 -> {
                e.player.sendMessage("You abandon all the items.")
                container.clear()
            }
            10 -> {
                for (slot in 0 until container.size) {
                    if (container[slot] == null) continue
                    if (e.player.inventory.addItemDrop(container[slot])) container[slot] = null
                }
                if (!container.isEmpty) e.player.packets.sendItems(100, container)
            }
        }
        if (container.isEmpty) {
            e.player.sendMessage("You've finished looting everything available.")
            e.player.closeInterfaces()
        }
    }
}

object LootInterface {
    @JvmStatic
    @JvmOverloads
    fun open(title: String?, player: Player, container: ItemsContainer<Item?>, autoLootOnClose: Boolean = true, onClose: Runnable? = null) {
        player.packets.setIFText(1284, 28, title)
        player.interfaceManager.sendInterface(1284)
        player.packets.sendInterSetItemsOptionsScript(1284, 7, 100, 7, 4, "Take-1", "Bank", "Discard", "Examine")
        player.packets.setIFRightClickOps(1284, 7, 0, 28, 0, 1, 2, 3)
        player.packets.sendItems(100, container)
        player.tempAttribs.setO<Any>("lootInterfaceContainer", container)
        player.setCloseInterfacesEvent {
            player.tempAttribs.removeO<Any>("lootInterfaceContainer")
            onClose?.run()
            if (autoLootOnClose) {
                container.array().filterNotNull().forEach { item ->
                    if (player.inventory.hasRoomFor(item)) {
                        player.inventory.addItemDrop(item)
                    } else {
                        if (item.definitions.isNoted)
                            item.id = item.definitions.certId
                        if (!player.bank.addItem(item, true))
                            player.inventory.addItemDrop(item)
                    }
                }
            }
        }
    }
}

private fun sendExamine(player: Player, item: Item) {
    val def = ItemDefinitions.getDefs(item.id)
    player.sendMessage(ItemConfig.get(item.id).getExamine(item) + (if (ItemConstants.isTradeable(item)) (" General store: " + Utils.formatTypicalInteger(item.definitions.sellPrice) + " High Alchemy: " + Utils.formatTypicalInteger(def.highAlchPrice)) else ""))
    if (item.getMetaData("combatCharges") != null) player.sendMessage("<col=FF0000>It looks like it will last another " + Utils.ticksToTime(item.getMetaDataI("combatCharges").toDouble()))
    else if (item.getMetaData("brawlerCharges") != null) player.sendMessage("These gloves have " + item.getMetaDataI("brawlerCharges") + " charges left.")
}
