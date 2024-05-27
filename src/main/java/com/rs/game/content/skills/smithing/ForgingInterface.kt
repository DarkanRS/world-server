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
package com.rs.game.content.skills.smithing

import com.rs.cache.loaders.ItemDefinitions
import com.rs.cache.loaders.interfaces.IComponentDefinitions
import com.rs.game.content.skills.smithing.ForgingInterface.Slot
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.lib.Constants
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onButtonClick
import java.util.*

@ServerStartupEvent
fun mapForgeButtons() {
    onButtonClick(SMITHING_INTERFACE) { e ->
        val barId = e.player.tempAttribs.getI("SmithingBar")
        val anvil = e.player.tempAttribs.getO<GameObject>("SmithingAnvil")
        val slot = Slot.forId(e.componentId) ?: return@onButtonClick
        val items = Smithing.Smithable.forBar(barId)
        if (items[slot] == null) return@onButtonClick
        val makeX = when (e.componentId - slot.componentId) {
            3 -> 28
            4 -> -1
            5 -> 5
            6 -> 1
            else -> 1
        }
        if (makeX < 0) {
            e.player.sendInputInteger("How many would you like to make?") { amount: Int ->
                e.player.actionManager.setAction(Smithing(amount, items[slot], anvil))
            }
        } else
            e.player.actionManager.setAction(Smithing(makeX, items[slot], anvil))
    }
}

const val SMITHING_INTERFACE = 300

private fun getStrings(player: Player, item: Smithing.Smithable): Array<String> {
    val barName = StringBuilder()
    val levelString = StringBuilder()
    val name: String = item.product.definitions.getName().lowercase(Locale.getDefault())
    val barVariableName: String = item.toString().lowercase(Locale.getDefault())
    if (player.inventory.items.getNumberOf(item.bar.id) >= item.bar.amount) barName.append("<col=00FF00>")
    barName.append(item.bar.amount).append(" ").append(if (item.bar.amount > 1) "bars" else "bar")
    if (player.skills.getLevel(Constants.SMITHING) >= item.level) levelString.append("<col=FFFFFF>")
    levelString.append(Utils.formatPlayerNameForDisplay(name.replace("$barVariableName ", "")))
    return arrayOf(levelString.toString(), barName.toString())
}

fun openSmithingInterfaceForHighestBar(player: Player, obj: GameObject) {
    val bar: Int = Smithing.Smithable.getHighestBar(player)
    if (bar != -1) sendSmithingInterface(player, obj, bar)
    else player.sendMessage("You have no bars which you have smithing level to use.")
}

fun sendSmithingInterface(player: Player, obj: GameObject, barId: Int) {
    player.tempAttribs.setI("SmithingBar", barId)
    player.tempAttribs.setO<Any>("SmithingAnvil", obj)
    val items: Map<Slot, Smithing.Smithable> = Smithing.Smithable.forBar(barId)
    for (slot in Slot.entries) {
        val item = items[slot]
        val componentDef = IComponentDefinitions.getInterface(300)[slot.componentId]

        if (item == null) {
            if (!componentDef.hidden)
                player.packets.setIFHidden(SMITHING_INTERFACE, slot.componentId - 1, true)
            continue
        }

        if (componentDef.hidden)
            player.packets.setIFHidden(SMITHING_INTERFACE, slot.componentId - 1, false)
        player.packets.setIFItem(SMITHING_INTERFACE, slot.componentId, item.product.id, item.product.amount)
        val name = getStrings(player, item)
        player.packets.setIFText(300, slot.componentId + 1, name[0])
        player.packets.setIFText(300, slot.componentId + 2, name[1])
    }
    player.packets.setIFText(300, 14, ItemDefinitions.getDefs(barId).name.replace(" bar", ""))
    player.interfaceManager.sendInterface(SMITHING_INTERFACE)
}

object ForgingInterface {
    enum class Slot(val componentId: Int) {
        DAGGER(18),
        HATCHET(26),
        MACE(34),
        MEDIUM_HELM(42),
        CROSSBOW_BOLTS(50),
        SWORD(58),
        DART_TIPS(66),
        NAILS(74),
        BRONZE_WIRE(82),
        SPIT_IRON(90),
        STUDS(98),
        ARROW_TIPS(106),
        SCIMITAR(114),
        CROSSBOW_LIMBS(122),
        LONGSWORD(130),
        THROWING_KNIFE(138),
        FULL_HELM(146),
        SQUARE_SHIELD(154),
        BULLSEYE_LANTERN(162),
        GRAPPLE_TIP(170),
        WARHAMMER(178),
        BATTLEAXE(186),
        CHAINBODY(194),
        KITESHIELD(202),
        CLAWS(210),
        TWO_HAND_SWORD(218),
        PLATESKIRT(226),
        PLATELEGS(234),
        PLATEBODY(242),
        PICKAXE(267);

        companion object {
            private val CLICK_ID_MAP: MutableMap<Int, Slot> = HashMap()

            init {
                for (s in entries) {
                    CLICK_ID_MAP[s.componentId + 3] = s //make all
                    CLICK_ID_MAP[s.componentId + 4] = s //make x
                    CLICK_ID_MAP[s.componentId + 5] = s //make 5
                    CLICK_ID_MAP[s.componentId + 6] = s //make 1
                }
            }

            fun forId(componentId: Int): Slot? {
                return CLICK_ID_MAP[componentId]
            }
        }
    }
}
