package com.rs.game.content.world.areas.troll_stronghold

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.troll_stronghold.utils.STAGE_COMPLETE
import com.rs.game.content.quests.troll_stronghold.utils.STAGE_UNLOCKED_BOTH_CELLS
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick

@ServerStartupEvent
fun mapTrollStronghold() {

    onObjectClick(3788) { (player, obj) ->
        when (obj.plane) {
            1 -> { player.useStairs(player.tile.transform(if (player.x < obj.x) 4 else -4, 0, 1)) }
            0 -> { player.useStairs(player.tile.transform(0, if (player.y < obj.y) 4 else -4, 1)) }
        }
    }

    onObjectClick(3789) { (player, obj) ->
        when (obj.plane) {
            2 -> { player.useStairs(player.tile.transform(if (player.x < obj.x) 4 else -4, 0, -1)) }
            1 -> { player.useStairs(player.tile.transform( 0, if (player.y < obj.y) 4 else -4, -1)) }
        }
    }

    onObjectClick(18834, 18833) { (player, obj) ->
        when (obj.id) {
            18834 -> {
                if (player.getQuestStage(Quest.TROLL_STRONGHOLD) >= STAGE_COMPLETE) {
                    player.ladder(Tile.of(2812, 3669, 0))
                } else {
                    player.playerDialogue(CALM_TALK, "I'm not climbing that without a good reason. I might scrape my lovely hands on the rough ladder.")
                    return@onObjectClick
                }
            }
            18833 -> {
                if (player.getQuestStage(Quest.TROLL_STRONGHOLD) >= STAGE_COMPLETE) {
                    player.ladder(Tile.of(2831, 10076, 2))
                } else {
                    player.playerDialogue(CALM_TALK, "I'm not climbing that without a good reason. I might scrape my lovely hands on the rough ladder.")
                    return@onObjectClick
                }
            }
        }
    }

    onObjectClick(3761) { (player) ->
        if (player.getQuestStage(Quest.TROLL_STRONGHOLD) >= STAGE_UNLOCKED_BOTH_CELLS) {
            player.tele(Tile.of(2831, 3637, 0))
        } else {
            player.sendMessage("It is cold and dark in there. You have no reason to go in.")
            return@onObjectClick
        }
    }

}
