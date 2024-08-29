package com.rs.game.content.quests.death_plateau.utils

import com.rs.engine.dialogue.DialogueBuilder
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.death_plateau.setDeathPlateauVarBits
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class DeathPlateauUtils(val player: Player) {

    val gotSuppliesInInventory = player.inventory.containsOneItem(SUPPLIES)
    var deliveredSuppliesAmount = player.getI(DEATH_PLATEAU_SUPPLY_TASKS)
    val lampsLost = player.getI(DEATH_PLATEAU_SUPPLY_LAMPS_LOST)
    val combatLevel = player.skills.combatLevel

    fun handleItemOnFreda(npc: NPC) {
        player.startConversation {
            npc(npc, CALM_TALK, "What's that then?")
            player(CALM_TALK, "I thought you might need this.")
            npc(npc, CALM_TALK, "Not really.")
        }
    }

    fun handleSurvey(closeEventDialogue: Boolean) {
        player.interfaceManager.sendInterface(1242)
        if (player.getQuestStage(Quest.DEATH_PLATEAU) == STAGE_RECEIVED_SURVEY)
            player.setCloseInterfacesEvent {
                player.setCloseInterfacesEvent(null)
                if (closeEventDialogue) player.playerDialogue(CALM_TALK, "I see...I think I know where to dig now!")
                player.setQuestStage(Quest.DEATH_PLATEAU, STAGE_READ_SURVEY)
            }
    }

    fun supplyAndDemand(npc: NPC, dialogue: DialogueBuilder) {

        val requiredCombatLevel = when (deliveredSuppliesAmount) {
            -1, 0 -> 10
            1 -> 12
            2 -> 14
            3 -> 16
            4 -> 18
            else -> 138
        }

        if (combatLevel >= requiredCombatLevel) {
            val gotSupplies = player.inventory.containsOneItem(SUPPLIES) || player.bank.containsItem(SUPPLIES)
            if (gotSupplies) {
                dialogue.npc(npc, CALM_TALK, "Well, if you wouldn't mind delivering those supplies for me, I may have some more to send you off with.")
                dialogue.npc(npc, CALM_TALK, "The Ambush Commander at the ambush point over Death Plateau has a supply of reward lamps that he will give you when he gets that gear.")
                dialogue.goto("initialOps")
            } else {
                dialogue.npc(npc, CALM_TALK, "Well, I suppose that the archers on the overlook could do with some more supplies.")
                if (player.inventory.hasFreeSlots()) {
                    dialogue.simple("You place the heavy crate of supplies in your pack.") { player.inventory.addItem(SUPPLIES) }
                    dialogue.npc(npc, CALM_TALK, "Here, take these supplies up to the Ambush Commander at the ambush point.")
                    dialogue.npc(npc, CALM_TALK, "It has some supplies for the men, such as more arrows and replacement bows, as well as some things they can put together to make the ambush point better equipped.")
                    dialogue.npc(npc, CALM_TALK, "Plus a few luxuries like cakes, and letters from friends and family. That sort of thing.")
                    dialogue.npc(npc, CALM_TALK, "The Ambush Commander has some reward lamps. Once they get this gear he will give you some to help with your training.")
                    dialogue.player(CALM_TALK, "All right, I will drop this off immediately.")
                } else {
                    dialogue.npc(npc, CALM_TALK, "But you don't have room to take any supplies right now. Please come back when you've got room.")
                    dialogue.goto("initialOps")
                }
            }
        } else {
            dialogue.npc(npc, CALM_TALK, "Well, I suppose that the archers on the overlook could do with some more supplies.")
            dialogue.npc(npc, SAD, "I'm sorry, but I am afraid that you need to be a little more skilled in combat before I can entrust you with these vital supplies.")
            dialogue.simple("You need to have a combat level of $requiredCombatLevel before I can entrust you with this mission.")
            dialogue.player(SAD, "Oh, never mind then.")
            dialogue.goto("initialOps")
        }
    }

    fun completeSupplyTask() {
        player.inventory.deleteItem(SUPPLIES, 1)
        player.inventory.addItem(SUPPLY_REWARD_LAMP, 3)
        if (player.getI(DEATH_PLATEAU_SUPPLY_TASKS) < 0) player.set(DEATH_PLATEAU_SUPPLY_TASKS, 1)
        else player.set(DEATH_PLATEAU_SUPPLY_TASKS, (player.getI(DEATH_PLATEAU_SUPPLY_TASKS) + 1))
        setDeathPlateauVarBits(player)
    }

    fun returnLostSupplyLamps() {
        player.inventory.addItem(SUPPLY_REWARD_LAMP, lampsLost)
        val updatedAmount = player.getI(DEATH_PLATEAU_SUPPLY_LAMPS_LOST) - lampsLost
        if (updatedAmount == 0) player.delete(DEATH_PLATEAU_SUPPLY_LAMPS_LOST)
        else player.set(DEATH_PLATEAU_SUPPLY_LAMPS_LOST, updatedAmount)
    }

    fun returnLostQuestLamps(amount: Int) {
        player.inventory.addItem(QUEST_REWARD_LAMP, amount)
        val updatedAmount = player.getI(DEATH_PLATEAU_QUEST_LAMPS_LOST) - amount
        if (updatedAmount == 0) player.delete(DEATH_PLATEAU_QUEST_LAMPS_LOST)
        else player.set(DEATH_PLATEAU_QUEST_LAMPS_LOST, updatedAmount)
    }

}
