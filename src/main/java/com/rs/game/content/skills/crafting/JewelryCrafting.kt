// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.skills.crafting

import com.rs.game.model.entity.player.Player
import com.rs.lib.Constants
import com.rs.lib.game.Item
import com.rs.lib.net.ClientPacket
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onButtonClick

const val RING_MOULD = 1592
const val AMULET_MOULD = 1595
const val NECKLACE_MOULD = 1597
const val BRACELET_MOULD = 11065

const val GOLD_BAR = 2357

const val SAPPHIRE = 1607
const val EMERALD = 1605
const val RUBY = 1603
const val DIAMOND = 1601
const val DRAGONSTONE = 1615
const val ONYX = 6573
const val ENCHANTED_GEM = 4155

enum class Bling(val levelRequired: Int, val experience: Double, private val itemsRequired: Array<Int>, val product: Int, val buttonId: Int, val nullId: Int) {
    GOLD_RING(5, 15.0, arrayOf(RING_MOULD, GOLD_BAR),  1635, 82, 1647),
    SAPP_RING(20, 40.0, arrayOf(RING_MOULD, GOLD_BAR, SAPPHIRE), 1637, 84, 1647),
    EMER_RING(27, 55.0, arrayOf(RING_MOULD, GOLD_BAR, EMERALD), 1639, 86, 1647),
    RUBY_RING(34, 70.0, arrayOf(RING_MOULD, GOLD_BAR, RUBY), 1641, 88, 1647),
    DIAM_RING(43, 85.0, arrayOf(RING_MOULD, GOLD_BAR, DIAMOND), 1643, 90, 1647),
    DRAG_RING(55, 100.0, arrayOf(RING_MOULD, GOLD_BAR, DRAGONSTONE), 1645, 92, 1647),
    ONYX_RING(67, 115.0, arrayOf(RING_MOULD, GOLD_BAR, ONYX), 6575, 94, 6564),
    SLAYER_RING(55, 15.0, arrayOf(RING_MOULD, GOLD_BAR, ENCHANTED_GEM), 13281, 97, 1647),

    GOLD_NECK(6, 20.0, arrayOf(NECKLACE_MOULD, GOLD_BAR), 1654, 68, 1666),
    SAPP_NECK(22, 55.0, arrayOf(NECKLACE_MOULD, GOLD_BAR, SAPPHIRE), 1656, 70, 1666),
    EMER_NECK(29, 60.0, arrayOf(NECKLACE_MOULD, GOLD_BAR, EMERALD), 1658, 72, 1666),
    RUBY_NECK(40, 75.0, arrayOf(NECKLACE_MOULD, GOLD_BAR, RUBY), 1660, 74, 1666),
    DIAM_NECK(56, 90.0, arrayOf(NECKLACE_MOULD, GOLD_BAR, DIAMOND), 1662, 76, 1666),
    DRAG_NECK(72, 105.0, arrayOf(NECKLACE_MOULD, GOLD_BAR, DRAGONSTONE), 1664, 78, 1666),
    ONYX_NECK(82, 120.0, arrayOf(NECKLACE_MOULD, GOLD_BAR, ONYX), 6577, 80, 6565),

    GOLD_AMMY(8, 30.0, arrayOf(AMULET_MOULD, GOLD_BAR), 1673, 53, 1685),
    SAPP_AMMY(24, 65.0, arrayOf(AMULET_MOULD, GOLD_BAR, SAPPHIRE), 1675, 55, 1685),
    EMER_AMMY(31, 70.0, arrayOf(AMULET_MOULD, GOLD_BAR, EMERALD), 1677, 57, 1685),
    RUBY_AMMY(50, 85.0, arrayOf(AMULET_MOULD, GOLD_BAR, RUBY), 1679, 59, 1685),
    DIAM_AMMY(70, 100.0, arrayOf(AMULET_MOULD, GOLD_BAR, DIAMOND), 1681, 61, 1685),
    DRAG_AMMY(80, 150.0, arrayOf(AMULET_MOULD, GOLD_BAR, DRAGONSTONE), 1683, 63, 1685),
    ONYX_AMMY(90, 165.0, arrayOf(AMULET_MOULD, GOLD_BAR, ONYX), 6579, 65, 6566),

