package com.rs.game.content.items

import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.World
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onDestroyItem
import com.rs.plugin.kts.onItemClick
import com.rs.plugin.kts.onItemOnItem

private val WATER_RUNE = Item(555)
private val REGULAR_WATER_TIARA = Item(5531)
private val ENCHANTED_WATER_TIARA = Item(11969)

@ServerStartupEvent
fun mapEnchantedWaterTiara() {

    // Check Enchanted Water Tiara charges
    onItemClick(ENCHANTED_WATER_TIARA.id, options = arrayOf("Charges", "Check-charges")) { e ->
        val tiara = e.item
        val charges = tiara.getMetaDataI("enchantedWaterTiaraCharges", 0)
        val chargeText = if (charges == 1) "charge" else "charges"
        val formattedCharges = String.format("%,d", charges)
        e.player.interfaceManager.closeChatBoxInterface()
        e.player.sendMessage("Your enchanted water tiara has $formattedCharges $chargeText remaining.")
    }

    // Create Enchanted Water Tiara with the option to add more charges
    onItemOnItem(intArrayOf(REGULAR_WATER_TIARA.id), intArrayOf(WATER_RUNE.id)) { e ->
        val player = e.player

        if (!player.isQuestComplete(Quest.DEALING_WITH_SCABARAS)) {
            player.sendMessage("Nothing interesting happens.")
            return@onItemOnItem
        }
        if (player.inventory.containsOneItem(ENCHANTED_WATER_TIARA.id) || player.bank.containsItem(ENCHANTED_WATER_TIARA.id) || player.equipment.containsOneItem(ENCHANTED_WATER_TIARA.id)) {
            player.sendMessage("You already own an enchanted water tiara.")
            return@onItemOnItem
        }
        if (!player.inventory.containsOneItem(REGULAR_WATER_TIARA.id)) {
            player.sendMessage("You need a regular water tiara to enchant.")
            return@onItemOnItem
        }

        val waterRunesAvailable = player.inventory.getAmountOf(WATER_RUNE.id)
        if (waterRunesAvailable < 3) {
            player.sendMessage("You need at least 3 water runes to enchant the water tiara.")
            return@onItemOnItem
        }

        // Prompt the user to ask how many runes they want to use
        val maxChargesToAdd = Utils.clampI(waterRunesAvailable / 3, 0, 500000)
        val formattedMaxChargesToAdd = String.format("%,d", maxChargesToAdd)
        val chargeTextQuestion = if (maxChargesToAdd == 1) "charge" else "charges"

        player.sendInputInteger("How many charges would you like to add while enchanting the tiara?<br><br>(You have enough water runes for $formattedMaxChargesToAdd $chargeTextQuestion)") { desiredCharges: Int ->
            val chargeText = if (desiredCharges == 1) "charge" else "charges"
            if (desiredCharges < 1) {
                player.simpleDialogue("You can only enchant your water tiara with a minimum of 1 charge.")
            } else if (desiredCharges > maxChargesToAdd) {
                player.simpleDialogue("You don't have enough water runes to add $desiredCharges $chargeText.")
            } else {
                val chargesToAdd = Utils.clampI(desiredCharges, 0, maxChargesToAdd)
                val formattedChargesToAdd = String.format("%,d", chargesToAdd)
                val runesNeeded = chargesToAdd * 3
                val formattedRunesNeeded = String.format("%,d", runesNeeded)

                player.startConversation {
                    options("Enchant your water tiara with $formattedChargesToAdd $chargeText in exchange for $formattedRunesNeeded water runes?") {
                        opExec("Yes.") {
                            if (player.inventory.getAmountOf(WATER_RUNE.id) >= runesNeeded) {
                                player.inventory.deleteItem(WATER_RUNE.id, runesNeeded)
                                player.inventory.deleteItem(REGULAR_WATER_TIARA)

                                val enchantedTiara = Item(ENCHANTED_WATER_TIARA.id)
                                enchantedTiara.addMetaData("enchantedWaterTiaraCharges", chargesToAdd)
                                player.inventory.addItem(enchantedTiara)
                                player.inventory.refresh()

                                player.sendMessage("You enchant your water tiara with $formattedChargesToAdd $chargeText, using $formattedRunesNeeded water runes.")
                            } else {
                                player.sendMessage("You don't have enough water runes to add $formattedChargesToAdd $chargeText to your water tiara.")
                            }
                        }
                        op("No.")
                    }
                }
            }
        }
    }

    // Recharge Enchanted Water Tiara
    onItemOnItem(intArrayOf(ENCHANTED_WATER_TIARA.id), intArrayOf(WATER_RUNE.id)) { e ->
        EnchantedWaterTiara(e.player).rechargeEnchantedWaterTiara()
    }

    // Handle destroying the Enchanted Water Tiara
    onDestroyItem(ENCHANTED_WATER_TIARA.id) { e ->
        if (e.player.inventory.hasFreeSlots()) {
            val tiara = e.item
            val charges = tiara.getMetaDataI("enchantedWaterTiaraCharges", 0)
            val waterRunesToReturn = charges * 3

            if (waterRunesToReturn > 0) {
                e.player.inventory.deleteItem(e.item)
                World.addGroundItem(REGULAR_WATER_TIARA, e.player.tile, e.player)
                World.addGroundItem(Item(WATER_RUNE.id, waterRunesToReturn), e.player.tile, e.player)
                e.player.sendMessage("You destroy your enchanted water tiara, leaving behind a regular water tiara and any remaining water runes it contained.")
            }
        }
    }

}

