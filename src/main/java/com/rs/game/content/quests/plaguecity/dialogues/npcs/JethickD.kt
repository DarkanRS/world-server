package com.rs.game.content.quests.plaguecity.dialogues.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.plaguecity.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class JethickD (player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.PLAGUE_CITY)) {

                in STAGE_UNSTARTED..STAGE_SPOKEN_TO_JETHICK -> {
                    npc(npc, SKEPTICAL, "Hello I don't recognise you. We don't get many newcomers around here.")
                    label("initialOptions")
                    options {
                        op("Hi, I'm looking for a woman from East Ardougne.") {
                            player(SKEPTICAL_THINKING, "Hi, I'm looking for a woman from East Ardougne called Elena.")
                            npc(npc, CONFUSED, "East Ardougnian women are easier to find in East Ardougne. Not many would come to West Ardougne to find one. Although the name is familiar, what does she look like?") { player.questManager.setStage(Quest.PLAGUE_CITY, STAGE_SPOKEN_TO_JETHICK) }
                            if (!player.inventory.containsOneItem(PICTURE_OF_ELENA)) {
                                player(SKEPTICAL_THINKING, "Um... brown hair... in her twenties...")
                                npc(npc, CONFUSED, "Hmmm, that doesn't narrow it down a huge amount... I'll need to know more than that, or see a picture?") { player.questManager.getAttribs(Quest.PLAGUE_CITY).setB(JETHICK_NEEDS_PICTURE, true) }
                            } else {
                                item(PICTURE_OF_ELENA, "You show Jethick the picture.")
                                npc(npc, CALM_TALK, "She came over here to help to aid plague victims. I think she is staying over with the Rehnison family. They live in the small timbered building at the far north side of town. I've not seen her in a while, mind.")
                                if (player.inventory.containsOneItem(BOOK_TURNIP_GROWING_FOR_BEGINNERS)) {
                                    npc(npc, CALM_TALK, "You were supposed to be returning a book to them for me.")
                                    player(CALM_TALK, "Yes, I've got it here.")
                                } else {
                                    npc(npc, SKEPTICAL_THINKING, "I don't suppose you could run me a little errand while you're over there? I borrowed this book from them. Can you return it?")
                                    options {
                                        op("Yes, I'll return it for you.") {
                                            player(CALM_TALK, "Yes, I'll return it for you.")
                                            if (player.inventory.hasFreeSlots())
                                                item(BOOK_TURNIP_GROWING_FOR_BEGINNERS, "Jethick gives you a book.") {
                                                    player.inventory.addItem(BOOK_TURNIP_GROWING_FOR_BEGINNERS)
                                                    player.questManager.getAttribs(Quest.PLAGUE_CITY).setB(JETHICK_RETURN_BOOK, true)
                                                }
                                            else item(BOOK_TURNIP_GROWING_FOR_BEGINNERS, "Jethick shows you the book, but you don't have room to take it from him.")
                                        }
                                        op("No, I don't have time for that.") { player(CALM_TALK, "No, I don't have time for that.") }
                                    }
                                }
                            }
                        }
                        op("So who's in charge here?") {
                            player(CONFUSED, "So who's in charge here?")
                            npc(npc, SAD, "Well King Tyras has wandered off into the west kingdom. He doesn't care about the mess he's left here.")
                            npc(npc, SKEPTICAL, "The city warder Bravek is in charge at the moment... He's not much better.")
                            goto("initialOptions")
                        }
                    }
                }

                in STAGE_GAVE_BOOK_TO_TED..STAGE_COMPLETE -> {
                    npc(npc, CALM_TALK, "Hello. We don't get many newcomers around here.")
                }

            }
        }
    }
}
