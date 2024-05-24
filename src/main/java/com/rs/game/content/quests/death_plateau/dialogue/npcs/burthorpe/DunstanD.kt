package com.rs.game.content.quests.death_plateau.dialogue.npcs.burthorpe

import com.rs.engine.dialogue.DialogueBuilder
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.death_plateau.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class DunstanD(player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.DEATH_PLATEAU)) {

                in STAGE_UNSTARTED..STAGE_TAKE_BOOTS_DUNSTAN -> {
                    player(CALM_TALK, "Hi!")
                    npc(npc, SKEPTICAL_THINKING, "Hi! Did you want something?")
                    exec { optionsDialogue(player, npc, this)}
                }

                STAGE_RETURN_TO_FREDA -> {
                    player(CALM_TALK, "Hi!")
                    npc(npc, CALM_TALK, "Hi! What can I do for you?")
                    if (!player.containsOneItem(SPIKED_BOOTS)) {
                        player(CALM_TALK, "Could I get some more spiked boots please? I may have misplaced the ones you gave me.")
                        npc(npc, CALM_TALK, "Oh that's a laugh! Freda would tan your hide and use you as troll-bait if she found out!")
                        if (player.inventory.hasFreeSlots()) {
                            npc(npc, CALM_TALK, "Here, have these ones. They are an old pair Freda left here a while back. I'm sure she won't notice.") { player.inventory.addItem(SPIKED_BOOTS) }
                            player(CALM_TALK, "Thanks! I'll take these straight over to Freda!")
                        } else {
                            npc(npc, CALM_TALK, "I'd give you them, but you don't have enough room.")
                        }
                        exec { optionsDialogue(player, npc, this) }
                    } else {
                        exec { optionsDialogue(player, npc, this) }
                    }
                }

                in STAGE_RECEIVED_SURVEY.. STAGE_COMPLETE -> {
                    player(CALM_TALK, "Hi!")
                    npc(npc, SKEPTICAL_THINKING, "Hi! Did you want something?")
                    exec { optionsDialogue(player, npc, this)}
                }

            }

        }
    }

    private fun optionsDialogue(player: Player, npc: NPC, dialogue: DialogueBuilder) {
        val stage = player.questManager.getStage(Quest.DEATH_PLATEAU)
        dialogue.label("initialOps")
        dialogue.options {
            if (stage == STAGE_TAKE_BOOTS_DUNSTAN)
                op("Can you put some fresh spikes on these climbing boots for me?") {
                    if (!player.inventory.containsOneItem(CLIMBING_BOOTS)) {
                        npc(npc, CALM_TALK, "Really? What boots?")
                        player(CALM_TALK, "I...don't...know!")
                        npc(npc, CALM_TALK, "Maybe you should check with Freda if you don't know where you left them.")
                    } else {
                        npc(npc, CALM_TALK, "Hey, these are Freda's boots. Where did you get them?")
                        player(CALM_TALK, "She gave them to me to get re-spiked. Can you do it, please?")
                        npc(npc, CALM_TALK, "Oh, I suppose so. There you go then, here are your boots.")
                        item(SPIKED_BOOTS, "Dunstan quickly repairs the old boots.") {
                            player.inventory.replace(CLIMBING_BOOTS, SPIKED_BOOTS)
                            player.setQuestStage(Quest.DEATH_PLATEAU, STAGE_RETURN_TO_FREDA)
                        }
                    }
                }
            if (stage >= STAGE_RETURN_TO_FREDA)
                op("Can you put some spikes on my climbing boots?") {
                    npc(npc, CALM_TALK, "For you, no problem.")
                    npc(npc, CALM_TALK, "You do realise you can only use the climbing boots right now? The spiked boots can only be used in the Icelands but no-one's been able to get there for years!")
                    options {
                        op("Yes, but I still want them.") {
                            if (player.inventory.containsOneItem(IRON_BAR)) {
                                if (player.inventory.containsOneItem(CLIMBING_BOOTS)) {
                                    simple("You give Dunstan an iron bar and the climbing boots.")
                                    item(SPIKED_BOOTS, "Dunstan has given you the spiked boots.") {
                                        player.inventory.deleteItem(CLIMBING_BOOTS, 1)
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
