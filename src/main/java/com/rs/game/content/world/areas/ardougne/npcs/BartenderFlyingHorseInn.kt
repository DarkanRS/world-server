package com.rs.game.content.world.areas.ardougne.npcs

import com.rs.engine.dialogue.Conversation
import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.Options
import com.rs.game.content.miniquests.bar_crawl.BarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.FLYING_HORSE_INN
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.onBarCrawl
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

@PluginEventHandler
class BartenderFlyingHorseInn(p: Player, npc: NPC) : Conversation(p) {
    init {
        val name = npc.name
        addNPC(npc, HeadE.HAPPY_TALKING, "Would you like to buy a drink?")
        addPlayer(HeadE.CALM, "What do you serve?")
        addNPC(npc, HeadE.CHEERFUL_EXPOSITION, "Beer!")

        addOptions(object : Options() {
            override fun create() {

                option("I'll have a beer then.", Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "I'll have a beer then.")
                    .addNPC(npc, HeadE.HAPPY_TALKING, "Ok, that'll be two coins.")
                    .addNext {
                        val hasCoins = player.inventory.hasCoins(2)
                        val hasSlots = player.inventory.hasFreeSlots()
                        if (!hasCoins) {
                            player.startConversation(Dialogue()
                                .addPlayer(HeadE.SAD, "Oh dear. I don't seem to have enough money.")
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
                                .addNPC(npc.id, HeadE.HAPPY_TALKING, "There you go.")
                                .addPlayer(HeadE.HAPPY_TALKING, "Thanks, $name!")
                            )
                        }
                    }
                )

                option("I'll not have anything then.", Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "I'll not have anything then.")
                )

                if (!isBarVisited(player, FLYING_HORSE_INN) && BarCrawl.hasCard(player) && onBarCrawl(player)) {
                    option("I'm doing Alfred Grimhand's Barcrawl.", Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "I'm doing Alfred Grimhand's Barcrawl.")
                        .addNPC(npc, HeadE.CHEERFUL, "Fancy a bit of ${FLYING_HORSE_INN.drinkName}. It'll only be ${FLYING_HORSE_INN.price} coins.")
                        .addNext {
                            if (player.inventory.hasCoins(FLYING_HORSE_INN.price)) {
                                player.startConversation(object : Conversation(player) {
                                    init {
                                        addNext {
                                            player.lock()
                                            player.schedule {
                                                FLYING_HORSE_INN.effect.message(player, start = true)
                                                wait(2)
                                                FLYING_HORSE_INN.effect.effect(player)
                                                wait(4)
                                                FLYING_HORSE_INN.effect.message(player, start = false)
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

        @JvmStatic
        fun handleTalkTo(p: Player, npc: NPC) {
            p.startConversation(BartenderFlyingHorseInn(p, npc))
        }
    }
}

@ServerStartupEvent
fun mapBartenderFlyingHorseInn() {
    onNpcClick(738) { (player, npc) ->
        BartenderFlyingHorseInn.handleTalkTo(player, npc)
    }
}
