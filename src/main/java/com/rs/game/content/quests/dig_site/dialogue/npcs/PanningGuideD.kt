package com.rs.game.content.quests.dig_site.dialogue.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.dig_site.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class PanningGuideD(val player: Player, val npc: NPC) {
    init {
        player.startConversation {
            if (player.questManager.getAttribs(Quest.DIG_SITE).getB(PANNING_GUIDE_GIVEN_TEA) || player.isQuestComplete(Quest.DIG_SITE)) {
                player(CALM_TALK, "Hello, who are you?")
                npc(npc, CALM_TALK, "Hello, I am the panning guide. I'm here to teach you how to pan for gold.")
                player(CALM_TALK, "Excellent!")
                npc(npc, CALM_TALK, "Let me explain how panning works. First, you need a panning tray. Use the tray in the panning points in the water and then search your tray.")
                npc(npc, CALM_TALK, "If you find any gold, take it to the Terry, the archaeological expert, in the Exam Centre. He will calculate its value for you.")
                options {
                    op("Can you tell me more about the tools an archaeologist uses?") {
                        player(CALM_TALK, "Can you tell me more about the tools an archaeologist uses?")
                        npc(npc, CALM_TALK, "Of course! Let's see now... Trowels are vital for fine digging work, so you can be careful to not damage or disturb any artefacts. Rock picks are for splitting rocks or scraping away soil.")
                        player(CALM_TALK, "What about specimen jars and brushes?")
                        npc(npc, CALM_TALK, "Those are essential for carefully cleaning and storing smaller samples.")
                        player(CALM_TALK, "Where can I get any of these things?")
                        npc(npc, CALM_TALK, "Well, we've come into a bit more funding of late, so there should be a stock of each of them in the Exam Centre's tools cupboard. We also hand out relevant tools as students complete each level of their Earth Sciences exams.")
                        player(CALM_TALK, "Anything else?")
                        npc(npc, CALM_TALK, "If you need something identified or are not sure about something, give it to Terry. He's the archaeological expert in the next room.")
                        player(CALM_TALK, "Ah, okay, thanks.")
                    }
                    op("Thank you!") { player(CALM_TALK, "Thank you!") }
                }
            } else {
                player(CALM_TALK, "Hello, who are you?")
                npc(npc, CALM_TALK, "Hello, I am the panning guide. I teach students how to pan in these waters. They're not permitted to do so until after they've had training and, of course, they must be invited to pan here too.")
                player(CALM_TALK, "So, how do I become invited?")
                npc(npc, CALM_TALK, "I'm not supposed to let people pan here unless they have permission. Mind you, I could let you have a go if you're willing to do me a favour...")
                player(CALM_TALK, "What's that?")
                npc(npc, CALM_TALK, "Well, to be honest, what I would really like is a nice cup of tea!")
                if (player.inventory.containsOneItem(CUP_OF_TEA)) {
                    player(CALM_TALK, "I've some here that you can have.")
                    npc(npc, CALM_TALK, "Ah! Lovely! You can't beat a good cuppa! You're free to pan all you want.") {
                        player.inventory.deleteItem(CUP_OF_TEA, 1)
                        player.questManager.getAttribs(Quest.DIG_SITE).setB(PANNING_GUIDE_GIVEN_TEA, true)
                    }
                } else {
                    player(CALM_TALK, "Tea?")
                    npc(npc, CALM_TALK, "Absolutely, I'm parched!")
                    npc(npc, CALM_TALK, "If you could bring me one of those, I would be more than willing to let you pan here. I usually get some from the main campus building, but I'm busy at the moment.")
                }
            }
        }
    }

    fun notAllowedToPan() {
        player.faceEntityTile(npc)
        npc.faceEntityTile(player)
        player.startConversation {
            npc(npc, FRUSTRATED, "Hey! You can't pan yet!")
            player(CONFUSED, "Why not?")
            npc(npc, CALM_TALK, "We do not allow the uninvited to pan here.")
            player(SKEPTICAL_THINKING, "So how do I become invited then?")
            npc(npc, CALM_TALK, "I'm not supposed to let people pan here unless they have permission from the authorities first. Mind you, I could let you have a go if you're willing to do me a favour.")
            player(CALM_TALK, "What's that?")
            npc(npc, CALM_TALK, "Well, to be honest, what I would really like is a nice cup of tea!")
            if (player.inventory.containsOneItem(CUP_OF_TEA)) {
                player(CALM_TALK, "I've some here that you can have.")
                npc(npc, CALM_TALK, "Ah! Lovely! You can't beat a good cuppa! You're free to pan all you want.") {
                    player.inventory.deleteItem(CUP_OF_TEA, 1)
                    player.questManager.getAttribs(Quest.DIG_SITE).setB(PANNING_GUIDE_GIVEN_TEA, true)
                }
            } else {
                player(CALM_TALK, "Tea?!")
                npc(npc, CALM_TALK, "Absolutely, I'm parched!")
                npc(npc, CALM_TALK, "If you could bring me one of those, I would be more than willing to let you pan here. I usually get some from the main campus building, but I'm busy at the moment.")
            }
        }
    }

}
