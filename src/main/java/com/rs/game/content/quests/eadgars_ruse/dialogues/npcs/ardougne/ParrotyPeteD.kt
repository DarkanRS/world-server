package com.rs.game.content.quests.eadgars_ruse.dialogues.npcs.ardougne

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.eadgars_ruse.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class ParrotyPeteD(player: Player, npc: NPC) {
    init {
        player.startConversation {
            npc(npc, HAPPY_TALKING, "Good day, good day. Come to admire the new parrot aviary have we?")
            options {
                op("It's very nice.") {
                    player(CALM_TALK, "It's very nice.")
                    npc(npc, HAPPY_TALKING, "Isn't it just?")
                }
                op("When did you add it?") {
                    player(SKEPTICAL_THINKING, "When did you add it?")
                    npc(npc, SAD_MILD_LOOK_DOWN, "Just recently. It would have been sooner, but some wretch thought it would be amusing to replace their drinking water with vodka. The vet had to nurse them back to health for weeks!") {
                        if (player.getQuestStage(Quest.EADGARS_RUSE) == STAGE_GET_PARROT) player.questManager.getAttribs(Quest.EADGARS_RUSE).setB(LEARNED_ABOUT_VODKA, true)
                    }
                }
                op("What do you feed them?") {
                    player(SKEPTICAL_THINKING, "What do you feed them?")
                    npc(npc, TALKING_ALOT, "Well, fruit and wheat mostly. I try to give them a balanced diet, but their favourite treat is pineapple chunks.") {
                        if (player.getQuestStage(Quest.EADGARS_RUSE) == STAGE_GET_PARROT) player.questManager.getAttribs(Quest.EADGARS_RUSE).setB(LEARNED_ABOUT_PINEAPPLE, true)
                    }
                }
            }
        }
    }
}
