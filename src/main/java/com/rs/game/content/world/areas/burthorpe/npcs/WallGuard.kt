package com.rs.game.content.world.areas.burthorpe.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class WallGuard(player: Player, npc: NPC) {
    init {
        player.startConversation {
            player(SKEPTICAL_THINKING, "Hi. May I pass?")
            npc(npc, CALM_TALK, "I'm here to stop the Trolls. I stand here all day watching for Trolls. Trolls look a lot like rocks, I spend all day staring at rocks.")
            player(HAPPY_TALKING, "Sounds fun!")
            exec {
                val wasRunning = player.run
                player.setRun(false)
                player.lock()
                player.walkToAndExecute(Tile.of(2868, 3555, 0)) {
                    if (wasRunning) player.setRun(true) else player.setRun(false)
                    player.unlock()
                }
            }
        }
    }
}

@ServerStartupEvent
fun mapWallGuardBurthorpe() {
    onNpcClick(13696) { (player, npc) -> WallGuard(player, npc) }
}
