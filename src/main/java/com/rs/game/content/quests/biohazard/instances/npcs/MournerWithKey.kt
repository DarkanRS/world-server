package com.rs.game.content.quests.biohazard.instances.npcs

import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.lib.game.Tile

class MournerWithKey(id: Int, tile: Tile) : NPC(id, tile) {
    override fun drop() {
        val player = mostDamageReceivedSourcePlayer ?: return
        val gotDistillator = player.questManager.getAttribs(Quest.BIOHAZARD).getB(GOT_DISTILLATOR)
        if (player.questManager.getStage(Quest.BIOHAZARD) in STAGE_APPLE_IN_CAULDRON..STAGE_FOUND_DISTILLATOR && !gotDistillator) {
            if (!player.inventory.containsOneItem(KEY)) {
                player.sendMessage("You search the mourner...")
                if (player.inventory.hasFreeSlots()) {
                    player.sendMessage("and find a key.")
                    player.inventory.addItem(KEY)
                } else {
                    player.sendMessage("and find a key, but you don't have enough room to take it.")
                }
            }
        }
    }
}
