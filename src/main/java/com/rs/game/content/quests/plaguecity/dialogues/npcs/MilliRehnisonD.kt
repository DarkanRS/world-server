package com.rs.game.content.quests.plaguecity.dialogues.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.plaguecity.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class MilliRehnisonD (player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.PLAGUE_CITY)) {

                STAGE_GAVE_BOOK_TO_TED,
                STAGE_SPEAK_TO_MILLI -> {
                    player(CALM_TALK, "Hello. Your parents say you saw what happened to Elena...")
                    npc(npc, CHILD_CALM_TALK, "*sniff* Yes I was near the south east corner when I saw Elena walking by. I was about to run to greet her when some men jumped out. They shoved a sack over her head and dragged her into a building.")
                    player(CALM_TALK, "Which building?")
                    npc(npc, CHILD_CALM_TALK, "It was the boarded up building with no windows in the south east corner of West Ardougne.") { player.questManager.setStage(Quest.PLAGUE_CITY, STAGE_SPOKEN_TO_MILLI) }
                }

                in STAGE_SPOKEN_TO_MILLI..STAGE_GAVE_HANGOVER_CURE -> {
                    npc(npc, CHILD_CALM_TALK, "Have you found Elena yet?")
                    player(CALM_TALK, "No, I'm still looking.")
                    npc(npc, CHILD_CALM_TALK, "I hope you find her. She was nice.")
                }

                STAGE_FREED_ELENA,
                STAGE_COMPLETE -> {
                    npc(npc, CHILD_CALM_TALK, "Have you found Elena yet?")
                    player(CALM_TALK, "Yes, she's safe at home.")
                    npc(npc, CHILD_HAPPY_TALK, "I hope she comes and visits sometime.")
                    player(CALM_TALK, "Maybe.")
                }

            }
        }
    }
}
