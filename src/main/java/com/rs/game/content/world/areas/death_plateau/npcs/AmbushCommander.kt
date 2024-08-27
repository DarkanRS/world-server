package com.rs.game.content.world.areas.death_plateau.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.content.quests.death_plateau.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class AmbushCommander(player: Player, npc: NPC) {
    private val dpu = DeathPlateauUtils(player)

    init {
        player.startConversation {
            player(CALM_TALK, "Hello.")
            npc(npc, CALM_TALK, "Good day. Come to see how things are going?")
            options {
                if (dpu.gotSuppliesInInventory) {
                    op("Actually, I've come to drop off these supplies!") {
                        player(CALM_TALK, "Actually, I've come to drop off these supplies!")
                        if (player.inventory.freeSlots >= 2) {
                            npc(npc, CALM_TALK, "Thanks. These supplies will come in handy. Here, have your reward.") { dpu.completeSupplyTask() }
                            if (dpu.deliveredSuppliesAmount < 4) npc(npc, CALM_TALK, "We still need some more supplies though. Please speak to Denulth if you have a chance.")
                        } else {
                            npc(npc, CALM_TALK, "Thanks. But you'll need 2 free inventory slots for your reward.")
                        }
                    }
                } else {
                    op("Has there been any activity from the trolls?") {
                        player(CALM_TALK, "Has there been any activity from the trolls?")
                        npc(npc, CALM_TALK, "Not a lot. I think they are up to something, but we'll be ready for them.")
                        if (dpu.deliveredSuppliesAmount < 5) npc(npc, CALM_TALK, "We still need some more supplies though. Please speak to Denulth if you have a chance.")
                    }
                }
                if (dpu.lampsLost > 0)
                    op("Can I have those reward lamps that you owe me?") {
                        if (player.inventory.freeSlots >= dpu.lampsLost) {
                            npc(npc, CALM_TALK, "Of course. Thanks again for the supplies.") { dpu.returnLostSupplyLamps() }
                            player(CALM_TALK, "You're welcome.")
                        } else {
                            npc(npc, CALM_TALK, "Of course. But you'll need ${dpu.lampsLost} spare inventory slots first.")
                            player(CALM_TALK, "Okay, I'll go clear out my backpack then.")
                        }
                    }
            }

        }
    }
}

@ServerStartupEvent
fun mapAmbushCommander() {
    onNpcClick(15104) { (player, npc) -> AmbushCommander(player, npc) }
}
