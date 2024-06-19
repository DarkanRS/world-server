package com.rs.game.content.world.areas.taverley.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.eadgars_ruse.dialogues.npcs.taverley.TegidD
import com.rs.game.content.quests.eadgars_ruse.utils.*
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.onNpcClick

class Tegid(player: Player, npc: NPC) {
    init {
        val stage = player.getQuestStage(Quest.EADGARS_RUSE)
        if (stage in STAGE_NEED_TO_HIDE_PARROT..STAGE_HIDDEN_PARROT) {
            TegidD(player, npc)
        } else {
            player.startConversation {
                player(CALM_TALK, "So, you're doing laundry, eh?")
                npc(npc, CALM_TALK, "Yeah. What is it to you?")
                player(CALM_TALK, "Nice day for it.")
                npc(npc, CALM_TALK, "Suppose it is.")
            }
        }
    }
}

class TegidInstance(id: Int, tile: Tile) : NPC(id, tile) {
    override fun faceEntityTile(target: Entity?) { }
}

@ServerStartupEvent
fun mapTegid() {
    onNpcClick(1213, options = arrayOf("Talk-to")) { (player, npc) -> Tegid(player, npc) }
    instantiateNpc(1213) { id, tile -> TegidInstance(id, tile) }
}
