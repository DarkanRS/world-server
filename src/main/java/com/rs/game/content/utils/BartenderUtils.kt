package com.rs.game.content.utils

import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.lib.util.Utils

object BartenderUtils {
    fun buyDrinkOrIngredients(p: Player, npc: NPC, price: Int, item: Item, playerNoCoinsMessage: Boolean) {
        val hasCoins = p.inventory.hasCoins(price)
        val hasSlots = p.inventory.hasFreeSlots()
        if (!hasCoins) {
            p.startConversation {
                if (playerNoCoinsMessage)
                    player(HeadE.SAD, "Oh dear. I don't seem to have enough money.")
                else
                    npc(npc, HeadE.FRUSTRATED, "I said 20 coins! You haven't got 20 coins!")
            }
        } else if (!hasSlots) {
            p.startConversation {
                npc(npc, HeadE.SHAKING_HEAD, "You don't have space for ${Utils.addArticle(item.name.lowercase())}!")
            }
        } else {
            p.inventory.removeCoins(price)
            p.inventory.addItem(Item(item))
            p.startConversation {
                npc(npc, HeadE.HAPPY_TALKING, "There you go.")
                player(HeadE.HAPPY_TALKING, "Thanks, ${npc.name}!")
            }
        }
    }

    fun buyBarcrawlDrink(p: Player, bar: Bars) {
        if (p.inventory.hasCoins(bar.price)) {
            p.lock()
            p.schedule {
                bar.effect.message(p, start = true)
                wait(2)
                bar.effect.effect(p)
                wait(4)
                bar.effect.message(p, start = false)
            }
        } else {
            p.startConversation { player(HeadE.SAD, "I don't have that much money on me.") }
        }
    }

    fun sellBeerGlass(p: Player, npc: NPC) {
        p.startConversation {
            player(HeadE.CONFUSED, "I've got this beer glass...")
            npc(npc, HeadE.HAPPY_TALKING, "We'll buy it for a couple of coins if you're interested.")
            options {
                op("Okay, sure.") {
                    player(HeadE.HAPPY_TALKING, "Okay, sure.")
                    exec {
                        p.inventory.removeItems(Item(1919, 1))
                        p.inventory.addItem(995, 2)
                    }
                    npc(npc.id, HeadE.HAPPY_TALKING, "There you go.")
                    player(HeadE.HAPPY_TALKING, "Thanks!")
                }
                op("No thanks, I like empty beer glasses.") { player(HeadE.SHAKING_HEAD, "No thanks, I like empty beer glasses.") }
            }
        }
    }
}