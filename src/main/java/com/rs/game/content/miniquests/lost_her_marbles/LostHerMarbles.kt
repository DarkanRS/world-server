package com.rs.game.content.miniquests.lost_her_marbles

import com.rs.engine.miniquest.Miniquest
import com.rs.engine.miniquest.MiniquestHandler
import com.rs.engine.miniquest.MiniquestOutline
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.plugin.annotations.ServerStartupEvent

@ServerStartupEvent
fun mapLostHerMarbles() {
}

@MiniquestHandler(
    miniquest = Miniquest.LOST_HER_MARBLES,
    startText = "Speak to Darren Lightfinger in his cellar accessed through a trapdoor north of Lumbridge furnace.",
    itemsText = "None",
    combatText = "None",
    rewardsText = "5,000 Thieving XP.<br>Access to Dodgy Dereks's Dirty Deals store.<br>Access to wall safes.<br>Thieves' guild expansion.",
    completedStage = 5
)

class FromTinyAcorns : MiniquestOutline() {
    override fun getJournalLines(player: Player, stage: Int): List<String> {
        val lines = ArrayList<String>()
        when (stage) {
            0 -> {
                lines.add("I can start this miniquest by speaking to Darren Lightfinger in")
                lines.add("the Lumbridge Thieves' Guild.")
                lines.add("")
            }

            1 -> {
                lines.add("Darren Lightfinger wants me to be on the lookout for valuable flame fragments,")
                lines.add("which I should find while picking pockets around Gielinor.")
                lines.add("")
                lines.add("Robin can advise on their probable locations.")
            }

            5 -> {
                lines.add("I found all thirty-two fragments and was rewarded with 9,800 Thieving XP,")
                lines.add("a share of guild swag, and pointers on my pickpocketing technique.,")
                lines.add("The Thieves' Guild's facilities have been expanded further.")
                lines.add("")
                lines.add("MINIQUEST COMPLETE!")
            }
            else -> lines.add("Invalid quest stage. Report this to an administrator.")
        }
        return lines
    }

    override fun complete(player: Player) {
        player.skills.addXpQuest(Skills.THIEVING, 5000.0)
        sendQuestCompleteInterface(player, 18653)
    }

    override fun updateStage(player: Player) {

    }
}
