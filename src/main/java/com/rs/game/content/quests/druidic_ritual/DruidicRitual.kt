package com.rs.game.content.quests.druidic_ritual

import com.rs.engine.quest.Quest
import com.rs.engine.quest.QuestHandler
import com.rs.engine.quest.QuestOutline
import com.rs.game.content.achievements.Achievement
import com.rs.game.content.quests.druidic_ritual.utils.*
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemOnObject

@QuestHandler(
    quest = Quest.DRUIDIC_RITUAL,
    startText = "Speak to Kaqemeex at the stone circle north of Taverley.",
    itemsText = "Raw bear meat<br>Raw rat meat<br>Raw beef<br>Raw chicken",
    combatText = "Ability to survive some skeletons in the Taverley Dungeon",
    rewardsText = "250 Herblore XP<br>15 Grimy Guam<br>15 Eye of Newt<br>",
    completedStage = STAGE_COMPLETE
)

class DruidicRitual : QuestOutline() {
    override fun getJournalLines(player: Player, stage: Int) = when (stage) {
        STAGE_UNSTARTED -> listOf("I can start this quest by speaking to Kaqemeex at the stone circle north of Taverley.")

        STAGE_SPEAK_TO_SANFEW -> listOf("I must speak to Sanfew, south of the stone circle, in Taverley.")

        STAGE_GATHER_MEATS -> listOf("Sanfew told me to get raw rat, chicken, beef and bear meat.",
            "I'm to take them to the Cauldron of Thunder and dip them in.",
            "Afterwards, I should take all four meats to Sanfew.",
            "",
            "I can find the Cauldron of Thunder in the Taverley Dungeon. It is at the first gate north east of the dungeon entrance.")

        STAGE_RETURN_TO_KAQEMEEX -> listOf("I have given Sanfew the enchanted meats. I should return to Kaqemeex at the stone circle north of Taverley.")

        STAGE_COMPLETE -> listOf("QUEST COMPLETE!")
        else -> listOf("Invalid quest stage. Report this to an administrator.")
    }

    override fun updateStage(player: Player, stage: Int) {}

    override fun complete(player: Player) {
        player.interfaceManager.sendAchievementComplete(Achievement.DRUIDIC_RITUAL_440)
        player.packets.setIFGraphic(1244, 18, 4432)
        sendQuestCompleteInterface(player, ANTIPOISON)
        player.skills.addXpQuest(Skills.HERBLORE, 250.0)
        player.inventory.addItem(GRIMY_GAUM, 15)
        player.inventory.addItem(EYE_OF_NEWT, 15)
    }
}

@ServerStartupEvent
fun mapDruidicRitualInteractions() {
    onItemOnObject(objectNamesOrIds = arrayOf(CAULDRON_OF_THUNDER), itemNamesOrIds = arrayOf(RAW_BEEF, RAW_CHICKEN, RAW_BEAR_MEAT, RAW_RAT_MEAT)) { (player, _, item) ->
        val rawToEnchantedMap = mapOf(
            RAW_BEEF to ENCHANTED_RAW_BEEF,
            RAW_CHICKEN to ENCHANTED_RAW_CHICKEN,
            RAW_BEAR_MEAT to ENCHANTED_RAW_BEAR_MEAT,
            RAW_RAT_MEAT to ENCHANTED_RAW_RAT_MEAT)

        rawToEnchantedMap[item.id]?.let { enchantedItemId -> DruidicRitualUtils(player).handleEnchantedMeat(item.id, enchantedItemId) }
    }
}
