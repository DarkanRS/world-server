package com.rs.game.content.world.areas.karamja.npcs

import com.rs.engine.dialogue.*
import com.rs.game.content.miniquests.bar_crawl.BarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.DEAD_MANS_CHEST
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.onBarCrawl
import com.rs.game.content.utils.BartenderUtils.buyBarcrawlDrink
import com.rs.game.content.utils.BartenderUtils.buyDrinkOrIngredients
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class BartenderDeadMansChest(p: Player, npc: NPC) {
    init {
        p.startConversation {
            npc(npc, HeadE.HAPPY_TALKING, "Yohoho me hearty what would you like to drink?")
            options {
                op("Nothing, thank you.") { player(HeadE.HAPPY_TALKING, "Nothing, thank you.") }
                op("A pint of Grog please.") {
                    player(HeadE.HAPPY_TALKING, "A pint of Grog please.")
                    npc(npc, HeadE.HAPPY_TALKING, "One grog coming right up, that'll be three coins.")
                    exec { buyDrinkOrIngredients(p, npc, 3, Item(1915), true) }
                }
                op("A bottle of rum please.") {
                    player(HeadE.HAPPY_TALKING, "A bottle of rum please.")
                    npc(npc, HeadE.HAPPY_TALKING, "That'll be 27 coins.")
                    exec { buyDrinkOrIngredients(p, npc, 27, Item(431), true) }
                }
                if (!isBarVisited(p, DEAD_MANS_CHEST) && BarCrawl.hasCard(p) && onBarCrawl(p)) {
                    op("I'm doing Alfred Grimhand's Barcrawl.") {
                        player(HeadE.HAPPY_TALKING, "I'm doing Alfred Grimhand's Barcrawl.")
                        npc(npc, HeadE.CHEERFUL, "Haha time to be breaking out the old ${DEAD_MANS_CHEST.drinkName}. That'll be ${DEAD_MANS_CHEST.price} coins please.")
                        exec { buyBarcrawlDrink(p, DEAD_MANS_CHEST) }
                    }
                }
            }
        }
    }
}

@ServerStartupEvent
fun mapBartenderDeadMansChest() {
    onNpcClick(735) { (player, npc) ->
        BartenderDeadMansChest(player, npc)
    }
}
