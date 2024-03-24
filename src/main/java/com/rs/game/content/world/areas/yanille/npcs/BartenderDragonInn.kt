package com.rs.game.content.world.areas.yanille.npcs

import com.rs.engine.dialogue.Conversation
import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.Options
import com.rs.game.content.miniquests.bar_crawl.BarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.DRAGON_INN
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.onBarCrawl
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

@PluginEventHandler
class BartenderDragonInn(p: Player, npc: NPC) : Conversation(p) {
    init {
        val name = npc.name
        addNPC(npc, HeadE.HAPPY_TALKING, "What can I get you?")
        addPlayer(HeadE.CALM, "What's on the menu?")
        addNPC(npc, HeadE.HAPPY_TALKING, "Dragon Bitter and Greenman's Ale, oh and some cheap beer.")

        addOptions(object : Options() {
            override fun create() {

                option("I'll give it a miss I think.", Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "I'll give it a miss I think.")
                    .addNPC(npc, HeadE.SAD_MILD, "Come back when you're a little thirstier."))

                option("I'll try the Dragon Bitter.", Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "I'll try the Dragon Bitter.")
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
                                .addNPC(npc.id, HeadE.SHAKING_HEAD, "You don't have the space for a pint of Dragon Bitter!")
                            )
                        }
                        else {
                            player.inventory.removeCoins(2)
                            player.inventory.addItem(1911)
                            player.sendMessage("You buy a pint of Dragon Bitter.")
                            player.startConversation(Dialogue()
                                .addNPC(npc.id, HeadE.HAPPY_TALKING, "There you go.")
                                .addPlayer(HeadE.HAPPY_TALKING, "Thanks, $name!")
                            )
                        }
                    }
                )

                option("Can I have some Greenman's Ale?", Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "Can I have some Greenman's Ale?")
                    .addNPC(npc, HeadE.HAPPY_TALKING, "Ok, that'll be ten coins.")
                    .addNext {
                        val hasCoins = player.inventory.hasCoins(10)
                        val hasSlots = player.inventory.hasFreeSlots()
                        if (!hasCoins) {
                            player.startConversation(Dialogue()
                                .addPlayer(HeadE.SAD, "Oh dear. I don't seem to have enough money.")
                            )
                        }
                        else if (!hasSlots) {
                            player.startConversation(Dialogue()
                                .addNPC(npc.id, HeadE.SHAKING_HEAD, "You don't have the space for a pint of Greenman's Ale!")
                            )
                        }
                        else {
                            player.inventory.removeCoins(10)
                            player.inventory.addItem(1901)
                            player.sendMessage("You buy a pint of Greenman's Ale.")
                            player.startConversation(Dialogue()
                                .addNPC(npc.id, HeadE.HAPPY_TALKING, "There you go.")
                                .addPlayer(HeadE.HAPPY_TALKING, "Thanks, $name!")
                            )
                        }
                    }
                )

                option("One cheap beer please!", Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "One cheap beer please!")
                    .addNPC(npc, HeadE.HAPPY_TALKING, "That'll be 2 gold coins please!")
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
                            player.sendMessage("You buy a pint of cheap beer.")
                            player.startConversation(Dialogue()
                                .addNPC(npc.id, HeadE.HAPPY_TALKING, "There you go. Have a super day!")
                                .addPlayer(HeadE.HAPPY_TALKING, "Thanks, $name!")
                            )
                        }
                    }
                )

                if (!isBarVisited(player, DRAGON_INN) && BarCrawl.hasCard(player) && onBarCrawl(player)) {
                    option("I'm doing Alfred Grimhand's Barcrawl.", Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "I'm doing Alfred Grimhand's Barcrawl.")
                        .addNPC(npc, HeadE.CHEERFUL, "I suppose you'll be wanting some ${DRAGON_INN.drinkName}. That'll cost you ${DRAGON_INN.price} coins.")
                        .addNext {
                            if (player.inventory.hasCoins(DRAGON_INN.price)) {
                                player.startConversation(object : Conversation(player) {
                                    init {
                                        addNext {
                                            player.lock()
                                            player.schedule {
                                                DRAGON_INN.effect.message(player, start = true)
                                                wait(2)
                                                DRAGON_INN.effect.effect(player)
                                                wait(4)
                                                DRAGON_INN.effect.message(player, start = false)
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
            p.startConversation(BartenderDragonInn(p, npc))
        }
    }
}

@ServerStartupEvent
fun mapBartenderDragonInn() {
    onNpcClick(739) { (player, npc) ->
        BartenderDragonInn.handleTalkTo(player, npc)
    }
}
