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
package com.rs.game.player.content.minigames.herblorehabitat;

import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public class HerbloreHabitat {
	//inter 72 jadinko methods

	public static final int REGION_ID = 11821;

	@ServerStartupEvent
	public static void initUpdateTask() {
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				for (Player player : World.getPlayersInRegion(REGION_ID))
					if (player.hasStarted() && !player.hasFinished()) {
						JadinkoType.updateGroup(player, JadinkoType.COMMON);
						JadinkoType.updateGroup(player, JadinkoType.IGNEOUS, JadinkoType.AQUATIC);
						JadinkoType.updateGroup(player, JadinkoType.CANNIBAL, JadinkoType.CARRION);
						JadinkoType.updateGroup(player, JadinkoType.AMPHIBIOUS, JadinkoType.DRACONIC);
						JadinkoType.updateGroup(player, JadinkoType.SARADOMIN, JadinkoType.GUTHIX, JadinkoType.ZAMORAK);
					}
			}
		}, 25, 25);
	}

}
