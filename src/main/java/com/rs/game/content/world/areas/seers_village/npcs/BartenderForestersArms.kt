package com.rs.game.content.world.areas.seers_village.npcs

import com.rs.engine.dialogue.*
import com.rs.game.content.miniquests.bar_crawl.BarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.FORESTERS_ARMS
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.onBarCrawl
import com.rs.game.content.utils.BartenderUtils.buyBarcrawlDrink
import com.rs.game.content.utils.BartenderUtils.buyDrinkOrIngredients
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class BartenderForestersArms(p: Player, npc: NPC) {
    init {
        p.startConversation {
            npc(npc, HeadE.HAPPY_TALKING, "Good morning, what would you like?")
            options {
                op("What do you have?") {
                    player(HeadE.HAPPY_TALKING, "What do you have?")
                    npc(npc, HeadE.HAPPY_TALKING, "Well we have beer, or if you want some food, we have our home made stew and meat pies.")
                    options {
                        op("Beer please.") {
                            player(HeadE.HAPPY_TALKING, "Beer please.")
                            npc(npc, HeadE.HAPPY_TALKING, "One beer coming up. Ok, that'll be two coins.")
                            exec {
                                buyDrinkOrIngredients(p, npc, 2, Item(1917), true)
                                p.sendMessage("You buy a pint of beer.")
                            }
                        }
                        op("I'll try the meat pie.") {
                            player(HeadE.HAPPY_TALKING, "I'll try the meat pie.")
                            npc(npc, HeadE.HAPPY_TALKING, "Ok, that'll be 16 coins.")
                            exec {
                                buyDrinkOrIngredients(p, npc, 16, Item(2327), true)
                                p.sendMessage("You buy a nice hot meat pie.")
                            }
                        }
                        op("Could I have some stew please?") {
                            player(HeadE.HAPPY_TALKING, "Could I have some stew please?")
                            npc(npc, HeadE.HAPPY_TALKING, "A bowl of stew, that'll be 20 coins please.")
                            exec {
                                buyDrinkOrIngredients(p, npc, 20, Item(2003), true)
                                p.sendMessage("You buy a bowl of home made stew.")
                            }
                        }
                        op("I don't really want anything thanks.") { player(HeadE.HAPPY_TALKING, "I don't really want anything thanks.") }
                    }
                }
                op("Beer please.") {
                    player(HeadE.HAPPY_TALKING, "Beer please.")
                    npc(npc, HeadE.HAPPY_TALKING, "One beer coming up. Ok, that'll be two coins.")
                    exec {
                        buyDrinkOrIngredients(p, npc, 2, Item(1917), true)
                        p.sendMessage("You buy a pint of beer.")
                    }
                }
                if (!isBarVisited(p, FORESTERS_ARMS) && BarCrawl.hasCard(p) && onBarCrawl(p)) {
                    op("I'm doing Alfred Grimhand's Barcrawl.") {
                        player(HeadE.HAPPY_TALKING, "I'm doing Alfred Grimhand's Barcrawl.")
                        npc(npc, HeadE.CHEERFUL, "Oh you're a barbarian then. Now which of these barrels contained the ${FORESTERS_ARMS.drinkName}? That'll be ${FORESTERS_ARMS.price} coins please.")
                        exec { buyBarcrawlDrink(p, FORESTERS_ARMS) }
                    }
                }
            }
        }
    }
}

@ServerStartupEvent
fun mapBartenderForestersArms() {
    onNpcClick(737) { (player, npc) ->
        BartenderForestersArms(player, npc)
    }
}
