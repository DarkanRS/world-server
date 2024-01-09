package com.rs.game.content.quests.naturespirit

import com.rs.engine.quest.Quest
import com.rs.engine.quest.QuestHandler
import com.rs.engine.quest.QuestOutline
import com.rs.game.model.entity.player.Player

//@QuestHandler(
//    quest = Quest.NATURE_SPIRIT,
//    startText = "Speak to Drezel under the Saradomin temple near the River Salve.",
//    itemsText = "Silver sickle, Ghostspeak amulet",
//    combatText = "You will have to kill a few level 30 ghasts.",
//    rewardsText = """
//        3,000 Crafting XP<br>
//        2,000 Constitution XP<br>
//        2,000 Defence XP<br>
//        Access to Mort Myre swamp and the Altar of Nature<br>
//        30 Prayer XP each time you kill a ghast<br>
//    """,
//    completedStage = 7
//)
class NatureSpirit : QuestOutline() {
    override fun getJournalLines(player: Player, stage: Int) = when (stage) {
        0 -> listOf("I should speak to Drezel under the Saradomin temple!")
        1 -> listOf("")
        2 -> listOf()
        3 -> listOf()
        4 -> listOf()
        5 -> listOf()
        6 -> listOf()
        7 -> listOf("QUEST COMPLETE!")
        else -> listOf("Invalid quest stage. Report this to an administrator.")
    }

    override fun complete(player: Player) {
        TODO("Not yet implemented")
    }
}