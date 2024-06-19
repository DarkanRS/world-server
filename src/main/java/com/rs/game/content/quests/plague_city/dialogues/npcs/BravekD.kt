package com.rs.game.content.quests.plague_city.dialogues.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.plague_city.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class BravekD (player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.PLAGUE_CITY)) {

                STAGE_PERMISSION_TO_BRAVEK -> {
                    npc(npc, DRUNK, "My head hurts! I'll speak to you another day...")
                    options {
                        op("This is really important though!") {
                            player(FRUSTRATED, "This is really important though!")
                            npc(npc, DRUNK, "I can't possibly speak to you with my head spinning like this... I went a bit heavy on the drink again last night. Curse my herbalist, she made the best hang over cures. Darn inconvenient of her catching the plague.")
                            options {
                                op("Ok, goodbye.") { player(CALM_TALK, "Ok, goodbye.") }
                                op("You shouldn't drink so much then!") {
                                    player(FRUSTRATED, "You shouldn't drink so much then!")
                                    npc(npc, DRUNK, "Well positions of responsibility are hard, I need something to take my mind off things... Especially with the problems this place has.")
                                    options {
                                        op("Ok, goodbye.") { player(CALM_TALK, "Ok, goodbye.") }
                                        op("Do you know what's in the cure?") {
                                            player(CONFUSED, "Do you know what's in the cure?")
                                            goto("inTheCure")
                                        }
                                        op("I don't think drink is the solution.") {
                                            player(CALM_TALK, "I don't think drink is the solution.")
                                            npc(npc, DRUNK, "Uurgh! My head still hurts too much to think straight. Oh for one of Trudi's hangover cures!")
                                        }
                                    }
                                }
                                op("Do you know what's in the cure?") {
                                    player(CONFUSED, "Do you know what's in the cure?")
                                    label("inTheCure")
                                    npc(npc, DRUNK, "Hmmm let me think... Ouch! Thinking isn't clever. Ah here, she did scribble it down for me.")
                                    if (player.inventory.hasFreeSlots()) {
                                        item(SCRUFFY_NOTE, "Bravek hands you a tatty piece of paper.") {
                                            player.inventory.addItem(SCRUFFY_NOTE)
                                            player.questManager.setStage(Quest.PLAGUE_CITY, STAGE_GET_HANGOVER_CURE)
                                        }
                                    } else {
                                        item(SCRUFFY_NOTE, "Bravek shows you a tatty piece of paper, but you don't have enough inventory space to take it from him.")
                                    }
                                }
                            }
                        }
                        op("Ok, goodbye.") { player(CALM_TALK, "Ok, goodbye.") }
                    }
                }

                STAGE_GET_HANGOVER_CURE -> {
                    npc(npc, DRUNK, "Uurgh! My head still hurts too much to think straight. Oh for one of Trudi's hangover cures!")
                    if (player.inventory.containsOneItem(HANGOVER_CURE)) {
                        player(SKEPTICAL_THINKING, "Try this.")
                        item(HANGOVER_CURE, "You give Bravek the hangover cure. Bravek gulps down the foul-looking liquid.") {
                            player.inventory.deleteItem(HANGOVER_CURE, 1)
                            npc.forceTalk("Grruurgh!")
                            npc.anim(DRINK_HANGOVER_CURE_ANIM)
                            player.questManager.setStage(Quest.PLAGUE_CITY, STAGE_GAVE_HANGOVER_CURE)
                        }
                        npc(npc, HAPPY_TALKING, "Ooh that's much better! Thanks, that's the clearest my head has felt in a month.")
                        npc(npc, HAPPY_TALKING, "Ah now, what was it you wanted me to do for you?")
                        player(TALKING_ALOT, "I need to rescue a kidnap victim called Elena. She's being held in a plague house, I need permission to enter.")
                        npc(npc, SKEPTICAL, "Well the mourners deal with that sort of thing...")
                        options {
                            op("Ok, I'll go speak to them.") { player(CALM_TALK, "Ok, I'll go speak to them.") }
                            op("Is that all anyone says around here?") {
                                player(FRUSTRATED, "Is that all anyone says around here?")
                                npc(npc, CALM_TALK, "Well, they know best about plague issues.")
                                options {
                                    op("Don't you want to take an interest in it at all?") {
                                        player(FRUSTRATED, "Don't you want to take an interest in it at all?")
                                        npc(npc, WORRIED, "Nope, I don't wish to take a deep interest in plagues. That stuff is too scary for me!")
                                        options {
                                            op("I see why people say you're a weak leader.") {
                                                player(FRUSTRATED, "I see why people say you're a weak leader.")
                                                npc(npc, CALM_TALK, "Bah, people always criticise their leaders but delegating is the only way to lead. I delegate all plague issues to the mourners.")
                                                player(FRUSTRATED, "This whole city is a plague issue!")
                                            }
                                            op("Ok, I'll talk to the mourners.") { player(CALM_TALK, "Ok, I'll talk to the mourners.") }
                                            op("They won't listen to me!") {
                                                player(CALM_TALK, "They won't listen to me! They say I'm not properly equipped to go in the house, though I do have a very effective gasmask.")
                                                goto("listenToMe")
                                            }
                                        }
                                    }
                                    op("They won't listen to me!") {
                                        player(CALM_TALK, "They won't listen to me! They say I'm not properly equipped to go in the house, though I do have a very effective gasmask.")
                                        goto("listenToMe")
                                    }
                                }
                            }
                            op("They won't listen to me!") {
                                player(CALM_TALK, "They won't listen to me! They say I'm not properly equipped to go in the house, though I do have a very effective gasmask.")
                                label("listenToMe")
                                npc(npc, CALM_TALK, "Hmmm, well I guess they're not taking the issue of a kidnapping seriously enough. They do go a bit far sometimes. I've heard of Elena, she has helped us a lot... Ok, I'll give you this warrant to enter the house.")
                                if (player.inventory.hasFreeSlots()) item(WARRANT, "Bravek hands you a warrant.") { player.inventory.addItem(WARRANT) }
                                else item(WARRANT, "Bravek shows you a warrant, but you don't have room to take it from him.")
                            }
                        }
                    }
                }

                STAGE_GAVE_HANGOVER_CURE -> {
                    npc(npc, HAPPY_TALKING, "Thanks again for the hangover cure.")
                    if (player.inventory.containsOneItem(WARRANT)) {
                        player(TALKING_ALOT, "I need to rescue a kidnap victim called Elena. She's being held in a plague house, I need permission to enter.")
                        npc(npc, HAPPY_TALKING, "I'm just having a little drop of whisky, then I'll feel really good.")
                    } else {
                        npc(npc, HAPPY_TALKING, "Ah now, what was it you wanted me to do for you?")
                        player(TALKING_ALOT, "I need to rescue a kidnap victim called Elena. She's being held in a plague house, I need permission to enter.")
                        npc(npc, SKEPTICAL, "Well the mourners deal with that sort of thing...")
                        options {
                            op("Ok, I'll go speak to them.") { player(CALM_TALK, "Ok, I'll go speak to them.") }
                            op("Is that all anyone says around here?") {
                                player(FRUSTRATED, "Is that all anyone says around here?")
                                npc(npc, CALM_TALK, "Well, they know best about plague issues.")
                                options {
                                    op("Don't you want to take an interest in it at all?") {
                                        player(FRUSTRATED, "Don't you want to take an interest in it at all?")
                                        npc(npc, WORRIED, "Nope, I don't wish to take a deep interest in plagues. That stuff is too scary for me!")
                                        options {
                                            op("I see why people say you're a weak leader.") {
                                                player(FRUSTRATED, "I see why people say you're a weak leader.")
                                                npc(npc, CALM_TALK, "Bah, people always criticise their leaders but delegating is the only way to lead. I delegate all plague issues to the mourners.")
                                                player(FRUSTRATED, "This whole city is a plague issue!")
                                            }
                                            op("Ok, I'll talk to the mourners.") { player(CALM_TALK, "Ok, I'll talk to the mourners.") }
                                            op("They won't listen to me!") {
                                                player(CALM_TALK, "They won't listen to me! They say I'm not properly equipped to go in the house, though I do have a very effective gasmask.")
                                                goto("listenToMe")
                                            }
                                        }
                                    }
                                    op("They won't listen to me!") {
                                        player(CALM_TALK, "They won't listen to me! They say I'm not properly equipped to go in the house, though I do have a very effective gasmask.")
                                        goto("listenToMe")
                                    }
                                }
                            }
                            op("They won't listen to me!") {
                                player(CALM_TALK, "They won't listen to me! They say I'm not properly equipped to go in the house, though I do have a very effective gasmask.")
                                label("listenToMe")
                                npc(npc, CALM_TALK, "Hmmm, well I guess they're not taking the issue of a kidnapping seriously enough. They do go a bit far sometimes. I've heard of Elena, she has helped us a lot... Ok, I'll give you this warrant to enter the house.")
                                if (player.inventory.hasFreeSlots()) item(WARRANT, "Bravek hands you a warrant.") { player.inventory.addItem(WARRANT) }
                                else item(WARRANT, "Bravek shows you a warrant, but you don't have room to take it from him.")
                            }
                        }
                    }
                }

                STAGE_FREED_ELENA,
                STAGE_COMPLETE -> {
                    npc(npc, HAPPY_TALKING, "Thanks again for the hangover cure.")
                }

            }
        }
    }
}
