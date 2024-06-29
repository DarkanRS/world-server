package com.rs.game.content.quests.dig_site.dialogue.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.dig_site.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class GreenStudentD(val player: Player, val npc: NPC) {
    private val questAttribs = player.questManager.getAttribs(Quest.DIG_SITE)
    private val talkedToAlready = questAttribs.getB(GREEN_STUDENT_TALKED_TO)
    private val hasSkull = player.inventory.containsOneItem(ANIMAL_SKULL)
    private val exam1AnswerObtained = questAttribs.getB(GREEN_STUDENT_EXAM_1_OBTAINED_ANSWER)

    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.DIG_SITE)) {
                in STAGE_UNSTARTED..STAGE_RECEIVED_SEALED_LETTER -> {
                    player(CALM_TALK, "Hello there.")
                    npc(npc, CALM_TALK, "Oh, hi. I'm studying hard for an exam.")
                    player(CALM_TALK, "What exam is that?")
                    npc(npc, CALM_TALK, "It's the Earth Sciences exam.")
                    player(CALM_TALK, "Interesting....")
                }

                STAGE_BEGIN_EXAM_1 -> {
                    if (!talkedToAlready) {
                        player(CALM_TALK, "Hello there. Can you help me with the Earth Sciences exams at all?")
                        npc(npc, CALM_TALK, "Well... Maybe I will if you help me with something.")
                        player(CALM_TALK, "What's that?")
                        npc(npc, CALM_TALK, "I have lost my recent good find.")
                        player(CALM_TALK, "What does it look like?")
                        npc(npc, CALM_TALK, "Err... Like an animal skull!")
                        player(CALM_TALK, "Well, that's not too helpful, there are lots of those around here. Can you remember where you last had it?")
                        npc(npc, CALM_TALK, "It was around here for sure. Maybe one of the workmen picked it up?")
                        player(CALM_TALK, "Okay, I'll have a look for you.") {
                            questAttribs.setB(GREEN_STUDENT_TALKED_TO, true)
                        }
                    } else if (talkedToAlready && !hasSkull && !exam1AnswerObtained) {
                        player(CALM_TALK, "Hello there. How's the study going?")
                        npc(npc, CALM_TALK, "Very well, thanks. Have you found my animal skull yet?")
                        player(CALM_TALK, "No, sorry, not yet.")
                        npc(npc, CALM_TALK, "Oh well, I am sure it's been picked up. Couldn't you try looking through some pockets?")
                    } else if (talkedToAlready && hasSkull && !exam1AnswerObtained) {
                        player(CALM_TALK, "Hello there. Is this your animal skull?")
                        npc(npc, HAPPY_TALKING, "Oh wow! You've found it! Thank you so much. I'll be glad to tell you what I know about the exam.")
                        npc(npc, CALM_TALK, "The study of Earth Sciences is: The study of the earth, its contents and history.") {
                            player.inventory.deleteItem(ANIMAL_SKULL, 1)
                            questAttribs.setB(GREEN_STUDENT_EXAM_1_OBTAINED_ANSWER, true)
                        }
                        player(CALM_TALK, "Okay, I'll remember that.")
                    } else if (talkedToAlready && exam1AnswerObtained) {
                        player(CALM_TALK, "Hello there.")
                        npc(npc, CALM_TALK, "How's it going?")
                        player(CALM_TALK, "I need more help with the exam.")
                        npc(npc, CALM_TALK, "Well, okay, this is what I have learned since I last spoke to you...")
                        npc(npc, CALM_TALK, "The study of Earth Sciences is: The study of the earth, its contents and history.")
                        player(CALM_TALK, "Okay, I'll remember that.")
                    }
                }

                STAGE_BEGIN_EXAM_2 -> {
                    player(CALM_TALK, "Hello there.")
                    npc(npc, CALM_TALK, "How's it going?")
                    player(CALM_TALK, "I need more help with the exam.")
                    npc(npc, CALM_TALK, "Well, okay, this is what I have learned since I last spoke to you...")
                    npc(npc, CALM_TALK, "Correct rock pick usage: Always handle with care; strike the rock cleanly on its cleaving point.") {
                        questAttribs.setB(GREEN_STUDENT_EXAM_2_OBTAINED_ANSWER, true)
                    }
                    player(CALM_TALK, "Okay, I'll remember that. Thanks for all your help.")
                }

                STAGE_BEGIN_EXAM_3 -> {
                    player(CALM_TALK, "Hello there.")
                    npc(npc, CALM_TALK, "How's it going?")
                    player(CALM_TALK, "I need more help with the exam.")
                    npc(npc, CALM_TALK, "Well, okay, this is what I have learned since I last spoke to you...")
                    npc(npc, CALM_TALK, "Specimen brush use: Brush carefully and slowly using short strokes.") {
                        questAttribs.setB(GREEN_STUDENT_EXAM_3_OBTAINED_ANSWER, true)
                    }
                    player(CALM_TALK, "Okay, I'll remember that. Thanks for all your help.")
                }

                in STAGE_COMPLETED_EXAMS..STAGE_COMPLETE -> {
                    npc(npc, CALM_TALK, "Oh, hi again. News of your find has spread fast; you are quite famous around here now.")
                }

            }
        }
    }
}
