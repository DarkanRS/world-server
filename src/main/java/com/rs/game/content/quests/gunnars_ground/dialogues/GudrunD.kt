package com.rs.game.content.quests.gunnars_ground.dialogues

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.gunnars_ground.*
import com.rs.game.content.quests.gunnars_ground.cutscene.GunnarsGroundCutscenes
import com.rs.game.content.quests.gunnars_ground.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class GudrunD (player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.GUNNARS_GROUND)) {

                STAGE_NEED_TO_DELIVER_RING -> {
                    if (!getHasItem(player, DORORANS_ENGRAVED_RING)) {
                        player(SAD, "I don't seem to have the ring on me. Perhaps, I should go back to Dororan first!")
                    } else if (!player.containsOneItem(DORORANS_ENGRAVED_RING.id)) {
                        player(CALM_TALK, "I should fetch the engraved ring from the bank.")
                    } else {
                        npc(QUESTING_KJELL, ANGRY, "Gudrun! You caught enough fish?")
                        npc(npc, FRUSTRATED, "Yes! I have plenty of fish!")
                        npc(QUESTING_KJELL, ANGRY, "Your father needs many fish to feed the freemen!")
                        npc(npc, FRUSTRATED, "I know!")
                        npc(QUESTING_KJELL, ANGRY, "Maybe you should sneak off to the outerlander city again? Buy fish in market, instead of catching them?")
                        npc(npc, ANGRY, "Shut up! I'm much better at fishing than you.")
                        npc(QUESTING_KJELL, ANGRY, "You are not!")
                        npc(npc, FRUSTRATED, "Just guard the hut like the chieftain told you to!")
                        npc(QUESTING_KJELL, FRUSTRATED, "Fine!")
                        npc(npc, FRUSTRATED, "Stupid barbarian.")
                        npc(npc, CALM_TALK, "Sorry about that, stranger. Did you want something?")
                        player(CONFUSED, "Are you Gudrun?")
                        npc(npc, CALM_TALK, "Yes.")
                        player(CONFUSED, "This is for you.")
                        item(DORORANS_ENGRAVED_RING.id, "You show Gudrun the ring.") { player.anim(ANIM_GIVE_ITEM) }
                        npc(npc, HAPPY_TALKING, "It's lovely! There's something written on it:")
                        npc(npc, HAPPY_TALKING, "'Gudrun the Fair, Gudrun the Fiery.' Is it about me?")
                        options {
                            op("Yes.") {
                                player(CALM_TALK, "Yes.")
                                goto("beautifulGift")
                            }
                            op("Presumably.") {
                                player(CALM_TALK, "Presumably.")
                                goto("beautifulGift")
                            }
                        }
                        label("beautifulGift")
                        npc(npc, HAPPY_TALKING, "This is a beautiful gift, stranger. Thank you.")
                        label("ringFromOptions")
                        options {
                            op("This ring isn't from me!") {
                                player(CHUCKLE, "This ring isn't from me!")
                                goto("ohWhoIsItFrom")
                            }
                            op("It should belong to someone just as beautiful.") {
                                player(HAPPY_TALKING, "It should belong to someone just as beautiful.")
                                npc(npc, HAPPY_TALKING, "That's very flattering! You look like an adventurer, though?")
                                options {
                                    op("That's right.") {
                                        player(CALM_TALK, "That's right.")
                                        goto("couldNeverGetInvolved")
                                    }
                                    op("Some call me that.") {
                                        player(CHUCKLE, "Some call me that.")
                                        label("couldNeverGetInvolved")
                                        npc(npc, SAD, "I'm sorry, I could never get involved with an adventurer.")
                                        goto("ringFromOptions")
                                    }
                                }
                            }
                        }
                        label("ohWhoIsItFrom")
                        npc(npc, CONFUSED, "Oh! Who is it from?")
                        options {
                            op("A great poet.") {
                                player(CALM_TALK, "A great poet.")
                                npc(npc, HAPPY_TALKING, "A tale-teller? A bard? My people have great respect for poets.")
                                goto("thisMan")
                            }
                            op("A secret admirer.") {
                                player(CALM_TALK, "A secret admirer.")
                                npc(npc, HAPPY_TALKING, "Does that really happen? How exciting!")
                                goto("thisMan")
                            }
                            op("A short suitor.") {
                                player(CHUCKLE, "A short suitor.")
                                npc(npc, CONFUSED, "What?")
                                player(CHUCKLE, "A petite paramour.")
                                npc(npc, CONFUSED, "What?")
                                player(CHUCKLE, "A concise courter!")
                                goto("thisMan")
                            }
                        }
                        label("thisMan")
                        npc(npc, CONFUSED, "This man, he is from outside the village?")
                        player(CALM_TALK, "Yes.")
                        npc(npc, SAD, "I would love to leave the village and be romanced by exotic, handsome, outerlander men. There's a problem, though.")
                        player(CONFUSED, "What's that?")
                        npc(npc, SAD, "My papa, the chieftain. He would never let an outerlander pursue me.")
                        player(CONFUSED, "Why not?")
                        npc(npc, SAD, "He thinks all your people are our enemies.")
                        options {
                            op("So, you want me to talk to your father?") {
                                player(CALM_TALK, "So, you want me to talk to your father?")
                                npc(npc, SKEPTICAL_THINKING, "I suppose that might work.")
                                goto("triedToReason")
                            }
                            op("So, you want me to kill your father?") {
                                player(CALM_TALK, "So, you want me to kill your father?")
                                npc(npc, MORTIFIED, "What? No! Maybe...you could just try talking to him.")
                                goto("triedToReason")
                            }
                        }
                        label("triedToReason")
                        npc(npc, SAD, "I've tried to reason with him, but he's impossible! Maybe he'll listen to you. I know some of the others feel the same, but they're loyal to papa.") {
                            player.setQuestStage(Quest.GUNNARS_GROUND, STAGE_NEED_TO_TALK_TO_GUNTHOR)
                            player.inventory.deleteItem(DORORANS_ENGRAVED_RING)
                            setHasItem(player, DORORANS_ENGRAVED_RING, false)
                        }
                        label("fatherOptions")
                        options {
                            op("Where is he?") {
                                player(CALM_TALK, "Where is he?")
                                npc(npc, CALM_TALK, "In the longhouse at the north end of the village, drinking and shouting.")
                                goto("fatherOptions")
                            }
                            op("I'll see what I can do.") { player(CALM_TALK, "I'll see what I can do.") }
                        }
                    }
                }

                STAGE_NEED_TO_TALK_TO_GUNTHOR -> {
                    npc(npc, SAD, "If there's anything you can do to make papa see sense, please do it.")
                    label("fatherOptions")
                    options {
                        op("Where is he?") {
                            player(CALM_TALK, "Where is he?")
                            npc(npc, CALM_TALK, "In the longhouse at the north end of the village, drinking and shouting.")
                            goto("fatherOptions")
                        }
                        op("I'll see what I can do.") { player(CALM_TALK, "I'll see what I can do.") }
                    }
                }

                STAGE_RETURN_TO_GUDRUN_AFTER_GUNTHOR -> {
                    npc(npc, CALM_TALK, "What did he say?")
                    player(CALM_TALK, "He mentioned someone called Gunnar, and that you should think about his feelings.")
                    npc(npc, FRUSTRATED, "By the eyeballs of Guthix! Always Gunnar!")
                    options {
                        op("Who is Gunnar?") {
                            player(CONFUSED, "Who is Gunnar?")
                            npc(npc, CALM_TALK, "He was my great-grandpapa! He founded this village a hundred years ago.")
                            options {
                                op("You don't seem to share your father's attitude towards him.") {
                                    player(SKEPTICAL, "You don't seem to share your father's attitude towards him.")
                                    npc(npc, CHUCKLE, "I think there's a difference between respecting my ancestors and obsessing over them. Papa thinks whatever stupid war Gunnar fought is still going on.")
                                    goto("whatShouldWeDoNow")
                                }
                                op("What should we do now?") {
                                    player(CONFUSED, "What should we do now?")
                                    goto("whatShouldWeDoNow")
                                }
                            }
                        }
                        op("What should we do now?") {
                            label("whatShouldWeDoNow")
                            player(CONFUSED, "What should we do now?")
                            npc(npc, SKEPTICAL_THINKING, "I don't know. Maybe your mystery man has some ideas.")
                            player(CALM_TALK, "I'll ask him.") {
                                player.setQuestStage(Quest.GUNNARS_GROUND, STAGE_RETURN_TO_DORORAN_TO_WRITE_POEM)
                            }
                        }
                    }
                }

                STAGE_RETURN_TO_DORORAN_TO_WRITE_POEM, STAGE_WRITING_THE_POEM, STAGE_FINISHED_WRITING_POEM -> {
                    npc(npc, TALKING_ALOT, "If there's anything you can do to make papa see sense, please do it.")
                }

                STAGE_RECEIVED_GUNNARS_GROUND_POEM -> {
                    if (!getHasItem(player, GUNNARS_GROUND_POEM)) {
                        player(SAD, "I don't seem to have the poem on me. I should see if Dororan has a spare copy!")
                    } else if (!player.containsOneItem(GUNNARS_GROUND_POEM.id)) {
                        player(CALM_TALK, "I should fetch the poem from the bank.")
                    } else {
                        npc(npc, CONFUSED, "What have you got there?")
                        player(HAPPY_TALKING, "Another gift from your mysterious suitor.")
                        npc(npc, CONFUSED, "A scroll?")
                        player(HAPPY_TALKING, "It's a poem; a story to convince your father to settle down. You could recite it to him.")
                        npc(npc, AMAZED, "Let me see that.")
                        item(GUNNARS_GROUND_POEM.id, "You show Gudrun the poem.") { player.anim(ANIM_GIVE_ITEM) }
                        npc(npc, HAPPY_TALKING, "'Gunnar's Ground.'")
                        npc(npc, HAPPY_TALKING, "Yes! I think this could work. I'll go to the longhouse right away!")
                        exec {
                            GunnarsGroundCutscenes(player)
                            player.inventory.removeItems(GUNNARS_GROUND_POEM)
                            setHasItem(player, GUNNARS_GROUND_POEM, false)
                        }
                    }
                }

                STAGE_POST_CUTSCENES -> {
                    npc(npc, HAPPY_TALKING, "Papa was so impressed by Dororan's poem, he's made him the village poet!")
                    npc(DORORAN_POST_CUTSCENE, HAPPY_TALKING, "I'm more than a little surprised! He even gave me a house to live in!")
                    npc(npc, HAPPY_TALKING, "Our people's tradition is that the tribe provides lodging for the poet.")
                    npc(DORORAN_POST_CUTSCENE, HAPPY_TALKING, "It's huge!")
                    npc(npc, HAPPY_TALKING, "It's not in the village. It's east of here: across the river and north of the road on the way to Varrock. It's a big house with roses outside.")
                    npc(DORORAN_POST_CUTSCENE, HAPPY_TALKING, "I think Gunthor wants to keep me close, but not too close.")
                    npc(DORORAN_POST_CUTSCENE, HAPPY_TALKING, "Oh, I found something there for you! Whoever lived there before left a dozen pairs of boots in the attic.")
                    npc(DORORAN_POST_CUTSCENE, HAPPY_TALKING, "I picked out a pair for you to thank you for all your help.")
                    npc(DORORAN_POST_CUTSCENE, HAPPY_TALKING, "Underneath them all was this magic lamp. You should have it as well!")
                    npc(npc, HAPPY_TALKING, "We're going to the new house. You should come and visit!")
                    npc(DORORAN_POST_CUTSCENE, HAPPY_TALKING, "Yes, we'll see you there!")
                    options {
                        op("I'll see you soon.") {
                            player(HAPPY_TALKING, "I'll see you soon.")
                            goto("questComplete")
                        }
                        op("I'll consider dropping in.") {
                            player(HAPPY_TALKING, "I'll consider dropping in.")
                            label("questComplete")
                            npc(npc, HAPPY_TALKING, "Goodbye!")
                            npc(DORORAN_POST_CUTSCENE, HAPPY_TALKING, "Goodbye!") {
                                exec {
                                    completeGunnarsGround(player)
                                }
                            }
                        }
                    }
                }

                else -> {
                    npc(npc, SKEPTICAL, "Can I help you, stranger?")
                    npc(QUESTING_KJELL, ANGRY, "Why are you talking to that outerlander?")
                    npc(QUESTING_GUDRUN, ANGRY, "It's none of your business, Kjell! Just guard the hut!")
                    npc(QUESTING_GUDRUN, CALM_TALK, "Sorry about that. Did you want something?")
                    label("didYouWantSomethingOptions")
                    options {
                        op("What is this place?") {
                            player(CALM_TALK, "What is this place?")
                            npc(npc, SKEPTICAL, "Outerlanders call this the barbarian village. It doesn't have a name because... it's complicated.")
                            npc(npc, CALM_TALK, "If you wish to know more, you should talk to Hunding. He's up in the tower at the east entrance...")
                            goto("didYouWantSomethingOptions")
                        }
                        op("Who are you?") {
                            player(CALM_TALK, "Who are you?")
                            npc(npc, CALM_TALK, "My name is Gudrun. My father, Gunthor, is chieftain of the village.")
                            goto("didYouWantSomethingOptions")
                        }
                        op("Goodbye.") {
                            player(CALM_TALK, "Goodbye.")
                            npc(npc, CALM_TALK, "Goodbye.")
                        }
                    }
                }
            }
        }
    }
}
