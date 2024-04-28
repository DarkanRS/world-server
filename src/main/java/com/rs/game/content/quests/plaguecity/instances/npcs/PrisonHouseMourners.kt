package com.rs.game.content.quests.plaguecity.instances.npcs

import com.rs.engine.pathfinder.Direction
import com.rs.game.content.quests.plaguecity.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.lib.game.Tile

class PrisonHouseMourners(id: Int, tile: Tile) : NPC(id, tile) {
    init {
        setRandomWalk(false)
        when (id) {
            MOURNER_EAST_PRISON_DOOR -> { faceDir(Direction.WEST) }
            MOURNER_WEST_PRISON_DOOR -> { faceDir(Direction.EAST) }
        }
    }
}
