package com.rs.game.content.world.areas.combat_training_camp.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick
import com.rs.utils.shop.ShopsHandler

class Shopkeeper(player: Player, npc: NPC) {
    init {
        player.startConversation {
            player(CALM_TALK, "Hello.")
            npc(npc, CALM_TALK, "So, are you looking to buy weapons? King Lathas keeps us very well stocked.")
            options {
                op("What do you have?") {
                    player(CALM_TALK, "What do you have?")
                    npc(npc, CALM_TALK, "Take a look.")
                    exec { ShopsHandler.openShop(player, "king_lathas_armoury") }
                }
                op("No thanks.") {
                    player(CALM_TALK, "No thanks.")
                }
            }
        }
    }
}


@ServerStartupEvent
fun mapCombatTrainingCampShopkeeper() {
    onNpcClick(561, options = arrayOf("Talk-to")) { (player, npc) -> Shopkeeper(player, npc) }
}
