package com.rs.game.content.quests.sheep_herder.dialogues

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.sheep_herder.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class FarmerBrumtyD (player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.SHEEP_HERDER)) {

                STAGE_UNSTARTED -> {
                    player(CALM_TALK, "Hello there.")
                    npc(npc, SAD_EXTREME, "I have all the bad luck. My sheep all run off somewhere, and then those mourners tell me they're infected!")
                    player(SAD, "Well, I hope things start to look up for you.")
                }

                STAGE_RECEIVED_SHEEP_FEED, STAGE_RECEIVED_PROTECTIVE_CLOTHING -> {
                    player(CALM_TALK, "Hello there.")
                    npc(npc, TALKING_ALOT, "Hello adventurer. Be careful herding those sheep; I don't think they've wandered far, but if you touch them you risk getting infected by the plague.")
                    npc(npc, TALKING_ALOT, "I suggest you use the cattleprod from the barn over there to herd them, that way you won't have to touch them directly and risk infection.")
                }

                STAGE_SHEEP_INCINERATED, STAGE_COMPLETE -> {
                    player(SAD, "Hello there. Sorry about your sheep...")
                    npc(npc, SAD, "That's alright. It had to be done for the sake of the town. I just hope none of my other livestock get infected.")
                }

            }
        }
    }
}