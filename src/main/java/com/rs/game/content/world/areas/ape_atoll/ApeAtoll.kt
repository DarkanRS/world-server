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
package com.rs.game.content.world.areas.ape_atoll;

import com.rs.game.tasks.Task
import com.rs.game.tasks.WorldTasks
import com.rs.lib.Constants
import com.rs.lib.game.Animation
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick

@ServerStartupEvent
fun mapApeAtoll() {
	val ladders = mapOf(
		4714 to Tile.of(2803, 9170, 0),
		4743 to Tile.of(2803, 2725, 0),
		4728 to Tile.of(2765, 2768, 0),
		4889 to Tile.of(2748, 2767, 0)
	)

	ladders.forEach { (objectId, toTile) ->
		onObjectClick(objectId) { (player) ->
			player.useLadder(toTile)
		}
	}

	onObjectClick(4773, 4779) { (player, obj) ->
		val deltaX = when(obj.rotation) {
			0 -> if (obj.id == 4773) -2 else 2
			3 -> 0
			else -> 0
		}
		val deltaY = when(obj.rotation) {
			0 -> 0
			3 -> if (obj.id == 4773) -2 else 2
			else -> 0
		}
		val deltaZ = if (obj.id == 4773) 1 else -1

		player.tele(player.transform(deltaX, deltaY, deltaZ))
	}

	onObjectClick(4859) { (player, obj) ->
		player.prayer.worshipAltar()
	}
}
