package com.rs.game.content.quests.fairytales.fairytalept1

import com.rs.engine.quest.Quest
import com.rs.engine.quest.QuestHandler
import com.rs.engine.quest.QuestOutline
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.plugin.annotations.ServerStartupEvent

const val STAGE_UNSTARTED = 0

const val STAGE_COMPLETE = 10

@QuestHandler(
    quest = Quest.FAIRY_TALE_I_GROWING_PAINS,
    startText = "Talk to Martin the Master Gardener in Draynor Village.",
    itemsText = "Dramen staff, secateurs, and three or four items randomly assigned to each player.",
    combatText = "Your farming and combat levels will help defeat a level 68 tanglefoot.",
    rewardsText =
            "3,500 Farming XP<br>" +
            "2,000 Attack XP<br>" +
            "1,000 Magic XP<br>" +
            "Magic Secateurs",
    completedStage = STAGE_COMPLETE
)
class FairyTalePt1 : QuestOutline() {
    override fun getJournalLines(player: Player, stage: Int) = when (stage) {
        STAGE_UNSTARTED -> listOf("I should talk to Martin the Master Gardener in Draynor Village.")

        STAGE_COMPLETE -> listOf("QUEST COMPLETE!")
        else -> listOf("Invalid quest stage. Report this to an administrator.")
    }

    override fun complete(player: Player) {
        player.skills.addXpQuest(Skills.FARMING, 3500.0)
        player.skills.addXpQuest(Skills.ATTACK, 2000.0)
        player.skills.addXpQuest(Skills.MAGIC, 1000.0)
        sendQuestCompleteInterface(player, 7410)
    }
}

@ServerStartupEvent
fun mapFairyTaleP1() {

}