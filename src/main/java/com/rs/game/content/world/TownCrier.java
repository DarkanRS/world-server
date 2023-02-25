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
package com.rs.game.content.world;

import com.rs.Settings;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class TownCrier extends NPC {

	public TownCrier(int id, Tile tile) {
		super(id, tile);
	}

	@Override
	public void processNPC() {
		if (Settings.getConfig().getLoginMessage() != null && Utils.random(100) == 0)
			setNextForceTalk(new ForceTalk(Settings.getConfig().getLoginMessage()));
		super.processNPC();
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 6135, 6136, 6137, 6138, 6139 }, (npcId, tile) -> new TownCrier(npcId, tile));
}
