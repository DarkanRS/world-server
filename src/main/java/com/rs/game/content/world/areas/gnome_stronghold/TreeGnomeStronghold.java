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
package com.rs.game.content.world.areas.gnome_stronghold;

import com.rs.game.World;
import com.rs.game.map.ChunkManager;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class TreeGnomeStronghold {
	public static ObjectClickHandler handleGnomeSpiralStairsUp = new ObjectClickHandler(new Object[] { 69505 }, e -> {
		GameObject oppObj = e.getObject();
		for(GameObject obj : ChunkManager.getChunk(e.getPlayer().transform(0, 0, 1).getChunkId()).getBaseObjects())
			if(obj.getId()== 69504 && obj.getTile().withinDistance(e.getObject().getTile().transform(0, 0, 1), 3))
				oppObj = obj;
		if(oppObj.getTile().matches(Tile.of(2444, 3415, 0))) {
			e.getPlayer().useStairs(Tile.of(2445, 3416, 1));
			return;
		}
		if(oppObj.getTile().matches(Tile.of(2456, 3417, 0))) {
			e.getPlayer().useStairs(Tile.of(2457, 3417, 1));
			return;
		}
		if(oppObj.getTile().matches(Tile.of(2440, 3404, 0))) {
			e.getPlayer().useStairs(Tile.of(2440, 3403, 1));
			return;
		}
		if(oppObj.getRotation() == 0)
			e.getPlayer().useStairs(oppObj.getTile().transform(1, 0));
		if(oppObj.getRotation() == 1)
			e.getPlayer().useStairs(oppObj.getTile().transform(0, -1));
		if(oppObj.getRotation() == 2)
			e.getPlayer().useStairs(oppObj.getTile().transform(-1, 0));
		if(oppObj.getRotation() == 3)
			e.getPlayer().useStairs(oppObj.getTile().transform(0, 1));
	});
	
	public static ObjectClickHandler handleGnomeSpiralStairsDown = new ObjectClickHandler(new Object[] { 69504 }, e -> {
		GameObject oppObj = e.getObject();
		for(GameObject obj : ChunkManager.getChunk(e.getPlayer().transform(0, 0, -1).getChunkId()).getBaseObjects())
			if(obj.getId()== 69505 && obj.getTile().withinDistance(e.getObject().getTile().transform(0, 0, -1), 3))
				oppObj = obj;
		if(oppObj.getTile().matches(Tile.of(2445, 3415, 1))) {
			e.getPlayer().useStairs(Tile.of(2446, 3416, 0));
			return;
		}
		if(oppObj.getTile().matches(Tile.of(2416, 3446, 1))) {
			e.getPlayer().useStairs(Tile.of(2416, 3447, 0));
			return;
		}
		if(oppObj.getTile().matches(Tile.of(2421, 3472, 1))) {
			e.getPlayer().useStairs(Tile.of(2420, 3473, 0));
			return;
		}
		if(oppObj.getRotation() == 0)
			e.getPlayer().useStairs(oppObj.getTile().transform(1, -1));
		if(oppObj.getRotation() == 1)
			e.getPlayer().useStairs(oppObj.getTile().transform(-1, 0));
		if(oppObj.getRotation() == 2)
			e.getPlayer().useStairs(oppObj.getTile().transform(0, 2));
		if(oppObj.getRotation() == 3)
			e.getPlayer().useStairs(oppObj.getTile().transform(2, 1));
	});

	public static ObjectClickHandler handleSpecialStair = new ObjectClickHandler(new Object[] { 69549, 69550 }, e -> {
		if(e.getPlayer().getPlane() == 1) {
			e.getPlayer().useStairs(Tile.of(2485, 3463, 2));
			return;
		}
		e.getPlayer().useStairs(Tile.of(2483, 3463, 1));
	});
}
