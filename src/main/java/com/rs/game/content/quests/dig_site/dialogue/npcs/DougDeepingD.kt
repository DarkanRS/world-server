package com.rs.game.content.quests.dig_site.dialogue.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.dig_site.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class DougDeepingD(val player: Player, val npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.DIG_SITE)) {

                in STAGE_PERMISSION_GRANTED..STAGE_COMPLETE -> {
                    player(CALM_TALK, "Hello.")
                    npc(npc, CALM_TALK, "Well, well... I have a visitor. What are you doing here?")
                    options {
                        op("I have been invited to research here.") {
                            player(CALM_TALK, "I have been invited to research here.")
                            npc(npc, CALM_TALK, "Indeed, you must be someone special to be allowed down here.")
                            options {
                                op("Do you know where to find a specimen jar?") {
                                    player(CALM_TALK, "Do you know where to find a specimen jar?")
                                    npc(npc, CALM_TALK, "Hmmm, let me think... Nope, can't help you there, I'm afraid. Try asking at the Exam Centre.")
                                }
                                op("I have things to do...") {
                                    player(CALM_TALK, "I have things to do...")
                                    npc(npc, CALM_TALK, "Of course, don't let me keep you.")
                                }
                            }
                        }
                        op("I'm not really sure.") {
                            player(CALM_TALK, "I'm not really sure.")
                            npc(npc, CALM_TALK, "A miner without a clue - how funny!")
                        }
                        op("I'm here to get rich, rich, rich!") {
                            player(CALM_TALK, "I'm here to get rich, rich, rich!")
                            npc(npc, CALM_TALK, "Oh, well, don't forget that wealth and riches aren't everything.")
                        }
                        if (player.getQuestStage(Quest.DIG_SITE) == STAGE_SPEAK_TO_DOUG)
                            op("How could I move a large pile of rocks?") {
                                player(CALM_TALK, "How do you move a large pile of rocks?")
                                npc(npc, CALM_TALK, "There used to be this chap that worked in the other shaft. He was working on an explosive chemical mixture to be used for clearing blocked areas underground.")
                                npc(npc, CALM_TALK, "He left in a hurry one day. Apparently, something in the shaft scared him to death, but he didn't say what.")
                                player(CALM_TALK, "Oh?")
                                npc(npc, CALM_TALK, "Rumour has it he'd been writing a book on his chemical mixture. I'm not sure what goes in it but I'm sure you'll find the stuff he was using scattered around the digsite. He left so quickly he didn't take anything with")
                                npc(npc, CALM_TALK, "him. In fact, I still have a chest key he gave me to look after; perhaps it's more useful to you.")
                                if (player.inventory.containsOneItem(CHEST_KEY)) {
                                    player(CALM_TALK, "It's okay, I already have one.")
                                } else {
                                    if (player.inventory.hasFreeSlots()) {
                                        item(CHEST_KEY, "Doug hands you a key.") {
                                            player.inventory.addItem(CHEST_KEY)
                                        }
                                    } else {
                                        item(CHEST_KEY, "Doug shows you a key, but you don't have enough room to take it from him.")
                                    }
                                }
                            }
                    }
                }

            }
        }
    }
}