    GOLD_BRACE(7, 25.0, arrayOf(BRACELET_MOULD, GOLD_BAR), 11069, 33, 11067),
    SAPP_BRACE(23, 60.0, arrayOf(BRACELET_MOULD, GOLD_BAR, SAPPHIRE), 11072, 35, 11067),
    EMER_BRACE(30, 65.0, arrayOf(BRACELET_MOULD, GOLD_BAR, EMERALD), 11076, 37, 11067),
    RUBY_BRACE(42, 80.0, arrayOf(BRACELET_MOULD, GOLD_BAR, RUBY), 11085, 39, 11067),
    DIAM_BRACE(58, 95.0, arrayOf(BRACELET_MOULD, GOLD_BAR, DIAMOND), 11092, 41, 11067),
    DRAG_BRACE(74, 110.0, arrayOf(BRACELET_MOULD, GOLD_BAR, DRAGONSTONE), 11115, 43, 11067),
    ONYX_BRACE(84, 125.0, arrayOf(BRACELET_MOULD, GOLD_BAR, ONYX), 11130, 45, 11067);

    companion object {
        private val rings = values().associateBy { it.buttonId }

        fun forId(buttonId: Int) = rings[buttonId]
    }

    fun getItemsRequired() = itemsRequired.sliceArray(1 until itemsRequired.size).map { Item(it, 1) }

    fun getMouldRequired() = itemsRequired[0]
}

@ServerStartupEvent
fun mapJewelryCrafting() {
    fun getNumberToMake(packetId: ClientPacket): Int {
        return when(packetId) {
            ClientPacket.IF_OP1 -> 1
            ClientPacket.IF_OP2 -> 5
            ClientPacket.IF_OP3 -> 28
            ClientPacket.IF_OP4 -> -5
            else -> 1
        }
    }

    onButtonClick(446) { e ->
        val bling = Bling.forId(e.componentId) ?: return@onButtonClick
        if (bling == Bling.SLAYER_RING && !e.player.hasCraftROS()) {
            e.player.sendMessage("You have not unlocked the ability to craft this. Purchase the ability from a slayer master.")
            return@onButtonClick
        }
        when(val numberToMake = getNumberToMake(e.packet)) {
            -5 -> e.player.sendInputInteger("How many would you like to make?") { number ->
                e.player.actionManager.setAction(JewelryAction(bling, number, e.player.tempAttribs.getB("immenseHeatCrafting")))
            }
            else -> e.player.actionManager.setAction(JewelryAction(bling, numberToMake, e.player.tempAttribs.getB("immenseHeatCrafting")))
        }
        e.player.closeInterfaces()
    }
}

fun openInterface(player: Player, pyrefiend: Boolean) {
    player.interfaceManager.sendInterface(446)

    //Unhide sections
    listOf(17, 21, 26, 30).forEach { component -> player.packets.setIFHidden(446, component, true) }

    for (bling in Bling.entries) {
        if (!player.inventory.containsItem(bling.getMouldRequired(), 1) || !player.inventory.containsItems(bling.getItemsRequired()) || player.skills.getLevel(Constants.CRAFTING) < bling.levelRequired)
            player.packets.setIFItem(446, bling.buttonId - 1, bling.nullId, 75)
        else
            player.packets.setIFItem(446, bling.buttonId - 1, bling.product, 75)
    }

    if (pyrefiend) {
        player.tempAttribs.setB("immenseHeatCrafting", true)
        player.setCloseInterfacesEvent { player.tempAttribs.removeB("immenseHeatCrafting") }
    }
}
