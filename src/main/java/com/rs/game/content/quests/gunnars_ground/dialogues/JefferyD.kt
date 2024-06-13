package com.rs.game.content.quests.gunnars_ground.dialogues

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.gunnars_ground.getHasItem
import com.rs.game.content.quests.gunnars_ground.setHasItem
import com.rs.game.content.quests.gunnars_ground.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class JefferyD (player: Player, npc: NPC) {
    init {
        player.startConversation {
            npc(npc, CALM, "Keep it quick. What do you want?")
            label("startOptions")
            options {
                when (player.questManager.getStage(Quest.GUNNARS_GROUND)) {

                    STAGE_RECEIVED_LOVE_POEM -> {
                        op("I'm here about a gold ring.") {
                            if (!getHasItem(player, LOVE_POEM)) {
                                player(SAD, "I don't seem to have the poem on me. Perhaps, I should go back to Dororan first!")
                            } else if (!player.containsOneItem(LOVE_POEM.id)) {
                                player(SAD, "I should fetch the poem from the bank first.")
                            } else {
                                player(CALM, "I'm here about a gold ring.")
                                npc(npc, FRUSTRATED, "You want to buy a gold ring? You want to sell a gold ring? You want to ask pointless questions about gold rings?")
                                options {
                                    op("I was hoping you would trade me a gold ring.") {
                                        player(CALM, "I was hoping you would trade me a gold ring.")
                                        npc(npc, SKEPTICAL_THINKING, "Trade you? Trade you for what?")
                                        options {
                                            op("This splendid love poem.") {
                                                player(CALM, "This splendid love poem.")
                                                goto("aLovePoem?")
                                            }
                                            op("Some old love poem or something.") {
                                                player(CALM, "Some old love poem or something.")
                                                label("aLovePoem?")
                                                npc(npc, CONFUSED, "A love poem? What?")
                                                npc(npc, FRUSTRATED, "Wait...that dwarf put you up to this, didn't he?")
                                                options {
                                                    op("Yes, he did.") {
                                                        player(HAPPY_TALKING, "Yes, he did.")
                                                        goto("cheekyLittle")
                                                    }
                                                    op("I don't know any dwarf.") {
                                                        player(SKEPTICAL_HEAD_SHAKE, "I don't know any dwarf.")
                                                        npc(npc, FRUSTRATED, "I recognise his awful handwriting.")
                                                        label("cheekyLittle")
                                                        npc(npc, FRUSTRATED, "That cheeky little...")
                                                        npc(npc, FRUSTRATED, "He can't just leave it alone, can he? Fine! I'll trade you for the poem. What is it you want?")
                                                        options {
                                                            op("Just a plain, gold ring.") {
                                                                player(HAPPY_TALKING, "Just a plain, gold ring.")
                                                                goto("jefferyTradesYou")
                                                            }
                                                            op("The most valuable diamond ring you have.") {
                                                                player(HAPPY_TALKING, "The most valuable diamond ring you have.")
                                                                npc(npc, TALKING_ALOT, "Well, all I have is this plain, gold ring. That will have to do.")
                                                                label("jefferyTradesYou")
                                                                item(RING_FROM_JEFFERY.id, "Jeffery trades you a gold ring for the poem.") {
                                                                    npc.anim(ANIM_TAKE_ITEM)
                                                                    player.inventory.deleteItem(LOVE_POEM)
                                                                    setHasItem(player, LOVE_POEM, false)
                                                                    player.inventory.addItem(RING_FROM_JEFFERY, true)
                                                                    setHasItem(player, RING_FROM_JEFFERY, true)
                                                                    player.anim(ANIM_GIVE_ITEM)
                                                                    player.questManager.setStage(Quest.GUNNARS_GROUND, STAGE_RECEIVED_RING)
                                                                }
                                                                npc(npc, FRUSTRATED, "Now, leave me in peace!")
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    op("Actually, forget it.") { player(CALM, "Actually, forget it.") }
                                }
                            }
                        }
                    }

                    STAGE_RECEIVED_RING -> {
                        if (!getHasItem(player, RING_FROM_JEFFERY)) {
                            op("I seem to have misplaced the ring you gave me.") {
                                player(SAD, "I seem to have misplaced the ring you gave me.")
                                npc(npc, CALM_TALK, "Luckily, I have more than one!")
                                item(RING_FROM_JEFFERY.id, "Jeffery gives you another gold ring.") {
                                    player.inventory.addItem(RING_FROM_JEFFERY, true)
                                    setHasItem(player, RING_FROM_JEFFERY, true)
                                }
                                npc(npc, FRUSTRATED, "Don't lose it this time!")
                                player(WORRIED, "I'll try!")
                            }
                        }
                    }

                    STAGE_COMPLETE -> {
                        op("Who was that love poem for?") {
                            player(CONFUSED, "Who was that love poem for?")
                            npc(npc, VERY_FRUSTRATED, "It, er, it didn't work out well. I don't want to talk about it! Leave me alone!")
                            goto("startOptions")
                        }
                    }

                    else -> {
                        op("Who was that love poem for?") {
                            player(CONFUSED, "Who was that love poem for?")
                            npc(npc, TALKING_ALOT, "I haven't had a chance to do anything with it yet!")
                            goto("startOptions")
                        }
                    }
                }
                op("I want to use the furnace.") { player(HAPPY_TALKING, "I want to use the furnace.") }
                op("Er...nothing.") { player(CALM_TALK, "Er...nothing.") }
            }
        }
    }
}
