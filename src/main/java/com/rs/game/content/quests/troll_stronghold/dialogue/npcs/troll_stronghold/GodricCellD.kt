package com.rs.game.content.quests.troll_stronghold.dialogue.npcs.troll_stronghold

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.troll_stronghold.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class GodricCellD(player: Player, npc: NPC) {
    init {
        player.startConversation {
            if (player.getQuestStage(Quest.TROLL_STRONGHOLD) < STAGE_UNLOCKED_BOTH_CELLS) {
                if (!player.questManager.getAttribs(Quest.TROLL_STRONGHOLD).getB(UNLOCKED_GODRIC_CELL)) {
                    player(CALM_TALK, "How are you doing in there?")
                    npc(npc, CALM_TALK, "I'm a little weakened by my stay, but nothing serious. How soon can you get me out of here?")
                    if (player.inventory.containsOneItem(CELL_KEY_GODRIC)) {
                        player(CALM_TALK, "I have the key right here.")
                        npc(npc, CALM_TALK, "Excellent! Unlock this door and I'll have us out of here in no time.")
                    } else {
                        player(CALM_TALK, "I haven't found the key yet.")
                        npc(npc, CALM_TALK, "One of the guards is bound to have it. Find a way to get it off them!")
                    }
                } else {
                    player.sendMessage("Maybe you're imagining seeing Godric in there... You've already rescued him.")
                }
            } else {
                player.sendMessage("Maybe you're imagining seeing Godric in there... You've already rescued him.")
            }
        }
    }
}
