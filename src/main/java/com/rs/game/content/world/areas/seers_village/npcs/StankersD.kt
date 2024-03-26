package com.rs.game.content.world.areas.seers_village.npcs

import com.rs.engine.dialogue.*
import com.rs.game.content.achievements.AchievementSystemD
import com.rs.game.content.achievements.SetReward
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.utils.shop.ShopsHandler

class StankersD(player: Player, npc: NPC) : Conversation(player) {
    init {
        player.startConversation {
            npc(npc.id, HeadE.HAPPY_TALKING, "Hello, bold adventurer.")
            label("initialOps")
            options {
                op("Are these your trucks?") {
                    player(HeadE.CALM, "Are these your trucks?")
                    npc(npc.id, HeadE.CALM, "Yes, I use them to transport coal over the river. I will let other people use them too, I'm a nice person like that...")
                    npc(npc.id, HeadE.CALM, "Just put coal in a truck and I'll move it down to my depot over the river.")
                    goto("initialOps")
                }
                op("Hello, Mr Stankers.") {
                    player(HeadE.CHEERFUL, "Hello, Mr Stankers.")
                    npc(npc.id, HeadE.CALM, "Would you like a poison chalice?")
                    label("chaliceOps")
                    options("Would you like a poison chalice?") {
                        op("Yes, please.") {
                            player(HeadE.NO_EXPRESSION, "Yes, please.")
                            npc(npc.id, HeadE.CHEERFUL, "Take one from my shop. You might consider buying one of my fine pickaxes while you're looking.")
                            player(HeadE.CONFUSED, "Pickaxes?")
                            npc(npc.id, HeadE.CALM, "Yes, I've started a business. It's not going very well so far, but the poison chalices are quite a nice loss-leader.")
                            exec { ShopsHandler.openShop(player, "stankers_bronze_pickaxes") }
                        }
                        op("What's a poison chalice?") {
                            player(HeadE.CONFUSED, "What's a poison chalice?")
                            npc(npc.id, HeadE.CHEERFUL, "It's an exciting drink I've invented. I don't know what it tastes like, I haven't tried it myself.")
                            goto("chaliceOps")
                        }
                        op("No, thank you.") {
                            player(HeadE.NONE, "No, thank you.")
                            goto("initialOps")
                        }
                    }
                }
                op("About the Achievement System...") { AchievementSystemD(player, npc.id, SetReward.SEERS_HEADBAND) }
            }
        }
    }
}