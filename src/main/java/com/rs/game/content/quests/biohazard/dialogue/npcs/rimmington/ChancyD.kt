package com.rs.game.content.quests.biohazard.dialogue.npcs.rimmington

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class ChancyD(player: Player, npc: NPC) {
    init {
        player.startConversation {
            val chancyItem = player.questManager.getAttribs(Quest.BIOHAZARD).getI(GAVE_CHANCY_VIAL_OF)

            if (chancyItem <= 2) {
                player(CALM_TALK, "Hello, I've got a vial for you to take to Varrock.")
                npc(npc, CALM_TALK, "Tssch... that chemist asks for a lot for the wages he pays.")
                player(CALM_TALK, "Maybe you should ask him for more money.")
                npc(npc, CALM_TALK, "Nah... I just use my initiative here and there.")
                options {
                    op("You give him the vial of ethenea...") {
                        if (player.inventory.containsOneItem(ETHENEA)) {
                            player(CALM_TALK, "Right. I'll see you later in the Dancing Donkey Inn.") { BiohazardUtils(player).handleGiveItemToErrandBoy(GAVE_CHANCY_VIAL_OF, ETHENEA) }
                            npc(npc, CALM_TALK, "Be lucky!")
                        } else {
                            simple("You can't give him what you don't have.")
                        }
                    }
                    op("You give him the vial of liquid honey...") {
                        if (player.inventory.containsOneItem(LIQUID_HONEY)) {
                            player(CALM_TALK, "Right. I'll see you later in the Dancing Donkey Inn.") { BiohazardUtils(player).handleGiveItemToErrandBoy(GAVE_CHANCY_VIAL_OF, LIQUID_HONEY) }
                            npc(npc, CALM_TALK, "Be lucky!")
                        } else {
                            simple("You can't give him what you don't have.")
                        }
                    }
                    op("You give him the vial of sulphuric broline...") {
                        if (player.inventory.containsOneItem(SULPHURIC_BROLINE)) {
                            player(CALM_TALK, "Right. I'll see you later in the Dancing Donkey Inn.") { BiohazardUtils(player).handleGiveItemToErrandBoy(GAVE_CHANCY_VIAL_OF, SULPHURIC_BROLINE) }
                            npc(npc, CALM_TALK, "Be lucky!")
                        } else {
                            simple("You can't give him what you don't have.")
                        }
                    }
                }

            } else {
                npc(npc, CALM_TALK, "Look, I've got your vial but I'm not taking two. I always like to play the percentages.")
            }

        }
    }
}
