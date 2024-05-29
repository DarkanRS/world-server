package com.rs.game.content.quests.death_plateau

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.engine.quest.QuestHandler
import com.rs.engine.quest.QuestOutline
import com.rs.game.content.achievements.Achievement
import com.rs.game.content.quests.death_plateau.utils.*
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.*

@QuestHandler(
    quest = Quest.DEATH_PLATEAU,
    startText = "Speak to Denulth, commander of the Guard at the Burthorpe training ground.",
    itemsText = "A pickaxe.",
    combatText = "The ability to beat a level 5 Troll.",
    rewardsText = "3 x 100-xp reward lamps<br>" +
            "I can speak to Denulth for a repeatable task.",
    completedStage = STAGE_COMPLETE
)

class DeathPlateau : QuestOutline() {
    override fun getJournalLines(player: Player, stage: Int) = when (stage) {
        STAGE_UNSTARTED -> listOf("To start this quest, I should talk to Commander Denulth at the Burthorpe training camp.")

        STAGE_SPEAK_TO_SABBOT -> listOf("I have agreed to scout Death Plateau to try and find a new passage for the Imperial Guard.",
            "I have been told to speak to Sabbot, a dwarf that lives in a cave at the foot of the plateau as my first point of contact.",
            "His cave is under Death Plateau. To get to it I must travel to the west of Burthorpe castle, past the defensive wall. Once I am past that point I need to go north and look for the cave set into the wall of the cliff.")
        STAGE_SPEAK_TO_FREDA -> listOf("Sabbot the dwarf thinks there could be an underground route to the plateau. He has told me to speak to his wife, Freda, to get a copy of the geological survey they were working on when the trolls attacked.",
            "She lives in a small house on a path to the west of Death Plateau.")
        STAGE_TAKE_BOOTS_DUNSTAN -> listOf("Freda has agreed to let me have a copy of her geological survey. While she makes it for me, I have promised to run an errand. " +
                "I need to deliver her boots to Dunstan the smith at his house east of the castle in Burthorpe for repairs.")
        STAGE_RETURN_TO_FREDA -> listOf("Dunstan has given me the boots to return to Freda. By the time I get back she should have finished with the copy of the geological survey I need.")
        STAGE_RECEIVED_SURVEY -> {
            listOf("I have a copy of the geological survey and should head to Sabbot's cave to find a good spot to dig.",
                if (!player.inventory.containsOneItem(SURVEY) && player.getQuestStage(Quest.DEATH_PLATEAU) == STAGE_RECEIVED_SURVEY) "I have lost the survey. I should talk to Freda to see if she has any more copies of it." else "" )
        }
        STAGE_READ_SURVEY -> {
            if (player.containsTool(BRONZE_PICKAXE)) listOf("I have discovered the right area to dig inside Sabbot's cave. I have a pickaxe on my tool belt to clear the rocks covering the passageway.")
            else listOf("I have discovered the right area to dig inside Sabbot's cave.")
        }
        STAGE_MINED_TUNNEL -> listOf("I have opened up the passageway in Sabbot's cave, and I should explore it to the end to discover if it leads up to Death Plateau.")
        STAGE_FOUND_TROLL -> listOf("I have explored the passage, and it does appear to give a commanding view of Death Plateau. However, there is a troll there, and I should look into dealing with them before I report back.")
        STAGE_ANGERED_TROLL -> listOf("I have angered the troll at the end of the passage to Death Plateau. I need to kill him, and quickly, before they alert any other trolls in the area.")
        STAGE_KILLED_THE_MAP -> listOf("I have defeated the troll at the end of the passage to Death Plateau. I should head back through the tunnel, teleport or some other means and report my findings to Denulth.")
        STAGE_COMPLETE -> listOf("QUEST COMPLETE!")
        else -> listOf("Invalid quest stage. Report this to an administrator.")
    }

    override fun updateStage(player: Player, stage: Int) {
        setDeathPlateauVarBits(player)
    }

    override fun complete(player: Player) {
        player.interfaceManager.sendAchievementComplete(Achievement.DEATH_PLATEAU_441)
        player.packets.setIFGraphic(1244, 18, 9570)
        sendQuestCompleteInterface(player, SURVEY)
        player.inventory.addItem(QUEST_REWARD_LAMP, 3)
    }
}

