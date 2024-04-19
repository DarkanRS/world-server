package com.rs.game.content.quests.elderkiln

import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

const val GAAL_JEH = 15137

@ServerStartupEvent
fun mapGaalJeh() {
    onNpcClick(TZHAAR_MEJ_JEH_BPOOL, TZHAAR_MEJ_AK_BPOOL) { (player) -> gaalJehDialogue(player) }
}

fun gaalJehDialogue(player: Player) {
    player.startConversation {
        when(player.getQuestStage(Quest.ELDER_KILN)) {

        }
    }
}