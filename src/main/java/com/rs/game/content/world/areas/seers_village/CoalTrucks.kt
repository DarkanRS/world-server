package com.rs.game.content.world.areas.seers_village

import com.rs.game.content.achievements.AchievementDef
import com.rs.game.content.achievements.SetReward
import com.rs.game.content.world.areas.seers_village.npcs.StankersD
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemOnObject
import com.rs.plugin.kts.onLogin
import com.rs.plugin.kts.onNpcClick
import com.rs.plugin.kts.onObjectClick

const val STANKERS_ID = 383
const val COAL_ITEM_ID = 453
const val PLAYER_COAL_TRUCK_VAR = 74
const val DEFAULT_MAX_COAL_STORAGE = 120

val MAX_COAL_STORAGE_MAP = mapOf(
    "Easy" to 140,
    "Medium" to 168,
    "Hard" to 196
)

@ServerStartupEvent
fun handleCoalTrucks() {
    onNpcClick(STANKERS_ID, options = arrayOf("Talk-to")) { e ->
        e.player.apply {
            e.player.startConversation(StankersD(this, e.npc))
        }
    }

    onItemOnObject(arrayOf("Coal Truck"), arrayOf(COAL_ITEM_ID)) { e ->
        e.apply {
            val maxCoalStorage = getMaxCoalStorage(player)
            val currentCoalInTruck = player.vars.getVar(PLAYER_COAL_TRUCK_VAR)
            val coalToAdd = minOf(player.inventory.getAmountOf(COAL_ITEM_ID), maxCoalStorage - currentCoalInTruck)

            if (coalToAdd <= 0) {
                player.simpleDialogue("The coal truck is too full to hold any more coal.")
                return@apply
            }

            player.inventory.removeItems(Item(COAL_ITEM_ID, coalToAdd))
            player.vars.saveVar(PLAYER_COAL_TRUCK_VAR, currentCoalInTruck + coalToAdd)
            player.sendMessage("You add some coal to the coal truck.")

            if (currentCoalInTruck + coalToAdd == maxCoalStorage) {
                player.simpleDialogue("The coal truck is now full.")
                return@apply
            }
            return@apply
        }
    }

    onObjectClick("Coal Truck") { (player, _, option) ->
        when (option) {
            "Investigate" -> {
                val maxCoalStorage = getMaxCoalStorage(player)
                val spaceLeft = maxCoalStorage - player.vars.getVar(PLAYER_COAL_TRUCK_VAR)
                val message = if (spaceLeft > 0) {
                    "The coal truck has space for $spaceLeft more coal."
                } else {
                    "The coal truck doesn't have space for any more coal."
                }
                player.simpleDialogue("There is currently ${player.vars.getVar(PLAYER_COAL_TRUCK_VAR)} coal in the coal truck.<br>$message")
            }
            "Remove-coal" -> {
                val currentCoalInTruck = player.vars.getVar(PLAYER_COAL_TRUCK_VAR)
                if (currentCoalInTruck <= 0) {
                    player.simpleDialogue("The coal truck is empty.")
                    return@onObjectClick
                }

                val coalToRemove = minOf(player.inventory.freeSlots, currentCoalInTruck)
                if (player.inventory.addItem(COAL_ITEM_ID, coalToRemove)) {
                    player.vars.saveVar(PLAYER_COAL_TRUCK_VAR, currentCoalInTruck - coalToRemove)
                    player.sendMessage("You remove some coal from the coal truck.")
                }
            }
        }
    }

    onLogin { (player) ->
        player.vars.getVar(PLAYER_COAL_TRUCK_VAR)
    }
}

private fun getMaxCoalStorage(player: Player): Int {
    val seersHeadbandRequirements = getSeersHeadbandRequirements(player)
    return MAX_COAL_STORAGE_MAP.getOrElse(seersHeadbandRequirements) { DEFAULT_MAX_COAL_STORAGE }
}

private fun getSeersHeadbandRequirements(player: Player): String {
    val area = AchievementDef.Area.SEERS
    val easyRequirementMet = SetReward.SEERS_HEADBAND.hasRequirements(player, area, AchievementDef.Difficulty.EASY, false)
    val mediumRequirementMet = SetReward.SEERS_HEADBAND.hasRequirements(player, area, AchievementDef.Difficulty.MEDIUM, false)
    val hardRequirementMet = SetReward.SEERS_HEADBAND.hasRequirements(player, area, AchievementDef.Difficulty.HARD, false)

    return when {
        hardRequirementMet -> "Hard"
        mediumRequirementMet -> "Medium"
        easyRequirementMet -> "Easy"
        else -> "None"
    }
}