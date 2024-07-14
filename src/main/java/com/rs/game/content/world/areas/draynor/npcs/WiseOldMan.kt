package com.rs.game.content.world.areas.draynor.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class WiseOldMan(player: Player, npc: NPC) {
    init {
        player.startConversation {
            npc(npc, CALM_TALK, "Greetings! What can I do for you?")
            if (player.questManager.completedAllQuests()) {
                player(CONFUSED, "I was wondering if you could sell me a quest cape! I have completed all the quests.")
                npc(npc, LAUGH, "Impressive! I see you have! It will cost you 99,000 coins, though.")
                options {
                    if (player.inventory.hasCoins(99000))
                        op("Yes, I have that with me now.") {
                            player(HAPPY_TALKING, "Yeah I have that with me. Here you go.")
                            if (player.inventory.freeSlots >= 2) {
                                npc(npc, LAUGH, "Wear the cape with pride, adventurer.") {
                                    player.inventory.removeCoins(99000);
                                    player.inventory.addItemDrop(9814, 1);
                                    player.inventory.addItemDrop(9813, 1);
                                }
                            } else {
                                npc(npc, LAUGH, "You'll need at least 2 inventory slots free to take the cape and the hood.")
                                player(SAD, "Good point! Let me empty my backpack first.")
                            }
                        }
                    op("Sorry, nevermind.")
                }
            } else {
                player(CONFUSED, "I'm not sure. What can you do for me?")
                npc(npc, HAPPY_TALKING, "I can offer you a quest cape once you reach maximum quest points.")
            }
        }
    }
}

@ServerStartupEvent
fun mapWiseOldManDraynor() {
    onNpcClick(3820, options = arrayOf("Talk-to")) { (player, npc) -> WiseOldMan(player, npc) }
}
