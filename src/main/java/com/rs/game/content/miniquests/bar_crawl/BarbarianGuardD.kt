package com.rs.game.content.miniquests.bar_crawl

import com.rs.engine.dialogue.Conversation
import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.Options
import com.rs.engine.miniquest.Miniquest
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.allBarsVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.resetAllBars
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.Companion.BARBARIAN_GUARD_ID
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.Companion.BARCRAWL_CARD_ID
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.Companion.COMPLETED
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.Companion.RECEIVED_CRAWL_CARD
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.Companion.hasCard
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.PluginEventHandler

@PluginEventHandler
class BarbarianGuardD(p: Player) : Conversation(p) {
    init {
        when (p.miniquestManager.getStage(Miniquest.BAR_CRAWL)) {

            BarCrawl.NOT_STARTED -> {
                addNPC(BARBARIAN_GUARD_ID, HeadE.FRUSTRATED, "Oi, whaddya want?")
                addOptions(this, "startOptions") { ops: Options ->

                    ops.add("I want to come through this gate.", Dialogue()
                        .addPlayer(HeadE.CONFUSED, "I want to come through this gate.")
                        .addNPC(BARBARIAN_GUARD_ID, HeadE.FRUSTRATED, "Barbarians only. Are you a barbarian? You don't look like one.")
                        .addOptions {
                                startPubCrawl: Options ->
                            startPubCrawl.add("Hmm, yep you've got me there.", Dialogue()
                                .addPlayer(HeadE.SAD, "Hmm, yep you've got me there.")
                            )
                            startPubCrawl.add("Looks can be deceiving, I am in fact a barbarian.", Dialogue()
                                .addPlayer(HeadE.SECRETIVE, "Looks can be deceiving, I am in fact a barbarian.")
                                .addNPC(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "If you're a barbarian you need to be able to drink like one. We barbarians like a good drink.")
                                .addNext {
                                    if (player.inventory.hasFreeSlots()) {
                                        addNPC(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "I have the perfect challenge for you... The Alfred Grimhand Barcrawl! First completed by Alfred Grimhand.")
                                            .addItem(BARCRAWL_CARD_ID, "The guard hands you a Barcrawl card.") {
                                                player.inventory.addItem(BARCRAWL_CARD_ID, 1)
                                                player.miniquestManager.setStage(Miniquest.BAR_CRAWL, RECEIVED_CRAWL_CARD)
                                                resetAllBars(player)
                                            }
                                            .addNPC(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "Take that card to each of the bars named on it. The bartenders will know what it means. We're kinda well known.")
                                            .addNPC(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "They'll give you their strongest drink and sign your card. When you've done all that, we'll be happy to let you in.")
                                    } else {
                                        addNPC(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "I have the perfect challenge for you, but you'll need at least 1 free inventory slot.")
                                    }
                                }
                            )
                        }
                    )

                    ops.add("I want some money.", Dialogue()
                        .addPlayer(HeadE.CHEERFUL, "I want some money.")
                        .addNPC(BARBARIAN_GUARD_ID, HeadE.ANGRY, "Do I look like a bank to you?")
                    )
                }
            }

            RECEIVED_CRAWL_CARD -> {
                addNPC(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "So, how's the Barcrawl coming along?")
                    .addNext {
                        if (!hasCard(player) && !player.bank.containsItem(BARCRAWL_CARD_ID) && player.inventory.hasFreeSlots()) {
                            addPlayer(HeadE.WORRIED, "I've lost my barcrawl card...")
                                .addNPC(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "What are you like? You're gonna have to start all over now.")
                                .addItem(BARCRAWL_CARD_ID, "The guard hands you a Barcrawl card.") {
                                    player.inventory.addItem(BARCRAWL_CARD_ID, 1)
                                    player.miniquestManager.setStage(Miniquest.BAR_CRAWL, RECEIVED_CRAWL_CARD)
                                    resetAllBars(player)
                                }

                        } else if (!hasCard(player) && !player.inventory.hasFreeSlots()) {
                            addNPC(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "What are you like? You'll need at least 1 free inventory slot before I can give you another.")

                        } else if (!hasCard(player) && player.bank.containsItem(BARCRAWL_CARD_ID)) {
                            addPlayer(HeadE.CHEERFUL, "Not too bad, my barcrawl card is in my bank right now.")
                                .addNPC(BARBARIAN_GUARD_ID, HeadE.LAUGH, "You need it with you when you are going on a barcrawl.")

                        } else if (hasCard(player) && !allBarsVisited(player)) {
                            addPlayer(HeadE.WORRIED, "I haven't finished it yet.")
                                .addNPC(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "Well come back when you have, you lightweight.")

                        } else if (hasCard(player) && allBarsVisited(player)) {
                            addPlayer(HeadE.DRUNK, "I tink I jusht 'bout done dem all... but I losht count...")
                                .addItem(BARCRAWL_CARD_ID, "You give the card to the barbarian.")
                                .addNPC(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "Yep that seems fine, you can come in now. I never learned to read, but you look like you've drunk plenty.")
                                .addNext {
                                    player.inventory.removeAllItems(BARCRAWL_CARD_ID)
                                    player.bank.removeItem(BARCRAWL_CARD_ID)
                                    player.miniquestManager.complete(Miniquest.BAR_CRAWL)
                                }
                        }
                    }
            }

            COMPLETED -> {
                addNPC(BARBARIAN_GUARD_ID, HeadE.CHEERFUL, "Ello friend.")
            }
        }
    }
}
