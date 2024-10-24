package com.rs.game.content.items

import com.rs.engine.quest.Quest
import com.rs.game.content.items.Spade.dig
import com.rs.game.content.minigames.barrows.BarrowsController
import com.rs.game.content.quests.piratestreasure.PiratesTreasure
import com.rs.game.content.quests.plague_city.utils.*
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemClick
import com.rs.plugin.kts.onObjectClick

@ServerStartupEvent
fun mapSpade() {
    onItemClick(952, options = arrayOf("Dig")) { dig(it.player) }
    onObjectClick(66115, 66116) { dig(it.player) }
}

object Spade {
    @JvmStatic
	fun dig(player: Player) {
        player.resetWalkSteps()
        player.anim(830)
        player.lock()
        player.schedule {
            player.unlock()
            if (player.treasureTrailsManager.useDig(false) || BarrowsController.digIntoGrave(player)) return@schedule
            if (
                //mole holes
                (player.x == 3005 && player.y == 3376) ||
                (player.x == 2999 && player.y == 3375) ||
                (player.x == 2996 && player.y == 3377) ||
                (player.x == 2989 && player.y == 3378) ||
                (player.x == 2987 && player.y == 3387) ||
                (player.x == 2984 && player.y == 3387)
            ) {
                player.tele(Tile.of(1752, 5137, 0))
                player.sendMessage("You seem to have dropped down into a network of mole tunnels.")
                return@schedule
            }
            if (Utils.getDistance(player.tile, Tile.of(2749, 3734, 0)) < 3) {
                player.useStairs(-1, Tile.of(2690, 10124, 0), 0, 1)
                return@schedule
            }

            // Plague City Mud Patch
            if (player.tile == MUD_PATCH_DIG_TILE) {
                if (player.questManager.getStage(Quest.PLAGUE_CITY) >= STAGE_UNCOVERED_SEWER_ENTRANCE) player.simpleDialogue("You've already uncovered the entrance to the Ardougne Sewers.")
                else PlagueCityUtils().digAtMudPatch(player)
                return@schedule
            }

            //Pirate's Treasure
            if (player.questManager.getStage(Quest.PIRATES_TREASURE) == PiratesTreasure.GET_TREASURE) PiratesTreasure.findTreasure(player)
            player.sendMessage("You find nothing.")
        }
    }
}
