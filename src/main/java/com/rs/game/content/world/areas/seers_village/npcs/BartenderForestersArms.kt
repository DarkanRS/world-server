package com.rs.game.content.world.areas.seers_village.npcs

import com.rs.engine.dialogue.Conversation
import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.Options
import com.rs.game.content.miniquests.bar_crawl.BarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.FORESTERS_ARMS
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.onBarCrawl
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

@PluginEventHandler
class BartenderForestersArms(p: Player, npc: NPC) : Conversation(p) {
    init {
        val name = npc.name
        addNPC(npc, HeadE.HAPPY_TALKING, "Good morning, what would you like?")

        addOptions(object : Options() {
            override fun create() {

                option("What do you have?", Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "What do you have?")
                    .addNPC(npc, HeadE.HAPPY_TALKING, "Well we have beer, or if you want some food, we have our home made stew and meat pies.")
                    .addOptions(object : Options() {
                    override fun create() {
                        option("Beer please.", Dialogue()
                            .addPlayer(HeadE.HAPPY_TALKING, "Beer please.")
                            .addNPC(npc, HeadE.HAPPY_TALKING, "One beer coming up. Ok, that'll be two coins.")
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
                                    player.sendMessage("You buy a pint of beer.")
                                    player.startConversation(Dialogue()
                                        .addNPC(npc.id, HeadE.HAPPY_TALKING, "There you go.")
                                        .addPlayer(HeadE.HAPPY_TALKING, "Thanks, $name!")
                                    )
                                }
                            }
                        )

                        option("I'll try the meat pie.", Dialogue()
                            .addPlayer(HeadE.HAPPY_TALKING, "I'll try the meat pie.")
                            .addNPC(npc, HeadE.HAPPY_TALKING, "Ok, that'll be 16 coins.")
                            .addNext {
                                val hasCoins = player.inventory.hasCoins(16)
                                val hasSlots = player.inventory.hasFreeSlots()
                                if (!hasCoins) {
                                    player.startConversation(Dialogue()
                                        .addPlayer(HeadE.SAD, "Oh dear. I don't seem to have enough money.")
                                    )
                                }
                                else if (!hasSlots) {
                                    player.startConversation(Dialogue()
                                        .addNPC(npc.id, HeadE.SHAKING_HEAD, "You don't have the space for a meat pie!")
                                    )
                                }
                                else {
                                    player.inventory.removeCoins(16)
                                    player.inventory.addItem(2327)
                                    player.sendMessage("You buy a nice hot meat pie.")
                                    player.startConversation(Dialogue()
                                        .addNPC(npc.id, HeadE.HAPPY_TALKING, "There you go.")
                                        .addPlayer(HeadE.HAPPY_TALKING, "Thanks, $name!")
                                    )
                                }
                            }
                        )

                        option("Could I have some stew please?", Dialogue()
                            .addPlayer(HeadE.HAPPY_TALKING, "Could I have some stew please?")
                            .addNPC(npc, HeadE.HAPPY_TALKING, "A bowl of stew, that'll be 20 coins please.")
                            .addNext {
                                val hasCoins = player.inventory.hasCoins(20)
                                val hasSlots = player.inventory.hasFreeSlots()
                                if (!hasCoins) {
                                    player.startConversation(Dialogue()
                                        .addPlayer(HeadE.SAD, "Oh dear. I don't seem to have enough money.")
                                    )
                                }
                                else if (!hasSlots) {
                                    player.startConversation(Dialogue()
                                        .addNPC(npc.id, HeadE.SHAKING_HEAD, "You don't have the space for a bowl of stew!")
                                    )
                                }
                                else {
                                    player.inventory.removeCoins(20)
                                    player.inventory.addItem(2003)
                                    player.sendMessage("You buy a bowl of home made stew.")
                                    player.startConversation(Dialogue()
                                        .addNPC(npc.id, HeadE.HAPPY_TALKING, "There you go.")
                                        .addPlayer(HeadE.HAPPY_TALKING, "Thanks, $name!")
                                    )
                                }
                            }
                        )

                        option("I don't really want anything thanks.", Dialogue()
                            .addPlayer(HeadE.HAPPY_TALKING, "I don't really want anything thanks.")
                        )
                        }

                    })
                )

                option("Beer please.", Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "Beer please.")
                    .addNPC(npc, HeadE.HAPPY_TALKING, "One beer coming up. Ok, that'll be two coins.")
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
                            player.sendMessage("You buy a pint of beer.")
                            player.startConversation(Dialogue()
                                .addNPC(npc.id, HeadE.HAPPY_TALKING, "There you go.")
                                .addPlayer(HeadE.HAPPY_TALKING, "Thanks, $name!")
                            )
                        }
                    }
                )

                if (!isBarVisited(player, FORESTERS_ARMS) && BarCrawl.hasCard(player) && onBarCrawl(player)) {
                    option("I'm doing Alfred Grimhand's Barcrawl.", Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "I'm doing Alfred Grimhand's Barcrawl.")
                        .addNPC(npc, HeadE.CHEERFUL, "Oh you're a barbarian then. Now which of these barrels contained the ${FORESTERS_ARMS.drinkName}? That'll be ${FORESTERS_ARMS.price} coins please.")
                        .addNext {
                            if (player.inventory.hasCoins(FORESTERS_ARMS.price)) {
                                player.startConversation(object : Conversation(player) {
                                    init {
                                        addNext {
                                            player.lock()
                                            player.schedule {
                                                FORESTERS_ARMS.effect.message(player, start = true)
                                                wait(2)
                                                FORESTERS_ARMS.effect.effect(player)
                                                wait(4)
                                                FORESTERS_ARMS.effect.message(player, start = false)
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

                option("I don't really want anything thanks.", Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "I don't really want anything thanks.")
                )

            }
        })

        create()
    }

    companion object {

        @JvmStatic
        fun handleTalkTo(p: Player, npc: NPC) {
            p.startConversation(BartenderForestersArms(p, npc))
        }
    }
}

@ServerStartupEvent
fun mapBartenderForestersArms() {
    onNpcClick(737) { (player, npc) ->
        BartenderForestersArms.handleTalkTo(player, npc)
    }
}
