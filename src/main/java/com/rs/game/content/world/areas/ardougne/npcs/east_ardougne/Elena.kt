package com.rs.game.content.world.areas.ardougne.npcs.east_ardougne

import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.dialogue.npcs.east_ardougne.ElenaD
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class Elena(player: Player, npc: NPC) {
    init {
        if (player.questManager.getStage(Quest.BIOHAZARD) in STAGE_UNSTARTED..STAGE_COMPLETE) {
            ElenaD(player, npc)
        } else {
            player.sendMessage("Elena isn't interested in talking right now.")
        }
    }
}

@ServerStartupEvent
fun mapElenaEastArdougne() {
    onNpcClick(335) { (player, npc) -> Elena(player, npc) }
}
