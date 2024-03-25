package com.rs.game.content.world.areas.varrock.npcs

import com.rs.engine.dialogue.*
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.JOLLY_BOAR_INN
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.onBarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.Companion.hasCard
import com.rs.game.content.utils.BartenderUtils.buyBarcrawlDrink
import com.rs.game.content.utils.BartenderUtils.buyDrinkOrIngredients
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class BartenderJollyBoarInn(p: Player, npc: NPC) {
    init {
        p.startConversation {
            npc(npc, HeadE.HAPPY_TALKING, "Can I help you?")
            options {
                op("I'll have a beer please.") {
                    player(HeadE.HAPPY_TALKING, "I'll have a pint of beer please.")
                    npc(npc, HeadE.HAPPY_TALKING, "Ok, that'll be two coins please.")
                    exec { buyDrinkOrIngredients(p, npc, 2, Item(1917), true) }
                }
                op("Any hints where I can go adventuring?") {
                    player(HeadE.HAPPY_TALKING, "Any hints on where I can go adventuring?")
                    npc(npc, HeadE.SKEPTICAL_THINKING, "Ooh, now. Let me see...")
                    npc(npc, HeadE.CHEERFUL, "Well there is the Varrock sewers. There are tales of untold horrors coming out at night and stealing babies from houses.")
                    player(HeadE.CHEERFUL, "Sounds perfect! Where's the entrance?")
                    npc(npc, HeadE.CHEERFUL, "It's just to the east of the palace.")
                }
                op("Heard any good gossip?") {
                    player(HeadE.SECRETIVE, "Heard any gossip?")
                    npc(npc, HeadE.LAUGH, "I'm not that well up on the gossip out here. I've heard that the bartender in the Blue Moon Inn has gone a little crazy, he keeps claiming he is part of something called a computer game.")
                    npc(npc, HeadE.LAUGH, "What that means, I don't know. That's probably old news by now though.")
                }
                if (!isBarVisited(p, JOLLY_BOAR_INN) && hasCard(p) && onBarCrawl(p)) {
                    op("I'm doing Alfred Grimhand's Barcrawl.") {
                        player(HeadE.HAPPY_TALKING, "I'm doing Alfred Grimhand's Barcrawl.")
                        npc(npc, HeadE.CHEERFUL, "Ah, there seems to be a fair few doing that one these days. My supply of ${JOLLY_BOAR_INN.drinkName} is starting to run low, it'll cost you ${JOLLY_BOAR_INN.price} coins.")
                        exec { buyBarcrawlDrink(p, JOLLY_BOAR_INN) }
                    }
                }
            }
        }
    }
}

@ServerStartupEvent
fun mapBartenderJollyBoarInn() {
    onNpcClick(731) { (player, npc) ->
        BartenderJollyBoarInn(player, npc)
    }
}
