package com.rs.game.content.quests.eadgars_ruse.dialogues.npcs.taverley

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.content.quests.eadgars_ruse.utils.DIRTY_ROBE
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class TegidD(player: Player, npc: NPC) {
    init {
        val hasRobes = player.inventory.containsOneItem(DIRTY_ROBE) || player.bank.containsItem(DIRTY_ROBE)

        player.startConversation {
            player(CALM_TALK, "So, you're doing laundry, eh?")
            npc(npc, CALM_TALK, "Yeah. What is it to you?")
            player(CALM_TALK, "Nice day for it.")
            npc(npc, CALM_TALK, "Suppose it is.")
            player(CALM_TALK, "You wouldn't be able to spare any of those dirty robes by any chance? It's a matter of the utmost importance.")
            npc(npc, FRUSTRATED, "What? No! These are my robes!")
            options {
                op("Fine.") {
                    player(FRUSTRATED, "Fine.")
                }
                op("Sanfew won't be happy...") {
                    player(FRUSTRATED, "I'm sure Sanfew won't be happy when I tell him it's your fault he can't perform the purification ritual.")
                    if (!hasRobes) {
                        if (player.inventory.hasFreeSlots()) {
                            npc(npc, TALKING_ALOT, "What? Oh well, if it's a matter of that much importance, I suppose you can borrow one...") { player.inventory.addItem(DIRTY_ROBE) }
                        } else {
                            npc(npc, TALKING_ALOT, "What? Oh well, if it's a matter of that much importance, I suppose you can borrow one... But you don't have enough room to take them from me.")
                        }
                    } else {
                        npc(npc, TALKING_ALOT, "You already have my robes!")
                    }
                }
                op("You'll give me those robes right now...") {
                    player(FRUSTRATED, "You'll give me those robes right now...or I'm going to cut down trees until you do.")
                    npc(npc, FRUSTRATED, "You wouldn't dare!")
                    player(CALM_TALK, "Of course I would. They're just trees.")
                    if (!hasRobes) {
                        if (player.inventory.hasFreeSlots()) {
                            npc(npc, FRUSTRATED, "You monster! Take the robes and leave this place!") { player.inventory.addItem(DIRTY_ROBE) }
                        } else {
                            npc(npc, FRUSTRATED, "You don't even have enough room to take my robes, you monster!")
                        }
                    } else {
                        npc(npc, FRUSTRATED, "You already have my robes, you monster!")
                    }
                }
            }
        }
    }
}
