package com.rs.game.content.world.areas.dig_site.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.dig_site.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class Researcher(player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.getQuestStage(Quest.DIG_SITE)) {
                in STAGE_UNSTARTED..STAGE_BLOWN_UP_BRICKS -> {
                    npc(npc, CALM_TALK, "Hello there. What are you doing here?")
                    player(CALM_TALK, "Just looking around at the moment.")
                    npc(npc, CALM_TALK, "Well, feel free to talk to me should you come across anything you can't figure out.")
                    options {
                        op("Can you tell me more about the tools an archaeologist uses?") {
                            player(CALM_TALK, "Can you tell me more about the tools an archaeologist uses?")
                            npc(npc, CALM_TALK, "Of course! Let's see now...")
                            npc(npc, CALM_TALK, "Trowels are vital for fine digging work, so you can be careful to not damage or disturb any artefacts.")
                            npc(npc, CALM_TALK, "Rock picks are for splitting rocks or scraping away soil.")
                            player(CALM_TALK, "What about specimen jars and brushes?")
                            npc(npc, CALM_TALK, "Those are essential for carefully cleaning and storing smaller samples.")
                            player(CALM_TALK, "Where can I get any of these things?")
                            npc(npc, CALM_TALK, "Well, we've come into a bit more funding of late, so there should be a stock of each of them in the Exam Centre's tools cupboard.")
                            npc(npc, CALM_TALK, "We also hand out relevant tools as students complete each level of their Earth Sciences exams.")
                            player(CALM_TALK, "Anything else?")
                            npc(npc, CALM_TALK, "If you need something identified or are not sure about something, give it to Terry. He's the archaeological expert in the next room.")
                            player(CALM_TALK, "Ah, okay, thanks.")
                        }
                        op("Thank you!") { player(CALM_TALK, "Thank you!") }
                    }
                }
                STAGE_COMPLETE -> {
                    npc(npc, CALM_TALK, "Hello there! I heard all about your big find; well done!")
                    options {
                        op("Can you tell me more about the tools an archaeologist uses?") {
                            player(CALM_TALK, "Can you tell me more about the tools an archaeologist uses?")
                            npc(npc, CALM_TALK, "Of course! Let's see now...")
                            npc(npc, CALM_TALK, "Trowels are vital for fine digging work, so you can be careful to not damage or disturb any artefacts.")
                            npc(npc, CALM_TALK, "Rock picks are for splitting rocks or scraping away soil.")
                            player(CALM_TALK, "What about specimen jars and brushes?")
                            npc(npc, CALM_TALK, "Those are essential for carefully cleaning and storing smaller samples.")
                            player(CALM_TALK, "Where can I get any of these things?")
                            npc(npc, CALM_TALK, "Well, we've come into a bit more funding of late, so there should be a stock of each of them in the Exam Centre's tools cupboard.")
                            npc(npc, CALM_TALK, "We also hand out relevant tools as students complete each level of their Earth Sciences exams.")
                            player(CALM_TALK, "Anything else?")
                            npc(npc, CALM_TALK, "If you need something identified or are not sure about something, give it to Terry. He's the archaeological expert in the next room.")
                            player(CALM_TALK, "Ah, okay, thanks.")
                        }
                        op("Thank you!") { player(CALM_TALK, "Thank you!") }
                    }
                }
            }
        }
    }
}

@ServerStartupEvent
fun mapResearcher() {
    onNpcClick(4568) { (player, npc) -> Researcher(player, npc) }
}
