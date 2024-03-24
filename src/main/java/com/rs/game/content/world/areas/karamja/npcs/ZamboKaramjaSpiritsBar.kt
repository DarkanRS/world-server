package com.rs.game.content.world.areas.karamja.npcs

import com.rs.engine.dialogue.Conversation
import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.Options
import com.rs.game.content.miniquests.bar_crawl.BarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.KARAMJA_SPIRITS
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.onBarCrawl
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick
import com.rs.utils.shop.ShopsHandler

@PluginEventHandler
class ZamboKaramjaSpiritsBar(p: Player, npc: NPC) : Conversation(p) {
    init {
        addNPC(npc, HeadE.HAPPY_TALKING, "Hey, are you wanting to try some of my fine wines and spirits? All brewed locally on Karamja.")

        addOptions(object : Options() {
            override fun create() {

                option("Yes, please.", Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "Yes, please.")
                    .addNext {
                        ShopsHandler.openShop(player, "karamja_wines_spirits_and_beers")
                    })

                option("No, thank you.", Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "No, thank you."))

                if (!isBarVisited(player, KARAMJA_SPIRITS) && BarCrawl.hasCard(player) && onBarCrawl(player)) {
                    option("I'm doing Alfred Grimhand's Barcrawl.", Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "I'm doing Alfred Grimhand's Barcrawl.")
                        .addNPC(npc, HeadE.CHEERFUL, "Ah, you'll be wanting some ${KARAMJA_SPIRITS.drinkName}, then. It's got a lovely banana taste and it'll only cost you ${KARAMJA_SPIRITS.price} coins.")
                        .addNext {
                            if (player.inventory.hasCoins(KARAMJA_SPIRITS.price)) {
                                player.startConversation(object : Conversation(player) {
                                    init {
                                        addNext {
                                            player.lock()
                                            player.schedule {
                                                KARAMJA_SPIRITS.effect.message(player, start = true)
                                                wait(2)
                                                KARAMJA_SPIRITS.effect.effect(player)
                                                wait(4)
                                                KARAMJA_SPIRITS.effect.message(player, start = false)
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
                        })
                }

            }
        })

        create()
    }

    companion object {

        @JvmStatic
        fun handleTalkTo(p: Player, npc: NPC) {
            p.startConversation(ZamboKaramjaSpiritsBar(p, npc))
        }
    }
}

@ServerStartupEvent
fun mapZamboKaramjaSpiritsBar() {
    onNpcClick(568) { (player, npc) ->
        ZamboKaramjaSpiritsBar.handleTalkTo(player, npc)
    }
}
