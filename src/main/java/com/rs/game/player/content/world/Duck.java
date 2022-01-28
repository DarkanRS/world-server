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
package com.rs.game.player.content.world;

import com.rs.game.ForceTalk;
import com.rs.game.npc.NPC;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class Duck extends NPC {

	public Duck(int id, WorldTile tile) {
		super(id, tile);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (Utils.random(50) == 0)
			setNextForceTalk(new ForceTalk("Quack!"));
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler("Duck", "Drake") {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new Duck(npcId, tile);
		}
	};
}
