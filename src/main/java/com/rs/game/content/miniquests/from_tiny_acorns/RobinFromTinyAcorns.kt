package com.rs.game.content.miniquests.from_tiny_acorns

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class RobinFromTinyAcorns(p: Player, npc: NPC) {
    init {
        p.startConversation {
            options {
                op("Talk about the current caper.") {
                    player(CHEERFUL, "Do you have any advice for me about this Urist fellow?")
                    exec {
                        if (p.inventory.containsItem(18651)) {
                            npc(npc, LAUGH, "Well, since you seem to have bamboozled him handly, I'd have a word with Darren about it.")
                            player(CHEERFUL, "Will do, thanks.")
                        } else {
                            npc(npc, SHAKING_HEAD, "I don't know anything about him, sorry. He's only just set up shop and I've not been to Varrock in a while. You'll probably want to ask someone who sees him on a more regular basis.")
                            player(SKEPTICAL, "Hmm.")
                            npc(npc, SCARED, "Oh, and if you're stealing from his stall, watch out for the guards. They keep a close eye on activity in the marketplace and they're not lenient with shoplifters.")
                            player(SKEPTICAL_THINKING, "Will do, thanks.")
                        }
                    }
                }
            }
        }
    }
}
