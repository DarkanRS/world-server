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
package com.rs.game.npc.dungeoneering;

import com.rs.game.World;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.player.content.skills.dungeoneering.RoomReference;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class HobgoblinGeomancer extends DungeonBoss {

	public HobgoblinGeomancer(WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(10059, 10072), manager.getBossLevel()), tile, manager, reference);
		setHitpoints(getMaxHitpoints());
	}

	public void sendTeleport(final WorldTile tile, final RoomReference room) {
		setCantInteract(true);
		setNextAnimation(new Animation(12991, 70));
		setNextSpotAnim(new SpotAnim(1576, 70, 0));
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				setCantInteract(false);
				setNextAnimation(new Animation(-1));
				setNextWorldTile(World.getFreeTile(getManager().getRoomCenterTile(room), 6));
				resetReceivedHits();
			}
		}, 5);
	}
}
