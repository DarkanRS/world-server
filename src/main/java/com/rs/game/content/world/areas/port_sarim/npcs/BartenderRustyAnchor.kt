package com.rs.game.content.world.areas.port_sarim.npcs

import com.rs.engine.dialogue.Conversation
import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.Options
import com.rs.engine.quest.Quest
import com.rs.game.content.miniquests.bar_crawl.BarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.RUSTY_ANCHOR
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.onBarCrawl
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

@PluginEventHandler
class BartenderRustyAnchor(p: Player, npc: NPC) : Conversation(p) {
    init {
        val name = npc.name
        addPlayer(HeadE.HAPPY_TALKING, "Good day to you!")
            .addNPC(npc, HeadE.HAPPY_TALKING, "Hello there!")

            .addOptions(object : Options() {
                override fun create() {

                    if (!isBarVisited(player, RUSTY_ANCHOR) && BarCrawl.hasCard(player) && onBarCrawl(player)) {
                        option("I'm doing Alfred Grimhand's Barcrawl.", Dialogue()
                            .addPlayer(HeadE.HAPPY_TALKING, "I'm doing Alfred Grimhand's Barcrawl.")
                            .addNPC(npc, HeadE.LAUGH, "Are you sure? You look a bit skinny for that.")
                            .addPlayer(HeadE.FRUSTRATED, "Just give me whatever I need to drink here.")
                            .addNPC(npc, HeadE.CHEERFUL, "Okay, one ${RUSTY_ANCHOR.drinkName} coming up. ${RUSTY_ANCHOR.price} coins, please.")
                            .addNext {
                                if (player.inventory.hasCoins(RUSTY_ANCHOR.price)) {
                                    player.startConversation(object : Conversation(player) {
                                        init {
                                            addNext {
                                                player.lock()
                                                player.schedule {
                                                    RUSTY_ANCHOR.effect.message(player, start = true)
                                                    wait(2)
                                                    RUSTY_ANCHOR.effect.effect(player)
                                                    wait(4)
                                                    RUSTY_ANCHOR.effect.message(player, start = false)
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

                    option("Could I buy a beer, please?", Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "Could I buy a beer, please?")
                        .addNPC(npc, HeadE.HAPPY_TALKING, "Sure, that will be two gold coins, please.")
                        .addNext {
                            val hasCoins = player.inventory.hasCoins(2)
                            val hasSlots = player.inventory.hasFreeSlots()
                            if (!hasCoins) {
                                player.startConversation(Dialogue()
                                    .addNPC(npc, HeadE.ANGRY, "I said 2 coins! You haven't got 2 coins!")
                                    .addPlayer(HeadE.SAD_SNIFFLE, "Sorry, I'll come back another day.")
                                )
                            }
                            else if (!hasSlots) {
                                player.startConversation(Dialogue()
                                    .addNPC(npc.id, HeadE.SHAKING_HEAD, "You don't have the space for a pint of beer!")
                                )
                            }
                            else {
                                player.inventory.removeCoins(2)
                                player.inventory.addItem(1917)
                                player.startConversation(Dialogue()
                                    .addPlayer(HeadE.HAPPY_TALKING, "Thanks, $name!")
                                )
                            }
                        }
                    )

                    option("Have you heard any rumours here?", Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "Have you heard any rumours here?")
                        .addNext {
                            if (!player.questManager.isComplete(Quest.GOBLIN_DIPLOMACY)) {
                                player.startConversation(Dialogue()
                                    .addNPC(npc, HeadE.CONFUSED, "Well, there was a guy in here earlier saying the goblins up by the mountain are arguing again, about the colour of their armour of all things.")
                                    .addNPC(npc, HeadE.WORRIED, "Knowing the goblins it could easily turn into a full blown war, which wouldn't be good. Goblin wars make such a mess of the countryside.")
                                    .addPlayer(HeadE.CALM, "Well if I have the time I'll go and see if I can knock some sense into them."))
                            } else {
                                player.startConversation(Dialogue()
                                    .addNPC(npc, HeadE.SHAKING_HEAD, "No, it hasn't been very busy lately."))
                            }
                        })

                    option("Bye, then.", Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "Bye, then.")
                        .addNPC(npc, HeadE.CALM, "Come back soon!"))

                }
            })

        create()
    }

    companion object {

        @JvmStatic
        fun handleTalkTo(p: Player, npc: NPC) {
            p.startConversation(BartenderRustyAnchor(p, npc))
        }
    }
}

@ServerStartupEvent
fun mapBartenderRustyAnchor() {
    onNpcClick(734) { (player, npc) ->
        BartenderRustyAnchor.handleTalkTo(player, npc)
    }
}
