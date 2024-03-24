package com.rs.game.content.world.areas.karamja.npcs

import com.rs.engine.dialogue.Conversation
import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.Options
import com.rs.game.content.miniquests.bar_crawl.BarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.DEAD_MANS_CHEST
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.onBarCrawl
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

@PluginEventHandler
class BartenderDeadMansChest(p: Player, npc: NPC) : Conversation(p) {
    init {
        val name = npc.name
        addNPC(npc, HeadE.HAPPY_TALKING, "Yohoho me hearty what would you like to drink?")

        addOptions(object : Options() {
            override fun create() {

                option("Nothing, thank you.", Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "Nothing, thank you."))

                option("A pint of Grog please.", Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "A pint of Grog please.")
                    .addNPC(npc, HeadE.HAPPY_TALKING, "One grog coming right up, that'll be three coins.")
                    .addNext {
                        val hasCoins = player.inventory.hasCoins(3)
                        val hasSlots = player.inventory.hasFreeSlots()
                        if (!hasCoins) {
                            player.startConversation(Dialogue()
                                .addPlayer(HeadE.SAD, "Oh dear. I don't seem to have enough money.")
                            )
                        }
                        else if (!hasSlots) {
                            player.startConversation(Dialogue()
                                .addNPC(npc.id, HeadE.SHAKING_HEAD, "You don't have the space for grog!")
                            )
                        }
                        else {
                            player.inventory.removeCoins(3)
                            player.inventory.addItem(1915)
                            player.startConversation(Dialogue()
                                .addNPC(npc.id, HeadE.HAPPY_TALKING, "There you go.")
                                .addPlayer(HeadE.HAPPY_TALKING, "Thanks, $name!")
                            )
                        }
                    }
                )

                option("A bottle of rum please.", Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "A bottle of rum please.")
                    .addNPC(npc, HeadE.HAPPY_TALKING, "That'll be 27 coins.")
                    .addNext {
                        val hasCoins = player.inventory.hasCoins(27)
                        val hasSlots = player.inventory.hasFreeSlots()
                        if (!hasCoins) {
                            player.startConversation(Dialogue()
                                .addPlayer(HeadE.SAD, "Oh dear. I don't seem to have enough money.")
                            )
                        }
                        else if (!hasSlots) {
                            player.startConversation(Dialogue()
                                .addNPC(npc.id, HeadE.SHAKING_HEAD, "You don't have the space for a bottle of rum!")
                            )
                        }
                        else {
                            player.inventory.removeCoins(27)
                            player.inventory.addItem(431)
                            player.startConversation(Dialogue()
                                .addNPC(npc.id, HeadE.HAPPY_TALKING, "There you go.")
                                .addPlayer(HeadE.HAPPY_TALKING, "Thanks, $name!")
                            )
                        }
                    }
                )

                if (!isBarVisited(player, DEAD_MANS_CHEST) && BarCrawl.hasCard(player) && onBarCrawl(player)) {
                    option("I'm doing Alfred Grimhand's Barcrawl.", Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "I'm doing Alfred Grimhand's Barcrawl.")
                        .addNPC(npc, HeadE.CHEERFUL, "Haha time to be breaking out the old ${DEAD_MANS_CHEST.drinkName}. That'll be ${DEAD_MANS_CHEST.price} coins please.")
                        .addNext {
                            if (player.inventory.hasCoins(DEAD_MANS_CHEST.price)) {
                                player.startConversation(object : Conversation(player) {
                                    init {
                                        addNext {
                                            player.lock()
                                            player.schedule {
                                                DEAD_MANS_CHEST.effect.message(player, start = true)
                                                wait(2)
                                                DEAD_MANS_CHEST.effect.effect(player)
                                                wait(4)
                                                DEAD_MANS_CHEST.effect.message(player, start = false)
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
            p.startConversation(BartenderDeadMansChest(p, npc))
        }
    }
}

@ServerStartupEvent
fun mapBartenderDeadMansChest() {
    onNpcClick(735) { (player, npc) ->
        BartenderDeadMansChest.handleTalkTo(player, npc)
    }
}
