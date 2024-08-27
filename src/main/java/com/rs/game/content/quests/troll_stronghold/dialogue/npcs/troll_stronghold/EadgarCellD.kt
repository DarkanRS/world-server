package com.rs.game.content.quests.troll_stronghold.dialogue.npcs.troll_stronghold

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.troll_stronghold.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class EadgarCellD(player: Player, npc: NPC) {
    init {
        player.startConversation {
            if (player.getQuestStage(Quest.TROLL_STRONGHOLD) < STAGE_UNLOCKED_BOTH_CELLS) {
                if (!player.questManager.getAttribs(Quest.TROLL_STRONGHOLD).getB(UNLOCKED_EADGAR_CELL)) {
                    player(CALM_TALK, "How are you doing in there?")
                    npc(npc, CALM_TALK, "I'm fine! Just dandy! A bit cold, I keep asking them to install a fireplace but they don't listen.")
                    if (player.inventory.containsOneItem(CELL_KEY_EADGAR)) {
                        player(CALM_TALK, "I have the key right here.")
                        npc(npc, CALM_TALK, "Well done, well done. Now let me out! That rock is looking at me funny.")
                    } else {
                        player(CALM_TALK, "I don't have the key.")
                        npc(npc, CALM_TALK, "It's on the guard's belt, silly. Free me and I can cook you something good!")
                    }
                } else {
                    player.sendMessage("Maybe you're imagining seeing Eadgar in there... You've already rescued him.")
                }
            } else {
                player.sendMessage("Maybe you're imagining seeing Eadgar in there... You've already rescued him.")
            }
        }
    }
}
