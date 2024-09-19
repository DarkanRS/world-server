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
package com.rs.game.content.minigames.pest

import com.rs.cache.loaders.ItemDefinitions
import com.rs.engine.dialogue.sendOptionsDialogue
import com.rs.engine.quest.Quest
import com.rs.game.content.combat.CombatMod
import com.rs.game.content.combat.CombatStyle
import com.rs.game.content.combat.onCombatFormulaAdjust
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.lib.Constants
import com.rs.lib.game.Rights
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onButtonClick
import com.rs.plugin.kts.onItemOnNpc
import com.rs.plugin.kts.onNpcClick
import com.rs.utils.DropSets
import com.rs.utils.drop.DropTable
import kotlin.math.floor

private const val INTERFACE = 1011
private val COMPS: IntArray = intArrayOf(15, 196, 208, 220, 232, 244, 256, 268, 280)
private val VOIDS: IntArray = intArrayOf(11665, 11664, 11663, 10611, 8840, 8842, 8841, 19712, 11666)
private val COSTS: IntArray = intArrayOf(200, 200, 200, 250, 250, 150, 250, 150, 10)

private data class Reward(val cost: Int, val give: Runnable)

@ServerStartupEvent
fun mapPestControlRewards() {
    onCombatFormulaAdjust voidEffect@ { player, target, offensiveBonus, combatStyle ->
        if (player !is Player) return@voidEffect CombatMod()
        return@voidEffect when(combatStyle) {
            CombatStyle.MELEE -> if (player.fullVoidEquipped(11665, 11676)) CombatMod(
                accuracyLevel = 1.1,
                strengthLevel = 1.1
            ) else null
            CombatStyle.RANGE -> if (player.fullVoidEquipped(11664, 11675)) CombatMod(
                accuracyLevel = 1.1,
                strengthLevel = 1.1
            ) else null
            CombatStyle.MAGE -> if (player.fullVoidEquipped(11663, 11674)) CombatMod(accuracyLevel = 1.3) else null
            else -> null
        } ?: CombatMod()
    }

    onNpcClick(npcNamesOrIds = arrayOf(12195, "Void Knight"), options = arrayOf("Exchange")) { it.player.openExchangeShop() }

    onNpcClick(11681) { (player) ->
        if (!player.isQuestComplete(Quest.VOID_STARES_BACK, "to buy Korasi's sword")) return@onNpcClick
        player.sendOptionsDialogue("Would you like to buy Korasi's sword for 200,000 coins?") {
            opExec("Yes, that sounds like a fair price.") {
                if (player.inventory.getCoinsAsInt() < 200000) {
                    player.sendMessage("You don't have enough money for that.")
                    return@opExec
                }
                player.inventory.removeCoins(200000)
                player.inventory.addItemDrop(19784, 1)
            }
            op("Nevermind.")
        }
    }

    onItemOnNpc(11681) { (player, item) ->
        if (item.id == 10611 || item.id == 8840 || item.id == 8839) {
            if (!player.isQuestComplete(Quest.VOID_STARES_BACK, "to upgrade void knight armor")) return@onItemOnNpc
            if (player.pestPoints < 100) {
                player.sendMessage("You need 100 Void Knight Commendations to upgrade void knight armor.")
                return@onItemOnNpc
            }
            player.sendOptionsDialogue("Upgrade this piece to elite for 100 commendations?") {
                opExec("Yes, upgrade my armor.") {
                    if (player.pestPoints >= 100) {
                        player.pestPoints = player.pestPoints - 100
                        item.id = if (item.id == 8840) 19786 else 19785
                        player.inventory.refresh(item.slot)
                    }
                }
                op("Nevermind.")
            }
        }
    }

    onButtonClick(1011) { (player, _, componentId) ->
        when (componentId) {
            15, 196, 208, 220, 232, 244, 256, 268, 280 -> {
                if (!player.skills.hasRequirements(Constants.ATTACK, 42, Constants.STRENGTH, 42, Constants.DEFENSE, 42, Constants.HITPOINTS, 42, Constants.RANGE, 42, Constants.MAGIC, 42, Constants.PRAYER, 22)) {
                    player.sendMessage("You need level 42 Attack, Strength, Defence, Constitution, Ranged, Magic, and 22 Prayer in to purchase void equipment.")
                    return@onButtonClick
                }
                var search = -1
                for (i in COMPS.indices)
                    if (COMPS[i] == componentId)
                        search = i
                val slot = search
                player.buy(ItemDefinitions.getDefs(VOIDS[slot]).name, COSTS[slot]) {
                    player.inventory.addItemDrop(VOIDS[slot], 1)
                }
            }

            68, 86, 88 -> player.buySkillXp(Skills.ATTACK, componentId)
            100, 102, 104 -> player.buySkillXp(Skills.STRENGTH, componentId)
            116, 118, 120 -> player.buySkillXp(Skills.DEFENSE, componentId)
            132, 134, 136 -> player.buySkillXp(Skills.HITPOINTS, componentId)
            148, 150, 152 -> player.buySkillXp(Skills.RANGE, componentId)
            164, 166, 168 -> player.buySkillXp(Skills.MAGIC, componentId)
            180, 182, 184 -> player.buySkillXp(Skills.PRAYER, componentId)
            291 -> {
                if (player.skills.getLevelForXp(Constants.HERBLORE) < 25) {
                    player.sendMessage("You need an Herblore level of 25 in order to purchase an herblore pack.")
                    return@onButtonClick
                }
                player.buy("herb pack", 30, Runnable {
                    repeat(5) {
                        val herb = DropTable.calculateDrops(player, DropSets.getDropSet("herb"))
                        if (herb.size <= 0) return@repeat
                        player.inventory.addItemDrop(herb[0].id + 1, 1)
                    }
                })
            }

            302 -> {
                if (player.skills.getLevelForXp(Constants.MINING) < 25) {
                    player.sendMessage("You need an Mining level of 25 in order to purchase a mineral pack.")
                    return@onButtonClick
                }
                player.buy("mineral pack", 15) {
                    player.inventory.addItem(441, Utils.random(20), true)
                    player.inventory.addItem(454, Utils.random(30), true)
                }
            }

            313 -> {
                if (player.skills.getLevelForXp(Constants.FARMING) < 25) {
                    player.sendMessage("You need an Farming level of 25 in order to purchase a seed pack.")
                    return@onButtonClick
                }
                player.buy("seed pack", 15) {
                    repeat(6) {
                        for (rew in DropTable.calculateDrops(player, DropSets.getDropSet("nest_shit_seed")))
                            player.inventory.addItemDrop(rew)
                    }
                }
            }

            324, 326, 328 -> player.sendInputInteger("How many would you like to buy?") { amt ->
                player.buy(Utils.formatNumber(amt) + " spinner charms", amt * 2) { player.inventory.addItemDrop(12166, amt) }
            }

            339, 341, 343 -> player.sendInputInteger("How many would you like to buy?") { amt ->
                player.buy(Utils.formatNumber(amt) + " torcher charms", amt * 2) { player.inventory.addItemDrop(12167, amt) }
            }

            354, 356, 358 -> player.sendInputInteger("How many would you like to buy?") { amt ->
                player.buy(Utils.formatNumber(amt) + " ravager charms", amt * 2) { player.inventory.addItemDrop(12164, amt) }
            }

            369, 371, 373 -> player.sendInputInteger("How many would you like to buy?") { amt ->
                player.buy(Utils.formatNumber(amt) + " shifter charms", amt * 2) { player.inventory.addItemDrop(12165, amt) }
            }

            383 -> player.confirmBuy()
            385 -> {
                player.packets.setIFHidden(INTERFACE, 71, true)
                player.tempAttribs.removeO<Any?>("pcShopBuy")
            }

            20, 73 -> {
                //experience
                player.packets.setIFHidden(INTERFACE, 70, true)
                player.packets.setIFHidden(INTERFACE, 69, true)
            }

            24, 31 -> {
                //consumables
                player.packets.setIFHidden(INTERFACE, 70, false)
                player.packets.setIFHidden(INTERFACE, 69, true)
            }

            29, 75 -> {
                //equipment
                player.packets.setIFHidden(INTERFACE, 70, true)
                player.packets.setIFHidden(INTERFACE, 69, false)
            }

            else -> player.sendMessage("Component: $componentId")
        }
    }
}