class EnchantedWaterTiara(val player: Player) {

    fun depleteEnchantedWaterTiara(): Boolean {
        val tiara = player.getItemWithPlayer(ENCHANTED_WATER_TIARA.id)
        if (player.equipment.get(Equipment.HEAD) == tiara) {
            if (tiara != null && tiara.id == ENCHANTED_WATER_TIARA.id) {
                if (!player.isQuestComplete(Quest.DEALING_WITH_SCABARAS)) {
                    val charges = tiara.getMetaDataI("enchantedWaterTiaraCharges", 0)
                    val waterRunesToReturn = charges * 3
                    player.sendMessage("<col=FF0000>You feel your enchanted water tiara deteriorate into a regular water tiara, leaving behind any remaining water runes it contained.</col>")
                    player.equipment.replace(ENCHANTED_WATER_TIARA, REGULAR_WATER_TIARA)
                    if (waterRunesToReturn > 0)
                        World.addGroundItem(Item(WATER_RUNE.id, waterRunesToReturn), player.tile, player)
                    return false
                }
                var charges = tiara.getMetaDataI("enchantedWaterTiaraCharges", 0)
                if (charges == 0) {
                    player.sendMessage("<col=FF0000>Your enchanted water tiara has run out of water runes and has reverted back to a regular water tiara.")
                    player.equipment.replace(ENCHANTED_WATER_TIARA, REGULAR_WATER_TIARA)
                    return false
                }
                charges -= 1
                tiara.addMetaData("enchantedWaterTiaraCharges", charges)
                player.sendMessage("Your enchanted water tiara quenches your thirst.")
                player.anim(829)
                player.soundEffect(2401, false)
                if (charges <= 0) {
                    player.sendMessage("<col=FF0000>Your enchanted water tiara has run out of water runes and has reverted back to a regular water tiara.")
                    player.equipment.replace(ENCHANTED_WATER_TIARA, REGULAR_WATER_TIARA)
                    return true
                }
                return true
            }
        }
        return false
    }

    fun rechargeEnchantedWaterTiara() {
        if (!player.isQuestComplete(Quest.DEALING_WITH_SCABARAS)) {
            player.sendMessage("Nothing interesting happens.")
            return
        }
        val tiara = player.getItemWithPlayer(ENCHANTED_WATER_TIARA.id)
        if (tiara != null) {
            val currentCharges = tiara.getMetaDataI("enchantedWaterTiaraCharges", 0)
            val waterRunesAvailable = player.inventory.getAmountOf(WATER_RUNE.id)
            val maxChargesToAdd = Utils.clampI(waterRunesAvailable / 3, 0, 500000 - currentCharges)
            val formattedMaxChargesToAdd = String.format("%,d", maxChargesToAdd)
            val chargeTextQuestion = if (maxChargesToAdd == 1) "charge" else "charges"

            if (maxChargesToAdd > 0) {
                player.sendInputInteger("How many charges would you like to add?<br><br>" +
                        "(You have enough water runes for $formattedMaxChargesToAdd $chargeTextQuestion)") { desiredCharges: Int ->
                    val chargeText = if (desiredCharges == 1) "charge" else "charges"
                    if (desiredCharges > maxChargesToAdd) {
                        player.simpleDialogue("Your water tiara can only hold $formattedMaxChargesToAdd more $chargeText.")
                    } else if (desiredCharges * 3 > waterRunesAvailable) {
                        player.simpleDialogue("You don't have enough water runes to add that many charges.")
                    } else {
                        val chargesToAdd = Utils.clampI(desiredCharges, 0, maxChargesToAdd)
                        val formattedChargesToAdd = String.format("%,d", chargesToAdd)

                        if (chargesToAdd > 0) {
                            val runesNeeded = chargesToAdd * 3
                            val formattedRunesNeeded = String.format("%,d", runesNeeded)

                            player.startConversation {
                                options("Add $formattedChargesToAdd $chargeText to the water tiara in exchange for $formattedRunesNeeded water runes?") {
                                    opExec("Yes.") {
                                        if (player.inventory.getAmountOf(WATER_RUNE.id) >= runesNeeded) {
                                            player.inventory.deleteItem(WATER_RUNE.id, runesNeeded)
                                            tiara.addMetaData("enchantedWaterTiaraCharges", currentCharges + chargesToAdd)
                                            player.inventory.refresh()
                                            player.sendMessage("You add $formattedRunesNeeded water runes to your enchanted water tiara, adding $chargesToAdd $chargeText.")
                                        } else {
                                            player.sendMessage("You don't have enough water runes to add $formattedChargesToAdd $chargeText to your enchanted water tiara.")
                                        }
                                    }
                                    op("No.")
                                }
                            }
                        } else {
                            player.simpleDialogue("You must input a valid number of charges to add to your enchanted water tiara.")
                        }
                    }
                }
            } else {
                player.sendMessage("You need at least 3 water runes to add charges to your enchanted water tiara.")
            }
        } else {
            player.sendMessage("You need to have an enchanted water tiara to be able to recharge it.")
        }
    }

}
