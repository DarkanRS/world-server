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
package com.rs.game.content.bosses.kingblackdrag;

import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class KingBlackDragon extends NPC {

	public KingBlackDragon(int id, Tile tile, boolean spawned) {
		super(id, tile, spawned);
		setLureDelay(3000);
		setIntelligentRouteFinder(true);
		setIgnoreDocile(true);
	}

	public static boolean atKBD(Tile tile) {
		if ((tile.getX() >= 2250 && tile.getX() <= 2292) && (tile.getY() >= 4675 && tile.getY() <= 4710))
			return true;
		return false;
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 50, 2642 }, (npcId, tile) -> new KingBlackDragon(npcId, tile, false));
}
