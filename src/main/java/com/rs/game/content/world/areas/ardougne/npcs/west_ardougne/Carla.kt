package com.rs.game.content.world.areas.ardougne.npcs.west_ardougne

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class Carla(player: Player, npc: NPC) {
    init {
        player.startConversation {
            player(CALM_TALK, "Hello.")
            npc(npc, UPSET_SNIFFLE, "Oh, hello.")
            player(CONFUSED, "You seem upset... What's wrong?")
            npc(npc, UPSET, "My son fell ill with the plague...")
            player(SAD, "That's awful, I'm sorry.")
            npc(npc, SAD, "It would be easier to cope with if I could have spent his last few days with him.")
            player(CONFUSED, "Why didn't you?")
            npc(npc, SAD, "Those mourners came and whisked him away. He didn't even seem that ill, I thought it was a common cold... But the mourners said that he was infected and had to be taken away.")
            npc(npc, SAD, "Two days later the mourners returned and told me he had died.")
            player(SAD, "Again, I'm sorry. Life can be harsh.")
            label("initialOps")
            options {
                op("Where did the plague come from?") {
                    player(CONFUSED, "Where did the plague come from?")
                    npc(npc, FRUSTRATED, "It's down to King Tyras. He and his men brought the plague here from the darklands, and then left us to suffer. One day he'll pay for what he's done!")
                    goto("initialOps")
                }
                op("Have there been many victims of the plague?") {
                    player(SKEPTICAL, "Have there been many victims of the plague?")
                    npc(npc, SKEPTICAL_HEAD_SHAKE, "You could say that... I've heard reports that half of West Ardougne is infected! Many have lost friends and family...")
                    player(SAD, "It sounds an awful way to live.")
                    npc(npc, SAD, "People are very depressed and scared. I've never met anyone fully infected though. I suppose we should be grateful to the mourners for that.")
                    goto("initialOps")
                }
                op("I hope things get easier for you.") {
                    player(CALM_TALK, "I hope things get easier for you.")
                    npc(npc, SAD, "Me too...")
                }
            }
        }
    }
}

@ServerStartupEvent
fun mapCarla() {
    onNpcClick(712) { (player, npc) ->
        Carla(player, npc)
    }
}
