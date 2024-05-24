package com.rs.game.content.quests.biohazard.dialogue.npcs.rimmington

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class DaVinciD(player: Player, npc: NPC) {
    init {
        player.startConversation {
            val daVinciItem = player.questManager.getAttribs(Quest.BIOHAZARD).getI(GAVE_DA_VINCI_VIAL_OF)

            if (daVinciItem <= 2) {
                player(CALM_TALK, "Hello, I hear you're an errand boy for the chemist.")
                npc(npc, CALM_TALK, "Well that's my job yes. But I don't necessarily define my identity in such black and white terms.")
                player(CALM_TALK, "Good for you. Now can you take a vial to Varrock for me?")
                npc(npc, CALM_TALK, "Go on then.")
                options {
                    op("You give him the vial of ethenea...") {
                        if (player.inventory.containsOneItem(ETHENEA)) {
                            player(CALM_TALK, "Ok, we're meeting at the Dancing Donkey in Varrock right?") { BiohazardUtils(player).handleGiveItemToErrandBoy(GAVE_DA_VINCI_VIAL_OF, ETHENEA) }
                            npc(npc, CALM_TALK, "That's right.")
                        } else {
                            simple("You can't give him what you don't have.")
                        }
                    }
                    op("You give him the vial of liquid honey...") {
                        if (player.inventory.containsOneItem(LIQUID_HONEY)) {
                            player(CALM_TALK, "Ok, we're meeting at the Dancing Donkey in Varrock right?") { BiohazardUtils(player).handleGiveItemToErrandBoy(GAVE_DA_VINCI_VIAL_OF, LIQUID_HONEY) }
                            npc(npc, CALM_TALK, "That's right.")
                        } else {
                            simple("You can't give him what you don't have.")
                        }
                    }
                    op("You give him the vial of sulphuric broline...") {
                        if (player.inventory.containsOneItem(SULPHURIC_BROLINE)) {
                            player(CALM_TALK, "Ok, we're meeting at the Dancing Donkey in Varrock right?") { BiohazardUtils(player).handleGiveItemToErrandBoy(GAVE_DA_VINCI_VIAL_OF, SULPHURIC_BROLINE) }
                            npc(npc, CALM_TALK, "That's right.")
                        } else {
                            simple("You can't give him what you don't have.")
                        }
                    }
                }

            } else {
                npc(npc, CALM_TALK, "Oh, it's you again. Please don't distract me now, I'm contemplating the sublime.")
            }

        }
    }
}
