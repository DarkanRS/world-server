package com.rs.game.content.world.areas.troll_stronghold.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.eadgars_ruse.dialogues.npcs.troll_stronghold.BurntmeatD
import com.rs.game.content.quests.eadgars_ruse.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class Burntmeat(player: Player, npc: NPC) {
    init {
        val stage = player.getQuestStage(Quest.EADGARS_RUSE)
        if (stage in STAGE_SPEAK_TO_BURNTMEAT..STAGE_UNLOCKED_STOREROOM ) {
            BurntmeatD(player, npc)
        } else {
            player.startConversation {
                player(SCARED, "Er, hi.")
                npc(npc, T_HAPPY_TALK, "Hmm? What human do in troll kitchen? Burntmeat tired of cooking goats. Human look tasty.")
                player(CALM_TALK, "Oh, you don't want to eat me! I'm a tough, hardened adventurer, not tender or tasty at all!")
                npc(npc, T_HAPPY_TALK, "Hmm. Burntmeat think you probably right. What human doing here?")
                player(CALM_TALK, "Nothing. I was just leaving.")
            }
        }
    }
}

@ServerStartupEvent
fun mapBurntmeat() {
    onNpcClick(1151, options = arrayOf("Talk-to")) { (player, npc) -> Burntmeat(player, npc) }
}
