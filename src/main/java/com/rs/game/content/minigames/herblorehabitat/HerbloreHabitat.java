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
package com.rs.game.content.minigames.herblorehabitat;

import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public class HerbloreHabitat {
	//inter 72 jadinko methods

	@ServerStartupEvent
	public static void initUpdateTask() {
		WorldTasks.schedule(25, 25, () -> {
			for (Player player : World.getPlayersInChunkRange(Tile.of(2959, 2915, 0).getChunkId(), 5)) {
				if (player.hasStarted() && !player.hasFinished()) {
					JadinkoType.updateGroup(player, JadinkoType.COMMON);
					JadinkoType.updateGroup(player, JadinkoType.IGNEOUS, JadinkoType.AQUATIC);
					JadinkoType.updateGroup(player, JadinkoType.CANNIBAL, JadinkoType.CARRION);
					JadinkoType.updateGroup(player, JadinkoType.AMPHIBIOUS, JadinkoType.DRACONIC);
					JadinkoType.updateGroup(player, JadinkoType.SARADOMIN, JadinkoType.GUTHIX, JadinkoType.ZAMORAK);
				}
			}
		});
	}


}
