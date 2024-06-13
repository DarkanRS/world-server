package com.rs.game.content.quests.gunnars_ground.dialogues

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.gunnars_ground.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class ChieftainGunthorD (player: Player, npc: NPC) {
    init {
        player.startConversation {

            when (player.questManager.getStage(Quest.GUNNARS_GROUND)) {

                STAGE_NEED_TO_TALK_TO_GUNTHOR -> {
                    npc(npc, FRUSTRATED, "Begone, outerlander! Your kind are not welcome here!")
                    options {
                        op("I need to speak with you, chieftain.") {
                            player(CALM_TALK, "I need to speak with you, chieftain.")
                            goto("makeItShort")
                        }
                        op("Be quiet and listen.") {
                            player(FRUSTRATED, "Be quiet and listen.")
                            goto("makeItShort")
                        }
                    }
                    label("makeItShort")
                    npc(npc, FRUSTRATED, "Make it short.")
                    player(CALM_TALK, "Your daughter seeks permission to court an outerlander.")
                    npc(npc, MORTIFIED, "WHAT?")
                    options {
                        op("Your daughter seeks permission to court an outerlander.") {
                            player(CALM_TALK, "Your daughter seeks permission to court an outerlander.")
                            goto("doYouHaveAnyIdea")
                        }
                        op("Are you deaf?") {
                            player(FRUSTRATED, "Are you deaf?")
                            goto("doYouHaveAnyIdea")
                        }
                    }
                    label("doYouHaveAnyIdea")
                    npc(npc, FRUSTRATED, "Do you have ANY idea who we are?")
                    options {
                        op("You're barbarians.") {
                            player(CALM_TALK, "You're barbarians.")
                            goto("weAreTheStorm")
                        }
                        op("You're a tribe of primitives.") {
                            player(FRUSTRATED, "You're a tribe of primitives.")
                            goto("weAreTheStorm")
                        }
                    }
                    label("weAreTheStorm")
                    npc(npc, TALKING_ALOT, "We are the storm that sweeps from the mountains! We are the scourge of these soft lands!")
                    options {
                        op("Please wait a moment.") {
                            player(CALM_TALK, "Please wait a moment.")
                            goto("weAreTheFreemen")
                        }
                        op("Are you finished?") {
                            player(FRUSTRATED, "Are you finished?")
                            goto("weAreTheFreemen")
                        }
                    }
                    label("weAreTheFreemen")
                    npc(npc, TALKING_ALOT, "We are the freemen of the ice. You think this is a settlement, but it is a camp of war!")
                    npc(QUESTING_HAAKON, TALKING_ALOT, "Chieftain! May I interrupt?")
                    npc(npc, TALKING_ALOT, "What is it, Haakon?")
                    npc(QUESTING_HAAKON, TALKING_ALOT, "We have lived here since before the time of my father. Perhaps we are no longer a camp.")
                    npc(npc, FRUSTRATED, "Your father? Do you honour him, Haakon?")
                    npc(QUESTING_HAAKON, TALKING_ALOT, "Of course!")
                    npc(npc, FRUSTRATED, "And do you honour Warlord Gunnar?")
                    npc(QUESTING_HAAKON, TALKING_ALOT, "Of course, Chieftain!")
                    npc(npc, FRUSTRATED, "Then why do you dishonour his name by abandoning what he fought for?")
                    npc(npc, FRUSTRATED, "We will honour our fathers and we will honour Gunnar!")
                    npc(QUESTING_HAAKON, SAD, "Yes, Chieftain. You are wise. I am sorry.")
                    npc(npc, FRUSTRATED, "You! Outerlander!")
                    player(CALM_TALK, "What?")
                    npc(npc, FRUSTRATED, "We are not friends, you and I! We are not allies!")
                    npc(npc, FRUSTRATED, "Run back to Gudrun and tell her to remember her forefathers!")
                    npc(npc, FRUSTRATED, "Tell her to think of Gunnar and what he would think of this insult! Now go, before I have Haakon dismember you.") {
                        player.setQuestStage(Quest.GUNNARS_GROUND, STAGE_RETURN_TO_GUDRUN_AFTER_GUNTHOR)
                    }
                    options {
                        op("I'm going!") { player(WORRIED, "I'm going!") }
                        op("I'd like to see him try.") {
                            player(ROLL_EYES, "I'd like to see him try.")
                            npc(QUESTING_HAAKON, ANGRY, "Come here and say that to my face, outerlander!")
                        }
                        op("I'm going to challenge him right now!") {
                            player(CHUCKLE, "I'm going to challenge him right now!")
                            npc(QUESTING_HAAKON, ANGRY, "Come here and say that to my face, outerlander!")
                        }
                    }
                }

                STAGE_RETURN_TO_GUDRUN_AFTER_GUNTHOR, STAGE_RETURN_TO_DORORAN_TO_WRITE_POEM, STAGE_WRITING_THE_POEM,
                STAGE_FINISHED_WRITING_POEM, STAGE_RECEIVED_GUNNARS_GROUND_POEM -> {
                    npc(npc, FRUSTRATED, "Run back to Gudrun and tell her to remember her forefathers!")
                    npc(npc, FRUSTRATED, "Tell her to think of Gunnar and what he would think of this insult! Now go, before I have Haakon dismember you.")
                    options {
                        op("I'm going!") { player(WORRIED, "I'm going!") }
                        op("I'd like to see him try.") {
                            player(ROLL_EYES, "I'd like to see him try.")
                            npc(QUESTING_HAAKON, ANGRY, "Come here and say that to my face, outerlander!")
                        }
                        op("I'm going to challenge him right now!") {
                            player(CHUCKLE, "I'm going to challenge him right now!")
                            npc(QUESTING_HAAKON, ANGRY, "Come here and say that to my face, outerlander!")
                        }
                    }
                }

                STAGE_POST_CUTSCENES, STAGE_COMPLETE -> {
                    npc(npc, HAPPY_TALKING, "Welcome back, outerlander. Eat, drink, but do not trouble me with questions.")
                    if (!player.questManager.isComplete(Quest.FREMENNIK_TRIALS)) {
                        npc(npc, HAPPY_TALKING, "A drink to the outerlander!")
                    } else {
                        player(CALM_TALK, "I passed my trials in Rellekka. I am no outerlander.")
                        npc(npc, HAPPY_TALKING, "A drink to our brother!")
                    }
                    options {
                        op("A drink to Gunnar the strong!") {
                            player(HAPPY_TALKING, "A drink to Gunnar the strong!")
                            label("cheersTalk")
                            npc(npc, HAPPY_TALKING, "Skål!")
                            npc(QUESTING_HAAKON, HAPPY_TALKING, "Skål!")
                            if (!player.questManager.isComplete(Quest.GLORIOUS_MEMORIES)) player(HAPPY_TALKING, "Cheers!") else player(HAPPY_TALKING, "Skål!")
                        }
                        op("A drink to Dororan the poet!") {
                            player(HAPPY_TALKING, "A drink to Dororan the poet!")
                            goto("cheersTalk")
                        }
                        op("A drink to Gunthor the brave!") {
                            player(HAPPY_TALKING, "A drink to Gunthor the brave!")
                            goto("cheersTalk")
                        }
                        op("A drink to Gudrun the fiery!") {
                            player(HAPPY_TALKING, "A drink to Gudrun the fiery!")
                            goto("cheersTalk")
                        }
                        op("A drink to Haakon the mighty!") {
                            player(HAPPY_TALKING, "A drink to Haakon the mighty!")
                            goto("cheersTalk")
                        }
                    }
                }

                else -> {
                    npc(npc, VERY_FRUSTRATED, "Begone, outerlander! Your kind are not welcome here!")
                }

            }
        }
    }
}
