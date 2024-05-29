package com.rs.game.content.quests.plaguecity.dialogues.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.plaguecity.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class ElenaD (player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.PLAGUE_CITY)) {

                STAGE_GAVE_HANGOVER_CURE -> {
                    player(HAPPY_TALKING, "Hi, you're free to go! Your kidnappers don't seem to be about right now.")
                    npc(npc, HAPPY_TALKING, "Thank you, being kidnapped was so inconvenient. I was on my way back to East Ardougne with some samples, I want to see if I can diagnose a cure for this plague.")
                    player(HAPPY_TALKING, "Well you can leave via the manhole near the gate.")
                    npc(npc, HAPPY_TALKING, "Go and see my father, I'll make sure he adequately rewards you. Now I'd better leave while I still can.")
                    exec { player.questManager.setStage(Quest.PLAGUE_CITY, STAGE_FREED_ELENA) }
                }

                else -> {
                    player.sendMessage("Elena doesn't want to talk right now.")
                }

            }
        }
    }
}
