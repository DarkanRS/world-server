package com.rs.game.content.world.areas.death_plateau.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class Archer(player: Player, npc: NPC) {
    init {
        player.startConversation {
            if (npc.id == 15102) {
                player(CALM_TALK, "How are things going?")
                npc(npc, HAPPY_TALKING, "G'day mate. Things are goin' great.")
                npc(npc, HAPPY_TALKING, "Snipin' at trolls is great work. Out in the open air...")
                player(CALM_TALK, "So you're enjoying yourself?")
                npc(npc, HAPPY_TALKING, "Beats bein' stuck in a castle all day, mate!")
            } else if (npc.id == 15103) {
                player(CALM_TALK, "How are things going?")
                npc(npc, CALM_TALK, "Eh, I can't complain.")
                player(SKEPTICAL_THINKING, "Are you having any trouble with the trolls?")
                npc(npc, SAD, "It's not the trolls that bother me out here. It's the lack of a decent bedroll.")
                npc(npc, SAD, "That and having to come to work every day through that jungle-gym downstairs.")
                npc(npc, CALM_TALK, "Still, it could be worse. I once spent a week trapped in the store room before they got rid of that bloody combination lock!")
            }
        }
    }
}

@ServerStartupEvent
fun mapArcherDeathPlateau() {
    onNpcClick(15102, 15103) { (player, npc) -> Archer(player, npc) }
}
