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
package com.rs.game.player.content.transportation;

import java.util.Set;

import com.rs.game.World;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.game.region.Region;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class WildernessObelisk {

	private static final WorldTile[] OBELISK_CENTER_TILES = { new WorldTile(2978, 3864, 0), new WorldTile(3033, 3730, 0), new WorldTile(3104, 3792, 0), new WorldTile(3154, 3618, 0), new WorldTile(3217, 3654, 0), new WorldTile(3305, 3914, 0) };
	private static final boolean[] IS_ACTIVE = new boolean[6];

	public static void activateObelisk(int id, final Player player) {
		final int index = id - 65616;
		final WorldTile center = OBELISK_CENTER_TILES[index];
		if (IS_ACTIVE[index]) {
			player.sendMessage("The obelisk is already active.");
			return;
		}
		IS_ACTIVE[index] = true;
		GameObject object = World.getObjectWithId(center, id);
		if (object == null) // still loading objects i guess
			return;
		World.sendObjectAnimation(object, new Animation(2226));
		World.sendObjectAnimation(World.getObjectWithId(center.transform(4, 0, 0), id), new Animation(2226));
		World.sendObjectAnimation(World.getObjectWithId(center.transform(0, 4, 0), id), new Animation(2226));
		World.sendObjectAnimation(World.getObjectWithId(center.transform(4, 4, 0), id), new Animation(2226));
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				for (int x = 1; x < 4; x++)
					for (int y = 1; y < 4; y++)
						World.sendSpotAnim(player, new SpotAnim(661), center.transform(x, y, 0));
				Region region = World.getRegion(center.getRegionId());
				Set<Integer> playerIndexes = region.getPlayerIndexes();
				WorldTile newCenter = OBELISK_CENTER_TILES[Utils.random(OBELISK_CENTER_TILES.length)];
				if (playerIndexes != null)
					for (Integer i : playerIndexes) {
						Player p = World.getPlayers().get(i);
						if (p == null || (p.getX() < center.getX() + 1 || p.getX() > center.getX() + 3 || p.getY() < center.getY() + 1 || p.getY() > center.getY() + 3))
							continue;
						int offsetX = p.getX() - center.getX();
						int offsetY = p.getY() - center.getY();
						Magic.sendTeleportSpell(p, 8939, 8941, 1690, -1, 0, 0, new WorldTile(newCenter.getX() + offsetX, newCenter.getY() + offsetY, 0), 3, false, Magic.OBJECT_TELEPORT);
					}
				IS_ACTIVE[index] = false;
			}

		}, 8);

	}
}