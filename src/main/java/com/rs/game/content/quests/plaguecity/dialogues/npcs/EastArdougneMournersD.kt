package com.rs.game.content.quests.plaguecity.dialogues.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.plaguecity.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class EastArdougneMournersD (player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.PLAGUE_CITY)) {

                STAGE_UNSTARTED -> {
                    player(CALM_TALK, "Hello.")
                    npc(npc, SKEPTICAL_THINKING, "What are you up to?")
                    player(CALM_TALK, "Nothing.")
                    npc(npc, SKEPTICAL_THINKING, "I don't trust you.")
                    player(CALM_TALK, "You don't have to.")
                    npc(npc, SKEPTICAL_THINKING, "If I find you attempting to cross the wall I'll make sure you never return.")
                }

                in STAGE_SPEAK_TO_ALRENA..STAGE_CAN_DIG -> {
                    player(CALM_TALK, "Hello.")
                    npc(npc, SKEPTICAL_THINKING, "What are you up to with old man Edmond?")
                    player(CALM_TALK, "Nothing, we've just been chatting.")
                    npc(npc, SKEPTICAL_THINKING, "What about his daughter?")
                    player(CALM_TALK, "Oh, you know about that then?")
                    npc(npc, SKEPTICAL_THINKING, "We know everything that goes on in Ardougne. We have to if we are to contain the plague.")
                    player(CALM_TALK, "Have you seen his daughter recently?")
                    npc(npc, SKEPTICAL_THINKING, "I imagine she's caught the plague. Either way she won't be allowed out of West Ardougne, the risk is too great.")
                }

                in STAGE_UNCOVERED_SEWER_ENTRANCE..STAGE_GRILL_REMOVED -> {
                    player(CALM_TALK, "Hello there.")
                    npc(npc, SKEPTICAL_THINKING, "Been digging have we?")
                    player(CALM_TALK, "What do you mean?")
                    npc(npc, SKEPTICAL_THINKING, "Your hands are covered in mud.")
                    player(CALM_TALK, "Oh that...")
                    npc(npc, SKEPTICAL_THINKING, "Funny, you don't look like the gardening type.")
                    player(CALM_TALK, "Oh no, I love gardening! It's my favorite pastime.")
                }

                in STAGE_GRILL_REMOVED..STAGE_COMPLETE -> {
                    if (player.questManager.getAttribs(Quest.PLAGUE_CITY).getB(ENTERED_CITY)) {
                        player(CALM_TALK, "Hello.")
                        npc(npc, SKEPTICAL_THINKING, "What are you up to?")
                        player(CALM_TALK, "Nothing.")
                        npc(npc, SKEPTICAL_THINKING, "I don't trust you.")
                        player(CALM_TALK, "You don't have to.")
                        npc(npc, SKEPTICAL_THINKING, "If I find you attempting to cross the wall I'll make sure you never return.")
                    }
                }

            }
        }
    }
}
