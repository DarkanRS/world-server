package com.rs.game.content.world.areas.dig_site.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class Elissa(player: Player, npc: NPC) {
    init {
        player.startConversation {
            npc(npc, CALM_TALK, "Hello there.")
            options {
                op("What do you do here?") {
                    player(CALM_TALK, "What do you do here?")
                    npc(npc, CALM_TALK, "I'm helping with the dig. I'm an expert on Third Age architecture.")
                }
                op("What is this place?") {
                    player(CALM_TALK, "What is this place?")
                    npc(npc, CALM_TALK, "In the Third Age, this was a great city. Look at these giant walls! They put Varrock to shame!")
                    options {
                        op("I don't know, Varrock is pretty impressive.") {
                            player(CALM_TALK, "I don't know, Varrock palace is impressive.")
                            npc(npc, CALM_TALK, "Hmph. I don't think it will look this good when it's been buried in the ground for three thousand years!")
                        }
                        op("What happened to the city?") {
                            player(CALM_TALK, "What happened to the city?")
                            npc(npc, CALM_TALK, "No one knows for sure.")
                            npc(npc, CALM_TALK, "But the Third Age was a time of destruction, when the gods were violently at war.")
                            npc(npc, CALM_TALK, "Many great civilizations were destroyed then.")
                        }
                    }
                }
            }
        }
    }
}

@ServerStartupEvent
fun mapElissa() {
    onNpcClick(1912) { (player, npc) -> Elissa(player, npc) }
}
