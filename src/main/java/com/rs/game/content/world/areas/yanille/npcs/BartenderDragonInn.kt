package com.rs.game.content.world.areas.yanille.npcs

import com.rs.engine.dialogue.*
import com.rs.game.content.miniquests.bar_crawl.BarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.DRAGON_INN
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.onBarCrawl
import com.rs.game.content.utils.BartenderUtils.buyBarcrawlDrink
import com.rs.game.content.utils.BartenderUtils.buyDrinkOrIngredients
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class BartenderDragonInn(p: Player, npc: NPC) {
    init {
        p.startConversation {
            npc(npc, HeadE.HAPPY_TALKING, "What can I get you?")
            player(HeadE.CALM, "What's on the menu?")
            npc(npc, HeadE.HAPPY_TALKING, "Dragon Bitter and Greenman's Ale, oh and some cheap beer.")
            options {
                op("I'll give it a miss I think.") {
                    player(HeadE.HAPPY_TALKING, "I'll give it a miss I think.")
                    npc(npc, HeadE.SAD_MILD, "Come back when you're a little thirstier.")
                }
                op("I'll try the Dragon Bitter.") {
                    player(HeadE.HAPPY_TALKING, "I'll try the Dragon Bitter.")
                    npc(npc, HeadE.HAPPY_TALKING, "Ok, that'll be two coins.")
                    exec {
                        buyDrinkOrIngredients(p, npc, 2, Item(1911), true)
                        p.sendMessage("You buy a pint of Dragon Bitter.")
                    }
                }
                op("Can I have some Greenman's Ale?") {
                    player(HeadE.HAPPY_TALKING, "Can I have some Greenman's Ale?")
                    npc(npc, HeadE.HAPPY_TALKING, "Ok, that'll be ten coins.")
                    exec {
                        buyDrinkOrIngredients(p, npc, 10, Item(1909), true)
                        p.sendMessage("You buy a pint of Greenman's Ale.")
                    }
                }
                op("One cheap beer please!") {
                    player(HeadE.HAPPY_TALKING, "One cheap beer please!")
                    npc(npc, HeadE.HAPPY_TALKING, "That'll be 2 gold coins please!")
                    exec {
                        buyDrinkOrIngredients(p, npc, 2, Item(1917), true)
                        p.sendMessage("You buy a pint of cheap beer.")
                    }
                }
                if (!isBarVisited(p, DRAGON_INN) && BarCrawl.hasCard(p) && onBarCrawl(p)) {
                    op("I'm doing Alfred Grimhand's Barcrawl.") {
                        player(HeadE.HAPPY_TALKING, "I'm doing Alfred Grimhand's Barcrawl.")
                        npc(npc, HeadE.CHEERFUL, "I suppose you'll be wanting some ${DRAGON_INN.drinkName}. That'll cost you ${DRAGON_INN.price} coins.")
                        exec { buyBarcrawlDrink(p, DRAGON_INN) }
                    }
                }
            }
        }
    }
}

@ServerStartupEvent
fun mapBartenderDragonInn() {
    onNpcClick(739) { (player, npc) ->
        BartenderDragonInn(player, npc)
    }
}
