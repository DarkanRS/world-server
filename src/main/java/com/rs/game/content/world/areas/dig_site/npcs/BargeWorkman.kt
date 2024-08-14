package com.rs.game.content.world.areas.dig_site.npcs

import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.npc.NPC
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.onNpcClick

class BargeWorkman(npc: NPC) {
    private val phrases = listOf(
        "Can't stop. Too busy.",
        "This work isn't going to do itself.",
        "Wonder when I'll get paid.",
        "Is it lunch break yet?",
        "Ouch! That was my finger!",
        "Hey I'm working here. I'm working here."
    )

    init {
        val randomPhrase = phrases.random()
        npc.forceTalk(randomPhrase)
    }
}

class BargeWorkmanInstance(id: Int, tile: Tile) : NPC(id, tile) {
    override fun faceEntityTile(target: Entity?) { }
}

@ServerStartupEvent
fun mapBargeWorkman() {
    onNpcClick(5959, 5960, 5961, 5962) { (_, npc) -> BargeWorkman(npc) }
    instantiateNpc(5959, 5960, 5961, 5962) { id, tile -> BargeWorkmanInstance(id, tile) }
}