private fun Player.fullVoidEquipped(vararg helmid: Int): Boolean {
    var hasDeflector = equipment.shieldId == 19712
    if (equipment.glovesId != 8842) {
        if (!hasDeflector) return false
        hasDeflector = false
    }
    val legsId = equipment.legsId
    val hasLegs = legsId != -1 && (legsId == 8840 || legsId == 19786 || legsId == 19788 || legsId == 19790)
    if (!hasLegs) {
        if (!hasDeflector) return false
        hasDeflector = false
    }
    val torsoId = equipment.chestId
    val hasTorso = torsoId != -1 && (torsoId == 8839 || torsoId == 10611 || torsoId == 19785 || torsoId == 19787 || torsoId == 19789)
    if (!hasTorso && !hasDeflector)
        return false
    val helmId = equipment.hatId
    if (helmId == -1) return false
    var hasHelm = false
    for (id in helmid) if (helmId == id) {
        hasHelm = true
        break
    }
    return hasHelm
}

private fun Player.openExchangeShop() {
    interfaceManager.sendInterface(INTERFACE)
    vars.setVar(1875, 1250)
    refreshPoints()
}

private fun Player.refreshPoints() {
    vars.setVarBit(2086, pestPoints)
}

private fun Player.confirmBuy() {
    packets.setIFHidden(INTERFACE, 71, true)
    val reward = tempAttribs.removeO<Reward?>("pcShopBuy")
    if (reward == null) return
    val currentPoints = pestPoints
    if (!hasRights(Rights.DEVELOPER) && currentPoints - reward.cost < 0) {
        sendMessage("You don't have enough Commendations remaining to complete this exchange.")
        return
    }
    pestPoints = currentPoints - reward.cost
    refreshPoints()
    reward.give.run()
}

