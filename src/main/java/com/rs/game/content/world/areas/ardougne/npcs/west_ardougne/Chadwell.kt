package com.rs.game.content.world.areas.ardougne.npcs.west_ardougne

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick
import com.rs.utils.shop.ShopsHandler

class Chadwell(player: Player, npc: NPC) {
    init {
        player.startConversation {
            player(CALM_TALK, "Hello there.")
            npc(npc, CALM_TALK, "Good day. What can I get you?")
            options {
                op("Let's see what you've got.") { exec { ShopsHandler.openShop(player, "west_ardougne_general_store") } }
                op("Nothing thanks.") {
                    player(CALM_TALK, "Nothing thanks.")
                    npc(npc, CALM_TALK, "Ok then.")
                }
            }
        }
    }
}

@ServerStartupEvent
fun mapChadwell() {
    onNpcClick(971, options = arrayOf("Talk-to")) { (player, npc) ->
        Chadwell(player, npc)
    }
}
