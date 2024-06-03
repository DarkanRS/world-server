package com.rs.game.content.quests.druidic_ritual.dialogues.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.model.entity.player.Player
import com.rs.game.content.quests.druidic_ritual.utils.*
import com.rs.game.model.entity.npc.NPC

class SanfewD(player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.DRUIDIC_RITUAL)) {

                STAGE_SPEAK_TO_SANFEW -> {
                    player(CALM_TALK, "Hello there.")
                    npc(npc, CALM_TALK, "What can I do for you young 'un?") { player.voiceEffect(77263, false) }
                    player(CALM_TALK, "I've been sent to assist you with the ritual to purify the Varrockian stone circle.") { player.packets.resetSounds() }
                    npc(npc, CALM_TALK, "Well, what I'm struggling with right now is the meats needed for the potion to honour Guthix. I need the raw meats of four different animals for it, ")
                    npc(npc, CALM_TALK, "but not just any old meats will do. Each meat has to be dipped individually into the Cauldron of Thunder for it to work correctly.")
                    npc(npc, CALM_TALK, "I will need 4 raw meats put into the cauldron. They are rat, bear, beef and chicken")
                    options {
                        op("Where can I find this cauldron?") {
                            player(CALM_TALK, "Where can I find this cauldron?")
                            npc(npc, CALM_TALK, "It is located somewhere in the mysterious underground halls which are located somewhere in the woods just South of here. They are too dangerous for me to go myself however.")
                            player(CALM_TALK, "Ok, I'll go do that then.") { player.setQuestStage(Quest.DRUIDIC_RITUAL, STAGE_GATHER_MEATS) }
                        }
                        opExec("Ok, I'll go do that then.") { player.setQuestStage(Quest.DRUIDIC_RITUAL, STAGE_GATHER_MEATS) }
                    }
                }

                STAGE_GATHER_MEATS -> {
                    npc(npc, CALM_TALK, "Did you bring me the required ingredients for the potion?")
                    if (DruidicRitualUtils(player).hasEnchantedItems()) {
                        player(CALM_TALK, "Yes, I have all four now!")
                        npc(npc, CALM_TALK, "Well hand 'em over then lad! Thank you so much adventurer! These meats will allow our potion, to honour Guthix, to be completed, and bring us one step closer to reclaiming our stone circle!")
                        npc(npc, CALM_TALK, "Now go and talk to Kaqemeex and he will introduce you to the wonderful world of herblore and potion making!") {
                            player.inventory.deleteItem(ENCHANTED_RAW_BEAR_MEAT, 1)
                            player.inventory.deleteItem(ENCHANTED_RAW_CHICKEN, 1)
                            player.inventory.deleteItem(ENCHANTED_RAW_RAT_MEAT, 1)
                            player.inventory.deleteItem(ENCHANTED_RAW_BEEF, 1)
                            player.setQuestStage(Quest.DRUIDIC_RITUAL, STAGE_RETURN_TO_KAQEMEEX)
                        }
                    } else {
                        player(CALM_TALK, "No, not yet...")
                        npc(npc, CALM_TALK, "Well, let me know when you do young 'un.")
                        player(CALM_TALK, "I'll get on with it.")
                    }
                }

                STAGE_RETURN_TO_KAQEMEEX -> {
                    npc(npc, CALM_TALK, "Thank you so much adventurer! These meats will allow our potion, to honour Guthix, to be completed, and bring us one step closer to reclaiming our stone circle!")
                    npc(npc, CALM_TALK, "Now go and talk to Kaqemeex and he will introduce you to the wonderful world of herblore and potion making!")
                }

            }
        }
    }
}
