package com.rs.game.content.world.areas.sophanem

import com.rs.engine.quest.Quest
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick

@ServerStartupEvent
fun mapSophanem() {
    onObjectClick(20275) { (player) -> player.useLadder(if (player.isQuestComplete(Quest.CONTACT)) Tile.of(2799, 5160, 0) else Tile.of(2765, 5131, 0)) }
    onObjectClick(20277, 20280) { (player) -> player.useLadder(Tile.of(3315, 2796, 0)) }
    onObjectClick(20340) { (player) -> player.useLadder(Tile.of(3286, 9273, 0)) }
    onObjectClick(20284) { (player) -> player.useLadder(Tile.of(2766, 5131, 0)) }
}