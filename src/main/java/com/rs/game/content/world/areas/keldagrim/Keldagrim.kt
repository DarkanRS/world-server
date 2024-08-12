// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.world.areas.keldagrim

import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick
import com.rs.plugin.kts.onObjectClick
import com.rs.utils.shop.ShopsHandler

@ServerStartupEvent
fun mapKeldagrim() {
    onNpcClick(4558) { e ->
        when (e.option) {
            "Talk-to" -> e.player.startConversation {
                npc(e.npcId, HeadE.HAPPY_TALKING, "Can I help you at all?")
                exec { ShopsHandler.openShop(e.player, "keldagrim_crossbow_shop") }
            }
            "Trade" -> ShopsHandler.openShop(e.player, "keldagrim_crossbow_shop")
        }
    }

    onNpcClick(2158) { e ->
        when (e.option) {
            "Talk-to" -> e.player.startConversation {
                npc(e.npcId, HeadE.HAPPY_TALKING, "Can I help you at all?")
                exec { ShopsHandler.openShop(e.player, "carefree_crafting_stall") }
            }
            "Trade" -> ShopsHandler.openShop(e.player, "carefree_crafting_stall")
        }
    }

    val stairMap = mapOf(
        5973 to Tile.of(2838, 10124, 0),
        5998 to Tile.of(2780, 10161, 0),
        9084 to Tile.of(1939, 4958, 0),
        9138 to Tile.of(2931, 10196, 0),
        45060 to Tile.of(1520, 4704, 0),
        45008 to Tile.of(2817, 10155, 0),
        45005 to Tile.of(2871, 10176, 1),
        45006 to Tile.of(2871, 10173, 0),
        45007 to Tile.of(2873, 10173, 2),

    )

    fun handleStairs(objectId: Int, player: Player) {
        stairMap[objectId]?.let { player.useStairs(it) }
    }

    stairMap.keys.forEach { objectId ->
        onObjectClick(objectId) { e -> handleStairs(e.objectId, e.player) }
    }

    onObjectClick(6085, 6086) { e ->
        if (e.objectId == 6085) e.player.tele(e.player.transform(if (e.getObject().rotation == 1) 3 else if (e.getObject().rotation == 3) -3 else 0, if (e.getObject().rotation == 2) -3 else if (e.getObject().rotation == 0) 3 else 0, 1))
        else if (e.objectId == 6086) e.player.tele(e.player.transform(if (e.getObject().rotation == 1) -3 else if (e.getObject().rotation == 3) 3 else 0, if (e.getObject().rotation == 2) 3 else if (e.getObject().rotation == 0) -3 else 0, -1))
    }
    onObjectClick(6089, 6090) { e ->
        if (e.objectId == 6089) e.player.tele(e.player.transform(if (e.getObject().rotation == 3) -3 else if (e.getObject().rotation == 1) 3 else 0, if (e.getObject().rotation == 3) 0 else if (e.getObject().rotation == 1) -0 else 0, 1))
        else if (e.objectId == 6090) e.player.tele(e.player.transform(if (e.getObject().rotation == 3) 3 else if (e.getObject().rotation == 1) -3 else 0, if (e.getObject().rotation == 3) -0 else if (e.getObject().rotation == 1) -0 else 0, -1))
    }
    onObjectClick(6087, 6088) { e ->
        var xOffset = when(e.getObject().rotation) {
            0 -> -1
            1 -> 0
            2 -> 1
            else -> 0
        }
        var yOffset = when(e.getObject().rotation) {
            0 -> 0
            1 -> 1
            2 -> 0
            else -> -1
        }
        if (e.objectId == 6087)
            e.player.tele(e.player.transform(xOffset, yOffset, 1))
        else if (e.objectId == 6088)
            e.player.tele(e.player.transform(-xOffset, -yOffset, -1))
    }
}
