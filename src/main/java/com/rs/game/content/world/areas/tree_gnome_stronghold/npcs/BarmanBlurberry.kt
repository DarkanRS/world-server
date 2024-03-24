package com.rs.game.content.world.areas.tree_gnome_stronghold.npcs

import com.rs.engine.dialogue.Conversation
import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.Options
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.BLURBERRYS_BAR
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.onBarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.Companion.hasCard
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick
import com.rs.utils.shop.ShopsHandler

@PluginEventHandler
class BarmanBlurberry(p: Player, npc: NPC) : Conversation(p) {
    init {
        addNPC(npc, HeadE.HAPPY_TALKING, "Good day to you. What can I get you to drink?")

        addOptions(object : Options() {
            override fun create() {
                if (!isBarVisited(player, BLURBERRYS_BAR) && hasCard(player) && onBarCrawl(player)) {
                    option("I'm trying to do Alfred Grimhand's Barcrawl.", Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "I'm trying to do Alfred Grimhand's barcrawl.")
                        .addNPC(npc, HeadE.FRUSTRATED, "Oh, another silly human come to have his mind melted? You should take that barcrawl card to Blurberry - he always likes to serve the ${BLURBERRYS_BAR.drinkName} himself!")
                        .addPlayer(HeadE.FRUSTRATED, "Um... thanks?!")
                    )
                }

                option("What do you have?", Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "What do you have?")
                    .addNPC(npc, HeadE.HAPPY_TALKING, "Here, take a look at our menu.")
                    .addNext {
                        ShopsHandler.openShop(player, "blurberry_bar")
                    })

                option("Can I buy some ingredients?", Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "I was just wanting to buy a cocktail ingredient actually.")
                    .addNPC(npc, HeadE.HAPPY_TALKING, "Sure thing, what did you want?")
                    .addNext {
                        purchaseIngredients(player, npc)
                    })
            }
        })

        create()
    }

    fun purchaseIngredients(player: Player, npc: NPC) {
        val hasCoins = player.inventory.hasCoins(20)
        val hasSlots = player.inventory.hasFreeSlots()
        val name = npc.name
        val dialogue = Dialogue()
            .addOptions { ops: Options ->
                ops.add("A lemon.")
                    .addPlayer(HeadE.HAPPY_TALKING, "A lemon.")
                    .addNPC(npc, HeadE.HAPPY_TALKING, "20 coins please.")
                    .addNext {
                        if (!hasCoins) {
                            player.startConversation(Dialogue()
                                .addNPC(npc, HeadE.ANGRY, "I said 20 coins! You haven't got 20 coins!")
                            )
                        } else if (!hasSlots) {
                            player.startConversation(Dialogue()
                                .addNPC(npc, HeadE.SHAKING_HEAD, "You don't have the space for a lemon!")
                            )
                        } else {
                            player.startConversation(Dialogue()
                                .addNPC(npc, HeadE.HAPPY_TALKING, "There you go.")
                                .addNext {
                                    player.inventory.removeCoins(20)
                                    player.inventory.addItem(2102)
                                }
                                .addPlayer(HeadE.HAPPY_TALKING, "Thanks, $name")
                            )
                        }
                    }
                ops.add("An orange.")
                    .addPlayer(HeadE.HAPPY_TALKING, "An orange.")
                    .addNPC(npc, HeadE.HAPPY_TALKING, "20 coins please.")
                    .addNext {
                        if (!hasCoins) {
                            player.startConversation(Dialogue()
                                .addNPC(npc, HeadE.ANGRY, "I said 20 coins! You haven't got 20 coins!")
                            )
                        } else if (!hasSlots) {
                            player.startConversation(Dialogue()
                                .addNPC(npc, HeadE.SHAKING_HEAD, "You don't have the space for an orange!")
                            )
                        } else {
                            player.startConversation(Dialogue()
                                .addNPC(npc, HeadE.HAPPY_TALKING, "There you go.")
                                .addNext {
                                    player.inventory.removeCoins(20)
                                    player.inventory.addItem(2108)
                                }
                                .addPlayer(HeadE.HAPPY_TALKING, "Thanks, $name")
                            )
                        }
                    }
                ops.add("A cocktail shaker.")
                    .addPlayer(HeadE.HAPPY_TALKING, "A cocktail shaker.")
                    .addNPC(npc, HeadE.HAPPY_TALKING, "20 coins please.")
                    .addNext {
                        if (!hasCoins) {
                            player.startConversation(Dialogue()
                                .addNPC(npc, HeadE.ANGRY, "I said 20 coins! You haven't got 20 coins!")
                            )
                        } else if (!hasSlots) {
                            player.startConversation(Dialogue()
                                .addNPC(npc, HeadE.SHAKING_HEAD, "You don't have the space for a cocktail shaker!")
                            )
                        } else {
                            player.startConversation(Dialogue()
                                .addNPC(npc, HeadE.HAPPY_TALKING, "There you go.")
                                .addNext {
                                    player.inventory.removeCoins(20)
                                    player.inventory.addItem(2025)
                                }
                                .addPlayer(HeadE.HAPPY_TALKING, "Thanks, $name")
                            )
                        }
                    }
                ops.add("Nothing thanks.")
                    .addPlayer(HeadE.HAPPY_TALKING, "Actually nothing thanks.")
            }
        player.startConversation(dialogue)
    }

    companion object {

        @JvmStatic
        fun handleTalkTo(p: Player, npc: NPC) {
            p.startConversation(BarmanBlurberry(p, npc))
        }
    }
}

@ServerStartupEvent
fun mapBarmanBlurberry() {
    onNpcClick(849) { (player, npc) ->
        BarmanBlurberry.handleTalkTo(player, npc)
    }
}
