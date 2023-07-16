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
package com.rs.game.content.world.areas.burthorpe.npcs.announcers;

import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class AntonWorld extends NPC {
	private static final int npcId = 4295;

	public AntonWorld(int id, Tile tile) {
		super(id, tile);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (Utils.random(50) == 0)
			setNextForceTalk(new ForceTalk("Imported weapons from the finest smithys around the lands!"));
		if (Utils.random(50) == 25)
			setNextForceTalk(new ForceTalk("Ow my toe! That armour is heavy."));
		if (Utils.random(50) == 50)
			setNextForceTalk(new ForceTalk("Armour and axes to suit your needs."));
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { npcId }, (npcId, tile) -> new AntonWorld(npcId, tile));
}
