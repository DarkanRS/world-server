package com.rs.game.content.world.areas.varrock.npcs

import com.rs.engine.dialogue.Conversation
import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.Options
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.BLUE_MOON_INN
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.onBarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.Companion.hasCard
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

@PluginEventHandler
class BartenderBlueMoonInn(p: Player) : Conversation(p) {
    init {
        addNPC(BARTENDER_ID, HeadE.HAPPY_TALKING, "What can I do yer for?")

        addOptions(object : Options() {
            override fun create() {
                if (!isBarVisited(player, BLUE_MOON_INN) && hasCard(player) && onBarCrawl(player)) {
                    option("I'm doing Alfred Grimhand's Barcrawl.", Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "I'm doing Alfred Grimhand's Barcrawl. Do you have any ${BLUE_MOON_INN.drinkName}?")
                        .addNPC(BARTENDER_ID, HeadE.FRUSTRATED, "Oh no not another of you guys.")
                        .addNPC(BARTENDER_ID, HeadE.FRUSTRATED, "These barbarian barcrawls cause too much damage to my bar.")
                        .addNPC(BARTENDER_ID, HeadE.CALM, "You're going to have to pay me ${BLUE_MOON_INN.price} gold for the ${BLUE_MOON_INN.drinkName}.")
                        .addNext {
                            if (player.inventory.hasCoins(BLUE_MOON_INN.price)) {
                                player.startConversation(object : Conversation(player) {
                                    init {
                                        addNext {
                                                player.lock()
                                                player.schedule {
                                                    BLUE_MOON_INN.effect.message(player, start = true)
                                                    wait(2)
                                                    BLUE_MOON_INN.effect.effect(player)
                                                    wait(4)
                                                    BLUE_MOON_INN.effect.message(player, start = false)
                                                }
                                            }
                                    }
                                })
                            } else {
                                player.startConversation(object : Conversation(player) {
                                    init {
                                        addPlayer(HeadE.SAD_MILD_LOOK_DOWN, "I don't have that much money on me.")
                                        create()
                                    }
                                })
                            }
                        })
                }

                option("A glass of your finest ale please.", Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "A glass of your finest ale please.")
                    .addNPC(BARTENDER_ID, HeadE.HAPPY_TALKING, "No problemo. That'll be 2 coins.")
                    .addNext {
                        if (player.inventory.hasCoins(2)) {
                            player.inventory.removeCoins(2)
                            player.inventory.addItem(1917, 1)
                            player.startConversation(object : Conversation(player) {
                                init {
                                    addSimple("The bartender hands you a beer...")
                                    create()
                                }
                            })
                        } else {
                            player.startConversation(object : Conversation(player) {
                                init {
                                    addNPC(BARTENDER_ID, HeadE.SKEPTICAL_THINKING, "You have 2 coins don't you?")
                                    addPlayer(HeadE.SAD_MILD_LOOK_DOWN, "No..")
                                    addNPC(BARTENDER_ID, HeadE.FRUSTRATED, "That's too bad...")
                                    create()
                                }
                            })
                        }
                    })

                option("Can you recommend where an adventurer might make his fortune?", Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "Can you recommend where an adventurer might make his fortune?")
                    .addNPC(BARTENDER_ID, HeadE.HAPPY_TALKING, "Ooh I don't know if I should be giving away information, makes the game too easy.")
                    .addOptions("Choose an option:", object : Options() {
                        override fun create() {
                            option("Oh ah well...", Dialogue()
                                .addPlayer(HeadE.SAD_MILD, "Oh ah well..."))
                            option("Game? What are you talking about?", Dialogue()
                                .addPlayer(HeadE.SKEPTICAL_THINKING, "Game? What are you talking about?")
                                .addNPC(BARTENDER_ID, HeadE.TALKING_ALOT, "This world around us... is an online game... called RuneScape.")
                                .addPlayer(HeadE.SKEPTICAL_THINKING, "Nope, still don't understand what you are talking about. What does 'online' mean?")
                                .addNPC(BARTENDER_ID, HeadE.TALKING_ALOT, "It's a sort of connection between magic boxes across the world, big " + "boxes on people's desktops and little ones people can carry. They can talk to each other to play games.")
                                .addPlayer(HeadE.AMAZED_MILD, "I give up. You're obviously completely mad!"))
                            option("Just a small clue?", Dialogue()
                                .addPlayer(HeadE.HAPPY_TALKING, "Just a small clue?")
                                .addNPC(BARTENDER_ID, HeadE.HAPPY_TALKING, "Go and talk to the bartender at the Jolly Boar Inn, he doesn't " + "seem to mind giving away clues."))
                        }
                    }))

                option("Do you know where I can get some good equipment?", Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "Do you know where I can get some good equipment?")
                    .addNPC(BARTENDER_ID, HeadE.HAPPY_TALKING, "Well, there's the sword shop across the road, or there's also all sorts of " +
                            "shops up around the market.")
                )
            }
        })

        create()
    }

    companion object {
        const val BARTENDER_ID = 733

        @JvmStatic
        fun handleTalkTo(p: Player) {
            p.startConversation(BartenderBlueMoonInn(p))
        }
    }
}

@ServerStartupEvent
fun mapBartenderBlueMoonInn() {
    onNpcClick(BartenderBlueMoonInn.BARTENDER_ID) { e ->
        BartenderBlueMoonInn.handleTalkTo(e.player)
    }
}
