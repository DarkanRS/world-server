package com.rs.game.content.miniquests.witches_potion

import com.rs.engine.dialogue.startConversation
import com.rs.engine.miniquest.Miniquest
import com.rs.engine.miniquest.MiniquestHandler
import com.rs.engine.miniquest.MiniquestOutline
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onLogin
import com.rs.plugin.kts.onNpcClick
import com.rs.plugin.kts.onObjectClick

@MiniquestHandler(
    miniquest = Miniquest.WITCHES_POTION,
    startText = "Hetty in her house in Rimmington.",
    itemsText = "None.",
    combatText = "Level 1 Rat",
    rewardsText = "325 Magic XP",
    completedStage = WitchesPotion.COMPLETED
)

class WitchesPotion : MiniquestOutline() {

    companion object {
        const val ONION_ID = 1957
        const val RATS_TAIL_ID = 300
        const val EYE_OF_NEWT_ID = 221
        const val BURNT_MEAT_ID = 2146

        const val NOT_STARTED = 0
        const val NEED_INGREDIENTS = 1
        const val HANDED_IN_INGREDIENTS = 2
        const val COMPLETED = 3
    }

    override fun getJournalLines(p: Player, stage: Int): List<String> {
        val lines = ArrayList<String>()
        when (stage) {
            NOT_STARTED -> {
                lines.add("I can start this miniquest by talking to Hetty in")
                lines.add("her house in Rimmington.")
            }

            NEED_INGREDIENTS -> {
                lines.add("Hetty has said that in order for her to make")
                lines.add("me a potion to help bring out my darker")
                lines.add("self, I must bring her the following ingredients:")
                lines.add("")
                lines.add("1 rat's tail")
                lines.add("1 burnt meat")
                lines.add("1 eye of newt")
                lines.add("1 onion")
            }

            HANDED_IN_INGREDIENTS -> {
                lines.add("I have given Hetty all the ingredients she asked for,")
                lines.add("and she has told me to drink from her cauldron.")
                lines.add("")
                lines.add("If I really want to bring out my darker self")
                lines.add("I should give this potion a try.")
                lines.add("")
                lines.add("What's the worst that could happen?")
            }

            COMPLETED -> {
                lines.add("")
                lines.add("")
                lines.add("MINIQUEST COMPLETE!")
            }

            else -> lines.add("Invalid miniquest stage. Report this to an administrator.")
        }
        return lines
    }

    override fun complete(p: Player) {
        p.skills.addXpQuest(Skills.MAGIC, 325.0)
        sendQuestCompleteInterface(p, RATS_TAIL_ID)
    }

    override fun updateStage(p: Player) {
        p.vars.setVar(67, if (p.getMiniquestStage(Miniquest.WITCHES_POTION) == HANDED_IN_INGREDIENTS) 2 else 3)
    }
}

@ServerStartupEvent
fun mapWitchesPotion() {
    onNpcClick(307) { (p, npc) ->
        HettyD(p, npc)
    }
    onObjectClick(2024) { (p, obj) ->
        p.startConversation {
            simple("You drink from the cauldron. It tastes horrible!<br>You feel yourself imbued with power.")
            exec { p.miniquestManager.complete(Miniquest.WITCHES_POTION) }
        }
    }
    onLogin { (p) -> p.vars.setVar(67, if (p.getMiniquestStage(Miniquest.WITCHES_POTION) == WitchesPotion.HANDED_IN_INGREDIENTS) 2 else 3) }
}
