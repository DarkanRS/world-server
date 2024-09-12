package com.rs.game.content.world.areas.gu_tanoth

import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick

@ServerStartupEvent
fun mapRunecraftingGuild() {
    onObjectClick(38279, tiles = arrayOf(Tile.of(1696, 5460, 2))) { e -> e.player.useStairs(-1, Tile.of(3106, 3160, 1), 0, 1) }
}
