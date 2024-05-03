package com.rs.game.content.world.areas.varrock.npcs.east_varrock

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.content.utils.BartenderUtils.buyDrinkOrIngredients
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class BartenderDancingDonkeyInn(player: Player, npc: NPC) {
    init {
        player.startConversation {
            player(CALM_TALK, "Hello.")
            npc(npc, CALM_TALK, "Good day to you, brave adventurer. Can I get you a refreshing beer?")
            options {
                op("Yes please!") {
                    player(CALM_TALK, "Yes please!")
                    label("yesPlease")
                    npc(npc, CALM_TALK, "Ok then, that's two gold coins please.")
                    exec { buyDrinkOrIngredients(player, npc, 2, Item(1917), true) }
                }
                op("No thanks.") {
                    player(CALM_TALK, "No thanks.")
                    label("noThanks")
                    npc(npc, CALM_TALK, "Let me know if you change your mind.")
                }
                op("How much?") {
                    player(CALM_TALK, "How much?")
                    npc(npc, CALM_TALK, "Two gold pieces a pint. So, what do you say?")
                    options {
                        op("Yes please!") {
                            player(CALM_TALK, "Yes please!")
                            goto("yesPlease")
                        }
                        op("No thanks.") {
                            player(CALM_TALK, "No thanks.")
                            goto("noThanks")
                        }
                    }
                }
            }
        }
    }
}

@ServerStartupEvent
fun mapBartenderJollyBoarInn() {
    onNpcClick(732) { (player, npc) -> BartenderDancingDonkeyInn(player, npc) }
}
