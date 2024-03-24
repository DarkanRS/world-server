package com.rs.game.content.world.areas.tree_gnome_stronghold.npcs

import com.rs.engine.dialogue.Conversation
import com.rs.engine.dialogue.HeadE
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.BLURBERRYS_BAR
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.onBarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.Companion.hasCard
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

@PluginEventHandler
class Blurberry(p: Player, npc: NPC) : Conversation(p) {
    init {
        addPlayer(HeadE.HAPPY_TALKING, "Hello.")
            .addNPC(npc, HeadE.HAPPY_TALKING, "Well hello there traveller. If you're looking for a cocktail, the barman will happily make you one.")
            .addNext {
                if (!isBarVisited(player, BLURBERRYS_BAR) && hasCard(player) && onBarCrawl(player)) {
                    addPlayer(HeadE.HAPPY_TALKING, "I'm doing Alfred Grimhand's Barcrawl.")
                        .addNPC(npc, HeadE.CHEERFUL, "Ah, you've come to the best stop on your list! I'll give you my famous  ${BLURBERRYS_BAR.drinkName}! It'll cost you ${BLURBERRYS_BAR.price} coins.")
                        .addNext {
                            if (player.inventory.hasCoins(BLURBERRYS_BAR.price)) {
                                player.startConversation(object : Conversation(player) {
                                    init {
                                        addNext {
                                            player.lock()
                                            player.schedule {
                                                BLURBERRYS_BAR.effect.message(player, start = true)
                                                wait(2)
                                                BLURBERRYS_BAR.effect.effect(player)
                                                wait(4)
                                                BLURBERRYS_BAR.effect.message(
                                                    player, start = false
                                                )
                                            }
                                        }
                                    }
                                })
                            } else {
                                player.startConversation(object : Conversation(player) {
                                    init {
                                        addPlayer(HeadE.SAD_MILD_LOOK_DOWN, "I don't have that much money on me.")
                                        create()
                                    }
                                })
                            }
                        }
                }
            }
        create()
    }

    companion object {

        @JvmStatic
        fun handleTalkTo(p: Player, npc: NPC) {
            p.startConversation(Blurberry(p, npc))
        }
    }
}

@ServerStartupEvent
fun mapBlurberry() {
    onNpcClick(848) { (player, npc) ->
        Blurberry.handleTalkTo(player, npc)
    }
}
