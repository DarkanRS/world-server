package com.rs.game.content.world.areas.tree_gnome_stronghold.npcs

import com.rs.engine.dialogue.*
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.BLURBERRYS_BAR
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.onBarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.Companion.hasCard
import com.rs.game.content.utils.BartenderUtils.buyDrinkOrIngredients
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick
import com.rs.utils.shop.ShopsHandler

class BarmanBlurberry(p: Player, npc: NPC) {
    init {
        p.startConversation {
            npc(npc, HeadE.HAPPY_TALKING, "Good day to you. What can I get you to drink?")
            options {
                if (!isBarVisited(p, BLURBERRYS_BAR) && hasCard(p) && onBarCrawl(p)) {
                    op("I'm trying to do Alfred Grimhand's Barcrawl.") {
                        player(HeadE.HAPPY_TALKING, "I'm trying to do Alfred Grimhand's barcrawl.")
                        npc(npc, HeadE.FRUSTRATED, "Oh, another silly human come to have his mind melted? You should take that barcrawl card to Blurberry - he always likes to serve the ${BLURBERRYS_BAR.drinkName} himself!")
                        player(HeadE.FRUSTRATED, "Um... thanks?!")
                    }
                }
                op("What do you have?") {
                    player(HeadE.HAPPY_TALKING, "What do you have?")
                    npc(npc, HeadE.HAPPY_TALKING, "Here, take a look at our menu.")
                    exec { ShopsHandler.openShop(p, "blurberry_bar") }
                }
                op("Can I buy some ingredients?") {
                    player(HeadE.HAPPY_TALKING, "I was just wanting to buy a cocktail ingredient actually.")
                    npc(npc, HeadE.HAPPY_TALKING, "Sure thing, what did you want?")
                    exec { purchaseIngredients(p, npc) }
                }
            }
        }
    }

    private fun purchaseIngredients(p: Player, npc: NPC) {
        p.startConversation {
            options {
                op("A lemon.") {
                    player(HeadE.HAPPY_TALKING, "A lemon.")
                    npc(npc, HeadE.HAPPY_TALKING, "20 coins please.")
                    exec { buyDrinkOrIngredients(p, npc, 20, Item(2102), false) }
                }
                op("An orange.") {
                    player(HeadE.HAPPY_TALKING, "An orange.")
                    npc(npc, HeadE.HAPPY_TALKING, "20 coins please.")
                    exec { buyDrinkOrIngredients(p, npc, 20, Item(2108), false) }
                }
                op("A cocktail shaker.") {
                    player(HeadE.HAPPY_TALKING, "A cocktail shaker.")
                    npc(npc, HeadE.HAPPY_TALKING, "20 coins please.")
                    exec { buyDrinkOrIngredients(p, npc, 20, Item(2025), false) }
                }
                op("Nothing thanks.") { player(HeadE.HAPPY_TALKING, "Actually nothing thanks.") }
            }
        }
    }
}

@ServerStartupEvent
fun mapBarmanBlurberry() {
    onNpcClick(849) { (player, npc) ->
        BarmanBlurberry(player, npc)
    }
}
