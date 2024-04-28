package com.rs.game.content.world.areas.tree_gnome_stronghold.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick
import com.rs.utils.shop.ShopsHandler

class Hudo(player: Player, npc: NPC) {
    init {
        player.startConversation {
            player(HAPPY_TALKING, "Hello there.")
            npc(npc, HAPPY_TALKING, "Hello there, traveller. Would you like some groceries? I have a large selection.")
            options {
                op("No, thank you.")
                op("I'll have a look.") {
                    player(HAPPY_TALKING, "I'll have a look.")
                    npc(npc, HAPPY_TALKING, "Great stuff.")
                    exec { ShopsHandler.openShop(player, "grand_tree_groceries") }
                }
            }
        }
    }
}

@ServerStartupEvent
fun mapHudo() {
    onNpcClick(600, options = arrayOf("Talk-to")) { (player, npc) ->
        Hudo(player, npc)
    }
}
