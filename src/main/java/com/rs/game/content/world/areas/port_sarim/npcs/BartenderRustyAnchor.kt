package com.rs.game.content.world.areas.port_sarim.npcs

import com.rs.engine.dialogue.*
import com.rs.engine.quest.Quest
import com.rs.game.content.miniquests.bar_crawl.BarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.RUSTY_ANCHOR
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.onBarCrawl
import com.rs.game.content.utils.BartenderUtils.buyBarcrawlDrink
import com.rs.game.content.utils.BartenderUtils.buyDrinkOrIngredients
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class BartenderRustyAnchor(p: Player, npc: NPC) {
    init {
        p.startConversation {
            player(HeadE.HAPPY_TALKING, "Good day to you!")
            npc(npc, HeadE.HAPPY_TALKING, "Hello there!")
            options {
                if (!isBarVisited(p, RUSTY_ANCHOR) && BarCrawl.hasCard(p) && onBarCrawl(p)) {
                    op("I'm doing Alfred Grimhand's Barcrawl.") {
                        player(HeadE.HAPPY_TALKING, "I'm doing Alfred Grimhand's Barcrawl.")
                        npc(npc, HeadE.LAUGH, "Are you sure? You look a bit skinny for that.")
                        player(HeadE.FRUSTRATED, "Just give me whatever I need to drink here.")
                        npc(npc, HeadE.CHEERFUL, "Okay, one ${RUSTY_ANCHOR.drinkName} coming up. ${RUSTY_ANCHOR.price} coins, please.")
                        exec { buyBarcrawlDrink(p, RUSTY_ANCHOR) }
                    }
                }
                op("Could I buy a beer, please?") {
                    player(HeadE.HAPPY_TALKING, "Could I buy a beer, please?")
                    npc(npc, HeadE.HAPPY_TALKING, "Sure, that will be two gold coins, please.")
                    exec { buyDrinkOrIngredients(p, npc, 2, Item(1917), false) }
                }
                op("Have you heard any rumours here?") {
                    player(HeadE.HAPPY_TALKING, "Have you heard any rumours here?")
                    exec {
                        if (!p.questManager.isComplete(Quest.GOBLIN_DIPLOMACY)) {
                            npc(npc, HeadE.CONFUSED, "Well, there was a guy in here earlier saying the goblins up by the mountain are arguing again, about the colour of their armour of all things.")
                            npc(npc, HeadE.WORRIED, "Knowing the goblins it could easily turn into a full blown war, which wouldn't be good. Goblin wars make such a mess of the countryside.")
                            player(HeadE.CALM, "Well if I have the time I'll go and see if I can knock some sense into them.")
                        } else {
                            npc(npc, HeadE.SHAKING_HEAD, "No, it hasn't been very busy lately.")
                        }
                    }
                }
                op("Bye, then.") {
                    player(HeadE.HAPPY_TALKING, "Bye, then.")
                    npc(npc, HeadE.CALM, "Come back soon!")
                }
            }
        }
    }
}

@ServerStartupEvent
fun mapBartenderRustyAnchor() {
    onNpcClick(734) { (player, npc) ->
        BartenderRustyAnchor(player, npc)
    }
}
