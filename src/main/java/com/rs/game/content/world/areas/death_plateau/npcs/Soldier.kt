package com.rs.game.content.world.areas.death_plateau.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.pathfinder.Direction
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.onNpcClick

class Soldier(player: Player, npc: NPC) {
    init {
        player.startConversation {
            player(WORRIED, "Are you OK?")
            npc(npc, CALM_TALK, "Urrrgggh... I'll be OK, the trolls only leave the plateau at nightfall. The guys are bringing a stretcher shortly.")
            player(CALM_TALK, "As long as you're sure.")
            npc(npc, CALM_TALK, "It's my own fault really, I was having a walk and wandered too far past the danger sign. The trolls throw rocks down at any one who goes up the path! Don't go up there!")
            player(CALM_TALK, "OK, thanks for the warning.")
        }
    }
}

class InjuredSoldier(id: Int, tile: Tile) : NPC(id, tile) {
    override fun processNPC() {
        faceDir(Direction.SOUTH)
    }
}

@ServerStartupEvent
fun mapSoldierDeathPlateau() {
    onNpcClick(15087) { (player, npc) -> Soldier(player, npc) }
    instantiateNpc(15087) { id, tile -> InjuredSoldier(id, tile) }
}
