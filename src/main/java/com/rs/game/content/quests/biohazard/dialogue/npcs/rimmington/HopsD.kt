package com.rs.game.content.quests.biohazard.dialogue.npcs.rimmington

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class HopsD(player: Player, npc: NPC) {
    init {
        player.startConversation {
            val hopsItem = player.questManager.getAttribs(Quest.BIOHAZARD).getI(GAVE_HOPS_VIAL_OF)

            if (hopsItem <= 2) {
                player(CALM_TALK, "Hi, I've got something for you to take to Varrock.")
                npc(npc, CALM_TALK, "Sounds like pretty thirsty work.")
                player(CALM_TALK, "Well, there's an Inn in Varrock if you're desperate.")
                npc(npc, CALM_TALK, "Don't worry, I'm a pretty resourceful fellow you know.")
                options {
                    op("You give him the vial of ethenea...") {
                        if (player.inventory.containsOneItem(ETHENEA)) {
                            player(CALM_TALK, "Ok, I'll see you in Varrock.") { BiohazardUtils(player).handleGiveItemToErrandBoy(GAVE_HOPS_VIAL_OF, ETHENEA) }
                            npc(npc, CALM_TALK, "Sure, I'm a regular at the Dancing Donkey Inn as it happens.")
                        } else {
                            simple("You can't give him what you don't have.")
                        }
                    }
                    op("You give him the vial of liquid honey...") {
                        if (player.inventory.containsOneItem(LIQUID_HONEY)) {
                            player(CALM_TALK, "Ok, I'll see you in Varrock.") { BiohazardUtils(player).handleGiveItemToErrandBoy(GAVE_HOPS_VIAL_OF, LIQUID_HONEY) }
                            npc(npc, CALM_TALK, "Sure, I'm a regular at the Dancing Donkey Inn as it happens.")
                        } else {
                            simple("You can't give him what you don't have.")
                        }
                    }
                    op("You give him the vial of sulphuric broline...") {
                        if (player.inventory.containsOneItem(SULPHURIC_BROLINE)) {
                            player(CALM_TALK, "Ok, I'll see you in Varrock.") { BiohazardUtils(player).handleGiveItemToErrandBoy(GAVE_HOPS_VIAL_OF, SULPHURIC_BROLINE) }
                            npc(npc, CALM_TALK, "Sure, I'm a regular at the Dancing Donkey Inn as it happens.")
                        } else {
                            simple("You can't give him what you don't have.")
                        }
                    }
                }

            } else {
                npc(npc, CALM_TALK, "I suppose I'd better get going. I'll meet you at the Dancing Donkey Inn.")
            }

        }
    }
}
