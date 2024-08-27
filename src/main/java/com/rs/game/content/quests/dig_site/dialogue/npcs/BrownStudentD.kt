package com.rs.game.content.quests.dig_site.dialogue.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.dig_site.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class BrownStudentD(val player: Player, val npc: NPC) {
    private val questAttribs = player.questManager.getAttribs(Quest.DIG_SITE)
    private val talkedToAlready = questAttribs.getB(BROWN_STUDENT_TALKED_TO)
    private val hasCup = player.inventory.containsOneItem(SPECIAL_CUP)
    private val exam1AnswerObtained = questAttribs.getB(BROWN_STUDENT_EXAM_1_OBTAINED_ANSWER)

    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.DIG_SITE)) {
                in STAGE_UNSTARTED..STAGE_RECEIVED_SEALED_LETTER -> {
                    player(CALM_TALK, "Hello there.")
                    npc(npc, CALM_TALK, "Hello there. As you can see, I am a student.")
                    player(CALM_TALK, "What are you doing here?")
                    npc(npc, CALM_TALK, "I'm studying for the Earth Sciences exam.")
                    player(CALM_TALK, "Interesting... Perhaps I should study for it as well.")
                }

                STAGE_BEGIN_EXAM_1 -> {
                    if (!talkedToAlready) {
                        player(CALM_TALK, "Hello there. Can you help me with the Earth Sciences exams at all?")
                        npc(npc, CALM_TALK, "I can't do anything unless I find my special cup.")
                        player(CALM_TALK, "Your what?")
                        npc(npc, CALM_TALK, "My special cup. I won it for a particularly good find last month.")
                        player(CALM_TALK, "Oh, right. So if I find it, you'll help me?")
                        npc(npc, CALM_TALK, "I sure will!")
                        player(CALM_TALK, "Any ideas where it may be?")
                        npc(npc, CALM_TALK, "All I remember is that I was working near the panning area when I lost it.")
                        player(CALM_TALK, "Okay, I'll see what I can do.") {
                            questAttribs.setB(BROWN_STUDENT_TALKED_TO, true)
                        }
                        npc(npc, CALM_TALK, "Yeah, maybe the panning guide saw it? I hope I didn't lose it in the water!")
                    } else if (talkedToAlready && !hasCup && !exam1AnswerObtained) {
                        player(CALM_TALK, "Hello there. How's the study going?")
                        npc(npc, CALM_TALK, "I'm getting there. Have you found my special cup yet?")
                        player(CALM_TALK, "No, sorry, not yet.")
                        npc(npc, CALM_TALK, "Oh dear, I hope it didn't fall into the river. I might never find it again.")
                    } else if (talkedToAlready && hasCup && !exam1AnswerObtained) {
                        player(CALM_TALK, "Hello there.")
                        player(CALM_TALK, "Look what I found!")
                        npc(npc, HAPPY_TALKING, "Excellent! I'm so happy. Let me now help you with your exams...")
                        npc(npc, CALM_TALK, "The people eligible to use the site are: All that have passed the appropriate Earth Sciences exams.") {
                            player.inventory.deleteItem(SPECIAL_CUP, 1)
                            questAttribs.setB(BROWN_STUDENT_EXAM_1_OBTAINED_ANSWER, true)
                        }
                        player(CALM_TALK, "Thanks for the information.")
                    } else if (talkedToAlready && exam1AnswerObtained) {
                        player(CALM_TALK, "Hello there.")
                        npc(npc, CALM_TALK, "How's it going?")
                        player(CALM_TALK, "There are more exam questions I'm stuck on.")
                        npc(npc, CALM_TALK, "Hey, I'll tell you what I've learned. That may help.")
                        npc(npc, CALM_TALK, "The people eligible to use the site are: All that have passed the appropriate Earth Sciences exams.")
                        player(CALM_TALK, "Thanks for the information.")
                    }
                }

                STAGE_BEGIN_EXAM_2 -> {
                    player(CALM_TALK, "Hello there.")
                    npc(npc, CALM_TALK, "How's it going?")
                    player(CALM_TALK, "There are more exam questions I'm stuck on.")
                    npc(npc, CALM_TALK, "Hey, I'll tell you what I've learned. That may help.")
                    npc(npc, CALM_TALK, "Correct sample transportation: Samples taken in rough form; kept only in sealed containers.") {
                        questAttribs.setB(BROWN_STUDENT_EXAM_2_OBTAINED_ANSWER, true)
                    }
                    player(CALM_TALK, "Thanks for the information.")
                }

                STAGE_BEGIN_EXAM_3 -> {
                    player(CALM_TALK, "Hello there.")
                    npc(npc, CALM_TALK, "How's it going?")
                    player(CALM_TALK, "There are more exam questions I'm stuck on.")
                    npc(npc, CALM_TALK, "Hey, I'll tell you what I've learned. That may help.")
                    npc(npc, CALM_TALK, "The proper technique for handling bones is: Handle bones carefully and keep them away from other samples.") {
                        questAttribs.setB(BROWN_STUDENT_EXAM_3_OBTAINED_ANSWER, true)
                    }
                    player(CALM_TALK, "Thanks for the information.")
                }

                in STAGE_COMPLETED_EXAMS..STAGE_COMPLETE -> {
                    npc(npc, CALM_TALK, "Hey it's the great explorer! Thanks a lot for the help earlier!")
                }

            }
        }
    }
}
