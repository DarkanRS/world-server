package com.rs.game.content.world.areas.ardougne.npcs.west_ardougne

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class NurseSarah(player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.BIOHAZARD)) {

                STAGE_COMPLETED_WALL_CROSSING -> {
                    player(CALM_TALK, "Hello nurse.")
                    npc(npc, WORRIED, "I don't know how much longer I can cope here.")
                    player(CONFUSED, "What? Is the plague getting to you?")
                    npc(npc, WORRIED, "No, strangely enough the people here don't seem to be affected. It's just the awful living conditions that is making people ill.")
                    player(CONFUSED, "I was under the impression that everyone here was affected.")
                    npc(npc, WORRIED, "Me too, but that doesn't seem to be the case.")
                }

                STAGE_APPLE_IN_CAULDRON -> {
                    player(CALM_TALK, "Hello nurse.")
                    npc(npc, WORRIED, "Oh hello there. I'm afraid I can't stop and talk, a group of mourners have become ill with food poisoning. I need to go over and see what I can do.")
                    player(CALM_TALK, "Hmmm, strange that!")
                }

                else -> {
                    player(CALM_TALK, "Hello there.")
                    npc(npc, CALM_TALK, "Hello my dear, how are you feeling?")
                    player(CALM_TALK, "I'm ok thanks.")
                    npc(npc, CALM_TALK, "Well in that case I'd better get back to work. Take care.")
                    player(CALM_TALK, "You too.")
                }

            }

        }
    }
}

@ServerStartupEvent
fun mapNurseSarah() {
    onNpcClick(373) { (player, npc) -> NurseSarah(player, npc) }
}
