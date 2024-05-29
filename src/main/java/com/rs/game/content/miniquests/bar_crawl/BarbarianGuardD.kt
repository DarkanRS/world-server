package com.rs.game.content.miniquests.bar_crawl

import com.rs.engine.dialogue.*
import com.rs.engine.miniquest.Miniquest
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.allBarsVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.resetAllBars
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.Companion.BARBARIAN_GUARD_ID
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.Companion.BARCRAWL_CARD_ID
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.Companion.COMPLETED
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.Companion.RECEIVED_CRAWL_CARD
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.Companion.hasCard
import com.rs.game.model.entity.player.Player

class BarbarianGuardD(p: Player) {
    init {
        p.startConversation {
            when (p.miniquestManager.getStage(Miniquest.BAR_CRAWL)) {

                BarCrawl.NOT_STARTED -> {
                    npc(BARBARIAN_GUARD_ID, HeadE.FRUSTRATED, "Oi, whaddya want?")
                    options {
                        op("I want to come through this gate.") {
                            player(HeadE.CONFUSED, "I want to come through this gate.")
                            npc(BARBARIAN_GUARD_ID, HeadE.FRUSTRATED, "Barbarians only. Are you a barbarian? You don't look like one.")
                            options {
                                op("Hmm, yep you've got me there.") { player(HeadE.SAD, "Hmm, yep you've got me there.") }
                                op("Looks can be deceiving, I am in fact a barbarian.") {
                                    player(HeadE.SECRETIVE, "Looks can be deceiving, I am in fact a barbarian.")
                                    npc(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "If you're a barbarian you need to be able to drink like one. We barbarians like a good drink.")
                                    exec {
                                        if (p.inventory.hasFreeSlots()) {
                                            npc(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "I have the perfect challenge for you... The Alfred Grimhand Barcrawl! First completed by Alfred Grimhand.")
                                            item(BARCRAWL_CARD_ID, "The guard hands you a Barcrawl card.") {
                                                giveNewCard(p)
                                            }
                                            npc(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "Take that card to each of the bars named on it. The bartenders will know what it means. We're kinda well known.")
                                            npc(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "They'll give you their strongest drink and sign your card. When you've done all that, we'll be happy to let you in.")
                                        } else {
                                            npc(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "I have the perfect challenge for you, but you'll need at least 1 free inventory slot.")
                                        }
                                    }
                                }
                            }
                        }
                        op("I want some money.") {
                            player(HeadE.CHEERFUL, "I want some money.")
                            npc(BARBARIAN_GUARD_ID, HeadE.ANGRY, "Do I look like a bank to you?")
                        }
                    }
                }

                RECEIVED_CRAWL_CARD -> {
                    npc(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "So, how's the Barcrawl coming along?")
                    exec {
                        if (!hasCard(p)) {
                            if (!p.bank.containsItem(BARCRAWL_CARD_ID) && p.inventory.hasFreeSlots()) {
                                player(HeadE.WORRIED, "I've lost my barcrawl card...")
                                npc(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "What are you like? You're gonna have to start all over now.")
                                item(BARCRAWL_CARD_ID, "The guard hands you a Barcrawl card.") {
                                    giveNewCard(p)
                                }
                            } else if (!p.inventory.hasFreeSlots()) {
                                npc(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "What are you like? You'll need at least 1 free inventory slot before I can give you another.")
                            } else if (p.bank.containsItem(BARCRAWL_CARD_ID)) {
                                player(HeadE.CHEERFUL, "Not too bad, my barcrawl card is in my bank right now.")
                                npc(BARBARIAN_GUARD_ID, HeadE.LAUGH, "You need it with you when you are going on a barcrawl.")
                            }
                        } else {
                            if (!allBarsVisited(p)) {
                                player(HeadE.WORRIED, "I haven't finished it yet.")
                                npc(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "Well come back when you have, you lightweight.")
                            } else {
                                player(HeadE.DRUNK, "I think I just 'bout done them all... but I lost count...")
                                item(BARCRAWL_CARD_ID, "You give the card to the barbarian.")
                                npc(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "Yep that seems fine, you can come in now. I never learned to read, but you look like you've drunk plenty.")
                                exec {
                                    p.inventory.removeAllItems(BARCRAWL_CARD_ID)
                                    p.bank.removeItem(BARCRAWL_CARD_ID)
                                    p.miniquestManager.complete(Miniquest.BAR_CRAWL)
                                }
                            }
                        }
                    }
                }

                COMPLETED -> {
                    npc(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "Ello friend.")
                }
            }
        }
    }
    private fun giveNewCard(p: Player) {
        p.inventory.addItem(BARCRAWL_CARD_ID, 1)
        p.miniquestManager.setStage(Miniquest.BAR_CRAWL, RECEIVED_CRAWL_CARD)
        resetAllBars(p)
    }
}
