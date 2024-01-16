package com.rs.game.content.world.areas.morytania.npcs

import com.rs.game.content.quests.naturespirit.drezelNatureSpiritOptions
import com.rs.game.content.quests.priestinperil.DrezelMausoleumD
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick
import com.rs.plugin.kts.overrideNpcLOS

const val DREZEL = 1049

@ServerStartupEvent
fun mapDrezel() {
    overrideNpcLOS(DREZEL)
    onNpcClick(DREZEL, options = arrayOf("Talk-to")) { e ->
        e.player.apply {
            sendOptionDialogue {
                it.add("Talk about Priest in Peril") {
                    startConversation(DrezelMausoleumD(this))
                }

                drezelNatureSpiritOptions(this, e.npc, it)
            }
        }
    }
}