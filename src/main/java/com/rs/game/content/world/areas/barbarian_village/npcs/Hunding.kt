package com.rs.game.content.world.areas.barbarian_village.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

@ServerStartupEvent
fun mapHundingTalk() {
    onNpcClick(3254) { (player, npc) ->
        player.startConversation {
            npc(npc, FRUSTRATED, "What business do you have in our village, outlander?")
            options {
                op("I'm just exploring.") {
                    player(CALM_TALK, "I'm just exploring.")
                    npc(npc, FRUSTRATED, "What you have found is a powerful tribe to make your soft people tremble.")
                }
                op("I'm looking for a fight!") {
                    player(FRUSTRATED, "I'm looking for a fight!")
                    npc(npc, FRUSTRATED, "Go down to the longhouse and you'll find all the fighting you can handle, outlander. Haakon will give you a rousing welcome!")

                }
                op("Tell me about your people.") {
                    player(CALM_TALK, "Tell me about your people.")
                    npc(npc, FRUSTRATED, "We are the Fremennik. We came from the west a century ago in righteous war to purge the heresy of magic from this decadent land. Now we await the call to march to war once more! We are always ready for that day!")
                }
                op("You look like primitive savages.") {
                    player(CALM_TALK, "You look like primitive savages.")
                    npc(npc, FRUSTRATED, "You look like an arrogant fool. Which of us is the primitive?")
                }
                op("Goodbye.") {
                    player(CALM_TALK, "Goodbye.")
                }
            }
        }
    }
}
