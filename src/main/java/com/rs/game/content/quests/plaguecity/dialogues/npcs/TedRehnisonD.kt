package com.rs.game.content.quests.plaguecity.dialogues.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.plaguecity.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class TedRehnisonD (player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.PLAGUE_CITY)) {

                STAGE_GAVE_BOOK_TO_TED, STAGE_SPEAK_TO_MILLI -> {
                    player(CALM_TALK, "Hi, I hear a woman called Elena is staying here.")
                    npc(npc, CALM_TALK, "Yes she was staying here, but slightly over a week ago she was getting ready to go back. However she never managed to leave. My daughter Milli was playing near the west wall when she saw some shadowy figures jump out")
                    npc(npc, CALM_TALK, "and grab her. Milli is upstairs if you wish to speak to her.") { player.questManager.setStage(Quest.PLAGUE_CITY, STAGE_SPEAK_TO_MILLI) }
                }

                in STAGE_SPOKEN_TO_MILLI..STAGE_GAVE_HANGOVER_CURE -> {
                    npc(npc, CALM_TALK, "Any luck finding Elena yet?")
                    player(CALM_TALK, "Not yet...")
                    npc(npc, CALM_TALK, "I wish you luck, she did a lot for us.")
                }

                STAGE_FREED_ELENA,
                STAGE_COMPLETE -> {
                    npc(npc, CALM_TALK, "Any luck finding Elena yet?")
                    player(CALM_TALK, "Yes, she is safe at home now.")
                    npc(npc, HAPPY_TALKING, "That's good to hear, she helped us a lot.")
                }

            }
        }
    }
}