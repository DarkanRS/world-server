package com.rs.game.content.world.areas.ardougne.npcs

import com.rs.engine.dialogue.*
import com.rs.game.content.miniquests.bar_crawl.BarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.FLYING_HORSE_INN
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.onBarCrawl
import com.rs.game.content.utils.BartenderUtils.buyBarcrawlDrink
import com.rs.game.content.utils.BartenderUtils.buyDrinkOrIngredients
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class BartenderFlyingHorseInn(p: Player, npc: NPC) {
    init {
        p.startConversation {
            npc(npc, HeadE.HAPPY_TALKING, "Would you like to buy a drink?")
            player(HeadE.CALM, "What do you serve?")
            npc(npc, HeadE.CHEERFUL_EXPOSITION, "Beer!")
            options {
                op("I'll have a beer then.") {
                    player(HeadE.HAPPY_TALKING, "I'll have a beer then.")
                    npc(npc, HeadE.HAPPY_TALKING, "Ok, that'll be two coins.")
                    exec { buyDrinkOrIngredients(p, npc, 2, Item(1917), true) }
                }
                op("I'll not have anything then.") { player(HeadE.HAPPY_TALKING, "I'll not have anything then") }
                if (!isBarVisited(p, FLYING_HORSE_INN) && BarCrawl.hasCard(p) && onBarCrawl(p)) {
                    op("I'm doing Alfred Grimhand's Barcrawl") {
                        player(HeadE.HAPPY_TALKING, "I'm doing Alfred Grimhand's Barcrawl.")
                        npc(npc, HeadE.CHEERFUL, "Fancy a bit of ${FLYING_HORSE_INN.drinkName}. It'll only be ${FLYING_HORSE_INN.price} coins.")
                        exec { buyBarcrawlDrink(p, FLYING_HORSE_INN) }
                    }
                }
            }
        }
    }
}

@ServerStartupEvent
fun mapBartenderFlyingHorseInn() {
    onNpcClick(738) { (player, npc) ->
        BartenderFlyingHorseInn(player, npc)
    }
}
