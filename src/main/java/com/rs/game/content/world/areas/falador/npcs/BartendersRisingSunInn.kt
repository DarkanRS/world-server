package com.rs.game.content.world.areas.falador.npcs

import com.rs.engine.dialogue.*
import com.rs.game.content.miniquests.bar_crawl.BarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.RISING_SUN
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.onBarCrawl
import com.rs.game.content.utils.BartenderUtils.buyBarcrawlDrink
import com.rs.game.content.utils.BartenderUtils.buyDrinkOrIngredients
import com.rs.game.content.utils.BartenderUtils.sellBeerGlass
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.getInteractionDistance
import com.rs.plugin.kts.onNpcClick

class BartendersRisingSunInn(p: Player, npc: NPC) {
    init {
        p.startConversation {
            npc(npc.id, HeadE.HAPPY_TALKING, "Hi! What can I get you?")
            if (!isBarVisited(p, RISING_SUN) && BarCrawl.hasCard(p) && onBarCrawl(p)) {
                options {
                    op("What ales are you serving?") {
                        exec { normalDialogue(p, npc) }
                    }
                    op("I'm doing Alfred Grimhand's Barcrawl.") {
                        exec { barCrawlDialogue(p, npc) }
                    }
                }
            } else {
                exec { normalDialogue(p, npc) }
            }
        }
    }

    private fun normalDialogue(p: Player, npc: NPC) {
        p.startConversation {
            player(HeadE.HAPPY_TALKING, "What ales are you serving?")
            npc(npc.id, HeadE.HAPPY_TALKING, "Well, we've got Asgarnian Ale, Wizard's Mind Bomb and Dwarven Stout, all for only 3 coins.")
            options {
                op("One Asgarnian Ale, please.") {
                    player(HeadE.HAPPY_TALKING, "One Asgarnian Ale, please.")
                    npc(npc, HeadE.HAPPY_TALKING, "That'll be 3 coins, please.")
                    exec { buyDrinkOrIngredients(p, npc, 3, Item(1905), false) }
                }
                op("I'll try the Mind Bomb.") {
                    player(HeadE.HAPPY_TALKING, "I'll try the Mind Bomb.")
                    npc(npc, HeadE.HAPPY_TALKING, "That'll be 3 coins, please.")
                    exec { buyDrinkOrIngredients(p, npc, 3, Item(1907), false) }
                }
                op("Can I have a Dwarven Stout?") {
                    player(HeadE.HAPPY_TALKING, "Can I have a Dwarven Stout?")
                    npc(npc, HeadE.HAPPY_TALKING, "That'll be 3 coins, please.")
                    exec { buyDrinkOrIngredients(p, npc, 3, Item(1913), false) }
                }
                op("I don't feel like any of those.") { player(HeadE.HAPPY_TALKING, "I don't feel like any of those.") }
                if (p.inventory.containsItem(1919)) {
                    op("I've got this beer glass...") {
                        exec { sellBeerGlass(p, npc) }
                    }
                }
            }
        }
    }

    private fun barCrawlDialogue(p: Player, npc: NPC) {
        p.startConversation {
            player(HeadE.HAPPY_TALKING, "I'm doing Alfred Grimhand's Barcrawl.")
            npc(npc, HeadE.LAUGH, "Heehee, this'll be fun!")
            npc(npc, HeadE.CHEERFUL_EXPOSITION, "You'll be after our ${RISING_SUN.drinkName}, then. Lots of expensive parts to the cocktail, though, so it will cost you ${RISING_SUN.price} coins.")
            exec { buyBarcrawlDrink(p, RISING_SUN) }
        }
    }
}

@ServerStartupEvent
fun mapBartendersRisingSunInn() {
    getInteractionDistance(736) { _, _ -> 2 }
    onNpcClick(736, 3217, 3218) { (player, npc) ->
        BartendersRisingSunInn(player, npc)
    }
}
