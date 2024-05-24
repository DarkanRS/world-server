package com.rs.game.content.world.areas.combat_training_camp.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class GuardsWithCrossbow(player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (npc.id) {

                345 -> {
                    player(CALM_TALK, "Hello.")
                    npc(npc, CALM_TALK, "Well hello brave warrior. These ogres have been terrorising the area, they've eaten four children this week alone.")
                    player(AMAZED_MILD, "Brutes!")
                    npc(npc, CALM_TALK, "So we decided to use them for target practice. A fair punishment.")
                    player(CALM_TALK, "Indeed.")
                }

                346 -> {
                    player(CALM_TALK, "Hello.")
                    npc(npc, CALM_TALK, "Hello soldier.")
                    player(CALM_TALK, "I'm more of an adventurer really.")
                    npc(npc, CALM_TALK, "In this day and age we're all soldiers. No time to waste gassing. Fight! Fight! Fight!")
                }

            }
        }
    }
}


@ServerStartupEvent
fun mapGuardsWithCrossbow() {
    onNpcClick(345, 346, options = arrayOf("Talk-to")) { (player, npc) -> GuardsWithCrossbow(player, npc) }
}
