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
package com.rs.game.content.combat

import com.rs.engine.dialogue.Conversation
import com.rs.engine.dialogue.startConversation
import com.rs.game.content.skills.magic.Rune
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Player
import com.rs.lib.Constants
import com.rs.lib.game.Animation
import com.rs.lib.game.SpotAnim
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.events.ItemClickEvent
import com.rs.plugin.events.ItemOnItemEvent
import com.rs.plugin.handlers.ItemClickHandler
import com.rs.plugin.handlers.ItemOnItemHandler
import com.rs.plugin.kts.onItemClick
import com.rs.plugin.kts.onItemOnItem
import java.util.function.Consumer

@ServerStartupEvent
fun mapPolypore() {
    onItemClick(22494, 22496, 22497, options = arrayOf("Check", "Clean")) { e ->
        when (e.option) {
            "Check" -> {
                val charges = e.item.getMetaDataI("polyporeCasts")
                if (charges == -1) e.player.sendMessage("It looks like its got about 3000 casts left.")
                else e.player.sendMessage("It looks like its got about $charges casts left.")
            }

            "Clean" -> e.player.startConversation {
                simple("WARNING: You will only be able to recover half of the spores and runes that went into charging the staff.")
                options("Do you want to clean the staff?") {
                    opExec("Yes, please.") {
                        val staff = e.player.inventory.getItem(e.slotId)
                        if (staff != null) {
                            val toRecover = if (staff.id == 22494) 3000 else e.item.getMetaDataI("polyporeCasts")
                            if (toRecover > 0) {
                                staff.id = 22498
                                e.player.inventory.addItemDrop(Rune.FIRE.id(), (toRecover * 5) / 2)
                                e.player.inventory.addItemDrop(Rune.CHAOS.id(), toRecover / 2)
                                e.player.inventory.addItemDrop(22448, toRecover / 2)
                                e.player.inventory.refresh(e.slotId)
                            }
                        }
                    }
                    op("No, thanks.")
                }
            }
        }
    }

    onItemOnItem(intArrayOf(22448), intArrayOf(22496, 22497, 22498)) { e ->
        val stick = e.getUsedWith(22448)
        if (stick.id == 22498) {
            var canMake = true
            if (!e.player.inventory.containsItem(22448, 3000)) {
                e.player.sendMessage("You need 3,000 polypore spores to create a polypore staff.")
                canMake = false
            }
            if (!e.player.inventory.containsItem(Rune.FIRE.id(), 15000)) {
                e.player.sendMessage("You need 15,000 fire runes to create a polypore staff.")
                canMake = false
            }
            if (!e.player.inventory.containsItem(Rune.CHAOS.id(), 3000)) {
                e.player.sendMessage("You need 3,000 chaos runes to create a polypore staff.")
                canMake = false
            }
            if (!canMake) return@onItemOnItem

            e.player.startConversation {
                simple("To create a polypore staff you will need 3,000 polypore spores, 3,000 chaos runes and 15,000 fire runes.")
                options("Are you sure you want to create the staff?") {
                    op("Yes, please.") {
                        item(22494, "You plant the spores on the stick and they quickly grow with the power of the fire runes.")
                        exec {
                            val staff = e.player.inventory.getItem(stick.slot)
                            if (staff != null) {
                                e.player.sync(15434, 2032)
                                staff.id = 22494
                                e.player.inventory.deleteItem(Rune.FIRE.id(), 15000)
                                e.player.inventory.deleteItem(Rune.CHAOS.id(), 3000)
                                e.player.inventory.deleteItem(22448, 3000)
                                e.player.inventory.refresh(stick.slot)
                                e.player.skills.addXp(Constants.FARMING, 300.0)
                            }
                        }
                    }
                    op("No, thanks.")
                }
            }
        } else {
            val charges = stick.getMetaDataI("polyporeCasts")
            if (charges == -1 || charges >= 3000) {
                e.player.sendMessage("This polypore staff is already full.")
                return@onItemOnItem
            }
            val canRecharge = 3000 - charges
            val maxRecharge = getMaxCharges(e.player, canRecharge)
            val newCharges = charges + maxRecharge
            if (maxRecharge <= 0) {
                e.player.sendMessage("You need 1 polypore spore, 1 chaos rune, and 5 fire runes per charge.")
                return@onItemOnItem
            }
            e.player.sync(15434, 2032)
            if (newCharges == 3000) {
                stick.id = 22494
                stick.deleteMetaData()
                e.player.inventory.refresh(stick.slot)
            } else stick.addMetaData("polyporeCasts", newCharges)
            e.player.inventory.deleteItem(Rune.FIRE.id(), maxRecharge * 5)
            e.player.inventory.deleteItem(Rune.CHAOS.id(), maxRecharge)
            e.player.inventory.deleteItem(22448, maxRecharge)
            e.player.skills.addXp(Constants.FARMING, maxRecharge * 0.1)
            e.player.sendMessage("You charge the staff with $maxRecharge charges. It now has $newCharges")
        }
    }
}

private fun getMaxCharges(player: Player, currCharges: Int): Int {
    var max = currCharges
    val numSpores = player.inventory.getNumberOf(22448)
    val numFires = player.inventory.getNumberOf(Rune.FIRE.id())
    val numChaos = player.inventory.getNumberOf(Rune.CHAOS.id())
    if (numSpores < max) max = numSpores
    if (numChaos < max) max = numChaos
    if (((numFires / 5.0).toInt()) < max) max = ((numFires / 5.0).toInt())
    if (numChaos < max) max = numChaos
    return max
}

fun isWielding(player: Player): Boolean {
    val weaponId = player.equipment.weaponId
    return weaponId == 22494 || weaponId == 22496 || weaponId == 22497
}

fun drainCharge(player: Player) {
    val staff = player.equipment.getItem(Equipment.WEAPON) ?: return
    if (staff.id == 22494) {
        staff.id = 22496
        staff.addMetaData("polyporeCasts", 2999)
        player.equipment.refresh(Equipment.WEAPON)
    } else if (staff.id == 22496) {
        val charges = staff.getMetaDataI("polyporeCasts")
        if (charges <= 1) {
            staff.deleteMetaData()
            staff.id = 22498
            player.equipment.refresh(Equipment.WEAPON)
            player.appearance.generateAppearanceData()
            player.sendMessage("<col=FF0000>Your polypore staff has degraded back into a polypore stick!")
        } else staff.addMetaData("polyporeCasts", charges - 1)
    }
}