@ServerStartupEvent
fun mapDeathPlateauInteractions() {

    onItemClick(SURVEY, options = arrayOf("Read")) { (player, _) -> DeathPlateauUtils(player).handleSurvey(true) }

    onItemOnNpc(SABBOT, FREDA, AMBUSH_COMMANDER) { (player, item, npc) ->
        when (npc.id) {
            SABBOT -> if (item.id == CLIMBING_BOOTS) player.simpleDialogue("Sabbot has no use for climbing boots, giving them to Freda would be a good idea.") else player.sendMessage("Nothing interesting happens.")
            FREDA -> DeathPlateauUtils(player).handleItemOnFreda(npc)
            AMBUSH_COMMANDER -> {
                if (item.id == SUPPLIES) {
                    player.startConversation {
                        player(CALM_TALK, "Actually, I've come to drop off these supplies!")
                        if (player.inventory.freeSlots >= 2) {
                            npc(npc, CALM_TALK, "Thanks. These supplies will come in handy. Here, have your reward.") { DeathPlateauUtils(player).completeSupplyTask() }
                            if (DeathPlateauUtils(player).deliveredSuppliesAmount < 4) npc(npc, CALM_TALK, "We still need some more supplies though. Please speak to Denulth if you have a chance.")
                        } else {
                            npc(npc, CALM_TALK, "Thanks. But you'll need 2 free inventory slots for your reward.")
                            player(CALM_TALK, "Okay, I'll go clear out my backpack then.")
                        }
                    }
                }
            }
        }
    }

    onItemEquip(CLIMBING_BOOTS, SPIKED_BOOTS) { e ->
        if (e.equip() && e.item.id == CLIMBING_BOOTS) {
            if (e.player.getQuestStage(Quest.DEATH_PLATEAU) != STAGE_COMPLETE) {
                e.player.sendMessage("The sherpa's feet must be very small. I can't get them on.")
                e.cancel()
            }
        }
        if (e.equip() && e.item.id == SPIKED_BOOTS) {
            e.player.sendMessage("Trying to walk in these would be difficult. I'll carry them for now.")
            e.cancel()
        }
    }

    onObjectClick(DEATH_PLATEAU_WARNING) { (player) -> player.interfaceManager.sendInterface(581) }

    onDestroyItem(QUEST_REWARD_LAMP, SUPPLY_REWARD_LAMP) { (player, item) ->
        if (item.id == QUEST_REWARD_LAMP) {
            if (player.getI(DEATH_PLATEAU_QUEST_LAMPS_LOST) < 0) player.save(DEATH_PLATEAU_QUEST_LAMPS_LOST, 1)
            else player.save(DEATH_PLATEAU_QUEST_LAMPS_LOST, (player.getI(DEATH_PLATEAU_QUEST_LAMPS_LOST) + 1))
        } else if (item.id == SUPPLY_REWARD_LAMP) {
            if (player.getI(DEATH_PLATEAU_SUPPLY_LAMPS_LOST) < 0) player.save(DEATH_PLATEAU_SUPPLY_LAMPS_LOST, 1)
            else player.save(DEATH_PLATEAU_SUPPLY_LAMPS_LOST, (player.getI(DEATH_PLATEAU_SUPPLY_LAMPS_LOST) + 1))
        }
    }

    onLogin { (player) ->
        setDeathPlateauVarBits(player)
    }
}

/*
 * Sets various VarBits relating to Death Plateau quest.
 */
fun setDeathPlateauVarBits(player: Player) {
    val deathPlateauStage = player.questManager.getStage(Quest.DEATH_PLATEAU)
    val deliveredSuppliesAmount = player.getI(DEATH_PLATEAU_SUPPLY_TASKS)

    player.vars.apply {
        setVarBit(DEATH_PLATEAU_VB, if (deathPlateauStage in STAGE_SPEAK_TO_SABBOT..STAGE_KILLED_THE_MAP) 5 else if (deathPlateauStage == STAGE_COMPLETE) 65 else 0)
        setVarBit(SABBOT_CAVE_VB, if (deathPlateauStage == STAGE_READ_SURVEY) 1 else if (deathPlateauStage >= STAGE_MINED_TUNNEL) 3 else 0)
        setVarBit(SABBOT_CAVE_POST_QUEST_VB, deliveredSuppliesAmount)
    }
}
