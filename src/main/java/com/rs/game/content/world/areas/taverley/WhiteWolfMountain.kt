package com.rs.game.content.world.areas.taverley

import com.rs.engine.quest.Quest
import com.rs.game.World
import com.rs.game.content.quests.fishingcontest.DwarfBrothersFishingContestD
import com.rs.game.content.quests.fishingcontest.FishingContest.QUEST_COMPLETE
import com.rs.game.model.entity.pathing.RouteEvent
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick
import com.rs.plugin.kts.onObjectClick

const val AUSTRI_ID = 232
const val VESTRI_ID = 3679

@ServerStartupEvent
fun handleShortcutStairs() {
    onObjectClick(54, 55, 56, 66990) { (player, obj) ->
        val targetLocation = when {
            obj.id == 54 && obj.x == 2820 && obj.y == 9883 -> Tile.of(2820, 3486, 0)
            obj.id == 55 && obj.x == 2820 && obj.y == 3484 -> Tile.of(2821, 9882, 0)
            obj.id == 56 && obj.x == 2876 && obj.y == 9880 -> Tile.of(2879, 3465, 0)
            obj.id == 66990 && obj.x == 2876 && obj.y == 3462 -> Tile.of(2876, 9879, 0)
            else -> null
        }
        if (targetLocation != null && player.questManager.getStage(Quest.FISHING_CONTEST) == QUEST_COMPLETE) {
            player.useStairs(-1, targetLocation, 0, 1)
        } else {
            val npcId = if (obj.id == 66990) AUSTRI_ID else VESTRI_ID
            World.getNPCsInChunkRange(player.chunkId, 2).firstOrNull { it.id == npcId }?.let {
                player.setRouteEvent(RouteEvent(it) {
                    player.faceEntity(it)
                    it.faceEntity(player)
                    player.startConversation(DwarfBrothersFishingContestD(player, npcId).start)
                })
            }
        }
    }
}

@ServerStartupEvent
fun handleDwarfNPCDialogue() {
    onNpcClick(AUSTRI_ID, VESTRI_ID, options = arrayOf("Talk-to")) { e ->
        val npcId = e.npc.id
        e.player.startConversation(DwarfBrothersFishingContestD(e.player, npcId).start)
    }
}