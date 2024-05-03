package com.rs.game.content.quests.biohazard.dialogue.npcs.rimmington

import com.rs.engine.dialogue.DialogueBuilder
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class ChemistD(player: Player, npc: NPC, dialogue: DialogueBuilder) {
    init {
        when (player.questManager.getStage(Quest.BIOHAZARD)) {

            STAGE_RECEIVED_VIALS -> {
                dialogue.npc(npc, CALM_TALK, "Sorry, I'm afraid we're just closing now. You'll have to come back another time.")
                dialogue.options {
                    op("This can't wait, I'm carrying a plague sample.") {
                        player(CALM_TALK, "This can't wait, I'm carrying a plague sample.")
                        label("youIdiot")
                        npc(npc, MORTIFIED, "You idiot! A plague sample should be confined to a lab! I'm taking it off you. I'm afraid it's the only responsible thing to do.") {
                            if (player.inventory.containsOneItem(PLAGUE_SAMPLE)) player.inventory.deleteItem(PLAGUE_SAMPLE, player.inventory.getAmountOf(PLAGUE_SAMPLE))
                        }
                    }
                    op("It's ok, I'm Elena's friend.") {
                        player(CALM_TALK, "It's ok, I'm Elena's friend.")
                        npc(npc, CALM_TALK, "Oh, well that's different then. Must be pretty important to come all this way.")
                        npc(npc, SKEPTICAL, "How's everyone doing there anyway? Wasn't there some plague scare?")
                        options {
                            op("I need some more touch paper for this plague sample.") {
                                player(CALM_TALK, "I need some more touch paper for this plague sample.")
                                goto("youIdiot")
                            }
                            op("I just need some touch paper for a guy called Guidor.") {
                                player(CALM_TALK, "Who knows... I just need some touch paper for a guy called Guidor.")
                                npc(npc, CALM_TALK, "Guidor? This one's on me then... the poor guy. Sorry for the interrogation.")
                                npc(npc, WORRIED, "It's just that there've been rumours of a ${player.genderTerm("man", "woman")} travelling with the plague on ${player.genderTerm("him", "her")}.")
                                npc(npc, CALM_TALK, "They're even doing spot checks in Varrock. It's a pharmaceutical disaster!")
                                player(CALM_TALK, "Oh right... so am I going to be ok carrying these three vials with me?")
                                npc(npc, AMAZED_MILD, "With touch paper as well? You're asking for trouble. You'd better use my errand boys, outside. Give them a vial each.")
                                npc(npc, CALM_TALK, "They're not the most reliable people in the world. One's a painter, one's a gambler, and one's a drunk. Still if you pay peanuts you'll get monkeys, right?")
                                npc(npc, CALM_TALK, "It's better than entering Varrock with half a laboratory in your napsack.")
                                player(CALM_TALK, "Ok, thanks for your help. I know Elena appreciates it.")
                                npc(npc, CALM_TALK, "Yes well don't stand around here gassing. You'd better hurry if you want to see Guidor... He won't be around for much longer.") {
                                    if (player.inventory.hasFreeSlots()) {
                                        player.sendMessage("He gives you the touch paper.")
                                        player.inventory.addItem(TOUCH_PAPER)
                                        player.questManager.setStage(Quest.BIOHAZARD, STAGE_RECEIVED_TOUCH_PAPER)
                                    } else {
                                        player.sendMessage("He shows you the touch paper, but you don't have enough room to take it from him.")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            STAGE_RECEIVED_TOUCH_PAPER -> {
                dialogue.player(CALM_TALK, "Hello again.")
                dialogue.npc(npc, CALM_TALK, "Oh hello, do you need more touch paper?")
                if (player.inventory.containsOneItem(TOUCH_PAPER)) {
                    dialogue.player(CALM_TALK, "No, I just wanted to say hello.")
                    dialogue.npc(npc, CALM_TALK, "Oh.. ok then... hello.")
                    dialogue.player(CALM_TALK, "Hi.")
                } else {
                    dialogue.player(CALM_TALK, "Yes please.")
                    dialogue.npc(npc, CALM_TALK, "Ok, here you go.") {
                        if (player.inventory.hasFreeSlots()) {
                            player.sendMessage("The chemist gives you some touch paper.")
                            player.inventory.addItem(TOUCH_PAPER)
                            BiohazardUtils(player).resetAllErrandBoyItems()
                        } else {
                            player.sendMessage("The chemist you the touch paper, but you don't have enough room to take it from him.")
                        }
                    }
                }
            }

        }
    }
}
