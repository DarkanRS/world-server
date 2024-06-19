package com.rs.game.content.quests.troll_stronghold.instances.npcs

import com.rs.engine.quest.Quest
import com.rs.game.content.quests.troll_stronghold.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.lib.game.Tile

class TrollGenerals(id: Int, tile: Tile) : NPC(id, tile) {
    override fun drop(killer: Player) {
        if (killer.getQuestStage(Quest.TROLL_STRONGHOLD) == STAGE_FINISHED_DAD)
            sendDrop(killer, Item(PRISON_KEY))
        super.drop(killer)
    }
}
