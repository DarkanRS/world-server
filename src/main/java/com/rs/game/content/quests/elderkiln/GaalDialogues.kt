package com.rs.game.content.quests.elderkiln

import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

const val GAAL_JEH = 15137

@ServerStartupEvent
fun mapGaalJeh() {
    onNpcClick(GAAL_JEH) { (player) -> gaalJehDialogue(player) }
}

fun gaalJehDialogue(player: Player) {
    player.startConversation {
        when(player.getQuestStage(Quest.ELDER_KILN)) {
            STAGE_SAVE_GAAL_FIGHTPITS -> {
                npc(GAAL_JEH, HeadE.T_CONFUSED, "Guh?")
                options {
                    ops("Come with me. You'll die if you stay here.", "Come with me - now!") {
                        npc(GAAL_JEH, HeadE.T_CONFUSED, "Guh-huh!")
                        simple("Although you can't understand what the Ga'al is trying to say, you get the impression that it is unwilling to leave.")
                    }
                }
            }
        }
    }
}