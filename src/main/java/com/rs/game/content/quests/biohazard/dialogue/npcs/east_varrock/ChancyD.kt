package com.rs.game.content.quests.biohazard.dialogue.npcs.east_varrock

import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class ChancyD(player: Player, npc: NPC) {
    init {

        player.startConversation {
            when (val gaveItem = player.questManager.getAttribs(Quest.BIOHAZARD).getI(GAVE_CHANCY_VIAL_OF)) {

                LIQUID_HONEY -> {
                    player(CALM_TALK, "Hi, thanks for doing that.")
                    npc(npc, CALM_TALK, "No problem.")
                    exec {
                        if (player.inventory.hasFreeSlots()) {
                            player.cutscene {
                                BiohazardUtils(player).handleCollectItemFromErrandBoy(GAVE_CHANCY_VIAL_OF, gaveItem)
                                wait(3)
                                dialogue {
                                    npc(npc, CALM_TALK, "Next time give me something more valuable... I couldn't get anything for this on the black market.")
                                    player(CALM_TALK, "That was the idea.")
                                }
                            }
                        } else {
                            simple("You need at least 1 free inventory space before collecting the vial from ${npc.name}.")
                        }
                    }
                }

                SULPHURIC_BROLINE, ETHENEA -> {
                    player(CALM_TALK, "Hi, thanks for doing that.")
                    npc(npc, CALM_TALK, "No problem. I've got some money for you actually.") { BiohazardUtils(player).handleResetErrandBoy(GAVE_CHANCY_VIAL_OF) }
                    player(CONFUSED, "What do you mean?")
                    npc(npc, CALM_TALK, "Well, it turns out that potion you gave me, was quite valuable...")
                    player(CONFUSED, "What?")
                    npc(npc, CALM_TALK, "I know that I probably shouldn't have sold it... but some friends and I were having a little wager, the odds were just too good!")
                    player(FRUSTRATED, "You sold my vial and gambled with the money?!")
                    npc(npc, CALM_TALK, "Actually yes... but praise be to Saradomin because I won! So all's well that ends well right?")
                    options {
                        op("No! Nothing could be further from the truth!") {
                            player(FRUSTRATED, "No! Nothing could be further from the truth!")
                            npc(npc, CALM_TALK, "Well, there's no pleasing some people.")
                        }
                        op("You have no idea what you have just done!") {
                            player(FRUSTRATED, "You have no idea what you have just done!")
                            npc(npc, CALM_TALK, "Ignorance is bliss I'm afraid.")
                        }
                    }
                }

            }
        }
    }
}
