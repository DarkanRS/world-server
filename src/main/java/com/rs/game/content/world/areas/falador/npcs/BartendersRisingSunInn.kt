package com.rs.game.content.world.areas.falador.npcs

import com.rs.engine.dialogue.Conversation
import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.Options
import com.rs.game.content.miniquests.bar_crawl.BarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.RISING_SUN
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.onBarCrawl
import com.rs.game.content.quests.fightarena.dialogue.LazyGuardFightArenaD
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.handlers.NPCInteractionDistanceHandler
import com.rs.plugin.kts.getInteractionDistance
import com.rs.plugin.kts.onNpcClick

@PluginEventHandler
class BartendersRisingSunInn(p: Player, npc: NPC) : Conversation(p) {
    init {
        addNPC(npc.id, HeadE.HAPPY_TALKING, "Hi! What can I get you?")

        if (!isBarVisited(player, RISING_SUN) && BarCrawl.hasCard(player) && onBarCrawl(player)) {
            addOptions(object : Options() {
                override fun create() {

                    option("What ales are you serving?", Dialogue()
                        .addNext {
                            normalDialogue(player, npc)
                        })

                    option("I'm doing Alfred Grimhand's Barcrawl.", Dialogue()
                        .addNext{
                            barCrawlDialogue(player, npc)
                        })
                }
            })
        } else {
            addNext {
                normalDialogue(player, npc)
            }
        }
        create()
    }

    fun normalDialogue(player: Player, npc: NPC) {
        val hasCoins = player.inventory.hasCoins(3)
        val hasSlots = player.inventory.hasFreeSlots()
        val name = npc.name
        val dialogue = Dialogue()
            .addPlayer(HeadE.HAPPY_TALKING, "What ales are you serving?")
            .addNPC(npc.id, HeadE.HAPPY_TALKING, "Well, we've got Asgarnian Ale, Wizard's Mind Bomb and Dwarven Stout, all for only 3 coins.")
            .addOptions { ops: Options ->
                ops.add("One Asgarnian Ale, please.")
                    .addNext {
                        if (!hasCoins) {
                            player.startConversation(Dialogue()
                                .addPlayer(HeadE.HAPPY_TALKING, "One Asgarnian Ale, please.")
                                .addNPC(npc.id, HeadE.ANGRY, "I said 3 coins! You haven't got 3 coins!")
                            )
                        }
                        else if (!hasSlots) {
                            player.startConversation(Dialogue()
                                .addPlayer(HeadE.HAPPY_TALKING, "One Asgarnian Ale, please.")
                                .addNPC(npc.id, HeadE.SHAKING_HEAD, "You don't have the space for a beer!")
                            )
                        }
                        else {
                            player.inventory.removeCoins(3)
                            player.inventory.addItem(1905)
                            player.startConversation(Dialogue()
                                .addPlayer(HeadE.HAPPY_TALKING, "One Asgarnian Ale, please.")
                                .addNPC(npc.id, HeadE.HAPPY_TALKING, "There you go.")
                                .addPlayer(HeadE.HAPPY_TALKING, "Thanks, $name")
                            )
                        }
                    }
                ops.add("I'll try the Mind Bomb.")
                    .addNext {
                        if (!hasCoins){
                            player.startConversation(Dialogue()
                                .addPlayer(HeadE.HAPPY_TALKING, "I'll try the Mind Bomb.")
                                .addNPC(npc.id, HeadE.ANGRY, "I said 3 coins! You haven't got 3 coins!")
                            )
                        }
                        else if (!hasSlots) {
                            player.startConversation(Dialogue()
                                .addPlayer(HeadE.HAPPY_TALKING, "I'll try the Mind Bomb.")
                                .addNPC(npc.id, HeadE.SHAKING_HEAD, "You don't have the space for a beer!")
                            )
                        }
                        else {
                            player.inventory.removeCoins(3)
                            player.inventory.addItem(1907)
                            player.startConversation(Dialogue()
                                .addPlayer(HeadE.HAPPY_TALKING, "I'll try the Mind Bomb.")
                                .addNPC(npc.id, HeadE.HAPPY_TALKING, "There you go.")
                                .addPlayer(HeadE.HAPPY_TALKING, "Thanks, $name")
                            )
                        }
                    }
                ops.add("Can I have a Dwarven Stout?")
                    .addNext {
                        if (!hasCoins) {
                            player.startConversation(Dialogue()
                                .addPlayer(HeadE.HAPPY_TALKING, "Can I have a Dwarven Stout?")
                                .addNPC(npc.id, HeadE.ANGRY, "I said 3 coins! You haven't got 3 coins!")
                            )
                        }
                        else if (!hasSlots) {
                            player.startConversation(Dialogue()
                                .addPlayer(HeadE.HAPPY_TALKING, "Can I have a Dwarven Stout?")
                                .addNPC(npc.id, HeadE.SHAKING_HEAD, "You don't have the space for a beer!")
                            )
                        }
                        else {
                            player.inventory.removeCoins(3)
                            player.inventory.addItem(1913)
                            player.startConversation(Dialogue()
                                .addPlayer(HeadE.HAPPY_TALKING, "Can I have a Dwarven Stout?")
                                .addNPC(npc.id, HeadE.HAPPY_TALKING, "There you go.")
                                .addPlayer(HeadE.HAPPY_TALKING, "Thanks, $name")
                            )
                        }
                    }
                if (player.inventory.containsItem(1919)) {
                    ops.add("I've got this beer glass...")
                        .addPlayer(HeadE.CONFUSED, "I've got this beer glass...")
                        .addNPC(npc.id, HeadE.HAPPY_TALKING, "We'll buy it for a couple of coins if you're interested.")
                        .addOptions { ops2: Options ->
                            ops2.add("Okay, sure.")
                                .addPlayer(HeadE.HAPPY_TALKING, "Okay, sure.")
                                .addNext {
                                    player.inventory.removeItems(Item(1919, 1))
                                    player.inventory.addItem(995, 2)
                                }
                                .addNPC(npc.id, HeadE.HAPPY_TALKING, "There you go.")
                                .addPlayer(HeadE.HAPPY_TALKING, "Thanks!")
                            ops2.add("No thanks, I like empty beer glasses.")
                                .addPlayer(HeadE.SHAKING_HEAD, "No thanks, I like empty beer glasses.")
                        }
                }
                ops.add("I don't feel like any of those.")
                    .addPlayer(HeadE.SHAKING_HEAD, " I don't feel like any of those.")
            }
        player.startConversation(dialogue)
    }