private fun Player.buy(reward: String, cost: Int, give: Runnable) {
    packets.setIFText(1011, 380, "Are you sure you wish to exchange $cost Commendations in return for a $reward?")
    packets.setIFHidden(INTERFACE, 71, false)
    tempAttribs.setO<Any?>("pcShopBuy", Reward(cost, give))
}

private fun Player.buySkillXp(skillId: Int, componentId: Int) {
    if (skills.getLevelForXp(skillId) < 25) {
        sendMessage("You need 25 " + Skills.SKILL_NAME[skillId] + " to purchase experience.")
        return
    }
    val multiplier = when (skillId) {
        Skills.PRAYER -> 18.0
        Skills.MAGIC, Skills.RANGE -> 32.0
        else -> 35.0
    }
    val baseXp = (skills.getLevelForXp(skillId) * skills.getLevelForXp(skillId)) / 600.0
    val xp = floor(baseXp * multiplier) * getXpCost(componentId)
    buy("${Utils.formatNumber(xp.toInt())} ${Skills.SKILL_NAME[skillId]} XP", getXpCost(componentId)) {
        skills.addXp(skillId, xp)
    }
}

private fun getXpCost(componentId: Int): Int {
    return when (componentId) {
        68, 100, 116, 132, 148, 164, 180 -> 1
        86, 102, 118, 134, 150, 166, 182 -> 10
        88, 104, 120, 136, 152, 168, 184 -> 100
        else -> 1
    }
}