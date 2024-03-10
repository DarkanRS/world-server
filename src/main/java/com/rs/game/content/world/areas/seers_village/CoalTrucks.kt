package com.rs.game.content.world.areas.seers_village

import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.Options
import com.rs.engine.dialogue.startConversation
import com.rs.game.content.Effect
import com.rs.game.content.achievements.AchievementDef
import com.rs.game.content.achievements.AchievementSystemDialogue
import com.rs.game.content.achievements.SetReward
import com.rs.game.content.dnds.eviltree.KINDLING
import com.rs.game.content.quests.naturespirit.drezelNatureSpiritOptions
import com.rs.game.content.quests.priestinperil.DrezelMausoleumD
import com.rs.game.content.world.areas.morytania.npcs.DREZEL
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.events.NPCClickEvent
import com.rs.plugin.handlers.NPCClickHandler
import com.rs.plugin.kts.onItemOnObject
import com.rs.plugin.kts.onLogin
import com.rs.plugin.kts.onNpcClick
import com.rs.plugin.kts.onObjectClick

const val STANKERS = 383
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

    onNpcClick(STANKERS, options = arrayOf("Talk-to")) { e ->
        e.player.apply {
            e.player.startConversation(
                Dialogue()
                    .addNPC(e.npcId, HeadE.CHEERFUL, "Hello, what can I do for you?")
                    .addOptions(
                        "What would you like to say?"
                    ) { ops: Options ->
                        ops.add(
                            "About the Achievement System...",
                            AchievementSystemDialogue(
                                e.player,
                                e.npcId,
                                SetReward.SEERS_HEADBAND
                            ).start
                        )
                    }
            )
        }
    }

    onItemOnObject(arrayOf("Coal Truck"), arrayOf(COAL_ITEM_ID)) { e ->
        e.apply {
            val maxCoalStorage = getMaxCoalStorage(player)
            val coalToAdd = minOf(player.inventory.getAmountOf(COAL_ITEM_ID), maxCoalStorage - player.coalTruckInventory)

            if (coalToAdd <= 0) {
                player.simpleDialogue("The coal truck is too full to hold any more coal.")
                return@apply
            }

            player.inventory.removeItems(Item(COAL_ITEM_ID, coalToAdd))
            player.coalTruckInventory += coalToAdd
            updateTruckAppearance(player)
            player.sendMessage("You add some coal to the coal truck.")

            if (player.coalTruckInventory == maxCoalStorage) {
                player.simpleDialogue("The coal truck is now full.")
            }
        }
    }

    onObjectClick("Coal Truck") { (player, _, option) ->
        when (option) {
            "Investigate" -> {
                val maxCoalStorage = getMaxCoalStorage(player)
                val spaceLeft = maxCoalStorage - player.coalTruckInventory
                val message = if (spaceLeft > 0) {
                    "The coal truck has space for $spaceLeft more coal."
                } else {
                    "The coal truck doesn't have space for any more coal."
                }
                player.simpleDialogue("There is currently ${player.coalTruckInventory} coal in the coal truck.<br>$message")
            }
            "Remove-coal" -> {
                if (player.coalTruckInventory <= 0) {
                    player.simpleDialogue("The coal truck is empty.")
                    return@onObjectClick
                }

                val coalToRemove = minOf(player.inventory.freeSlots, player.coalTruckInventory)
                if (player.inventory.addItem(COAL_ITEM_ID, coalToRemove)) {
                    player.coalTruckInventory -= coalToRemove
                    updateTruckAppearance(player)
                    player.sendMessage("You remove some coal from the coal truck.")
                }
            }
        }
    }

    onLogin { (player) ->
        updateTruckAppearance(player)
    }
}

fun updateTruckAppearance(player: Player) {
    player.vars.setVar(PLAYER_COAL_TRUCK_VAR, player.coalTruckInventory)
}

fun getMaxCoalStorage(player: Player): Int {
    val seersHeadbandRequirements = getSeersHeadbandRequirements(player)
    return MAX_COAL_STORAGE_MAP.getOrElse(seersHeadbandRequirements) { DEFAULT_MAX_COAL_STORAGE }
}

fun getSeersHeadbandRequirements(player: Player): String {
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