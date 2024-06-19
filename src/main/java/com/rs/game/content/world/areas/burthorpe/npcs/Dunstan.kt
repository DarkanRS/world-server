package com.rs.game.content.world.areas.burthorpe.npcs

import com.rs.engine.dialogue.DialogueBuilder
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.death_plateau.utils.*
import com.rs.game.content.quests.troll_stronghold.utils.*
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.onNpcClick

class Dunstan(id: Int, tile: Tile) : NPC(id, tile) {
    override fun faceEntityTile(target: Entity?) { }
}

class DunstanD(val player: Player, npc: NPC) {
    private val lampsLost = player.getI(TROLL_STRONGHOLD_QUEST_LAMPS_LOST)

    init {
        if (!player.isQuestStarted(Quest.TROLL_STRONGHOLD)) {
            com.rs.game.content.quests.death_plateau.dialogue.npcs.burthorpe.DunstanD(player, npc)
        } else {
            player.startConversation {
                when (player.getQuestStage(Quest.TROLL_STRONGHOLD)) {
                    in STAGE_ACCEPTED_QUEST..STAGE_UNLOCKED_PRISON_DOOR -> {
                        npc(npc, CALM_TALK, "Have you managed to rescue Godric yet?")
                        player(SAD, "Not yet.")
                        npc(npc, CALM_TALK, "Please hurry! Who knows what they will do to him?")
                        npc(npc, SKEPTICAL, "Is there anything I can do in the meantime?")
                        exec { optionsDialogue(player, npc, this) }
                    }

                    STAGE_UNLOCKED_BOTH_CELLS -> {
                        player(CALM_TALK, "Has Godric returned home?")
                        npc(npc, HAPPY_TALKING, "He is safe and sound, thanks to you my friend!")
                        player(CALM_TALK, "I'm glad to hear it.")
                        npc(npc, CALM_TALK, "I have very little to offer you by way of thanks, but perhaps you will accept these family heirlooms.")
                        npc(npc, CALM_TALK, "They were found by my great-great-grandfather, but we still don't have any idea what they do.")
                        exec {
                            if (player.inventory.freeSlots >= 2) {
                                player.completeQuest(Quest.TROLL_STRONGHOLD)
                            } else {
                                simple("You need at least 2 free inventory slots to accept your reward.")
                                npc(npc, SKEPTICAL, "Is there anything I can do in the meantime?")
                                exec { optionsDialogue(player, npc, this) }
                            }
                        }
                    }

                    com.rs.game.content.quests.troll_stronghold.utils.STAGE_COMPLETE -> {
                        when (lampsLost) {
                            1 -> {
                                player(CALM_TALK, "I'm afraid I lost the lamp you gave me.")
                                if (player.inventory.hasFreeSlots()) {
                                    npc(npc, CALM_TALK, "You're in luck! Someone found it and brought it back to me. Here you go.") {
                                        TrollStrongholdUtils(player).returnLostLamps(1)
                                    }
                                    npc(npc, CALM_TALK, "Is there anything else I can do for you?")
                                } else {
                                    npc(npc, CALM_TALK, "You're in luck! Someone found them and brought them back to me. You'll need 1 free inventory slot before I can return it to you.")
                                    npc(npc, SKEPTICAL, "Is there anything I can do in the meantime?")
                                }
                                exec { optionsDialogue(player, npc, this) }
                            }

                            2 -> {
                                player(CALM_TALK, "I'm afraid I lost the lamps you gave me.")
                                if (player.inventory.freeSlots >= 2) {
                                    npc(npc, CALM_TALK, "You're in luck! Someone found them and brought them back to me. Here you go.") {
                                        TrollStrongholdUtils(player).returnLostLamps(2)
                                    }
                                    npc(npc, CALM_TALK, "Is there anything else I can do for you?")
                                } else {
                                    npc(npc, CALM_TALK, "You're in luck! Someone found them and brought them back to me. You'll need 2 free inventory slots before I can return them to you.")
                                    npc(npc, SKEPTICAL, "Is there anything I can do in the meantime?")
                                }
                                exec { optionsDialogue(player, npc, this) }
                            }

                            else -> {
                                player(CALM_TALK, "Hi!")
                                npc(npc, SKEPTICAL_THINKING, "Hi! Did you want something?")
                                exec { optionsDialogue(player, npc, this)}
                            }
                        }
                    }

                }
            }
        }
    }

    private fun optionsDialogue(player: Player, npc: NPC, dialogue: DialogueBuilder) {
        dialogue.label("initialOps")
        dialogue.options {
            op("Can you put some spikes on my climbing boots?") {
                npc(npc, CALM_TALK, "For you, no problem.")
                npc(npc, CALM_TALK, "You do realise you can only use the climbing boots right now? The spiked boots can only be used in the Icelands but no-one's been able to get there for years!")
                options {
                    op("Yes, but I still want them.") {
                        if (player.inventory.containsOneItem(IRON_BAR)) {
                            if (player.inventory.containsOneItem(CLIMBING_BOOTS_POST_DEATH_PLATEAU)) {
                                simple("You give Dunstan an iron bar and the climbing boots.")
                                item(SPIKED_BOOTS, "Dunstan has given you the spiked boots.") {
                                    player.inventory.deleteItem(CLIMBING_BOOTS_POST_DEATH_PLATEAU, 1)
                                    player.inventory.deleteItem(IRON_BAR, 1)
                                    player.inventory.addItem(SPIKED_BOOTS)
                                }
                            } else {
                                player(CALM_TALK, "I don't have them on me.")
                            }
                        } else {
                            npc(npc, CALM_TALK, "Sorry, I'll need an iron bar to make the spikes.")
                        }

                        npc(npc, CALM_TALK, "Anything else before I get on with my work?")
                        goto("initialOps")
                    }
                    op("Oh okay, I'll leave them thanks.") {
                        npc(npc, CALM_TALK, "Anything else before I get on with my work?")
                        goto("initialOps")
                    }
                }
            }
            op("Is it okay if I use your anvil?") {
                npc(npc, SKEPTICAL, "So you're a smith are you?")
                player(HAPPY_TALKING, "I dabble.")
                npc(npc, HAPPY_TALKING, "A fellow smith is welcome to use my anvil!")
                player(HAPPY_TALKING, "Thanks!")
            }
            op("Nothing, thanks.") {
                npc(npc, CALM_TALK, "All right. Speak to you later then.")
            }
        }
    }

}

@ServerStartupEvent
fun mapDunstan() {
    onNpcClick(1082) { (player, npc) -> DunstanD(player, npc) }
    instantiateNpc(1082) { id, tile -> Dunstan(id, tile) }
}
