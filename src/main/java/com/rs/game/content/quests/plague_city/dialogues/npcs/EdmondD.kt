package com.rs.game.content.quests.plague_city.dialogues.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.plague_city.cutscene.PlagueCityCutscene
import com.rs.game.content.quests.plague_city.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class EdmondD (player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.PLAGUE_CITY)) {

                STAGE_UNSTARTED -> {
                    player(CALM_TALK, "Hello old man.")
                    npc(npc, WORRIED, "Sorry, I can't stop to talk...")
                    player(CONFUSED, "Why, what's wrong?")
                    npc(npc, WORRIED, "I've got to find my daughter. I pray that she is still alive...")
                    options {
                        op("What's happened to her?") {
                            player(CONFUSED, "What's happened to her?")
                            npc(npc, WORRIED, "Elena's a missionary and a healer. Three weeks ago she managed to cross the Ardougne wall... No-one's allowed to cross the wall in case they spread the plague. But after hearing the screams of suffering she felt she had to help.")
                            npc(npc, WORRIED, "She said she'd be gone for a few days but we've heard nothing since.")
                            questStart(Quest.PLAGUE_CITY)
                            player(SKEPTICAL_THINKING, "Tell me more about the plague.")
                            npc(npc, WORRIED, "The mourners can tell you more than me. They're the only ones allowed to cross the border. I do know the plague is a horrible way to go... That's why Elena felt she had to go help.")
                            player(SKEPTICAL_THINKING, "Can I help find her?")
                            npc(npc, AMAZED_MILD, "Really, would you? I've been working on a plan to get into West Ardougne, but I'm too old and tired to carry it through. If you're going into West Ardougne you'll need protection from the plague. My wife made a special")
                            npc(npc, AMAZED_MILD, "gasmask for Elena with dwellberries rubbed into it. Dwellberries help repel the virus! We need some more though...")
                            player(CONFUSED, "Where can I find these Dwellberries?")
                            npc(npc, SKEPTICAL_THINKING, "The only place I know of is McGrubor's Wood just north of the Rangers' Guild.")
                            player(CALM_TALK, "Ok, I'll go and get some.")
                            npc(npc, CALM_TALK, "The foresters keep a close eye on it, but there is a back way in.")
                            exec { player.questManager.setStage(Quest.PLAGUE_CITY, STAGE_SPEAK_TO_ALRENA) }

                        }
                        op("Well, good luck finding her.") { player(CALM_TALK, "Well, good luck finding her.") }
                    }
                }

                STAGE_SPEAK_TO_ALRENA -> {
                    player(HAPPY_TALKING, "Hello Edmond.")
                    npc(npc, CALM_TALK, "Have you got the dwellberries yet?")
                    if (player.inventory.containsItems(DWELLBERRIES)) {
                        player(HAPPY_TALKING, "Yes I've got some here.")
                        npc(npc, HAPPY_TALKING, "Take them to my wife Alrena, she's inside.")
                    } else {
                        player(SAD, "Sorry, I'm afraid not.")
                        npc(npc, CALM_TALK, "You'll probably find them in McGrubor's Wood it's just west of Seers village.")
                        player(CALM_TALK, "Ok, I'll go and get some.")
                        npc(npc, CALM_TALK, "The foresters keep a close eye on it, but there is a back way in.")
                    }
                }

                STAGE_RECEIVED_GAS_MASK -> {
                    player(HAPPY_TALKING, "Hi Edmond, I've got the gasmask now.")
                    npc(npc, CALM_TALK, "Good stuff, now for the digging. Beneath us are the Ardougne sewers, there you'll find access to West Ardougne.")
                    npc(npc, CALM_TALK, "The problem is the soil is rock hard. You'll need to pour on several buckets of water to soften it up. I'll keep an eye out for the mourners.")
                    exec { player.questManager.setStage(Quest.PLAGUE_CITY, STAGE_PREPARE_TO_DIG) }
                }

                STAGE_PREPARE_TO_DIG -> {
                    npc(npc, CALM_TALK, "How's it going?")
                    player(HAPPY_TALKING, "I still need to pour more buckets of water on the soil.")
                }

                STAGE_CAN_DIG -> {
                    player(HAPPY_TALKING, "I've soaked the soil with water.")
                    npc(npc, HAPPY_TALKING, "That's great, it should be soft enough to dig through now.")
                }

                STAGE_UNCOVERED_SEWER_ENTRANCE -> {
                    npc(npc, CALM_TALK, "I think it's the pipe at the south end of the sewer that comes up in West Ardougne.")
                    player(CALM_TALK, "Alright I'll check it out.")
                    npc(npc, CALM_TALK, "Once you're in the city look for a man called Jethick, he's an old friend and should help you. Send him my regards, I haven't seen him since before Elena was born.")
                    player(CALM_TALK, "Alright, thanks I will.")
                }

                STAGE_NEED_HELP_WITH_GRILL -> {
                    player(CALM_TALK, "Edmond, I can't get through to West Ardougne! There's an iron grill blocking my way, I can't pull it off alone.")
                    npc(npc, CALM_TALK, "If you get some rope you could tie to the grill, then we could both pull it at the same time.") { player.questManager.getAttribs(Quest.PLAGUE_CITY).setB(EDMOND_SUGGESTED_ROPE, true) }
                }

                STAGE_ROPE_TIED_TO_GRILL -> {
                    player(CALM_TALK, "I've tied a rope to the grill in the sewer, will you help me pull it off?")
                    npc(npc, CALM_TALK, "Alright, let's get to it...")
                    exec { PlagueCityCutscene(player) }
                }

                STAGE_GRILL_REMOVED -> {
                    player(CALM_TALK, "Hello.")
                    npc(npc, CALM_TALK, "Have you found Elena yet?")
                    player(CALM_TALK, "Not yet, it's a big city over there.")
                    npc(npc, CALM_TALK, "Don't forget to look for my friend Jethick. He may be able to help.")
                }

                in STAGE_SPOKEN_TO_JETHICK..STAGE_GAVE_HANGOVER_CURE -> {
                    player(CALM_TALK, "Hello.")
                    npc(npc, CALM_TALK, "Have you found Elena yet?")
                    player(CALM_TALK, "Not yet, it's a big city over there.")
                    npc(npc, WORRIED, "I hope it's not too late.")
                }

                STAGE_FREED_ELENA -> {
                    npc(npc, AMAZED, "Thank you, thank you! Elena beat you back by minutes. Now I said I'd give you a reward. What can I give you as a reward I wonder? Here take this magic scroll, I have little use for it but it may help you.")
                    exec { player.questManager.completeQuest(Quest.PLAGUE_CITY) }
                }

                STAGE_COMPLETE -> {
                    val hasMagicScroll = player.inventory.containsOneItem(A_MAGIC_SCROLL) || player.bank.containsItem(A_MAGIC_SCROLL)
                    val hasReadMagicScroll = player.getBool(ARDOUGNE_TELEPORT_UNLOCKED)
                    npc(npc, HAPPY_TALKING, "Ah hello again and thank you again for rescuing my daughter.")
                    if (hasReadMagicScroll || hasMagicScroll) {
                        player(HAPPY_TALKING, "No problem.")
                    } else if (!hasMagicScroll) {
                        options {
                            op("Do you have any more of those scrolls?") {
                                player(CALM_TALK, "Do you have any more of those scrolls?")
                                if (player.inventory.hasFreeSlots())
                                    npc(npc, CALM_TALK, "Yes, here you go.") { player.inventory.addItem(A_MAGIC_SCROLL) }
                                else
                                    player(SAD, "Actually, never mind. I don't have enough room to take one.")
                            }
                            op("No problem.") {
                                player(HAPPY_TALKING, "No problem.")
                            }
                        }
                    }
                }

            }
        }
    }
}
