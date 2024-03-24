package com.rs.game.content.world.areas.varrock.npcs

import com.rs.engine.dialogue.Conversation
import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.Options
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.JOLLY_BOAR_INN
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.onBarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.Companion.hasCard
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

@PluginEventHandler
class BartenderJollyBoarInn(p: Player) : Conversation(p) {
    init {
        addNPC(BARTENDER_ID, HeadE.HAPPY_TALKING, "Can I help you?")

        addOptions(object : Options() {
            override fun create() {

                option("I'll have a beer please.", Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "I'll have a pint of beer please.")
                    .addNPC(BARTENDER_ID, HeadE.HAPPY_TALKING, "Ok, that'll be two coins please.")
                    .addNext {
                        if (player.inventory.hasCoins(2)) {
                            player.inventory.removeCoins(2)
                            player.inventory.addItem(1917, 1)
                            player.startConversation(object : Conversation(player) {
                                init {
                                    addPlayer(HeadE.CHEERFUL, "Thanks, Bartender.")
                                    create()
                                }
                            })
                        } else {
                            player.startConversation(object : Conversation(player) {
                                init {
                                    addNPC(BARTENDER_ID, HeadE.SKEPTICAL_THINKING, "I said 2 coins! You haven't got 2 coins!")
                                    addPlayer(HeadE.SAD_MILD_LOOK_DOWN, "Sorry, I'll come back another day.")
                                    create()
                                }
                            })
                        }
                    })

                option("Any hints where I can go adventuring?", Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "Any hints on where I can go adventuring?")
                    .addNPC(BARTENDER_ID, HeadE.SKEPTICAL_THINKING, "Ooh, now. Let me see...")
                    .addNPC(BARTENDER_ID, HeadE.CHEERFUL, "Well there is the Varrock sewers. There are tales of untold horrors coming out at night and stealing babies from houses.")
                    .addPlayer(HeadE.CHEERFUL, "Sounds perfect! Where's the entrance?")
                    .addNPC(BARTENDER_ID, HeadE.CHEERFUL, "It's just to the east of the palace.")
                )

                option("Heard any good gossip?", Dialogue()
                    .addPlayer(HeadE.SECRETIVE, "Heard any gossip?")
                    .addNPC(BARTENDER_ID, HeadE.LAUGH, "I'm not that well up on the gossip out here. I've heard that the bartender in the Blue Moon Inn has gone a little crazy, he keeps claiming he is part of something called a computer game.")
                    .addNPC(BARTENDER_ID, HeadE.LAUGH, "What that means, I don't know. That's probably old news by now though.")

                )

                if (!isBarVisited(player, JOLLY_BOAR_INN) && hasCard(player) && onBarCrawl(player)) {
                    option("I'm doing Alfred Grimhand's Barcrawl.", Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "I'm doing Alfred Grimhand's Barcrawl.")
                        .addNPC(BARTENDER_ID, HeadE.CHEERFUL, "Ah, there seems to be a fair few doing that one these days. My supply of ${JOLLY_BOAR_INN.drinkName} is starting to run low, it'll cost you ${JOLLY_BOAR_INN.price} coins.")
                        .addNext {
                            if (player.inventory.hasCoins(JOLLY_BOAR_INN.price)) {
                                player.startConversation(object : Conversation(player) {
                                    init {
                                        addNext {
                                                player.lock()
                                                player.schedule {
                                                    JOLLY_BOAR_INN.effect.message(player, start = true)
                                                    wait(2)
                                                    JOLLY_BOAR_INN.effect.effect(player)
                                                    wait(4)
                                                    JOLLY_BOAR_INN.effect.message(player, start = false)
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

            }
        })

        create()
    }

    companion object {
        const val BARTENDER_ID = 731

        @JvmStatic
        fun handleTalkTo(p: Player) {
            p.startConversation(BartenderJollyBoarInn(p))
        }
    }
}

@ServerStartupEvent
fun mapBartenderJollyBoarInn() {
    onNpcClick(BartenderJollyBoarInn.BARTENDER_ID) { e ->
        BartenderJollyBoarInn.handleTalkTo(e.player)
    }
}
