package com.rs.game.content.quests.gunnarsground.dialogues

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.gunnarsground.*
import com.rs.game.content.quests.gunnarsground.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class DororanD (player: Player, npc: NPC) {
    init {
        if (!Quest.GUNNARS_GROUND.meetsReqs(player, "before you can talk to Dororan.")) {
        } else {
            player.startConversation {
                when (player.questManager.getStage(Quest.GUNNARS_GROUND)) {
                    STAGE_UNSTARTED -> {
                        npc(npc, CALM_TALK, "'My heart with burdens heavy does it lie.'")
                        npc(npc, SKEPTICAL_THINKING, "'For never did I...'")
                        npc(npc, SKEPTICAL_HEAD_SHAKE, "Um...")
                        options {
                            op("'...ever learn to fly?'") {
                                player(CHEERFUL, "'...ever learn to fly?'")
                                goto("finishLine")
                            }
                            op("'...eat redberry pie?'") {
                                player(CHEERFUL, "'...eat redberry pie?'")
                                goto("finishLine")
                            }
                            op("'...get the evil eye?'") {
                                player(CHEERFUL, "'...get the evil eye?'")
                                goto("finishLine")
                            }
                        }
                        label("finishLine")
                        npc(npc, HAPPY_TALKING, "You're a poet too?")
                        options {
                            op("Yes.") {
                                player(CHEERFUL, "Yes.")
                                npc(npc, CHEERFUL, "Ah! Then I'm sure you can identify with the arduous state of my life.")
                                goto("poetToo?")
                            }
                            op("Maybe a bit.") {
                                player(SKEPTICAL, "Maybe a bit.")
                                npc(npc, CHEERFUL, "Oh. Then maybe you can identify with the arduous state of my life.")
                                goto("poetToo?")
                            }
                            op("No.") {
                                player(CALM, "No.")
                                npc(npc, SAD, "Oh. How can I expect you to identify with the arduous state of my life?")
                                goto("poetToo?")
                            }
                        }
                        label("poetToo?")
                        npc(npc, SAD, "My heart is stricken with that most audacious of maladies!")
                        options {
                            op("Angina?") {
                                player(WORRIED, "Angina?")
                                goto("love!")
                            }
                            op("Hypertension?") {
                                player(WORRIED, "Hypertension?")
                                goto("love!")
                            }
                            op("Coclearabsidosis?") {
                                player(WORRIED, "Coclearabsidosis?")
                                goto("love!")
                            }
                        }
                        label("love!")
                        npc(npc, HAPPY_TALKING, "Love!")
                        npc(npc, SAD_MILD_LOOK_DOWN, "The walls of my heart are besieged by love's armies, and those walls begin to tumble!")
                        npc(npc, TALKING_ALOT, "In the barbarian village lives the fairest maiden I have ever witnessed in all my life.")
                        options {
                            op("What's so special about her?") {
                                player(CONFUSED, "What's so special about her?")
                                npc(npc, AMAZED, "I wouldn't know where to start! Her fiery spirit? Her proud bearing? Her winsome form?")
                                options {
                                    op("But why is this making you sad?") {
                                        player(CONFUSED, "But why is this making you sad?")
                                        goto("peopleOfThisVillage")
                                    }
                                    op("What do you actually need?") {
                                        player(CONFUSED, "What do you actually need?")
                                        goto("peopleOfThisVillage")
                                    }
                                }
                            }
                            op("Get to the point.") {
                                player(FRUSTRATED, "Get to the point.")
                                goto("peopleOfThisVillage")
                            }
                        }
                        label("peopleOfThisVillage")
                        npc(npc, TALKING_ALOT, "The people of this village value strength, stature and riches. I have none of these things.")
                        npc(npc, TALKING_ALOT, "My people are indomitable warriors, dripping with gold and precious gems, but not I.")
                        npc(npc, TALKING_ALOT, "I am not built for combat, and poetry has proven a life of poverty!")
                        options {
                            op("There must be something you can do.") {
                                player(CONFUSED, "There must be something you can do.")
                                goto("couldEverLove")
                            }
                            op("Not to mention low stature.") {
                                player(LAUGH, "Not to mention low stature.")
                                npc(npc, SAD_SNIFFLE, "You see!")
                                goto("couldEverLove")
                            }
                        }
                        label("couldEverLove")
                        npc(npc, SKEPTICAL, "If Gudrun could ever love a dwarf, surely she would need to see my artisanry.")
                        npc(npc, AMAZED_MILD, "Will you help me? I am no crafter of metal.")
                        questStart(Quest.GUNNARS_GROUND)
                        npc(npc, TALKING_ALOT, "I need a ring of purest gold. Then we can engrave it with the words of my heart.")
                        npc(npc, HAPPY_TALKING, "Oh! I know the perfect place to get a gold ring.")
                        npc(npc, HAPPY_TALKING, "Edgeville's metalsmith, Jeffery, labours like myself under the weight of unrequited love.")
                        item(LOVE_POEM.id, "Dororan gives you a poem.") {
                            player.anim(ANIM_TAKE_ITEM)
                            player.inventory.addItem(LOVE_POEM, true)
                            setHasItem(player, LOVE_POEM, true)
                            player.setQuestStage(Quest.GUNNARS_GROUND, STAGE_RECEIVED_LOVE_POEM)
                        }
                        label("poemReceivedOptions")
                        options {
                            op("I have some questions.") {
                                player(SKEPTICAL_THINKING, "I have some questions.")
                                npc(npc, TALKING_ALOT, "By all means.")
                                label("questionOptions")
                                options {
                                    op("Does it have to be a ring from Jeffery?") {
                                        player(CONFUSED, "Does it have to be a ring from Jeffery?")
                                        npc(npc, AMAZED, "Yes! Jeffery's rings are timeless works of incomparable romantic splendour.")
                                        goto("questionOptions")
                                    }
                                    op("Where is Edgeville?") {
                                        player(CONFUSED, "Where is Edgeville?")
                                        npc(npc, CALM_TALK, "North of here, beyond a ruined fortress. It used to be a bustling den of cutthroats but it's quite quiet these days.")
                                        goto("questionOptions")
                                    }
                                    op("Why can't you go yourself?") {
                                        player(CONFUSED, "Why can't you go yourself?")
                                        npc(npc, SAD_MILD, "Some time ago, Jeffery asked me for advice in acting on his affections. I gave him the best advice that I could. Things didn't work out very well for him. One thing led to another and now he no longer wishes to speak to me.")
                                        goto("questionOptions")
                                    }
                                    op("Why can't you give a poem directly to Gudrun?") {
                                        player(CONFUSED, "Why can't you give a poem directly to Gudrun?")
                                        npc(npc, AMAZED, "These love poems are written in the Misthalinian style. A noble barbarian maiden would be insulted, not flattered.")
                                        goto("questionOptions")
                                    }
                                    op("You want me to trick her into thinking you made the ring?") {
                                        player(CONFUSED, "You want me to trick her into thinking you made the ring?")
                                        npc(npc, LAUGH, "Oh no, nothing like that! I have the words, I just need your help with the tools.")
                                        goto("questionOptions")
                                    }
                                    op("Actually, never mind.") {
                                        player(CALM_TALK, "Actually, never mind.")
                                        goto("poemReceivedOptions")
                                    }
                                }
                            }
                            op("I'll return with a ring from Jeffery.") { player(CALM_TALK, "I'll return with a ring from Jeffery.") }
                        }
                    }

                    STAGE_RECEIVED_LOVE_POEM -> {
                        label("poemReceivedOptions")
                        options {
                            if (getHasItem(player, LOVE_POEM)) {
                                op("I'll return with a ring from Jeffery.") { player(CALM_TALK, "I'll return with a ring from Jeffery.") }
                            } else {
                                op("I seem to have misplaced the poem you gave me.") {
                                    player(SAD, "I seem to have misplaced the poem you gave me.")
                                    npc(npc, CALM_TALK, "Luckily, I made a copy!")
                                    item(LOVE_POEM.id, "Dororan gives you a copy of the love poem.") {
                                        player.anim(ANIM_TAKE_ITEM)
                                        player.inventory.addItem(LOVE_POEM, true)
                                        setHasItem(player, LOVE_POEM, true)
                                    }
                                    player(CALM_TALK, "I'll return with a ring from Jeffery.")
                                }
                            }
                            op("I have some questions.") {
                                player(SKEPTICAL_THINKING, "I have some questions.")
                                npc(npc, TALKING_ALOT, "By all means.")
                                label("questionOptions")
                                options {
                                    op("Does it have to be a ring from Jeffery?") {
                                        player(CONFUSED, "Does it have to be a ring from Jeffery?")
                                        npc(npc, AMAZED, "Yes! Jeffery's rings are timeless works of incomparable romantic splendour.")
                                        goto("questionOptions")
                                    }
                                    op("Where is Edgeville?") {
                                        player(CONFUSED, "Where is Edgeville?")
                                        npc(npc, CALM_TALK, "North of here, beyond a ruined fortress. It used to be a bustling den of cutthroats but it's quite quiet these days.")
                                        goto("questionOptions")
                                    }
                                    op("Why can't you go yourself?") {
                                        player(CONFUSED, "Why can't you go yourself?")
                                        npc(npc, SAD_MILD, "Some time ago, Jeffery asked me for advice in acting on his affections. I gave him the best advice that I could. Things didn't work out very well for him. One thing led to another and now he no longer wishes to speak to me.")
                                        goto("questionOptions")
                                    }
                                    op("Why can't you give a poem directly to Gudrun?") {
                                        player(CONFUSED, "Why can't you give a poem directly to Gudrun?")
                                        npc(npc, AMAZED, "These love poems are written in the Misthalinian style. A noble barbarian maiden would be insulted, not flattered.")
                                        goto("questionOptions")
                                    }
                                    op("You want me to trick her into thinking you made the ring?") {
                                        player(CONFUSED, "You want me to trick her into thinking you made the ring?")
                                        npc(npc, LAUGH, "Oh no, nothing like that! I have the words, I just need your help with the tools.")
                                        goto("questionOptions")
                                    }
                                    op("Actually, never mind.") {
                                        player(CALM_TALK, "Actually, never mind.")
                                        goto("poemReceivedOptions")
                                    }
                                }
                            }
                        }
                    }

                    STAGE_RECEIVED_RING -> {
                        npc(npc, TALKING_ALOT, "'I await in eagerness for a loop of lustrous grandeur.' No, that just sounds ridiculous. Have you brought me a ring from Jeffery?")
                        if (!getHasItem(player, RING_FROM_JEFFERY)) {
                            player(SAD, "I must have dropped it on the way here! I wonder if Jeffery will give me another?")
                        } else if (!player.containsOneItem(RING_FROM_JEFFERY.id)) {
                            player(SAD, "I should fetch the gold ring from the bank first.")
                        } else {
                            player(HAPPY_TALKING, "I have one right here.")
                            item(RING_FROM_JEFFERY.id, "You show Dororan the ring from Jeffery.")
                            npc(npc, HAPPY_TALKING, "Thank you! That's exactly what I need!")
                            npc(npc, HAPPY_TALKING, "Now, would you engrave something on it for me?")
                            options {
                                op("What do you want me to engrave?") {
                                    player(CONFUSED, "What do you want me to engrave?")
                                    goto("givenThisSomeThought")
                                }
                                op("It had better be something impressive.") {
                                    player(WORRIED, "It had better be something impressive.")
                                    goto("givenThisSomeThought")
                                }
                            }
                            label("givenThisSomeThought")
                            npc(npc, SKEPTICAL_THINKING, "I've given this some thought.")
                            npc(npc, CHEERFUL_EXPOSITION, "'Gudrun the Fair, Gudrun the Fiery.'")
                            options {
                                op("How do I engrave that?") {
                                    player(CONFUSED, "How do I engrave that?")
                                    goto("useAChisel")
                                }
                                op("That sounds simple enough.") {
                                    player(WORRIED, "That sounds simple enough.")
                                    goto("useAChisel")
                                }
                            }
                            label("useAChisel")
                            npc(npc, CALM_TALK, "Just use a chisel on the gold ring.") { player.setQuestStage(Quest.GUNNARS_GROUND, STAGE_NEED_TO_CHISEL_RING) }
                            options {
                                if (!player.containsOneItem(CHISEL.id)) {
                                    op("Do you have a chisel I can use?") {
                                        player(CALM_TALK, "Do you have a chisel I can use?")
                                        npc(npc, HAPPY_TALKING, "Yes, here you go.")
                                        item(CHISEL.id, "Dororan gives you a chisel.") {
                                            player.anim(ANIM_TAKE_ITEM)
                                            player.inventory.addItem(CHISEL, true)
                                            player.setQuestStage(Quest.GUNNARS_GROUND, STAGE_NEED_TO_CHISEL_RING)
                                        }
                                    }
                                }
                                op("Isn't a chisel a bit clumsy for that?") {
                                    player(CONFUSED, "Isn't a chisel a bit clumsy for that?")
                                    npc(npc, HAPPY_TALKING, "I've seen jewelcrafters use them for all sorts of precise work.") { player.setQuestStage(Quest.GUNNARS_GROUND, STAGE_NEED_TO_CHISEL_RING) }
                                }
                                op("Okay.") { player(CALM_TALK, "Okay.") }
                            }
                        }
                    }

                    STAGE_NEED_TO_CHISEL_RING -> {
                        if (!getHasItem(player, RING_FROM_JEFFERY)) {
                            player(SAD, "I seem to have lost the ring I got from Jeffery!")
                            npc(npc, WORRIED, "Oh no! You should ask Jeffery if he has another gold ring.") {
                                player.setQuestStage(Quest.GUNNARS_GROUND, STAGE_RECEIVED_RING)
                            }
                        } else {
                            npc(npc, CALM_TALK, "Is it done? Have you created a work of magnificent beauty?")
                            label("chiselOptions")
                            options {
                                op("Do you have a chisel I can use?") {
                                    if (!player.containsOneItem(CHISEL.id)) {
                                        player(CALM_TALK, "Do you have a chisel I can use?")
                                        npc(npc, HAPPY_TALKING, "Yes, here you go.")
                                        item(CHISEL.id, "Dororan gives you a chisel.") {
                                            player.inventory.addItem(CHISEL, true)
                                            player.setQuestStage(Quest.GUNNARS_GROUND, STAGE_NEED_TO_CHISEL_RING)
                                        }
                                        goto("chiselOptions")
                                    } else {
                                        npc(npc, HAPPY_TALKING, "You seem to already have one.")
                                        goto("chiselOptions")
                                    }
                                }
                                op("Isn't a chisel a bit clumsy for that?") {
                                    player(CONFUSED, "Isn't a chisel a bit clumsy for that?")
                                    npc(npc, HAPPY_TALKING, "I've seen jewelcrafters use them for all sorts of precise work.")
                                    goto("chiselOptions")
                                }
                                op("Not yet.") { player(CALM_TALK, "Not yet.") }
                            }
                        }
                    }

                    STAGE_CHISELED_RING -> {
                        if (!getHasItem(player, DORORANS_ENGRAVED_RING)) {
                            player(SAD, "I seem to have lost the ring I engraved!")
                            npc(npc, WORRIED, "Oh no! You should ask Jeffery if he has another gold ring.") {
                                player.setQuestStage(Quest.GUNNARS_GROUND, STAGE_RECEIVED_RING)
                            }
                        } else if (!player.containsOneItem(DORORANS_ENGRAVED_RING.id)) {
                            player(SAD, "I should fetch the engraved ring from the bank.")
                        } else {
                            npc(npc, CALM_TALK, "Is it done? Have you created a work of magnificent beauty?")
                            options {
                                op("It's come out perfectly.") {
                                    player(HAPPY_TALKING, "It's come out perfectly.")
                                    item(DORORANS_ENGRAVED_RING.id, "You show Dororan the engraved ring.")
                                    npc(npc, HAPPY_TALKING, "You're right! It's perfect!")
                                    goto("doOneMoreThing")
                                }
                                op("How does this look?") {
                                    player(SKEPTICAL, "How does this look?")
                                    item(DORORANS_ENGRAVED_RING.id, "You show Dororan the engraved ring.")
                                    npc(npc, HAPPY_TALKING, "Brilliant! That's perfect!")
                                    goto("doOneMoreThing")
                                }
                                op("It's a complete disaster.") {
                                    player(WORRIED, "It's a complete disaster.")
                                    item(DORORANS_ENGRAVED_RING.id, "You show Dororan the engraved ring.")
                                    npc(npc, AMAZED, "I don't know what you mean: it's perfect!")
                                    goto("doOneMoreThing")
                                }
                            }
                            label("doOneMoreThing")
                            npc(npc, TALKING_ALOT, "Will you do one more thing for me?")
                            options {
                                op("Of course.") {
                                    player(HAPPY_TALKING, "Of course.")
                                    goto("iFearSheWillOnlyJudge")
                                }
                                op("What now?") {
                                    player(FRUSTRATED, "What now?")
                                    goto("iFearSheWillOnlyJudge")
                                }
                            }
                            label("iFearSheWillOnlyJudge")
                            npc(npc, TALKING_ALOT, "I fear she will only judge this poor book by its cover. Would you take the ring to Gudrun for me?")
                            options {
                                op("Very well.") {
                                    player(HAPPY_TALKING, "Very well.")
                                    goto("pleaseDon'tTellHer")
                                }
                                op("I hope this is going somewhere.") {
                                    player(FRUSTRATED, "I hope this is going somewhere.")
                                    goto("pleaseDon'tTellHer")
                                }
                            }
                            label("pleaseDon'tTellHer")
                            npc(npc, WORRIED, "Please don't tell her I'm a dwarf just yet.") {
                                player.setQuestStage(Quest.GUNNARS_GROUND, STAGE_NEED_TO_DELIVER_RING)
                            }
                            label("pleaseDon'tTellHerOptions")
                            options {
                                op("Where is she?") {
                                    player(CONFUSED, "Where is she?")
                                    npc(npc, CALM_TALK, "Inside the barbarian village.")
                                    goto("pleaseDon'tTellHerOptions")
                                }
                                op("I'm on it.") { player(HAPPY_TALKING, "I'm on it.") }
                            }
                        }
                    }

                    STAGE_NEED_TO_DELIVER_RING -> {
                        if (!getHasItem(player, DORORANS_ENGRAVED_RING)) {
                            player(SAD, "I seem to have lost the ring I engraved!")
                            npc(npc, WORRIED, "Oh no! You should ask Jeffery if he has another gold ring.") {
                                player.setQuestStage(Quest.GUNNARS_GROUND, STAGE_RECEIVED_RING)
                            }
                        } else {
                            npc(npc, CALM_TALK, "Please take the ring to Gudrun for me.")
                            label("pleaseTakeTheRingOptions")
                            options {
                                op("Where is she?") {
                                    player(CONFUSED, "Where is she?")
                                    npc(npc, CALM_TALK, "Inside the barbarian village.")
                                }
                                op("I'm on it.") { player(HAPPY_TALKING, "I'm on it.") }
                            }
                        }
                    }

                    STAGE_NEED_TO_TALK_TO_GUNTHOR, STAGE_RETURN_TO_GUDRUN_AFTER_GUNTHOR -> {
                        npc(npc, HAPPY_TALKING, "Did you give Gudrun the ring? What did she think?")
                        player(CALM_TALK, "She liked it, but there's a problem. I'm dealing with it.")
                        npc(npc, SAD, "Oh no!")
                    }

                    STAGE_RETURN_TO_DORORAN_TO_WRITE_POEM -> {
                        npc(npc, HAPPY_TALKING, "Did you give Gudrun the ring? What did she think? Did it capture her heart?")
                        player(SAD, "There's a problem.")
                        npc(npc, UPSET_SNIFFLE, "It's because I'm a dwarf, isn't it? Or because I'm a poet? I knew it! I'm completely worthless!")
                        options {
                            op("No, she liked the ring.") {
                                player(HAPPY_TALKING, "No, she liked the ring.")
                                npc(npc, AMAZED_MILD, "Oh! Then what's the problem?")
                                goto("mostCruelIsFate")
                            }
                            op("Would you be quiet for a moment?") {
                                player(FRUSTRATED, "Would you be quiet for a moment?")
                                npc(npc, SAD, "Sorry!")
                                goto("mostCruelIsFate")
                            }
                        }
                        label("mostCruelIsFate")
                        npc(npc, SAD, "Most cruel is fate! Most cruel! Why not?")
                        player(SKEPTICAL, "He's obsessed with the stories of his ancestors. He says his people are still at war.")
                        npc(npc, CONFUSED, "This village has stood for a hundred years!")
                        player(CALM_TALK, "I heard him arguing with one of the others. He says he honours his ancestors this way.")
                        npc(npc, AMAZED_MILD, "Really? Interesting.")
                        options {
                            op("Do you know a lot about the village's history?") {
                                player(SKEPTICAL_THINKING, "Do you know a lot about the village's history?")
                                npc(npc, CALM_TALK, "Not really. I talked with Hunding, who guards this tower here. An idea occurs to me, but it is hubris of the greatest magnitude.")
                                goto("whatIsIt")
                            }
                            op("What are we going to do?") {
                                player(FRUSTRATED, "What are we going to do?")
                                npc(npc, TALKING_ALOT, "An idea occurs to me, but it is hubris of the greatest magnitude.")
                                goto("whatIsIt")
                            }
                        }
                        label("whatIsIt")
                        player(CONFUSED, "What is it?")
                        npc(npc, SKEPTICAL_THINKING, "What if I wrote a poem? Forged a sweeping, historical epic? Crafted a tale to touch the chieftain's soul?")
                        player(SKEPTICAL, "Will that work?")
                        npc(npc, SKEPTICAL_THINKING, "To win the heart of my beloved from her father's iron grasp? It is worth it just to try!")
                        exec {
                            player.fadeScreen {
                                player.soundEffect(1723, false)
                                player.setQuestStage(Quest.GUNNARS_GROUND, STAGE_WRITING_THE_POEM)
                                setPoemStage(player, 0)
                                DororanD(player, npc)
                            }
                        }
                    }

                    STAGE_WRITING_THE_POEM -> {
                        when (getPoemStage(player)) {

                            0 -> {
                                exec {
                                    setPoemStage(player, 1)
                                    DororanPoemD(player, npc)
                                }
                            }

                            else -> {
                                exec { DororanPoemD(player, npc) }
                            }
                        }
                    }

                    STAGE_FINISHED_WRITING_POEM -> {
                        npc(npc, CHEERFUL, "At last! It's done! It's finished! My finest work! Thank you so much for your help!")
                        player(CALM_TALK, "Are you ready to present it to the chieftain?")
                        npc(npc, CHUCKLE, "What? No! I'm a writer, not a performer.")
                        npc(npc, CALM_TALK, "I think the chieftain would respond best to one of his people. Perhaps you could ask Gudrun to recite it to her father?")
                        item(GUNNARS_GROUND_POEM.id, "Dororan gives you the poem.") {
                            player.anim(ANIM_TAKE_ITEM)
                            player.inventory.addItem(GUNNARS_GROUND_POEM, true)
                            setHasItem(player, GUNNARS_GROUND_POEM, true)
                            player.questManager.setStage(Quest.GUNNARS_GROUND, STAGE_RECEIVED_GUNNARS_GROUND_POEM)
                        }
                        options {
                            op("I'll get right on it.") {
                                player(CALM_TALK, "I'll get right on it.")
                            }
                            op("This had better be the last time.") {
                                player(FRUSTRATED, "This had better be the last time.")
                            }
                        }
                    }

                    STAGE_RECEIVED_GUNNARS_GROUND_POEM -> {
                        if (!getHasItem(player, GUNNARS_GROUND_POEM)) {
                            player(SAD, "I seem to have misplaced the poem you gave me.")
                            npc(npc, CALM_TALK, "Luckily, I made a copy!")
                            item(GUNNARS_GROUND_POEM.id, "Dororan gives you a copy of the poem.") {
                                player.inventory.addItem(GUNNARS_GROUND_POEM, true)
                                setHasItem(player, GUNNARS_GROUND_POEM, true)
                            }
                        } else {
                            npc(npc, WORRIED, "My poem is terrible, isn't it? The chieftain will probably have me killed.")
                            options {
                                op("Everything will work out.") {
                                    player(CALM_TALK, "Everything will work out.")
                                }
                                op("I expect so.") {
                                    player(CHUCKLE, "I expect so.")
                                }
                            }
                        }
                    }

                    STAGE_POST_CUTSCENES -> {
                        npc(GUDRUN_POST_CUTSCENE, HAPPY_TALKING, "Papa was so impressed by Dororan's poem, he's made him the village poet!")
                        npc(npc, HAPPY_TALKING, "I'm more than a little surprised! He even gave me a house to live in!")
                        npc(GUDRUN_POST_CUTSCENE, HAPPY_TALKING, "Our people's tradition is that the tribe provides lodging for the poet.")
                        npc(npc, HAPPY_TALKING, "It's huge!")
                        npc(GUDRUN_POST_CUTSCENE, HAPPY_TALKING, "It's not in the village. It's east of here: across the river and north of the road on the way to Varrock. It's a big house with roses outside.")
                        npc(npc, HAPPY_TALKING, "I think Gunthor wants to keep me close, but not too close.")
                        npc(npc, HAPPY_TALKING, "Oh, I found something there for you! Whoever lived there before left a dozen pairs of boots in the attic.")
                        npc(npc, HAPPY_TALKING, "I picked out a pair for you to thank you for all your help.")
                        npc(npc, HAPPY_TALKING, "Underneath them all was this magic lamp. You should have it as well!")
                        npc(GUDRUN_POST_CUTSCENE, HAPPY_TALKING, "We're going to the new house. You should come and visit!")
                        npc(npc, HAPPY_TALKING, "Yes, we'll see you there!")
                        options {
                            op("I'll see you soon.") {
                                player(HAPPY_TALKING, "I'll see you soon.")
                                goto("questComplete")
                            }
                            op("I'll consider dropping in.") {
                                player(HAPPY_TALKING, "I'll consider dropping in.")
                                label("questComplete")
                                npc(npc, HAPPY_TALKING, "Goodbye!")
                                npc(GUDRUN_POST_CUTSCENE, HAPPY_TALKING, "Goodbye!") {
                                    exec {
                                        completeGunnarsGround(player)
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}