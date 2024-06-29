package com.rs.game.content.quests.dig_site.dialogue.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.dig_site.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class PurpleStudentD(val player: Player, val npc: NPC) {
    private val questAttribs = player.questManager.getAttribs(Quest.DIG_SITE)
    private val talkedToAlready = questAttribs.getB(PURPLE_STUDENT_TALKED_TO)
    private val hasTeddy = player.inventory.containsOneItem(TEDDY)
    private val exam1AnswerObtained = questAttribs.getB(PURPLE_STUDENT_EXAM_1_OBTAINED_ANSWER)
    private val exam3AnswerObtained = questAttribs.getB(PURPLE_STUDENT_EXAM_3_OBTAINED_ANSWER)
    private val exam3TalkedTo = questAttribs.getB(PURPLE_STUDENT_EXAM_3_TALKED_TO)

    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.DIG_SITE)) {

                in STAGE_UNSTARTED..STAGE_RECEIVED_SEALED_LETTER -> {
                    player(CALM_TALK, "Hello there.")
                    npc(npc, CALM_TALK, "Hi there. I'm studying for the Earth Sciences exam.")
                    player(CALM_TALK, "Interesting... This exam seems to be a popular one!")
                }

                STAGE_BEGIN_EXAM_1 -> {
                    if (!talkedToAlready) {
                        player(CALM_TALK, "Hello there. Can you help me with the Earth Sciences exams at all?")
                        npc(npc, CALM_TALK, "I can if you help me...")
                        player(CALM_TALK, "How can I do that?")
                        npc(npc, CALM_TALK, "I have lost my teddy bear. He was my lucky mascot.")
                        player(CALM_TALK, "Do you know where you dropped him?")
                        npc(npc, CALM_TALK, "Well, I was doing a lot of walking that day... Oh yes, that's right - we were studying ceramics in fact, near the edge of the digsite.")
                        npc(npc, CALM_TALK, "I found some pottery that seemed to match the design on those large urns.")
                        player(CALM_TALK, "Leave it to me, I'll find it.") {
                            questAttribs.setB(PURPLE_STUDENT_TALKED_TO, true)
                        }
                        npc(npc, CALM_TALK, "Oh, great! Thanks!")
                    } else if (talkedToAlready && !hasTeddy && !exam1AnswerObtained) {
                        player(CALM_TALK, "Hello there. How's the study going?")
                        npc(npc, CALM_TALK, "Very well thanks. Have you found my lucky mascot yet?")
                        player(CALM_TALK, "No sorry, not yet.")
                        npc(npc, CALM_TALK, "I'm sure it's just outside the site somewhere...")
                    } else if (talkedToAlready && hasTeddy && !exam1AnswerObtained) {

                        player(CALM_TALK, "Hello there.")
                        player(CALM_TALK, "Guess what I found.")
                        npc(npc, CALM_TALK, "Hey! My lucky mascot! Thanks ever so much. Let me help you with those questions now.")
                        npc(npc, CALM_TALK, "The proper health and safety points are: Proper tools must be used.") {
                            player.inventory.deleteItem(TEDDY, 1)
                            questAttribs.setB(PURPLE_STUDENT_EXAM_1_OBTAINED_ANSWER, true)
                        }
                        player(CALM_TALK, "Great, thanks for your advice.")
                    } else if (talkedToAlready && exam1AnswerObtained) {
                        player(CALM_TALK, "Hello there.")
                        npc(npc, CALM_TALK, "How's it going?")
                        player(CALM_TALK, "I am stuck on some more exam questions.")
                        npc(npc, CALM_TALK, "Okay, I'll tell you my latest notes...")
                        npc(npc, CALM_TALK, "The proper health and safety points are: Proper tools must be used.")
                        player(CALM_TALK, "Okay, I'll remember that.")
                    }
                }

                STAGE_BEGIN_EXAM_2 -> {
                    player(CALM_TALK, "Hello there.")
                    npc(npc, CALM_TALK, "How's it going?")
                    player(CALM_TALK, "I am stuck on some more exam questions.")
                    npc(npc, CALM_TALK, "Okay, I'll tell you my latest notes...")
                    npc(npc, CALM_TALK, "Finds handling: Finds must be carefully handled.") {
                        questAttribs.setB(PURPLE_STUDENT_EXAM_2_OBTAINED_ANSWER, true)
                    }
                    player(CALM_TALK, "Great, thanks for your advice.")
                }

                STAGE_BEGIN_EXAM_3 -> {
                    if (!exam3TalkedTo) {
                        player(CALM_TALK, "Hello there.")
                        npc(npc, CALM_TALK, "What, you want more help?")
                        player(CALM_TALK, "Err... Yes please!")
                        npc(npc, CALM_TALK, "Well... it's going to cost you...")
                        player(CALM_TALK, "Oh, well how much?")
                        npc(npc, CALM_TALK, "I'll tell you what I would like: a precious stone. I don't find many of them. My favorite are opals; they are beautiful.")
                        npc(npc, CALM_TALK, "Just like me! Tee hee hee!")
                        player(CALM_TALK, "Err... OK I'll see what I can do, but I'm not sure where I'd get one.")
                        npc(npc, CALM_TALK, "Well, I have seen people get them from panning occasionally.")
                        player(CALM_TALK, "OK, I'll see what I can turn up for you.") {
                            questAttribs.setB(PURPLE_STUDENT_EXAM_3_TALKED_TO, true)
                        }
                    } else if (exam3TalkedTo && !exam3AnswerObtained) {
                        player(CALM_TALK, "Hello there.")
                        npc(npc, CALM_TALK, "Oh, hi again. Did you bring me the opal?")
                        if (player.inventory.containsOneItem(OPAL) || player.inventory.containsOneItem(UNCUT_OPAL)) {
                            player(CALM_TALK, "Would an opal look like this by any chance?")
                            npc(npc, CALM_TALK, "Wow, great, you've found one. This will look beautiful set in my necklace. Thanks for that; now I'll tell you what I know...")
                            npc(npc, CALM_TALK, "Sample preparation: Samples cleaned, and carried only in specimen jars.") {
                                if (player.inventory.containsOneItem(OPAL)) player.inventory.deleteItem(OPAL, 1) else player.inventory.deleteItem(UNCUT_OPAL, 1)
                                questAttribs.setB(PURPLE_STUDENT_EXAM_3_OBTAINED_ANSWER, true)
                            }
                            player(CALM_TALK, "Great, thanks for your advice.")
                        } else {
                            player(CALM_TALK, "I haven't found one yet.")
                            npc(npc, CALM_TALK, "Oh, well, tell me when you do. Remember that they can be found around the site; perhaps try panning the river.")
                        }
                    } else if (exam3TalkedTo && exam3AnswerObtained) {
                        player(CALM_TALK, "Hello there.")
                        npc(npc, CALM_TALK, "How's it going?")
                        player(CALM_TALK, "I am stuck on some more exam questions.")
                        npc(npc, CALM_TALK, "Okay, I'll tell you my latest notes...")
                        npc(npc, CALM_TALK, "Sample preparation: Samples cleaned, and carried only in specimen jars.")
                        player(CALM_TALK, "Great, thanks for your advice.")

                    }
                }

                in STAGE_COMPLETED_EXAMS..STAGE_COMPLETE -> {
                    npc(npc, CALM_TALK, "Hi there! Thanks again. Hey, maybe I'll be asking you for help next time. It seems you are something of an expert now!")
                }

            }
        }
    }
}
