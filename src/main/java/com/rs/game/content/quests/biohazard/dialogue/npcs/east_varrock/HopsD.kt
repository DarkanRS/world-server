package com.rs.game.content.quests.biohazard.dialogue.npcs.east_varrock

import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class HopsD(player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (val gaveItem = player.questManager.getAttribs(Quest.BIOHAZARD).getI(GAVE_HOPS_VIAL_OF)) {

                SULPHURIC_BROLINE -> {
                    player(CALM_TALK, "Hello, how was your journey?")
                    npc(npc, CALM_TALK, "Pretty thirst-inducing actually...")
                    player(CALM_TALK, "Please tell me that you haven't drunk the contents...")
                    npc(npc, CALM_TALK, "Oh the gods no! What do you take me for? Here's your vial anyway.")
                    exec {
                        if (player.inventory.hasFreeSlots()) {
                            player.cutscene {
                                BiohazardUtils(player).handleCollectItemFromErrandBoy(GAVE_HOPS_VIAL_OF, gaveItem)
                                wait(3)
                                dialogue {
                                    player(CALM_TALK, "Thanks, I'll let you get your drink now.")
                                }
                            }
                        } else {
                            simple("You need at least 1 free inventory space before collecting the vial from ${npc.name}.")
                        }
                    }
                }

                LIQUID_HONEY, ETHENEA -> {
                    player(CALM_TALK, "Hello, how was your journey?")
                    npc(npc, CALM_TALK, "Pretty thirst-inducing actually...")
                    player(CALM_TALK, "Please tell me that you haven't drunk the contents...")
                    npc(npc, CALM_TALK, "Of course I can tell you that I haven't drunk the contents...") { BiohazardUtils(player).handleResetErrandBoy(GAVE_HOPS_VIAL_OF) }
                    npc(npc, CALM_TALK, "But I'd be lying. Sorry about that me old mucker, can I get you a drink?")
                    player(FRUSTRATED, "No, I think you've done enough for now.")
                }

            }
        }
    }
}
