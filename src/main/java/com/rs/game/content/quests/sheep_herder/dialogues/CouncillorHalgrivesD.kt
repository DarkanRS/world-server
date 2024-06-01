package com.rs.game.content.quests.sheep_herder.dialogues

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.sheep_herder.completeSheepHerder
import com.rs.game.content.quests.sheep_herder.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item

class CouncillorHalgrivesD (player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.SHEEP_HERDER)) {
                STAGE_UNSTARTED -> {
                    player(CALM_TALK, "Hello. How are you?")
                    npc(npc, SAD, "I've been better.")
                    options {
                        op("What's wrong?") {
                            player(CONFUSED, "What's wrong?")
                            npc(npc, WORRIED, "You may or may not be aware, but a plague has spread across Western Ardougne. " +
                                    "Now, so far, our efforts to contain it have been largely successful, for the most part.")
                            npc(npc, WORRIED, "However, four sheep recently escaped from a farm near the city. " +
                                    "When they were found, we noticed that they were strangely discoloured, so we asked the mourners to examine them.")
                            npc(npc, WORRIED, "They found that the sheep had become infected with the plague.")
                            npc(npc, WORRIED, "As the councillor responsible for public health and safety here in East Ardougne I am looking for someone to herd " +
                                    "these sheep into a safe enclosure, kill them quickly and cleanly and then dispose of the remains hygienically")
                            npc(npc, WORRIED, "in a special incinerator.")
                            npc(npc, WORRIED, "Unfortunately nobody wants to risk catching the plague, and I am unable to find someone willing to " +
                                    "undertake this mission for me.")
                            questStart(Quest.SHEEP_HERDER)
                            player(HAPPY_TALKING, "I can do that for you.")
                            npc(npc, AMAZED_MILD, "Y-you will??? That is excellent news! Head to the enclosure we have set up on Farmer Brumty's land to the north of the city; " +
                                    "the four infected sheep should still be somewhere in that vicinity. Before you will be allowed to")
                            npc(npc, AMAZED_MILD, "enter the enclosure, however, you must ensure you have some kind of protective clothing to prevent contagion.")
                            player(CONFUSED, "Where can I find some protective clothing then?")
                            npc(npc, AMAZED_MILD, "Doctor Orbon wears it when conducting mercy missions to the infected parts of the city. " +
                                    "You should be able to find him in the chapel just north of here. Please also take this poisoned sheep feed; we believe poisoning the sheep will")
                            npc(npc, CALM_TALK, "minimise the risk of airborne contamination, and is of course also more humane to the sheep.")
                            simple("The councillor gives you some poisoned sheep feed.") {
                                player.inventory.addItem(Item(SHEEP_FEED), true)
                                player.setQuestStage(Quest.SHEEP_HERDER, STAGE_RECEIVED_SHEEP_FEED)
                            }
                            player(CONFUSED, "How will I know which sheep are infected?")
                            npc(npc, TALKING_ALOT, "The poor creatures have developed strangely discoloured wool and flesh. You should have no trouble spotting them.")
                        }
                        op("That's life for you.") {
                            player(CALM_TALK, "That's life for you.")
                            npc(npc, WORRIED, "That's what concerns me... There might not BE much life around here soon.")
                        }
                    }
                }

                STAGE_RECEIVED_SHEEP_FEED -> {
                    npc(npc, WORRIED, "Please... please find and dispose of those diseased sheep as quickly as possible. Every second the risk of contamination grows!")
                    if (player.inventory.containsOneItem(SHEEP_FEED)) {
                        player(CALM_TALK, "I'll do my best sir.")
                    } else {
                        player(CALM_TALK, "Some more sheep poison would be appreciated...")
                        npc(npc, CALM_TALK, "Certainly adventurer. Please hurry!") {
                            player.inventory.addItem(Item(SHEEP_FEED), true)
                        }
                    }
                }

                STAGE_RECEIVED_PROTECTIVE_CLOTHING -> {
                    npc(npc, WORRIED, "Have you managed to find and dispose of those four plague-bearing sheep yet?")
                    player(CALM_TALK, "Uh... yeah... not quite just yet...")
                    npc(npc, WORRIED, "Not quite's not good enough. It is vital you catch, kill, and incinerate all four sheep as quickly as possible. Each second they are free the risk of further contagion increases.")
                    player(CALM_TALK, "Ok, I'll get to it.")
                }

                STAGE_SHEEP_INCINERATED -> {
                    npc(npc, WORRIED, "Have you managed to find and dispose of those four plague-bearing sheep yet?")
                    player(HAPPY_TALKING, "Yes, I have.")
                    npc(npc, AMAZED_MILD, "Excellent work adventurer! Please let me reimburse you the 100 gold it cost you to purchase your protective clothing.")
                    npc(npc, AMAZED_MILD, "And in recognition of your service to the public health of Ardougne please accept this further 3000 coins as a reward.")
                    exec { completeSheepHerder(player) }
                }

                STAGE_COMPLETE -> {
                    player(HAPPY_TALKING, "Hello again sir.")
                    npc(npc, HAPPY_TALKING, "Well hello again adventurer! How are you today?")
                    player(HAPPY_TALKING, "Fine thank you. And yourself?")
                    npc(npc, SAD, "I'm okay. Sadly some more diseased sheep have appeared so I have them to deal with.")
                    player(HAPPY_TALKING, "Do you need me to help again?")
                    npc(npc, HAPPY_TALKING, "Thank you for the kind offer. You've done enough though. We'll find someone else to deal with these ones.")
                }

            }
        }
    }
}
