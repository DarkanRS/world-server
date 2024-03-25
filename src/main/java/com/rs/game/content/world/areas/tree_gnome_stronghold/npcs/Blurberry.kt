package com.rs.game.content.world.areas.tree_gnome_stronghold.npcs

import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.BLURBERRYS_BAR
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.onBarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.Companion.hasCard
import com.rs.game.content.utils.BartenderUtils.buyBarcrawlDrink
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class Blurberry(p: Player, npc: NPC)  {
    init {
        p.startConversation {
            player(HeadE.HAPPY_TALKING, "Hello.")
            npc(npc, HeadE.HAPPY_TALKING, "Well hello there traveller. If you're looking for a cocktail, the barman will happily make you one.")
            exec {
                if (!isBarVisited(p, BLURBERRYS_BAR) && hasCard(p) && onBarCrawl(p)) {
                    player(HeadE.HAPPY_TALKING, "I'm doing Alfred Grimhand's Barcrawl.")
                    npc(npc, HeadE.CHEERFUL, "Ah, you've come to the best stop on your list! I'll give you my famous  ${BLURBERRYS_BAR.drinkName}! It'll cost you ${BLURBERRYS_BAR.price} coins.")
                    exec { buyBarcrawlDrink(p, BLURBERRYS_BAR) }
                }
            }
        }
    }
}

@ServerStartupEvent
fun mapBlurberry() {
    onNpcClick(848) { (player, npc) ->
        Blurberry(player, npc)
    }
}
