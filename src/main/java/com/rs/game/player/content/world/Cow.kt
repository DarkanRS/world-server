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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.world

import com.rs.plugin.annotations.PluginEventHandler
import com.rs.lib.game.WorldTile
import com.rs.game.npc.NPC
import com.rs.game.ForceTalk
import com.rs.plugin.handlers.ItemOnNPCHandler
import com.rs.plugin.events.ItemOnNPCEvent
import com.rs.plugin.handlers.NPCInstanceHandler
import com.rs.game.player.content.world.Cow
import com.rs.lib.util.Utils

@PluginEventHandler
class Cow(id: Int, tile: WorldTile?) : NPC(id, tile) {
    override fun processNPC() {
        if (Utils.random(100) == 0)
            nextForceTalk = ForceTalk("Moo")
        super.processNPC()
    }

    companion object {
        var itemOnCow: ItemOnNPCHandler = object : ItemOnNPCHandler("Cow") {
            override fun handle(e: ItemOnNPCEvent) {
                e.player.sendMessage("The cow doesn't want that.")
            }
        }
        var toFunc: NPCInstanceHandler = object : NPCInstanceHandler("Cow") {
            override fun getNPC(npcId: Int, tile: WorldTile): NPC {
                return Cow(npcId, tile)
            }
        }
    }
}