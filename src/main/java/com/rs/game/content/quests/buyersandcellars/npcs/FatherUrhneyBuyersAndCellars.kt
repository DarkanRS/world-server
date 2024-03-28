package com.rs.game.content.quests.buyersandcellars.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.World
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.tasks.WorldTasks
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

private const val key = 18647

@ServerStartupEvent
fun mapFatherUrhneyBuyersAndCellars() {
    onNpcClick(458, options = arrayOf("Pickpocket")) { (p, npc) ->
        if (!p.inventory.hasFreeSlots()) {
            p.sendMessage("You don't have enough space in your inventory.")
        }
        if (!FatherUrhneyBuyersAndCellars.fireIsLit() && p.getQuestStage(Quest.BUYERS_AND_CELLARS) <= 7) {
            p.sendMessage("For an old man, he's very alert. You can't get an opportunity to pick his pocket.")
        }
        if (FatherUrhneyBuyersAndCellars.fireIsLit() && p.getQuestStage(Quest.BUYERS_AND_CELLARS) == 7) {
            if (!p.inventory.containsItem(key) && !p.bank.containsItem(key, 1)) {
                p.faceEntity(npc)
                WorldTasks.delay(0) { p.anim(881) }
                p.inventory.addItem(18647)
                p.sendMessage("You take advantage of Father Urhney's panic to lift a complex-looking key from his pocket.")
                p.setQuestStage(Quest.BUYERS_AND_CELLARS, 7)
            } else {
                p.sendMessage("You already have Father Urhney's key.")
            }
        } else if (p.getQuestStage(Quest.BUYERS_AND_CELLARS) >= 8) {
            p.sendMessage("You've already stolen his key and chalice!")
        }
    }
}

object FatherUrhneyBuyersAndCellars {

    private val fireTiles = arrayOf(
        Tile.of(3205, 3152, 0),
        Tile.of(3209, 3152, 0),
        Tile.of(3211, 3150, 0),
        Tile.of(3211, 3148, 0),
        Tile.of(3209, 3146, 0),
        Tile.of(3204, 3146, 0),
        Tile.of(3202, 3148, 0),
        Tile.of(3202, 3150, 0)
    )

    fun fireIsLit(): Boolean {
        return World.getAllObjectsInChunkRange(819593, 5)
            .filterNotNull()
            .filter { obj -> obj.definitions?.name == "Fire" }
            .any { obj -> fireTiles.any { fireTile -> fireTile == obj.tile } }
    }

    @JvmStatic
    fun stage4(p: Player, npc: NPC) {
        p.startConversation {
            npc(npc, ANGRY, "Go away! I'm meditating!")
            label("startOptions")
            options {
                op("Well, that's friendly.") {
                    player(FRUSTRATED, "Well, that's friendly.")
                    npc(npc, FRUSTRATED, "I said go away!")
                    player(CALM_TALK, "Okay, okay, Sheesh, what a grouch.")
                }
                op("I've come to repossess your house.") {
                    player(CALM_TALK, "I've come to repossess your house.")
                    npc(npc, SCARED, "On what grounds?")
                    options {
                        op("Repeated failure to make mortgage repayments.") {
                            player(CALM_TALK, "Repeated failure to make mortgage repayments.")
                            npc(npc, FRUSTRATED, "What?")
                            npc(npc, FRUSTRATED, "But I don't have a mortgage - I built this house myself.")
                            player(CALM, "Sorry, I must have got the wrong address. All the houses look the same around here.")
                            npc(npc, FRUSTRATED, "What? What houses? This is the only one. What are you talking about?")
                            player(CALM, "Never mind.")
                            goto("startOptions")
                        }
                        op("I don't know, I just wanted this house.") {
                            player(CALM_TALK, "I don't know, I just wanted this house.")
                            npc(npc, FRUSTRATED, "Oh, go away and stop wasting my time.")
                        }
                    }
                }
                op("Nice chalice.") {
                    player(CALM_TALK, "That's a nice chalice.")
                    label("chaliceOptions")
                    options {
                        op("Aren't you afraid it will be taken in lieu of mortgage payments?") {
                            player(CALM_TALK, "Aren't you afraid it will be taken in lieu of mortgage payments?")
                            npc(npc, FRUSTRATED, "Are you just here to bother me?")
                            goto("chaliceOptions")
                        }
                        op("The bailiffs are coming! Quick: hide the valuables!") {
                            player(SCARED, "The bailiffs are coming! Quick: hide the valuables!")
                            npc(npc, FRUSTRATED, "The thoughts I am entertaining about you are worth another three months' mediation, young " + p.getPronoun("man", "lady") + ".")
                            goto("chaliceOptions")
                        }
                        op("Can I have a look at that chalice?") {
                            player(SKEPTICAL_THINKING, "Can I have a look at that chalice?")
                            npc(npc, FRUSTRATED, "If you must. It's in the display case over there.")
                            player(SKEPTICAL_HEAD_SHAKE, "I meant...a closer look.")
                            npc(npc, FRUSTRATED, "It's only a couple of inches from the glass.")
                            player(CALM_TALK, "Can I hold it?")
                            npc(npc, ANGRY, "And get grubby fingermarks over it? I think not.")
                            player(CALM_TALK, "Hmm. I'll need something more urgent to draw his eye. Perhaps Robin can help")
                            npc(npc, CALM_TALK, "What was that?")
                            player(SKEPTICAL_HEAD_SHAKE, "Mumble mumble mumble!")
                            npc(npc, CALM_TALK, "Quite.") {
                                p.sendMessage("Perhaps you should ask Robin how best to distract the priest.")
                                p.setQuestStage(Quest.BUYERS_AND_CELLARS, 5)
                            }
                        }
                        op("Bye, then.") {
                            player(CALM_TALK, "Bye, then.")
                            npc(npc, FRUSTRATED, "Bah.")
                        }
                    }
                }
                op("Bye, then.") {
                    player(CALM_TALK, "Bye, then.")
                    npc(npc, FRUSTRATED, "Bah.")
                }
            }
        }
    }

