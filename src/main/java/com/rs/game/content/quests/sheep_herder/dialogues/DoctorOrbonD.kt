package com.rs.game.content.quests.sheep_herder.dialogues

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.sheep_herder.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item

class DoctorOrbonD (player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.SHEEP_HERDER)) {

                STAGE_UNSTARTED -> {
                    player(CALM_TALK, "Hello.")
                    npc(npc, TALKING_ALOT, "How do you feel? No heavy flu, shivers, nausea or loss of appetite?")
                    player(CONFUSED, "Uh... no... I feel fine...")
                    npc(npc, TALKING_ALOT, "How about nightmares then? Are you experiencing any problems with especially scary nightmares?")
                    player(CONFUSED, "No... not since I was young.")
                    npc(npc, WORRIED, "Good, good, I had to make sure. This plague really has an incredibly fast contagion rate. It spreads faster than the common cold!")
                    label("plagueOptions")
                    options {
                        op("The plague? Tell me more.") {
                            player(AMAZED_MILD, "The plague? Tell me more.")
                            npc(npc, TALKING_ALOT, "It came from the west somewhere. It is extremely quick spreading, and utterly deadly.")
                            player(WORRIED, "What are the symptoms?")
                            npc(npc, TALKING_ALOT, "It varies slightly from case to case, but usually abnormal nightmares and flu-like symptoms, progressing as you succumb more. " +
                                    "Eventually, it results in the presence of a thick black tar-like substance secreted from the nose and eyes.")
                            goto("plagueOptions")
                        }
                        op("Okay, I'll be careful.") {
                            player(HAPPY_TALKING, "Okay, I'll be careful.")
                            npc(npc, TALKING_ALOT, "You do that adventurer. You do not want to become infected by the plague.")
                        }
                    }
                }

                STAGE_RECEIVED_SHEEP_FEED -> {
                    player(CALM_TALK, "Hello doctor. I need to acquire some protective clothing so that I can dispose of some escaped sheep infected with the plague.")
                    npc(npc, TALKING_ALOT, "Protective clothing? I'm afraid I only have the one suit which I made myself to prevent infection from the contaminated patients I treat.")
                    npc(npc, TALKING_ALOT, "I suppose I could sell you this one and make myself another, but it would cost you at least 100 gold so that I could afford a replacement.")
                    label("clothingOptions")
                    options {
                        op("Sorry doc, that's too much.") {
                            player(WORRIED, "Sorry doc, that's too much.")
                            npc(npc, SAD, "I'm afraid it will cost me that much to replace: I cannot possibly sell it for less.")
                            if (player.inventory.hasCoins(100))
                                goto("clothingOptions")
                        }
                        if (player.inventory.hasCoins(100))
                            op("Ok, I'll take it.") {
                                simple("You give Doctor Orbon 100 coins. Doctor Orbon hands over a protective suit.") {
                                    player.inventory.removeCoins(100)
                                    player.inventory.addItem(Item(PLAGUE_JACKET), true)
                                    player.inventory.addItem(Item(PLAGUE_TROUSERS), true)
                                    player.questManager.setStage(Quest.SHEEP_HERDER, STAGE_RECEIVED_PROTECTIVE_CLOTHING)
                                }
                                npc(npc, HAPPY_TALKING, "These should protect you from infection.")
                            }
                    }
                }

                STAGE_RECEIVED_PROTECTIVE_CLOTHING -> {
                    player(CALM_TALK, "Hello again.")
                    npc(npc, SKEPTICAL, "Did you dispose of those infected sheep yet?")
                    player(SAD, "Not yet.")
                    npc(npc, WORRIED, "You MUST hurry! With the rate of infection I have documented in West Ardougne, they could infect the entire town in a matter of a few days!")
                    if ((player.equipment.getId(Equipment.LEGS) != PLAGUE_TROUSERS || player.equipment.getId(Equipment.CHEST) != PLAGUE_JACKET) && (!player.inventory.containsItems(Item(PLAGUE_JACKET), Item(PLAGUE_TROUSERS)))) {
                        npc(npc, SKEPTICAL_THINKING, "I notice you do not appear to have your protective clothing with you. Would you care to buy some more? Same price as before.")
                        options {
                            op("No, I don't need any more.") {
                                player(CALM_TALK, "No, I don't need any more.")
                                npc(npc, AMAZED_MILD, "I suggest you go and retrieve your protective suit then. Trust me, you do NOT want to be infected.")
                            }
                            if (player.inventory.hasCoins(100))
                                op("Ok, I'll take it.") {
                                    simple("You give Doctor Orbon 100 coins. Doctor Orbon hands over a protective suit.") {
                                        player.inventory.removeCoins(100)
                                        player.inventory.addItem(Item(PLAGUE_JACKET), true)
                                        player.inventory.addItem(Item(PLAGUE_TROUSERS), true)
                                        player.questManager.setStage(Quest.SHEEP_HERDER, STAGE_RECEIVED_PROTECTIVE_CLOTHING)
                                    }
                                    npc(npc, HAPPY_TALKING, "These should protect you from infection.")
                                }
                        }
                    }
                }

                STAGE_SHEEP_INCINERATED -> {
                    player(CALM_TALK, "Hello again.")
                    npc(npc, SKEPTICAL, "Did you dispose of those infected sheep yet?")
                    player(SAD, "Yes, I have.")
                    npc(npc, AMAZED_MILD, "Excellent work! You should let Councillor Halgrives know at once!")
                }

                STAGE_COMPLETE -> {
                npc(npc, HAPPY_TALKING, "Well hello again, I was so relieved when I heard that you had managed to dispose of the plagued sheep successfully.")
                npc(npc, HAPPY_TALKING, "It means that our quarantine is working again.")
            }

            }
        }
    }
}
