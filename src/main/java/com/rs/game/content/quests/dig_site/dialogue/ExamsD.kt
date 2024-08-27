package com.rs.game.content.quests.dig_site.dialogue

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.dig_site.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class ExamsD(val player: Player, val npc: NPC) {
    fun exam1() {
        player.startConversation {
            var correctAnswerCount = 0
            npc(npc, CALM_TALK, "Okay, we will start with the first exam: Earth Sciences level 1 - Beginner.")
            npc(npc, CALM_TALK, "Question 1 - Earth Sciences overview. Can you tell me what Earth Sciences is?")
            options {
                if (player.questManager.getAttribs(Quest.DIG_SITE).getB(GREEN_STUDENT_EXAM_1_OBTAINED_ANSWER)) {
                    op("The study of the earth, its contents and history.") {
                        player(CALM_TALK, "The study of the earth, its contents and history.") {
                            correctAnswerCount += 1
                        }
                        goto("toQuestion2")
                    }
                } else {
                    op("The study of gardening, planing and fruiting vegetation.") {
                        player(CALM_TALK, "The study of gardening, planting and fruiting vegetation.")
                        goto("toQuestion2")
                    }
                }
                op("The study of planets and the history of worlds.") {
                    player(CALM_TALK, "The study of planets and the history of worlds.")
                    goto("toQuestion2")
                }
                op("The combination of archaeology and vegetarianism.") {
                    player(CALM_TALK, "The combination of archaeology and vegetarianism.")
                    goto("toQuestion2")
                }
            }
            label("toQuestion2")
            npc(npc, CALM_TALK, "Okay, next question...")
            npc(npc, CALM_TALK, "Earth Sciences level 1, question 2 - Eligibility. Can you tell me which people are allowed to use this site?")
            options {
                if (!player.questManager.getAttribs(Quest.DIG_SITE).getB(BROWN_STUDENT_EXAM_1_OBTAINED_ANSWER)) {
                    op("Magic users, miners and their escorts.") {
                        player(CALM_TALK, "Magic users, miners and their escorts.")
                        goto("toQuestion3")
                    }
                }
                op("Professors, students and workmen only.") {
                    player(CALM_TALK, "Professors, students and workmen only.")
                    goto("toQuestion3")
                }
                op("Local residents, contractors and small pink fish.") {
                    player(CALM_TALK, "Local residents, contractors and small pink fish.")
                    goto("toQuestion3")
                }
                if (player.questManager.getAttribs(Quest.DIG_SITE).getB(BROWN_STUDENT_EXAM_1_OBTAINED_ANSWER)) {
                    op("All that have passed the appropriate Earth Sciences exam.") {
                        player(CALM_TALK, "All that have passed the appropriate Earth Sciences exam.") {
                            correctAnswerCount += 1
                        }
                        goto("toQuestion3")
                    }
                }
            }
            label("toQuestion3")
            npc(npc, CALM_TALK, "Okay, next question...")
            npc(npc, CALM_TALK, "Earth Sciences level 1, question 3 - Health and safety. Can you tell me the proper safety points when working on a dig site?")
            options {
                if (!player.questManager.getAttribs(Quest.DIG_SITE).getB(PURPLE_STUDENT_EXAM_1_OBTAINED_ANSWER)) {
                    op("Heat-resistant clothing to be worn at all times.") {
                        player(CALM_TALK, "Heat-resistant clothing to be worn at all times.")
                        goto("toResults1")
                    }
                }
                op("Rubber chickens to be worn on the head at all times.") {
                    player(CALM_TALK, "Rubber chickens to be worn on the head at all times.")
                    goto("toResults1")
                }
                if (player.questManager.getAttribs(Quest.DIG_SITE).getB(PURPLE_STUDENT_EXAM_1_OBTAINED_ANSWER)) {
                    op("Proper tools must be used.") {
                        player(CALM_TALK, "Proper tools must be used.") {
                            correctAnswerCount += 1
                        }
                        goto("toResults1")
                    }
                }
                op("Protective clothing to be worn; tools kept away from site.") {
                    player(CALM_TALK, "Protective clothing to be worn; tools kept away from site.")
                    goto("toResults1")
                }
            }
            label("toResults1")
            npc(npc, CALM_TALK, "Okay, that covers the level 1 Earth Sciences exam.")
            npc(npc, CALM_TALK, "Let's see how you did...")
            exec { results1(correctAnswerCount) }
        }
    }

    private fun results1(correctAnswerCount: Int) {
        player.startConversation {
            when (correctAnswerCount) {
                0 -> {
                    npc(npc, CALM_TALK, "Deary me! This is appalling, none correct at all! I suggest you go and study properly.")
                    player(CALM_TALK, "Oh dear...")
                    npc(npc, CALM_TALK, "Why don't you use the resources here? There are the researchers, and you could even ask other students who are also studying for these exams.")
                }
                1 -> {
                    npc(npc, CALM_TALK, "You got one question correct. Better luck next time.")
                    player(CALM_TALK, "Oh bother!")
                    npc(npc, CALM_TALK, "Do some more research. I'm sure other students could help you out.")
                }
                2 -> {
                    npc(npc, CALM_TALK, "You got two questions correct. Not bad, just a little more revision needed.")
                    player(CALM_TALK, "Oh well...")
                }
                3 -> {
                    npc(npc, CALM_TALK, "You got all the questions correct. Well done!")
                    if (player.inventory.freeSlots >= 2) {
                        player(CALM_TALK, "Hey! Excellent!")
                        npc(npc, CALM_TALK, "You have now passed the Earth Sciences level 1 general exam. Here is your certificate to prove it. You also get a decent trowel to dig with. Of course, you'll want to get studying for your next exam now!") {
                            player.inventory.addItem(TROWEL)
                            player.inventory.addItem(EXAM_1_CERT)
                            player.setQuestStage(Quest.DIG_SITE, STAGE_BEGIN_EXAM_2)
                        }
                        exec { TheDigSiteUtils(player).openCertInterface(EXAM_1_CERT) }
                    } else {
                        simple("You need at least 2 free inventory slots to receive your exam certificate & reward.")
                    }
                }
            }
        }
    }

    fun exam2() {
        player.startConversation {
            var correctAnswerCount = 0
            npc(npc, CALM_TALK, "Okay, this is the next part of the Earth Sciences exam: Earth Sciences level 2 - Intermediate.")
            npc(npc, CALM_TALK, "Question 1 - Sample transportation. Can you tell me how we transport samples?")
            options {
                if (!player.questManager.getAttribs(Quest.DIG_SITE).getB(BROWN_STUDENT_EXAM_2_OBTAINED_ANSWER)) {
                    op("Samples cut and cleaned before transportation.") {
                        player(CALM_TALK, "Samples cut and cleaned before transportation.")
                        goto("toQuestion2")
                    }
                }
                op("Samples ground and suspended in an acid solution.") {
                    player(CALM_TALK, "Samples ground and suspended in an acid solution.")
                    goto("toQuestion2")
                }
                op("Samples to be given to the melon-collecting monkey.") {
                    player(CALM_TALK, "Samples to be given to the melon-collecting monkey.")
                    goto("toQuestion2")
                }
                if (player.questManager.getAttribs(Quest.DIG_SITE).getB(BROWN_STUDENT_EXAM_2_OBTAINED_ANSWER)) {
                    op("Samples taken in rough form; kept only in sealed containers.") {
                        player(CALM_TALK, "Samples taken in rough form; kept only in sealed containers.") {
                            correctAnswerCount += 1
                        }
                        goto("toQuestion2")
                    }
                }
            }
            label("toQuestion2")
            npc(npc, CALM_TALK, "Okay, next question...")
            npc(npc, CALM_TALK, "Earth Sciences level 2, question 2 - Handling of finds. What is the proper way to handle finds?")
            options {
                if (!player.questManager.getAttribs(Quest.DIG_SITE).getB(PURPLE_STUDENT_EXAM_2_OBTAINED_ANSWER)) {
                    op("Finds must not be handled by anyone.") {
                        player(CALM_TALK, "Finds must not be handled by anyone.")
                        goto("toQuestion3")
                    }
                }
                if (player.questManager.getAttribs(Quest.DIG_SITE).getB(PURPLE_STUDENT_EXAM_2_OBTAINED_ANSWER)) {
                    op("Finds must be carefully handled.") {
                        player(CALM_TALK, "Finds must be carefully handled.") {
                            correctAnswerCount += 1
                        }
                        goto("toQuestion3")
                    }
                }
                op("Finds to be given to the site workmen.") {
                    player(CALM_TALK, "Finds to be given to the site workmen.")
                    goto("toQuestion3")
                }
                op("Drop them on the floor and jump on them.") {
                    player(CALM_TALK, "Drop them on the floor and jump on them.")
                    goto("toQuestion3")
                }

            }
            label("toQuestion3")
            npc(npc, CALM_TALK, "Okay, next question...")
            npc(npc, CALM_TALK, "Earth Sciences level 2, question 3 - Rock pick usage. Can you tell me the proper use for a rock pick?")
            options {
                if (!player.questManager.getAttribs(Quest.DIG_SITE).getB(GREEN_STUDENT_EXAM_2_OBTAINED_ANSWER)) {
                    op("Strike rock repeatedly until powdered.") {
                        player(CALM_TALK, "Strike rock repeatedly until powdered.")
                        goto("toResults2")
                    }
                }
                op("Rock pick must be used flat and with strong force.") {
                    player(CALM_TALK, "Rock pick must be used flat and with strong force.")
                    goto("toResults2")
                }
                if (player.questManager.getAttribs(Quest.DIG_SITE).getB(GREEN_STUDENT_EXAM_2_OBTAINED_ANSWER)) {
                    op("Always handle with care; strike cleanly on its cleaving point.") {
                        player(CALM_TALK, "Always handle with care; strike cleanly on its cleaving point.") {
                            correctAnswerCount += 1
                        }
                        goto("toResults2")
                    }
                }
                op("Rock picks are to be used to milk cows on a rainy morning.") {
                    player(CALM_TALK, "Rock picks are to be used to milk cows on a rainy morning.")
                    goto("toResults2")
                }
            }
            label("toResults2")
            npc(npc, CALM_TALK, "Okay, that covers the level 2 Earth Sciences exam.")
            npc(npc, CALM_TALK, "Let me add up your total...")
            exec { results2(correctAnswerCount) }
        }
    }

    private fun results2(correctAnswerCount: Int) {
        player.startConversation {
            when (correctAnswerCount) {
                0 -> {
                    npc(npc, CALM_TALK, "No, no, no! This will not do. They are all wrong. Start again!")
                    player(CALM_TALK, "Oh no!")
                    npc(npc, CALM_TALK, "More studying for you my ${player.genderTerm("boy", "girl")}.")
                }
                1 -> {
                    npc(npc, CALM_TALK, "You got one question correct. At least it's a start.")
                    player(CALM_TALK, "Oh well...")
                    npc(npc, CALM_TALK, "Get out and explore the site, talk to people and learn!")
                }
                2 -> {
                    npc(npc, CALM_TALK, "You got two questions correct. Not too bad, but you can do better...")
                    player(CALM_TALK, "Nearly got it.")
                }
                3 -> {
                    npc(npc, CALM_TALK, "You got all the questions correct, well done!")
                    if (player.inventory.freeSlots >= 2) {
                        player(CALM_TALK, "Great, I'm getting good at this.")
                        npc(npc, CALM_TALK, "You have now passed the Earth Sciences level 2 intermediate exam. Here is your certificate. You also get a nice rock pick. Of course, you'll want to get studying for your next exam now!") {
                            player.inventory.addItem(ROCK_PICK)
                            player.inventory.addItem(EXAM_2_CERT)
                            player.setQuestStage(Quest.DIG_SITE, STAGE_BEGIN_EXAM_3)
                        }
                        exec { TheDigSiteUtils(player).openCertInterface(EXAM_2_CERT) }
                    } else {
                        simple("You need at least 2 free inventory slots to receive your exam certificate & reward.")
                    }
                }
            }
        }
    }

    fun exam3() {
        player.startConversation {
            var correctAnswerCount = 0
            npc(npc, CALM_TALK, "Attention, this is the final part of the Earth Sciences exam: Earth Sciences level 3 - Advanced.")
            npc(npc, CALM_TALK, "Question 1 - Sample preparation. Can you tell me how we prepare samples?")
            options {
                if (!player.questManager.getAttribs(Quest.DIG_SITE).getB(PURPLE_STUDENT_EXAM_3_OBTAINED_ANSWER)) {
                    op("Samples may be mixed together safely.") {
                        player(CALM_TALK, "Samples may be mixed together safely.")
                        goto("toQuestion2")
                    }
                } else {
                    op("Samples cleaned, and carried only in specimen jars.") {
                        player(CALM_TALK, "Samples cleaned, and carried only in specimen jars.") {
                            correctAnswerCount += 1
                        }
                        goto("toQuestion2")
                    }
                }
                op("Sample types catalogued and carried by hand only.") {
                    player(CALM_TALK, "Sample types catalogued and carried by hand only.")
                    goto("toQuestion2")
                }
                op("Samples to be spread thickly with mashed banana.") {
                    player(CALM_TALK, "Samples to be spread thickly with mashed banana.")
                    goto("toQuestion2")
                }

            }
            label("toQuestion2")
            npc(npc, CALM_TALK, "Okay, next question...")
            npc(npc, CALM_TALK, "Earth Sciences level 3, question 2 - Specimen brush use. What is the proper way to use a specimen brush?")
            options {
                if (!player.questManager.getAttribs(Quest.DIG_SITE).getB(GREEN_STUDENT_EXAM_2_OBTAINED_ANSWER)) {
                    op("Brush quickly using a wet brush.") {
                        player(CALM_TALK, "Brush quickly using a wet brush.")
                        goto("toQuestion3")
                    }
                } else {
                    op("Brush carefully and slowly using short strokes.") {
                        player(CALM_TALK, "Brush carefully and slowly using short strokes.") {
                            correctAnswerCount += 1
                        }
                        goto("toQuestion3")
                    }
                }
                op("Dipped in glue and stuck to a sheep's back.") {
                    player(CALM_TALK, "Dipped in glue and stuck to a sheep's back.")
                    goto("toQuestion3")
                }
                op("Brush quickly and with force.") {
                    player(CALM_TALK, "Brush quickly and with force.")
                    goto("toQuestion3")
                }

            }
            label("toQuestion3")
            npc(npc, CALM_TALK, "Okay, next question...")
            npc(npc, CALM_TALK, "Earth Sciences level 3, question 3 - Advanced techniques. Can you describe the technique for handling bones?")
            options {
                if (!player.questManager.getAttribs(Quest.DIG_SITE).getB(BROWN_STUDENT_EXAM_2_OBTAINED_ANSWER)) {
                    op("Bones must not be taken from the site.") {
                        player(CALM_TALK, "Bones must not be taken from the site.")
                        goto("toResults3")
                    }
                }
                op("Feed to hungry dogs.") {
                    player(CALM_TALK, "Feed to hungry dogs.")
                    goto("toResults3")
                }
                op("Bones to be ground and tested for mineral content.") {
                    player(CALM_TALK, "Bones to be ground and tested for mineral content.")
                    goto("toResults3")
                }
                if (player.questManager.getAttribs(Quest.DIG_SITE).getB(GREEN_STUDENT_EXAM_2_OBTAINED_ANSWER)) {
                    op("Handle bones very carefully and keep them away from other samples.") {
                        player(CALM_TALK, "Handle bones very carefully and keep them away from other samples.") {
                            correctAnswerCount += 1
                        }
                        goto("toResults3")
                    }
                }
            }
            label("toResults3")
            npc(npc, CALM_TALK, "Okay, that concludes the level 3 Earth Sciences exam.")
            npc(npc, CALM_TALK, "Let me add up the results...")
            exec { results3(correctAnswerCount) }
        }
    }

    private fun results3(correctAnswerCount: Int) {
        player.startConversation {
            when (correctAnswerCount) {
                0 -> {
                    npc(npc, CALM_TALK, "I cannot believe this! Absolutely none right at all. I doubt you did any research before you took this exam...")
                    player(CALM_TALK, "Ah... Yes... Erm.... I think I had better go and revise first!")
                }
                1 -> {
                    npc(npc, CALM_TALK, "You got one question correct. Try harder!")
                    player(CALM_TALK, "Oh bother!")
                }
                2 -> {
                    npc(npc, CALM_TALK, "You got two questions correct. A little more study and you will pass it.")
                    player(CALM_TALK, "I'm nearly there...")
                }
                3 -> {
                    npc(npc, CALM_TALK, "You got all the questions correct, well done!")
                    if (player.inventory.freeSlots >= 1) {
                        val hasSpecimenJar = player.inventory.containsOneItem(SPECIMEN_JAR)
                        val hasSpecimenBrush = player.inventory.containsOneItem(SPECIMEN_BRUSH)
                        var requiredSlots = 1 // For the level 3 cert

                        if (!hasSpecimenJar) requiredSlots++
                        if (!hasSpecimenBrush) requiredSlots++

                        if (player.inventory.freeSlots >= requiredSlots) {
                            player(CALM_TALK, "Hooray!")
                            npc(npc, CALM_TALK, "Congratulations! You have now passed the Earth Sciences level 3 exam. Here is your level 3 certificate and a specimen jar and brush.") {
                                player.inventory.addItem(EXAM_3_CERT)
                                if (!hasSpecimenJar) player.inventory.addItem(SPECIMEN_JAR)
                                if (!hasSpecimenBrush) player.inventory.addItem(SPECIMEN_BRUSH)
                                player.setQuestStage(Quest.DIG_SITE, STAGE_COMPLETED_EXAMS)
                            }
                            player(CALM_TALK, "I can dig wherever I want now!")
                            npc(npc, CALM_TALK, "Perhaps you should use your newfound skills to find an artefact on the site that will impress Terry, our archaeological expert.")
                            exec { TheDigSiteUtils(player).openCertInterface(EXAM_3_CERT) }
                        } else {
                            simple("You need at least $requiredSlots free inventory slots to receive your exam certificate & reward.")
                        }
                    } else {
                        simple("You need at least 1 free inventory slot to receive your exam certificate.")
                    }
                }
            }
        }
    }

}