    @JvmStatic
    fun stage6(p: Player, npc: NPC) {
        p.startConversation {
            npc(npc, ANGRY, "Go away! I'm meditating!")
            label("startOptions")
            options {
                op("Well, that's friendly.") {
                    player(FRUSTRATED, "Well, that's friendly.")
                    npc(npc, FRUSTRATED, "I said go away!")
                    player(CALM_TALK, "Okay, okay, Sheesh, what a grouch.")
                }
                op("I've come to repossess your house.") {
                    player(CALM_TALK, "I've come to repossess your house.")
                    npc(npc, SCARED, "On what grounds?")
                    options {
                        op("Repeated failure to make mortgage repayments.") {
                            player(CALM_TALK, "Repeated failure to make mortgage repayments.")
                            npc(npc, FRUSTRATED, "What?")
                            npc(npc, FRUSTRATED, "But I don't have a mortgage - I built this house myself.")
                            player(CALM, "Sorry, I must have got the wrong address. All the houses look the same around here.")
                            npc(npc, FRUSTRATED, "What? What houses? This is the only one. What are you talking about?")
                            player(CALM, "Never mind.")
                            goto("startOptions")
                        }
                        op("I don't know, I just wanted this house.") {
                            player(CALM_TALK, "I don't know, I just wanted this house.")
                            npc(npc, FRUSTRATED, "Oh, go away and stop wasting my time.")
                        }
                    }
                }
                if (p.getQuestStage(Quest.BUYERS_AND_CELLARS) == 8) {
                    op("Nice chalice.") { player(SKEPTICAL_THINKING, "Nice... um... never mind. Bye!") }
                }
                if (p.getQuestStage(Quest.BUYERS_AND_CELLARS) == 6 || p.getQuestStage(Quest.BUYERS_AND_CELLARS) == 7) {
                    op("Nice chalice.") {
                        player(CALM_TALK, "That's a nice chalice.")
                        label("chaliceOptions")
                        options {
                            op("Aren't you afraid it will be taken in lieu of mortgage payments?") {
                                player(CALM_TALK, "Aren't you afraid it will be taken in lieu of mortgage payments?")
                                npc(npc, FRUSTRATED, "Are you just here to bother me?")
                                goto("chaliceOptions")
                            }
                            op("The bailiffs are coming! Quick: hide the valuables!") {
                                player(SCARED, "The bailiffs are coming! Quick: hide the valuables!")
                                npc(npc, FRUSTRATED, "The thoughts I am entertaining about you are worth another three months' mediation, young " + p.getPronoun("man", "lady") + ".")
                                goto("chaliceOptions")
                            }
                            op("Can I have a look at that chalice?") {
                                player(SKEPTICAL_THINKING, "Can I have a look at that chalice?")
                                npc(npc, FRUSTRATED, "If you must. It's in the display case over there.")
                                player(SKEPTICAL_HEAD_SHAKE, "I meant...a closer look.")
                                npc(npc, FRUSTRATED, "It's only a couple of inches from the glass.")
                                player(CALM_TALK, "Can I hold it?")
                                npc(npc, ANGRY, "And get grubby fingermarks over it? I think not.")
                                goto("chaliceOptions")
                            }
                            op("Fire! Fire!") {
                                player(SCARED, "Fire! FIRE!")
                                if (!fireIsLit()) {
                                    npc(npc, FRUSTRATED, "Don't be foolish.")
                                    goto("startOptions")
                                } else {
                                    npc(npc, SCARED, "Oh, no! My house...that I built with my own two hands!")
                                    player(LAUGH, "Ha, now's my chance to pick his pocket while he's distracted...") {
                                        p.setQuestStage(Quest.BUYERS_AND_CELLARS, 7)
                                        npc.forceTalk("Accursed kids. Light a fire under my window?! I'll teach them a lesson when I find them...")
                                    }
                                }
                            }
                        }
                    }
                }
                op("Bye, then.") {
                    player(CALM_TALK, "Bye, then.")
                    npc(npc, FRUSTRATED, "Bah.")
                }
            }
        }
    }
}