    fun barCrawlDialogue(player: Player, npc: NPC) {
        val dialogue = Dialogue()
            .addPlayer(HeadE.HAPPY_TALKING, "I'm doing Alfred Grimhand's Barcrawl.")
            .addNPC(npc, HeadE.LAUGH, "Heehee, this'll be fun!")
            .addNPC(npc, HeadE.CHEERFUL_EXPOSITION, "You'll be after our ${RISING_SUN.drinkName}, then. Lots of expensive parts to the cocktail, though, so it will cost you ${RISING_SUN.price} coins.")
            .addNext {
                if (player.inventory.hasCoins(RISING_SUN.price)) {
                    player.startConversation(object : Conversation(player) {
                        init {
                            addNext {
                                player.lock()
                                player.schedule {
                                    RISING_SUN.effect.message(player, start = true)
                                    wait(2)
                                    RISING_SUN.effect.effect(player)
                                    wait(4)
                                    RISING_SUN.effect.message(player, start = false
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
        player.startConversation(dialogue)
    }

    companion object {

        @JvmStatic
        fun handleTalkTo(p: Player, npc: NPC) {
            p.startConversation(BartendersRisingSunInn(p, npc))
        }
    }
}

@ServerStartupEvent
fun mapBartendersRisingSunInn() {
    getInteractionDistance(736) { _, _ -> 2 }
    onNpcClick(736, 3217, 3218) { (player, npc) ->
        BartendersRisingSunInn.handleTalkTo(player, npc)
    }
}
