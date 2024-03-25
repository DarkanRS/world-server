package com.rs.game.content.world.areas.karamja.npcs

import com.rs.engine.dialogue.*
import com.rs.game.content.miniquests.bar_crawl.BarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.KARAMJA_SPIRITS
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.onBarCrawl
import com.rs.game.content.utils.BartenderUtils.buyBarcrawlDrink
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick
import com.rs.utils.shop.ShopsHandler

class ZamboKaramjaSpiritsBar(p: Player, npc: NPC) {
    init {
        p.startConversation {
            npc(npc, HeadE.HAPPY_TALKING, "Hey, are you wanting to try some of my fine wines and spirits? All brewed locally on Karamja.")
            options {
                op("Yes, please.") {
                    player(HeadE.HAPPY_TALKING, "Yes, please.")
                    exec { ShopsHandler.openShop(p, "karamja_wines_spirits_and_beers") }
                }
                op("No, thank you.") { player(HeadE.HAPPY_TALKING, "No, thank you.") }
                if (!isBarVisited(p, KARAMJA_SPIRITS) && BarCrawl.hasCard(p) && onBarCrawl(p)) {
                    op("I'm doing Alfred Grimhand's Barcrawl.") {
                        player(HeadE.HAPPY_TALKING, "I'm doing Alfred Grimhand's Barcrawl.")
                        npc(npc, HeadE.CHEERFUL, "Ah, you'll be wanting some ${KARAMJA_SPIRITS.drinkName}, then. It's got a lovely banana taste and it'll only cost you ${KARAMJA_SPIRITS.price} coins.")
                        exec { buyBarcrawlDrink(p, KARAMJA_SPIRITS) }
                    }
                }
            }
        }
    }
}

@ServerStartupEvent
fun mapZamboKaramjaSpiritsBar() {
    onNpcClick(568) { (player, npc) ->
        ZamboKaramjaSpiritsBar(player, npc)
    }
}
