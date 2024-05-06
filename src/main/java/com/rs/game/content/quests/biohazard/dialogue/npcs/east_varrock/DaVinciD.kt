package com.rs.game.content.quests.biohazard.dialogue.npcs.east_varrock

import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class DaVinciD(player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (val gaveItem = player.questManager.getAttribs(Quest.BIOHAZARD).getI(GAVE_DA_VINCI_VIAL_OF)) {

                ETHENEA -> {
                    npc(npc, CALM_TALK, "Hello again. I hope your journey was as pleasant as mine.")
                    player(CALM_TALK, "Well, as they say, it's always sunny in Gielinor.")
                    npc(npc, CALM_TALK, "Ok, here it is.")
                    exec {
                        if (player.inventory.hasFreeSlots()) {
                            player.cutscene {
                                BiohazardUtils(player).handleCollectItemFromErrandBoy(GAVE_DA_VINCI_VIAL_OF, gaveItem)
                                wait(3)
                                dialogue {
                                    player(CALM_TALK, "Thanks, you've been a big help.")
                                }
                            }
                        } else {
                            simple("You need at least 1 free inventory space before collecting the vial from ${npc.name}.")
                        }
                    }
                }

                SULPHURIC_BROLINE, LIQUID_HONEY -> {
                    npc(npc, CALM_TALK, "Hello again. I hope your journey was as pleasant as mine.")
                    player(CALM_TALK, "Yep. Anyway, I'll take the package off you now.") { BiohazardUtils(player).handleResetErrandBoy(GAVE_DA_VINCI_VIAL_OF) }
                    npc(npc, CALM_TALK, "Package? That's a funny way to describe a liquid of such exquisite beauty!")
                    options {
                        op("I'm getting a bad feeling about this.") {
                            player(CALM_TALK, "I'm getting a bad feeling about this.")
                            goto("stillHaveIt")
                        }
                        op("Just give me the stuff now please.") {
                            player(CALM_TALK, "Just give me the stuff now please.")
                            goto("stillHaveIt")
                        }
                    }
                    label("stillHaveIt")
                    player(CALM_TALK, "You do still have it don't you?")
                    npc(npc, CALM_TALK, "Absolutely. It's just not stored in a vial anymore.")
                    player(CALM_TALK, "What?")
                    npc(npc, CALM_TALK, "Instead it has been liberated. It now gleams from the canvas of my latest epic: The Majesty of Varrock!")
                    player(CALM_TALK, "That's great. Thanks to you I'll have to walk back to East Ardougne to get another vial.")
                    npc(npc, CALM_TALK, "Well you can't put a price on art.")
                }

            }
        }
    }
}
